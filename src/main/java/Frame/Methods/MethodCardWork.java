package Frame.Methods;

import Frame.CardWork;
import Frame.Result;
import Helper.Language.Language;
import Helper.Language.Method;

/**
 * CardWork for the MethodCard
 * @author abischoff
 *
 */
public class MethodCardWork implements CardWork {

    private Result result = new Result();
    private Language myLanguage = new Language("","");

    /**
     * nothing to do
     */
    @Override
    public void setPreviousResult(Result previousResult) {}

    /**
     * nothing to do
     */
    @Override
    public void runWhenShown() {}
    
    /**
     * Getter for the result
     * @return Result
     */
    @Override
    public Result getResult() {
        return result;
    }
	
	/**
	 * sets the result
	 */
	public void close() {
		this.result.setResult(myLanguage);
	}

	/**
	 * getter for the Language object
	 * @return language
	 */
	public Language getLanguage() {
		return this.myLanguage;
	}

	/**
	 * setter for the choosen method
	 * @param inputMethod as Method object
	 */
	public void setMethod(Method inputMethod) {
		this.myLanguage.setMethod(inputMethod);
	}
	
	
}