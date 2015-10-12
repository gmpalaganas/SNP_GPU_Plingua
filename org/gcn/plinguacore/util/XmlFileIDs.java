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

package org.gcn.plinguacore.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.HashMap;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import java.util.Iterator;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * This class provides all common functionality for reading XML documents which
 * follow a key-value structure
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public abstract class XmlFileIDs {

	private String fileRoute = null;

	
	private static final String ROOT_DIRECTORY = "org/gcn/plinguacore/";
	private Map<String, Map<String, String>> ids;

	protected XmlFileIDs(String fileRoute) {
		if (fileRoute == null)
			throw new NullPointerException(
					"FileRoute argument shouldn't be null");
		this.fileRoute = ROOT_DIRECTORY+fileRoute;

		ids = new HashMap<String, Map<String, String>>();

		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		InputStream stream = null;
		/*
		 * First, we build the document from an input stream appointed by the
		 * file route
		 */
		try {
			stream = this.getClass().getResourceAsStream("/" + this.fileRoute);
			doc = builder.build(stream);
			/* Once built, the document is read */
			readDocument(doc);
			stream.close();
		} catch (JDOMException e) {
			throw new PlinguaPanicError(e.getMessage());
		} catch (IOException e) {
			throw new PlinguaPanicError(e.getMessage());
		}

	}

	protected void throwFileException() {
		throw new PlinguaPanicError("The file " + fileRoute
				+ " doesn't match the expected format");
	}

	private void addID(Element element, String expectedElementName,
			String expectedElementChildName, Class<?> expectedParentClass,
			boolean isSingleton) {
		/* The element name should match its expected name */
		if (!element.getName().equals(expectedElementName))
			throwFileException();
		ListIterator<?> iterator = element.getChildren().listIterator();
		/*
		 * Then, each element on the document should be checked and its map
		 * should be read
		 */
		while (iterator.hasNext()) {
			Element idElement = (Element) iterator.next();
			checkElement(idElement, expectedElementChildName,
					expectedParentClass, isSingleton);
			getMap(expectedElementName).put(idElement.getAttributeValue("id"),
					idElement.getAttributeValue("class"));
		}
	}

	protected void addSingletonID(Element element, String expectedElementName,
			String expectedElementChildName, Class<?> expectedParentClass) {
		addID(element, expectedElementName, expectedElementChildName,
				expectedParentClass, true);
	}

	protected void addID(Element element, String expectedElementName,
			String expectedElementChildName, Class<?> expectedParentClass) {

		addID(element, expectedElementName, expectedElementChildName,
				expectedParentClass, false);
	}

	/* The specific way to read a document depends on the specific child */
	protected abstract void readDocument(Document doc);

	private void throwModifierException(String name, String modifier) {
		throw new PlinguaPanicError("The " + name + " shouldn't be " + modifier);
	}

	private void checkSingletonModifiers(Class<?> c)
			throws NoSuchMethodException {
		int m = c.getDeclaredConstructor().getModifiers();

		/*
		 * Because class c follows Singleton pattern, its constructor shouldn't
		 * be public
		 */
		if (Modifier.isPublic(m))
			throwModifierException("constructor of " + c.getCanonicalName(),
					"public");

		try {
			Method method = c.getDeclaredMethod("getInstance");

			m = method.getModifiers();
			/* getInstance method should be public and static, as well */
			if (!Modifier.isStatic(m))
				throw new PlinguaPanicError("getInstance() method in "
						+ c.getCanonicalName() + " should be static");

			if (!Modifier.isPublic(m))
				throw new PlinguaPanicError("getInstance() method in "
						+ c.getCanonicalName() + " should be public");

			Class<?>[] params = method.getParameterTypes();
			/* getInstance method has no parameters */
			if (params.length != 0)
				throw new PlinguaPanicError("getInstance() method in "
						+ c.getCanonicalName()
						+ " shouldn't contain parameters");

			Class<?> r = method.getReturnType();
			/*
			 * Finally, the return type should be the same as the class which
			 * the method belongs
			 */
			if (!r.getCanonicalName().equals(c.getCanonicalName()))
				throw new PlinguaPanicError("getInstance() method in "
						+ c.getCanonicalName()
						+ " should return an object of type "
						+ c.getCanonicalName());
		} catch (NoSuchMethodException ex) {
			throw new PlinguaPanicError(c.getCanonicalName()
					+ " must contain a getInstance() method");
		}
	}

	private void checkModifiers(int modifier, String name) {
		if (Modifier.isAbstract(modifier))
			throwModifierException(name, "abstract");
		if (Modifier.isInterface(modifier))
			throwModifierException(name, "an interface");
		if (Modifier.isPrivate(modifier))
			throwModifierException(name, "private");
		if (Modifier.isProtected(modifier))
			throwModifierException(name, "protected");
	}

	protected void checkElement(Element element, String expectedElementName,
			Class<?> expectedParentClass, boolean isSingleton) {

		/*
		 * Each element should have a name equal to the expected one and should
		 * have both an id and class attributes
		 */
		if (!element.getName().equals(expectedElementName))
			throwFileException();

		if (element.getAttributeValue("id") == null
				|| element.getAttributeValue("class") == null)
			throwFileException();

		try {
			/* It should be a child class of its expected parent, as well */
			Class<?> c = Class.forName(element.getAttributeValue("class"));

			if (!expectedParentClass.isAssignableFrom(c))
				throw new PlinguaPanicError("The class "
						+ expectedParentClass.getCanonicalName()
						+ " must be assignable from " + c.getCanonicalName());
			try {
				/*
				 * Finally, both its modifiers and its constructor's should
				 * match its expected values
				 */
				checkModifiers(c.getModifiers(), "class "
						+ c.getCanonicalName());

				if (isSingleton)
					checkSingletonModifiers(c);
				else {
					Constructor<?> validConstructor = getValidConstructor(c);
					checkModifiers(validConstructor.getModifiers(),
							"constructor of " + c.getCanonicalName());
				}

			} catch (NoSuchMethodException e) {
				throw new PlinguaPanicError(
						"There is no non-argument constructor in class "
								+ c.getCanonicalName());
			} catch (SecurityException e) {

				throw new PlinguaPanicError("Class not instanciable: "
						+ c.getCanonicalName());
			}
		} catch (ClassNotFoundException e) {
			throw new PlinguaPanicError("Class not found: " + e.getMessage());
		}

	}

	protected Iterator<String> inmutableIterator(String typeId) {
		return Collections.unmodifiableCollection(getMap(typeId).keySet())
				.iterator();
	}

	protected Map<String, String> getMap(String typeId) {
		/*
		 * We get the map corresponding to its ID. In case id toens't exist, it
		 * should be created
		 */
		Map<String, String> map = ids.get(typeId);
		if (map == null) {
			map = new LinkedHashMap<String, String>();
			ids.put(typeId, map);
		}
		return map;
	}

	protected Constructor<?> getValidConstructor(Class<?> c)
			throws SecurityException, NoSuchMethodException {
		return c.getDeclaredConstructor();
	}

	protected String classIdName(String classStr, String typeId)
			throws PlinguaCoreException {
		/*
		 * We need to go through each map stored looking for the class
		 * corresponding to its implemented functionality
		 */
		Iterator<String> it = getMap(typeId).keySet().iterator();
		String key = null;
		while (it.hasNext()) {
			key = it.next();
			/* Once we've found it, we stop searching */
			if (getMap(typeId).get(key).equals(classStr))
				return key;
		}
		throw new PlinguaCoreException();

	}

	protected String idClassName(String id, String typeId)
			throws PlinguaCoreException {

		String result = getMap(typeId).get(id);
		if (result == null)
			throw new PlinguaCoreException();
		return result;
	}

}
