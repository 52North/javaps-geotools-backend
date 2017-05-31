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

import org.n52.javaps.io.complex.ComplexData;

import com.vividsolutions.jts.geom.Geometry;

/**
 * This class wraps a JTS Geometry.
 * @author Benjamin Pross
 *
 */
public class JTSGeometryBinding implements ComplexData<Geometry> {

    /**
     *
     */
    private static final long serialVersionUID = 3415522592135759594L;
    private Geometry geom;

    public JTSGeometryBinding(Geometry geom){
        this.geom = geom;
    }

    public Geometry getPayload() {
        return this.geom;
    }

    public Class<?> getSupportedClass() {
        return Geometry.class;
    }

    public void dispose() {

    }

}
