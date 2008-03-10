package se.citerus.registerapp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.text.DefaultFormatter;

import se.citerus.registerapp.service.HandlingEventService;
import se.citerus.registerapp.validation.FormValidationDecorator;
import se.citerus.registerapp.validation.FormValidationSwingDecorator;
import se.citerus.registerapp.validation.MandatoryTextFieldValidator;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.debug.FormDebugPanel;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

public class RegisterApp {
  private static final DefaultFormatter DEFAULT_FORMATTER = new DefaultFormatter();
  private static final SimpleDateFormat ISO8601_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS.SSS");
  private static final String TITLE = "Handling Event Registration";
  private JFrame frame;
  private JFormattedTextField completionTimeField;
  private JFormattedTextField trackingIdField;
  private JFormattedTextField carrierMovementField;
  private JFormattedTextField locationField;
  private JFormattedTextField eventTypeField;
  private JButton registerButton;
  
  private HandlingEventService handlingEventService;
  private boolean debugUI;
  private FormValidationDecorator validator;



  /////////////////////////////////////////////////////////////////////////////
  // GUI EVENT HANDLING
  /////////////////////////////////////////////////////////////////////////////
  protected void onRegister(){
    assert handlingEventService != null : "No HandlingEventService available";
    
    handlingEventService.register(ISO8601_DATE_FORMAT.format(completionTimeField.getValue()), 
                                  getStringValue(trackingIdField), 
                                  getStringValue(carrierMovementField), 
                                  getStringValue(locationField), 
                                  getStringValue(eventTypeField));
    
    clearForm();
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


  public void setHandlingEventService(HandlingEventService handlingEventService) {
    this.handlingEventService = handlingEventService;
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
    eventTypeField.setText("");
  }

  public static Border getMandatoryBorder() {
    return new CompoundBorder(new LineBorder(Color.RED), new BasicBorders.MarginBorder());
  }
  
  private void initComponents(){
    completionTimeField = new JFormattedTextField(ISO8601_DATE_FORMAT);
    trackingIdField = new JFormattedTextField(DEFAULT_FORMATTER);
    carrierMovementField = new JFormattedTextField(DEFAULT_FORMATTER);
    locationField = new JFormattedTextField(DEFAULT_FORMATTER);
    eventTypeField = new JFormattedTextField(DEFAULT_FORMATTER);
    
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
    
    validator.add(eventTypeField, new MandatoryTextFieldValidator("Event type can't be empty"));
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
    builder.append("Completion Time", completionTimeField); builder.nextLine();
    
    builder.appendSeparator("Cargo");
    builder.append("Tracking Id", trackingIdField); builder.nextLine();
    
    builder.appendSeparator("Event");
    builder.append("Carrier Movement", carrierMovementField); builder.nextLine();
    builder.append("Location", locationField); builder.nextLine();
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
