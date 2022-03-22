package Frame;

import Frame.Drop.DropCardGui;
import Frame.Drop.DropCardWork;
import Frame.Execute.ExecuteCardGui;
import Frame.Execute.ExecuteCardWork;
import Frame.Methods.MethodCardGui;
import Frame.Methods.MethodCardWork;
import Frame.Start.StartCardGui;
import Frame.Start.StartCardWork;
import Helper.Language.Language;

/**
 * card decks
 *
 */
public class MyCardDeck extends CardDeck {
	
	/**
	 * first card deck
	 */
    public MyCardDeck() {
    	addCard(new Card("Method", new MethodCardWork(), new MethodCardGui()));
       	addCard(new Card("Drop", new DropCardWork(), new DropCardGui()));
    }
    
    /**
     * second card deck
     * @param result	Result of the first deck
     */
    public MyCardDeck(Result result) {

    	addCard(new Card("Start", new StartCardWork(result), new StartCardGui(result)));
    }

    /**
     * third card deck
     * @param lang	result of the second deck as Language object
     */
    public MyCardDeck(Language lang) {
    	addCard(new Card("Execute", new ExecuteCardWork(lang), new ExecuteCardGui(lang)));
    }
}