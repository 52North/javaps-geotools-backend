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
import java.util.Collection;
import java.util.UUID;
import org.geotools.feature.DefaultFeatureCollections;
import org.n52.javaps.algorithm.annotation.Algorithm;
import org.n52.javaps.algorithm.annotation.LiteralInput;
import org.n52.javaps.algorithm.annotation.Execute;
import org.n52.javaps.algorithm.annotation.ComplexInput;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.n52.geoprocessing.jts.io.data.binding.complex.GTHelper;
import org.n52.geoprocessing.jts.io.data.binding.complex.GTVectorDataBinding;
import org.n52.javaps.algorithm.annotation.ComplexOutput;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * This algorithm is a testdummy
 *
 * @author Maurin Radtke (m.radtke@52north.org)
 *
 */
@Algorithm(version = "1.1.0")
public class JTSCoordinationTransformationAlgorithm {

    private String target_epsg;
    private String source_epsg;
    private FeatureCollection<?, ?> data;
    private FeatureCollection<?, ?> result;
    private SimpleFeatureType featureType;

    @LiteralInput(identifier = "source_epsg")
    public void setSourceEPSG(String source_epsg) {
        this.source_epsg = source_epsg;
    }

    @LiteralInput(identifier = "target_epsg")
    public void setTargetEPSG(String target_epsg) {
        this.target_epsg = target_epsg;
    }

    @ComplexInput(identifier = "data", binding = GTVectorDataBinding.class)
    public void setData(FeatureCollection<?, ?> data) {
        this.data = data;
    }

    @ComplexOutput(identifier = "result", binding = GTVectorDataBinding.class)
    public FeatureCollection<?, ?> getOutput() {
        return this.result;
    }

    @Execute
    public void runAlgorithm() {

        // 1. decode target CRS by epsg code:
        CoordinateReferenceSystem toCRS = null;
        try {

            toCRS = CRS.decode(this.target_epsg);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not determine target CRS. Valid EPSG code needed.",
                    e);
        }

        if (toCRS == null) {
            throw new RuntimeException(
                    "Could not determine target CRS. Valid EPSG code needed.");
        }

        // 2. decode source CRS by epsg code:
        CoordinateReferenceSystem fromCRS = null;
        try {

            fromCRS = CRS.decode(this.source_epsg);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not determine source CRS. Valid EPSG code needed.",
                    e);
        }

        if (fromCRS == null) {
            throw new RuntimeException(
                    "Could not determine source CRS. Valid EPSG code needed.");
        }

        // 3. Transform featureCollection:
        FeatureIterator<?> featureIterator = this.data.features();

        FeatureCollection fOut = DefaultFeatureCollections.newCollection();
        this.result = DefaultFeatureCollections.newCollection();

        try {

            MathTransform tx = CRS.findMathTransform(fromCRS, toCRS, true);

            int coordinates = 0;

            while (featureIterator.hasNext()) {

                SimpleFeature feature = (SimpleFeature) featureIterator.next();

                Geometry geometry = (Geometry) feature.getDefaultGeometry();

                coordinates = coordinates + geometry.getCoordinates().length;

                Coordinate[] coords = geometry.getCoordinates();

                for (Coordinate coordinate : coords) {
                    Coordinate k = new Coordinate();
                    k = JTS.transform(coordinate, k, tx);
                }

                Geometry newGeometry = JTS.transform(geometry, tx);

                Feature newFeature = createFeature(feature.getID(),
                        newGeometry, toCRS, feature.getProperties());

                fOut.add(newFeature);
            }

        } catch (TransformException te) {
            throw new RuntimeException("Error while transforming: " + te.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error while transforming", e);
        }

        this.result = fOut;
    }

    private Feature createFeature(String id, Geometry geometry,
            CoordinateReferenceSystem crs, Collection<Property> properties) {
        String uuid = UUID.randomUUID().toString();

        if (featureType == null) {
            featureType = GTHelper.createFeatureType(properties,
                    geometry, uuid, crs);
            GTHelper.createGML3SchemaForFeatureType(featureType);
        }

        Feature feature = GTHelper.createFeature(id, geometry, featureType,
                properties);

        return feature;
    }
}
