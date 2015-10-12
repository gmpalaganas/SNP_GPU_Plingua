package org.gcn.plinguacore.parser;

import org.gcn.plinguacore.util.PlinguaCoreException;

/**
* An interface to represent the class name provider for AbstractParserFactory.
* It is intended to be used only by AbstractParserFactory, in order to hide the resources
* layer from the user.
* 
* @author Research Group on Natural Computing (http://www.gcn.us.es)
*/

public interface IParsersClassProvider extends IParsersInfo{
	
	/**
	 * Gets the name of the InputParser subclass for the format
	 * 
	 * @param formatID
	 *            the format which InputParser subclass name is required
	 * @return the name of the Parser class for a specific input format
	 * @throws PlinguaCoreException
	 *             if the given format hasn't a IntputParser subclass
	 */
	public String getInputFormatClassName(String formatID)
			throws PlinguaCoreException;
	

	/**
	 * Gets the name of the OutputParser subclass for the format
	 * 
	 * @param formatID
	 *            the format which OutputParser subclass name is required
	 * @return the name of the Parser class for a specific output format
	 * @throws PlinguaCoreException
	 *             if the given format hasn't a OutputParser subclass
	 */
	public String getOutputFormatClassName(String formatID)
			throws PlinguaCoreException;
	
	/**
	 * Looks if there's any input format whose files correspond to the extension given and returns the name of the input format class which parses that format
	 * @param extension the extension which input format class name should be returned
	 * @return the input format class name which corresponds to the file extension given
	 * @throws PlinguaCoreException if there's no recognized input parser for the extension given
	 */
	public String getInputFormatClassNameThroughExtension(String extension) throws PlinguaCoreException;
	
	
	
}
