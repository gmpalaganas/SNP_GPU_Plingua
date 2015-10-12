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

package org.gcn.plinguacore.simulator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.gcn.plinguacore.util.PlinguaPanicError;



/**
 * This class gives support for storing and loading different elements, according to ElementType class. The elements this class is intended for are Simulators and P-systems
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * @param <ElementType> The element type to be saved and loaded
 * 
 */

public class ElementFileStream<ElementType> implements Serializable {

	private static final long serialVersionUID = 1L;

	private ElementType element;

	private ObjectOutputStream outputStream;

	private ObjectInputStream inputStream;

	private String fileRoute;

	/**
	 * Creates a new ElementFileStream
	 * 
	 * @param element
	 *            the element to be saved by this instance
	 * @param route
	 *            the file route of the file where this instance will save the
	 *            element
	 * @throws NullPointerException if route or element argument are null
	 */
	public ElementFileStream(ElementType element, String route){
		this(route);
		
		if (element == null)
			throw new NullPointerException(
					"The element constructor argument shouldn't be null");
		this.element = element;
	}
	
	/**
	 * Creates a new ElementFileStream
	 * 
	 * @param route
	 *            the file route of the file where this instance will save the
	 *            element
	 * @throws NullPointerException if route argument is null
	 */
	public ElementFileStream(String route)
			throws NullPointerException{
		super();
		setFile(route);
	}

	/**
	 * Sets the file where the current element will be saved on
	 * 
	 * @param fileRoute
	 *            the output file route
	 * @throws NullPointerException if fileRoute argument is null
	 */
	public void setFile(String fileRoute) throws NullPointerException{
		/*
		 * If the file is set we need to construct back both the input stream
		 * and the output stream according to the new route
		 */
		if (fileRoute == null)
			throw new NullPointerException(
					"The file route to save elements shouldn't be null");
		this.fileRoute = fileRoute;

	}

	/**
	 * Saves the element
	 * 
	 * @throws IOException
	 *             if the element couldn't be properly written
	 */
	public void saveElement() throws IOException {
		outputStream = new ObjectOutputStream(new FileOutputStream(
				fileRoute));
		outputStream.flush();
		outputStream.writeObject(element);
		outputStream.close();

	}

	/**
	 * Gets the element to be loaded
	 * 
	 * @return the element to be saved and loaded
	 */
	public ElementType getElement() {
		return element;
	}

	/**
	 * Sets the element to be saved
	 * 
	 * @param element
	 *            the simulator to be saved
	 */
	public void setElement(ElementType element) {
		if (element == null)
			throw new NullPointerException(
					"setElement parameter shouldn't be null");
		this.element = element;
	}

	/**
	 * Loads and returns the element loaded
	 * 
	 * @return the element loaded
	 * @throws IOException
	 *             if the element couldn't be loaded
	 */
	public ElementType loadElement() throws IOException{
		this.inputStream = new ObjectInputStream(new FileInputStream(fileRoute));
		try {
			/* We read the element from the file indicated by fileRoute */
			try{
			element = ((ElementType) inputStream.readObject());
			}
			catch(ClassCastException e){
				throw new IOException(" The object on the file is not one of the expected class ");
			}
			inputStream = new ObjectInputStream(new FileInputStream(fileRoute));

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			throw new PlinguaPanicError(e.getMessage());
		}
		return element;
	}
}
