package com.epam.commons.process;

/**
 *
 * @author Александр
 */
public interface LongProcess<T> {
	
	void registerMonitor(ProcessMonitor<T> monitor);
	
	void unregisterMonitor(ProcessMonitor<T> monitor);
}
