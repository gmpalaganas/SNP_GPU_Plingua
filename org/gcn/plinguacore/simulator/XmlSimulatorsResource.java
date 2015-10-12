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

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.PlinguaPanicError;
import org.gcn.plinguacore.util.XmlFileIDs;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.jdom.Document;
import org.jdom.Element;



/**
 * This class is intended to be used only by CreateSimulator, as its only
 * purpose is to read files containing info about the classes which implement
 * the simulators for the models to simulate
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
class XmlSimulatorsResource extends XmlFileIDs implements ISimulatorClassProvider{

	
	
	private static final String SIMULATORS_FOLDER = "resources/simulatorsXML/";
	private static final String EXTENSION = ".xml";
	private static final String SIMULATORS = "simulators";

	private List<Class<?>> alternativeStepSimulators;

	protected XmlSimulatorsResource(String fileRoute) {
		super(SIMULATORS_FOLDER + fileRoute + EXTENSION);

		// TODO Auto-generated constructor stub
	}

	private void readDecoratorSimulator(Element element, String field,
			Class<?> ancestor, List<Class<?>> classList)
			throws PlinguaCoreException {
		/*
		 * For each ID, it could be a StepBackSimulator subclass, an
		 * AlternativeStepSimulator class, both or none of them
		 */
		String className = element.getAttributeValue(field);
		/* If the element isn't null, there's a class to read */
		if (className != null)
			classList.add(readClass(className,
					AlternativeStepsSupportedSimulator.class));
		/*
		 * If this element is null, it means such subclass doesn't exist.
		 * However, it's important to add an element to fill the gap between
		 * simulators
		 */
		else
			classList.add(null);

	}

	public Iterator<String> getIdentifiersIterator() {

		return inmutableIterator(SIMULATORS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see util.XmlFileIDs#checkElement(org.jdom.Element, java.lang.String,
	 * java.lang.Class, boolean)
	 */
	@Override
	protected void checkElement(Element element, String expectedElementName,
			Class<?> expectedParentClass, boolean isSingleton) {
		// TODO Auto-generated method stub
		super.checkElement(element, expectedElementName, expectedParentClass,
				isSingleton);

		try {
			/*
			 * We need to count on the StepBackSimulator and
			 * AlternativeStepSimulator which could be implemented for this ID
			 */
			readDecoratorSimulator(element, "alternative",
					AlternativeStepsSupportedSimulator.class,
					alternativeStepSimulators);
		} catch (PlinguaCoreException e) {
			// TODO Auto-generated catch block
			throw new PlinguaPanicError(
					"An error ocurred while reading an alternative or step back supporting simulator class");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see util.XmlFileIDs#getConstructorModifiers(java.lang.Class)
	 */
	@Override
	protected Constructor<?> getValidConstructor(Class<?> c)
			throws SecurityException, NoSuchMethodException {
		// TODO Auto-generated method stub
		return c.getDeclaredConstructor(Psystem.class);
	}

	private Class<?> readClass(String className, Class<?> expectedParent)
			throws PlinguaCoreException {
		Class<?> result = null;
		try {
			/* The class is obtained from its name */
			result = Class.forName(className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			throw new PlinguaCoreException("Error instantiating the class "+className);
		}
		/* The class should be a descendant of its expected parent */
		if (!expectedParent.isAssignableFrom(result))
			throw new PlinguaCoreException(expectedParent.getCanonicalName()
					+ " class should be an ancestor to "
					+ result.getCanonicalName());
		return result;

	}

	public Class<?>[] getAlternativeStepSupportedSimulators() {
		if (alternativeStepSimulators.isEmpty())
			return null;
		return alternativeStepSimulators.toArray(new Class<?>[] {});
	}


	@Override
	protected void readDocument(Document doc) {
		/*
		 * As these list doesn't exist in XmlFileIDs, they should be created.
		 * However, they can't be created in the constructor, because then it's
		 * too late (would result in a null pointer exception for using them
		 * before creating them)
		 */
		alternativeStepSimulators = new LinkedList<Class<?>>();
		Element root = doc.getRootElement();
		addID(root, SIMULATORS, "simulator", AbstractSimulator.class);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see util.XmlFileIDs#idClassName(java.lang.String, java.lang.String)
	 */
	@Override
	public String idClassName(String id, String typeId)
			throws PlinguaCoreException {
		// TODO Auto-generated method stub
		return super.idClassName(id, typeId);
	}

}
