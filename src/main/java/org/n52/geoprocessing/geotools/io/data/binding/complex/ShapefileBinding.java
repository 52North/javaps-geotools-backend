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
package org.n52.geoprocessing.geotools.io.data.binding.complex;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.geotools.data.DataStore;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.n52.javaps.io.complex.ComplexData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ShapefileBinding implements ComplexData<File>{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static Logger LOGGER = LoggerFactory.getLogger(ShapefileBinding.class);


    protected File shpFile;
    protected String mimeType;

    public ShapefileBinding(File shapeFile){
        this.shpFile = shapeFile;
        mimeType = "application/x-zipped-shp";
    }

    @Override
    public File getPayload() {
        return shpFile;
    }

    @Override
    public Class<File> getSupportedClass() {
        return File.class;
    }

    public String getMimeType() {
        return mimeType;
    }

    public File getZippedPayload(){
        String path = shpFile.getAbsolutePath();
        String baseName = path.substring(0, path.length() - ".shp".length());
        File shx = new File(baseName + ".shx");
        File dbf = new File(baseName + ".dbf");
        File prj = new File(baseName + ".prj");
        File zipped = null;
        try {
            zipped = IOUtils.zip(shpFile, shx, dbf, prj);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return zipped;

    }

    public GTVectorDataBinding getPayloadAsGTVectorDataBinding(){
        try {
            DataStore store = new ShapefileDataStore(shpFile.toURI().toURL());
            SimpleFeatureCollection features = store.getFeatureSource(store.getTypeNames()[0]).getFeatures();
            return new GTVectorDataBinding(features);
        } catch (MalformedURLException e) {
            LOGGER.error("Something went wrong while creating data store.", e);
            throw new RuntimeException("Something went wrong while creating data store.", e);
        } catch (IOException e) {
            LOGGER.error("Something went wrong while converting shapefile to FeatureCollection", e);
            throw new RuntimeException("Something went wrong while converting shapefile to FeatureCollection", e);
        }
    }

}
