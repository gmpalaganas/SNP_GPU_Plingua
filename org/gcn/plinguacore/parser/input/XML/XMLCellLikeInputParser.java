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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.gcn.plinguacore.parser.input.InputParser;
import org.gcn.plinguacore.parser.input.VerbosityConstants;
import org.gcn.plinguacore.parser.input.messages.InputParserMsgFactory;
import org.gcn.plinguacore.util.HashMultiSet;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.Label;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.cellLike.CellLikePsystem;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembraneFactory;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeSkinMembrane;
import org.gcn.plinguacore.util.psystem.factory.AbstractPsystemFactory;
import org.gcn.plinguacore.util.psystem.factory.IPsystemFactory;
import org.gcn.plinguacore.util.psystem.factory.cellLike.AbstractCellLikePsystemFactory;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


import java.util.ListIterator;



/**
 * This class reads a P-system encoded on a XML format file
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public class XMLCellLikeInputParser extends InputParser {

	protected int readMultiplicity(String mul) throws PlinguaCoreException {
		int mulValue = 1;
		if (mul != null) {
			try {
				mulValue = Integer.parseInt(mul);
				/* If the multiplicity is 1, there's no need to specify it */
				if (mulValue == 1)
					unnecesaryAttribute("multiplicity", "1");
				/* Any multiplicity below 0 isn't valid */
				if (mulValue < 1)
					invalidValue("multiplicity", "less than 1");
			} catch (NumberFormatException ex) {
				unexpectedElement(mul);
			}
		}
		return mulValue;

	}

	private SAXBuilder builder = null;

	protected void unnecesaryEmptyCollection(String msg) {
		writeMsg(InputParserMsgFactory.createWarningMessage("If empty, " + msg
				+ " declaration isn't necessary"));
	}

	protected void unnecesaryAttribute(String attribute, String value) {
		writeMsg(InputParserMsgFactory
				.createWarningMessage("If " + attribute + " is " + value
						+ ", its explicit declaration isn't necessary"));
	}

	protected void doubleDeclaration(String msg) throws PlinguaCoreException {
		doubleDeclaration(msg, "");
	}

	protected void doubleDeclaration(String msg, String limit)
			throws PlinguaCoreException {
		writeMsg(InputParserMsgFactory.createSyntacticErrorMessage(msg
				+ " declared twice", msg + " should be declared once" + limit,
				null));
		throwException();
	}

	protected void requiredDeclarationMissed(String msg, String appendix)
			throws PlinguaCoreException {
		writeMsg(InputParserMsgFactory.createSyntacticErrorMessage(msg
				+ " undeclared", msg + " should be declared once" + appendix,
				null));
		throwException();
	}

	protected void requiredDeclarationMissed(String msg)
			throws PlinguaCoreException {
		requiredDeclarationMissed(msg, "");
	}

	protected void notifyBeforeReading(String msg){
		writeMsg(InputParserMsgFactory.createInfoMessage("Reading " + msg,
				VerbosityConstants.DETAILED_INFO));
	}

	protected void notifyAfterReading(String msg){
		notifyAfterReading(msg, null);
	}

	protected void notifyAfterReading(String msg, String value){
		writeMsg(InputParserMsgFactory.createInfoMessage(msg + " read", value,
				null, VerbosityConstants.DETAILED_INFO));
	}

	/**
	 * Creates a new XMLCellLikeInputParser instance
	 */
	public XMLCellLikeInputParser() {
		builder = new SAXBuilder();
	}

	private static void throwException() throws PlinguaCoreException {
		throw new PlinguaCoreException(
				"This XML file does not define a P system with active membranes.");
	}

	protected byte parseCharge(String charge) throws PlinguaCoreException {
		byte ch = 0;

		if (charge != null) {
			try {
				ch = Byte.parseByte(charge);
				if (ch > 0)
					ch = 1;
				else {
					if (ch < 0)
						ch = -1;
					else
						/*
						 * If the charge is equal to 0, there's no need to
						 * specify it
						 */
						unnecesaryAttribute("charge", "0");
				}
			} catch (NumberFormatException ex) {
				if (charge.equals("+1"))
					ch = 1;
				else if (charge.equals("-1"))
					ch = -1;
				else
					/*
					 * In case the charge doesn't represent a number, it's a
					 * parsing exception
					 */
					unexpectedElement(charge);
			}
		}
		return ch;

	}

	private CellLikeMembrane readMembranesRec(Element e,
			CellLikeMembrane parentMembrane) throws PlinguaCoreException {
		notifyBeforeReading("Membrane");
		/* A membrane is expected to be read */
		if (!e.getName().equals("membrane"))
			unexpectedElement(e.getText());
		String label = e.getAttributeValue("label");
		String charge = e.getAttributeValue("charge");
		/* The label is always compulsory */
		if (label == null)
			requiredDeclarationMissed("Label", " per membrane");
		CellLikeMembrane m = CellLikeMembraneFactory.getCellLikeMembrane(new Label(label),
				parentMembrane);
		m.setCharge(parseCharge(charge));
		ListIterator<?> li = e.getChildren().listIterator();
		/* Now, we go on to read all contents of the membrane */
		while (li.hasNext()) {
			Element elemMembrane = (Element) li.next();
			/* In case we're reading a membrane, it should be read recursively */
			if (elemMembrane.getName().equals("membrane"))
				readMembranesRec(elemMembrane, m);
			else {
				/* In case we're reading a multiset, the should be only one */
				if (elemMembrane.getName().equals("multiset")) {
					if (m.getMultiSet().isEmpty())
						m.getMultiSet().addAll(
								readSpecificMultiSet(elemMembrane));
					else
						doubleDeclaration("Multiset", " or never per membrane");
				} else
					/*
					 * If this element isn't a membrane nor a multiset, it's an
					 * unexpected element resulting in a parsing error
					 */
					unexpectedElement(elemMembrane.getName());
			}

		}
		notifyAfterReading("Membrane", m.toString());
		return m;
	}

	private void readMembranes(Element e, CellLikePsystem ps)
			throws PlinguaCoreException {
		notifyBeforeReading("Initial configuration");
		List<?> l = e.getChildren();
		if (l == null) {
			notifyAfterReading("Initial configuration");
			return;
		}
		/* There should be one and only one skin membrane */
		if (l.size() > 1)
			doubleDeclaration("Skin membrane");
		if (l.size() < 1)
			requiredDeclarationMissed("Skin membrane");
		Element m = (Element) l.get(0);
		notifyBeforeReading("Skin membrane");
		/*
		 * One we've made sure there's only one skin membrane, we go on to read
		 * it
		 */
		CellLikeMembrane skin = readMembranesRec(m, null);
		notifyAfterReading("Skin membrane");
		ps.setMembraneStructure((CellLikeSkinMembrane) skin);
		notifyAfterReading("Initial configuration");
	}

	protected MultiSet<String> readSpecificMultiSet(Element m)
			throws PlinguaCoreException {
		notifyBeforeReading("Multiset");
		/* A null pointer corresponds to an empty multiset */
		if (m == null) {
			notifyAfterReading("Multiset");
			return new HashMultiSet<String>();
		}
		/*
		 * We make sure we're reading a multiset, otherwise it will result in a
		 * parsing error
		 */
		if (!m.getName().equals("multiset"))
			unexpectedElement(m.getName());

		MultiSet<String> ms = new HashMultiSet<String>();
		/* Finally, we read all objects in the multiset one by one */
		ListIterator<?> ito = m.getChildren().listIterator();
		while (ito.hasNext()) {
			notifyBeforeReading("Object");
			Element k = (Element) ito.next();
			/* Each object should be tagged as such */
			if (!k.getName().equals("object"))
				unexpectedElement(k.getName());
			String name = k.getAttributeValue("name");
			String mul = k.getAttributeValue("multiplicity");
			/* The object name is compulsory */
			if (name == null)
				requiredDeclarationMissed("name");
			int mulValue = readMultiplicity(mul);

			ms.add(name, mulValue);
			notifyAfterReading("Object");
		}
		if (ms.isEmpty())
			/* If the element is empty, there's no need to specify it */
			unnecesaryEmptyCollection("Multiset");
		notifyAfterReading("Multiset", ms.toString());
		return ms;

	}

	private void readMultiSets(Element e, CellLikePsystem ps)
			throws PlinguaCoreException {
		notifyBeforeReading("Initial multisets");
		List<?> l = e.getChildren();
		/* If there's no initial multisets, we're done */
		if (l == null) {
			notifyAfterReading("Initial multisets");
			return;
		}
		/*
		 * If there's an empty set of initial multisets, there's no need to
		 * specify it
		 */
		ListIterator<?> it = l.listIterator();
		if (l.isEmpty())
			unnecesaryEmptyCollection("initial multisets");

		Map<String, MultiSet<String>> initialMultiSets = ps
				.getInitialMultiSets();
		while (it.hasNext()) {
			Element m = (Element) it.next();
			String label = m.getAttributeValue("label");
			/*
			 * Initial multiset label is compulsory, otherwise it would be
			 * impossible to assign each multiset to its corresponding initial
			 * membrane
			 */
			if (label == null)
				requiredDeclarationMissed("Label", " per initial multiset");
			initialMultiSets.put(label, readSpecificMultiSet(m));
		}
		notifyAfterReading("Initial multisets");
	}

	protected void checkElementName(Element e, String expectedName)
			throws PlinguaCoreException {
		if (!e.getName().equals(expectedName))
			unexpectedElement(e.getName(), expectedName);
	}

	private void readRules(Element e, CellLikePsystem ps)
			throws PlinguaCoreException {
		notifyBeforeReading("Rules");
		List<?> l = e.getChildren();
		/* If the rules element is null, we're done */
		if (l == null) {
			notifyAfterReading("Rules");
			return;
		}
		ListIterator<?> li = l.listIterator();
		while (li.hasNext()) {
			Element r = (Element) li.next();
			/*
			 * As we recognize several ways to represent rules, we use the
			 * Strategy pattern for reading rules
			 */
			XMLReadCellLikeRule cellLikeReader = ReadRuleFactory
					.createReadRule(r.getName(), this);
			IRule rule = cellLikeReader.readRule(r,ps.getAbstractPsystemFactory().getRuleFactory());
			ps.addRule(rule);

		}
		notifyAfterReading("Rules");
	}

	private CellLikePsystem readDocument(Document d)
			throws PlinguaCoreException {

		Element root = null, membranes = null, multisets = null, rules = null;
		root = d.getRootElement();
		//CellLikePsystem psystem = new CellLikePsystem();
		/*
		 * In pLinguaCore version 1.0, we could only read membrane division
		 * systems, tagged by "active_membrane_psystem"
		 */
		String model;
		if (root.getName().equals("active_membrane_psystem"))
			model = "membrane_division";
		else
			/* Now, we can read a wide range of different models */
			model = root.getAttributeValue("model");
		
		IPsystemFactory factory = createAbstractPsystemFactory(model);
		
		if (!(factory instanceof AbstractCellLikePsystemFactory))
		{
			writeMsg(InputParserMsgFactory.createSemanticsErrorMessage("Only cell-like P systems can be defined in XML format"));
			throwException();
		}

		CellLikePsystem psystem=(CellLikePsystem)factory.createPsystem();
		
		
		ListIterator<?> li = root.getChildren().listIterator();
		while (li.hasNext()) {
			Element e = (Element) li.next();
			/* The initial configuration should be declared once and only once */
			if (e.getName().equals("init_config")) {
				if (membranes != null) {
					doubleDeclaration("Initial configuration");
				}
				membranes = e;
				readMembranes(e, psystem);
				/* The initial multisets should be declared once or never */
			} else if (e.getName().equals("multisets")) {
				if (multisets != null) {
					doubleDeclaration("Initial multisets", " or never");
				}
				multisets = e;

				readMultiSets(e, psystem);
				notifyAfterReading("Initial multisets");
				/* The rules set should be declared once and only once */
			} else if (e.getName().equals("rules")) {
				if (rules != null)
					doubleDeclaration("Rules");
				rules = e;

				readRules(e, psystem);
				notifyAfterReading("Rules");
			} else {
				unexpectedElement(e.getText());
			}
		}
		/* The initial configuration and the rules set should be declared */
		if (membranes == null) {
			requiredDeclarationMissed("Initial configuration");
		}
		if (rules == null) {
			requiredDeclarationMissed("Rules");
		}

		return psystem;
	}

	protected void unexpectedElement(String msg) throws PlinguaCoreException {
		writeMsg(InputParserMsgFactory
				.createSyntacticErrorMessage("Unexpected element: " + msg));
		throwException();
	}

	protected void unexpectedElement(String msg, String expected)
			throws PlinguaCoreException {
		unexpectedElement(msg + ", expected " + expected);
	}

	protected void invalidValue(String msg, String value)
			throws PlinguaCoreException {
		writeMsg(InputParserMsgFactory.createSemanticsErrorMessage(msg
				+ " value shouldn't be " + value));
		throwException();
	}

	private Psystem parseInput(Object ob) throws PlinguaCoreException {
		Document d = null;
		try {
			/* Both an InputStream and a StringReader can be read */
			if (ob instanceof InputStream)
				d = builder.build((InputStream) ob);
			else {
				if (ob instanceof StringReader)
					d = builder.build((StringReader) ob);
				else
					throw new IllegalArgumentException(
							"The parameter should be an InputStream or StringReader instance");
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			throw new PlinguaCoreException("The input file is not an XML file");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new PlinguaCoreException(e.getMessage());
		}
		writeMsg(InputParserMsgFactory.createInfoMessage("Reading P-system",
				VerbosityConstants.GENERAL_INFO));
		Psystem ps = readDocument(d);
		writeMsg(InputParserMsgFactory.createInfoMessage("P-system read",
				VerbosityConstants.GENERAL_INFO));
		return ps;
	}


	/* (non-Javadoc)
	 * @see parser.input.InputParser#specificParse(java.io.InputStream, java.lang.String[])
	 */
	@Override
	protected Psystem specificParse(InputStream stream, String fileRoute[])
			throws PlinguaCoreException {
		// TODO Auto-generated method stub
		checkFileRoute(fileRoute);
		return parseInput(stream);
	}


	/* (non-Javadoc)
	 * @see parser.input.InputParser#specificParse(java.io.StringReader, java.lang.String[])
	 */
	@Override
	protected Psystem specificParse(StringReader reader, String fileRoute[])
			throws PlinguaCoreException {
		checkFileRoute(fileRoute);
		// TODO Auto-generated method stub
		return parseInput(reader);
	}

	private void checkFileRoute(String[] fileRoute) throws PlinguaCoreException {
		if (fileRoute.length != 1)
			throw new PlinguaCoreException(
					"in XML input parse files, fileRoute array should have one and only one file route");

	}

}
