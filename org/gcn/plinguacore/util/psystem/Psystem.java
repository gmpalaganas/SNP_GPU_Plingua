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

package org.gcn.plinguacore.util.psystem;

import java.io.Serializable;

import java.util.Collection;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Iterator;

import org.gcn.plinguacore.simulator.CreateSimulator;
import org.gcn.plinguacore.simulator.ISimulator;
import org.gcn.plinguacore.util.HashMultiSet;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.factory.IPsystemFactory;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.membrane.MembraneStructure;
import org.gcn.plinguacore.util.psystem.rule.HandRule;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.InnerRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.OuterRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.RulesSet;


/**
 * An abstract class for a P System
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */

public abstract class Psystem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3801859310721771797L;
	
	private RulesSet rules;

	private transient CreateSimulator cs;

	private SortedSet<AlphabetObject> alphabet;

	private Map<String, MultiSet<String>> initialMultiSets;
	
	private MultiSet<String> firstMultiSet = new HashMultiSet<String>();
	
	private MultiSet<String> secondMultiSet = new HashMultiSet<String>();

	private transient IPsystemFactory apf;

	private MembraneStructure membraneStructure;
	
	public static final transient Collection<? extends Membrane> emptyMembranes = new HashSet<Membrane>();
	
	protected String getModelName() {
		return apf.getModelName();
	}

	/**
	 * Sets an specific instance of PsystemFactotyInterface as the current
	 * factory to be used
	 * 
	 * @param apf
	 *            the PsystemFactoryInterface instance to be held
	 * @throws PlinguaCoreException
	 *             if any error ocurred during the instantiation of the
	 *             CreateSimulator instance
	 */
	public void setAbstractPsystemFactory(IPsystemFactory apf)
			throws PlinguaCoreException {
		if (apf == null)
			throw new NullPointerException(
					"Abstract Psystem Factory argument shouldn't be null");
		this.apf = apf;
		rules.setCheckRule(apf.getCheckRule());
		cs = apf.getCreateSimulator();

	}

	
	public MultiSet<String> getFirstMultiSet() {
		return firstMultiSet;
	}

	public void setFirstMultiSet(MultiSet<String> firstMultiSet) {
		this.firstMultiSet = firstMultiSet;
	}

	public MultiSet<String> getSecondMultiSet() {
		return secondMultiSet;
	}

	public void setSecondMultiSet(MultiSet<String> secondMultiSet) {
		this.secondMultiSet = secondMultiSet;
	}

	public void setRules(RulesSet rules) {
		this.rules = rules;
	}

	/**
	 * Gets the current AbstractPsystemFactory instance
	 * 
	 * @return the AbstractPsystemFactory instance currently held
	 */
	public IPsystemFactory getAbstractPsystemFactory() {
		return apf;
	}

	/**
	 * The constructor
	 */
	public Psystem() {

		alphabet = new TreeSet<AlphabetObject>();
		rules = new RulesSet();
		initialMultiSets = new HashMap<String, MultiSet<String>>();
	}

	/**
	 * Returns the initial P-system multisets
	 * 
	 * @return the initial P-system multisets
	 */
	public Map<String, MultiSet<String>> getInitialMultiSets() {
		return initialMultiSets;
	}

	/**
	 * Returns the P-system alphabet
	 * 
	 * @return the current alphabet
	 */
	public SortedSet<AlphabetObject> getAlphabet() {
		return alphabet;
	}

	/**
	 * Returns an iterator over the alphabet
	 * 
	 * @return an iterator on the current P-system alphabet
	 */

	public Iterator<AlphabetObject> alphabetIterator() {
		return alphabet.iterator();
	}

	/**
	 * @return The rules set of the p system
	 */
	public RulesSet getRules() {
		return rules;
	}

	private void addInnerMultiSets(OuterRuleMembrane outerRuleMembrane) {
		Iterator<InnerRuleMembrane> leftInnerMultiSets = outerRuleMembrane
				.getInnerRuleMembranes().listIterator();
		while (leftInnerMultiSets.hasNext())
			addAlphabetObjects(leftInnerMultiSets.next().getMultiSet());
	}

	protected void addAlphabetObjects(MultiSet<String> multiSet) {
		Iterator<String> multiSetIterator = multiSet.entrySet().iterator();
		while (multiSetIterator.hasNext())
			alphabet.add(new AlphabetObject(multiSetIterator.next()));

	}

	private void addHandRule(HandRule handRule) {
		addAlphabetObjects(handRule.getMultiSet());
		addAlphabetObjects(handRule.getOuterRuleMembrane().getMultiSet());
		addInnerMultiSets(handRule.getOuterRuleMembrane());
	}

	/**
	 * adds a rule to the existing rule set
	 * 
	 * @param r
	 *            the rule to be added
	 * @return if the rule has been successfully added
	 */
	public boolean addRule(IRule r) {
		addHandRule(r.getLeftHandRule());
		addHandRule(r.getRightHandRule());
		return rules.add(r);
	}


	protected abstract Configuration newConfigurationObject();

	
	
	public Configuration getFirstConfiguration() {
		Configuration config = newConfigurationObject();
		Iterator<? extends Membrane>it = config.getMembraneStructure().getAllMembranes().iterator();
		while(it.hasNext())
			addInitialMultiSets(it.next());
		return config;
	}

	
	
	@Override
	public String toString() {
		String str = "Initial membrane structure: "+membraneStructure.toString()+"\n\n";
		if (!alphabet.isEmpty()) {
			str += "Alphabet:\n" + alphabet.toString() + "\n";
		}
		if (!initialMultiSets.isEmpty())
			str += "\nInitial multisets:\n" + initialMultiSets.toString()
					+ "\n";
		if (!rules.isEmpty())
			str += "\nRules:\n" + rules.toString();
		return str;
	}

	private void checkRules(RulesSet rules) throws PlinguaCoreException {
		if (!rules.checkRules())
			throw new PlinguaCoreException(
					"There are rules which doesn't match the model specification");

	}

	/**
	 * Creates a simulator to simulate this P-system. The parameters specify if
	 * this simulator will be able to take steps back, take alternative steps
	 * (among different, possible steps) and which specific class will implement
	 * the simulator, specified by ID
	 * 
	 * @param stepBackSupported
	 *            sets if the simulator created supports steps back
	 * @param alternativeStepsSupported
	 *            sets if the simulator created supports alternate steps
	 * @param id
	 *            the Simulator id which references the specific Simulator class
	 *            to be instantiated
	 * @return a simulator which complies with the parameter conditions
	 * @throws PlinguaCoreException
	 *             if there are rules which doesn't match the model
	 *             specification previously defined by setting the
	 *             AbstractPsystemFactory instance
	 */
	public ISimulator createSimulator(boolean stepBackSupported,
			boolean alternativeStepsSupported, String id)
			throws PlinguaCoreException {
		checkRules(rules);
		ISimulator result = cs.createSimulator(stepBackSupported,
				alternativeStepsSupported, id, this);
		return result;
	}

	/**
	 * Creates a simulator to simulate this P-system. The parameters specify if
	 * this simulator will be able to take steps back and/or take alternative
	 * steps (among different, possible steps). As no ID is passed as parameter
	 * the class implemented by the simulator will be the first available one,
	 * taken by default
	 * 
	 * @param stepBackSupported
	 *            sets if the simulator created supports steps back
	 * @param alternativeStepsSupported
	 *            sets if the simulator created supports alternate steps
	 * @return a simulator which complies with the parameter conditions
	 * @throws PlinguaCoreException
	 *             if there are rules which doesn't match the model
	 *             specification previously defined by setting the
	 *             AbstractPsystemFactory instance
	 */
	public ISimulator createSimulator(boolean stepBackSupported,
			boolean alternativeStepsSupported) throws PlinguaCoreException {
		checkRules(rules);
		ISimulator result = cs.createSimulator(stepBackSupported,
				alternativeStepsSupported, this);
		return result;
	}
	
	/**
	 * Gets an iterator on all possible simulators IDs for the Psystem 
	 * @return an iterator on all possible simulators IDs for the Psystem 
	 * @throws PlinguaCoreException if the simulators IDs provider hasn't set yet
	 */
	public Iterator<String> getSimulatorsIDs() throws PlinguaCoreException{
		if(cs==null)
			throw new PlinguaCoreException("Create Simulator field not set yet");
		return cs.getSimulatorsIDs();
	}
	
	/**
	 * Returns a boolean representing if the simulator related to id could be
	 * created capable of performing alternative steps
	 * 
	 * @param id
	 *            the simulator identifier
	 * @return if the simulator related to id could be created capable of
	 *         performing alternative steps
	 * @throws PlinguaCoreException if the simulators IDs provider hasn't set yet
	 */
	public boolean supportsAlternativeStep(String id) throws PlinguaCoreException{
		if(cs==null)
			throw new PlinguaCoreException("Create Simulator field not set yet");
		return cs.supportsAlternativeStep(id);
	}
	
	protected void addInitialMultiSets(Membrane m) {
		/*
		 * If there's any initial multiset which corresponds to the membrane
		 * label, it should be added into the membrane
		 */
		if (getInitialMultiSets().containsKey(m.getLabel()))
			m.getMultiSet().addAll(getInitialMultiSets().get(m.getLabel()));
		/* Then, its alphabet objects should be added */
		addAlphabetObjects(m.getMultiSet());
		
	}
	
	

	public MembraneStructure getMembraneStructure() {
		return membraneStructure;
	}

	public void setMembraneStructure(MembraneStructure membraneStructure) {
		this.membraneStructure = membraneStructure;
	}

	

}
