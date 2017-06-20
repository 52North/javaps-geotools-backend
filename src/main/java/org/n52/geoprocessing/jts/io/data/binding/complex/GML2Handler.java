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
package org.n52.geoprocessing.jts.io.data.binding.complex;

import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Theodor Foerster, ITC
 *
 */
public class GML2Handler extends DefaultHandler {

    private Logger LOGGER = LoggerFactory.getLogger(GML2Handler.class);
    // private static String SCHEMA = "http://www.opengis.net/wfs";
    private String  schemaUrl;
    private String nameSpaceURI;
    private boolean rootVisited = false;
    private Map<String, String> namespaces = new HashMap<String, String>();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if(rootVisited) {
            return;
        }
        // check if root is a xml-beans element.
        if(localName.equals("xml-fragment")) {
            return;
        }
        rootVisited = true;
        String schemaLocationAttr = attributes.getValue(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation");
        if(schemaLocationAttr == null) {
            LOGGER.debug("schemaLocation attribute is not set correctly with namespace");
            schemaLocationAttr = attributes.getValue("xsi:schemaLocation");
            if(schemaLocationAttr == null){
                schemaLocationAttr = attributes.getValue("schemaLocation");
            }
        }
        String[] locationStrings = schemaLocationAttr.replace("  ", " ").split(" ");
        if(locationStrings.length % 2 != 0) {
            LOGGER.debug("schemaLocation does not reference locations correctly, odd number of whitespace separated addresses");
            return;
        }
        for(int i = 0; i< locationStrings.length; i++) {
            if(i % 2 == 0 && !locationStrings[i].equals("http://www.opengis.net/wfs") && !locationStrings[i].equals("http://www.opengis.net/gml") && !locationStrings[i].equals("")){
                nameSpaceURI = locationStrings[i];
                schemaUrl = locationStrings[i + 1];
                return;
            }
        }
    }

    public String getSchemaUrl(){
        return schemaUrl;
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        super.startPrefixMapping(prefix, uri);
        namespaces.put(prefix, uri);
    }

    public String getNameSpaceURI() {
        return nameSpaceURI;
    }




}
