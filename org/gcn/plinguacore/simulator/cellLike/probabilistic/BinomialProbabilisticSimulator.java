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


import org.gcn.plinguacore.util.RandomNumbersGenerator;
import org.gcn.plinguacore.util.psystem.ActivationSets;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.IConstantRule;

import cern.jet.random.Binomial;


/**
 * A (fast) simulator for probabilistic P systems which uses the binomial distribution
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 *
 */
public class BinomialProbabilisticSimulator extends AbstractProbabilisticSimulator {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -8850790290242455072L;
	private Map<ActivationSets,Double> activations=null;
	
	private static Binomial binomial=null;
	public BinomialProbabilisticSimulator(Psystem psystem) {
		super(psystem);
		activations = new HashMap<ActivationSets,Double>();
	
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void microStepInit() {
		// TODO Auto-generated method stub
		super.microStepInit();
		activations.clear();
	}
	

	
	protected static long binomial(long n,double p)
	{
		
		if (n==0)
			return 0;
		
		if (binomial==null)
			binomial=new Binomial(Integer.MAX_VALUE,p,RandomNumbersGenerator.getInstance());
		
		if (n<=Integer.MAX_VALUE)
			return binomial.nextInt((int)n,p);
		
		
		int m =(int) (n / Integer.MAX_VALUE);
		int b = binomial.nextInt(Integer.MAX_VALUE,p);
	
		return b*m; 
		
	}

	


	@Override
	protected void microStepSelectRules(ChangeableMembrane m,ChangeableMembrane temp) {
		
		Iterator<IRule> it = getPsystem().getRules().iterator(
				temp.getLabel(),
				temp.getLabelObj().getEnvironmentID(),
				temp.getCharge(),true);
		
		while(it.hasNext())
		{
			IRule r = it.next();
			long N = r.countExecutions(temp);
			long n; 
			
			if (N==0)
				n=0;
			else
			if (!(r instanceof IConstantRule))
				n=N;
			else
			{
				double p = ((IConstantRule)r).getConstant();
				if (p==0)
					n=0;
				else
				if (p==1)
					n=N;
				else
				{
					double d=1;
					ActivationSets key = ProbabilisticPsystemFactory.getActivationSets(r,(CellLikeMembrane)temp);
				
					if (activations.containsKey(key))
						d = activations.get(key);
					
					p = p/d;
					double q = 1-p;
					d = d*q;
					activations.put(key, d);
					
					if (p>=1)
						n=N;
					else
						n = binomial(N,p);
					
				}
			}
			
			if (n>0)
			{
				selectRule(r,m,n);
				removeLeftHandRuleObjects(temp,r,n);
			}
			
		
			
		}
		
	}

	
	
	
	

}
