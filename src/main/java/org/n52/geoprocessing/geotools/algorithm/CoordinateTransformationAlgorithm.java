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
package org.n52.geoprocessing.geotools.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.n52.geoprocessing.geotools.io.data.binding.complex.GTHelper;
import org.n52.geoprocessing.geotools.io.data.binding.complex.GTVectorDataBinding;
import org.n52.javaps.algorithm.annotation.Algorithm;
import org.n52.javaps.algorithm.annotation.ComplexInput;
import org.n52.javaps.algorithm.annotation.ComplexOutput;
import org.n52.javaps.algorithm.annotation.Execute;
import org.n52.javaps.algorithm.annotation.LiteralInput;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

/**
 * This algorithm transforms GML featureCollection from a source epsg into a GML featureCollection of a target_epsg
 *
 * @author Maurin Radtke (m.radtke@52north.org)
 *
 */
@Algorithm(version = "1.1.0")
public class CoordinateTransformationAlgorithm {

    private String target_epsg;
    private String source_epsg;
    private SimpleFeatureCollection data;
    private SimpleFeatureCollection result;
    private GTHelper gtHelper;

    @Inject
    public void setGTHelper(GTHelper gtHelper){
        this.gtHelper = gtHelper;
    }

    @LiteralInput(identifier = "source_epsg")
    public void setSourceEPSG(String source_epsg) {
        this.source_epsg = source_epsg;
    }

    @LiteralInput(identifier = "target_epsg")
    public void setTargetEPSG(String target_epsg) {
        this.target_epsg = target_epsg;
    }

    @ComplexInput(identifier = "data", binding = GTVectorDataBinding.class)
    public void setData(SimpleFeatureCollection data) {
        this.data = data;
    }

    @ComplexOutput(identifier = "result", binding = GTVectorDataBinding.class)
    public SimpleFeatureCollection getResult() {
        return result;
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
        SimpleFeatureIterator featureIterator = this.data.features();

        List<SimpleFeature> listOut = new ArrayList<SimpleFeature>();

        SimpleFeatureType featureType = null;

        try {

            MathTransform tx = CRS.findMathTransform(fromCRS, toCRS, true);

            while (featureIterator.hasNext()) {

                SimpleFeature feature = featureIterator.next();

                Geometry geometry = (Geometry) feature.getDefaultGeometry();

                Geometry newGeometry = JTS.transform(geometry, tx);

                if (featureType == null) {
                    String uuid = UUID.randomUUID().toString();

                    featureType = gtHelper.createFeatureType(feature.getProperties(), newGeometry, uuid, toCRS);
                }

                Feature newFeature = createFeature(feature.getID(),
                        newGeometry, feature.getProperties(), featureType);
                listOut.add((SimpleFeature) newFeature);
            }

        } catch (TransformException te) {
            throw new RuntimeException("Error while transforming: " + te.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Exception while transforming: " + e.getMessage(), e);
        }

        ListFeatureCollection lfc = new ListFeatureCollection(featureType, listOut);

        this.result = lfc;

    }

    private Feature createFeature(String id, Geometry geometry, Collection<Property> properties, SimpleFeatureType featureType) {

        Feature feature = gtHelper.createFeature(id, geometry, featureType,
                properties);

        return feature;
    }
}
