package Frame.Execute;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import App.InternalReconstruction;
import Distributional.ComplementarySounds;
import Frame.CardGui;
import Frame.CardWork;
import Helper.Language.Language;
import Helper.Language.Method;
import Helper.Log.Logging;

/**
 * fourth GUI of the app
 * it shows the input fields for the method
 * 
 * @author abischoff
 *
 */
public class ExecuteCardGui implements CardGui{

    private JPanel panel;
    private ExecuteCardWork work;
    private Language myMethod;
    
    private JTextField textFieldIteration;
    private JTextField textFieldLangSubTypo;
    private JComboBox<String> textFieldComp; 
	private String[] methStrings = new String[ComplementarySounds.values().length];

    private JLabel langNumb;
    private Button button;
	private JTextField textFieldThreshold;
	private JTextField textFieldExclude;
	private JTextField textFieldWeight1;
	private JTextField textFieldWeight2;
	private JTextField[] textFieldClusters = new JTextField[5];
	
	/**
	 * 
	 * @param lang	result of the previous card deck as Language
	 */
    public ExecuteCardGui(Language lang) {
    	Logging.debug("Open card "+lang.getMethod().toString());
    	this.myMethod = lang;
    	for (int i = 0; i < ComplementarySounds.values().length ; i++) {
    		this.methStrings[i] = ComplementarySounds.values()[i].toString();
    	}
    }
    
