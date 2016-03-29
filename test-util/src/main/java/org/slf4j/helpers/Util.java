package org.slf4j.helpers;

/**
 * An internal utility class.
 * 
 * @author Ceki G&uuml;lc&uuml;, Роман Ержуков I-Teco 26.09.2011
 */
public class Util {

	static final public void report(String msg, Throwable t) {
		System.err.println(msg);
		System.err.println("Reported exception:");
		t.printStackTrace();
	}

	static final public void report(String msg) {
		// В тестовом classpath-е используется несколько binding-ов для slf4j:
		// slf4j-log4j12, для тестов, и slf4j-jdk14, для прома, в ear
		// Чтоб при запуске тестов не писался warninig по этому поводу:
		// System.err.println("SLF4J: " + msg);
	}
}
