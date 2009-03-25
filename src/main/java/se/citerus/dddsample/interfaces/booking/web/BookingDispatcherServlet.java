package se.citerus.dddsample.interfaces.booking.web;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class BookingDispatcherServlet extends DispatcherServlet {

  @Override
  protected WebApplicationContext findWebApplicationContext() {
    // The booking web application should be standalone,
    // and not use the main application context as parent.
    return null;
  }

}
