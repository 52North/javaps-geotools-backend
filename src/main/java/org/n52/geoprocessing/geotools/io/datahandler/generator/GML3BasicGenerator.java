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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.gml2.SrsSyntax;
import org.geotools.gml3.ApplicationSchemaConfiguration;
import org.geotools.gml3.GMLConfiguration;
import org.geotools.xml.Configuration;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

import com.vividsolutions.jts.geom.Geometry;
import org.n52.geoprocessing.geotools.io.data.binding.complex.GTHelper;
import org.n52.geoprocessing.geotools.io.data.binding.complex.GTVectorDataBinding;
import org.n52.geoprocessing.geotools.io.data.binding.complex.SchemaRepository;
import org.n52.javaps.annotation.Properties;
import org.n52.javaps.description.TypedProcessOutputDescription;
import org.n52.javaps.io.AbstractPropertiesInputOutputHandler;
import org.n52.javaps.io.Data;
import org.n52.javaps.io.EncodingException;
import org.n52.javaps.io.OutputHandler;
import org.n52.shetland.ogc.wps.Format;

/**
 * This class generates
 * @author Maurin Radtke (m.radtke@52north.org)
 */
@Properties(
        defaultPropertyFileName = "gml.properties")
public class GML3BasicGenerator extends AbstractPropertiesInputOutputHandler implements OutputHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(GML3BasicGenerator.class);

    private SrsSyntax srsSyntax = null;

    private GTHelper gtHelper;

    @Inject
    public void setGTHelper(GTHelper gtHelper){
        this.gtHelper = gtHelper;
    }

    public GML3BasicGenerator() {
        super();
        addSupportedBinding(GTVectorDataBinding.class);
//        Property[] properties = WPSConfig.getInstance().getPropertiesForGeneratorClass(this.getClass().getCanonicalName());
//
//        for (Property property : properties) {
//                    if(property.getName().equals("srsSyntax")){
//                        String srsSyntaxString = property.getStringValue();
//                        if(srsSyntaxString != null && !srsSyntaxString.equals("")){
//                            srsSyntax = GTHelper.getSrsSyntaxFromString(srsSyntaxString);
//                        }
//                    }
//                }
    }

    private SimpleFeatureCollection createCorrectFeatureCollection(FeatureCollection<?, ?> fc) {

        List<SimpleFeature> simpleFeatureList = new ArrayList<SimpleFeature>();
        SimpleFeatureType featureType = null;
        FeatureIterator<?> iterator = fc.features();
        String uuid = UUID.randomUUID().toString();
        int i = 0;
        while (iterator.hasNext()) {
            SimpleFeature feature = (SimpleFeature) iterator.next();

            if (i == 0) {
                featureType = gtHelper.createFeatureType(feature.getProperties(), (Geometry) feature.getDefaultGeometry(), uuid, feature.getFeatureType().getCoordinateReferenceSystem());
                QName qname = gtHelper.createGML3SchemaForFeatureType(featureType);
                SchemaRepository.registerSchemaLocation(qname.getNamespaceURI(), qname.getLocalPart());
            }
            SimpleFeature resultFeature = gtHelper.createFeature("ID" + i, (Geometry) feature.getDefaultGeometry(), featureType, feature.getProperties());

            simpleFeatureList.add(resultFeature);
            i++;
        }
        iterator.close();

        ListFeatureCollection resultFeatureCollection = new ListFeatureCollection(featureType, simpleFeatureList);
        return resultFeatureCollection;

    }

    public void writeToStream(FeatureCollection<?,?> coll, OutputStream os) {
        FeatureCollection<?, ?> fc = coll;

        FeatureCollection<?, ?> correctFeatureCollection = createCorrectFeatureCollection(fc);
        //get the namespace from the features to pass into the encoder
        FeatureType schema = correctFeatureCollection.getSchema();
        String namespace = null;
        String schemaLocation = null;
        if (schema != null) {
            namespace = schema.getName().getNamespaceURI();
            schemaLocation = SchemaRepository.getSchemaLocation(namespace);
        }

        Configuration configuration = null;
        org.geotools.xml.Encoder encoder = null;
        if (schemaLocation == null || namespace == null) {
            namespace = "http://www.opengis.net/gml";
            schemaLocation = "http://schemas.opengis.net/gml/3.1.1/base/feature.xsd";
            configuration = new ApplicationSchemaConfiguration(namespace, schemaLocation);
//new GMLConfiguration();//new
            if (srsSyntax != null) {
                ((GMLConfiguration) configuration).setSrsSyntax(srsSyntax);
            }

            encoder = new org.geotools.xml.Encoder(configuration);
            encoder.setNamespaceAware(true);
            encoder.setSchemaLocation("http://www.opengis.net/gml", "http://schemas.opengis.net/gml/3.1.1/base/feature.xsd");

        } else {

            configuration = new ApplicationSchemaConfiguration(namespace, schemaLocation);
            if (srsSyntax != null) {
                ((org.geotools.gml3.ApplicationSchemaConfiguration) configuration).getDependency(org.geotools.gml3.GMLConfiguration.class).setSrsSyntax(srsSyntax);
            }
            encoder = new org.geotools.xml.Encoder(configuration);
            encoder.setNamespaceAware(true);
            encoder.setSchemaLocation("http://www.opengis.net/gml http://schemas.opengis.net/gml/3.1.1/base/feature.xsd", namespace + " " + schemaLocation);

        }

        fc.features().close();
        //use the gml namespace with the FeatureCollection element to start parsing the collection
        QName ns = new QName("http://www.opengis.net/gml", "FeatureCollection", "wfs");
        try {
            encoder.encode(correctFeatureCollection, ns, os);
        } catch (IOException e) {
            LOGGER.error("Exception while trying to encode FeatureCollection.", e);
            throw new RuntimeException(e);
        }

    }

    public InputStream generateStream(TypedProcessOutputDescription<?> description,
            Data<?> data,
            Format format) throws IOException {
        if (data instanceof GTVectorDataBinding) {

        }
        String uuid = UUID.randomUUID().toString();
        File file = File.createTempFile("gml3" + uuid, ".xml");
        FileOutputStream outputStream = new FileOutputStream(file);
        this.writeToStream( ((GTVectorDataBinding) data).getPayload() , outputStream);
        outputStream.flush();
        outputStream.close();
        if (file.length() <= 0) {
            return null;
        }
        FileInputStream inputStream = new FileInputStream(file);

        return inputStream;

    }

    @Override
    public InputStream generate(TypedProcessOutputDescription<?> description, Data<?> data, Format format) throws IOException, EncodingException {
        if (data instanceof GTVectorDataBinding) {

        }
        String uuid = UUID.randomUUID().toString();
        File file = File.createTempFile("gml3" + uuid, ".xml");
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            this.writeToStream( ((GTVectorDataBinding) data).getPayload() , outputStream);
            outputStream.flush();
        } catch (Exception e){
            throw new RuntimeException("error while generating stream: " + e.getMessage());
        }
        if (file.length() <= 0) {
            return null;
        }
        FileInputStream inputStream = new FileInputStream(file);

        return inputStream;
    }

}
