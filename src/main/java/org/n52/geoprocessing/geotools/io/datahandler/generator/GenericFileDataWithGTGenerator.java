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
package org.n52.geoprocessing.geotools.io.datahandler.generator;

import java.io.IOException;
import java.io.InputStream;

import org.n52.geoprocessing.geotools.io.data.binding.complex.GenericFileDataWithGT;
import org.n52.geoprocessing.geotools.io.data.binding.complex.GenericFileDataWithGTBinding;
import org.n52.javaps.annotation.Properties;
import org.n52.javaps.description.TypedProcessOutputDescription;
import org.n52.javaps.io.AbstractPropertiesInputOutputHandler;
import org.n52.javaps.io.Data;
import org.n52.javaps.io.OutputHandler;
import org.n52.shetland.ogc.wps.Format;

/**
 * @author Matthias Mueller, TU Dresden
 *
 */
@Properties(
        defaultPropertyFileName = "genericfiledatawithgt.properties")
public class GenericFileDataWithGTGenerator extends AbstractPropertiesInputOutputHandler implements OutputHandler {

    public GenericFileDataWithGTGenerator (){
        super();
        addSupportedBinding(GenericFileDataWithGTBinding.class);
    }

    public InputStream generate(TypedProcessOutputDescription<?> description, Data<?> data, Format format) throws IOException {

        InputStream theStream = ((GenericFileDataWithGTBinding)data).getPayload().getDataStream();
        return theStream;
    }

    /**
     * conversion method to support translation of output formats
     * TODO: implement logic
     *
     * @param inputFile
     * @return
     */
    private GenericFileDataWithGT convertFile (GenericFileDataWithGT inputFile){
        //not implemented
        return null;
    }

}
