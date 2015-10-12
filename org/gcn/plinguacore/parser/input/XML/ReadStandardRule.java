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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.Label;
import org.gcn.plinguacore.util.psystem.rule.AbstractRuleFactory;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.InnerRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.LeftHandRule;
import org.gcn.plinguacore.util.psystem.rule.OuterRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.RightHandRule;
import org.jdom.Element;


/**
 * This class reads current format rules
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
class ReadStandardRule extends XMLReadCellLikeRule {

	/**
	 * Creates a new instance which will be used by the argument passed
	 * 
	 * @param cellLikeInputParser
	 *            the XMLCellLikeInputParser instance which will use this
	 *            instance
	 */
	public ReadStandardRule(XMLCellLikeInputParser cellLikeInputParser) {
		super(cellLikeInputParser);
		// TODO Auto-generated constructor stub
	}

	/* This method reads a list of inner rule membranes from an XML element */
	private List<InnerRuleMembrane> readInnerRuleMembranes(Element ims)
			throws PlinguaCoreException {
		notifyBeforeReading("Inner Rule Membranes");
		/* If the element is null, an empty list is returned */
		if (ims == null) {

			notifyAfterReading("Inner Rule Membranes");
			return new ArrayList<InnerRuleMembrane>();
		}

		/* Otherwise, the reader goes on to check if the element name is right */
		checkElementName(ims, "inner_rule_membranes");
		List<InnerRuleMembrane> result = new LinkedList<InnerRuleMembrane>();
		/* If the list of children is empty, it's a warning to notify */
		List<?> children = ims.getChildren();
		if (children.isEmpty())
			unnecesaryEmptyCollection("Inner Rule Membranes");
		/* Otherwise, all membranes in the list are read */
		ListIterator<?> innerMembranes = children.listIterator();
		while (innerMembranes.hasNext()) {
			result.add(readSpecificInnerRuleMembrane((Element) innerMembranes
					.next()));
		}
		notifyAfterReading("Inner Rule Membranes");
		return result;
	}

	private OuterRuleMembrane readOuterMembrane(Element om, String second)
			throws PlinguaCoreException {
		notifyBeforeReading("Outer Rule Membrane");
		/* The reader checks if the element name matches "second_outer_membrane" */
		checkElementName(om, second + "outer_membrane");
		/* The name and charge are read */
		String label = om.getAttributeValue("label");
		byte charge = 0;
		charge = parseCharge(om.getAttributeValue("charge"));
		/*
		 * The multiset and the inner rule membranes within the outer rule
		 * membrane are read
		 */
		MultiSet<String> innerMultiSet = readSpecificMultiSet(om
				.getChild("multiset"));
		List<InnerRuleMembrane> col = readInnerRuleMembranes(om
				.getChild("inner_rule_membranes"));
		/* Eventually, the outer rule membrane is created and returned */
		OuterRuleMembrane result = new OuterRuleMembrane(new Label(label), charge,
				innerMultiSet, col);
		notifyAfterReading("Outer Rule Membrane", result.toString());
		return result;
	}

	@Override
	protected IRule readRule(Element o,AbstractRuleFactory factory) throws PlinguaCoreException {
		
		notifyBeforeReading("Rule");
		checkElementName(o, "rule");
		Element lhrElement = o.getChild("left_hand_rule");
		/* First, both rule hands are read */
		LeftHandRule lhr = readLeftHandRule(lhrElement);
		Element rhrElement = o.getChild("right_hand_rule");
		RightHandRule rhr = readRightHandRule(rhrElement);
		/* Then, the reader goes on to read if the rule dissolves the membrane */
		boolean dissolves = Boolean.parseBoolean(o
				.getAttributeValue("dissolves"));
		String attrRatio = o.getAttributeValue("ratio");
		float ratio = (float) 1.0;
		/* Then, the ratio is read. If it's 1.0, there's no need to specify it */
		if (attrRatio != null) {
			ratio = Float.parseFloat(attrRatio);
			if (ratio == 1.0)
				unnecesaryAttribute("Ratio", "1.0");
		}
		/*
		 * Then, the priority is read. Note that the same rule can't hold both
		 * priority and ratio at the same time
		 */
		String attrPriority = o.getAttributeValue("priority");
		int priority = 1;
		if (attrPriority != null) {
			if (attrRatio != null)
				unexpectedElement("Priority in RatioCellLikeRule");
			priority = Integer.parseInt(attrPriority);
			if (priority == 1)
				unnecesaryAttribute("Priority", "1");
			if (priority < 0)
				invalidValue("Priority", "less than 0");
		}
		IRule result;
		/* Eventually, the rule is created according to the parameters given */
		if (attrPriority == null) {
			if (attrRatio == null)
				result = factory.createBasicRule(dissolves, lhr, rhr);
			else
				result = factory.createConstantRule(dissolves, lhr, rhr, ratio);
		} else
			result = factory.createPriorityRule(dissolves, lhr, rhr, priority);
		notifyAfterReading("Rule", result.toString());
		return result;

	}

	private RightHandRule readRightHandRule(Element rhr)
			throws PlinguaCoreException {
		notifyBeforeReading("Right Hand Rule");
		checkElementName(rhr, "right_hand_rule");
		/* First, the outer multiset and the outer rule membrane are read */
		MultiSet<String> outerMultiSet = readSpecificMultiSet(rhr
				.getChild("multiset"));
		OuterRuleMembrane outerMembrane = readOuterMembrane(rhr
				.getChild("outer_membrane"), "");
		RightHandRule rightHandRule;
		/*
		 * If there's a second membrane, it means the rule performs division on
		 * the membrane and, hence, a new outer membrane is created
		 */
		if (rhr.getChild("second_outer_membrane") != null) {
			OuterRuleMembrane secondOuterMembrane = readOuterMembrane(rhr
					.getChild("second_outer_membrane"), "second_");
			rightHandRule = new RightHandRule(outerMembrane,
					secondOuterMembrane, outerMultiSet);

		} else {
			rightHandRule = new RightHandRule(outerMembrane, outerMultiSet);
		}
		notifyAfterReading("Right Hand Rule", rightHandRule.toString());
		return rightHandRule;
	}

	@Override
	protected LeftHandRule readLeftHandRule(Element lhr)
			throws PlinguaCoreException {
		notifyBeforeReading("Left Hand Rule");
		checkElementName(lhr, "left_hand_rule");
		/* First, the outer multiset and the outer rule membrane are read */
		MultiSet<String> outerMultiSet = readSpecificMultiSet(lhr
				.getChild("multiset"));
		OuterRuleMembrane outerMembrane = readOuterMembrane(lhr
				.getChild("outer_membrane"), "");
		/*
		 * Eventually, the left hand rule is created according to the elements
		 * read
		 */
		LeftHandRule leftHandRule = new LeftHandRule(outerMembrane,
				outerMultiSet);
		notifyAfterReading("Left Hand Rule", leftHandRule.toString());
		return leftHandRule;
	}

	private InnerRuleMembrane readSpecificInnerRuleMembrane(Element im)
			throws PlinguaCoreException {
		notifyBeforeReading("Inner Rule Membrane");
		checkElementName(im, "inner_membrane");
		String label = im.getAttributeValue("label");
		if (label == null)
			requiredDeclarationMissed("Label", " per Membrane");
		byte charge = parseCharge(im.getAttributeValue("charge"));
		MultiSet<String> innerMultiSet = readSpecificMultiSet(im
				.getChild("multiset"));
		InnerRuleMembrane result = new InnerRuleMembrane(new Label(label), charge,
				innerMultiSet);
		notifyAfterReading("Inner Rule Membrane", result.toString());
		return result;
	}

}
