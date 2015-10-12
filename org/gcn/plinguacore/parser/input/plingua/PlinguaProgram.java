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

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.util.List;


import org.gcn.plinguacore.parser.input.byteCounter.IByteCounter;
import org.gcn.plinguacore.parser.input.messages.InputParserMsg;
import org.gcn.plinguacore.parser.input.messages.InputParserMsgFactory;
import org.gcn.plinguacore.parser.input.messages.MsgInterval;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.Pair;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.cellLike.CellLikePsystem;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoGeneStrings;



abstract class PlinguaProgram {

	
	protected static final CheckRule noGeneStringsCheckRule = new NoGeneStrings(
			);
	
	private Map<String, PlinguaModule> modules;

	private PlinguaGlobalEnvironment globalEnvironment;

	private Stack<PlinguaLocalEnvironment> localEnvironmentStack;

	private boolean error = false;

	private PlinguaInputParser inputParser = null;

	private Psystem psystem;
	
	protected long countRules=0;
	
	private Map<Pair<String,String>,ChangeableMembrane> membranes; /* membranas por etiqueta e identificador del usuario de P-Lingua */

	protected static IByteCounter byteCounter=null;
	protected int ruleCounter=0;
	
	protected PlinguaProgram() {
		modules = new HashMap<String, PlinguaModule>();
		localEnvironmentStack = new Stack<PlinguaLocalEnvironment>();
		globalEnvironment = new PlinguaGlobalEnvironment();
		psystem = null;
		membranes = new HashMap<Pair<String,String>,ChangeableMembrane>();
	}

	protected void reset() {
		psystem = null;
		inputParser = null;
		error = false;
		globalEnvironment.reset();
		localEnvironmentStack.clear();
		modules.clear();
		membranes.clear();
		countRules=0;
		ruleCounter=0;
	}
	
	protected void setPsystem(Psystem psystem) {
		this.psystem=psystem;
	}
	

	protected Psystem getPsystem() {
		return psystem;
	}

	private void setInitialParameters(Map<String, Number> parameters) {
		Iterator<String> it = parameters.keySet().iterator();
		boolean safeMode = getCurrentEnvironment().isSafeMode();
		getCurrentEnvironment().disableSafeMode();
		while (it.hasNext()) {
			String param = it.next();
			Number value = parameters.get(param);
			getCurrentEnvironment().setVariable(param, value);
		}
		if (safeMode)
			getCurrentEnvironment().enableSafeMode();

	}

	protected void setInputParser(PlinguaInputParser inputParser) {

		this.inputParser = inputParser;
		if (inputParser != null)
			setInitialParameters(inputParser.getParameters());
	}

	protected boolean isError() {
		return error;
	}

	protected PlinguaEnvironment getCurrentEnvironment() {
		if (localEnvironmentStack.empty())
			return globalEnvironment;
		else
			return localEnvironmentStack.peek();
	}

	protected void addMultiSet(String label,String id,MultiSet<String> ms,boolean inc,Token beginToken,Token endToken) throws ParseException
	{
		Pair<String,String> key= new Pair<String,String>(label,id);
		if (!membranes.containsKey(key))
			throwSemanticsException("Cannot find membrane identification '"+label+","+id+"'",beginToken,endToken);
	
		ChangeableMembrane m = membranes.get(key);
		if (!inc)
			m.getMultiSet().clear();
		
		m.getMultiSet().addAll(ms);
		
	}
	
	protected void addMembrane(String label,String id,ChangeableMembrane membrane, Token beginToken,Token endToken) throws ParseException
	{
		Pair<String,String> key= new Pair<String,String>(label,id);
		
		if (membranes.containsKey(key))
			throwSemanticsException("Repeated membrane identification '"+label+","+id+"'",beginToken,endToken);
		
		membranes.put(key,membrane);
	}
	
