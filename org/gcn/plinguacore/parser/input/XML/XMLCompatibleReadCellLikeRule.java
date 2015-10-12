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


package org.gcn.plinguacore.parser.input.XML;

import java.util.Iterator;
import java.util.List;

import org.gcn.plinguacore.util.HashMultiSet;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.Label;
import org.gcn.plinguacore.util.psystem.rule.AbstractRuleFactory;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.LeftHandRule;
import org.gcn.plinguacore.util.psystem.rule.OuterRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.RightHandRule;

import org.jdom.Element;


/**
 * This class provides the basic functionality for reading rules related to the
 * original format
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
abstract class XMLCompatibleReadCellLikeRule extends XMLReadCellLikeRule {

	protected boolean dissolves;
	protected String label;
	protected byte charge;
	protected MultiSet<String> rightInnerMultiSet;
	protected MultiSet<String> rightOuterMultiSet;

	/**
	 * Creates a new instance which will be used by the XMLCellLikeInputParser
	 * passed as argument
	 * 
	 * @param cellLikeInputParser
	 *            the input parser which uses this instance
	 */
	public XMLCompatibleReadCellLikeRule(
			XMLCellLikeInputParser cellLikeInputParser) {
		super(cellLikeInputParser);
		// TODO Auto-generated constructor stub
	}

	protected LeftHandRule constructLeftHandRule(String label, byte charge,
			String object) {
		MultiSet<String> leftMultiSet = new HashMultiSet<String>();
		leftMultiSet.add(object);
		return new LeftHandRule(new OuterRuleMembrane(new Label(label), charge,
				leftMultiSet), new HashMultiSet<String>());
	}

	@Override
	protected LeftHandRule readLeftHandRule(Element leftHandElement)
			throws PlinguaCoreException {
		// TODO Auto-generated method stub
		/* First of all, the left hand shouldn't be null */
		if (leftHandRule != null)
			doubleDeclaration("Left Hand Rule Object", " per rule");
		notifyBeforeReading("Left Hand Rule");
		/* The left hand multiset should contain only une object */
		String name = leftHandElement.getAttributeValue("object");
		notifyAfterReading("Left Hand Rule");
		return constructLeftHandRule(label, charge, name);
	}

	protected Iterator<?> initializeRuleFeatures(Element rule)
			throws PlinguaCoreException {
		label = rule.getAttributeValue("label");
		/* The membrane label shouldn't be null */
		if (label == null)
			requiredDeclarationMissed("label", " in each rule");
		String chargeString = rule.getAttributeValue("charge");
		dissolves = false;
		/* We read the charge and initialize both inner and outer multiset */
		charge = parseCharge(chargeString);
		List<?> l = rule.getChildren();
		rightInnerMultiSet = new HashMultiSet<String>();
		rightOuterMultiSet = new HashMultiSet<String>();
		if (l == null)
			requiredDeclarationMissed("Hand Rules");
		return l.iterator();
	}

	protected void addRightHandRuleObject(String object, int multiplicity) {
		rightInnerMultiSet.add(object, multiplicity);
	}

	protected abstract void checkConsistency() throws PlinguaCoreException;

	@Override
	protected IRule readRule(Element e,AbstractRuleFactory factory) throws PlinguaCoreException {
		notifyBeforeReading("Evolution Rule");
		IRule r;

		Iterator<?> iterator = initializeRuleFeatures(e);
		/* Each rule should have one left hand rule and several right hand rules */
		while (iterator.hasNext()) {
			Element o = (Element) iterator.next();
			/*
			 * Depending on the element label, we read a left hand role or a
			 * right hand rule
			 */
			if (o.getName().equals("left_hand_rule")) {
				leftHandRule = readLeftHandRule(o);
			} else if (o.getName().equals("right_hand_rule")) {
				readRightHandRule(o);

			} else
				unexpectedElement(o.getName());

		}
		/*
		 * At last, we build the ultimate right hand rule based on the right
		 * hand rules previoulsy read
		 */
		rightHandRule = buildRightHandRule();
		if (leftHandRule == null)
			requiredDeclarationMissed("Left Hand Rule");
		checkConsistency();
		r = factory.createBasicRule(dissolves, leftHandRule, rightHandRule);
		return r;
	}

	protected abstract RightHandRule buildRightHandRule();

	protected abstract void readRightHandRule(Element rightHandElement)
			throws PlinguaCoreException;

}
