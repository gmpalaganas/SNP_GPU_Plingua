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

package org.gcn.plinguacore.util.psystem.rule.tissueLike;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.rule.IDoubleCommunicationRule;
import org.gcn.plinguacore.util.psystem.rule.LeftHandRule;
import org.gcn.plinguacore.util.psystem.rule.RightHandRule;
import org.gcn.plinguacore.util.psystem.tissueLike.membrane.TissueLikeMembrane;
import org.gcn.plinguacore.util.psystem.tissueLike.membrane.TissueLikeMembraneStructure;

public class DoubleCommunicationTissueLikeRule extends TissueLikeRule implements IDoubleCommunicationRule{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5601163359552027963L;
	
	private List<Communication>communications;
	private long executions;
	
	protected DoubleCommunicationTissueLikeRule(boolean dissolves,
			LeftHandRule leftHandRule, RightHandRule rightHandRule) {
		super(dissolves, leftHandRule, rightHandRule);
		communications = new ArrayList<Communication>();
		executions=0;
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString()
	{
		return "#"+getRuleId()+" "+getLeftHandRule().toString()+" <--> "+getRightHandRule().toString(false);
	}
	@Override
	protected void checkState() {
		// TODO Auto-generated method stub
		super.checkState();
		if (getLeftHandRule().getOuterRuleMembrane().getLabel().equals(getRightHandRule().getOuterRuleMembrane().getLabel()))
			throw new IllegalArgumentException("Membrane labels must be different");
	
		if(getRightHandRule().getSecondOuterRuleMembrane()!=null)
			throw new IllegalArgumentException("Rules with division and communication are not allowed");
	}
	
	
	
	public List<Communication> getCommunications() {
		return communications;
	}
	
	private void selectCommunications(TissueLikeMembrane firstMembrane,long max)
	{
		communications.clear();
		TissueLikeMembraneStructure structure = firstMembrane.getStructure();
		String label = getRightHandRule().getOuterRuleMembrane().getLabel();
		MultiSet<String>ms = getRightHandRule().getOuterRuleMembrane().getMultiSet();
		long count=0;
		Iterator<TissueLikeMembrane>it=structure.iterator(label, true);
		
		while(it.hasNext() && count<=max)
		{
			TissueLikeMembrane secondMembrane = it.next();
			long n;
			if (ms.isEmpty())
				n=max;
			else
				n = multiSetCount(ms,secondMembrane.getMultiSet());
			if (n>0)
			{
				if (count+n>max)
					n=max-count;
				count+=n;
				communications.add(new Communication(firstMembrane,secondMembrane,this,n));
			}
		}
		executions=count;
		
		
	}
	
	private void selectCommunications(TissueLikeMembrane firstMembrane)
	{
		selectCommunications(firstMembrane,multiSetCount(getLeftHandRule().getOuterRuleMembrane().getMultiSet(), firstMembrane.getMultiSet()));
		
	}
		
	@Override
	public long countExecutions(ChangeableMembrane membrane) {
		// TODO Auto-generated method stub
		if (!checkLabel(membrane))
			return 0;
		if (!(membrane instanceof TissueLikeMembrane))
			throw new IllegalArgumentException("Invalid membrane type");
		TissueLikeMembrane tlm = (TissueLikeMembrane)membrane;
		selectCommunications(tlm);
		return executions;
	}
	
	
	

	
	@Override
	protected boolean executeSafe(ChangeableMembrane membrane,
			MultiSet<String> environment, long executions) {
		// TODO Auto-generated method stub
		if (!(membrane instanceof TissueLikeMembrane))
			throw new IllegalArgumentException("Invalid membrane type");
		TissueLikeMembrane tlm = (TissueLikeMembrane)membrane;
		
		MultiSet<String>ms1 = getLeftHandRule().getOuterRuleMembrane().getMultiSet();
		MultiSet<String>ms2 = getRightHandRule().getOuterRuleMembrane().getMultiSet();
		
		selectCommunications(tlm,executions);
		
		tlm.getMultiSet().subtraction(ms1, executions);
		tlm.getMultiSet().addAll(ms2,executions);
						
		Iterator <Communication>it = communications.iterator();
	
		while(it.hasNext())
		{
			Communication com = it.next();
			TissueLikeMembrane m = com.getSecondMembrane();
			long mul = com.getExecutions();
			m.getMultiSet().subtraction(ms2, mul);
			m.getMultiSet().addAll(ms1,mul);
		}
		
		return true;
		
	
	}
	
	

	

}
