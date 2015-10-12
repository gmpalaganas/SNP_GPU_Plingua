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

package org.gcn.plinguacore.parser.input;

import java.io.InputStream;
import java.io.StringReader;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gcn.plinguacore.parser.IParser;
import org.gcn.plinguacore.parser.input.messages.InputParserMsg;
import org.gcn.plinguacore.parser.input.messages.InputParserMsgFactory;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.checkPsystem.CheckPsystem;
import org.gcn.plinguacore.util.psystem.factory.AbstractPsystemFactory;
import org.gcn.plinguacore.util.psystem.factory.IPsystemFactory;



/**
 * This class provides the common functionality for all input parsers
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public abstract class InputParser implements IParser {

	private PrintStream errorChannel = System.err;

	private PrintStream warningChannel = System.out;

	private PrintStream infoChannel = System.out;

	private PrintStream msgChannel = System.out;

	private int verbosityLevel = 3;

	private Map<String, Set<InputParserMsg>> report;

	private boolean error = false;

	private String[] fileRoutes;

	/**
	 * Creates a new InputParser instance
	 */
	public InputParser() {
		report = new HashMap<String, Set<InputParserMsg>>();
	}

	/**
	 * Gets the errors channel
	 * 
	 * @return the PrintStream instance to write errors
	 */
	public PrintStream getErrorChannel() {
		return errorChannel;
	}

	/**
	 * Sets the PrintStream instance to write errors
	 * 
	 * @param errorChannel
	 *            the PrintStream instance to write errors
	 */
	public void setErrorChannel(PrintStream errorChannel) {
		this.errorChannel = errorChannel;
	}

	/**
	 * Gets the PrintStream instance to write information
	 * 
	 * @return the PrintStream instance to write information
	 */
	public PrintStream getInfoChannel() {
		return infoChannel;
	}

	protected static IPsystemFactory createAbstractPsystemFactory(
			String modelName) throws PlinguaCoreException {
		/*
		 * We delegate on PsystemFactory class for creating PSystemFactory
		 * instances based on a model name
		 */
		return AbstractPsystemFactory.createAbstractPsystemFactory(modelName);
	}

	/**
	 * Sets the PrintStream to write information
	 * 
	 * @param infoChannel
	 *            the PrintStream instance to write information
	 */
	public void setInfoChannel(PrintStream infoChannel) {
		this.infoChannel = infoChannel;
	}

	/**
	 * Gets the PrintStream instance to write warnings
	 * 
	 * @return The PrintStream instance to write warnings
	 */
	public PrintStream getWarningChannel() {
		return warningChannel;
	}

	/**
	 * Sets the PrintStream instance to write warnings
	 * 
	 * @param warningChannel
	 *            the PrintStream instance to write warnings
	 */
	public void setWarningChannel(PrintStream warningChannel) {
		this.warningChannel = warningChannel;
	}

	/**
	 * Gets the verbosity level
	 * 
	 * @return the verbosity level (usually between 0-5)
	 */
	public int getVerbosityLevel() {
		return verbosityLevel;
	}

	/**
	 * Sets the verbosity level (usually between 0-5)
	 * 
	 * @param verbosityLevel
	 *            the verbosity level
	 */
	public void setVerbosityLevel(int verbosityLevel) {
		this.verbosityLevel = verbosityLevel;
	}

	/**
	 * Gets the PrintStream instance to write messages
	 * 
	 * @return The PrintStream instance to write messages
	 */
	public PrintStream getMsgChannel() {
		return msgChannel;
	}

	/**
	 * Sets the PrintStream instance to write messages
	 * 
	 * @param msgChannel
	 *            the PrintStream instance to write messages
	 */
	public void setMsgChannel(PrintStream msgChannel) {
		this.msgChannel = msgChannel;
	}

	/**
	 * Parses an InputStream instance and generates a new P system
	 * 
	 * @param stream
	 *            the stream to be parsed
	 * @param fileRoutes
	 *            an array containing all file routes for each file which
	 *            compose the stream
	 * @return a new P system or null if it has errors
	 * @throws PlinguaCoreException
	 *             if the parsing couldn't be committed
	 */
	public final Psystem parse(InputStream stream, String fileRoutes[])
			throws PlinguaCoreException {
		initFileRoutes(fileRoutes);
		resetReport();
		Psystem ps= specificParse(stream, fileRoutes);
		
			
		checkPsystem(ps);
		return ps;
	}
	
	private  void checkPsystem(Psystem ps) throws PlinguaCoreException
	{
		if (ps==null)
			return;
		if (ps.getAbstractPsystemFactory()==null)
			return;
		CheckPsystem cps= ps.getAbstractPsystemFactory().getCheckPsystem();
		if (cps==null)
			return;
		
		if (!cps.checkPsystem(ps))
		{
			
			writeMsg(InputParserMsgFactory.createSemanticsErrorMessage(cps.getCausesString()));
			throw new PlinguaCoreException("Parser process finished with errors");
		}
				
		
	}

	/**
	 * Parses an InputStream instance and generates a new P system without any
	 * file routes
	 * 
	 * @param stream
	 *            the stream to be parsed
	 * @return a new P system or null if it has errors
	 * @throws PlinguaCoreException
	 *             if the parsing couldn't be committed
	 */
	public final Psystem parse(InputStream stream) throws PlinguaCoreException {

		return parse(stream, new String[] { "" });
	}

	protected abstract Psystem specificParse(InputStream stream,
			String[] fileRoutes) throws PlinguaCoreException;

	// TODO Auto-generated method stub

	/**
	 * Parses a StringReader instance and generates a new P system without any
	 * file routes
	 * 
	 * @param reader
	 *            the StringReader instance to be parsed
	 * @param fileRoutes
	 *            an array containing all file routes for each file which
	 *            compose the stream
	 * @return a new P system or null if it has errors
	 * @throws PlinguaCoreException
	 *             if the parsing couldn't be committed
	 */
	public Psystem parse(StringReader reader, String fileRoutes[])
			throws PlinguaCoreException {
		initFileRoutes(fileRoutes);
		resetReport();
		Psystem ps= specificParse(reader, fileRoutes);
		checkPsystem(ps);
		return ps;
		
	}

	/**
	 * Parses an StringReader instance and generates a new P system without any
	 * file routes
	 * 
	 * @param reader
	 *            the StringReader instance to be parsed
	 * @return a new P system or null if it has errors
	 * @throws PlinguaCoreException
	 *             if the parsing couldn't be committed
	 */
	public final Psystem parse(StringReader reader) throws PlinguaCoreException {

		return parse(reader, new String[] { "" });
	}

	/**
	 * Parses a string reader according to the specific implementation of
	 * InputParser
	 * 
	 * @param reader
	 *            the reader to be parsed
	 * @param fileRoutes
	 *            the routes of the files to be parsed
	 * @return the Psystem instance parsed
	 * */
	protected abstract Psystem specificParse(StringReader reader,
			String[] fileRoutes) throws PlinguaCoreException;

	private void initFileRoutes(String[] fileRoutes) {
		if (fileRoutes == null)
			throw new NullPointerException(
					"fileRoutes argument shouldn't be null");
		this.fileRoutes = fileRoutes;
		// TODO Auto-generated method stub

	}

	/**
	 * Returns if the parsing process detected any error
	 * 
	 * @return true if the parsing process detected any error, false otherwise
	 */
	public boolean hasErrors() {
		return error;
	}

	/**
	 * Gets the report map of the last parsing
	 * 
	 * @return the report map of the last parsing
	 */
	public Map<String, Set<InputParserMsg>> getReport() {
		return Collections.unmodifiableMap(report);
	}

	protected void resetReport() {
		error = false;
		report.clear();
		for (int i = 0; i < fileRoutes.length; i++)
			report.put(fileRoutes[i], new HashSet<InputParserMsg>());

	}

	protected void writeMsg(InputParserMsg msg) {
		writeMsg(msg, report.keySet().iterator().next());
	}

	protected void writeMsg(InputParserMsg msg, String fileRoute) {

		if (msg.getType().toUpperCase().contains("ERROR"))
			error = true;

		report.get(fileRoute).add(msg);

		if (msg.getVerbosityLevel() <= verbosityLevel) {
			PrintStream stream;
			if (msg.getType().toUpperCase().contains("ERROR"))
				stream = errorChannel;
			else if (msg.getType().toUpperCase().contains("WARNING"))
				stream = warningChannel;
			else
				stream = infoChannel;
			stream.println(msg.toString());
		}

	}

}
