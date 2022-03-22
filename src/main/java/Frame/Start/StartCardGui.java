package Frame.Start;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import App.InternalReconstruction;
import Frame.CardGui;
import Frame.CardWork;
import Frame.InfoLabel;
import Frame.InfoTextField;
import Frame.Result;
import Helper.Log.Logging;

/**
 * third GUI card of the app
 * it shows the analysis of the input file
 * 
 * @author abischoff
 *
 */
public class StartCardGui implements CardGui {

    private InternalReconstruction changer;
    private StartCardWork work;
    private JPanel contentPane;
    
	private JTextField textFieldFile;
	private JTextField textField_enc;
	private JTextField textField_cat;
	private JTextField textField_language;
	private JTextField textField_words;
	private JTextField textField_fmt;
	private JLabel center;
	private Result language;
  
	/**
	 * 
	 * @param result	result of the previous card deck
	 */
	public StartCardGui(Result result) {
		this.language = result;
	}

    /**
     * setter for the CardWork
     * @param CardWork	StartCardWork
     */
	@Override
    public void setWork(CardWork work) {
        if (work instanceof StartCardWork) {
            this.work = (StartCardWork) work;
        } else {
        	Logging.error("False CardWork for StartCardGui!");
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

    	this.contentPane = new JPanel();
    	
		ImageIcon ico = new ImageIcon(StartCardGui.class.getClassLoader().getResource("images/header_world - 500px.png"));
		
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

		JLabel lblNewLabel_1 = new JLabel("<html><center><span style=\"font-size:30;font-family:Segoe Print\"><i><b>Internal Reconstruction</b></i></span><br/><span style=\"font-size:14;font-family:Tahoma\">Eo Ipso 1.0</span></center></html>");
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
		 * right side of the card
		 */
		final JLabel panelRight = new JLabel();
		panelRight.setLayout(new BorderLayout());
		panelRight.setPreferredSize(new Dimension(frameWidth/2+15, frameHeight));

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
		panelRight.add(close);

    	JLabel lblLogo = new JLabel();
		ImageIcon icoLogo = new ImageIcon(StartCardGui.class.getClassLoader().getResource("images/Seminar_300px.png"));
		icoLogo.setImage(icoLogo.getImage().getScaledInstance(icoLogo.getIconWidth(), icoLogo.getIconHeight(),Image.SCALE_DEFAULT)); 
    	lblLogo.setIcon(icoLogo);
    	lblLogo.setPreferredSize(new Dimension((int) ((double)frameWidth*(1.0/2.0)),(int) ((double)frameHeight*(1.0/5.0))));
    	lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
    	lblLogo.setVerticalAlignment(SwingConstants.BOTTOM);
    	panelRight.add(lblLogo,BorderLayout.PAGE_START);

    	/*
    	 * Center Right
    	 */
    	
    	JLabel lineStart = new JLabel();
    	lineStart.setPreferredSize(new Dimension(40,0));
    	panelRight.add(lineStart,BorderLayout.LINE_START);
    	
    	JLabel lineEnd = new JLabel();
    	lineEnd.setLayout(new GridLayout(4,1));
    	lineEnd.setPreferredSize(new Dimension(65,(int)((double)frameHeight*(3.0/5.0))));
    	
    	panelRight.add(lineEnd,BorderLayout.LINE_END);

    	this.center = new JLabel(); //for data
    	this.center.setPreferredSize(new Dimension((int) ((double)frameWidth*(1.0/2.0)),(int) ((double)frameHeight*(2.5/5.0))));
    	panelRight.add(this.center,BorderLayout.CENTER);
    	FlowLayout flow = new FlowLayout();
    	this.center.setLayout(flow);
    	JLabel infoLabels = new JLabel();
    	infoLabels.setPreferredSize(new Dimension(120,(int) ((double)frameHeight*(2.5/5.0))));
    	infoLabels.setLayout(new GridLayout(8,1));
    	JLabel infoTextfields = new JLabel();
    	infoTextfields.setPreferredSize(new Dimension(180,(int) ((double)frameHeight*(2.5/5.0))));
    	infoTextfields.setLayout(new GridLayout(8,1));
    	infoLabels.add(new JLabel("")); //first line is empty
    	infoTextfields.add(new JLabel());

    	this.center.add(infoLabels);
    	this.center.add(infoTextfields);
    	this.center.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		InfoLabel filename = new InfoLabel("FILE");
		filename.setVisible(true);
		infoLabels.add(filename);
    	textFieldFile = new InfoTextField();
    	textFieldFile.setText(language.getLanguage().getPath());
    	infoTextfields.add(textFieldFile);
    	
    	InfoLabel languagename = new InfoLabel("LANGUAGE");
    	languagename.setVisible(true);
    	infoLabels.add(languagename);
    	textField_language = new InfoTextField();
    	textField_language.setText(language.getLanguage().getLang());
    	infoTextfields.add(textField_language);
    	
    	InfoLabel lblWords = new InfoLabel("WORDS");
    	lblWords.setVisible(true);
    	infoLabels.add(lblWords);
    	textField_words = new InfoTextField();
    	textField_words.setText(Integer.toString(language.getLanguage().getLines()));
    	infoTextfields.add(textField_words);

    	InfoLabel lblTrans = new InfoLabel("TRANSCRIPTION");
    	lblTrans.setVisible(true);
    	infoLabels.add(lblTrans);
    	textField_cat = new InfoTextField();
    	textField_cat.setText(language.getLanguage().getTranscription());
    	infoTextfields.add(textField_cat);
    	
    	InfoLabel lblFormat = new InfoLabel("FORMAT");
    	lblFormat.setVisible(true);
    	infoLabels.add(lblFormat);
    	textField_fmt = new InfoTextField();
    	textField_fmt.setText(language.getLanguage().getFormat());
    	infoTextfields.add(textField_fmt);
    	
    	InfoLabel lblEncoding = new InfoLabel("ENCODING");
    	lblEncoding.setVisible(true);
    	infoLabels.add(lblEncoding);
    	textField_enc = new InfoTextField();
    	textField_enc.setText(language.getLanguage().getEncoding());
    	infoTextfields.add(textField_enc);

		Button button = new Button("NEXT");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				work.setFilePath(textFieldFile.getText());
				work.setLanguage(textField_language.getText());
				work.setTranscription(textField_cat.getText());
				work.setEncoding(textField_enc.getText());
				work.setFormat(textField_fmt.getText());
				
				changer.close();
				work.close(); 
			}
		});
		button.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
		button.setForeground(Color.WHITE);
		button.setBackground(new Color(230, 55, 66));
		button.setSize(new Dimension(244,41));
    	JLabel lineMargin = new JLabel();
    	lineMargin.setPreferredSize(new Dimension((int) ((double)frameWidth*(1.0/2.0)-244.0)/2,41));
    	JLabel lineMargin2 = new JLabel();
    	lineMargin2.setPreferredSize(new Dimension((int) ((double)frameWidth*(1.0/2.0)-244.0)/2,41));
    	JLabel lineMargin3 = new JLabel();
    	lineMargin3.setPreferredSize(new Dimension(1,(int) ((double)frameHeight*(1.0/4.0))-41));
		
    	JLabel bottom = new JLabel();
    	bottom.setPreferredSize(new Dimension(0,(int) ((double)frameHeight*(1.0/4.0))));
    	bottom.setHorizontalAlignment(SwingConstants.CENTER);
    	bottom.setVerticalAlignment(SwingConstants.TOP);
    	bottom.setLayout(new BorderLayout());
    	bottom.add(button,BorderLayout.CENTER);
    	bottom.add(lineMargin,BorderLayout.LINE_START);
    	bottom.add(lineMargin2, BorderLayout.LINE_END);
    	bottom.add(lineMargin3, BorderLayout.PAGE_END);
    	
    	panelRight.add(bottom,BorderLayout.PAGE_END);
    	this.contentPane.add(panelRight);
    }
    

}