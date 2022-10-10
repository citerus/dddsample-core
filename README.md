# DDDSample
[![Build Status](https://travis-ci.org/citerus/dddsample-core.svg?branch=master)](https://travis-ci.org/citerus/dddsample-core)

This is the new home of the original DDD Sample app hosted at SourceForge. 

Our intention is to move everything from SourceForge to GitHub in due time while starting upgrading both the technical aspects as well as the DDD aspects of the DDD Sample.

This project is a joint effort by Eric Evans' company [Domain Language](https://www.domainlanguage.com/) and the [Swedish software consulting company Citerus](https://www.citerus.se/).

The application uses Spring Boot. To start it go to the root directory and type `mvn spring-boot:run` or run the `main` method of the `Application` class from your IDE.  
Then open http://localhost:8080/dddsample in your browser (and make sure that no firewall is blocking the communication and that Javascript for localhost is not blocked).

Discussion group: https://groups.google.com/forum/#!forum/dddsample

Development blog: https://citerus.github.io/dddsample-core/

Trello board: https://trello.com/b/PTDFRyxd

## Entity relationships

![](./dddsample.drawio.png)

The diagram was created with diagrams.net (formerly draw.io).

## Using the HandlingReportService ("regapp") web service

Using your favorite SOAP client, send an HTTP POST message to http://localhost:8080/dddsample/ws/RegisterEvent with the following body:

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ws="http://ws.handling.interfaces.dddsample.citerus.se/">
    <soapenv:Header/>
    <soapenv:Body>
        <ws:submitReport>
            <!--Optional:-->
            <arg0>
                <completionTime>2022-01-01T00:00:00</completionTime>
                <!--1 or more repetitions:-->
                <trackingIds>2</trackingIds>
                <type>LOAD</type>
                <unLocode>AA234</unLocode>
                <!--Optional:-->
                <voyageNumber>5</voyageNumber>
            </arg0>
        </ws:submitReport>
    </soapenv:Body>
</soapenv:Envelope>
```

You can also use cURL to send the request using an xml file for the body:

    curl --data-binary "@/path/to/project/src/test/resources/sampleSoapBody.xml" -H 'Content-Type: text/xml;charset=UTF-8' http://localhost:8080/dddsample/ws/RegisterEvent
