package se.citerus.registerapp;

import com.aggregator.HandlingReport;
import com.aggregator.HandlingReportErrors_Exception;
import com.aggregator.HandlingReportService;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.debug.FormDebugPanel;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import se.citerus.registerapp.validation.FormValidationDecorator;
import se.citerus.registerapp.validation.FormValidationSwingDecorator;
import se.citerus.registerapp.validation.MandatoryTextFieldValidator;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.text.DefaultFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class RegisterApp {
  private static final DefaultFormatter DEFAULT_FORMATTER = new DefaultFormatter();
  private static final SimpleDateFormat ISO8601_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
  private static final String TITLE = "Incident Logging Application";
  private JFrame frame;
  private JFormattedTextField completionTimeField;
  private JFormattedTextField trackingIdField;
  private JFormattedTextField carrierMovementField;
  private JFormattedTextField locationField;
  //private JFormattedTextField eventTypeField;
  private JComboBox eventTypeField;
  private JButton registerButton;

  private HandlingReportService handlingReportService;
  private boolean debugUI;
  private FormValidationDecorator validator;


    /////////////////////////////////////////////////////////////////////////////
  // GUI EVENT HANDLING
  /////////////////////////////////////////////////////////////////////////////
  protected void onRegister(){
    assert handlingReportService != null : "No HandlingEventService available";

        try {
            final HandlingReport report = new HandlingReport();
            final GregorianCalendar completionTime = new GregorianCalendar();
            completionTime.setTime((Date) completionTimeField.getValue());
            report.setCompletionTime(new XMLGregorianCalendarImpl(completionTime));
            report.getTrackingIds().add(getStringValue(trackingIdField));
            report.setType(getStringValue(eventTypeField).toUpperCase());
            report.setUnLocode(getStringValue(locationField));
            report.setVoyageNumber(getStringValue(carrierMovementField));

            handlingReportService.submitReport(report);
            clearForm();
        } catch (HandlingReportErrors_Exception e) {
            e.printStackTrace();
        }
    }

  private String getStringValue(JComboBox comboBox) {
      Object value = comboBox.getSelectedItem();

      if (value != null) {
        return value.toString();
      } else {
        return null;
      }
  }


  private String getStringValue(JFormattedTextField formattedTextField) {
    Object value = formattedTextField.getValue();

    if (value != null) {
      return value.toString();
    } else {
      return null;
    }
  }


  /////////////////////////////////////////////////////////////////////////////
  // PUBLIC API
  /////////////////////////////////////////////////////////////////////////////
  public void show() {
    try {
      UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
    } catch (Exception e) {
      // Likely PlasticXP is not in the class path; ignore.
    }

    initComponents();
    JComponent panel = createMainPanel();


    frame = createFrame(panel);
    frame.pack();
    centerOnScreen(frame);

    initValidation(frame);

    frame.setVisible(true);

    clearForm();
  }


  public void setHandlingReportService(HandlingReportService handlingReportService) {
    this.handlingReportService = handlingReportService;
  }

  public void setDebugUI(boolean on){
    this.debugUI = on;
  }

  /////////////////////////////////////////////////////////////////////////////
  // GUI SETUP
  /////////////////////////////////////////////////////////////////////////////
  private void clearForm() {
    completionTimeField.setText(ISO8601_DATE_FORMAT.format(new Date()));
    trackingIdField.setText("");
    carrierMovementField.setText("");
    locationField.setText("");
    eventTypeField.setSelectedIndex(0);
  }

  public static Border getMandatoryBorder() {
    return new CompoundBorder(new LineBorder(Color.RED), new BasicBorders.MarginBorder());
  }

  private void initComponents(){
    completionTimeField = new JFormattedTextField(ISO8601_DATE_FORMAT);
    trackingIdField = new JFormattedTextField(DEFAULT_FORMATTER);
    carrierMovementField = new JFormattedTextField(DEFAULT_FORMATTER);
    locationField = new JFormattedTextField(DEFAULT_FORMATTER);
    eventTypeField = new JComboBox(new String[] {"-- Select --","Receive","Load","Unload","Customs","Claim"});

    registerButton = new JButton("Register");
    registerButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        if (validator.validate()){
          onRegister();
        }
      }
    });
  }

  /**
   * We need to do initialization of validators after the components and their parent frame has been initialized.
   * @param frame
   *
   */
  private void initValidation(JFrame frame){
    validator = new FormValidationSwingDecorator(frame);

    validator.add(trackingIdField, new MandatoryTextFieldValidator("Tracking id can't be empty"));
    validator.add(locationField, new MandatoryTextFieldValidator("Location can't be empty"));
  }


  /**
   * Center a component on the screen.
   *
   * @param component the component to be centered
   */

  public static void centerOnScreen(Component component) {
      Dimension componentSize = component.getSize();
      Dimension screenSize = component.getToolkit().getScreenSize();

      component.setLocation(
          (screenSize.width  - componentSize.width)  / 2,
          (int) ((screenSize.height - componentSize.height) *0.45));

  }

  private JFrame createFrame(JComponent panel) {
    JFrame f = new JFrame();
    f.setTitle(TITLE);
    f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    f.getContentPane().add(panel);

    return f;
  }

  private JComponent createMainPanel() {
    JComponent fieldPanel = createFieldPanel();
    JComponent buttonPanel = createButtonPanel();

    DefaultFormBuilder mainBuilder = createBuilder("p");
    mainBuilder.append(fieldPanel);
    mainBuilder.append(buttonPanel);

    return mainBuilder.getPanel();
  }


  private JComponent createButtonPanel() {
    return ButtonBarFactory.buildRightAlignedBar(registerButton);
  }


  private JComponent createFieldPanel() {
    DefaultFormBuilder builder = createBuilder("right:pref, 3dlu, 100dlu");

    builder.appendSeparator("Completion");
    builder.append("Time", completionTimeField); builder.nextLine();

    builder.appendSeparator("Cargo");
    builder.append("Tracking Id", trackingIdField); builder.nextLine();

    builder.appendSeparator("Event");
    builder.append("Voyage", carrierMovementField); builder.nextLine();
    builder.append("Location UN/Locode", locationField); builder.nextLine();
    builder.append("Event Type", eventTypeField); builder.nextLine();

    builder.appendSeparator("");

    return builder.getPanel();
  }


  private DefaultFormBuilder createBuilder(String colSpec) {
    FormLayout layout = new FormLayout(colSpec);

    DefaultFormBuilder builder = createDefaultFormBuilder(layout);
    builder.setDefaultDialogBorder();
    return builder;
  }


  private DefaultFormBuilder createDefaultFormBuilder(FormLayout layout) {
    if (debugUI){
      return new DefaultFormBuilder(layout, new FormDebugPanel());
    } else {
      return new DefaultFormBuilder(layout);
    }
  }

}
