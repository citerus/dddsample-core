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

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
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
    FormLayout layout = createFormLayout();
    PanelBuilder builder = createBuilder(layout);
    
 // Obtain a reusable constraints object to place components in the grid.
    CellConstraints cc = new CellConstraints();
    
    builder.addLabel("Completion Time",         cc.xy (1,  1   ));
    builder.add(completionTimeField,            cc.xyw(3,  1, 3));
    builder.addLabel("Tracking Id",             cc.xy (1,  3   ));
    builder.add(trackingIdField,                cc.xyw(3,  3, 3));
    builder.addLabel("Carrier Movement",        cc.xy (1,  5   ));
    builder.add(carrierMovementField,           cc.xyw(3,  5, 3));
    builder.addLabel("Location",                cc.xy (1,  7   ));
    builder.add(locationField,                  cc.xyw(3,  7, 3));
    builder.addLabel("Event Type",              cc.xy (1,  9   ));
    builder.add(eventTypeField,                 cc.xyw(3,  9, 3));
    builder.add(registerButton,                 cc.xy (5,  11  ));
    
    return builder.getPanel();
  }

  private PanelBuilder createBuilder(FormLayout layout) {
    PanelBuilder builder = new PanelBuilder(layout);
    builder.setDefaultDialogBorder();
    
    return builder;
  }

  private FormLayout createFormLayout() {
    FormLayout layout = new FormLayout(
        "right:pref, 3dlu, 100dlu, 3dlu, pref",                  // columns
        "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu"); // rows
    
    return layout;
  }
}
