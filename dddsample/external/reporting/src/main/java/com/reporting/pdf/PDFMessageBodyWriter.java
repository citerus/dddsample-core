package com.reporting.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public abstract class PDFMessageBodyWriter<T> implements MessageBodyWriter<T> {

  public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mt) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    // TODO creates document twice, not optimal, but this class must be stateless.
    // Could probably be worked around using a ThreadLocal, but it's beyond the scope of the sample app.
    writeDocumentToStream(t, baos);
    return baos.toByteArray().length;
  }

  public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mt) {
    return getSupportedClass().isAssignableFrom(type);
  }

  public void writeTo(T t, Class<?> clazz, Type type, Annotation[] a, MediaType mt, MultivaluedMap<String, Object> headers, OutputStream os) throws IOException {
    writeDocumentToStream(t, os);
  }

  protected void writeDocumentToStream(T t, OutputStream os) {
    Document document = new Document();
    try {
      PdfWriter.getInstance(document, os);
    } catch (DocumentException e) {
      throw new RuntimeException(e);
    }
    document.open();
    buildDocument(t, document);
    document.close();
  }

  protected abstract void buildDocument(T t, Document document);

  protected abstract Class<T> getSupportedClass();

}
