package Frame;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * labels for the StartCardGui
 * @author abischoff
 *
 */
public class InfoLabel extends JLabel {
	
	private static final long serialVersionUID = 6439657799398731828L;

	/**
	 * 
	 * @param text	name of the label
	 */
	public InfoLabel(String text) {
		super(text);
		this.setForeground(new Color(0, 40, 86));
		this.setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
		this.setVerticalAlignment(SwingConstants.CENTER);
		if (!text.contentEquals("")) {
			this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.blue));
		}
	}
	
}