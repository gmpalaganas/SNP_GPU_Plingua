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

package org.gcn.plinguacore.util.psystem.rule.cellLike;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.gcn.plinguacore.util.HashMultiSet;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembraneFactory;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeNoSkinMembrane;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.rule.InnerRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.LeftHandRule;
import org.gcn.plinguacore.util.psystem.rule.OuterRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.RightHandRule;
import org.gcn.plinguacore.util.psystem.rule.AbstractRule;

/**
 * This class performs the specific functionality of rules in cell-like
 * P-systems
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
class CellLikeRule extends AbstractRule {

	private static final long serialVersionUID = -4691051192943646636L;



	/**
	 * Creates a new CellLikeRule instance.
	 * 
	 * @param dissolves
	 *            a boolean parameter which reflects if the rule will dissolve
	 *            the membrane
	 * @param leftHandRule
	 *            the left hand of the rule
	 * @param rightHandRule
	 *            the right hand of the rule
	 */
	protected CellLikeRule(boolean dissolves, LeftHandRule leftHandRule,
			RightHandRule rightHandRule) {
		super(dissolves, leftHandRule, rightHandRule);
		
	}

	private boolean checkSkinMembrane() {
		/* The skin membrane can't be dissolved */
		if (dissolves())
			return false;
		/* There can be only one skin membrane */
		if (getRightHandRule().getSecondOuterRuleMembrane() != null)
			return false;
		/* The skin membrane can't access the outer objects */
		if (!getLeftHandRule().getMultiSet().isEmpty())
			return false;
		return true;
	}

	@Override
	protected boolean executeSafe(ChangeableMembrane membrane,
			MultiSet<String> environment, long executions) {


		CellLikeMembrane outerClm = (CellLikeMembrane) membrane;
		
		/*
		 * First of all, the multiset in the outer membrane of the left hand
		 * rule is subtracted from the membrane multiset
		 */
		subtractMultiSet(
				getLeftHandRule().getOuterRuleMembrane().getMultiSet(),
				outerClm.getMultiSet(), executions);
		executeRightHand(membrane, environment, executions);
		return true;

	}
	/**
	 * @see org.gcn.plinguacore.util.psystem.rule.AbstractRule#countExecutions(org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane)
	 */
	@Override
	public long countExecutions(ChangeableMembrane membrane) {
		CellLikeMembrane cellLikeMembrane = (CellLikeMembrane) membrane;
		long outerMultiSetCount = Long.MAX_VALUE;
		long innerMultiSetCount = Long.MAX_VALUE;
		long innerMembranesCount = Long.MAX_VALUE;
		/*
		 * In case is intended to be executed on a skin membrane, it would be no
		 * possible execution if it doesn't match skin membrane requirements
		 */
		if (cellLikeMembrane.isSkinMembrane()) {
			if (!checkSkinMembrane())
				return 0;
		} else {
			/*
			 * Otherwise, the number of executions is the minimum of three
			 * candidates:
			 */
			CellLikeNoSkinMembrane cellLikeNoSkinMembrane = (CellLikeNoSkinMembrane) cellLikeMembrane;
			/*
			 * a. The number of matching objects in the parent membrane multiset
			 * and the outer multiset on the left hand rule. As skin membranes
			 * have not a parent, it would only be possible in case the membrane
			 * isn't a skin
			 */
			
			try{
				outerMultiSetCount = multiSetCount(getLeftHandRule().getMultiSet(),
						cellLikeNoSkinMembrane.getParentMembrane().getMultiSet());
			}catch(RuntimeException ex)
			{
				System.out.println(this+" ### "+cellLikeNoSkinMembrane.getParentMembrane());
				throw ex;
			}
		}
		/*
		 * b. The number of matching objects in the membrane multiset and the
		 * outer rule membrane multiset on the left hand of the rule
		 */
		innerMultiSetCount = multiSetCount(getLeftHandRule()
				.getOuterRuleMembrane().getMultiSet(), cellLikeMembrane
				.getMultiSet());
	
		/*
		 * c. The number of matching membranes among the membrane children and
		 * the inner rule membranes
		 */
		if (!getLeftHandRule().getOuterRuleMembrane().getInnerRuleMembranes()
				.isEmpty())
			innerMembranesCount = countInnerMembranes(cellLikeMembrane);
		/* Once we have all numbers to consider, we get the minimum */
			
		long count= Math.min(innerMembranesCount, Math.min(innerMultiSetCount,
				outerMultiSetCount));
		//System.out.println(count+" ("+innerMembranesCount+","+innerMultiSetCount+","+outerMultiSetCount+"): "+this+" "+membrane);
		return count;
	}
	
	

	private long countInnerMembranes(CellLikeMembrane cellLikeMembrane) {
		/*
		 * For a child membrane to match an inner membrane, it should comply
		 * with several conditions: a. The label and charge of both membranes
		 * match b. The child membrane should contain all objects in the inner
		 * membrane
		 */
	
		List<InnerRuleMembrane> lirm = getLeftHandRule().getOuterRuleMembrane()
				.getInnerRuleMembranes();
		long memCount[] = new long[lirm.size()];
		/*
		 * Each value in an array to count the number of matching objects is set
		 * to 0
		 */
		for (int i = 0; i < memCount.length; i++)
			memCount[i] = 0;
		/*
		 * Iterator<CellLikeNoSkinMembrane> membraneIterator = cellLikeMembrane
		 * .getChildMembranes().iterator();
		 */
		/* Finally, we go through every single inner rule membrane */
		Iterator<InnerRuleMembrane> irmIterator = lirm.iterator();
		/* We keep an index for the formerly declared array */
		int counter=0;
		while (irmIterator.hasNext()) {
			// CellLikeMembrane clm = membraneIterator.next();
			InnerRuleMembrane irm = irmIterator.next();
			// ListIterator<InnerRuleMembrane> irmIterator =
			// lirm.listIterator();
			Iterator<CellLikeNoSkinMembrane> membraneIterator = cellLikeMembrane
					.getChildMembranes().iterator();
			
			/*
			 * For each inner rule membrane, we go through every child membrane
			 * of the cell-like membrane passed
			 */
			boolean enc=false;
			while (membraneIterator.hasNext() && !enc) {
				/*
				 * In case the label and charge match on both membranes, we get
				 * cardinality of the intersection of both multiset
				 */
				CellLikeNoSkinMembrane clm = membraneIterator.next();
				
				if (clm.getLabel().equals(irm.getLabel()) &&
						clm.getCharge()==irm.getCharge())
				{
					memCount[counter]=clm.getMultiSet().countSubSets(irm.getMultiSet());
					enc=true;
				}		
			}
			
			if (!enc) return 0;
			
			counter++;
		}
		/* Finally, we get the minimum value in the array */
		
		return getMinimum(memCount);
	}

	private long getMinimum(long[] memCount) {
		/* It gets the minimum out of an integer array */
		long min = memCount[0];
		int tam = memCount.length;
		for (int i = 1; i < tam; i++) {
			if (memCount[i] < min)
				min = memCount[i];
		}
		return min;
	}
	


	private CellLikeMembrane[] createMembraneCorrespondence(
			CellLikeMembrane outerClm) {
		List<InnerRuleMembrane> lirm = getLeftHandRule().getOuterRuleMembrane()
				.getInnerRuleMembranes();
		
		int numInner = getLeftHandRule().getOuterRuleMembrane()
				.getInnerRuleMembranes().size();
		
		/*
		 * We create the array which will hold the correspondence between inner
		 * rule membranes and child membranes
		 */
		CellLikeMembrane correspondence[] = new CellLikeMembrane[numInner];
	
		int counter = 0;
		
		ListIterator<InnerRuleMembrane> irmIterator = lirm.listIterator();

		/*
		 * For each inner membrane, we go through every child membrane,
		 * until we find any whose label and charges matches the inner membrane's one
		 */
		while (irmIterator.hasNext())
		{
			InnerRuleMembrane irm = irmIterator.next();
			Iterator<CellLikeNoSkinMembrane> membraneIterator = outerClm
			.getChildMembranes().iterator();
			boolean find=false;
			/* We go through every child membrane */
			while (membraneIterator.hasNext() && !find)
			{
				CellLikeMembrane clm = membraneIterator.next();
				/*
				 * In case both label and charges matches, we assume they correspond to each
				 * other
				 */
				if (irm.getLabel().equals(clm.getLabel()) && irm.getCharge()==clm.getCharge()) {
					correspondence[counter] = clm;
					find=true;
				}
			}
			counter++;
		}
		
	
	
		
		return correspondence;
	}

	


	private void updateOuterMultiSet(CellLikeNoSkinMembrane clnsm,
			long executions) {
		MultiSet<String> parentMultiSet = clnsm.getParentMembrane()
				.getMultiSet();
		/*
		 * All objects in the left hand rule outer membrane are subtracted after
		 * the execution of the rule
		 */
		subtractMultiSet(getLeftHandRule().getMultiSet(), parentMultiSet,
				executions);
		/*
		 * All objects in the right hand rule outer membrane are added after the
		 * execution of the rule
		 */
		addMultiSet(getRightHandRule().getMultiSet(), parentMultiSet,
				executions);
	}

	private void updateInnerMembrane(CellLikeMembrane correspondency[],
			InnerRuleMembrane irmMembrane, int index, long executions) {
		/*
		 * All objects in each inner membrane on the left hand are subtracted
		 * from its corresponding child membrane after the execution of the rule
		 */
		//System.out.println(irmMembrane);
		//System.out.println(correspondency[index]);
		
		subtractMultiSet(irmMembrane.getMultiSet(), correspondency[index]
				.getMultiSet(), executions);
		/*
		 * All objects in each inner membrane on the right hand are added from
		 * its corresponding child membrane after the execution of the rule
		 */
		addMultiSet(handCorrespondence[index].getMultiSet(),
				correspondency[index].getMultiSet(), executions);
		correspondency[index].setCharge(handCorrespondence[index].getCharge());

	}

	private void updateAllInnerMembranes(CellLikeMembrane correspondency[],
			List<InnerRuleMembrane> lirm, long executions) {
		ListIterator<InnerRuleMembrane> irmIter = lirm.listIterator();
		/* In order to execute a rule, all child membranes should be updated */
		int numInner = handCorrespondence.length;
		for (int i = 0; i < numInner; i++) {
			if (handCorrespondence[i] == null)
				continue;
			updateInnerMembrane(correspondency, irmIter.next(), i, executions);
		}
	}

	private void addNewMembranes(CellLikeMembrane outerClm, long executions) {
		/* All new membranes should be added to the outer cell like membrane */
		ListIterator<InnerRuleMembrane> nmIter = newMembranes.listIterator();
		while (nmIter.hasNext()) {
			InnerRuleMembrane newIrm = nmIter.next();
			for (int i = 0; i < executions; i++) {
				/*
				 * Each new membrane should contain the label, charge and inner
				 * multiset stated in the rule
				 */
				CellLikeMembrane newClm = CellLikeMembraneFactory
						.getCellLikeMembrane(newIrm.getLabelObj(), outerClm);
				addMultiSet(newIrm.getMultiSet(), newClm.getMultiSet(), 1);
				newClm.setCharge(newIrm.getCharge());
			}
		}
	}





	/**
	 * Applies the right hand of the rule to the membrane passed as argument, by
	 * updating all objects which the right hand consist of, updating the
	 * membrane children and, if specified, dissolving or dividing the rule
	 * 
	 * @param membrane
	 *            the membrane which the right hand rule will be applied to
	 * @param environment
	 *            the environment which should be expanded by adding the right
	 *            hand objects in case the rule is applied to the skin membrane
	 * @param executions
	 *            the number of times the right hand rule is applied to the
	 *            membrane
	 */

	
	protected void executeRightHand(ChangeableMembrane membrane, MultiSet<String> environment, long executions
			) {
		CellLikeMembrane outerClm = (CellLikeMembrane) membrane;
		CellLikeMembrane correspondency[] = createMembraneCorrespondence(outerClm);
			
		List<InnerRuleMembrane> lirm = getLeftHandRule().getOuterRuleMembrane()
		.getInnerRuleMembranes();
		CellLikeMembrane secondOclm;
		/*
		 * In case the membrane is not a skin membrane, we should update the
		 * outer multiset
		 */
		if (!outerClm.isSkinMembrane()) {
			updateOuterMultiSet((CellLikeNoSkinMembrane) outerClm, executions);
		} else {
			/*
			 * Else, we add the all objects in the right hand rule to the
			 * environment
			 */

			addMultiSet(getRightHandRule().getMultiSet(), environment,
					executions);

		}
		/*
		 * In case the rule performs membrane division, the membrane obtained
		 * out of the division should be created
		 */
		if (getRightHandRule().getSecondOuterRuleMembrane() != null) {
			secondOclm = (CellLikeMembrane) outerClm.divide();
			updateMembrane(secondOclm, getRightHandRule()
					.getSecondOuterRuleMembrane(), executions);
		}
		/* Later, all children membranes should be updated */
		updateAllInnerMembranes(correspondency, lirm, executions);
		/*
		 * All inner membranes created as a result of the rule execution are
		 * created, as well
		 */
		addNewMembranes(outerClm, executions);
		/*
		 * Finally, the outer membrane is updated according to the outer rule
		 * membrane
		 */
		updateMembrane(outerClm, getRightHandRule().getOuterRuleMembrane(),
				executions);
		/* If the rule dissolves the membrane, it should be performed */
		if (dissolves())
			outerClm.dissolve();
	}

	
	
	
	private boolean compressibleForm() {
		/*
		 * If the rule performs membrane division, the rule string can't be
		 * compressed
		 */
		if (getRightHandRule().getSecondOuterRuleMembrane() != null)
			return false;
		/* If the rule dissolves the rule, the rule string can't be compressed */
		if (dissolves())
			return false;
		/*
		 * If any outer multiset (whether on the left hand or on the right hand)
		 * is not empty, the rule string can't be compressed
		 */
		if (!getLeftHandRule().getMultiSet().isEmpty()
				|| !getRightHandRule().getMultiSet().isEmpty())
			return false;
		/*
		 * If the charge in the outer rule membrane of each hand is different,
		 * the rule string can't be compressed
		 */
		if (getLeftHandRule().getOuterRuleMembrane().getCharge() != getRightHandRule()
				.getOuterRuleMembrane().getCharge())
			return false;
		return true;
	}

	private String compressedForm() {
		String lhrString = getLeftHandRule().toString();
		/*String rhrString = getRightHandRule().getOuterRuleMembrane()
				.getMultiSet().toString();*/
		String rhrString = getRightHandRule().toString();
		String str= lhrString.substring(0, lhrString.lastIndexOf("]")) + " --> "
				+ rhrString.substring(rhrString.indexOf("[")+1)+"'"
				+ getRightHandRule().getOuterRuleMembrane().getLabelObj();
		
	
		
		return str;
	}

	/*
	 * IPHM adaptamos las funciones toString al formato P-Lingua, si existen
	 * innerMembranes, no se puede usar la forma de escritura habitual de
	 * membranas activas
	 */
	private String dissolvedForm() {

		MultiSet<String> ms = new HashMultiSet<String>(getRightHandRule()
				.getOuterRuleMembrane().getMultiSet());

		String ruleString;
		/*
		 * If there's no inner rule membranes on the left hand, all objects
		 * (both in the outer rule membrane and out of them) on the right hand
		 * are out of the membrane
		 */
		if (getRightHandRule().getOuterRuleMembrane().getInnerRuleMembranes()
				.isEmpty()) {
			ms.addAll(getRightHandRule().getMultiSet());
			ruleString = getLeftHandRule().toString() + " --> " + ms.toString();
		} else {
			/*
			 * Otherwise, we need to specify the rule dissolves the membrane by
			 * adding the "@d" symbol to its multiset
			 */
			ms.add("@d");
			OuterRuleMembrane o = new OuterRuleMembrane(getRightHandRule()
					.getOuterRuleMembrane().getLabelObj(), getRightHandRule()
					.getOuterRuleMembrane().getCharge(), ms, getRightHandRule()
					.getOuterRuleMembrane().getInnerRuleMembranes());
			RightHandRule rhr = new RightHandRule(o, getRightHandRule()
					.getMultiSet());
			AbstractRule r = new CellLikeRule(false, getLeftHandRule(), rhr);
			ruleString = r.toString();
		}
		return ruleString;
	}

	/**
	 * @see org.gcn.plinguacore.util.psystem.rule.AbstractRule#toString()
	 */
	@Override
	public String toString() {
		/*
		 * If the rule can be compressed, we express it by using its compressed
		 * form
		 */
		if (compressibleForm())
			return "#"+getRuleId()+" "+compressedForm();
		if (dissolves())
			/*
			 * If the rule dissolves the membrane, we express it by using its
			 * dissolved form
			 */
			return "#"+getRuleId()+" "+dissolvedForm();
		return super.toString();
	}



	@Override
	protected void checkState() {
		// TODO Auto-generated method stub
		super.checkState();
		/* Rules outer membranes (on both hands) should be equally labeled */
		if (!getLeftHandRule().getOuterRuleMembrane().getLabel().equals(
				getRightHandRule().getOuterRuleMembrane().getLabel()))
			throw new IllegalArgumentException(
					"Membrane labels on both sides should match");
	}
	
	

}