/*
 * Copyright 2016 52°North Initiative for Geospatial Open Source
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

import org.geotools.data.simple.SimpleFeatureCollection;
import org.n52.javaps.io.complex.ComplexData;

//import org.n52.wps.io.datahandler.generator.SimpleGMLGenerator;
//import org.n52.wps.io.datahandler.parser.SimpleGMLParser;

public class GTVectorDataBinding implements ComplexData<SimpleFeatureCollection> {


    /**
     *
     */
    private static final long serialVersionUID = -7498123687332359504L;

    protected transient SimpleFeatureCollection featureCollection;

    public GTVectorDataBinding(SimpleFeatureCollection payload) {
        this.featureCollection = payload;
    }

    public Class<?> getSupportedClass() {
        return SimpleFeatureCollection.class;
    }

    public SimpleFeatureCollection getPayload() {
        return this.featureCollection;
    }

    public File getPayloadAsShpFile() {
        try {
            return GenericFileDataWithGT.getShpFile(featureCollection);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not transform Feature Collection into shp file. Reason " + e.getMessage());
        }

    }

    /**
    private synchronized void writeObject(java.io.ObjectOutputStream oos) throws IOException {
        StringWriter buffer = new StringWriter();
        SimpleGMLGenerator generator = new SimpleGMLGenerator();
        generator.write(this, buffer);
        oos.writeObject(buffer.toString());
    }*/

    /**
    private synchronized void readObject(java.io.ObjectInputStream oos) throws IOException, ClassNotFoundException {
        SimpleGMLParser parser = new SimpleGMLParser();

        InputStream stream = new ByteArrayInputStream(((String) oos.readObject()).getBytes());

        // use a default configuration for the parser by requesting the first supported format and schema
        GTVectorDataBinding data = parser.parse(stream, parser.getSupportedFormats()[0], parser.getSupportedEncodings()[0]);

        this.featureCollection = data.getPayload();
    }*/

    /**
    @Override
    public void dispose() {

    }*/

}
