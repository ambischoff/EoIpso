package Frame;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;


/**
 * menu buttons of the first card
 * @author abischoff
 *
 */
public class MenuTab extends JLabel {
	
	private static final long serialVersionUID = 4072413801934269500L;
	
	/**
	 * 
	 * @param name	title of the method
	 */
	public MenuTab(String name) {
		this.setHorizontalAlignment(JLabel.CENTER);
		this.setVerticalAlignment(JLabel.BOTTOM);

		this.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
		this.setText(name);
		if (name.contentEquals("Paradigmatic")) {
	    	this.setForeground(new Color(0, 40, 86));
	    	this.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(139,0,0)));
		} else {
			this.setForeground(Color.GRAY);
			this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(139,0,0)));
		}
		
		this.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
		    	setForeground(new Color(0, 40, 86));
		    	setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(139,0,0)));
			}
			public void mouseExited(MouseEvent e) {
				setForeground(Color.GRAY);
				setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(139,0,0)));
			}
		});
		
	}
	
	
}