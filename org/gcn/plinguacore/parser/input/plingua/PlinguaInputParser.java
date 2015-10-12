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

package org.gcn.plinguacore.parser.input.plingua;

import java.io.InputStream;
import java.io.StringReader;
import java.util.Map;
import java.util.HashMap;

import org.gcn.plinguacore.parser.input.InputParser;
import org.gcn.plinguacore.parser.input.messages.InputParserMsg;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.Psystem;

/**
 * This class reads a P system encoded on a P-Lingua file
 * 
 * @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public class PlinguaInputParser extends InputParser {

	private Map<String, Number> parameters;

	public PlinguaInputParser() {
		super();
		parameters = new HashMap<String, Number>();
	}

	/**
	 * @return a Map<String, Number> of initial P system parameters. It is
	 *         possible to set the initial parameters before parsing the
	 *         P-Lingua file.
	 */
	public Map<String, Number> getParameters() {
		return parameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.gcn.plinguacore.parser.input.InputParser#specificParse(java.io.
	 * InputStream, java.lang.String[])
	 */
	@Override
	public Psystem specificParse(InputStream stream, String fileRoute[])
			throws PlinguaCoreException {

		return PlinguaJavaCcParser.parse(stream, this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.gcn.plinguacore.parser.input.InputParser#specificParse(java.io.
	 * StringReader, java.lang.String[])
	 */
	@Override
	public Psystem specificParse(StringReader reader, String fileRoute[])
			throws PlinguaCoreException {

		return PlinguaJavaCcParser.parse(reader, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gcn.plinguacore.parser.input.InputParser#writeMsg(org.gcn.plinguacore
	 * .parser.input.messages.InputParserMsg)
	 */
	@Override
	protected void writeMsg(InputParserMsg msg) {
		// TODO Auto-generated method stub
		super.writeMsg(msg);
	}

}
