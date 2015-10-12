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


package org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule;
import org.gcn.plinguacore.util.psystem.Psystem;
import java.util.Iterator;

import org.gcn.plinguacore.util.GeneString;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.rule.HandRule;
import org.gcn.plinguacore.util.psystem.rule.InnerRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.DecoratorCheckRule;


/**
 * This class tests if the rule doens't hold any gene string.
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */

public class NoGeneStrings extends DecoratorCheckRule {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4702673900938431959L;

	/**
	 * Creates a new NoGeneStrings instance, which checks only the restrictions defined on itself.
	 */
	public NoGeneStrings(){
		super();
	}
	
	/**
	 * Creates a new NoGeneStrings instance, which wraps cr as stated
	 * by Decorator pattern. Thus, it will be capable to test both the instance restrictions and cr restrictions 
	 * 
	 * @param cr
	 *            the CheckRule instance to be wrapped
	 */
	public NoGeneStrings(CheckRule cr) {
		super(cr);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean checkSpecificPart(IRule r) {
		/*The left hand rule nor the right hand rule should contain gene strings*/
		if (hasGeneStrings(r.getLeftHandRule()) || hasGeneStrings(r.getRightHandRule()))
			return false;
		/*In case the rule performs membrane division, its second membrane should no contain gene strings, as well*/
		if (r.getRightHandRule().getSecondOuterRuleMembrane()!=null && hasGeneStrings(r.getRightHandRule().getSecondOuterRuleMembrane().getMultiSet()))
			return false;
		return true;
		
			
	}
	
	private static boolean hasGeneStrings(HandRule hr)
	{
		if (hasGeneStrings(hr.getMultiSet()) || 
				hasGeneStrings(hr.getOuterRuleMembrane().getMultiSet()))
			return true;
		Iterator<InnerRuleMembrane> it = hr.getOuterRuleMembrane().getInnerRuleMembranes().listIterator();
		while(it.hasNext())
		{
			if (hasGeneStrings(it.next().getMultiSet()))
				return true;
		}
		return false;
	}
	
	private static boolean hasGeneStrings(MultiSet<String> ms)
	{
		Iterator<String>it = ms.entrySet().iterator();
		while(it.hasNext())
		{
			if (GeneString.isGeneString(it.next()))
				return true;
		}
		return false;
	}

	@Override
	protected String getSpecificCause() {
		// TODO Auto-generated method stub
		return "Rules with gene strings are not allowed";
	}

}
