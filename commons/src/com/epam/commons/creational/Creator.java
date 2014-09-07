package com.epam.commons.creational;

/**
 *
 * @author Александр
 */
public interface Creator<A, B> {
	
	B create(A params);
}
