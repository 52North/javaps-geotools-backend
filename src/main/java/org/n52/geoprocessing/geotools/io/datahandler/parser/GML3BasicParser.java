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
package org.n52.geoprocessing.geotools.io.datahandler.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollections;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.GeometryAttributeImpl;
import org.geotools.feature.type.GeometryDescriptorImpl;
import org.geotools.feature.type.GeometryTypeImpl;
import org.geotools.filter.identity.GmlObjectIdImpl;
import org.geotools.gml3.ApplicationSchemaConfiguration;
import org.geotools.gml3.GMLConfiguration;
import org.geotools.xml.Configuration;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.filter.identity.Identifier;

import org.xml.sax.helpers.DefaultHandler;
import com.vividsolutions.jts.geom.Geometry;
import org.geotools.xml.Parser;

import org.n52.geoprocessing.geotools.io.data.binding.complex.GML2Handler;
import org.n52.geoprocessing.geotools.io.data.binding.complex.GTVectorDataBinding;
import org.n52.geoprocessing.geotools.io.data.binding.complex.SchemaRepository;

import org.n52.javaps.annotation.Properties;
import org.n52.javaps.description.TypedProcessInputDescription;
import org.n52.javaps.io.AbstractPropertiesInputOutputHandler;
import org.n52.javaps.io.DecodingException;
import org.n52.javaps.io.InputHandler;

import org.n52.shetland.ogc.wps.Format;

/**
 * This parser handles xml files compliant to gmlpacket.xsd
 *
 * @author schaeffer
 *
 */
@Properties(
        defaultPropertyFileName = "gml.properties")
