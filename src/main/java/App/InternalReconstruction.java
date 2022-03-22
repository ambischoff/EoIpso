package App;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import Frame.MyCardDeck;
import Frame.Result;
import Helper.Language.Language;
import Frame.CardLayoutServer;

/**
 * Main class that starts the process
 *
 */
public class InternalReconstruction {

    protected final JFrame frame;
    private final CardLayoutServer cardLayout;
    private int xx,xy;
    private Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    
    public static void main(String[] args) {
    	System.setProperty("sun.java2d.uiScale", "1");
    	new InternalReconstruction().start();
    }
    
    /**
     * first constructor for the start frame
     */
    public InternalReconstruction() {
    	
        this.frame = new JFrame();
        this.frame.setIconImage(new ImageIcon(InternalReconstruction.class.getClassLoader().getResource("images/icon.jpg")).getImage());
        this.cardLayout = new CardLayoutServer(new MyCardDeck());

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                generateGui(true);
            }
        });
    }
    
    /**
     * second constructor for the analysis frame 
     * @param lang	result of the DropField as result object
     */
    public InternalReconstruction(Result lang) {
    	this.frame = new JFrame();
    	this.frame.setIconImage(new ImageIcon(InternalReconstruction.class.getClassLoader().getResource("images/icon.jpg")).getImage());
    	this.cardLayout = new CardLayoutServer(new MyCardDeck(lang));

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                generateGui(true);
            }
        });
    }
    
    /**
     * third constructor for the final frame
     * @param lang	result of the StartCard as language object
     */
    public InternalReconstruction(Language lang) {
    	this.frame = new JFrame();
    	this.frame.setIconImage(new ImageIcon(InternalReconstruction.class.getClassLoader().getResource("images/icon.jpg")).getImage());
    	this.cardLayout = new CardLayoutServer(new MyCardDeck(lang));

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                generateGui(false);
            }
        });
    }
    
    /**
     * starts the process
     */
    public void start() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame.setVisible(true);
            }
        });
    }
    
    /**
     * switches to the next card
     */

    public void nextCard() {
        this.cardLayout.nextCard();		
    }
  
    /**
     * closes the frame
     */

    public void close() {
       this.frame.setVisible(false);
       this.frame.dispose();
    }
    
    /**
     * creates the GUI 
     * @param hasNextCard	is there a subsequent card?
     */
    private void generateGui(boolean hasNextCard) {
    	
		ImageIcon ico = new ImageIcon(InternalReconstruction.class.getClassLoader().getResource("images/header_world - 500px.png"));
		
		int frameWide = ico.getIconWidth()*2; 
		int frameHeight = ico.getIconHeight()*2; 
    	
		this.frame.setUndecorated(true);
		this.frame.setTitle("Internal Reconstruction");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setLayout(new BorderLayout());

        this.cardLayout.addAllCards(this);
		this.frame.add(cardLayout.getPanel(), BorderLayout.CENTER);

		this.frame.setLocation((int) (dim.getWidth()/2.0)-frameWide/2, (int) (dim.getHeight()/2.0)-frameHeight/2);		
		this.frame.setPreferredSize(new Dimension(frameWide, frameHeight));
		this.frame.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				xx = e.getX();
				xy = e.getY();
			}
		});
		this.frame.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				
				int x = e.getXOnScreen();
				int y = e.getYOnScreen();
				frame.setLocation(x - xx, y-xy);
			}
		});
		
		this.frame.pack();
		if (!hasNextCard) {
	    	this.frame.setVisible(true);
		}
		this.frame.setLocationRelativeTo(null);

    }

}
