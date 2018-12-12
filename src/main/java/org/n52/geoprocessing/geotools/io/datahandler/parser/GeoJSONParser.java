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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.n52.geoprocessing.geotools.io.data.binding.complex.GTVectorDataBinding;
import org.n52.geoprocessing.jts.io.data.binding.complex.JTSGeometryBinding;
import org.n52.javaps.annotation.Properties;
import org.n52.javaps.description.TypedProcessInputDescription;
import org.n52.javaps.io.AbstractPropertiesInputOutputHandler;
import org.n52.javaps.io.Data;
import org.n52.javaps.io.InputHandler;
import org.n52.shetland.ogc.wps.Format;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;

/**
 *
 *
 * This class parses json into JTS geometries.
 *
 *  @author BenjaminPross(bpross-52n)
 *
 */
@Properties(
        defaultPropertyFileName = "geojson.properties")
public class GeoJSONParser extends AbstractPropertiesInputOutputHandler implements InputHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(GeoJSONParser.class);

    public GeoJSONParser() {
        super();
        addSupportedBinding(JTSGeometryBinding.class);
        addSupportedBinding(GTVectorDataBinding.class);
    }

    @Override
    public Data<?> parse(TypedProcessInputDescription<?> description, InputStream input, Format format) {

        String geojsonstring = "";

        String line = "";

        BufferedReader breader = new BufferedReader(
                new InputStreamReader(input));

        try {
            while ((line = breader.readLine()) != null) {
                geojsonstring = geojsonstring.concat(line);
            }
        } catch (IOException e) {
            LOGGER.error("Exception while reading inputstream.", e);
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        if (geojsonstring.contains("FeatureCollection")) {

            try {
                FeatureCollection<?, ?> featureCollection = new FeatureJSON()
                        .readFeatureCollection(geojsonstring);

                return new GTVectorDataBinding((SimpleFeatureCollection)featureCollection);

            } catch (IOException e) {
                LOGGER.info("Could not read FeatureCollection from inputstream");
            }

        } else if (geojsonstring.contains("Feature")) {

            try {
                SimpleFeature feature = new FeatureJSON().readFeature(geojsonstring);

                List<SimpleFeature> featureList = new ArrayList<SimpleFeature>();

                featureList.add(feature);

                ListFeatureCollection featureCollection = new ListFeatureCollection(
                        feature.getFeatureType(), featureList);

                return new GTVectorDataBinding(featureCollection);

            } catch (IOException e) {
                LOGGER.info("Could not read Feature from inputstream");
            }

        } else if (geojsonstring.contains("GeometryCollection")) {

            try {
                GeometryCollection g = new GeometryJSON().readGeometryCollection(geojsonstring);

                return new JTSGeometryBinding(g);

            } catch (IOException e) {
                LOGGER.info("Could not read GeometryCollection from inputstream.");
            }

        } else if(geojsonstring.contains("Point") ||
                geojsonstring.contains("LineString") ||
                geojsonstring.contains("Polygon") ||
                geojsonstring.contains("MultiPoint") ||
                geojsonstring.contains("MultiLineString") ||
                geojsonstring.contains("MultiPolygon")){

            try {
                Geometry g = new GeometryJSON().read(geojsonstring);

                return new JTSGeometryBinding(g);

            } catch (IOException e) {
                LOGGER.info("Could not read single Geometry from inputstream.");
            }

        }
        LOGGER.error("Could not parse inputstream, returning null.");
        return null;
    }

}
