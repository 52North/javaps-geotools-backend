# javaps-jts-backend
Processing backend for javaPS containing parsers/generators and processes build upon the Java Topology Suite

### Quick Start
Use git to clone the repository:
``` git clone https://github.com/52North/javaps-geotools-backend ```
Then just run ``` mvn clean install ``` on the repositories root directory.

### Execute-request
In order to execute a JTSCoordinationTransformationAlgorithm, you must specify three inputs and one output:
* The input coordinates as valid GML references with ```id="data"```:

In this example, we choose the tasmania_roads dataset as a reference from [http://geoprocessing.demo.52north.org:8080/geoserver/wfs?SERVICE=WFS&VERSION=1.0.0&REQUEST=GetFeature&TYPENAME=topp:tasmania_roads&SRS=EPSG:4326&OUTPUTFORMAT=GML3](http://geoprocessing.demo.52north.org:8080/geoserver/wfs?SERVICE=WFS&VERSION=1.0.0&REQUEST=GetFeature&TYPENAME=topp:tasmania_roads&SRS=EPSG:4326&OUTPUTFORMAT=GML3).
```xml
<wps:Input id="data">
    <wps:Reference xlink:href="http://geoprocessing.demo.52north.org:8080/geoserver/wfs?SERVICE=WFS&amp;VERSION=1.0.0&amp;REQUEST=GetFeature&amp;TYPENAME=topp:tasmania_roads&amp;SRS=EPSG:4326&amp;OUTPUTFORMAT=GML3" mimeType="text/xml" schema="http://schemas.opengis.net/gml/3.1.1/base/feature.xsd" />
</wps:Input>
```

* The EPSG Code of the reference stystem of your source coordinates references with ```id="source_epsg"```:

In this example, we choose the epsg code 4326, because the referenced tasmania_roads dataset is specified in that coordinate reference system.
```xml
<wps:Input id="source_epsg">
    <wps:Data mimeType="text/plain">EPSG:4326</wps:Data>
</wps:Input>
```

* The EPSG Code of the reference stystem of your target coordinates references with ```id="target_epsg"```:

In this example, we choose the epsg code 3857, in which we want the data to be transformed to.
```xml
<wps:Input id="target_epsg">
    <wps:Data mimeType="text/plain">EPSG:3857</wps:Data>
</wps:Input>
```

* The output format in which we specify mimeType, schema, and transmission referenced with ```id="result"```:

In this example, we choose the default text/xml mimeType, gml 3.1.1 schema and value-transmission.
```xml
<!-- Uses default output format -->
<wps:Output id="result" mimeType="text/xml" schema="http://schemas.opengis.net/gml/3.1.1/base/feature.xsd" transmission="value"/>
```
    
The full execute-request for the example inputs and output then is:

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
        <wps:Reference xlink:href="http://geoprocessing.demo.52north.org:8080/geoserver/wfs?SERVICE=WFS&amp;VERSION=1.0.0&amp;REQUEST=GetFeature&amp;TYPENAME=topp:tasmania_roads&amp;SRS=EPSG:4326&amp;OUTPUTFORMAT=GML3" mimeType="text/xml" schema="http://schemas.opengis.net/gml/3.1.1/base/feature.xsd" />
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