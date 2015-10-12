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

import java.lang.reflect.InvocationTargetException;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.checkPsystem.CheckPsystem;
import org.gcn.plinguacore.util.psystem.rule.AbstractRuleFactory;
import org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule;


/**
 * This class is intended to be extended by a P-system factory for each
 * supported model. Each factory should be able to create its own CheckRule
 * instances (which makes sure the p-system complies with the model
 * restrictions) and its own CreateSimulator, which should be able to create
 * simulators for that model All AbstractPsystemFactory subclasses should follow
 * the Singleton pattern.
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public abstract class AbstractPsystemFactory implements IPsystemFactory{

	protected CheckRule checkRule = null;
	protected CheckPsystem checkPsystem = null;
	private String modelName = null;
	private AbstractRuleFactory ruleFactory =null;

	
	private static IModelsClassProvider singletonModelsClassProvider=null;
	
	protected AbstractPsystemFactory() {

		try {
			/*
			 * The model name is got from the specific child class of
			 * AbstractPsystemFactory
			 */
			 
			modelName = getModelsClassProvider().getModelId(
					this.getClass().getCanonicalName());
			ruleFactory=newRuleFactory();
		} catch (PlinguaCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	protected static IModelsClassProvider getModelsClassProvider()
	{
		if (singletonModelsClassProvider==null)
			singletonModelsClassProvider= new XmlModelsResource();
		return singletonModelsClassProvider;
	}
	
	public static IModelsInfo getModelsInfo()
	{
		return getModelsClassProvider();
	}
	

	/**
	 * @see org.gcn.plinguacore.util.psystem.factory.IPsystemFactory#getCheckRule()
	 */
	public CheckRule getCheckRule() {
		return checkRule;
	}
	@Override
	public CheckPsystem getCheckPsystem() {
		return checkPsystem;
	}
	/**
	 * @see org.gcn.plinguacore.util.psystem.factory.IPsystemFactory#getModelName()
	 */
	public String getModelName() {

		return modelName;
	}
	
	protected abstract Psystem newPsystem();
	protected abstract AbstractRuleFactory newRuleFactory();
	
	@Override
	public final AbstractRuleFactory getRuleFactory()
	{
		return ruleFactory;
	}
	
	@Override
	public final Psystem createPsystem() throws PlinguaCoreException{
		// TODO Auto-generated method stub
		Psystem psystem = newPsystem();
		psystem.setAbstractPsystemFactory(this);
		return psystem;
	}


	/**
	 * Creates an AbstractPsystemFactory instance according to the model name
	 * given
	 * 
	 * @param modelName
	 *            the AbstractPsystemFactory model name
	 * @return the AbstractPsystemFactory instance created, as an instance of
	 *         PsystemFactoryInterface
	 * @throws PlinguaCoreException
	 *             if there's no AbstractPsystemFactory subclass which
	 *             corresponds to the variant name given
	 */
	public static IPsystemFactory createAbstractPsystemFactory(String modelName)
			throws PlinguaCoreException {
		Class<?> c = null;
		try {
			/* First, the class is loaded */
			c = Class.forName(getModelsClassProvider().getModelClassName(
					modelName));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			throw new PlinguaCoreException("The class for " + modelName
					+ " model doesn't exist: " + e.getMessage());
		}

		IPsystemFactory result = null;
		try {
			/*
			 * Since the class is a singleton, we need to invoke the getInstance
			 * method
			 */
			result = (IPsystemFactory) (c.getMethod("getInstance"))
					.invoke(null);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new PlinguaCoreException("The class for " + modelName
					+ " model cannot be instantiated: " + e.getMessage());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new PlinguaCoreException("The class for " + modelName
					+ " model cannot be accessed: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			throw new PlinguaCoreException(e.getMessage());
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			throw new PlinguaCoreException(e.getMessage());
		}
		return result;

	}
	
	

}
