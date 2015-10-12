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

package org.gcn.plinguacore.parser.output.XML;

import java.io.OutputStream;
import java.io.PrintWriter;

import org.gcn.plinguacore.parser.output.OutputParser;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.cellLike.CellLikeConfiguration;
import org.gcn.plinguacore.util.psystem.cellLike.CellLikePsystem;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeNoSkinMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeSkinMembrane;
import org.gcn.plinguacore.util.psystem.rule.IConstantRule;
import org.gcn.plinguacore.util.psystem.rule.IPriorityRule;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.InnerRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.LeftHandRule;
import org.gcn.plinguacore.util.psystem.rule.OuterRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.RightHandRule;

import org.jdom.Attribute;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;



import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * This class writes a P system in an XML  file
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public class XMLCellLikeOutputParser extends OutputParser {

	/**
	 * Resorts to the super constructor
	 */
	public XMLCellLikeOutputParser() {
		super();
	}

	/**
	 * Generates the document of a P-System
	 * 
	 * @param psystem
	 *            the P-System which document will be generated
	 * @return the generated document
	 */
	public Document generateDocument(CellLikePsystem psystem) {
		Document d = new Document();
		Element e = new Element("cell_like_psystem");
		e.setAttribute(new Attribute("version", "1.0"));
		e.setAttribute(new Attribute("model", psystem
				.getAbstractPsystemFactory().getModelName()));
		Comment c = new Comment(
				"File generated with P-Lingua Compiler 1.0 beta\n"
						+ "See http://www.gcn.us.es for more information.");
		/* First, we add the comments */
		d.addContent(c);
		/* Then, we add the initial configuration */
		e.addContent(generateInitConfigElement((CellLikeConfiguration) psystem
				.getFirstConfiguration()));
		/* At last, we add the rules */
		e.addContent(generateRulesElement(psystem.getRules().iterator()));
		d.setRootElement(e);
		return d;
	}

	private Element generateLeftHandRuleElement(LeftHandRule lhr) {
		Element result = new Element("left_hand_rule");
		/*
		 * The left hand rule consist of an outer rule membrane and an outer
		 * multiset
		 */
		Element multisetElement = generateMultiSetElement(lhr.getMultiSet());
		Element outerMembraneElement = generateOuterMembraneElement(lhr
				.getOuterRuleMembrane());
		/* Once read, they're added to the resulting element */
		if (multisetElement != null)
			result.addContent(multisetElement);
		result.addContent(outerMembraneElement);
		return result;
	}

	private Element generateInnerRuleMembranesElement(
			List<InnerRuleMembrane> irms) {
		if (irms.isEmpty())
			return null;
		/* The resulting element is created */
		Element result = new Element("inner_rule_membranes");
		/* For each membrane child, a new inner rule membrane is written */
		ListIterator<InnerRuleMembrane> lirm = irms.listIterator();
		while (lirm.hasNext()) {
			Element irm = generateInnerRuleMembraneElement(lirm.next());
			result.addContent(irm);
		}
		return result;
	}

	private Element generateInnerRuleMembraneElement(InnerRuleMembrane irm) {
		/* The charge and label are written */
		Element result = new Element("inner_membrane");
		parseCharge(result, irm.getCharge());
		result.setAttribute("label", irm.getLabel());
		/* Then, we go on to read the multiset in the inner membrane */
		Element msElement = generateMultiSetElement(irm.getMultiSet());
		if (msElement != null)
			result.addContent(msElement);
		return result;
	}

	private void parseCharge(Element e, short ch) {
		/* The charge is specified only if it's not 0 */
		String charge = null;
		if (ch > 0)
			charge = "+1";
		else if (ch < 0)
			charge = "-1";
		if (charge != null)
			e.setAttribute(new Attribute("charge", charge));

	}

	
	private Element generateOuterMembraneElement(OuterRuleMembrane orl){
		return generateOuterMembraneElement("", orl);
	}
	
	private Element generateOuterMembraneElement(String prefix, OuterRuleMembrane orl) {
		/*
		 * In order to generate an element representing the outer rule membrane
		 * we need to write the label and charge
		 */
		Element result = new Element(prefix+"outer_membrane");
		result.setAttribute("label", orl.getLabel());
		parseCharge(result, orl.getCharge());
		/*
		 * In case the outer membrane contains inner rule membranes or a
		 * multiset, they should be written
		 */
		Element multisetElement = generateMultiSetElement(orl.getMultiSet());
		Element innerMembranesElement = generateInnerRuleMembranesElement(orl
				.getInnerRuleMembranes());
		if (multisetElement != null)
			result.addContent(multisetElement);
		if (innerMembranesElement != null)
			result.addContent(innerMembranesElement);
		return result;
	}

	private Element generateMultiSetElement(MultiSet<String> ms) {
		Iterator<String> it1 = ms.entrySet().iterator();
		/* If the multiset to generate is empty, we don't add such element */
		if (ms.isEmpty())
			return null;
		Element e1 = new Element("multiset");
		/* Otherwise, we need to add it object by object */
		while (it1.hasNext()) {
			String obj = it1.next();
			long mult = ms.count(obj);
			/*
			 * Each object contains information related to its name and
			 * multiplicity
			 */
			Element e2 = new Element("object");
			e2.setAttribute("name", obj);
			/*
			 * The multiplicity needs to be written only in case it's greater
			 * than 1
			 */
			if (mult > 1)
				e2.setAttribute("multiplicity", Long.toString(mult));
			e1.addContent(e2);
		}

		return e1;
	}

	private Element generateRuleElement(IRule clr) {
		Element result = new Element("rule");
		/* If the rule is dissolved, we need to specify it */
		if (clr.dissolves())
			result.setAttribute("dissolves", clr.dissolves() + "");
		/*
		 * In case we're writing a rule with priority or ratio, we need to
		 * specify both fields
		 */
		if (clr instanceof IPriorityRule)
			result.setAttribute("priority", ((IPriorityRule) clr)
					.getPriority()
					+ "");
		else {
			if (clr instanceof IConstantRule)
				result.setAttribute("ratio", ((IConstantRule) clr)
						.getConstant()
						+ "");
		}
		/* Finally, each hand rule is read */
		Element leftHand = generateLeftHandRuleElement(clr.getLeftHandRule());
		Element rightHand = generateRightHandRuleElement(clr.getRightHandRule());
		result.addContent(leftHand);
		result.addContent(rightHand);
		return result;
	}

	private Element generateRightHandRuleElement(RightHandRule rhr) {
		Element result = new Element("right_hand_rule");
		/*
		 * The right hand rule consists of the same elements ghe left hand rule
		 * does, so we need to write them
		 */
		Element multiset = generateMultiSetElement(rhr.getMultiSet());
		Element outerMembrane = generateOuterMembraneElement(rhr
				.getOuterRuleMembrane());
		if (multiset != null)
			result.addContent(multiset);
		result.addContent(outerMembrane);
		/*
		 * The only exception to the rule comes up if there's membrane division,
		 * in such case we need to write a second outer membrane
		 */
		OuterRuleMembrane sorm = rhr.getSecondOuterRuleMembrane();
		if (sorm != null) {
			Element secondOuterMembrane = generateOuterMembraneElement("second_", sorm);
			result.addContent(secondOuterMembrane);			
		}
		return result;
	}

	private Element generateRulesElement(Iterator<IRule> it) {
		Element e = new Element("rules");
		/* All rules are written one by one */
		while (it.hasNext()) {
			IRule r = it.next();
			e.addContent(generateRuleElement(r));
		}
		return e;

	}

	private Element generateInitConfigElement(CellLikeConfiguration config) {

		Element e = new Element("init_config");
		/* In case there's a skin membrane, it should be written */
		if (config.getMembraneStructure() != null)
			e.addContent(generateMembraneElement(((CellLikeSkinMembrane)config.getMembraneStructure())));
		return e;
	}

	private Element generateMembraneElement(CellLikeMembrane clm) {
		Element e = new Element("membrane");
		/* First, we read the label and the charge */
		e.setAttribute(new Attribute("label", clm.getLabel()));
		parseCharge(e, clm.getCharge());
		/*
		 * Then, we read the multiset and, in case it isn't empty, it should be
		 * written
		 */
		MultiSet<String> multiset = clm.getMultiSet();
		Element multisetE = generateMultiSetElement(multiset);
		if (multisetE != null)
			e.addContent(multisetE);
		/* Finally, we read all child membranes */
		Iterator<CellLikeNoSkinMembrane> it = clm.getChildMembranes()
				.iterator();
		while (it.hasNext()) {
			CellLikeMembrane m = it.next();
			Element e1 = generateMembraneElement(m);
			e.addContent(e1);
		}

		return e;
	}

	/**
	 * @see org.gcn.plinguacore.parser.output.OutputParser#parse(org.gcn.plinguacore.util.psystem.Psystem,
	 *      java.io.OutputStream)
	 */
	@Override
	public boolean parse(Psystem psystem, OutputStream stream) {
		return parseOutput((CellLikePsystem) psystem, stream);
	}

	private boolean parseOutput(CellLikePsystem psystem, Object ob) {
		/* We can write P-systems on Write or OutputStream instances */
		if (psystem == null)
			return false;
		XMLOutputter x = new XMLOutputter(Format.getPrettyFormat());
		try {
			if (ob instanceof Writer)
				x.output(generateDocument(psystem), (Writer) ob);
			else {
				if (ob instanceof OutputStream)
					x.output(generateDocument(psystem), new PrintWriter(
							(OutputStream) ob));
				else
					throw new IllegalArgumentException(
							"Object argument should be an instance of OutputStream or Writer");
			}
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * @see org.gcn.plinguacore.parser.output.OutputParser#parse(org.gcn.plinguacore.util.psystem.Psystem,
	 *      java.io.Writer)
	 */
	@Override
	public boolean parse(Psystem psystem, Writer writer) {

		return parseOutput((CellLikePsystem) psystem, writer);

	}
}
