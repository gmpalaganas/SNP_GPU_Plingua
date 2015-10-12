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
import java.util.LinkedList;
import java.util.List;

import org.gcn.plinguacore.util.GeneString;
import org.gcn.plinguacore.util.HashMultiSet;
import org.gcn.plinguacore.util.InmutableMultiSet;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.rule.IStochasticRule;
import org.gcn.plinguacore.util.psystem.rule.LeftHandRule;
import org.gcn.plinguacore.util.psystem.rule.OuterRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.RightHandRule;


/**
 * This class represents cell-like rules for string rewriting. They are used in 
 * stochastic system, since string usually represents DNA sequences or fragments of a 
 * gene.
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 */
class StochasticCellLikeRule extends ConstantCellLikeRule implements IStochasticRule {

	private static final long serialVersionUID = -5477449679934222009L;
	private List<String> leftHandStrings;
	private List<String> rightHandStrings;
	private MultiSet<String> leftHandMultiset;
	private MultiSet<String> rightHandMultiset;
	
	/**
	 * Creates a new StochasticCellLikeRule instance.
	 * 
	 * @param dissolves
	 *            a boolean parameter which reflects if the rule will dissolve
	 *            the membrane
	 * @param leftHandRule
	 *            the left hand of the rule
	 * @param rightHandRule
	 *            the right hand of the rule
	 * @param constant
	 *            the constant which holds part of the rule information
	 */
	protected StochasticCellLikeRule(boolean dissolves, LeftHandRule leftHandRule,
			RightHandRule rightHandRule,float constant) {
		super(dissolves, leftHandRule, rightHandRule, constant);
		leftHandStrings = new LinkedList<String>();
		rightHandStrings = new LinkedList<String>();
		leftHandMultiset = new HashMultiSet<String>();
		rightHandMultiset = new HashMultiSet<String>();
		// Separate gene strings and multisets in the rule
		Iterator<String> it = leftHandRule.getOuterRuleMembrane().getMultiSet().iterator();
		while (it.hasNext()) {
			String s = it.next();
			if (GeneString.isGeneString(s))
				leftHandStrings.add(s);
			else
				leftHandMultiset.add(s);
		}
		
		it = rightHandRule.getOuterRuleMembrane().getMultiSet().iterator();
		while (it.hasNext()) {
			String s = it.next();
			if (GeneString.isGeneString(s))
				rightHandStrings.add(s);
			else
				rightHandMultiset.add(s);
		}
	}

	
	
	
	/* (non-Javadoc)
	 * @see util.psystem.rule.cellLike.CellLikeRule#countExecutions(util.psystem.membrane.ChangeableMembrane)
	 */
	@Override
	public long countExecutions(ChangeableMembrane membrane) {
		CellLikeRule equivalentRule = buildEquivalentRule(membrane);
		if (equivalentRule == null)
			return 0;
		return equivalentRule.countExecutions(membrane);
	}




	/* (non-Javadoc)
	 * @see util.psystem.rule.cellLike.CellLikeRule#execute(util.psystem.membrane.ChangeableMembrane, util.MultiSet, int)
	 */
	@Override
	/**
	 * Executes the rule on a membrane and an outer multiset once
	 *  and returns if the execution could be committed
	 * 
	 * @param membrane
	 *            the membrane which the rule will be applied to
	 * @param environment
	 *            the multiset out of the membrane
	 * @param executions
	 *            the number of times to be executed on the membrane passed
	 *            Rewriting string rules are always executed <strong>once</strong>
	 *            Therefore, this parameter is ignored.
	 * @return true if the execution was committed
	 */
	public boolean execute(ChangeableMembrane membrane, 
			MultiSet<String> environment, long executions) {
			CellLikeRule equivalentRule = buildEquivalentRule(membrane);
			if (equivalentRule == null)
				return false;
		return equivalentRule.execute(membrane, environment, 1);
	}
	
	
	/*
	 * Returns an equivalent CellLikeRule, with only multisets. The strings in the
	 * left hand rule are substituted with the strings in the membrane that match them.
	 * The strings in the right hand rule are substituted with the replaced strings
	 * indicated by this rule.
	 * For example, the StochasticCellLikeRule: 
	 * 
	 * 				a[b,<aa.?.bb>]'c --> b[b*2,<aa.cc.?.bb>]'c
	 * 
	 * to be executed in the membrane 
	 * 
	 * 				a[b,d,<aa.dddddd.bb.ffff>]'c.
	 * 
	 * The equivalent CellLikeRule is:
	 * 
	 * 				a[b,<aa.dddddd.bb.ffff>] --> b[b*2,<aa.cc.dddddd.bb.ffff>]'c
	 * 
	 * that can be executed as a normal CellLikeRule	
	 * 
	 * All the strings in the membrane have to match with one string in the rule. If
	 * this does not happen, the rule cannot be executed in this membrane. Therefore,
	 * null is returned		
	 */
	private CellLikeRule buildEquivalentRule(ChangeableMembrane membrane) {
		CellLikeRule res = null;
		List<String> aux = new LinkedList<String>();
		aux.addAll(leftHandStrings);
		List<String> aux2 = new LinkedList<String>();
		aux2.addAll(rightHandStrings);
		Iterator<String> it = aux.iterator();
		MultiSet<String> leftHandNewStrings = new HashMultiSet<String>();
		MultiSet<String> rightHandNewStrings = new HashMultiSet<String>();
		List<String> membraneContent = new LinkedList<String>(membrane.getMultiSet());
		while (it.hasNext() && !aux.isEmpty()) {
			String s = it.next();
			GeneString gs = new GeneString(s);
			Iterator<String> it2 = membraneContent.iterator();
			boolean found = false;
			while (it2.hasNext() && !found) {
				String s2 = it2.next();
				if (GeneString.isGeneString(s2) && gs.matches(GeneString.getString(s2))) {
					found = true;
					it.remove();
					it2.remove();
					leftHandNewStrings.add(s2);
					// look for a replacement, if there exists one
					boolean foundReplacement = false;
					Iterator<String> it3 = aux2.iterator();
					while (it3.hasNext() && !foundReplacement) {
						String r = it3.next();
						GeneString replacement = new GeneString(r);
						if (gs.getNumberOfGroups() == replacement.getNumberOfGroups()) {
							String replaced = gs.replace(GeneString.getString(r), GeneString.getString(s2));
							foundReplacement = true;
							rightHandNewStrings.add("<" + replaced + ">");
							it3.remove();
						}
					}
				}
			}
		}
		
		if (aux.isEmpty()) {
			leftHandNewStrings.addAll(leftHandMultiset);
			rightHandNewStrings.addAll(rightHandMultiset);
			rightHandNewStrings.addAll(aux2);
			MultiSet<String> m1 = new InmutableMultiSet<String>(leftHandNewStrings);
			MultiSet<String> m2 = new InmutableMultiSet<String>(rightHandNewStrings);
			OuterRuleMembrane orm = this.getLeftHandRule().getOuterRuleMembrane();
			OuterRuleMembrane lhm = new OuterRuleMembrane(orm.getLabelObj(),orm.getCharge(),m1); 
			orm = this.getRightHandRule().getOuterRuleMembrane();
			OuterRuleMembrane rhm = new OuterRuleMembrane(orm.getLabelObj(),orm.getCharge(),m2);
			LeftHandRule lh = new LeftHandRule(lhm,this.getLeftHandRule().getMultiSet());
			RightHandRule rh = new RightHandRule(rhm,this.getRightHandRule().getMultiSet());
			res = new CellLikeRule(this.dissolves(),lh,rh);
		}
		
		return res;
	}
	
	

}
