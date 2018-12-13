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
package org.n52.geoprocessing.geotools.io.data.binding.complex;

import java.io.File;

import org.n52.javaps.io.complex.ComplexData;


public class GeotiffBinding implements ComplexData<File>{

    private static final long serialVersionUID = 1L;
    protected File geotiff;
    protected String mimeType;

    public GeotiffBinding(File geotiff){
        this.geotiff = geotiff;
        mimeType = "image/tiff";
    }

    @Override
    public File getPayload() {
        return geotiff;
    }

    @Override
    public Class<?> getSupportedClass() {
        return File.class;
    }

    public String getMimeType() {
        return mimeType;
    }
}
