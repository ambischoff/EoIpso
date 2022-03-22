package Frame;

import java.awt.Component;

import App.InternalReconstruction;

/**
 * interface for card GUIs
 *
 */
public interface CardGui {

	/**
	 * generates a GUI
	 * @param changer	current instance of the app
	 * @return 
	 */
    Component generateGui(InternalReconstruction changer);

    /**
     * setter for the CardWork
     * @param work
     */
    void setWork(CardWork work);
    

}