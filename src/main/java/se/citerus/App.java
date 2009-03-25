package se.citerus;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.citerus.registerapp.RegisterApp;

/**
 * The application main class
 *
 */
public class App {
  private static RegisterApp app;
  
  public static void main(String[] args) {
    app = getRegisterApp();
    
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
        public void run() {
            app.show();
        }
    });
  }

  private static RegisterApp getRegisterApp() {
    return (RegisterApp)getBean("registerApp");
  }

  public static Object getBean(String bean){
    ApplicationContext context = getContext(); 

    return context.getBean(bean);
}

public static ClassPathXmlApplicationContext getContext() {
    return new ClassPathXmlApplicationContext(new String[] {"context-app.xml"});
}
}
