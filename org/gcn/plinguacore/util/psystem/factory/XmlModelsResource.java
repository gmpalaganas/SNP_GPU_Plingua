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

package org.gcn.plinguacore.util.psystem.factory;

import java.util.Iterator;

import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.XmlFileIDs;
import org.jdom.Document;
import org.jdom.Element;



/**
 * This class provides support for reading supported models and simulators
 * concerning those models
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */

class XmlModelsResource extends XmlFileIDs implements IModelsClassProvider,IModelsInfo{

	
	private static final String modelsStr = "models";
	private static final String MODELS_ROUTE = "resources/original/";
	private static final String MODELS_FILENAME = "models.xml";
	
	
	public XmlModelsResource() {
		super(MODELS_ROUTE+MODELS_FILENAME);

	}

	
	@Override
	protected void readDocument(Document doc) {

		Element root = doc.getRootElement();
		addSingletonID(root, modelsStr, "model", IPsystemFactory.class);

	}

	@Override
	public Iterator<String> getModelsIterator() {

		return inmutableIterator(modelsStr);
	}

	@Override
	public String getModelId(String className) throws PlinguaCoreException {
		try {
			return classIdName(className, modelsStr);
		} catch (PlinguaCoreException e) {

			throw new PlinguaCoreException(className
					+ " class P systems not supported");
		}
	}

	@Override
	public String getModelClassName(String model) throws PlinguaCoreException {

		try {
			return idClassName(model, modelsStr);
		} catch (PlinguaCoreException e) {

			throw new PlinguaCoreException(model + " P systems not supported");
		}

	}


}
