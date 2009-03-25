package se.citerus.registerapp.validation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FormValidationSwingDecorator extends AbstractFormValidationDecorator{

  private static final int POPUP_Y_OFFSET = 0;
  private static final int POPUP_X_OFFSET = 12;
  private Color oldColor = Color.WHITE;
  private JFrame form;
  private String message;
  private HashMap<JComponent, JPanel> popups = new HashMap<JComponent, JPanel>();
  private JPanel glassPane = new JPanel();

  public FormValidationSwingDecorator(JFrame form) {
    assert form != null : "Can't decorate null frame";
    
    this.form = form;
    glassPane.setOpaque(false);
    glassPane.setVisible(false);
    glassPane.setLayout(null);
//    glassPane.setPreferredSize(form.getPreferredSize());
    glassPane.setSize(form.getSize());
    
    form.setGlassPane(glassPane);
    
    
  }

  public FormValidationSwingDecorator(JFrame form, String message) {
    this(form);
    
    this.message = message;
  }

  private JPanel createPopupRelativeTo(JComponent component) {
    JPanel popupPane = null;
    
    if (popups.containsKey(component)){
      popupPane = popups.get(component);
    } else {
      popupPane = new JPanel(new FlowLayout());
      glassPane.add(popupPane);
      glassPane.setVisible(true);
      
      popupPane.add(getIconLabel());
      popupPane.add(getMessageLabel());
      popupPane.setLocation(getPopupPositionRelativeTo(component));
      popupPane.setVisible(true);
      popupPane.setOpaque(false);
      popupPane.validate();
      popupPane.setSize(popupPane.getPreferredSize());
     
    }
    
    return popupPane;
  }

  private Point getPopupPositionRelativeTo(JComponent component) {
    Point compPos = component.getLocation();
    Dimension compDim = component.getSize();
    Point popupPos = new Point(compPos.x + POPUP_X_OFFSET, compPos.y + compDim.height + POPUP_Y_OFFSET);
    
    System.out.println("Comp pos: " + compPos + " - Popup pos: " + popupPos);
    return popupPos;
  }

  private JLabel getMessageLabel() {
    JLabel label = new JLabel(message != null ? message : "");
    label.setOpaque(false);
    return label;
  }

  private JLabel getIconLabel() {
    JLabel label = new JLabel();
    
    java.net.URL imageURL = this.getClass().getResource("/icon/error_16x16.png");
    if (imageURL != null){
      ImageIcon icon = new ImageIcon(imageURL);
      label.setIcon(icon);
    } else {
      System.out.println("No icon found");
    }
    
    
    label.setOpaque(false);
    return label;
  }
  
  @Override
  public void decorate(JComponent component, ComponentValidator validator) {
    this.message = validator.getMessage();
    this.oldColor = component.getBackground();
    component.setBackground(Color.PINK);
    JPanel panel = createPopupRelativeTo(component);
    popups.put(component, panel);
    panel.setVisible(true);
  }

  @Override
  public void undecorate(JComponent component) {
    component.setBackground(oldColor);
    JPanel panel = popups.get(component);
    if (panel != null){
      panel.setVisible(false);
    }
  }
}
