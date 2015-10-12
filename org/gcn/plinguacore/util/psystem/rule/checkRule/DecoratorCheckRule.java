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

package org.gcn.plinguacore.util.psystem.rule.checkRule;

import java.util.List;

import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoCooperation;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoCreation;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoDifferentMainLabels;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoDissolution;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoDivision;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoLeftExternalMultiSet;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoLeftInnerMembranes;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoMultipleProduction;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoRightExternalMultiSet;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoRightInnerMembranes;


/**
 * This class provides the basic functionality for all CheckRule instances which
 * wrap another instance, as specified by Decorator pattern
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */

public abstract class DecoratorCheckRule implements InnerCheckRule {

	private static final long serialVersionUID = 339922718160092202L;

	/*
	 * As several CheckRule objects are used in some children class, we declare
	 * in the parent class
	 */
	protected final static NoLeftExternalMultiSet noLeftExternalMultiSetCheckRule = new NoLeftExternalMultiSet(
			new BaseCheckRule());

	protected final static NoRightExternalMultiSet noRightExternalMultiSetCheckRule = new NoRightExternalMultiSet(
			new BaseCheckRule());

	protected final static CheckRule evolutionCheckRule = new NoLeftExternalMultiSet(
			new NoRightExternalMultiSet(new NoLeftInnerMembranes(
					new NoRightInnerMembranes(new NoDissolution(new NoDifferentMainLabels(new NoDivision(
							new BaseCheckRule())))))));

	protected final static CheckRule noCreationCheckRule = new NoCreation(
			new BaseCheckRule());

	protected final static CheckRule noDissolutionCheckRule = new NoDissolution(
			new BaseCheckRule());

	protected final static CheckRule noDivisionCheckRule = new NoDivision(
			new BaseCheckRule());
	
	protected final static CheckRule noCooperationCheckRule = new NoCooperation(new BaseCheckRule());

	protected final static CheckRule noMultipleProductionCheckRule = new NoMultipleProduction(new BaseCheckRule());

	/**
	 * Gets if the simulator message causes have been initialized
	 * @return true if the causes have been initialized, false otherwise
	 */
	public boolean initializedCauses() {
		// TODO Auto-generated method stub
		return decorated.initializedCauses();
	}

	protected InnerCheckRule decorated;

	/**
	 * Creates a new DecoratorCheckRule instance, which checks only the restrictions defined on itself.
	 */
	public DecoratorCheckRule(){
		this(new BaseCheckRule());
	}
	
	
	/**
	 * Creates a new DecoratorCheckRule instance, which wraps cr as stated
	 * by Decorator pattern. Thus, it will be capable to test both the instance restrictions and cr restrictions 
	 * 
	 * @param cr
	 *            the CheckRule instance to be wrapped
	 */
	public DecoratorCheckRule(CheckRule cr) {
		if (cr == null)
			throw new NullPointerException(
					"CheckRule argument shouldn't be null");
		this.decorated = (InnerCheckRule) cr;
	}

	/**
	 * @see org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule#checkRule(org.gcn.plinguacore.util.psystem.rule.Rule)
	 */
	@Override
	public boolean checkRule(IRule r) {
		// TODO Auto-generated method stub
		boolean b = checkSpecificPart(r);

		if (!b) {
			if (!initializedCauses()) {
				initCauses();
				getCauses().clear();

			}
			getCauses().add(getSpecificCause());
		}

		return b & decorated.checkRule(r);
	}


	/**
	 * Initializes the simulator message causes
	 */
	public void initCauses() {
		decorated.initCauses();

	}


	/**
	 * Gets a string reporting the causes of the simulator messages
	 * @return a string reporting the causes of the simulator messages
	 * 
	 */
	public String getCausesString() {
		// TODO Auto-generated method stub
		return decorated.getCausesString();
	}

	/**
	 * @see org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule#getCauses()
	 */
	public List<String> getCauses() {
		return decorated.getCauses();
	}

	protected abstract boolean checkSpecificPart(IRule r);

	protected abstract String getSpecificCause();

}
