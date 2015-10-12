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
import java.util.HashMap;
import java.util.Iterator;

import java.util.Map;


import org.gcn.plinguacore.util.Pair;
import org.gcn.plinguacore.util.RandomNumbersGenerator;
import org.gcn.plinguacore.util.psystem.ActivationSets;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.rule.IConstantRule;
import org.gcn.plinguacore.util.psystem.rule.IRule;



/**
 * A (slow but realistic) simulator for probabilistic P systems
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 *
 */

public class ProbabilisticSimulator extends AbstractProbabilisticSimulator {

	

	private Map<ActivationSets, Pair<Float, float[]>> probabilities = null;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7427347014964937489L;

	public ProbabilisticSimulator(Psystem psystem) {
		super(psystem);
	
		probabilities = new HashMap<ActivationSets, Pair<Float, float[]>>();

		// TODO Auto-generated constructor stub
	}
	
	
	
	

	@Override
	protected void microStepInit() {
		// TODO Auto-generated method stub
		super.microStepInit();
		probabilities.clear();
	}

	private void nextFloats(float[] t) {
		for (int i = 0; i < t.length; i++)
			t[i] = RandomNumbersGenerator.getInstance().nextFloat();
	}

	@Override
	protected void microStepSelectRules(ChangeableMembrane m,ChangeableMembrane temp) {
		// TODO Auto-generated method stub

		Iterator<IRule> it = getPsystem().getRules().iterator(temp.getLabel(),
				temp.getCharge());

		while (it.hasNext()) {
			IRule r = it.next();
			int count = (int)r.countExecutions(temp);
			
			int selections = 0;
			if (count > 0) {
				if (r instanceof IConstantRule) {
					float min, max;
					min = 0;
					float p = ((IConstantRule) r).getConstant();
					if (p < 1) {
						float[] probs;
						ActivationSets key = ProbabilisticPsystemFactory.getActivationSets(r, (CellLikeMembrane)temp);
						
						if (!probabilities.containsKey(key)) {
							probs = new float[count];
							nextFloats(probs);
						} else {
							Pair<Float, float[]> pair = probabilities.get(key);
							probs = pair.getSecond();
							min = pair.getFirst();

						}
						max = min + p;
						probabilities.put(key, new Pair<Float, float[]>(max,
								probs));

						for (int i = 0; i < probs.length; i++) {
							if (probs[i] >= min && probs[i] < max)
								selections++;
						}
					} else
						selections = count;
				} else
					selections = count;
				if (selections > 0) {

					selectRule(r, m, selections);
					removeLeftHandRuleObjects(temp, r, selections);
				}
			}
		}

	}






}
