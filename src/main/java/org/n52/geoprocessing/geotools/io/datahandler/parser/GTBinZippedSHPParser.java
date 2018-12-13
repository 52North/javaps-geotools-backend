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
package org.n52.geoprocessing.geotools.io.datahandler.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.geotools.data.DataStore;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.n52.geoprocessing.geotools.io.data.binding.complex.GTVectorDataBinding;
import org.n52.geoprocessing.geotools.io.data.binding.complex.IOUtils;
import org.n52.javaps.annotation.Properties;
import org.n52.javaps.description.TypedProcessInputDescription;
import org.n52.javaps.io.AbstractPropertiesInputOutputHandler;
import org.n52.javaps.io.Data;
import org.n52.javaps.io.DecodingException;
import org.n52.javaps.io.InputHandler;
import org.n52.shetland.ogc.wps.Format;

@Properties(
        defaultPropertyFileName = "zipped-shp.properties")
public class GTBinZippedSHPParser extends AbstractPropertiesInputOutputHandler implements InputHandler {

    public GTBinZippedSHPParser() {
        super();
        addSupportedBinding(GTVectorDataBinding.class);
    }

    /**
     * @throws RuntimeException
     *             if an error occurs while writing the stream to disk or
     *             unzipping the written file
     */
    @Override
    public Data<?> parse(TypedProcessInputDescription<?> description,
            InputStream input,
            Format format) throws IOException, DecodingException {
        try {
            String fileName = "tempfile" + UUID.randomUUID() + ".zip";
            String tmpDirPath = System.getProperty("java.io.tmpdir");
            File tempFile = new File(tmpDirPath + File.separatorChar + fileName);
            try {
                FileOutputStream outputStream = new FileOutputStream(tempFile);
                byte buf[] = new byte[4096];
                int len;
                while ((len = input.read(buf)) > 0) {
                    outputStream.write(buf, 0, len);
                }
                outputStream.close();
                input.close();
            } catch (FileNotFoundException e) {
                System.gc();
                throw new RuntimeException(e);
            } catch (IOException e1) {
                System.gc();
                throw new RuntimeException(e1);
            }
            File shp = IOUtils.unzip(tempFile, "shp").get(0);
            DataStore store = new ShapefileDataStore(shp.toURI().toURL());
            SimpleFeatureCollection features = store.getFeatureSource(store.getTypeNames()[0]).getFeatures();
            System.gc();

            return new GTVectorDataBinding(features);
        } catch (IOException e) {
            throw new RuntimeException("An error has occurred while accessing provided data", e);
        }
    }

}
