/* 
 * pLinguaCore: A JAVA library for Membrane Computing
 *              http://www.p-lingua.org
 *
 * Copyright (C) 2009  Research Group on Natural Computing
 *                     http://www.gcn.us.es
 *                      
 * This file is part of pLinguaCore.
 *
 * pLinguaCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pLinguaCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pLinguaCore.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gcn.plinguacore.parser;

import java.util.ArrayList;
import java.util.Collections;

import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;

import org.gcn.plinguacore.parser.input.InputParser;
import org.gcn.plinguacore.parser.output.OutputParser;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.XmlFileIDs;
import org.jdom.Document;
import org.jdom.Element;

/**
 * This class manages everything related to formats and parsers which parse
 * those formats
 * 
 * @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
class XmlParsersResource extends XmlFileIDs implements IParsersClassProvider {

	private List<String> formatIDs;
	private List<String> fileExtensions;
	private List<String> commandLineOptions;

	private static final String inputFormatsStr = "input_formats";
	private static final String outputFormatsStr = "output_formats";
	private static final String PARSER_FORMATS_ROUTE = "resources/original/";
	private static final String PARSER_FORMATS_FILENAME = "formats.xml";

	public XmlParsersResource() {
		super(PARSER_FORMATS_ROUTE + PARSER_FORMATS_FILENAME);

	}

	@Override
	protected void readDocument(Document doc) {

		formatIDs = new ArrayList<String>();
		fileExtensions = new ArrayList<String>();
		commandLineOptions = new ArrayList<String>();

		Element root = doc.getRootElement();
		if (!root.getName().equals("formats"))
			throwFileException();
		/*
		 * This section adds all input and output formats declared on the
		 * document
		 */
		ListIterator<?> formatElementsIterator = root.getChildren()
				.listIterator();
		Element inputElement = (Element) formatElementsIterator.next();
		addFormats(inputFormatsStr, InputParser.class, inputElement);
		Element outputElement = (Element) formatElementsIterator.next();
		addFormats(outputFormatsStr, OutputParser.class, outputElement);
		if (formatElementsIterator.hasNext())
			throwFileException();

	}

	private void addFormats(String childName, Class<?> parserClass,
			Element element) {
		addID(element, childName, "format", parserClass);
		ListIterator<?> formatIDSIterator = element.getChildren()
				.listIterator();
		addCompilerFormats(formatIDSIterator);
	}

	@Override
	public Iterator<String> getFormatsIterator() {
		return Collections.unmodifiableList(formatIDs).iterator();
	}

	@Override
	public String getFileExtension(String formatID) {
		int index = formatIDs.indexOf(formatID);
		if (index < 0)
			throw new IllegalArgumentException(
					"The format ID passed as parameter does not match any recognized format");
		return fileExtensions.get(index);
	}

	private void addCompilerFormats(ListIterator<?> formatIDSIterator) {
		while (formatIDSIterator.hasNext()) {
			Element elem = (Element) formatIDSIterator.next();
			String formatID = elem.getAttributeValue("id");
			String option = elem.getAttributeValue("option");
			/* The file extension (related to the specific ID) is read */
			String extension = elem.getAttributeValue("extension");

			if (option == null)
				option = "";
			if (extension == null)
				extension = "";

			if (!formatIDs.contains(formatID)) {
				formatIDs.add(formatID);
				fileExtensions.add(extension);
				commandLineOptions.add(option);
			}

		}
	}

	@Override
	public Iterator<String> getOutputFormatsIterator() {

		return inmutableIterator(outputFormatsStr);
	}

	@Override
	public Iterator<String> getInputFormatsIterator() {

		return inmutableIterator(inputFormatsStr);
	}

	@Override
	public String getOutputFormatClassName(String format)
			throws PlinguaCoreException {

		try {
			return idClassName(format, outputFormatsStr);
		} catch (PlinguaCoreException e) {

			throw new PlinguaCoreException("Output format " + format
					+ " not supported");
		}

	}

	@Override
	public String getInputFormatClassName(String format)
			throws PlinguaCoreException {
		try {
			return idClassName(format, inputFormatsStr);
		} catch (PlinguaCoreException e) {
			throw new PlinguaCoreException("Input format " + format
					+ " not supported");
		}

	}

	@Override
	public String getCommandLineOption(String formatID) {

		int index = formatIDs.indexOf(formatID);

		if (index < 0)
			throw new IllegalArgumentException(
					"The format ID passed as parameter does not match any recognized format");
		return commandLineOptions.get(index);
	}

	@Override
	public String getFormatByCommandLineOption(String option) {
		int index = commandLineOptions.indexOf(option);

		if (index < 0)
			throw new IllegalArgumentException(
					"The command-line option passed as parameter does not match any recognized format");
		return formatIDs.get(index);
	}

	@Override
	public String getFormatByExtension(String extension) {
		int index = fileExtensions.indexOf(extension);

		if (index < 0)
			throw new IllegalArgumentException(
					"The file extension passed as parameter does not match any recognized format");
		return formatIDs.get(index);
	}

	@Override
	public Iterator<String> getCommandLineOptionsIterator() {

		return Collections.unmodifiableList(commandLineOptions).iterator();
	}

	@Override
	public Iterator<String> getFileExtensionsIterator() {

		return Collections.unmodifiableList(fileExtensions).iterator();
	}

	@Override
	public boolean hasCommandLineOption(String option) {

		return commandLineOptions.contains(option);
	}

	@Override
	public boolean hasFileExtension(String extension) {

		return fileExtensions.contains(extension);
	}

	@Override
	public boolean hasFormat(String formatID) {

		return formatIDs.contains(formatID);
	}

	@Override
	public boolean hasInputFormat(String formatID) {

		Iterator<String> it = getInputFormatsIterator();
		while (it.hasNext()) {
			if (formatID.equals(it.next()))
				return true;
		}
		return false;
	}

	@Override
	public boolean hasOutputFormat(String formatID) {

		Iterator<String> it = getOutputFormatsIterator();
		while (it.hasNext()) {
			if (formatID.equals(it.next()))
				return true;
		}
		return false;
	}

	@Override
	public String getInputFormatClassNameThroughExtension(String extension)
			throws PlinguaCoreException {
		// TODO Auto-generated method stub
		String formatID = getFormatByExtension(extension);
		return getInputFormatClassName(formatID);
	}

	@Override
	public boolean hasInputRecognizedFormat(String extension) {
		// TODO Auto-generated method stub
		String formatID = getFormatByExtension(extension);
		return hasInputFormat(formatID);
	}

}
