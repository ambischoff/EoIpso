package Frame.Drop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import App.InternalReconstruction;
import Frame.CardGui;
import Frame.CardWork;
import Helper.Log.Logging;

/**
 * second GUI card of the app
 * it shows an drag&drop field
 * 
 * @author abischoff
 *
 */
public class DropCardGui implements CardGui {

	    private InternalReconstruction changer;
	    private DropCardWork work;
	    private JPanel contentPane;
	    private JLabel panelRight; //right part of the GUI
	    private JLabel dragAndDrop;

	    /**
	     * setter for the CardWork
	     * @param CardWork	DropCardWork
	     */
		@Override
	    public void setWork(CardWork work) {
	        if (work instanceof DropCardWork) {
	            this.work = (DropCardWork) work;
	        } else {
	        	Logging.error("False CardWork for DropCardGui!");
	        }
	    }

		/**
		 * initializes variables and the gui method
		 * @param instance of InternalReconstruction
		 */
	    @Override
	    public Component generateGui(InternalReconstruction changer) {
	        this.changer = changer;

	        this.contentPane = new JPanel();
	        this.setGui();
	        
	        return this.contentPane;
	    }

	    /**
	     * generates the GUI
	     */
	    public void setGui() {

			ImageIcon ico = new ImageIcon(DropCardGui.class.getClassLoader().getResource("images/header_world - 500px.png"));

			int frameWidth = ico.getIconWidth()*2; 
			int frameHeight = ico.getIconHeight()*2;

			this.contentPane.setBackground(Color.WHITE);
			this.contentPane.setBorder(null);
			this.contentPane.setLayout(new GridLayout(1,2));

			/*
			 * left part of the GUI
			 */
			
			JPanel panelLeft = new JPanel();
			panelLeft.setBackground(new Color(139,0,0));
			contentPane.add(panelLeft);
			panelLeft.setLayout(new GridLayout(2,1));

			JLabel lblNewLabel_1 = new JLabel("<html><center><span style=\"font-size:30;font-family:Segoe Print\"><i><b>Internal Reconstruction</b></i></span><br/><span style=\"font-size:14;font-family:Tahoma\">Eo Ipso 2.0</span></center></html>");
			lblNewLabel_1.setForeground(new Color(240, 248, 255));
			lblNewLabel_1.setPreferredSize(new Dimension(frameWidth/2, (frameHeight/2)));
			lblNewLabel_1.setHorizontalTextPosition(SwingConstants.CENTER);
			lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
			panelLeft.add(lblNewLabel_1);

			JLabel bgimage = new JLabel("");
			ico.setImage(ico.getImage().getScaledInstance(ico.getIconWidth(),ico.getIconHeight(),Image.SCALE_DEFAULT)); 
			bgimage.setIcon(ico);
			bgimage.setPreferredSize(new Dimension(frameWidth/2, (frameHeight/2)));
			panelLeft.add(bgimage);
		
			
			/*
			 * right part of the GUI
			 */
			this.panelRight = new JLabel();
			this.panelRight.setLayout(new BorderLayout());
			this.panelRight.setPreferredSize(new Dimension(frameWidth/2+15, frameHeight));

			JLabel close = new JLabel("X");
			close.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					System.err.println("App closed by the user.");
					System.exit(0);
				}
			});
			close.setBackground(new Color(255, 0, 0));
			close.setForeground(new Color(255, 0, 0));
			close.setFont(new Font("Tahoma", Font.PLAIN, 18));
			close.setHorizontalAlignment(SwingConstants.RIGHT);
			close.setVerticalAlignment(SwingConstants.TOP);
			close.setBounds(frameWidth/2-30, 0, 23, 41);
			this.panelRight.add(close);

	    	JLabel lblLogo = new JLabel();
			ImageIcon icoLogo = new ImageIcon(DropCardGui.class.getClassLoader().getResource("images/Seminar_300px.png"));
			icoLogo.setImage(icoLogo.getImage().getScaledInstance(icoLogo.getIconWidth(), icoLogo.getIconHeight(),Image.SCALE_DEFAULT)); 
	    	lblLogo.setIcon(icoLogo);
	    	lblLogo.setPreferredSize(new Dimension((int) ((double)frameWidth*(1.0/2.0)),(int) ((double)frameHeight*(1.0/5.0))));
	    	lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
	    	lblLogo.setVerticalAlignment(SwingConstants.BOTTOM);

	    	this.panelRight.add(lblLogo,BorderLayout.PAGE_START);
	    	
	    	JLabel lineStart = new JLabel();
	    	lineStart.setPreferredSize(new Dimension(40,0));
	    	this.panelRight.add(lineStart,BorderLayout.LINE_START);
	    	
	    	JLabel lineEnd = new JLabel();
	    	lineEnd.setPreferredSize(new Dimension(40,0));
	    	this.panelRight.add(lineEnd,BorderLayout.LINE_END);

	    	JLabel myCenter =new JLabel();
	    	myCenter.setLayout(new GridBagLayout());
	    	JLabel buffer = new JLabel();
	    	buffer.setMaximumSize(new Dimension(0,10));
	    	
	    	GridBagConstraints c = new GridBagConstraints();
	    	c.fill = GridBagConstraints.VERTICAL;
	    	c.gridx = 0;
	    	c.gridy = 0;
	    	myCenter.add(buffer,c);
	    	
	    	this.dragAndDrop = new JLabel();
	    	this.dragAndDrop.setMaximumSize(new Dimension((int) ((double)frameWidth*(1.0/2.0)-105),(int) ((double)frameHeight*(3.0/5.0))));
	    	this.dragAndDrop.setVerticalAlignment(SwingConstants.BOTTOM);
	    	this.dragAndDrop.setHorizontalAlignment(SwingConstants.CENTER);
	    	
			ImageIcon icoDrop =  new ImageIcon(DropCardGui.class.getClassLoader().getResource("images/Dropfield.png"));
			icoDrop.setImage(icoDrop.getImage().getScaledInstance(433,340,Image.SCALE_DEFAULT)); 
	    	this.dragAndDrop.setIcon(icoDrop);
			
	    	this.dragAndDrop.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
	    	this.dragAndDrop.setBackground(new Color(255, 255, 255));

	    	c.gridx = 0;
	    	c.gridy = 1;
	    	myCenter.add(this.dragAndDrop,c);
	    	
	    	this.panelRight.add(myCenter,BorderLayout.CENTER);
	  
	    	JLabel bottom = new JLabel();
	    	bottom.setPreferredSize(new Dimension(0,100));
	    	this.panelRight.add(bottom,BorderLayout.PAGE_END);
	    	
	    	new DropTarget(this.dragAndDrop, new DropTargetListener()
	    	{
	    	    @Override
	    	    public void drop(DropTargetDropEvent dtde)
	    	    {
	    	    	
	    		try
	    		{
	    		    Transferable tr = dtde.getTransferable();
	    		    DataFlavor[] flavors = tr.getTransferDataFlavors();
	    		    for (int i = 0; i < flavors.length; i++)
	    		    {

	    			if (flavors[i].isFlavorJavaFileListType()) {

	    			    dtde.acceptDrop(dtde.getDropAction());
	    			    @SuppressWarnings("unchecked")
	    			    java.util.List<File> files = (java.util.List<File>) tr.getTransferData(flavors[i]);

	    			    for (int k = 0; k < files.size(); k++) {
	    			    		
	    			    	 	work.setFilePath(files.get(k).getAbsolutePath());
			    		        work.readFileInfo();
			    		        dragAndDrop.setVisible(false); 
			    		        changer.close();
			    				work.close();
			    				
	    			    }
	     
	    			    dtde.dropComplete(true);
	    			}
	    		    }
	    		    return;
	    		} catch (Throwable t) {
	    		    t.printStackTrace();
	    		}
	    		dtde.rejectDrop();
	    		
	    	    }
	     
	    	    @Override
	    	    public void dragEnter(DropTargetDragEvent dtde)
	    	    {}
	     
	    	    @Override
	    	    public void dragOver(DropTargetDragEvent dtde)
	    	    {}
	     
	    	    @Override
	    	    public void dropActionChanged(DropTargetDragEvent dtde)
	    	    {}
	     
	    	    @Override
	    	    public void dragExit(DropTargetEvent dtde)
	    	    {}

	     
	    	});

			this.contentPane.add(this.panelRight);
	    }



	}

