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
package org.n52.wps.server.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureIterator;
import org.n52.geoprocessing.geotools.io.data.binding.complex.GTHelper;
import org.n52.geoprocessing.geotools.io.data.binding.complex.GTVectorDataBinding;
import org.n52.geoprocessing.geotools.io.data.binding.complex.SchemaRepository;
import org.n52.javaps.algorithm.annotation.Algorithm;
import org.n52.javaps.algorithm.annotation.ComplexInput;
import org.n52.javaps.algorithm.annotation.ComplexOutput;
import org.n52.javaps.algorithm.annotation.Execute;
import org.n52.javaps.algorithm.annotation.LiteralInput;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;


@Algorithm(version = "1.1.0")
public class SimpleBufferAlgorithm {

    private static Logger LOGGER = LoggerFactory.getLogger(SimpleBufferAlgorithm.class);
    private Double percentage;

    public SimpleBufferAlgorithm() {
        super();
    }

    private SimpleFeatureCollection result;
    private SimpleFeatureCollection data;
    private double width;
    private GTHelper gtHelper;

    @Inject
    public void setGTHelper(GTHelper gtHelper){
        this.gtHelper = gtHelper;
    }

    @ComplexOutput(identifier = "result", binding = GTVectorDataBinding.class)
    public SimpleFeatureCollection getResult() {
        return result;
    }

    @ComplexInput(identifier = "data", binding = GTVectorDataBinding.class)
    public void setData(SimpleFeatureCollection data) {
        this.data = data;
    }

    @LiteralInput(identifier = "width")
    public void setWidth(double width) {
        this.width = width;
    }

    @Execute
    public void runBuffer() {

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //Collection resultColl = new ArrayList();
        double i = 0;
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
            percentage = (i / totalNumberOfFeatures) * 100;
//            this.update(new Integer(percentage.intValue()));

            /**
             * ******************
             */
            SimpleFeature feature = (SimpleFeature) ia.next();
            Geometry geometry = (Geometry) feature.getDefaultGeometry();
            Geometry geometryBuffered = runBuffer(geometry, width);

            if (i == 1) {
                CoordinateReferenceSystem crs = feature.getFeatureType().getCoordinateReferenceSystem();
                if (geometry.getUserData() instanceof CoordinateReferenceSystem) {
                    crs = ((CoordinateReferenceSystem) geometry.getUserData());
                }
                featureType = gtHelper.createFeatureType(feature.getProperties(), geometryBuffered, uuid, crs);
                QName qname = gtHelper.createGML3SchemaForFeatureType(featureType);
                SchemaRepository.registerSchemaLocation(qname.getNamespaceURI(), qname.getLocalPart());

            }

            if (geometryBuffered != null) {
                SimpleFeature createdFeature = (SimpleFeature) gtHelper.createFeature("ID" + new Double(i).intValue(), geometryBuffered, (SimpleFeatureType) featureType, feature.getProperties());
                feature.setDefaultGeometry(geometryBuffered);
                featureList.add(createdFeature);
            } else {
                LOGGER.warn("GeometryCollections are not supported, or result null. Original dataset will be returned");
            }
        }
        result = gtHelper.createSimpleFeatureCollectionFromSimpleFeatureList(featureList);
    }

    private Geometry runBuffer(Geometry a, double width) {
        Geometry buffered = null;

        try {
            buffered = a.buffer(width);
            return buffered;
        } catch (RuntimeException ex) {
            // simply eat exceptions and report them by returning null
        }
        return null;
    }
}