public class GML3BasicParser extends AbstractPropertiesInputOutputHandler implements InputHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(GML3BasicParser.class);

    public GML3BasicParser() {
        super();
        // Setting the system-wide default at startup time
        System.setProperty("org.geotools.referencing.forceXY", "true");
        addSupportedBinding(GTVectorDataBinding.class);
    }

    /**
     * public GTVectorDataBinding parse(InputStream stream, String mimeType,
     * String schema) {
     *
     * FileOutputStream fos = null; try{ File tempFile =
     * File.createTempFile("wps", "tmp"); //finalizeFiles.add(tempFile); // mark
     * for final delete fos = new FileOutputStream(tempFile); int i =
     * stream.read(); while(i != -1){ fos.write(i); i = stream.read(); }
     * fos.flush(); fos.close(); GTVectorDataBinding data = parseXML(tempFile);
     * return data; } catch(IOException e) { if (fos != null) try { fos.close();
     * } catch (Exception e1) { } throw new IllegalArgumentException("Error
     * while creating tempFile", e); }
    }
     */
    public GTVectorDataBinding parse(File file, String mimeType, String schema) {
        return parseXML(file);
    }

    private GTVectorDataBinding parseXML(File file) {

        SimpleFeatureCollection fc = parseFeatureCollection(file);

        GTVectorDataBinding data = new GTVectorDataBinding(fc);

        return data;
    }

    /**
     * Method to parse a SimpleFeatureCollection out of a file. Depending on the
     * schema and schema location the Configuration will be a GML or
     * ApplicationSchemaConfiguration and the Parser will be set strict or not.
     * *
     *
     * @param file File containing a SimpleFeatureCollection
     * @return The parsed SimpleFeatureCollection
     */
    public SimpleFeatureCollection parseFeatureCollection(File file) {
        QName schematypeTuple = determineFeatureTypeSchema(file);

        boolean schemaLocationIsRelative = false;
        if (!(schematypeTuple.getLocalPart().contains("://") || schematypeTuple.getLocalPart().contains("file:"))) {
            schemaLocationIsRelative = true;
        }

        Configuration configuration = null;

        boolean shouldSetParserStrict = true;
        if (schematypeTuple != null) {

            String schemaLocation = schematypeTuple.getLocalPart();

            if (schemaLocationIsRelative) {
                schemaLocation = new File(file.getParentFile(), schemaLocation).getAbsolutePath();
            }

            if (schemaLocation.equals("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd")) {
                configuration = new GMLConfiguration();
                shouldSetParserStrict = false;
            } else {
                if (schemaLocation != null && schematypeTuple.getNamespaceURI() != null) {
                    SchemaRepository.registerSchemaLocation(schematypeTuple.getNamespaceURI(), schemaLocation);
                    configuration = new ApplicationSchemaConfiguration(schematypeTuple.getNamespaceURI(), schemaLocation);
                } else {
                    configuration = new GMLConfiguration();
                    shouldSetParserStrict = false;
                }
            }
        }

        Parser parser = new Parser(configuration);

        parser.setStrict(shouldSetParserStrict);

        //parse
        SimpleFeatureCollection fc = parseFeatureCollection(file, configuration, shouldSetParserStrict);

        return fc;
    }

    /**
     * Method to parse a SimpleFeatureCollection out of a file.
     *
     * @param file File containing a SimpleFeatureCollection
     * @param configuration The Configuration for the Parser
     * @param shouldSetParserStrict Boolean specifying whether the Parser should
     * be set to strict or not.
     * @return The parsed SimpleFeatureCollection
     */
    public SimpleFeatureCollection parseFeatureCollection(File file, Configuration configuration, boolean shouldSetParserStrict) {

        Parser parser = new Parser(configuration);

        parser.setStrict(shouldSetParserStrict);

        //parse
        SimpleFeatureCollection fc = DefaultFeatureCollections.newCollection();
        try {
            Object parsedData = parser.parse(new FileInputStream(file));
            if (parsedData instanceof FeatureCollection) {
                fc = (SimpleFeatureCollection) parsedData;
            } else if (parsedData instanceof HashMap) {
                List<?> possibleSimpleFeatureList = ((ArrayList<?>) ((HashMap<?, ?>) parsedData).get("featureMember"));

                if (possibleSimpleFeatureList != null) {
                    List<SimpleFeature> simpleFeatureList = new ArrayList<SimpleFeature>();

                    SimpleFeatureType sft = null;

                    for (Object possibleSimpleFeature : possibleSimpleFeatureList) {

                        if (possibleSimpleFeature instanceof SimpleFeature) {
                            SimpleFeature sf = ((SimpleFeature) possibleSimpleFeature);
                            if (sft == null) {
                                sft = sf.getType();
                            }
                            simpleFeatureList.add(sf);
                        }
                    }

                    fc = new ListFeatureCollection(sft, simpleFeatureList);
                } else {
                    fc = (SimpleFeatureCollection) ((HashMap<?, ?>) parsedData).get("FeatureCollection");
                }
            } else if (parsedData instanceof SimpleFeature) {

                Collection<? extends Property> values = ((SimpleFeature) parsedData).getValue();
                for (Property value : values) {
                    Object tempValue = value.getValue();
                    if (value.getType().getBinding().isAssignableFrom(FeatureCollection.class)) {
                        if (tempValue instanceof ArrayList) {
                            ArrayList<?> list = (ArrayList<?>) tempValue;
                            List<SimpleFeature> simpleFeatureList = new ArrayList<SimpleFeature>();
                            SimpleFeatureType sft = null;
                            for (Object listValue : list) {
                                if (listValue instanceof SimpleFeature) {
                                    SimpleFeature sf = ((SimpleFeature) listValue);
                                    if (sft == null) {
                                        sft = sf.getType();
                                    }
                                    simpleFeatureList.add(sf);
                                }
                            }
                            fc = new ListFeatureCollection(sft, simpleFeatureList);
                        }
                    }
                }

            }

            FeatureIterator<?> featureIterator = fc.features();
            while (featureIterator.hasNext()) {
                SimpleFeature feature = (SimpleFeature) featureIterator.next();
                if (feature.getDefaultGeometry() == null) {
                    Collection<org.opengis.feature.Property> properties = feature.getProperties();
                    for (org.opengis.feature.Property property : properties) {
                        try {
                            Geometry g = (Geometry) property.getValue();
                            if (g != null) {
                                GeometryAttribute oldGeometryDescriptor = feature.getDefaultGeometryProperty();
                                GeometryType type = new GeometryTypeImpl(property.getName(), (Class<?>) oldGeometryDescriptor.getType().getBinding(), oldGeometryDescriptor.getType().getCoordinateReferenceSystem(), oldGeometryDescriptor.getType().isIdentified(), oldGeometryDescriptor.getType().isAbstract(), oldGeometryDescriptor.getType().getRestrictions(), oldGeometryDescriptor.getType().getSuper(), oldGeometryDescriptor.getType().getDescription());

                                GeometryDescriptor newGeometryDescriptor = new GeometryDescriptorImpl(type, property.getName(), 0, 1, true, null);
                                Identifier identifier = new GmlObjectIdImpl(feature.getID());
                                GeometryAttributeImpl geo = new GeometryAttributeImpl((Object) g, newGeometryDescriptor, identifier);
                                feature.setDefaultGeometryProperty(geo);
                                feature.setDefaultGeometry(g);

                            }
                        } catch (ClassCastException e) {
                            //do nothing
                        }

                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception while handling parsed GML.", e);
            throw new RuntimeException(e);
        }
        return fc;
    }

    private QName determineFeatureTypeSchema(File file) {
        try {
            GML2Handler handler = new GML2Handler();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.newSAXParser().parse(new FileInputStream(file), (DefaultHandler) handler);
            String schemaUrl = handler.getSchemaUrl();
            if (schemaUrl == null) {
                return null;
            }
            String namespaceURI = handler.getNameSpaceURI();
            return new QName(namespaceURI, schemaUrl);

        } catch (Exception e) {
            LOGGER.error("Exception while trying to determine schema of FeatureType.", e);
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public GTVectorDataBinding parse(TypedProcessInputDescription<?> description, InputStream input, Format format) throws IOException, DecodingException {
        FileOutputStream fos = null;
        try {
            File tempFile = File.createTempFile("wps", "tmp");
            //finalizeFiles.add(tempFile); // mark for final delete
            fos = new FileOutputStream(tempFile);
            int i = input.read();
            while (i != -1) {
                fos.write(i);
                i = input.read();
            }
            fos.flush();
            fos.close();
            GTVectorDataBinding data = parseXML(tempFile);
            return data;
        } catch (IOException e) {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e1) {
                }
            }
            throw new IllegalArgumentException("Error while creating tempFile", e);
        }
    }

}