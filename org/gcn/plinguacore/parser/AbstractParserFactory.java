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
 * along with pLinguaCore. If not, see <http://www.gnu.org/licenses/>.
 */

package org.gcn.plinguacore.parser;



import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.PlinguaPanicError;



/**
 * This class is intended to be extended by InputParserFactory and
 * OutputParserFactory
 * 
 * @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */

public abstract class AbstractParserFactory {
	
	private static IParsersClassProvider singletonParserClassProvider=null ;
	
	/**
	 * Creates a new AbstractParserFactory instance
	 */
	public AbstractParserFactory(){
		super();
	}
	
	/**
	 * Return general information about available parsers
	 * @return an IParsersInfo instance with general information about available parsers
	 */
	
	public final static IParsersInfo getParserInfo()
	{
		return getParserClassProvider();
	}
	
	/**
	 * Return a class name provider for available parsers
	 * @return an IParserClassProvider instance for available parsers
	 */
	
	protected final static IParsersClassProvider getParserClassProvider()
	{
		if (singletonParserClassProvider==null)
			singletonParserClassProvider = new XmlParsersResource();
		return singletonParserClassProvider;
	}

	private final static IParser createParserFromClassName(String className)
			throws PlinguaCoreException {

		/* The parser (whether input or output) is created by using refection */
		IParser result = null;
		try {
			Class<?> c = Class.forName(className);
			result = (IParser) c.newInstance();
		} catch (ClassNotFoundException e) {
		

			throw new PlinguaPanicError(e.getMessage());
		} catch (InstantiationException e) {
			
			throw new PlinguaPanicError(e.getMessage());
		} catch (IllegalAccessException e) {
		
			throw new PlinguaPanicError(e.getMessage());
		}
		return result;
	}
	/**
	 * Creates a IParser instance according to the given format
	 * 
	 * @param className
	 *            the format name of the parser to create
	 * @return a proper IParser instance according to the given format
	 * @throws PlinguaCoreException
	 *             if the format is not supported
	 */
	public final IParser createParser(String formatName) throws PlinguaCoreException{
		return createParserFromClassName(getClassName(formatName));
		
	}
	
	/**
	 * Looks if there's any input format whose files correspond to the extension given and returns an input format which parses that format
	 * @param fileExtension the extension which input format class name should be returned
	 * @return the input format instance which corresponds to the file extension given
	 * @throws PlinguaCoreException if there's no recognized input parser for the extension given
	 */
	public IParser createInputParserThroughExtension(String fileExtension) throws PlinguaCoreException{
		
		return createParserFromClassName(getParserClassProvider().getInputFormatClassNameThroughExtension(fileExtension));
	}
		

	protected abstract String getClassName(String formatName)
			throws PlinguaCoreException;

}
