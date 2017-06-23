# javaps-jts-backend
Processing backend for javaPS containing parsers/generators and processes build upon the Java Topology Suite

### Quick Start
Use git to clone the repository:
``` git clone https://github.com/52North/javaps-geotools-backend ```
Then just run ``` mvn clean install ``` on the repositories root directory.

### Execute-request
In order to execute a JTSCoordinationTransformationAlgorithm request, you must specify three inputs and one output:
* The input coordinates as valid GML being referenced with ```id="data"```:

In this example, we choose the tasmania_roads dataset as a reference from [http://geoprocessing.demo.52north.org:8080/geoserver/wfs?SERVICE=WFS&VERSION=1.0.0&REQUEST=GetFeature&TYPENAME=topp:tasmania_roads&SRS=EPSG:4326&OUTPUTFORMAT=GML3](http://geoprocessing.demo.52north.org:8080/geoserver/wfs?SERVICE=WFS&VERSION=1.0.0&REQUEST=GetFeature&TYPENAME=topp:tasmania_roads&SRS=EPSG:4326&OUTPUTFORMAT=GML3).
```xml
<wps:Input id="data">
    <wps:Reference xlink:href="http://geoprocessing.demo.52north.org:8080/geoserver/wfs?SERVICE=WFS&amp;VERSION=1.0.0&amp;REQUEST=GetFeature&amp;TYPENAME=topp:tasmania_roads&amp;SRS=EPSG:4326&amp;OUTPUTFORMAT=GML3" mimeType="text/xml" schema="http://schemas.opengis.net/gml/3.1.1/base/feature.xsd" />
</wps:Input>
```

* The EPSG Code of the reference stystem of your source coordinates being referenced with ```id="source_epsg"```:

In this example, we choose the epsg code 4326, because the referenced tasmania_roads dataset is specified by that coordinate reference system.
```xml
<wps:Input id="source_epsg">
    <wps:Data mimeType="text/plain">EPSG:4326</wps:Data>
</wps:Input>
```

* The EPSG Code of the reference stystem of your target coordinates being referenced with ```id="target_epsg"```:

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

### Execute-response
Send the full execute-request from the example above to the 52north geoprocessing service via a HTTP POST request.
The response to the example request from above is the following xml file:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<wps:Result xmlns:wps="http://www.opengis.net/wps/2.0" xmlns:ows="http://www.opengis.net/ows/2.0" xmlns:xlink="http://www.w3.org/1999/xlink">
    <wps:JobID>a19752c3-6a5f-4cd3-acd1-41c6fc9f96fe</wps:JobID>
    <wps:ExpirationDate>2017-06-23T11:05:25.602905Z</wps:ExpirationDate>
    <wps:Output id="result">
        <wps:Data mimeType="text/xml" encoding="UTF-8" schema="http://schemas.opengis.net/gml/3.1.1/base/feature.xsd">
            <gml:FeatureCollection 
             xmlns:xs="http://www.w3.org/2001/XMLSchema" 
             xmlns:gml="http://www.opengis.net/gml" 
             xmlns:n52="http://www.52north.org/d28e6071-e877-470a-a908-f1d6475306ca" 
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
             xsi:schemaLocation="http://www.opengis.net/gml http://schemas.opengis.net/gml/3.1.1/base/feature.xsd http://www.52north.org/d28e6071-e877-470a-a908-f1d6475306ca">
                <gml:featureMembers>
                    <n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca gml:id="ID0">
                        <gml:boundedBy>
                            <gml:Envelope srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="2">
                                <gml:lowerCorner>1.6304807965452837E7 -5068588.90369088</gml:lowerCorner>
                                <gml:upperCorner>1.6374912639294297E7 -5048025.125907094</gml:upperCorner>
                            </gml:Envelope>
                        </gml:boundedBy>
                        <n52:GEOMETRY>
                            <gml:MultiCurve srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="3">
                                <gml:curveMember>
                                    <gml:LineString srsDimension="3">
                                        <gml:posList>1.6304807965452837E7 -5048025.125907094 -41.241478 1.6316628536902208E7 -5049462.438672063 -41.251186 1.6323935882236348E7 -5050049.980285645 -41.255154 1.63379307459799E7 -5061487.2029636325 -41.332348 1.6341054370891564E7 -5063239.971036594 -41.34417 1.6344169646841412E7 -5066030.645306121 -41.362988 1.6348762689031536E7 -5068588.90369088 -41.380234 1.6352779875495795E7 -5068472.887731121 -41.379452 1.635611756778825E7 -5068291.15197939 -41.378227 1.636486026663668E7 -5065005.959777551 -41.356079 1.6374912639294297E7 -5066020.411261094 -41.362919</gml:posList>
                                    </gml:LineString>
                                </gml:curveMember>
                            </gml:MultiCurve>
                        </n52:GEOMETRY>
                        <n52:TYPE>alley</n52:TYPE>
                    </n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca>
                    <n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca gml:id="ID1">
                        <gml:boundedBy>
                            <gml:Envelope srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="2">
                                <gml:lowerCorner>1.6374912639294297E7 -5106888.727930117</gml:lowerCorner>
                                <gml:upperCorner>1.639742589311233E7 -5066020.411261094</gml:upperCorner>
                            </gml:Envelope>
                        </gml:boundedBy>
                        <n52:GEOMETRY>
                            <gml:MultiCurve srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="3">
                                <gml:curveMember>
                                    <gml:LineString srsDimension="3">
                                        <gml:posList>1.6374912639294297E7 -5066020.411261094 -41.362919 1.6383228984492987E7 -5079357.46148951 -41.452778 1.6387772712148696E7 -5086934.457707389 -41.503773 1.639011342708161E7 -5093311.511371634 -41.546661 1.6391920699014638E7 -5097352.886978007 -41.573826 1.6393427408322524E7 -5101616.732366054 -41.602474 1.6395633871949542E7 -5103861.935332215 -41.617554 1.639742589311233E7 -5106888.727930117 -41.637878</gml:posList>
                                    </gml:LineString>
                                </gml:curveMember>
                            </gml:MultiCurve>
                        </n52:GEOMETRY>
                        <n52:TYPE>highway</n52:TYPE>
                    </n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca>
                    <n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca gml:id="ID2">
                        <gml:boundedBy>
                            <gml:Envelope srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="2">
                                <gml:lowerCorner>1.6304807965452837E7 -5106888.727930117</gml:lowerCorner>
                                <gml:upperCorner>1.639742589311233E7 -5048025.125907094</gml:upperCorner>
                            </gml:Envelope>
                        </gml:boundedBy>
                        <n52:GEOMETRY>
                            <gml:MultiCurve srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="3">
                                <gml:curveMember>
                                    <gml:LineString srsDimension="3">
                                        <gml:posList>1.639742589311233E7 -5106888.727930117 -41.637878 1.63891027574247E7 -5105259.347844445 -41.626938 1.6384372124343947E7 -5104112.3943562955 -41.619236 1.6373134199109383E7 -5097937.55054839 -41.577755 1.6367452340979801E7 -5096070.144540123 -41.565205 1.635967789906229E7 -5095918.536022578 -41.564186 1.6355565534433411E7 -5096570.0678783925 -41.568565 1.6350198042545829E7 -5096726.150460599 -41.569614 1.633988918978143E7 -5095404.364389802 -41.56073 1.6328892493883412E7 -5091760.425347366 -41.536232 1.6321024543593636E7 -5083127.0129577825 -41.478153 1.6321663183512319E7 -5075077.930317983 -41.423958 1.6317498276083779E7 -5066400.561406882 -41.365482 1.6311063898196436E7 -5056012.703776206 -41.29541 1.6305799933435291E7 -5053377.489642381 -41.277622 1.6304807965452837E7 -5048025.125907094 -41.241478</gml:posList>
                                    </gml:LineString>
                                </gml:curveMember>
                            </gml:MultiCurve>
                        </n52:GEOMETRY>
                        <n52:TYPE>lane</n52:TYPE>
                    </n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca>
                    <n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca gml:id="ID3">
                        <gml:boundedBy>
                            <gml:Envelope srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="2">
                                <gml:lowerCorner>1.6416137697639262E7 -5194986.701403551</gml:lowerCorner>
                                <gml:upperCorner>1.6430458616171345E7 -5140019.330079663</gml:upperCorner>
                            </gml:Envelope>
                        </gml:boundedBy>
                        <n52:GEOMETRY>
                            <gml:MultiCurve srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="3">
                                <gml:curveMember>
                                    <gml:LineString srsDimension="3">
                                        <gml:posList>1.6422101416719528E7 -5140019.330079663 -41.859921 1.6425398477397848E7 -5150175.449573266 -41.927834 1.6430458616171345E7 -5163588.933173483 -42.017418 1.6428379502041796E7 -5177953.767596976 -42.113216 1.6424262016716335E7 -5193652.300655509 -42.217743 1.6416137697639262E7 -5194986.701403551 -42.22662</gml:posList>
                                    </gml:LineString>
                                </gml:curveMember>
                            </gml:MultiCurve>
                        </n52:GEOMETRY>
                        <n52:TYPE>highway</n52:TYPE>
                    </n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca>
                    <n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca gml:id="ID4">
                        <gml:boundedBy>
                            <gml:Envelope srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="2">
                                <gml:lowerCorner>1.6264189375693712E7 -5048603.400980323</gml:lowerCorner>
                                <gml:upperCorner>1.6304807965452837E7 -5037697.055675731</gml:upperCorner>
                            </gml:Envelope>
                        </gml:boundedBy>
                        <n52:GEOMETRY>
                            <gml:MultiCurve srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="3">
                                <gml:curveMember>
                                    <gml:LineString srsDimension="3">
                                        <gml:posList>1.6264189375693712E7 -5037697.055675731 -41.171677 1.6286444368293107E7 -5047392.112739929 -41.237202 1.6292968692329008E7 -5047255.4785219515 -41.236279 1.6296525572698835E7 -5048603.400980323 -41.245384 1.630204100818968E7 -5048444.097997191 -41.244308 1.6304807965452837E7 -5048025.125907094 -41.241478</gml:posList>
                                    </gml:LineString>
                                </gml:curveMember>
                            </gml:MultiCurve>
                        </n52:GEOMETRY>
                        <n52:TYPE>gravel</n52:TYPE>
                    </n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca>
                    <n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca gml:id="ID5">
                        <gml:boundedBy>
                            <gml:Envelope srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="2">
                                <gml:lowerCorner>1.6236617652894545E7 -5037697.055675731</gml:lowerCorner>
                                <gml:upperCorner>1.6264189375693712E7 -5024159.153904384</gml:upperCorner>
                            </gml:Envelope>
                        </gml:boundedBy>
                        <n52:GEOMETRY>
                            <gml:MultiCurve srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="3">
                                <gml:curveMember>
                                    <gml:LineString srsDimension="3">
                                        <gml:posList>1.6236617652894545E7 -5024159.153904384 -41.08007 1.6246505161386294E7 -5030042.413578626 -41.119896 1.6256875128551144E7 -5034500.591441609 -41.150059 1.6264189375693712E7 -5037697.055675731 -41.171677</gml:posList>
                                    </gml:LineString>
                                </gml:curveMember>
                            </gml:MultiCurve>
                        </n52:GEOMETRY>
                        <n52:TYPE>road</n52:TYPE>
                    </n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca>
                    <n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca gml:id="ID6">
                        <gml:boundedBy>
                            <gml:Envelope srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="2">
                                <gml:lowerCorner>1.6391728784212513E7 -5278301.601744744</gml:lowerCorner>
                                <gml:upperCorner>1.6416835782166027E7 -5194986.701403551</gml:upperCorner>
                            </gml:Envelope>
                        </gml:boundedBy>
                        <n52:GEOMETRY>
                            <gml:MultiCurve srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="3">
                                <gml:curveMember>
                                    <gml:LineString srsDimension="3">
                                        <gml:posList>1.6416137697639262E7 -5194986.701403551 -42.22662 1.6416835782166027E7 -5204859.459952936 -42.292259 1.6416028938496755E7 -5206218.918040663 -42.301292 1.6414262409497356E7 -5212296.046777879 -42.341656 1.6411225279830048E7 -5217880.222581283 -42.378723 1.6404709527394934E7 -5222979.468536159 -42.412552 1.6402457088818222E7 -5225979.946053017 -42.432449 1.6396172324326511E7 -5232622.476364691 -42.476475 1.6393410376440438E7 -5236762.501634497 -42.503899 1.6392899086019224E7 -5243354.298461642 -42.547539 1.6391728784212513E7 -5253402.961498721 -42.614006 1.6394951038193012E7 -5265397.160539233 -42.693249 1.6395610049578508E7 -5275172.681473565 -42.757759 1.6392545757955445E7 -5278301.601744744 -42.778393</gml:posList>
                                    </gml:LineString>
                                </gml:curveMember>
                            </gml:MultiCurve>
                        </n52:GEOMETRY>
                        <n52:TYPE>highway</n52:TYPE>
                    </n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca>
                    <n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca gml:id="ID7">
                        <gml:boundedBy>
                            <gml:Envelope srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="2">
                                <gml:lowerCorner>1.6481199153269827E7 -5176055.217120337</gml:lowerCorner>
                                <gml:upperCorner>1.6503031243123693E7 -5140158.335000565</gml:upperCorner>
                            </gml:Envelope>
                        </gml:boundedBy>
                        <n52:GEOMETRY>
                            <gml:MultiCurve srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="3">
                                <gml:curveMember>
                                    <gml:LineString srsDimension="3">
                                        <gml:posList>1.6503031243123693E7 -5140158.335000565 -41.860851 1.65013819335481E7 -5146278.3490988845 -41.901783 1.6496671671934161E7 -5151578.442121213 -41.93721 1.6492623983929427E7 -5154041.510128005 -41.953667 1.6489503587283002E7 -5160161.148268007 -41.994537 1.6481199153269827E7 -5176055.217120337 -42.100563</gml:posList>
                                    </gml:LineString>
                                </gml:curveMember>
                            </gml:MultiCurve>
                        </n52:GEOMETRY>
                        <n52:TYPE>road</n52:TYPE>
                    </n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca>
                    <n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca gml:id="ID8">
                        <gml:boundedBy>
                            <gml:Envelope srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="2">
                                <gml:lowerCorner>1.6163316217235973E7 -5024159.153904384</gml:lowerCorner>
                                <gml:upperCorner>1.6236617652894545E7 -4990654.176671652</gml:upperCorner>
                            </gml:Envelope>
                        </gml:boundedBy>
                        <n52:GEOMETRY>
                            <gml:MultiCurve srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="3">
                                <gml:curveMember>
                                    <gml:LineString srsDimension="3">
                                        <gml:posList>1.6163316217235973E7 -4994410.866347665 -40.878323 1.6168785789096605E7 -4991744.48471761 -40.86021 1.6173974947159939E7 -4990654.176671652 -40.852802 1.6193114775128966E7 -4997288.4268091805 -40.897865 1.6201271488177866E7 -5002945.158925296 -40.936264 1.6203004064732568E7 -5003377.957138659 -40.939201 1.6208352966265185E7 -5006876.26846664 -40.962936 1.621327885373279E7 -5009750.438174789 -40.98243 1.6217450662969757E7 -5010849.523163927 -40.989883 1.6220428348028984E7 -5011781.328362548 -40.996201 1.6224180482785668E7 -5013454.613330365 -41.007545 1.623059949858328E7 -5018506.45403252 -41.041782 1.6236617652894545E7 -5024159.153904384 -41.08007</gml:posList>
                                    </gml:LineString>
                                </gml:curveMember>
                            </gml:MultiCurve>
                        </n52:GEOMETRY>
                        <n52:TYPE>logging</n52:TYPE>
                    </n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca>
                    <n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca gml:id="ID9">
                        <gml:boundedBy>
                            <gml:Envelope srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="2">
                                <gml:lowerCorner>1.6357343974618321E7 -5376658.4765299605</gml:lowerCorner>
                                <gml:upperCorner>1.6404040274616286E7 -5299792.306850674</gml:upperCorner>
                            </gml:Envelope>
                        </gml:boundedBy>
                        <n52:GEOMETRY>
                            <gml:MultiCurve srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="3">
                                <gml:curveMember>
                                    <gml:LineString srsDimension="3">
                                        <gml:posList>1.6404040274616286E7 -5299792.306850674 -42.91993 1.6402795166111764E7 -5302427.049670052 -42.93726 1.6395696656142348E7 -5308780.087133399 -42.979027 1.6388554063654577E7 -5311344.153158834 -42.995876 1.6382269299162861E7 -5316083.034754662 -43.027004 1.6371561254704477E7 -5321594.960503398 -43.06319 1.6360027776221856E7 -5329713.097117361 -43.116447 1.6358349523578655E7 -5337889.586832058 -43.17004 1.6358809940992575E7 -5343928.341489079 -43.209591 1.635997178251799E7 -5346212.57910252 -43.224545 1.636126264333323E7 -5350176.1151726805 -43.250484 1.6361823248288857E7 -5353174.604043465 -43.2701 1.6362028744068865E7 -5355868.197944154 -43.287716 1.6360722521163894E7 -5360333.856924994 -43.31691 1.6357343974618321E7 -5363579.589418471 -43.33812 1.6357625946888503E7 -5367275.534314489 -43.362263 1.6358643407034354E7 -5371949.351791806 -43.39278 1.6359003525587065E7 -5376658.4765299605 -43.423512</gml:posList>
                                    </gml:LineString>
                                </gml:curveMember>
                            </gml:MultiCurve>
                        </n52:GEOMETRY>
                        <n52:TYPE>road</n52:TYPE>
                    </n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca>
                    <n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca gml:id="ID10">
                        <gml:boundedBy>
                            <gml:Envelope srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="2">
                                <gml:lowerCorner>1.639742589311233E7 -5140019.330079663</gml:lowerCorner>
                                <gml:upperCorner>1.6422176223417342E7 -5106888.727930117</gml:upperCorner>
                            </gml:Envelope>
                        </gml:boundedBy>
                        <n52:GEOMETRY>
                            <gml:MultiCurve srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="3">
                                <gml:curveMember>
                                    <gml:LineString srsDimension="3">
                                        <gml:posList>1.639742589311233E7 -5106888.727930117 -41.637878 1.6405376999061726E7 -5115475.840969772 -41.695503 1.6408781037770694E7 -5119959.990467483 -41.725574 1.6413397791012365E7 -5123555.565626415 -41.749676 1.6418559898439432E7 -5128451.787491415 -41.782482 1.6420389211631639E7 -5130413.904194111 -41.795624 1.6422176223417342E7 -5136386.189317281 -41.835609 1.6422101416719528E7 -5140019.330079663 -41.859921</gml:posList>
                                    </gml:LineString>
                                </gml:curveMember>
                            </gml:MultiCurve>
                        </n52:GEOMETRY>
                        <n52:TYPE>highway</n52:TYPE>
                    </n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca>
                    <n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca gml:id="ID11">
                        <gml:boundedBy>
                            <gml:Envelope srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="2">
                                <gml:lowerCorner>1.6410364112249268E7 -5297192.954098973</gml:lowerCorner>
                                <gml:upperCorner>1.6481199153269827E7 -5176055.217120337</gml:upperCorner>
                            </gml:Envelope>
                        </gml:boundedBy>
                        <n52:GEOMETRY>
                            <gml:MultiCurve srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="3">
                                <gml:curveMember>
                                    <gml:LineString srsDimension="3">
                                        <gml:posList>1.6481199153269827E7 -5176055.217120337 -42.100563 1.6478427075310094E7 -5189225.634297746 -42.188286 1.64755359968147E7 -5195419.666594841 -42.2295 1.6471940043303607E7 -5199166.392278286 -42.254417 1.6470864919661527E7 -5203751.630007192 -42.284897 1.6468908145652361E7 -5220909.0804801695 -42.398819 1.6467092302118538E7 -5234065.331177443 -42.486034 1.6461379942448482E7 -5242000.973127276 -42.538582 1.6456583074270708E7 -5249364.0366080925 -42.587299 1.644681100409091E7 -5256065.724325191 -42.631607 1.644114272693921E7 -5259767.399907322 -42.656067 1.6432782299222164E7 -5265076.987531285 -42.691135 1.6428009142095927E7 -5272949.2249253625 -42.743092 1.6428340428900527E7 -5276958.861878653 -42.769539 1.6424951752281288E7 -5283750.81332762 -42.814312 1.6420370621276677E7 -5288091.176198264 -42.842907 1.641832378979946E7 -5293274.921065399 -42.877041 1.6414024631065026E7 -5296923.363070135 -42.901054 1.6410364112249268E7 -5297192.954098973 -42.902828</gml:posList>
                                    </gml:LineString>
                                </gml:curveMember>
                            </gml:MultiCurve>
                        </n52:GEOMETRY>
                        <n52:TYPE>road</n52:TYPE>
                    </n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca>
                    <n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca gml:id="ID12">
                        <gml:boundedBy>
                            <gml:Envelope srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="2">
                                <gml:lowerCorner>1.6353393023251088E7 -5140158.335000565</gml:lowerCorner>
                                <gml:upperCorner>1.6505672632001238E7 -5020239.650037619</gml:upperCorner>
                            </gml:Envelope>
                        </gml:boundedBy>
                        <n52:GEOMETRY>
                            <gml:MultiCurve srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="3">
                                <gml:curveMember>
                                    <gml:LineString srsDimension="3">
                                        <gml:posList>1.6374912639294297E7 -5066020.411261094 -41.362919 1.6371250450686183E7 -5058467.678520346 -41.311977 1.6366645497310532E7 -5050402.403921483 -41.257534 1.6361899613459546E7 -5043571.966826467 -41.211391 1.6358201802614372E7 -5039163.8963764515 -41.181595 1.6355813554258896E7 -5037818.914199631 -41.172501 1.6353393023251088E7 -5034069.656586101 -41.147144 1.6357371136574075E7 -5025013.811750645 -41.085857 1.635980869946398E7 -5023424.624764602 -41.075096 1.6366312652033065E7 -5024285.420543378 -41.080925 1.6375011157043647E7 -5030642.817180919 -41.123959 1.638484946232047E7 -5034580.127996435 -41.150597 1.6395360359960662E7 -5027729.810554961 -41.104244 1.6396883989831144E7 -5023528.288355362 -41.075798 1.6398095145890981E7 -5021613.791540552 -41.062832 1.6400226914139668E7 -5020239.650037619 -41.053524 1.640437311989376E7 -5024213.943086352 -41.080441 1.6410610462282393E7 -5024409.325894696 -41.081764 1.6415826782301476E7 -5021327.077721569 -41.06089 1.6421773580819143E7 -5026038.259777344 -41.092793 1.6422808072847085E7 -5032583.337035955 -41.137089 1.6425471502983809E7 -5040934.517226831 -41.193565 1.6430113748388864E7 -5046899.616874128 -41.233875 1.6445718848566735E7 -5047790.183773719 -41.239891 1.6456290860607378E7 -5041388.836464127 -41.196636 1.6462217287658228E7 -5036443.0667002965 -41.163197 1.6465407258986402E7 -5036430.054457904 -41.163109 1.647363354671704E7 -5045752.9254596885 -41.226128 1.6477751032042505E7 -5055596.218664525 -41.292599 1.6483646846233387E7 -5058754.901085456 -41.313915 1.6497560112790188E7 -5060115.841522486 -41.323097 1.6501577299254443E7 -5062509.735896235 -41.339245 1.6505672632001238E7 -5069071.675619029 -41.383488 1.6503114510102805E7 -5080003.862626619 -41.45713 1.6503603759264842E7 -5092233.056954789 -41.53941 1.6504498879290309E7 -5099048.0479614595 -41.585217 1.6503031243123693E7 -5140158.335000565 -41.860851</gml:posList>
                                    </gml:LineString>
                                </gml:curveMember>
                            </gml:MultiCurve>
                        </n52:GEOMETRY>
                        <n52:TYPE>road</n52:TYPE>
                    </n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca>
                    <n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca gml:id="ID13">
                        <gml:boundedBy>
                            <gml:Envelope srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="2">
                                <gml:lowerCorner>1.6191725396564377E7 -5285338.884544602</gml:lowerCorner>
                                <gml:upperCorner>1.6392545757955445E7 -5024159.153904384</gml:upperCorner>
                            </gml:Envelope>
                        </gml:boundedBy>
                        <n52:GEOMETRY>
                            <gml:MultiCurve srsName="http://www.opengis.net/gml/srs/epsg.xml#3857" srsDimension="3">
                                <gml:curveMember>
                                    <gml:LineString srsDimension="3">
                                        <gml:posList>1.6392545757955445E7 -5278301.601744744 -42.778393 1.6388475917372042E7 -5285338.884544602 -42.824776 1.6383957681879725E7 -5284831.048902796 -42.82143 1.6376358122882247E7 -5280931.525913695 -42.795731 1.6370321266896531E7 -5272720.193463359 -42.741581 1.6364352427119687E7 -5267147.248556957 -42.704803 1.635495917716706E7 -5254723.280676807 -42.622734 1.6354006282325866E7 -5252937.384365193 -42.610928 1.6351718221512105E7 -5249076.3101732675 -42.585396 1.6345980369678652E7 -5247170.854516954 -42.572792 1.6340108266539307E7 -5242117.305848929 -42.539352 1.6333278259181688E7 -5234055.066341234 -42.485966 1.6330015262267552E7 -5231582.169751429 -42.469582 1.6324989075938748E7 -5228683.4080882585 -42.450371 1.6319990051565692E7 -5225953.551814495 -42.432274 1.631707526201876E7 -5222373.215364291 -42.408531 1.6312681036439192E7 -5215693.13275948 -42.364208 1.6311094511056405E7 -5207353.529311724 -42.30883 1.6314766829738181E7 -5202405.163433019 -42.275948 1.6316793289748585E7 -5196663.0479861 -42.23777 1.6317374266171033E7 -5191500.548079229 -42.203426 1.6307192762904098E7 -5188014.172090539 -42.180222 1.6294913666472148E7 -5182924.534099642 -42.146332 1.6289833156231834E7 -5181784.882810946 -42.138741 1.628280945296023E7 -5185833.358673746 -42.165703 1.6274608546073493E7 -5194603.663944963 -42.224072 1.627133708887806E7 -5197725.38860643 -42.244835 1.6271005802073458E7 -5197775.914775422 -42.245171 1.6265004679644283E7 -5200788.572852664 -42.265202 1.6256817465054912E7 -5196958.9576676 -42.239738 1.6250600494133087E7 -5189160.279900785 -42.187851 1.623638321404693E7 -5180996.920335397 -42.133492 1.6232564844193233E7 -5180345.763443547 -42.129154 1.6221534084531039E7 -5176583.496167408 -42.104084 1.6210185730361609E7 -5169375.115417293 -42.056023 1.6201629936938219E7 -5165060.88757214 -42.027241 1.6195458829646604E7 -5158482.086858856 -41.983326 1.6191725396564377E7 -5149982.434526348 -41.926544 1.6196321778339231E7 -5145484.792725063 -41.896477 1.6207197915228719E7 -5140063.123885002 -41.860214 1.621280663645284E7 -5136802.902272027 -41.838398 1.621584888681673E7 -5135657.844380015 -41.830734 1.6217126166654097E7 -5130433.166045939 -41.795753 1.6217353815012768E7 -5122592.466020949 -41.743221 1.621648418715069E7 -5117693.570870572 -41.710377 1.6217036220505534E7 -5114492.683272377 -41.688908 1.6219368363837657E7 -5108430.489316945 -41.648228 1.6220897114404714E7 -5102664.081039763 -41.609509 1.621136794335383E7 -5080734.829400134 -41.462051 1.6213560826002967E7 -5081965.759599936 -41.470337 1.6211838490841415E7 -5074624.251048048 -41.420902 1.621166516639425E7 -5066370.599751565 -41.36528 1.62126657059775E7 -5056919.9656013725 -41.301533 1.6219297008044055E7 -5048192.860611922 -41.242611 1.6227312679298114E7 -5040983.631792514 -41.193897 1.6230642022628764E7 -5036190.3661636505 -41.161488 1.6236617652894545E7 -5024159.153904384 -41.08007</gml:posList>
                                    </gml:LineString>
                                </gml:curveMember>
                            </gml:MultiCurve>
                        </n52:GEOMETRY>
                        <n52:TYPE>road</n52:TYPE>
                    </n52:Feature-d28e6071-e877-470a-a908-f1d6475306ca>
                </gml:featureMembers>
            </gml:FeatureCollection>
        </wps:Data>
    </wps:Output>
</wps:Result>
```