    /**
     * setter for the CardWork
     * @param CardWork ExcecuteCardWork
     */
    @Override
    public void setWork(CardWork work) {
        if (work instanceof ExecuteCardWork) {
            this.work = (ExecuteCardWork) work;
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
        changer.close();
        this.panel = new JPanel();
        this.setGui();
        return this.panel;
    }

    /**
     * creates the GUI
     */
    public void setGui() {

		int frameHeight = new ImageIcon(ExecuteCardGui.class.getClassLoader().getResource("images/header_world - 500px.png")).getIconHeight()*2; 
		int frameWidth = new ImageIcon(ExecuteCardGui.class.getClassLoader().getResource("images/header_world - 500px.png")).getIconWidth()*2;

		this.panel.setBackground(new Color(0,0,0,0));
		this.panel.setBorder(null);
		this.panel.setLayout(new BorderLayout());
		this.panel.setOpaque(true);
		this.panel.setBackground(new Color(139,34,82));
		
		JLabel close = new JLabel("X");
		close.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				if (!button.isEnabled()) {
		                int opt = JOptionPane.showConfirmDialog(null, "Should the running process be canceled?",
		                        null, JOptionPane.OK_CANCEL_OPTION);
		                if (opt == JOptionPane.OK_OPTION)
		                    System.exit(0);

		       } else {				
		    	   Logging.debug("App closed by user.");
		    	   System.exit(0);
		       }
			}
		});
		close.setBackground(new Color(255, 0, 0));
		close.setForeground(new Color(255, 0, 0));
		close.setFont(new Font("Tahoma", Font.PLAIN, 18));
		close.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JPanel beigeBg = new JPanel();
		beigeBg.setLayout(new GridLayout(1,3));
		beigeBg.setBackground(new Color(0, 40, 86));
		beigeBg.setPreferredSize(new Dimension(panel.getWidth(),50));
		beigeBg.add(new JLabel("  "));
		JLabel title = new JLabel("Eo Ipso");
		title.setForeground(Color.WHITE);
		title.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
		title.setHorizontalAlignment(SwingConstants.CENTER);
		beigeBg.add(title);
		beigeBg.add(close);
		this.panel.add(beigeBg, BorderLayout.PAGE_START);
		
		JPanel left = new JPanel();
		left.setPreferredSize(new Dimension((int)((double)frameWidth*(1.0/6.0)),1));
		left.setBackground(new Color(139,34,82));
		this.panel.add(left,BorderLayout.LINE_START);
		
		JPanel right = new JPanel();
		right.setPreferredSize(new Dimension((int)((double)frameWidth*(1.0/6.0)),1));
		right.setBackground(new Color(139,34,82));
		this.panel.add(right,BorderLayout.LINE_END);
		
		/*
		 * Center of the GUI
		 */
		JPanel center = new JPanel();
		center.setLayout(new BorderLayout());

		
		JLabel methodName = new JLabel(myMethod.getMethod().toString());
		methodName.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 30));
		methodName.setForeground(Color.WHITE);
		methodName.setBackground(new Color(139,34,82));
		methodName.setOpaque(true);
		methodName.setVerticalAlignment(SwingConstants.BOTTOM);
		methodName.setPreferredSize(new Dimension(1,100));
		
		center.add(methodName,BorderLayout.PAGE_START);
		
		center.setBorder(BorderFactory.createMatteBorder(2, 2, 0, 0, Color.blue));
		this.panel.add(center,BorderLayout.CENTER);
		
		JLabel parameters = new JLabel();
		parameters.setLayout(new GridLayout(7,4));
		parameters.setBackground(new Color(139,34,82));
		parameters.setOpaque(true);
		center.add(parameters,BorderLayout.CENTER);
		
		parameters.add(new JLabel());//dummy
		parameters.add(new JLabel());
		parameters.add(new JLabel());//dummy
		parameters.add(new JLabel());
		
		JLabel iterations = new JLabel("Max. iterations");
		iterations.setForeground(Color.WHITE);
		iterations.setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
		parameters.add(iterations);
		
    	textFieldIteration = new JTextField(Integer.toString(myMethod.getIteration())) { private static final long serialVersionUID = -6190446027580699096L; @Override public void setBorder(Border border) {}};
    	textFieldIteration.setForeground(Color.GRAY);
    	textFieldIteration.setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
    	textFieldIteration.setColumns(10);
    	textFieldIteration.setBounds(frameHeight/8+150, (frameWidth/4)+50, 150, 25);
    	parameters.add(textFieldIteration);
    	textFieldIteration.setVisible(true);
		
		JRadioButton cons = new JRadioButton("<html><p style=\"font-size:14\">Use Wiktionary instead<br/>of the file</p></html>");
		JRadioButton vow = new JRadioButton("<html><p style=\"font-size:13\">Use IPA from Wiktionary<br/>instead of the file</p></html>");
		JRadioButton typo = new JRadioButton("<html>Typological Sound<br/>Correspondences</html>");
		this.textFieldComp = new JComboBox<String>(methStrings) { private static final long serialVersionUID = -5313785555326949302L;
		@Override public void setBorder(Border border) {}};

		if (this.myMethod.getMethod() == Method.distinctive || this.myMethod.getMethod() == Method.paradigmatic || this.myMethod.getMethod() == Method.derivational) {
			cons.setBackground(new Color(139,34,82));
			cons.setForeground(Color.WHITE);
			cons.setFont(new Font("Myriad Pro Light", Font.BOLD, 16));

			vow.setBackground(new Color(139,34,82));
			vow.setForeground(Color.WHITE);
			vow.setFont(new Font("Myriad Pro Light", Font.BOLD, 16));

	    	if (this.myMethod.getMethod() == Method.distinctive ) {
	    		if (myMethod.getTranscription().contains("IPA")) {
		    		 cons.setText("Exclude all consonants");
		    		 vow.setText("Exclude all vowels");
		 			parameters.add(cons);
		 			parameters.add(vow);
	    		} else {
	    			JLabel clusterTitle = new JLabel("Clustering");
	    			clusterTitle.setBackground(new Color(139,34,82));
	    			clusterTitle.setForeground(Color.WHITE);
	    			clusterTitle.setFont(new Font("Myriad Pro Light", Font.BOLD, 20));
		    		 parameters.add(clusterTitle);
		    		 parameters.add(new JLabel());
	    		}
	    	} else {
				parameters.add(cons);
				parameters.add(vow);
	    	}
		} else if (this.myMethod.getMethod() == Method.phonotactic) {
			JLabel compSound = new JLabel("Complementary Sound Approach");
			compSound.setForeground(Color.WHITE);
			compSound.setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
			compSound.setVisible(true);
			parameters.add(compSound);
	    	textFieldComp.setForeground(Color.GRAY);
	    	textFieldComp.setSelectedIndex(1);
	    	textFieldComp.setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
	    	textFieldComp.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent event) {                
	                @SuppressWarnings("unchecked")
					JComboBox<String> comboBox = (JComboBox<String>) event.getSource();

	                Object selected = comboBox.getSelectedItem();
	                if(selected.toString().equals("Subtypological Approach")) {
	                	textFieldLangSubTypo.setVisible(true);
	                	langNumb.setVisible(true);
	                }

	            }
	        });
	    	parameters.add(textFieldComp);
		} else {
			parameters.add(new JLabel());//dummy
			parameters.add(new JLabel());
		}
    	
    	if (myMethod.getMethod() == Method.phonotactic || myMethod.getMethod() == Method.distinctive || myMethod.getMethod() == Method.gap) {

			JLabel threshold = new JLabel("Threshold");
			threshold.setForeground(Color.WHITE);
			threshold.setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
			parameters.add(threshold);
			
	    	textFieldThreshold = new JTextField(Double.toString(myMethod.getThreshold())) { private static final long serialVersionUID = -6190446027580699096L; @Override public void setBorder(Border border) {}};
	    	textFieldThreshold.setForeground(Color.GRAY);
	    	textFieldThreshold.setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
	    	textFieldThreshold.setColumns(10);
	    	textFieldThreshold.setVisible(true);
	    	parameters.add(textFieldThreshold);
    	} else {
    		parameters.add(new JLabel());
    		parameters.add(new JLabel());
    	}
    	
    	if (this.myMethod.getMethod() == Method.distinctive) {
			JLabel exclude = new JLabel("Exclude sounds");
			exclude.setForeground(Color.WHITE);
			exclude.setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
			parameters.add(exclude);
	    	textFieldExclude = new JTextField() { private static final long serialVersionUID = -6190446027580699096L; @Override public void setBorder(Border border) {}};
	    	textFieldExclude.setForeground(Color.GRAY);
	    	textFieldExclude.setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
	    	parameters.add(textFieldExclude);
    	} else {
    		parameters.add(new JLabel());
    		parameters.add(new JLabel());
    	}
    	
		if (this.myMethod.getMethod() == Method.distinctive || this.myMethod.getMethod() == Method.gap) {
			typo.setBackground(new Color(139,34,82));
			typo.setForeground(Color.WHITE);
			typo.setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
			parameters.add(typo);
			parameters.add(new JLabel());
		} else {
			parameters.add(new JLabel());
			parameters.add(new JLabel());
		}

    	if (this.myMethod.getMethod() == Method.distinctive) {
			JLabel clusters = new JLabel("Using own clusters:");
			clusters.setForeground(Color.WHITE);
			clusters.setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
			clusters.setVisible(true);
			parameters.add(clusters);
	
	    	textFieldClusters[0] = new JTextField("[a,e,...]") { 
				private static final long serialVersionUID = 1L;
			@Override public void setBorder(Border border) {}};
	    	textFieldClusters[0].setForeground(Color.GRAY);
	    	textFieldClusters[0].setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
	    	textFieldClusters[0].setBounds(frameHeight/2+150, (frameWidth/4)+180, 150, 25);
	    	parameters.add(textFieldClusters[0]);
   
		
			JLabel weighting1 = new JLabel("Weights (g1)");
			weighting1.setForeground(Color.WHITE);
			weighting1.setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
			parameters.add(weighting1);
			
	    	textFieldWeight1 = new JTextField(Double.toString(5.0)) {
				private static final long serialVersionUID = -3924667964982082652L;
			@Override public void setBorder(Border border) {}};
			textFieldWeight1.setForeground(Color.GRAY);
			textFieldWeight1.setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
			textFieldWeight1.setColumns(10);
			textFieldWeight1.setVisible(true);
			parameters.add(textFieldWeight1);

			textFieldClusters[1] = new JTextField("[b,d,...]") { 
				private static final long serialVersionUID = 820515125634811285L;
			@Override public void setBorder(Border border) {}};
	    	textFieldClusters[1].setForeground(Color.GRAY);
	    	textFieldClusters[1].setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
	    	textFieldClusters[1].setColumns(10);
	    	textFieldClusters[1].setVisible(true);
	    	parameters.add(textFieldClusters[1]);
	    	textFieldClusters[2] = new JTextField("[c,e,...]") { 
				private static final long serialVersionUID = 1L;
			@Override public void setBorder(Border border) {}};
	    	textFieldClusters[2].setForeground(Color.GRAY);
	    	textFieldClusters[2].setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
	    	parameters.add(textFieldClusters[2]);
    	
			JLabel weighting2 = new JLabel("Weights (g2)");
			weighting2.setForeground(Color.WHITE);
			weighting2.setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
			parameters.add(weighting2);
	
	    	textFieldWeight2 = new JTextField(Double.toString(1.0)) {
				private static final long serialVersionUID = 1417159934477975431L;
			@Override public void setBorder(Border border) {}};
			textFieldWeight2.setForeground(Color.GRAY);
			textFieldWeight2.setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
			textFieldWeight2.setColumns(10);
			textFieldWeight2.setVisible(true);
			parameters.add(textFieldWeight2);

	    	textFieldClusters[3] = new JTextField("[d,f,...]") { 
				private static final long serialVersionUID = 1L;
			@Override public void setBorder(Border border) {}};
	    	textFieldClusters[3].setForeground(Color.GRAY);
	    	textFieldClusters[3].setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
	    	parameters.add(textFieldClusters[3]);
	    	textFieldClusters[4] = new JTextField("[c,e,...]") { 
				private static final long serialVersionUID = 1L;
			@Override public void setBorder(Border border) {}};
	    	textFieldClusters[4].setForeground(Color.GRAY);
	    	textFieldClusters[4].setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
	    	parameters.add(textFieldClusters[4]);
	    	
			parameters.add(new JLabel());
			parameters.add(new JLabel());
			
			langNumb = new JLabel("Number Languages");
			langNumb.setForeground(Color.WHITE);
			langNumb.setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
			parameters.add(langNumb);
	    	textFieldLangSubTypo = new JTextField("5") { private static final long serialVersionUID = -6190446027580699096L; @Override public void setBorder(Border border) {}};
	    	textFieldLangSubTypo.setForeground(Color.GRAY);
	    	textFieldLangSubTypo.setFont(new Font("Myriad Pro Light", Font.BOLD, 16));
	    	textFieldLangSubTypo.setColumns(10);
	    	parameters.add(textFieldLangSubTypo);
		} else {
			parameters.add(new JLabel());
			parameters.add(new JLabel());
	    	parameters.add(new JLabel());
	    	parameters.add(new JLabel());
	    	parameters.add(new JLabel());
	    	parameters.add(new JLabel());
	    	parameters.add(new JLabel());
	    	parameters.add(new JLabel());
			parameters.add(new JLabel());
			parameters.add(new JLabel());
		}

    	/*
    	 * END of GUI
    	 */
    	
    	JLabel end = new JLabel();
    	end.setLayout(new GridBagLayout());
    	end.setPreferredSize(new Dimension(1,120));
    	panel.add(end,BorderLayout.PAGE_END);
		
		this.button = new Button("START");
		this.button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			
				/*
				 * execute the script
				 */
				new Thread(new Runnable() {
		            @Override
		            public void run() {
				        button.setEnabled(false);	
		            	if (textFieldLangSubTypo != null) {
		            		work.setComplementaryFinder((String)textFieldComp.getSelectedItem(), textFieldLangSubTypo.getText());
		            	}
		            	if (textFieldWeight1 != null) {
		            		work.setWeights(textFieldWeight1.getText(),textFieldWeight2.getText());
		            	}
						if (textFieldClusters[0] != null && (!textFieldClusters[0].getText().contentEquals("[a,e,...]") || !textFieldClusters[1].getText().contentEquals("[b,d,...]") || !textFieldClusters[2].getText().contentEquals("[c,e,...]"))) {
							String[] texts = new String[textFieldClusters.length];
							for (int i = 0 ; i < texts.length; i++) {
								texts[i] = textFieldClusters[i].getText();
							}
							work.setClusters(texts);
						}
						
							String myExclude = "";
						if (textFieldExclude != null) {	
							textFieldExclude.getText();
							if (vow.isSelected()) {
								myExclude += ", vokal";
							} else if (cons.isSelected()) {
								myExclude += ", konsonant";
							}
						}
						String threshold = (textFieldThreshold == null ? "0.0" : textFieldThreshold.getText());
						work.setArguments(textFieldIteration.getText(),threshold,myExclude, typo.isSelected());
						work.close();
						button.setEnabled(true);

			            int opt = JOptionPane.showConfirmDialog(null, "Finished. Open the directory?",
			                      null, JOptionPane.OK_CANCEL_OPTION);
			           if (opt == JOptionPane.OK_OPTION) {
						try {
								Logging.closeProcessWindow();
								Runtime.getRuntime().exec("explorer.exe " + myMethod.getResultFile().replaceAll("\\\\[^\\\\]*$",""));
							} catch (IOException e) {
								Logging.error("Opening of the result file window failed: "+e.getLocalizedMessage());
							}
			             }


		            }
		        }).start();
				
			}
		});
		button.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
		button.setForeground(Color.WHITE);
		button.setBackground(new Color(230, 55, 66));
		button.setPreferredSize(new Dimension(150, 25));
		end.add(button);

    }
    

}

