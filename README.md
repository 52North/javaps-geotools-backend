# javaps-jts-backend
Processing backend for javaPS containing parsers/generators and processes build upon the Java Topology Suite

### Quick Start
Use git to clone the repository:
``` git clone https://github.com/52North/javaps-geotools-backend ```
Then just run ``` mvn clean install ``` on the repositories root directory.

### Execute-request
In order to execute a JTSCoordinationTransformationAlgorithm, you can 
```xml 
<?xml version="1.0" encoding="UTF-8"?>
<wps:Execute
  xmlns:wps="http://www.opengis.net/wps/2.0"
  xmlns:ows="http://www.opengis.net/ows/2.0"
  xmlns:xlink="http://www.w3.org/1999/xlink"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"	
  xsi:schemaLocation="http://www.opengis.net/wps/2.0 http://schemas.opengis.net/wps/2.0/wpsExecute.xsd"
  service="WPS" version="2.0.0" response="document" mode="sync">
    <ows:Identifier>org.n52.geoprocessing.jts.algorithm.JTSCoordinationTransformationAlgorithm</ows:Identifier>
    <wps:Input id="data">
    <wps:Data mimeType="text/xml" schema="http://schemas.opengis.net/gml/3.1.1/base/feature.xsd">
        
    </wps:Data>
    </wps:Input>
    <wps:Input id="source_epsg">
        <wps:Data mimeType="text/plain">EPSG:4326</wps:Data>
    </wps:Input>
    <wps:Input id="target_epsg">
        <wps:Data mimeType="text/plain">EPSG:3857</wps:Data>
    </wps:Input>
    <!-- Uses default output format -->
    <wps:Output id="result" mimeType="text/xml" schema="http://schemas.opengis.net/gml/3.1.1/base/feature.xsd" transmission="value"/>
</wps:Execute>
``` 