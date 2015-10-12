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

import java.util.List;
import java.util.ListIterator;

class PlinguaModule {

	private List<String> parameters;
	private String moduleName;
	private Token moduleBodyToken;
	private PlinguaGlobalEnvironment globalEnvironment;

	public PlinguaModule(String moduleName, Token moduleBodyToken,
			List<String> parameters, PlinguaGlobalEnvironment globalEnvironment) {
		if (globalEnvironment == null)
			throw new NullPointerException(
					"globalEnvironment constructor argument shouldn't be null");
		this.globalEnvironment = globalEnvironment;
		if (moduleName == null)
			throw new NullPointerException(
					"moduleName constructor argument shouldn't be null");
		if (moduleBodyToken == null)
			throw new NullPointerException(
					"moduleTokenBody constructor argument shouldn't be null");
		if (moduleBodyToken == null)
			throw new NullPointerException(
					"moduleTokenBody constructor argument shouldn't be null");
		if (parameters == null)
			throw new NullPointerException(
					"parameters constructor argument shouldn't be null");

		this.moduleName = moduleName;
		this.moduleBodyToken = moduleBodyToken;
		this.parameters = parameters;

	}

	public String getModuleName() {
		return moduleName;
	}

	public List<String> getParameters() {
		return parameters;
	}

	public Token getModuleBodyToken() {
		return moduleBodyToken;
	}

	public PlinguaLocalEnvironment generateLocalEnvironment(Token callToken)
			throws ParseException {
		return generateLocalEnvironment(callToken, null);
	}

	public PlinguaLocalEnvironment generateLocalEnvironment()
			throws ParseException {
		return generateLocalEnvironment(null, null);
	}

	public PlinguaLocalEnvironment generateLocalEnvironment(Token callToken,
			List parameterValues) throws ParseException {

		if (parameterValues != null && parameters != null
				&& parameterValues.size() != parameters.size()
				|| parameterValues == null && parameters != null
				&& !parameters.isEmpty() || parameterValues != null
				&& parameters == null && !parameterValues.isEmpty())

			PlinguaProgram.throwSemanticsException(
					"Invalid number of parameters at "
							+ moduleName.toUpperCase() + " call", callToken,
					callToken);

		PlinguaLocalEnvironment env = new PlinguaLocalEnvironment(
				globalEnvironment);

		if (parameterValues != null && parameters != null) {
			ListIterator itValues = parameterValues.listIterator();
			ListIterator<String> itParams = parameters.listIterator();

			int cont = 1;
			while (itValues.hasNext() && itParams.hasNext()) {
				Object value = itValues.next();
				String param = itParams.next();
				if (value instanceof Number) {
					if (param.charAt(0) == '$')
						PlinguaProgram.throwSemanticsException("Parameter nº "
								+ cont + " must be string at "
								+ moduleName.toUpperCase() + " call",
								callToken, callToken);
					env.disableSafeMode();
					env.setVariable(param, (Number) value);
					env.enableSafeMode();
				} else if (value instanceof String) {
					if (param.charAt(0) != '$')
						PlinguaProgram.throwSemanticsException("Parameter nº "
								+ cont + " must be numeric at "
								+ moduleName.toUpperCase() + " call"
								+ callToken);
					env.disableSafeMode();
					env.setVariable(param, (String) value);
					env.enableSafeMode();
				} else
					throw new ParseException();
				cont++;
			}
		}

		return env;
	}

}
