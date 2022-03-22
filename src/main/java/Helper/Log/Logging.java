package Helper.Log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * class for the logger
 * @author abischoff
 *
 */
public class Logging {

	private static final Logger logger = LogManager.getLogger(Logging.class);
	private static LogFrame frame = new LogFrame();
	private static boolean frameVisible = false;
	
	/**
	 * @param msg debug message
	 */
	public static void debug(String msg) {
		if (frameVisible) {
			frame.printProgress(msg);
		}
		logger.debug(msg);
	}
	
	/**
	 * @param msg error message
	 */
	public static void error(String msg) {
		logger.error(msg);
	}
	
	/**
	 * @param msg fatal message
	 */
	public static void fatal(String msg) {
		logger.fatal(msg);
	}
	
	/**
	 * @param msg info message
	 */
	public static void info(String msg) {
		if (frameVisible) {
			frame.printProgress(msg);
		}
		logger.info(msg);
	}
	
	/**
	 * @param msg warn message
	 */
	public static void warn(String msg) {
		if (frameVisible) {
			frame.printProgress(msg);
		}
		logger.warn(msg);
	}
	
	/**
	 * makes the log frame visible
	 */
	public static void openProcessWindow() {
		frameVisible = true;
		frame.makeVisible();
	}

	/**
	 * makes the log frame invisible
	 */
	public static void closeProcessWindow() {
		frameVisible = false;
	}
	
}
