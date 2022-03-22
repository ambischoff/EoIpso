package Frame;

import java.awt.CardLayout;
import javax.swing.JPanel;

import App.InternalReconstruction;

/**
 * class to handle the change of cards
 *
 */
public class CardLayoutServer {

    private final CardLayout myCardLayout;
    private final JPanel cardPanel;
    private final CardDeck myCards;
    private Card currentCard;

    /**
     * 
     * @param cards	deck of cards
     */
    public CardLayoutServer(CardDeck cards) {
        this.myCards = cards;
        this.cardPanel = new JPanel();
        this.myCardLayout = new CardLayout();
        this.cardPanel.setLayout(this.myCardLayout);
        this.currentCard = this.myCards.getFirstCard();
    }
    
    /**
     * adds all cards of the current instance to the panel
     * @param changer	current instance of the app
     */
    public void addAllCards(InternalReconstruction changer) {
        this.myCards.addCardsToPanel(cardPanel, changer);
    }
    
    /**
     * switches to the next
     */
    public void nextCard() {
        Card previousCard = this.currentCard;

        this.currentCard = this.myCards.getNextCard(previousCard.getID());
        this.currentCard.runWhenShown(previousCard);
        this.myCardLayout.show(this.cardPanel, this.currentCard.getID());

        this.cardPanel.validate();
    }
    
    /**
     * Getter for the panel
     * @return	JPanel
     */
    public JPanel getPanel() {
        return cardPanel;
    }

}