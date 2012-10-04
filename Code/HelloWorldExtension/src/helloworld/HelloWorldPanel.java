package helloworld;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A simple hello world extension.
 * 
 * @author Kevin Risden (risdenkj@rose-hulman.edu)
 */
public class HelloWorldPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HelloWorldPanel() {
		layout(this);
	}
	
	public void layout(JPanel contentPane) {
		contentPane.setLayout(new BorderLayout());
		JLabel lblName = new JLabel("Hello World");	
		contentPane.add(lblName); 
	}
}
