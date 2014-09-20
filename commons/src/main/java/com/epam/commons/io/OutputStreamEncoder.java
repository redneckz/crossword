package com.epam.commons.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Александр
 */
public interface OutputStreamEncoder<T> {
	
	void encode(T obj, OutputStream output, String encoding) throws IOException;
}
