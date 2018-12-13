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
package org.n52.geoprocessing.geotools.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.http.HttpException;
import org.n52.geoprocessing.geotools.io.datahandler.generator.GeoServerUploader;

public class GeoServerUpload {

    public static void main(String[] args) throws MalformedURLException {

        GeoServerUploader geoServerUploader = new GeoServerUploader();

        String fileName = "elev_ned_30m.tif";

//        File tiff = new File(GeoServerUpload.class.getResource(fileName).getFile());
        File tiff = new File("D:\\dev\\GitHub4w\\WPS\\52n-wps-io-geotools\\src\\test\\resources\\6_UTM2GTIF.TIF");

        try {
            String response = geoServerUploader.uploadGeotiff(tiff, "N52");

            System.out.println(response);
        } catch (HttpException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
