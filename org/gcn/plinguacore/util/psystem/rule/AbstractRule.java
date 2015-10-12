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

package org.gcn.plinguacore.util.psystem.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;

/**
 * This class represents all kind of rules, intended to be extended to fulfill
 * the requirements of each P-System group
 * 
 * @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public abstract class AbstractRule implements Serializable,IRule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2223028108798909376L;

	protected InnerRuleMembrane[] handCorrespondence;

	private boolean dissolves;
	private LeftHandRule leftHandRule;
	private RightHandRule rightHandRule;
	protected List<InnerRuleMembrane> newMembranes;
	private long ruleId=-1;
	/**
	 * Creates a new Rule instance. As Rule class is abstract, this constructor
	 * is intended to be used by Rule subclasses
	 * 
	 * @param dissolves
	 *            a boolean parameter which reflects if the rule will dissolve
	 *            the membrane
	 * @param leftHandRule
	 *            the left hand of the rule
	 * @param rightHandRule
	 *            the right hand of the rule
	 */
	public AbstractRule(boolean dissolves, LeftHandRule leftHandRule,
			RightHandRule rightHandRule) {
		super();
		if (leftHandRule == null)
			throw new NullPointerException(
					"LeftHandRule parameter in Rule constructor shouldn't be null");
		if (rightHandRule == null)
			throw new NullPointerException(
					"RightHandRule parameter in Rule constructor shouldn't be null");
		this.dissolves = dissolves;
		this.leftHandRule = leftHandRule;
		this.rightHandRule = rightHandRule;
		checkState();
		setHandCorrespondence();
		initializeNewMembranes();
		
	}

	/**
	 * Gets all different objects within the rule
	 * 
	 * @return a set containing all different objects within the rule
	 * */
	@Override
	public Set<String> getObjects() {
		Set<String> set = leftHandRule.getObjects();
		set.addAll(rightHandRule.getObjects());
		return set;

	}

	
	

	protected void checkState() {

		/*
		 * There are several conditions a rule should comply with: a rule can't
		 * dissolve a membrane and create a new one
		 */
		if (dissolves && rightHandRule.getSecondOuterRuleMembrane() != null)
			throw new IllegalArgumentException(
					"A rule cannot perform a division and a dissolution at once");
		

		/*
		 * If a rule divides a membrane, both labels in the outer membranes
		 * should be equal
		 */
		if (rightHandRule.getSecondOuterRuleMembrane() != null) {

			if (!leftHandRule.getOuterRuleMembrane().getLabel().equals(
					rightHandRule.getOuterRuleMembrane().getLabel())
					|| !leftHandRule.getOuterRuleMembrane().getLabel().equals(
							rightHandRule.getSecondOuterRuleMembrane()
									.getLabel()))
				throw new IllegalArgumentException(
						"Membrane labels on both sides should match");
		}

		

	}

	
	@Override
	public long getRuleId() {
		return ruleId;
	}
	@Override
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}

	private void setHandCorrespondence() {
		/*
		 * In this method we need to match any inner membranes in the left hand
		 * and any inner membranes in the right hand, and detect those which
		 * have been created
		 */
		List<InnerRuleMembrane> lirm = leftHandRule.getOuterRuleMembrane()
				.getInnerRuleMembranes();
		int size = lirm.size();
		handCorrespondence = new InnerRuleMembrane[size];
		/* At first, there's no correspondence between hands */
		for (int i = 0; i < size; i++)
			handCorrespondence[i] = null;
		ListIterator<InnerRuleMembrane> lirmIterator = lirm.listIterator();
		int leftHandCounter = 0;
		/*
		 * Now we go through each inner rule membrane on the right hand for each
		 * inner rule membrane on the right hand
		 */
		List<InnerRuleMembrane> rirm = rightHandRule.getOuterRuleMembrane()
				.getInnerRuleMembranes();
		while (lirmIterator.hasNext()) {
			InnerRuleMembrane leftirm = lirmIterator.next();
			ListIterator<InnerRuleMembrane> rirmIterator = rirm.listIterator();
			while (rirmIterator.hasNext()) {
				InnerRuleMembrane rightirm = rirmIterator.next();
				if (rightirm.getLabel().equals(leftirm.getLabel())) {
					/*
					 * In case we find two inner membranes with the same label
					 * (each one on a different hand), we assume they correspond
					 * to each other
					 */
					handCorrespondence[leftHandCounter] = rightirm;
					break;
				}
			}
			leftHandCounter++;

		}

	}

	/**
	 * Gets if the rule dissolves the membrane which is applied to
	 * 
	 * @return a boolean representing if the rule dissolves the membrane which
	 *         is applied to
	 */
	@Override
	public boolean dissolves() {
		return dissolves;
	}

	/**
	 * Gets the left hand rule of the current rule
	 * 
	 * @return the left hand rule of the current rule
	 */
	@Override
	public LeftHandRule getLeftHandRule() {
		return leftHandRule;
	}

	/**
	 * Gets the right hand rule of the current rule
	 * 
	 * @return the right hand rule of the current rule
	 */
	@Override
	public RightHandRule getRightHandRule() {
		return rightHandRule;
	}

	
	
	
	/**
	 * Executes the rule on a membrane and an outer multiset and returns if the
	 * execution could be committed
	 * 
	 * @param membrane
	 *            the membrane which the rule will be applied to
	 * @param environment
	 *            the multiset out of the membrane
	 * @return if the execution could be committed
	 */
	@Override
	public boolean execute(ChangeableMembrane membrane,
			MultiSet<String> environment) {

		return execute(membrane, environment, countExecutions(membrane));
	}

	

	protected abstract boolean executeSafe(ChangeableMembrane membrane,
			MultiSet<String> environment, long executions);

	/**
	 * Executes the rule on a membrane and an outer multiset a number of times
	 * indicated by executions and returns if the execution could be committed
	 * 
	 * @param membrane
	 *            the membrane which the rule will be applied to
	 * @param environment
	 *            the multiset out of the membrane
	 * @param executions
	 *            the number of times to be executed on the membrane passed
	 * @return if the execution could be committed
	 */
	@Override
	public boolean execute(ChangeableMembrane membrane,
			MultiSet<String> environment, long executions) {
		if (executions > countExecutions(membrane)) {
			
			return false;
		}
		/*
		 * We only execute the rule if we have made sure it can be executed the
		 * number of executions passed as parameter
		 */
		return executeSafe(membrane, environment, executions);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "#"+ruleId+" "+leftHandRule.toString() + " --> " + rightHandRule.toString();

	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (dissolves ? 1231 : 1237);
		result = PRIME * result
				+ ((leftHandRule == null) ? 0 : leftHandRule.hashCode());
		result = PRIME * result
				+ ((rightHandRule == null) ? 0 : rightHandRule.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AbstractRule other = (AbstractRule) obj;
		if (dissolves != other.dissolves)
			return false;
		if (leftHandRule == null) {
			if (other.leftHandRule != null)
				return false;
		} else if (!leftHandRule.equals(other.leftHandRule))
			return false;
		if (rightHandRule == null) {
			if (other.rightHandRule != null)
				return false;
		} else if (!rightHandRule.equals(other.rightHandRule))
			return false;
		return true;
	}

	
	
	private void initializeNewMembranes() {
		int size = handCorrespondence.length;
		List<InnerRuleMembrane> rirm = getRightHandRule()
				.getOuterRuleMembrane().getInnerRuleMembranes();
		/* We construct the array containing all membranes created by the rule */
		newMembranes = new ArrayList<InnerRuleMembrane>();
		/*
		 * To fill in this array we add all inner membranes without any
		 * corresponding child membrane
		 */
		if (size < rirm.size()) {
			/* IPHM error getLeftHandRule x getRightHandRule */
			ListIterator<InnerRuleMembrane> rirmCheckIterator = getRightHandRule()
					.getOuterRuleMembrane().getInnerRuleMembranes()
					.listIterator();
			/* We go through each inner rule membrane on the right hand */
			while (rirmCheckIterator.hasNext()) {
				InnerRuleMembrane rirmMembrane = rirmCheckIterator.next();
				int i = 0;
				/*
				 * For each inner rule membrane, we should look for its
				 * corresponding inner membrane on the left hand
				 */
				for (i = 0; i < size; i++) {
					if (rirmMembrane == handCorrespondence[i])
						/*
						 * In case it exists, the inner rule membrane is not
						 * newly created
						 */
						break;

				}
				if (i >= size)
					newMembranes.add(rirmMembrane);
			}
		}

	}
	
	
	/**
	 * Reports if the cell-like rule creates new membranes when executed
	 * 
	 * @return a boolean which represents if the cell-like rule creates new
	 *         membranes when executed
	 */
	@Override
	public boolean hasNewMembranes() {
		return !newMembranes.isEmpty();
	}

	protected final static long multiSetCount(MultiSet<String> ruleMultiSet,
			MultiSet<String> membraneMultiSet) {
		return membraneMultiSet.countSubSets(ruleMultiSet);
	}
	
	protected final static boolean addMultiSet(MultiSet<String> ruleMultiSet,
			MultiSet<String> membraneMultiSet, long executions) {
		
		return membraneMultiSet.addAll(ruleMultiSet, executions);

	}
	
	protected final static void updateMembrane(ChangeableMembrane m, OuterRuleMembrane orm,
			long executions) {

		/*
		 * To update a membrane after a rule execution, we set the new charge
		 * and add the new objects
		 */
		m.setCharge(orm.getCharge());
		addMultiSet(orm.getMultiSet(), m.getMultiSet(), executions);

	}
	
	protected final static void subtractMultiSet(MultiSet<String> ruleMultiSet,
			MultiSet<String> membraneMultiSet, long executions) {
		// TODO Auto-generated method stub
		membraneMultiSet.subtraction(ruleMultiSet, executions);

	}
	
	protected final boolean checkLabel(ChangeableMembrane membrane)
	{
		return membrane.getLabel().equals(getLeftHandRule().getOuterRuleMembrane().getLabel());
	}

	@Override
	public boolean isExecutable(ChangeableMembrane membrane) {
		// TODO Auto-generated method stub
		return countExecutions(membrane)>0;
	}





}
