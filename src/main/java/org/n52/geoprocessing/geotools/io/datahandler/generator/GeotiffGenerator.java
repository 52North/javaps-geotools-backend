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
package org.n52.geoprocessing.geotools.io.datahandler.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.media.jai.JAI;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.imageio.GeoToolsWriteParams;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffWriteParams;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.n52.geoprocessing.geotools.io.data.binding.complex.GTRasterDataBinding;
import org.n52.geoprocessing.geotools.io.data.binding.complex.GeotiffBinding;
import org.n52.javaps.description.TypedProcessOutputDescription;
import org.n52.javaps.io.AbstractPropertiesInputOutputHandler;
import org.n52.javaps.io.Data;
import org.n52.javaps.io.OutputHandler;
import org.n52.shetland.ogc.wps.Format;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeotiffGenerator extends AbstractPropertiesInputOutputHandler implements OutputHandler {
    private static Logger LOGGER = LoggerFactory.getLogger(GeotiffGenerator.class);

    public GeotiffGenerator() {
        super();
        addSupportedBinding(GTRasterDataBinding.class);
        addSupportedBinding(GeotiffBinding.class);
    }

    public InputStream generate(TypedProcessOutputDescription<?> description,
            Data<?> data,
            Format format) throws IOException {

        InputStream stream = null;

        if ((data instanceof GTRasterDataBinding)) {

            GridCoverage coverage = ((GTRasterDataBinding) data).getPayload();
            GeoTiffWriter geoTiffWriter = null;
            String tmpDirPath = System.getProperty("java.io.tmpdir");
            String fileName = tmpDirPath + File.separatorChar + "temp" + UUID.randomUUID() + ".tmp";
            File outputFile = new File(fileName);

            try {
                geoTiffWriter = new GeoTiffWriter(outputFile);
                writeGeotiff(geoTiffWriter, coverage);
                geoTiffWriter.dispose();
                stream = new FileInputStream(outputFile);

            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                throw new IOException("Could not create output due to an IO error");
            }
        }
        if (data instanceof GeotiffBinding) {
            File geotiff = ((GeotiffBinding) data).getPayload();
            try {
                stream = new FileInputStream(geotiff);
            } catch (FileNotFoundException e) {
                throw new IOException("Error while generating geotiff. Source file not found.");
            }
        }

        return stream;
    }

    private void writeGeotiff(GeoTiffWriter geoTiffWriter,
            GridCoverage coverage) {
        GeoTiffFormat format = new GeoTiffFormat();

        GeoTiffWriteParams wp = new GeoTiffWriteParams();

        wp.setCompressionMode(GeoTiffWriteParams.MODE_EXPLICIT);
        wp.setCompressionType("LZW");
        wp.setTilingMode(GeoToolsWriteParams.MODE_EXPLICIT);
        int width = ((GridCoverage2D) coverage).getRenderedImage().getWidth();
        int tileWidth = 1024;
        if (width < 2048) {
            tileWidth = new Double(Math.sqrt(width)).intValue();
        }
        wp.setTiling(tileWidth, tileWidth);
        ParameterValueGroup paramWrite = format.getWriteParameters();
        paramWrite.parameter(AbstractGridFormat.GEOTOOLS_WRITE_PARAMS.getName().toString()).setValue(wp);
        JAI.getDefaultInstance().getTileCache().setMemoryCapacity(256 * 1024 * 1024);

        try {
            geoTiffWriter.write(coverage, (GeneralParameterValue[]) paramWrite.values().toArray(
                    new GeneralParameterValue[1]));
        } catch (IllegalArgumentException e1) {
            LOGGER.error(e1.getMessage(), e1);
            throw new RuntimeException(e1);
        } catch (IndexOutOfBoundsException e1) {
            LOGGER.error(e1.getMessage(), e1);
            throw new RuntimeException(e1);
        } catch (IOException e1) {
            LOGGER.error(e1.getMessage(), e1);
            throw new RuntimeException(e1);
        }
    }

}
