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

package org.gcn.plinguacore.simulator.cellLike.probabilistic;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcn.plinguacore.simulator.CreateSimulator;

import org.gcn.plinguacore.util.HashMultiSet;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.Pair;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.ActivationSets;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeNoSkinMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.CellLikePsystem;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.rule.InnerRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.IConstantRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.*;
import org.gcn.plinguacore.util.psystem.checkPsystem.BaseCheckPsystem;
import org.gcn.plinguacore.util.psystem.checkPsystem.specificCheckPsystem.CompleteProbabilities;
import org.gcn.plinguacore.util.psystem.checkPsystem.specificCheckPsystem.ConsistentRules;
import org.gcn.plinguacore.util.psystem.factory.cellLike.AbstractCellLikePsystemFactory;

/**
 * This class is intended to give support for Probabilistic P systems
 * 
 * @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */

public class ProbabilisticPsystemFactory extends AbstractCellLikePsystemFactory {

	private static ProbabilisticPsystemFactory singleton = null;
	private static boolean checkCompleteProbabilities = true;
	private static boolean checkConsistentRules = true;

	public static boolean isCheckCompleteProbabilities() {
		return checkCompleteProbabilities;
	}

	public static void setCheckCompleteProbabilities(
			boolean checkCompleteProbabilities) {
		ProbabilisticPsystemFactory.checkCompleteProbabilities = checkCompleteProbabilities;
	}

	public static boolean isCheckConsistentRules() {
		return checkConsistentRules;
	}

	public static void setCheckConsistentRules(boolean checkConsistentRules) {
		ProbabilisticPsystemFactory.checkConsistentRules = checkConsistentRules;
	}

	private ProbabilisticPsystemFactory() {
		super();
		checkRule = new 
				Constant(new NoDivision(new NoCreation(new NoPriority(new NoDissolution(
						)))));
		checkRule = new MultiEnvironment(new NoEmptyLeftMultiSet(new NoLeftRepeatedLabels(
				new NoRightRepeatedLabels(new NoGeneStrings(
						new Ratio(checkRule))))));

		checkPsystem = new CompleteProbabilities(new ConsistentRules(
				new BaseCheckPsystem()));
	}

	/**
	 * Gets the only instance of ProbabilisticPsystemFactory, according to
	 * Singleton pattern
	 * 
	 * @return the only instance of ProbabilisticPsystemFactory, according to
	 *         Singleton pattern
	 */
	public static ProbabilisticPsystemFactory getInstance() {
		if (singleton == null)
			singleton = new ProbabilisticPsystemFactory();
		return singleton;
	}

	/**
	 * @see org.gcn.plinguacore.simulator.cellLike.stochastic.StochasticPsystemFactory#getCreateSimulator()
	 */
	@Override
	public CreateSimulator getCreateSimulator() throws PlinguaCoreException {
		return new ProbabilisticCreateSimulator(getModelName());
	}

	public static Map<ActivationSets, List<IRule>> getRulesByActivationSet(
			CellLikePsystem ps) {
		Map<ActivationSets, List<IRule>> map = new HashMap<ActivationSets, List<IRule>>();

		Iterator<? extends Membrane> it = ps.getMembraneStructure()
				.getAllMembranes().iterator();
		while (it.hasNext()) {
			CellLikeMembrane m = (CellLikeMembrane) it.next();
			for (int charge = -1; charge <= 1; charge++) {
				Iterator<IRule> it1 = ps.getRules().iterator(m.getLabel(),
						m.getLabelObj().getEnvironmentID(), charge, true);

				while (it1.hasNext()) {
					IRule r = it1.next();
					if (r instanceof IConstantRule) {
						IConstantRule r1 = (IConstantRule) r;
						if (r1.getConstant() > 0 && r1.getConstant() < 1) {
							ActivationSets key = getActivationSets(r, m);
							List<IRule> l = map.get(key);
							if (l == null) {
								l = new ArrayList<IRule>();
							}
							l.add(r);
							map.put(key, l);

						}
					}

				}
			}
		}
		/*
		 * try{ Iterator<ActivationSets>it1 = map.keySet().iterator(); File f =
		 * new File("/home/ignacio/grupos.txt"); OutputStream o = new
		 * FileOutputStream(f); PrintStream pr = new PrintStream(o);
		 * while(it1.hasNext()) { ActivationSets a = it1.next();
		 * 
		 * pr.println("PARTE IZQUIERDA: "+a); List<Rule>l = map.get(a); for(int
		 * i=0;i<l.size();i++) pr.println(l.get(i)); } o.close();
		 * }catch(IOException ex){}
		 */
		return map;
	}

	public static ActivationSets getActivationSets(IRule r, CellLikeMembrane m) {

		ActivationSets activation = new ActivationSets();

		if (!r.getLeftHandRule().getMultiSet().isEmpty()) {
			int id = ((CellLikeNoSkinMembrane) m).getParentMembrane().getId();
			//Set<String> set = r.getLeftHandRule().getMultiSet().entrySet();
			Set<Pair<String,Long>> set = processMultiSet(r.getLeftHandRule().getMultiSet());
			activation.add(id, 0, set);
		}

		if (!r.getLeftHandRule().getOuterRuleMembrane().getMultiSet().isEmpty()) {
			int id = m.getId();
			//Set<String> set = r.getLeftHandRule().getOuterRuleMembrane()
			//		.getMultiSet().entrySet();
			Set<Pair<String,Long>> set = processMultiSet(r.getLeftHandRule().getOuterRuleMembrane().getMultiSet());
			activation.add(id, r.getLeftHandRule().getOuterRuleMembrane()
					.getCharge(), set);
		}

		Iterator<InnerRuleMembrane> it = r.getLeftHandRule()
				.getOuterRuleMembrane().getInnerRuleMembranes().iterator();

		while (it.hasNext()) {
			InnerRuleMembrane irm = it.next();
			if (!irm.getMultiSet().isEmpty()) {
				Iterator<CellLikeNoSkinMembrane> it1 = m.getChildMembranes()
						.iterator();
				boolean find = false;
				CellLikeNoSkinMembrane childMembrane = null;
				while (it1.hasNext() && !find) {
					childMembrane = it1.next();
					if (irm.getLabel().equals(childMembrane.getLabel()))
						find = true;
				}
				if (find) {
					int id = childMembrane.getId();
					//Set<String> set = irm.getMultiSet().entrySet();
					Set<Pair<String,Long>> set = processMultiSet(irm.getMultiSet());
					activation.add(id, irm.getCharge(), set);
				}
			}

		}

		return activation;

	}
	
	public static Set<Pair<String,Long>> processMultiSet(MultiSet<String> set)
	{
		Set<Pair<String,Long>> result = new HashSet<Pair<String,Long>>();
		
		Iterator<String> it = set.entrySet().iterator();
		
		while(it.hasNext())
		{
			String obj	= (String) it.next();
			Long mult 	= set.count(obj);
			Pair<String,Long> p = new Pair<String,Long>(obj,mult);
			result.add(p);
		}
		
		return result;
	}

}
