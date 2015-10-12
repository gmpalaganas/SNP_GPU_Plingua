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

package org.gcn.plinguacore.simulator.tissueLike;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.gcn.plinguacore.simulator.AbstractSelectionExecutionSimulator;

import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.Pair;

import org.gcn.plinguacore.util.psystem.Configuration;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeNoSkinMembrane;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.membrane.MembraneStructure;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoDivision;
import org.gcn.plinguacore.util.psystem.rule.tissueLike.Communication;
import org.gcn.plinguacore.util.psystem.rule.tissueLike.DoubleCommunicationTissueLikeRule;

import org.gcn.plinguacore.util.psystem.tissueLike.membrane.TissueLikeMembrane;
import org.gcn.plinguacore.util.psystem.tissueLike.membrane.TissueLikeMembraneStructure;



import java.util.List;
import java.util.ArrayList;

public class TSCSSimulator extends AbstractSelectionExecutionSimulator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1556761252509150318L;
	private static final int STATE_SELECT_DIVISION=10;
	private static final int STATE_SELECT_COMMUNICATION=20;
	private int state;
	private static final CheckRule noDivision= new NoDivision();
	private List<Communication>communications;
	private Set<Integer>membranesToCommunicate;
	private Map<Integer,Pair<IRule,ChangeableMembrane>>membranesToDivide;
	
	

	public TSCSSimulator(Psystem psystem) {
		super(psystem);
		communications = new ArrayList<Communication>();
		membranesToCommunicate = new HashSet<Integer>();
		membranesToDivide=new HashMap<Integer,Pair<IRule,ChangeableMembrane>>();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getHead(ChangeableMembrane m) {
		// TODO Auto-generated method stub
		String str;
		str="CELL ID: "+m.getId();
		str += ", Label: " + m.getLabelObj();
		return str;
	}
	
	

	@Override
	protected void microStepInit() {
		// TODO Auto-generated method stub
		super.microStepInit();
		communications.clear();
		membranesToCommunicate.clear();
		membranesToDivide.clear();
		state=STATE_SELECT_COMMUNICATION;
		
	}

	@Override
	protected void microStepSelectRules(Configuration cnf, Configuration tmpCnf) {
		// TODO Auto-generated method stub
		for (int i=0;i<2;i++)
		{
			if (i==1)
			{
				state = STATE_SELECT_DIVISION;
				
			}
			Iterator<? extends Membrane> it = tmpCnf.getMembraneStructure().getAllMembranes().iterator();
			Iterator<? extends Membrane> it1 = cnf.getMembraneStructure().getAllMembranes().iterator();
			while(it.hasNext())
			{
				ChangeableMembrane tempMembrane = (ChangeableMembrane) it.next();
				ChangeableMembrane m = (ChangeableMembrane)it1.next();
				microStepSelectRules(m,tempMembrane);
			}
		}
	}

	@Override
	protected void microStepSelectRules(ChangeableMembrane m,
			ChangeableMembrane temp) {
		Iterator<IRule> it = getPsystem().getRules().iterator(temp.getLabel(),
				temp.getCharge(),true);

		while (it.hasNext()) {
			IRule r = it.next();
			
			
			if (state==STATE_SELECT_COMMUNICATION && (r instanceof DoubleCommunicationTissueLikeRule) && noDivision.checkRule(r))
			{
				DoubleCommunicationTissueLikeRule r1 = (DoubleCommunicationTissueLikeRule)r;
				
				long count = r.countExecutions(temp);
				if (count>0)
				{
					TissueLikeMembraneStructure structure = ((TissueLikeMembrane)m).getStructure();
					membranesToCommunicate.add(m.getId());
					Iterator<Communication>it1 = r1.getCommunications().iterator();
					while(it1.hasNext())
					{
						Communication com = it1.next();
						membranesToCommunicate.add(com.getSecondMembrane().getId());
						Communication c = new Communication(structure.getCell(com.getFirstMembrane().getId()),structure.getCell(com.getSecondMembrane().getId()),com.getRule(),com.getExecutions());
						communications.add(c);
					}
					selectRule(r, m, count);
					removeLeftHandRuleObjects(((TissueLikeMembrane)temp).getStructure(), r1.getCommunications());
				}
			}
			else
			if (state==STATE_SELECT_DIVISION && !noDivision.checkRule(r) && !membranesToCommunicate.contains(m.getId()) && !membranesToDivide.containsKey(m.getId()))
			{
				long count = r.countExecutions(temp);
				if (count>0)
				{
					count=1;
					selectRule(r, m, count);
					membranesToDivide.put(m.getId(),new Pair<IRule,ChangeableMembrane>(r,m));
					removeLeftHandRuleObjects(temp, r, count);
					
				}
			}
		}
		
	}
	
	

	@Override
	protected void microStepExecuteRules() {
		// TODO Auto-generated method stub
		Iterator<Communication>it = communications.iterator();
		while(it.hasNext())
		{
			Communication com = it.next();
			MultiSet<String>ms1 = com.getRule().getLeftHandRule().getOuterRuleMembrane().getMultiSet();
			MultiSet<String>ms2 = com.getRule().getRightHandRule().getOuterRuleMembrane().getMultiSet();
			
			if (!ms1.isEmpty())
				com.getFirstMembrane().getMultiSet().subtraction(ms1, com.getExecutions());
			if (!ms2.isEmpty())
			{
				com.getFirstMembrane().getMultiSet().addAll(ms2, com.getExecutions());
				com.getSecondMembrane().getMultiSet().subtraction(ms2, com.getExecutions());
			}
			if (!ms1.isEmpty())
				com.getSecondMembrane().getMultiSet().addAll(ms1, com.getExecutions());
			
		}
		Iterator<Pair<IRule,ChangeableMembrane>>it1 = membranesToDivide.values().iterator();
		while(it1.hasNext())
		{
			Pair<IRule,ChangeableMembrane>p = it1.next();
			p.getFirst().execute(p.getSecond(), null);
		}
	}

	@Override
	protected void printInfoMembraneShort(MembraneStructure membraneStructure) {
		// TODO Auto-generated method stub
		Iterator<? extends Membrane>it = membraneStructure.getAllMembranes().iterator();
		while(it.hasNext())
			printInfoMembrane((ChangeableMembrane)it.next());
	}

	@Override
	protected void printInfoMembrane(ChangeableMembrane membrane) {
		// TODO Auto-generated method stub
		TissueLikeMembrane tlm = (TissueLikeMembrane)membrane;
		if (!tlm.getLabel().equals(tlm.getStructure().getEnvironmentLabel()))
		{
			getInfoChannel().println("    " + getHead(membrane));
			getInfoChannel().println("    Multiset: " + membrane.getMultiSet());
			getInfoChannel().println();
		}
	}
	
	private void removeLeftHandRuleObjects(TissueLikeMembraneStructure structure,List<Communication>coms)
	{
		Iterator<Communication>it=coms.iterator();
		while(it.hasNext())
		{
			Communication com =it.next();
			DoubleCommunicationTissueLikeRule rule = com.getRule();
			MultiSet<String>ms1 = rule.getLeftHandRule().getOuterRuleMembrane().getMultiSet();
			MultiSet<String>ms2 = rule.getRightHandRule().getOuterRuleMembrane().getMultiSet();
			TissueLikeMembrane m1 = structure.getCell(com.getFirstMembrane().getId());
			TissueLikeMembrane m2 = structure.getCell(com.getSecondMembrane().getId());
			if (!ms1.isEmpty())
				m1.getMultiSet().subtraction(ms1,com.getExecutions());
			if (!ms2.isEmpty())
				m2.getMultiSet().subtraction(ms2,com.getExecutions());
		}
	}

	@Override
	protected void removeLeftHandRuleObjects(ChangeableMembrane membrane,
			IRule r, long count) {
		// TODO Auto-generated method stub
		MultiSet<String>ms = r.getLeftHandRule().getOuterRuleMembrane().getMultiSet();
		if (!ms.isEmpty())
			membrane.getMultiSet().subtraction(ms, count);
	}

	

	

}
