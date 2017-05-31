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
package org.n52.geoprocessing.jts.io.datahandler.generator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.n52.javaps.annotation.Properties;
import org.n52.javaps.description.TypedProcessOutputDescription;
import org.n52.javaps.io.AbstractPropertiesInputOutputHandler;
import org.n52.javaps.io.Data;
import org.n52.javaps.io.EncodingException;
import org.n52.javaps.io.OutputHandler;
import org.n52.shetland.ogc.wps.Format;
import org.n52.geoprocessing.jts.io.data.binding.complex.JTSGeometryBinding;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * This class generates a String representation out of a JTS Geometry.
 *
 * @author Benjamin Pross
 *
 */
@Properties(
        defaultPropertyFileName = "wkt.properties")
public class WKTGenerator extends AbstractPropertiesInputOutputHandler implements OutputHandler {

    public WKTGenerator() {
        super();
        //addSupportedBinding(JTSGeometryBinding.class);
    }

    public InputStream generate(TypedProcessOutputDescription<?> description,
            Data<?> data,
            Format format) throws IOException, EncodingException {
        if (data instanceof JTSGeometryBinding) {
            Geometry g = ((JTSGeometryBinding) data).getPayload();

            String wktString = new WKTWriter().write(g);

            InputStream is = new ByteArrayInputStream(wktString.getBytes());

            return is;
        }
        return null;
    }

}
