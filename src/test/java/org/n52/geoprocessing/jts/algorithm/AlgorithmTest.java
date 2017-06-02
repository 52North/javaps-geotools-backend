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
package org.n52.geoprocessing.jts.algorithm;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import java.net.URLDecoder;
import java.io.File;
import java.io.UnsupportedEncodingException;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import org.n52.geoprocessing.jts.io.datahandler.parser.GML3BasicParser;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Maurin Radtke (m.radtke@52north.org)
 */
public class AlgorithmTest {

    Logger LOGGER = LoggerFactory.getLogger(AlgorithmTest.class);
    String projectRoot = "";

    JTSCoordinationTransformationAlgorithm algo = null;
    SimpleFeatureCollection sfc = null;

    @Before
    public void setUp() {
        // Setting the system-wide default at startup time
        System.setProperty("org.geotools.referencing.forceXY", "true");
        algo = new JTSCoordinationTransformationAlgorithm();
        algo.setSourceEPSG("EPSG:4326");

        File f = new File(this.getClass().getProtectionDomain().getCodeSource()
                .getLocation().getFile());

        projectRoot = f.getParentFile().getParentFile().getParent();
        String testFilePath = projectRoot
                + "\\javaps-geotools-backend\\src\\test\\resources\\gml_example_1.gml";

        try {
            testFilePath = URLDecoder.decode(testFilePath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            fail(e.getMessage());
        };

        GML3BasicParser dataHandler = new GML3BasicParser();

        File testFile = new File(testFilePath);
        sfc = dataHandler.parseFeatureCollection(testFile);
        algo.setData(sfc);
    }

    /**
     * tests a coordinate transformation of 146°N from epsg:4236 --> epsg:3857,
     * which is expected to fail because 146°N is too close to a pole.
     */
    @Test
    public void testFailTransform() {
        String testFilePath = projectRoot
                + "\\javaps-geotools-backend\\src\\test\\resources\\gml_example.gml";

        try {
            testFilePath = URLDecoder.decode(testFilePath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            fail(e.getMessage());
        };

        GML3BasicParser dataHandler = new GML3BasicParser();

        File testFile = new File(testFilePath);
        sfc = dataHandler.parseFeatureCollection(testFile);
        algo.setData(sfc);
        algo.setTargetEPSG("EPSG:3857");

        try {
            algo.runAlgorithm();
        } catch (RuntimeException re) {
            System.out.println(re.getMessage());
            assert (re.getMessage().equals(
                    "Error while transforming: Latitude 146°34.4'N is too close to a pole."));
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    /**
     * tests a coordinate transformation from epsg:4236 --> epsg:3857
     */
    @Test
    public void testCorrect3857Transform() {
        algo.setTargetEPSG("EPSG:3857");

        try {
            algo.runAlgorithm();
            FeatureCollection fc = algo.getOutput();
            FeatureIterator<?> featureIterator = fc.features();
            SimpleFeature feature = (SimpleFeature) featureIterator.next();
            Geometry geometry = (Geometry) feature.getDefaultGeometry();
            Coordinate[] coords = geometry.getCoordinates();
            assertEquals(5781631.60732, coords[0].x, 0.001);
            assertEquals(1002058.9016, coords[0].y, 0.001);
            assertEquals(5364555.78035, coords[1].x, 0.001);
            assertEquals(908752.762799, coords[1].y, 0.001);
            assertEquals(5810127.070382, coords[2].x, 0.001);
            assertEquals(883747.339307, coords[2].y, 0.001);
            assertEquals(5770326.33379, coords[3].x, 0.001);
            assertEquals(861269.968343, coords[3].y, 0.001);
            assertEquals(5581093.09574, coords[4].x, 0.001);
            assertEquals(772727.762141, coords[4].y, 0.001);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    /**
     * tests a coordinate transformation from epsg:4236 --> epsg:4817
     */
    @Test
    public void testCorrect4817Transform() {
        algo.setTargetEPSG("EPSG:4817");

        try {
            algo.runAlgorithm();
            FeatureCollection fc = algo.getOutput();
            FeatureIterator<?> featureIterator = fc.features();
            SimpleFeature feature = (SimpleFeature) featureIterator.next();
            Geometry geometry = (Geometry) feature.getDefaultGeometry();
            Coordinate[] coords = geometry.getCoordinates();
            assertEquals(41.2178843506, coords[0].x, 0.001);
            assertEquals(8.95903973526, coords[0].y, 0.001);
            assertEquals(37.4710724824, coords[1].x, 0.001);
            assertEquals(8.13026146044, coords[1].y, 0.001);
            assertEquals(41.473842952, coords[2].x, 0.001);
            assertEquals(7.90771407361, coords[2].y, 0.001);
            assertEquals(41.1162889713, coords[3].x, 0.001);
            assertEquals(7.70767521468, coords[3].y, 0.001);
            assertEquals(39.4162959551, coords[4].x, 0.001);
            assertEquals(6.91879868251, coords[4].y, 0.001);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

}
