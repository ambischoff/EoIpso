package Frame;

/**
 *  interface for the backend work of a card
 *
 */
public interface CardWork {

    /**
     * setter for the result of the previous card
     * @param previousResult
     */
    void setPreviousResult(Result previousResult);

    /**
     * runs when shown
     */
    void runWhenShown();

    /**
     * getter for the result object
     * @return	result as result object
     */
    Result getResult();

}