	protected void addModule(Token moduleNameToken, Token moduleBodyToken,
			List<String> parameters) throws ParseException {

		if (modules.containsKey(moduleNameToken.image.toUpperCase()))
			throwSemanticsException("Repeated module '"
					+ moduleNameToken.image.toUpperCase() + "'",
					moduleNameToken, moduleNameToken);
		PlinguaModule module = new PlinguaModule(moduleNameToken.image
				.toUpperCase(), moduleBodyToken, parameters, globalEnvironment);
		modules.put(moduleNameToken.image.toUpperCase(), module);
		if (parameters.size() > 0)
			writeInfo("Reading module '" + moduleNameToken.image.toUpperCase()
					+ "' with parameters " + parameters, 5);
		else
			writeInfo("Reading module '" + moduleNameToken.image.toUpperCase()
					+ "'", 5);
	}

	protected void doCall(String moduleName) throws ParseException {
		doCall(moduleName, null, null);
	}

	protected void doCall(String moduleName, Token callToken)
			throws ParseException {
		doCall(moduleName, callToken, null);
	}

	protected void doCall(String moduleName, Token callToken,
			List parameterValues) throws ParseException {
		PlinguaModule module = modules.get(moduleName.toUpperCase());
		if (module == null) {
			if (callToken != null)
				throwSemanticsException("Module '" + moduleName.toUpperCase()
						+ "' cannot be resolved", callToken, callToken);
			else
				throwSemanticsException("Module '" + moduleName.toUpperCase()
						+ "' cannot be resolved");
		}
		localEnvironmentStack.push(module.generateLocalEnvironment(callToken,
				parameterValues));
		if (parameterValues != null && parameterValues.size() > 0)
			writeInfo("Calling module '" + moduleName.toUpperCase() + "' for "
					+ module.getParameters() + " = " + parameterValues, 5);
		else
			writeInfo("Calling module '" + moduleName.toUpperCase() + "'", 5);
		doSafeCall(module.getModuleBodyToken());
		localEnvironmentStack.pop();

	}

	protected abstract void doSafeCall(Token callToken) throws ParseException;

	protected static Number getNumber(double n) {
		if (Math.floor(n) == n) {
			if (n >= Byte.MIN_VALUE && n <= Byte.MAX_VALUE)
				return new Byte((byte) n);
			if (n >= Short.MIN_VALUE && n <= Short.MAX_VALUE)
				return new Short((short) n);
			if (n >= Integer.MIN_VALUE && n <= Integer.MAX_VALUE)
				return new Integer((int) n);
			if (n >= Long.MIN_VALUE && n <= Long.MAX_VALUE)
				return new Long((long) n);
		} else {
			if (n >= Float.MIN_VALUE && n <= Float.MAX_VALUE)
				return new Float((float) n);
		}
		return new Double(n);
	}

	protected void writeError(TokenMgrError tokenMgrError) {
		this.error = true;
		if (tokenMgrError.getErrorCode() != TokenMgrError.LEXICAL_ERROR)
			writeMsg(InputParserMsgFactory.createInternalErrorMsg(tokenMgrError
					.getMessage()));
		else
			writeMsg(InputParserMsgFactory.createLexicalErrorMessage(
					tokenMgrError.getMessage(), new MsgInterval(tokenMgrError
							.getErrorLine(), tokenMgrError.getErrorColumn(),
							tokenMgrError.getErrorLine(), tokenMgrError
									.getErrorColumn(),byteCounter)));
		 
	}

	private void writeMsg(InputParserMsg msg) {
		if (inputParser != null)
			inputParser.writeMsg(msg);
	}

