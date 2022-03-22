package Frame;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * input field for the StartCardGui
 * @author abischoff
 *
 */
public class InfoTextField extends JTextField {
	
	private static final long serialVersionUID = 7187154758888766951L;

	/**
	 * 
	 * @param text	text of the input field
	 */
	public InfoTextField() { 
    	this.setForeground(Color.GRAY);
    	this.setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
    	this.setColumns(10);
	}
	
	/**
	 * no border
	 */
	@Override 
	public void setBorder(Border border) {
		
	}

	
}