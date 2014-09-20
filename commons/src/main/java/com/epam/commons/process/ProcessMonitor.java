package com.epam.commons.process;

/**
 *
 * @author Александр
 */
public interface ProcessMonitor<T> {
	
	void onStart();
	
	void onStep(T data);
	
	void onFail(Exception ex);
	
	void onEnd();
}