	protected void writeError(ParseException ex) {

		InputParserMsg error;
		this.error = true;
		if (ex instanceof PlinguaSemanticsException)
			error = ((PlinguaSemanticsException) ex).getError();
		else {
			if (ex.specialConstructor) {
				String expectedString;
				StringBuffer expected = new StringBuffer();
				int maxSize = 0;
				for (int i = 0; i < ex.expectedTokenSequences.length; i++) {
					if (maxSize < ex.expectedTokenSequences[i].length) {
						maxSize = ex.expectedTokenSequences[i].length;
					}

					for (int j = 0; j < ex.expectedTokenSequences[i].length; j++) {
						expected.append(
								ex.tokenImage[ex.expectedTokenSequences[i][j]])
								.append(" ");
					}
					if (ex.expectedTokenSequences[i][ex.expectedTokenSequences[i].length - 1] != 0) {
						expected.append("...");
					}
					if (i + 1 < ex.expectedTokenSequences.length)
						expected.append(ex.eol).append("    ");
				}
				String retval = "Encountered \"";
				Token tok = ex.currentToken.next;
				for (int i = 0; i < maxSize; i++) {
					if (i != 0)
						retval += " ";
					if (tok.kind == 0) {
						retval += ex.tokenImage[0];
						break;
					}
					retval += ex.add_escapes(tok.image);
					tok = tok.next;
				}
				retval += "\"";

				if (ex.expectedTokenSequences.length == 1) {
					expectedString = "Was expecting:" + ex.eol + "    ";
				} else {
					expectedString = "Was expecting one of:" + ex.eol + "    ";
				}
				expectedString += expected.toString();
				error = InputParserMsgFactory.createSyntacticErrorMessage(
						retval, expectedString, new MsgInterval(
								ex.currentToken.next.beginLine,
								ex.currentToken.next.beginColumn,
								ex.currentToken.next.endLine,
								ex.currentToken.next.endColumn,byteCounter));
			} else
				error = InputParserMsgFactory.createSyntacticErrorMessage(ex
						.getMessage());

		}
		writeMsg(error);

	}

	protected void writeInfo(String msg, int verbosityLevel) {
		writeMsg(InputParserMsgFactory.createInfoMessage(msg, verbosityLevel));
	}

	protected static void throwInternalException(String msg)
			throws TokenMgrError {
		throw new TokenMgrError(msg, 10);
	}

	protected static void throwSemanticsException(String msg)
			throws PlinguaSemanticsException {
		InputParserMsg error = InputParserMsgFactory
				.createSemanticsErrorMessage(msg);
		throw new PlinguaSemanticsException(error);
	}

	protected void writeWarning(String msg, Token beginToken, Token endToken)
			throws PlinguaSemanticsException {
		if (beginToken == null || endToken == null)
			throwSemanticsException(msg);
		InputParserMsg error = InputParserMsgFactory.createWarningMessage(msg,
				new MsgInterval(beginToken.beginLine, beginToken.beginColumn,
						endToken.endLine, endToken.endColumn,byteCounter));
		if (inputParser != null)
			inputParser.writeMsg(error);

	}

	protected void writeWarning(String msg, Token beginToken, Token endToken,
			String moreInformation) throws PlinguaSemanticsException {
		if (beginToken == null || endToken == null)
			throwSemanticsException(msg);
		InputParserMsg error = InputParserMsgFactory.createWarningMessage(msg,
				moreInformation, new MsgInterval(beginToken.beginLine,
						beginToken.beginColumn, endToken.endLine,
						endToken.endColumn,byteCounter));
		if (inputParser != null)
			inputParser.writeMsg(error);
	}

	protected static void throwSemanticsException(String msg, Token beginToken,
			Token endToken, String moreInformation)
			throws PlinguaSemanticsException {
		if (beginToken == null || endToken == null)
			throwSemanticsException(msg);
		InputParserMsg error = InputParserMsgFactory
				.createSemanticsErrorMessage(msg, moreInformation,
						new MsgInterval(beginToken.beginLine,
								beginToken.beginColumn, endToken.endLine,
								endToken.endColumn,byteCounter));
		throw new PlinguaSemanticsException(error);
	}

	protected static void throwSemanticsException(String msg, Token beginToken,
			Token endToken) throws PlinguaSemanticsException {
		if (beginToken == null || endToken == null)
			throwSemanticsException(msg);
		InputParserMsg error = InputParserMsgFactory
				.createSemanticsErrorMessage(msg, new MsgInterval(
						beginToken.beginLine, beginToken.beginColumn,
						endToken.endLine, endToken.endColumn,byteCounter));
		throw new PlinguaSemanticsException(error);
	}

}
