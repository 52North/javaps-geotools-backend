/*
 * Copyright 2019 52°North Initiative for Geospatial Open Source
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
package org.n52.wps.server.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.locationtech.jts.geom.Geometry;
import org.n52.javaps.algorithm.annotation.Algorithm;
import org.n52.javaps.algorithm.annotation.ComplexInput;
import org.n52.javaps.algorithm.annotation.ComplexOutput;
import org.n52.javaps.algorithm.annotation.Execute;
import org.n52.javaps.algorithm.annotation.LiteralInput;
import org.n52.javaps.gt.io.GTHelper;
import org.n52.javaps.gt.io.data.binding.complex.GTVectorDataBinding;
import org.n52.javaps.io.SchemaRepository;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Algorithm(
        version = "1.1.0")
public class SimpleBufferAlgorithm {

    private static Logger LOGGER = LoggerFactory.getLogger(SimpleBufferAlgorithm.class);

    private Double percentage;

    private SimpleFeatureCollection result;

    private SimpleFeatureCollection data;

    private double width;

    private GTHelper gtHelper;

    public SimpleBufferAlgorithm() {
        super();
    }

    @Inject
    public void setGTHelper(GTHelper gtHelper) {
        this.gtHelper = gtHelper;
    }

    @ComplexOutput(
            identifier = "result",
            binding = GTVectorDataBinding.class)
    public SimpleFeatureCollection getResult() {
        return result;
    }

    @ComplexInput(
            identifier = "data",
            binding = GTVectorDataBinding.class)
    public void setData(SimpleFeatureCollection data) {
        this.data = data;
    }

    @LiteralInput(
            identifier = "width")
    public void setWidth(double width) {
        this.width = width;
    }

    @Execute
    public void runBuffer() {

        int i = 0;
        int totalNumberOfFeatures = data.size();
        String uuid = UUID.randomUUID().toString();
        List<SimpleFeature> featureList = new ArrayList<>();
        SimpleFeatureType featureType = null;
        LOGGER.debug("");
        for (SimpleFeatureIterator ia = data.features(); ia.hasNext();) {
            /**
             * ******* How to publish percentage results ************
             */
            i = i + 1;
            percentage = (double) ((i / totalNumberOfFeatures) * 100);

            /**
             * ******************
             */
            SimpleFeature feature = (SimpleFeature) ia.next();
            Geometry geometry = (Geometry) feature.getDefaultGeometry();
            Geometry geometryBuffered = runBuffer(geometry, width);

            if (i == 1) {
                CoordinateReferenceSystem crs = feature.getFeatureType().getCoordinateReferenceSystem();
                if (geometry.getUserData() instanceof CoordinateReferenceSystem) {
                    crs = (CoordinateReferenceSystem) geometry.getUserData();
                }
                featureType = gtHelper.createFeatureType(feature.getProperties(), geometryBuffered, uuid, crs);
                QName qname = gtHelper.createGML3SchemaForFeatureType(featureType);
                SchemaRepository.registerSchemaLocation(qname.getNamespaceURI(), qname.getLocalPart());

            }

            if (geometryBuffered != null) {
                SimpleFeature createdFeature = (SimpleFeature) gtHelper.createFeature("ID" + i,
                        geometryBuffered, (SimpleFeatureType) featureType, feature.getProperties());
                feature.setDefaultGeometry(geometryBuffered);
                featureList.add(createdFeature);
            } else {
                LOGGER.warn("GeometryCollections are not supported, or result null. Original dataset will be returned");
            }
        }
        result = gtHelper.createSimpleFeatureCollectionFromSimpleFeatureList(featureList);
    }

    private Geometry runBuffer(Geometry a,
            double width) {
        Geometry buffered = null;

        try {
            buffered = a.buffer(width);
            return buffered;
        } catch (RuntimeException ex) {
            LOGGER.trace(ex.getMessage());
        }
        return null;
    }
}
