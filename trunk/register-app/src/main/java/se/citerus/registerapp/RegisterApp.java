package se.citerus.registerapp;

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
import javax.swing.text.DefaultFormatter;

import se.citerus.registerapp.service.HandlingEventService;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.debug.FormDebugPanel;
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



  /////////////////////////////////////////////////////////////////////////////
  // GUI EVENT HANDLING
  /////////////////////////////////////////////////////////////////////////////
  protected void onRegister() {
    assert handlingEventService != null : "No HandlingEventService available";
    
    handlingEventService.register(ISO8601_DATE_FORMAT.format(completionTimeField.getValue()), 
                                  trackingIdField.getValue().toString(), 
                                  carrierMovementField.getValue().toString(), 
                                  locationField.getValue().toString(), 
                                  eventTypeField.getValue().toString());
    
    clearForm();
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
  
  private void initComponents(){
    completionTimeField = new JFormattedTextField(ISO8601_DATE_FORMAT);
    trackingIdField = new JFormattedTextField(DEFAULT_FORMATTER);
    carrierMovementField = new JFormattedTextField(DEFAULT_FORMATTER);
    locationField = new JFormattedTextField(DEFAULT_FORMATTER);
    eventTypeField = new JFormattedTextField(DEFAULT_FORMATTER);
    
    registerButton = new JButton("Register");
    registerButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        onRegister();
      }
    });
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
    
    DefaultFormBuilder mainBuilder = createBuilder("right:pref");
    mainBuilder.append(fieldPanel);
    mainBuilder.append(buttonPanel);
    
    return mainBuilder.getPanel();
  }


  private JComponent createButtonPanel() {
    DefaultFormBuilder builder = createBuilder("right:pref, 3dlu");
    
    builder.append(registerButton);
    
    return builder.getPanel();
  }


  private JComponent createFieldPanel() {
    DefaultFormBuilder builder = createBuilder("right:pref, 3dlu, 100dlu, 3dlu, p");
    
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
