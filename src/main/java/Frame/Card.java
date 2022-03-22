package Frame;

import java.awt.Component;

import App.InternalReconstruction;

/**
 * object for a card of the CardLayout
 *
 */
public class Card {

    private final String identifier;
    private final CardGui gui;
    private final CardWork work;

    /**
     * 
     * @param id	name of the Card
     * @param work	corresponding CardWork
     * @param gui	corresponding CardGui
     */
    public Card(String id, CardWork work, CardGui gui) {
        this.identifier = id;
        this.work = work;
        this.gui = gui;
        this.gui.setWork(work);

    }
    
    /**
     * sets the result from the previous card to the CardWork object of this card
     * 
     * @param previousCard	previous Card
     */
    public void runWhenShown(Card previousCard) {
    	CardWork previousWork = previousCard.work;
        Result result = previousWork.getResult();
        this.work.setPreviousResult(result);
        this.work.runWhenShown();
    }

    /**
     * generates a CardGui
     * @param changer	current instance of the app
     * @return	gui component
     */
    public Component generateCardGui(InternalReconstruction changer) {
        return this.gui.generateGui(changer);
    }
   
    /**
     * Getter for the id of the card
     * @return
     */
    public String getID() {
        return this.identifier;
    }


}