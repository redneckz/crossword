package com.epam.commons.io;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Alexander_Alexandrov
 */
public interface InputStreamDecoder<T> {
	
	T decode(InputStream input, String encoding) throws IOException;
}
