package com.epam.crossword.dictionary;

import com.epam.commons.process.LongProcess;
import com.epam.commons.process.ProcessHelper;
import com.epam.commons.process.ProcessMonitor;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author Alexander_Alexandrov
 */
public final class XDXFDictionaryCreator implements DictionaryCreator<InputStream>, LongProcess<String> {
	
	private final ProcessHelper<String> processHelper;

	public XDXFDictionaryCreator() {
		processHelper = new ProcessHelper<String>();
	}
	
	@Override
	public Dictionary create(final InputStream input) {
		processHelper.fireStart();
		try {
			XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(input);
			try {
				Collection<String> items = new LinkedList<String>();
				while (reader.hasNext()) {
					reader.next();
					if ((XMLStreamConstants.START_ELEMENT != reader.getEventType()) || !"k".equals(reader.getLocalName())) {
						continue;
					}
					String item = reader.getElementText().toLowerCase();
					if (item.contains("-") || item.contains(" ")) {
						continue;
					}
					items.add(item);
					processHelper.fireStep(item);
				}
				processHelper.fireStep(null);
				Dictionary result = IndexedDictionaryCreator.getInst().create(items);
				processHelper.fireEnd();
				return result;
			} finally {
				reader.close();
			}
		} catch(Exception ex) {
			processHelper.fireFail(ex);
		} finally {
			try {
				input.close();
			} catch (IOException ex) {
			}
		}
		return Dictionary.NIL;
	}

	@Override
	public void registerMonitor(ProcessMonitor<String> monitor) {
		processHelper.registerMonitor(monitor);
	}

	@Override
	public void unregisterMonitor(ProcessMonitor<String> monitor) {
		processHelper.unregisterMonitor(monitor);
	}
}
