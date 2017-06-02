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
package org.n52.wps.io.test.datahandler.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import org.n52.geoprocessing.jts.algorithm.AlgorithmTest;
import org.n52.geoprocessing.jts.io.datahandler.generator.GML3BasicGenerator;
import org.n52.geoprocessing.jts.io.datahandler.parser.GML3BasicParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;
import org.junit.Test;
import org.n52.geoprocessing.jts.io.data.binding.complex.GTVectorDataBinding;
import org.n52.javaps.description.TypedProcessInputDescription;
import org.n52.shetland.ogc.wps.Format;

/**
 *
 * @author Maurin Radtke (m.radtke@52north.org)
 */
public class GML3BasicGeneratorTest {

    Logger LOGGER = LoggerFactory.getLogger(AlgorithmTest.class);
    String projectRoot = "";
    GML3BasicGenerator dataHandler;

    @Test
    public void testParser() {

        //assertTrue(isDataHandlerActive());
        File f = new File(this.getClass().getProtectionDomain().getCodeSource()
                .getLocation().getFile());
        projectRoot = f.getParentFile().getParentFile().getParent();
        String testFilePath = projectRoot
                + "\\javaps-geotools-backend\\src\\test\\resources\\spearfish_restricted_sites_gml3.xml";

        try {
            testFilePath = URLDecoder.decode(testFilePath, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            fail(e1.getMessage());
        }

        GML3BasicParser theParser = new GML3BasicParser();

//		String[] mimetypes = theParser.getSupportedFormats();
        InputStream input = null;

        try {
            input = new FileInputStream(new File(testFilePath));
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        }

        TypedProcessInputDescription<?> tpid = null;
        Format format = new Format("text/xml", "subtype=gml/3.2.1", "http://schemas.opengis.net/gml/3.2.1/base/feature.xsd");

        // for (String mimetype : mimetypes) {
        dataHandler = new GML3BasicGenerator();
        try {
            GTVectorDataBinding theBinding = theParser.parse(tpid, input, format);

            InputStream resultStream = dataHandler.generateStream(null, theBinding, null);

            GTVectorDataBinding parsedGeneratedBinding = theParser.parse(null, resultStream, null);

            assertNotNull(parsedGeneratedBinding.getPayload());
            assertTrue(theBinding.getPayload().size() == theBinding.getPayload().size());
            assertTrue(parsedGeneratedBinding.getPayloadAsShpFile().exists());
            assertTrue(!parsedGeneratedBinding.getPayload().isEmpty());

//            InputStream resultStreamBase64 = dataHandler.generateBase64Stream(theBinding, "text/xml; subtype=gml/3.2.1", "http://schemas.opengis.net/gml/3.2.1/base/feature.xsd");
//
//            GTVectorDataBinding parsedGeneratedBindingBase64 = (GTVectorDataBinding) theParser.parseBase64(resultStreamBase64, "text/xml; subtype=gml/3.2.1", "http://schemas.opengis.net/gml/3.2.1/base/feature.xsd");
//
//            assertNotNull(parsedGeneratedBindingBase64.getPayload());
//            assertTrue(parsedGeneratedBindingBase64.getPayloadAsShpFile().exists());
//            assertTrue(!parsedGeneratedBindingBase64.getPayload().isEmpty());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        // }
    }

}

