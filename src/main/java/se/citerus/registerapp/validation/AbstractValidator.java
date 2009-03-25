package se.citerus.registerapp.validation;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class AbstractValidator extends InputVerifier implements KeyListener{
  private static final Color ERROR_COMPONENT_COLOR = Color.PINK;
  private static final Color POPUP_COLOR = new Color(243, 255, 159);
  private JPanel popup;
  private JLabel messageLabel = new JLabel();;
  private JLabel image = new JLabel();
  private JFrame parent;
  private String message;;

  
  
  public AbstractValidator(String message) {
    this.message = message;
  }
  
  public String getMessage(){
    return message;
  }

  public AbstractValidator(JFrame parent, JComponent c, String message) {
    c.addKeyListener(this);
    popup = new JPanel();
    this.parent = parent;
    messageLabel = new JLabel(message + " ");
    image = new JLabel(new ImageIcon("/icon/error_16x16.png"));
    initComponents();
  }

  /**
   * Implement the actual validation logic in this method. The method should
   * return false if data is invalid and true if it is valid.
   * 
   * @param c The JComponent to be validated.
   * @return false if data is invalid. true if it is valid.
   */
  protected abstract boolean validate(JComponent c);

  @Override
  public boolean verify(JComponent component) {
    if (!validate(component)) {

//      
//      Dimension compSize = component.getSize();
//      Point compPos = popup.getLocation();
//      
//      component.setBackground(ERROR_COMPONENT_COLOR);
//
//      popup.setLocation(
//          compPos.x - (int) compSize.getWidth() / 2, 
//          compPos.y + (int) compSize.getHeight() / 2);
      
      parent.setGlassPane(popup);
      component.setBackground(ERROR_COMPONENT_COLOR);
      popup.setVisible(true);
      return false;
    } else {
      component.setBackground(Color.WHITE);
      return true;
    }
  }

  public void keyPressed(KeyEvent e) {
    popup.setVisible(false);    
  }

  public void keyReleased(KeyEvent e) {}

  public void keyTyped(KeyEvent e) {}
  
  
  private void initComponents() {
    popup.setLayout(new FlowLayout());
//    popup.setUndecorated(true);
    popup.setBackground(POPUP_COLOR);
    popup.add(image);
    popup.add(messageLabel);
//    popup.setOpaque(false);
//    popup.setFocusableWindowState(false);
  }


}
