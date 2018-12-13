/*
 * Copyright 2018 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.geoprocessing.geotools.io.datahandler.generator;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.referencing.CRS;
import org.n52.geoprocessing.geotools.io.data.binding.complex.GTHelper;
import org.n52.geoprocessing.geotools.io.data.binding.complex.GTVectorDataBinding;
import org.n52.javaps.io.Data;
import org.n52.javaps.io.SchemaRepository;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import com.vividsolutions.jts.geom.Geometry;

public class GTBinDirectorySHPGenerator {

    private GTHelper gtHelper;

    @Inject
    public void setGTHelper(GTHelper gtHelper) {
        this.gtHelper = gtHelper;
    }

    public File writeFeatureCollectionToDirectory(Data<?> data) throws IOException {
        return writeFeatureCollectionToDirectory(data, null);
    }

    public File writeFeatureCollectionToDirectory(Data<?> data,
            File parent) throws IOException {
        GTVectorDataBinding binding = (GTVectorDataBinding) data;
        SimpleFeatureCollection originalCollection = binding.getPayload();

        SimpleFeatureCollection collection = createCorrectFeatureCollection(originalCollection);

        return createShapefileDirectory(collection, parent);
    }

    private SimpleFeatureCollection createCorrectFeatureCollection(SimpleFeatureCollection fc) {

        SimpleFeatureCollection resultFeatureCollection = new DefaultFeatureCollection(null, null);
        List<SimpleFeature> featureList = new ArrayList<>();
        SimpleFeatureType featureType = null;
        SimpleFeatureIterator iterator = fc.features();
        String uuid = UUID.randomUUID().toString();
        int i = 0;
        while (iterator.hasNext()) {
            SimpleFeature feature = iterator.next();

            if (i == 0) {
                featureType = gtHelper.createFeatureType(feature.getProperties(), (Geometry) feature.getDefaultGeometry(), uuid, feature.getFeatureType().getCoordinateReferenceSystem());
                QName qname = gtHelper.createGML3SchemaForFeatureType(featureType);
                SchemaRepository.registerSchemaLocation(qname.getNamespaceURI(), qname.getLocalPart());
            }
            SimpleFeature resultFeature = gtHelper.createFeature("ID" + i, (Geometry) feature.getDefaultGeometry(), featureType, feature.getProperties());

            featureList.add(resultFeature);
            i++;
        }

        resultFeatureCollection = new ListFeatureCollection(featureType, featureList);

        return resultFeatureCollection;

    }

    /**
     * Transforms the given {@link FeatureCollection} into a zipped SHP file
     * (.shp, .shx, .dbf, .prj) and returs its Base64 encoding
     *
     * @param collection
     *            the collection to transform
     * @return the zipped shapefile
     * @throws IOException
     *             If an error occurs while creating the SHP file or encoding
     *             the shapefile
     * @throws IllegalAttributeException
     *             If an error occurs while writing the features into the the
     *             shapefile
     */
    private File createShapefileDirectory(SimpleFeatureCollection collection,
            File parent) throws IOException, IllegalAttributeException {
        if (parent == null) {
            File tempBaseFile = File.createTempFile("resolveDir", ".tmp");
            tempBaseFile.deleteOnExit();
            parent = tempBaseFile.getParentFile();
        }

        if (parent == null || !parent.isDirectory()) {
            throw new IllegalStateException("Could not find temporary file directory.");
        }

        File shpBaseDirectory = new File(parent, UUID.randomUUID().toString());

        if (!shpBaseDirectory.mkdir()) {
            throw new IllegalStateException("Could not create temporary shp directory.");
        }

        File tempSHPfile = File.createTempFile("shp", ".shp", shpBaseDirectory);
        tempSHPfile.deleteOnExit();
        DataStoreFactorySpi dataStoreFactory = new ShapefileDataStoreFactory();
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("url", tempSHPfile.toURI().toURL());
        params.put("create spatial index", Boolean.TRUE);

        ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);

        newDataStore.createSchema(collection.getSchema());
        if (collection.getSchema().getCoordinateReferenceSystem() == null) {
            try {
                newDataStore.forceSchemaCRS(CRS.decode("4326"));
            } catch (NoSuchAuthorityCodeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FactoryException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            newDataStore.forceSchemaCRS(collection.getSchema().getCoordinateReferenceSystem());
        }

        Transaction transaction = new DefaultTransaction("create");

        String typeName = newDataStore.getTypeNames()[0];
        FeatureStore<SimpleFeatureType, SimpleFeature> featureStore = (FeatureStore<SimpleFeatureType, SimpleFeature>) newDataStore.getFeatureSource(typeName);
        featureStore.setTransaction(transaction);
        try {
            featureStore.addFeatures(collection);
            transaction.commit();
        } catch (Exception problem) {
            transaction.rollback();
        } finally {
            transaction.close();
        }

        // Zip the shapefile
        String path = tempSHPfile.getAbsolutePath();
        String baseName = path.substring(0, path.length() - ".shp".length());
        File shx = new File(baseName + ".shx");
        File dbf = new File(baseName + ".dbf");
        File prj = new File(baseName + ".prj");

        // mark created files for delete
        tempSHPfile.deleteOnExit();
        shx.deleteOnExit();
        dbf.deleteOnExit();
        prj.deleteOnExit();
        shpBaseDirectory.deleteOnExit();

        return shpBaseDirectory;
    }

}
