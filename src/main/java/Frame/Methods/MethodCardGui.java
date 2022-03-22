package Frame.Methods;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import App.InternalReconstruction;
import Frame.CardGui;
import Frame.CardWork;
import Frame.MenuTab;
import Helper.Language.Method;
import Helper.Log.Logging;

/**
 * first GUI of the app
 * it shows all methods
 * 
 * @author abischoff
 *
 */
public class MethodCardGui implements CardGui{

    private InternalReconstruction changer;
    private JPanel panel;
    private MethodCardWork work;
	
    /**
     * setter for the CardWork
     * @param CardWork	MethodCardWork
     */
	@Override
    public void setWork(CardWork work) {
        if (work instanceof MethodCardWork) {
             this.work = (MethodCardWork) work;
        } else {
        	Logging.error("False CardWork for MethodCardGui!");
        }
    }

	/**
	 * initializes variables and the gui method
	 * @param instance of InternalReconstruction
	 */
    @Override
    public Component generateGui(InternalReconstruction changer) {
        this.changer = changer;
        this.panel = new JPanel();
        this.setGui();
        return this.panel;
    }

    /**
     * generates the GUI
     */
    public void setGui() {
    
		ImageIcon ico = new ImageIcon(MethodCardGui.class.getClassLoader().getResource("images/Paradigmatic.png"));
		JLabel bgimage = new JLabel("");
		int frameWidth = 1000; 
		
		ico.setImage(ico.getImage().getScaledInstance(ico.getIconWidth(),ico.getIconHeight(),Image.SCALE_DEFAULT)); 

		this.panel.setBorder(null);
		this.panel.setLayout(new BorderLayout());
		
		JLabel close = new JLabel("X");
		close.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.err.println("App closed by user.");
				System.exit(0);
			}
		});
		close.setBackground(new Color(255, 0, 0));
		close.setForeground(new Color(255, 0, 0));
		close.setFont(new Font("Tahoma", Font.PLAIN, 18));
		close.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JPanel beigeBg = new JPanel();
		beigeBg.setLayout(new GridLayout(1,3));
		beigeBg.setBackground(new Color(139,0,0));
		beigeBg.setPreferredSize(new Dimension(panel.getWidth(),50));
		beigeBg.add(new JLabel("  "));
		JLabel title = new JLabel("Eo Ipso");
		title.setForeground(Color.WHITE);
		title.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
		title.setHorizontalAlignment(SwingConstants.CENTER);
		beigeBg.add(title);
		beigeBg.add(close);
		this.panel.add(beigeBg, BorderLayout.PAGE_START);
		
		JPanel center = new JPanel();
		center.setLayout(new BorderLayout());
		JLabel margin1 = new JLabel();
		margin1.setPreferredSize(new Dimension(40,40));
		center.add(margin1,BorderLayout.PAGE_START);
		JLabel margin2 = new JLabel();
		margin2.setPreferredSize(new Dimension(40,40));
		center.add(margin2,BorderLayout.LINE_START);
		
		ImageIcon startIcon = new ImageIcon(MethodCardGui.class.getClassLoader().getResource("images/LanguageNetwork 4.png"));
		JLabel centerText = new JLabel();
		centerText.setVerticalAlignment(SwingConstants.TOP);
		startIcon.setImage(startIcon.getImage().getScaledInstance(startIcon.getIconWidth(),startIcon.getIconHeight(),Image.SCALE_DEFAULT)); 
		centerText.setIcon(startIcon);
		center.add(centerText,BorderLayout.CENTER);
		this.panel.add(center,BorderLayout.CENTER);
		
		
		/*
		 *left part of the GUI
		 */
		
		JPanel leftSide = new JPanel();
		leftSide.setBackground(Color.WHITE);
		leftSide.setLayout(new GridLayout(8,1));
		leftSide.setPreferredSize(new Dimension((int) ((double)frameWidth*(1.0/4.0)),100));
		JLabel morphophonemic = new JLabel("Morphophonemic Methods"); 
		morphophonemic.setForeground(new Color(139,0,0));
		morphophonemic.setHorizontalAlignment(SwingConstants.CENTER);
		morphophonemic.setVerticalAlignment(SwingConstants.BOTTOM);
		morphophonemic.setFont(new Font("Segoe Print", Font.BOLD, 18));
		leftSide.add(morphophonemic);
		MenuTab paradigmatic = new MenuTab("Paradigmatic");
		paradigmatic.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				work.setMethod(Method.paradigmatic);
				work.close();
				changer.nextCard();
			}
			public void mouseEntered(MouseEvent e) {
				centerText.setIcon(null);
				centerText.setText("<html><font size=\"40\"><b>Paradigmatic Method</b></font><br/><br/>"
						+ "<p style=\"font-size:14px\" align=\"justify\">The classical method of Internal Reconstruction is the morphophonemic method. "
						+ "Depending on the source data, three types can can be distinguished. "
						+ "The paradigmatic approach works with alternations within a paradigm. "
						+ "The input file needs the structure <span style=\"font-size:16px;color:red;\">lemma&nbsp;&nbsp;&nbsp;&nbsp;part of speech&nbsp;&nbsp;&nbsp;&nbsp;allomorphs/wordforms</span>, e.g.:</p>"
						+ "<p style=\"font-size:16px\">sing&nbsp;&nbsp;&nbsp;&nbsp;Verb:&nbsp;&nbsp;&nbsp;&nbsp;sing&nbsp;&nbsp;&nbsp;&nbsp;sang&nbsp;&nbsp;&nbsp;&nbsp;sung</p>"
						+ "<p style=\"font-size:14px\" align=\"justify\">The wordforms and allomorphs should be separated by tabulators. The use of allomorphs performs better results. "
						+ "Alternatively, it is possible to extract the data from the German Wiktionary</html>");
				bgimage.setIcon(new ImageIcon(MethodCardGui.class.getClassLoader().getResource("images/Paradigmatic.png")));
			}
		});
		leftSide.add(paradigmatic);
		MenuTab derivational = new MenuTab("Derivational");
		derivational.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				work.setMethod(Method.derivational);
				work.close();
				changer.nextCard();

			}
			public void mouseEntered(MouseEvent e) {
				centerText.setIcon(null);
				centerText.setText("<html><font size=\"40\"><b>Derivational Method</b></font><br/><br/>"
						+ "<p style=\"font-size:14px\" align=\"justify\">The derivational approach is another type of the morphophonemic method. "
						+ "It works with derivatives and compound which contain alternating allomorphs."
						+ "The input file needs the structure </p><span style=\"font-size:16px;color:red;\">lemma&nbsp;&nbsp;&nbsp;&nbsp;part of speech&nbsp;&nbsp;&nbsp;&nbsp;allomorphs/wordforms</span><p style=\"font-size:14px\" align=\"justify\">, e.g.:</p>"
						+ "<p style=\"font-size:16px\">sing&nbsp;&nbsp;&nbsp;&nbsp;Verb:&nbsp;&nbsp;&nbsp;&nbsp;sing&nbsp;&nbsp;&nbsp;&nbsp;song</p>"
						+ "<p style=\"font-size:14px\" align=\"justify\">The wordforms and allomorphs should be separated by tabulators. It is recommendable to use allomorphs instead of wordforms. "
						+ "Alternatively, it is possible to extract the data from the German Wiktionary.</p></html>");
				bgimage.setIcon(new ImageIcon(MethodCardGui.class.getClassLoader().getResource("images/Derivational.png")));

			}

		});
		leftSide.add(derivational);
		MenuTab semantic = new MenuTab("Semantic");
		semantic.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				work.setMethod(Method.semantic);
				work.close();
				changer.nextCard();

			}
			public void mouseEntered(MouseEvent e) {
				centerText.setIcon(null);
				centerText.setText("<html><font size=\"40\"><b>Semantic Method</b></font><br/><br/>"
						+ "<p style=\"font-size:14px\" align=\"justify\">The semantic approach is the third type of the morphophonemic method. "
						+ "It searches for internal cognates within a semantic field. "
						+ "For this approach, a German ontology is used. "
						+ "The input file needs the structure <span style=\"font-size:16px;color:red;\">lemma&nbsp;&nbsp;&nbsp;&nbsp;meaning (in German)</span>, e.g.:</p>"
						+ "<p style=\"font-size:16px\">*perḱ-&nbsp;&nbsp;&nbsp;&nbsp;'graben, aufreißen'</p>"
						+ "<p style=\"font-size:14px\" align=\"justify\">It returns a list of the potential cognate pairs, a list of potential cognates for a specific word, and a list of significant sound alternations.</p>"
						+ "</html>");
				bgimage.setIcon(new ImageIcon(MethodCardGui.class.getClassLoader().getResource("images/Semantic.png")));
			}
		});
		leftSide.add(semantic);
		
		JLabel distributional = new JLabel("Distributional Methods"); 
		distributional.setForeground(new Color(139,0,0));
		distributional.setHorizontalAlignment(SwingConstants.CENTER);
		distributional.setVerticalAlignment(SwingConstants.BOTTOM);
		distributional.setFont(new Font("Segoe Print", Font.BOLD, 18));
		leftSide.add(distributional);
		MenuTab phonotactic = new MenuTab("Phonotactic");
		phonotactic.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				work.setMethod(Method.phonotactic);
				work.close();
				changer.nextCard();
			}
			public void mouseEntered(MouseEvent e) {
				centerText.setIcon(null);
				centerText.setText("<html><font size=\"40\"><b>Phonotactic Method</b></font><br/><br/>"
						+ "<p style=\"font-size:14px\" align=\"justify\">The phonotactic approach is one of the distributional method. "
						+ "It starts from the assumption that distributionally significantly divergent "
						+ "phonotagms in a language are the result of sound change. "
						+ "It searches for significantly relevant phonotagms within a corpus, morpheme list or word list (one word per line).</p>"
						+ "</html>");
				bgimage.setIcon(new ImageIcon(MethodCardGui.class.getClassLoader().getResource("images/Phonotactic.png")));
			}
		});
		leftSide.add(phonotactic);
		MenuTab distinctive = new MenuTab("Distinctive");
		distinctive.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				work.setMethod(Method.distinctive);
				work.close();
				changer.nextCard();
			}
			public void mouseEntered(MouseEvent e) {
				centerText.setIcon(null);
				centerText.setText("<html><font size=\"40\"><b>Distinctive Method</b></font><br/><br/>"
						+ "<p style=\"font-size:14px\" align=\"justify\">The distinctive approach is a variant of the phonotactic method. "
						+ "It works with minimal pairs instead of phonotagms. "
						+ "From the distinctiveness of the single sounds, statements about their sound \r\n"
						+ "classes and allophony could be derived, which are illustrated by k-means clustering. "
						+ "The input file should be a morpheme list or word list (one word per line).</p>"
						+ "</html>");
				bgimage.setIcon(new ImageIcon(MethodCardGui.class.getClassLoader().getResource("images/Distinctive.png")));
			}
		});
		leftSide.add(distinctive);
		MenuTab gap = new MenuTab("Gap Approach");
		gap.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				work.setMethod(Method.gap);
				work.close();
				changer.nextCard();
			}
			public void mouseEntered(MouseEvent e) {
				centerText.setIcon(null);
				centerText.setText("<html><font size=\"40\"><b>Gap Method</b></font><br/><br/>"
						+ "<p style=\"font-size:14px\" align=\"justify\">The gap approach is a popular distributional method. "
						+ "It searches for gaps within a phoneme system and tries to draw conclusions about historical mergers. "
						+ "The input file must be a list of phonemes and their distinctive features. It needs the structure</p>"
						+ "<p style=\"font-size:16px;color:red;\">No&nbsp;&nbsp;&nbsp;&nbsp;sound&nbsp;&nbsp;&nbsp;&nbsp;distinctive features</p>"
						+ "<p style=\"font-size:14px\" align=\"justify\">An example of such a line would be:</p>"
						+ "<p style=\"font-size:16px\">1&nbsp;&nbsp;&nbsp;&nbsp;a&nbsp;&nbsp;&nbsp;&nbsp;unrounded&nbsp;&nbsp;&nbsp;&nbsp;front&nbsp;&nbsp;&nbsp;&nbsp;vowel&nbsp;&nbsp;&nbsp;&nbsp;open</p>"
						+ "<p style=\"font-size:14px\" align=\"justify\">The distinctive features should be separated by tabulators.</p>"
						+ "</html>");
				bgimage.setIcon(new ImageIcon(MethodCardGui.class.getClassLoader().getResource("images/Gap.png")));
			}
		});
		leftSide.add(gap);
		this.panel.add(leftSide,BorderLayout.LINE_START);
		this.panel.add(bgimage,BorderLayout.LINE_END);
		

		JLabel bottomBg = new JLabel();
		bottomBg.setLayout(new GridLayout(1,4));
		bottomBg.setPreferredSize(new Dimension(frameWidth,25));
		bottomBg.setBackground(new Color(238,238,238));
		JLabel leftWhite = new JLabel();
		leftWhite.setOpaque(true);
		leftWhite.setBackground(Color.WHITE);
		bottomBg.add(leftWhite);
		bottomBg.add(new JLabel());
		bottomBg.add(new JLabel());
		bottomBg.add(new JLabel());
		this.panel.add(bottomBg, BorderLayout.PAGE_END);
    	
    }



}

