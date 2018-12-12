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
package org.n52.geoprocessing.geotools.io.datahandler.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.n52.geoprocessing.geotools.io.data.binding.complex.GTVectorDataBinding;
import org.n52.geoprocessing.jts.io.data.binding.complex.JTSGeometryBinding;
import org.n52.javaps.annotation.Properties;
import org.n52.javaps.description.TypedProcessOutputDescription;
import org.n52.javaps.io.AbstractPropertiesInputOutputHandler;
import org.n52.javaps.io.Data;
import org.n52.javaps.io.OutputHandler;
import org.n52.shetland.ogc.wps.Format;

import com.vividsolutions.jts.geom.Geometry;

/**
 * This class generates a GeoJSON String representation out of a JTS Geometry.
 * @author BenjaminPross(bpross-52n)
 *
 */
@Properties(
        defaultPropertyFileName = "geojson.properties")
public class GeoJSONGenerator extends AbstractPropertiesInputOutputHandler implements OutputHandler {

    public GeoJSONGenerator(){
        super();
        addSupportedBinding(JTSGeometryBinding.class);
        addSupportedBinding(GTVectorDataBinding.class);
    }

    @Override
    public InputStream generate(TypedProcessOutputDescription<?> description, Data<?> data, Format format) throws IOException {

        if(data instanceof JTSGeometryBinding){
            Geometry g = ((JTSGeometryBinding)data).getPayload();

            File tempFile = File.createTempFile("wps", "json");

             new GeometryJSON().write(g, tempFile);

            InputStream is = new FileInputStream(tempFile);

            return is;
        }else if(data instanceof GTVectorDataBinding){

            SimpleFeatureCollection f = (SimpleFeatureCollection)data.getPayload();

            File tempFile = File.createTempFile("wps", "json");

             new FeatureJSON().writeFeatureCollection(f, tempFile);

            InputStream is = new FileInputStream(tempFile);

            return is;
        }

        return null;
    }

}
