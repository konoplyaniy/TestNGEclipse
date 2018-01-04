package utils;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static base.BasicTest.stepsCount;

public class LogForTest {

	public static final Logger LOGGER = LogManager.getLogger(LogForTest.class);
	private static final String INFO_LOG = "INFO: %s";
	private static final String ERROR_LOG = "ERROR: %s !";

	private static ArrayList<String> errorLog;
	private static ArrayList<String> infoLog;
	private static ArrayList<String> headerLog;

	public static void resetLogLists() {
		headerLog = new ArrayList<>();
		infoLog = new ArrayList<>();
		errorLog = new ArrayList<>();
	}

	public static ArrayList<String> getErrorLog() {
		return errorLog;
	}

	public static ArrayList<String> getInfoLog() {
		return infoLog;
	}

	public static ArrayList<String> getHeaderLog() {
		return headerLog;
	}

	public static String header(String message) {
		if (headerLog ==null || infoLog== null || errorLog == null) {
			resetLogLists();
		}		
		LOGGER.info(String.format(INFO_LOG, message));		
		headerLog.add(message + "\n");
		return String.format(INFO_LOG, message);
	}

	public static String info(String message) {
		if (headerLog ==null || infoLog== null || errorLog == null) {
			resetLogLists();
		}
		LOGGER.info(String.format(INFO_LOG, stepsCount + ") " + message));
		infoLog.add(stepsCount + ") " + message + "\n");
		stepsCount++;
		return String.format(INFO_LOG, stepsCount + ") " + message);
	}

	public static String error(String message) {
		if (headerLog ==null || infoLog== null || errorLog == null) {
			resetLogLists();
		}
		LOGGER.error(String.format(ERROR_LOG, message));		
		errorLog.add(message + "\n");
		return String.format(ERROR_LOG, message);
	}

	public static String getFullTestLog() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Additional info\n");
		headerLog.forEach(stringBuilder::append);
		stringBuilder.append("Steps to reproduce:\n");
		infoLog.forEach(stringBuilder::append);
		stringBuilder.append("What went wrong? (Screenshot(s) in attachment):\n");
		errorLog.forEach(stringBuilder::append);
		return stringBuilder.toString();
	}
}
