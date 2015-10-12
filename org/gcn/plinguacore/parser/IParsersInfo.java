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

import java.util.Iterator;

/**
 * An interface to represent public information about available parsers
 * 
 * @author Research Group on Natural Computing (http://www.gcn.us.es)
 */

public interface IParsersInfo {

	/**
	 * Gets the file extension of the format identified by the ID passed as
	 * argument
	 * 
	 * @param formatID
	 *            the ID of the format file whose file extension is required
	 * @return the file extension of the format file identified by the ID passed
	 *         as argument
	 * @throws IllegalArgumentException
	 *             if the formatID is not a recognized format
	 */
	public String getFileExtension(String formatID);

	/**
	 * Gets the command-line option of the format file identified by the ID
	 * passed as argument
	 * 
	 * @param formatID
	 *            the ID of the format file whose command-line option is
	 *            required
	 * @return the command-line option of the format file identified by the ID
	 *         passed as argument
	 * @throws IllegalArgumentException
	 *             if the formatID is not a recognized format
	 */

	public String getCommandLineOption(String formatID);

	/**
	 * Gets the ID of the format file whose file extension is passed as argument
	 * 
	 * @param extension
	 *            the extension whose format file ID is required
	 * @return the ID of the format file identified by the extension passed as
	 *         argument
	 * @throws IllegalArgumentException
	 *             if the extension is not a recognized file extension
	 */

	public String getFormatByExtension(String extension);

	/**
	 * Gets the ID of the format file whose command-line option is passed as
	 * argument
	 * 
	 * @param option
	 *            the command-line option whose format file ID is required
	 * @return the ID of the format file identified by the command-line option
	 *         passed as argument
	 * @throws IllegalArgumentException
	 *             if the option is not a recognized command-line option
	 * 
	 */

	public String getFormatByCommandLineOption(String option);

	/**
	 * Check if there is any format file for a file extension
	 * 
	 * @param extension
	 *            a file extension
	 * @return true if there is a format file for the file extension passed as
	 *         argument
	 */

	public boolean hasFileExtension(String extension);

	/**
	 * Check if exists an specific format file
	 * 
	 * @param format
	 *            ID The ID of the format file
	 * @return true if exists the format file passed as argument
	 */

	public boolean hasFormat(String formatID);

	/**
	 * Check if exists a specific input format file
	 * 
	 * @param format
	 *            ID The ID of the input format file
	 * @return true if exists the input format file passed as argument
	 */

	public boolean hasInputFormat(String formatID);

	/**
	 * Check if exists a specific output format file
	 * 
	 * @param format
	 *            ID The ID of the output format file
	 * @return true if exists the output format file passed as argument
	 */

	public boolean hasOutputFormat(String formatID);

	/**
	 * Check if exists a specific command-line option
	 * 
	 * @param option
	 *            a command-line option
	 * @return true if exists the command-line option passed as argument
	 */
	public boolean hasCommandLineOption(String option);

	/**
	 * Gets an iterator on the existent input formats IDs
	 * 
	 * @return an iterator on the existent input formats IDs
	 */
	public Iterator<String> getInputFormatsIterator();

	/**
	 * Gets an iterator on the existent command-line options
	 * 
	 * @return an iterator on the existent command-line options
	 */
	public Iterator<String> getCommandLineOptionsIterator();

	public Iterator<String> getFileExtensionsIterator();

	/**
	 * Gets an iterator on the existent output formats IDs
	 * 
	 * @return an iterator on the existent output formats IDs
	 */
	public Iterator<String> getOutputFormatsIterator();

	/**
	 * Gets an iterator over the recognized IDs
	 * 
	 * @return an iterator over the recognized IDs
	 */
	public Iterator<String> getFormatsIterator();

	/**
	 * Checks if there's any input recognized format for a file extension
	 * 
	 * @param fileExtension
	 *            the file extension whose input format will be sought
	 * @return true if there's any recognized format for fileExtension, false
	 *         otherwise
	 */
	public boolean hasInputRecognizedFormat(String fileExtension);

}
