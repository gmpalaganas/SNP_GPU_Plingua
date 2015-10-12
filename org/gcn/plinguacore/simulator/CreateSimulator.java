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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.PlinguaPanicError;
import org.gcn.plinguacore.util.psystem.Psystem;




/**
 * This class provides support for creating simulators according to the options
 * defined
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public abstract class CreateSimulator{

	
	private ISimulatorClassProvider simulatorsClassProvider=null;
	
	private String defaultID;
	private static final String SIMULATORS = "simulators";
	
	private String model;
	private Map<String, Constructor<?>> baseSimulatorsMap;
	private Map<String, Constructor<?>> alternativeSimulatorsMap;


	/**
	 * Creates a new instance which creates simulators for the model indicated
	 * by its name
	 * 
	 * @param model
	 *            the name of the model which the simulators created should
	 *            simulate
	 * @throws PlinguaCoreException
	 *             if any simulator class doesn't match the expected modifiers
	 */
	public CreateSimulator(String model) throws PlinguaCoreException {
		super();
		if (model == null)
			throw new NullPointerException("model argument shouldn't be null");
		this.model = model;
		initializeSimulatorsMap();
	}
	
	protected ISimulatorClassProvider getSimulatorsClassProvider()
	{
		if (simulatorsClassProvider==null)
			simulatorsClassProvider=new XmlSimulatorsResource(getRoute());
		return simulatorsClassProvider;
	}
	
	public ISimulatorInfo getSimulatorsInfo()
	{
		return getSimulatorsClassProvider();
	}
	
	

	/**
	 * Gets the model name of the Simulator
	 * 
	 * @return the model name of the Simulator
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getModel();
	}


	private void addConstructor(String name, Class<?> simulatorClass,
			Map<String, Constructor<?>> simulatorsMap,
			Class<?> constructorArgument) throws PlinguaCoreException {
		try {
			/*
			 * The specific constructor (recognized by its parameter) is added
			 * to the specific map
			 */
			simulatorsMap.put(name, simulatorClass
					.getConstructor(constructorArgument));
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			throw new PlinguaCoreException("The constructor for the class "
					+ simulatorClass.getCanonicalName() + " parameterized by "
					+ constructorArgument.getCanonicalName()
					+ " couldn't be accessed");
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			throw new PlinguaCoreException("The constructor for the class "
					+ simulatorClass.getCanonicalName() + " parameterized by "
					+ constructorArgument.getCanonicalName() + " doesn't exist");
		}
	}

	private void initializeSimulatorsMap() throws PlinguaCoreException {
		baseSimulatorsMap = new LinkedHashMap<String, Constructor<?>>();
		/*
		 * At first, we need to get all simulators id. Each id represents a
		 * triplet which consist of: a. A base simulator b. A simulator which
		 * supports step back (optional) c. A simulator which supports
		 * alternative steps (optional)
		 */
		
		String classIds[] = getSimulatorsIds(getSimulatorsClassProvider());

		if (classIds == null || classIds.length == 0)
			throw new PlinguaCoreException(
					"Class ids array shouldn't be null or empty");
		defaultID = classIds[0];
		/* Then, we move on to get the base simulators */
		Class<?> baseSimulators[] = getBaseSimulators(getSimulatorsClassProvider(), classIds);
		if (baseSimulators == null || baseSimulators.length == 0)
			throw new PlinguaCoreException(
					"Base simulators array shouldn't be null or empty");
		for (int i = 0; i < baseSimulators.length; i++)
			if (baseSimulators[i] == null)
				throw new PlinguaCoreException(
						"No base simulator should be null");
		/*
		 * The same is done with alternative step simulators and step back
		 * simulators
		 */
		Class<?> alternativeSimulators[] = getSimulatorsClassProvider()
				.getAlternativeStepSupportedSimulators();
		/* Once we have all data we need, their lengths should be tested */
		checkLengths(classIds.length, baseSimulators.length,
				alternativeSimulators);
		/*
		 * Alternative and step back simulators map are created only in case
		 * there's any of such type
		 */
		if (alternativeSimulators != null)
			alternativeSimulatorsMap = new HashMap<String, Constructor<?>>();
		/* We need to make sure the simulator classes are right */
		checkSimulators(baseSimulators, alternativeSimulators
				);
		int length = classIds.length;
		/* Finally, we add all constructors to our maps */
		for (int i = 0; i < length; i++) {
			addConstructor(classIds[i], baseSimulators[i], baseSimulatorsMap,
					Psystem.class);
			if (alternativeSimulators != null
					&& alternativeSimulators[i] != null)
				addConstructor(classIds[i], alternativeSimulators[i],
						alternativeSimulatorsMap, ISimulator.class);

		}

	}

	protected abstract String getRoute();

	private Class<?>[] getBaseSimulators(ISimulatorClassProvider simulatorReader,
			String[] classIds) {
		/* First of all, we need to know how many simulators we will read */
		int maximum = classIds.length;
		/* Once we know it, we go on to create the resulting array */
		Class<?>[] baseSimulators = new Class<?>[maximum];

		for (int i = 0; i < maximum; i++)
			try {
				/* Later, we read a new simulator class for each ID */
				baseSimulators[i] = readClass(simulatorReader.idClassName(
						classIds[i], SIMULATORS));
			} catch (PlinguaCoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return baseSimulators;
	}

	private Class<?> readClass(String idClassName) {
		// TODO Auto-generated method stub
		try {
			return Class.forName(idClassName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			throw new PlinguaPanicError("The simulator class " + idClassName
					+ " doesn't exist");
		}
	}

	private String[] getSimulatorsIds(ISimulatorClassProvider simulatorReader) {
		// TODO Auto-generated method stub
		List<String> idsList = new LinkedList<String>();
		/* First, we need to get an iterator on the identifiers */
		Iterator<String> idsIterator = simulatorReader.getIdentifiersIterator();
		while (idsIterator.hasNext())
			/* Each identifier read is added to the list */
			idsList.add(idsIterator.next());
		/* Finally, the list is turned into an array */
		return idsList.toArray(new String[] {});
	}

	private void checkSpecificLength(String firstName, int firstLength,
			String secondName, int secondLength) throws PlinguaCoreException {
		/* We check if both lengths match */
		if (firstLength != secondLength)
			throw new PlinguaCoreException(firstName
					+ " array, which length is " + firstLength
					+ ", should be as long as " + secondName
					+ " array, which lenght is " + secondLength);

	}

	private void checkLengths(int classNamesLength, int baseSimulatorsLength,
			Class<?>[] alternativeSimulators)
			throws PlinguaCoreException {
		/*
		 * Class names array length and base simulators array length should
		 * match
		 */
		checkSpecificLength("Class names", classNamesLength, "base simulators",
				baseSimulatorsLength);
		/*
		 * In case there are alternative or step back simulators their length
		 * should match, as well
		 */
		if (alternativeSimulators != null)
			checkSpecificLength("Alternative simulators",
					alternativeSimulators.length, "base simulators",
					baseSimulatorsLength);
		// TODO Auto-generated method stub

	}

	private void checkSimulators(Class<?>[] baseSimulators,
			Class<?> alternativeSimulators[])
			throws PlinguaCoreException {
		int length = baseSimulators.length;
		for (int i = 0; i < length; i++) {
			/* All simulators should comply with several conditions */
			checkSpecificSim(baseSimulators[i], baseSimulators[i]
					.getModifiers(), AbstractSimulator.class);
			/*
			 * Obviously, these conditions only apply to alternative simulators
			 * or step back simulators if they exist
			 */
			if (alternativeSimulators != null
					&& alternativeSimulators[i] != null)
				checkSpecificSim(alternativeSimulators[i],
						alternativeSimulators[i].getModifiers(),
						AlternativeStepsSupportedSimulator.class);

		}

	}

	private void checkSpecificSim(Class<?> simulator, int modifiers,
			Class<?> expectedParent) throws PlinguaCoreException {
		/*
		 * A simulator class should't be abstract, because is intended to be
		 * instantiated
		 */
		if (Modifier.isAbstract(modifiers))
			throw new PlinguaCoreException(
					simulator.getCanonicalName()
							+ " class should not be abstract, as it's intended to be instantiated");
		/* A simulator class should be a child class of its expected parent */
		if (!expectedParent.isAssignableFrom(simulator))
			throw new PlinguaCoreException("The class "
					+ expectedParent.getCanonicalName()
					+ " should be an ancestor to "
					+ simulator.getCanonicalName());
		/*
		 * A simulator class should be public, so that it can be accessed from
		 * CreateSimulator class
		 */
		if (!Modifier.isPublic(modifiers))
			throw new PlinguaCoreException("The class "
					+ simulator.getCanonicalName() + " should be public");
	}

	/**
	 * Creates a new simulator, which specific classes are identified by an ID.
	 * The possible ID are initially provided by the class itself, and it's
	 * necessary to specify the ID of the specific class which will perform the
	 * simulation of the p-system
	 * 
	 * @param stepBackAllowed
	 *            if the simulator will allow steps back
	 * @param alternativeStepAllowed
	 *            if the simulator will allow steps back
	 * @param simulatorClassId
	 *            the ID which identifies the class to simulate the model
	 * @param psystem
	 *            the P-system to be simulated
	 * @return the simulator created
	 * @throws PlinguaCoreException
	 *             if an error occurred during the {@link ISimulator}
	 *             instantiation process
	 * @throws UnsupportedOperationException
	 *             if the simulator instantiation couldn't be committed
	 */
	public ISimulator createSimulator(boolean stepBackAllowed,
			boolean alternativeStepAllowed, String simulatorClassId,
			Psystem psystem) throws UnsupportedOperationException,
			PlinguaCoreException {

		ISimulator baseSimulator = createBaseSimulator(psystem,
				simulatorClassId);
		/*
		 * If the parameters specify that the simulator should admit steps back,
		 * we add such functionality
		 */
		if (stepBackAllowed)
			baseSimulator = createStepBackSimulator(baseSimulator);
		/*
		 * If the parameters specify that the simulator should admit alternate
		 * steps, we add such functionality
		 */
		if (alternativeStepAllowed)
			baseSimulator = createAlternativeStepSimulator(baseSimulator,
					simulatorClassId);
		return baseSimulator;

	}

	/**
	 * Creates a new simulator. The simulator triplet is the first one available
	 * (taken by default), so there's no need to specify an id for it
	 * 
	 * @param stepBackAllowed
	 *            if the simulator will allow steps back
	 * @param alternativeStepAllowed
	 *            if the simulator will allow steps back
	 * @param psystem
	 *            the P-system to be simulated
	 * @return the simulator created
	 * @throws PlinguaCoreException
	 *             if an error occurred during the {@link ISimulator}
	 *             instantiation process
	 * @throws UnsupportedOperationException
	 *             if the simulator instantiation couldn't be committed
	 */
	public ISimulator createSimulator(boolean stepBackAllowed,
			boolean alternativeStepAllowed, Psystem psystem)
			throws UnsupportedOperationException, PlinguaCoreException {

		ISimulator baseSimulator = createBaseSimulator(psystem, defaultID);
		/*
		 * If the parameters specify that the simulator should admit steps back,
		 * we add such functionality
		 */
		if (stepBackAllowed)
			baseSimulator = createStepBackSimulator(baseSimulator);
		/*
		 * If the parameters specify that the simulator should admit alternate
		 * steps, we add such functionality
		 */
		if (alternativeStepAllowed)
			baseSimulator = createAlternativeStepSimulator(baseSimulator,
					alternativeSimulatorsMap.keySet().iterator().next());
		return baseSimulator;

	}

	/**
	 * Gets an iterator on all simulators IDs
	 * 
	 * @return an iterator on all simulators IDs
	 */
	public Iterator<String> getSimulatorsIDs() {
		return baseSimulatorsMap.keySet().iterator();
	}

	/**
	 * Returns a boolean representing if the simulator related to id could be
	 * created capable of performing alternative steps
	 * 
	 * @param id
	 *            the simulator identifier
	 * @return if the simulator related to id could be created capable of
	 *         performing alternative steps
	 */
	public boolean supportsAlternativeStep(String id) {
		return supportsSelectedOption(alternativeSimulatorsMap, id);
	}



	private boolean supportsSelectedOption(Map<String, Constructor<?>> map,
			String id) {
		if (map == null)
			return false;
		return map.containsKey(id);
	}

	/**
	 * Returns a boolean representing if the default simulator could be created
	 * capable of performing alternative steps
	 * 
	 * @return if the default simulator could be created capable of performing
	 *         alternative steps
	 */
	public boolean supportsAlternativeStep() {
		return supportsSelectedOption(alternativeSimulatorsMap, defaultID);
	}

	protected StepBackSupportedSimulator createStepBackSimulator(ISimulator si){
		return new StepBackSupportedSimulator(si);
	}

	protected AlternativeStepsSupportedSimulator createAlternativeStepSimulator(
			ISimulator si, String id) throws UnsupportedOperationException,
			PlinguaCoreException {
		return (AlternativeStepsSupportedSimulator) createSimulator(si, id,
				"alternative step supported", alternativeSimulatorsMap);
	}

	protected DecoratorSimulator createSimulator(ISimulator si, String id,
			String typeName, Map<String, Constructor<?>> constructorMap)
			throws UnsupportedOperationException, PlinguaCoreException {
		if (constructorMap != null && constructorMap.containsKey(id))
			try {
				return (DecoratorSimulator) constructorMap.get(id).newInstance(
						si);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				throwException(typeName, id);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				throwException(typeName, id);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				throwException(typeName, id);
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				throwException(typeName, id);
			}
		throw new UnsupportedOperationException();
	}

	protected AbstractSimulator createBaseSimulator(Psystem si, String id)
			throws UnsupportedOperationException, PlinguaCoreException {
		if (baseSimulatorsMap.containsKey(id))
		{
			try {
				return (AbstractSimulator) baseSimulatorsMap.get(id).newInstance(si);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				throwException("base", id);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				throwException("base", id);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				throwException("base", id);
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				if (e.getTargetException() instanceof PlinguaCoreException)
					throw (PlinguaCoreException)e.getTargetException();
				else
				if (e.getTargetException() instanceof RuntimeException)
					throw (RuntimeException)e.getTargetException();
				else
					throwException("base", id);
			}
			
		}
		throw new UnsupportedOperationException("The simulator ID "+id+" for the model "+this.model+" doesn't exist");
	}

	private void throwException(String type, String id)
			throws PlinguaCoreException {
		throw new PlinguaCoreException(
				"An exception occurred during the instantiation of the " + type
						+ " class by passing the id " + id);

	}

}
