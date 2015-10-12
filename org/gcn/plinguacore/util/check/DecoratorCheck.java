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

package org.gcn.plinguacore.util.check;

import java.util.List;


public abstract class DecoratorCheck<T> implements InnerCheck<T>{
	
	private static final long serialVersionUID = 339922718160092202L;


	public boolean initializedCauses() {
		// TODO Auto-generated method stub
		return decorated.initializedCauses();
	}

	protected InnerCheck<T> decorated;

	/**
	 * Creates a new DecoratorCheckRule instance, which checks only the restrictions defined on itself.
	 */
	public DecoratorCheck(){
		this(new BaseCheck<T>());
	}
	
	
	/**
	 * Creates a new DecoratorCheckRule instance, which wraps cr as stated
	 * by Decorator pattern. Thus, it will be capable to test both the instance restrictions and cr restrictions 
	 * 
	 * @param cr
	 *            the CheckRule instance to be wrapped
	 */
	public DecoratorCheck(Check<T> cr) {
		if (cr == null)
			throw new NullPointerException(
					"Check argument shouldn't be null");
		this.decorated = (InnerCheck<T>) cr;
	}

	/**
	 * @see org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule#checkRule(org.gcn.plinguacore.util.psystem.rule.Rule)
	 */
	@Override
	public boolean check(T r) {
		// TODO Auto-generated method stub
		boolean b = checkSpecificPart(r);

		if (!b) {
			if (!initializedCauses()) {
				initCauses();
				getCauses().clear();

			}
			getCauses().add(getSpecificCause());
		}

		return b & decorated.check(r);
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

	protected abstract boolean checkSpecificPart(T r);

	protected abstract String getSpecificCause();


}
