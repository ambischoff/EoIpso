package Frame;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

import App.InternalReconstruction;
import Helper.Log.Logging;

/**
 * abstract class for card decks
 *
 */
public abstract class CardDeck {

    private final List<Card> cards;

    protected CardDeck() {
        this.cards = new ArrayList<Card>();
    }

    /**
     * getter for the first card
     * @return the first card as Card object
     */
    protected Card getFirstCard() {
    	if (this.cards != null) {
    		return this.cards.get(0);
    	}
    	return null;
    }
    
    /**
     * adds a card to the deck
     * @param card	card object
     */
    protected void addCard(Card card) {
    	this.cards.add(card);
    }

    /**
     * returns the next card given the current card
     * @param thisCard	current card
     * @return the next card
     */
    protected Card getNextCard(String thisCard) {

    	for (int i = 0 ; i < this.cards.size() ; i++) {
    		if (this.cards.get(i).getID().contentEquals(thisCard)) {
    			if (i+1 < this.cards.size()) {
    				return this.cards.get(i+1);
    			} else {
    				Logging.error("No card left.");
    			}
    		}
    	}
        Logging.error("Card not found!");
        return null;
    }

    /**
     * adds the cards of the current deck to the JPanel
     * @param panel		JPanel
     * @param changer	current instance of the app
     */
    protected void addCardsToPanel(JPanel panel, InternalReconstruction changer) {
        for (int i = 0 ; i < this.cards.size() ; i++) {
        	Card card = this.cards.get(i);
            panel.add(card.generateCardGui(changer), card.getID());
        }
    }



}