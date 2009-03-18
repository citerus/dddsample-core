
package com.aggregator;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.aggregator package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _SubmitReport_QNAME = new QName("http://ws.handling.interfaces.dddsample.citerus.se/", "submitReport");
    private final static QName _HandlingReportErrors_QNAME = new QName("http://ws.handling.interfaces.dddsample.citerus.se/", "HandlingReportErrors");
    private final static QName _SubmitReportResponse_QNAME = new QName("http://ws.handling.interfaces.dddsample.citerus.se/", "submitReportResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.aggregator
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link HandlingReportErrors }
     * 
     */
    public HandlingReportErrors createHandlingReportErrors() {
        return new HandlingReportErrors();
    }

    /**
     * Create an instance of {@link SubmitReportResponse }
     * 
     */
    public SubmitReportResponse createSubmitReportResponse() {
        return new SubmitReportResponse();
    }

    /**
     * Create an instance of {@link HandlingReport }
     * 
     */
    public HandlingReport createHandlingReport() {
        return new HandlingReport();
    }

    /**
     * Create an instance of {@link SubmitReport }
     * 
     */
    public SubmitReport createSubmitReport() {
        return new SubmitReport();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubmitReport }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.handling.interfaces.dddsample.citerus.se/", name = "submitReport")
    public JAXBElement<SubmitReport> createSubmitReport(SubmitReport value) {
        return new JAXBElement<SubmitReport>(_SubmitReport_QNAME, SubmitReport.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HandlingReportErrors }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.handling.interfaces.dddsample.citerus.se/", name = "HandlingReportErrors")
    public JAXBElement<HandlingReportErrors> createHandlingReportErrors(HandlingReportErrors value) {
        return new JAXBElement<HandlingReportErrors>(_HandlingReportErrors_QNAME, HandlingReportErrors.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubmitReportResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.handling.interfaces.dddsample.citerus.se/", name = "submitReportResponse")
    public JAXBElement<SubmitReportResponse> createSubmitReportResponse(SubmitReportResponse value) {
        return new JAXBElement<SubmitReportResponse>(_SubmitReportResponse_QNAME, SubmitReportResponse.class, null, value);
    }

}
