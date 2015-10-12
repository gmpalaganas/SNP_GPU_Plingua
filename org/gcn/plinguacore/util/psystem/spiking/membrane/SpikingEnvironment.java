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

package org.gcn.plinguacore.util.psystem.spiking.membrane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.gcn.plinguacore.util.HashInfiniteMultiSet;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.rule.spiking.SpikingRule;


// class SpikingEnvironment

public class SpikingEnvironment extends SpikingMembrane {

	
	private static final long serialVersionUID = 5585656570644646361L;
	private HashMap<Integer, ArrayList<Short>> binarySpikeTrain = null;
	private HashMap<Integer, ArrayList<Integer>> naturalSpikeTrain = null;
	private HashMap<Long,Long> inputSequence = null;
	private long stepsTaken = 0L;
	

	public SpikingEnvironment(String label,
			SpikingMembraneStructure structure) {
		super(label,new HashInfiniteMultiSet<String>(), structure, false);
		binarySpikeTrain = new HashMap<Integer, ArrayList<Short>>();
		naturalSpikeTrain = new HashMap<Integer, ArrayList<Integer>>();
		inputSequence = new HashMap<Long,Long>();
		stepsTaken = 0L;

		

		// TODO Auto-generated constructor stub
	}
	
	public HashMap<Integer, ArrayList<Short>> getBinarySpikeTrain()
	{
		return binarySpikeTrain;
	}
	
	
	public HashMap<Integer, ArrayList<Integer>> getNaturalSpikeTrain()
	{
		return naturalSpikeTrain;
	}
	
	
	protected void initializeSpikeTrain(SpikingMembrane o)
	{
		binarySpikeTrain.put(o.getId(), new ArrayList<Short>());
		naturalSpikeTrain.put(o.getId(), new ArrayList<Integer>());
	}
	
	protected void destroySpikeTrain(SpikingMembrane o)
	{
		binarySpikeTrain.remove(o.getId());
		naturalSpikeTrain.remove(o.getId());
		
	}
	
	public void cloneSpikeTran(SpikingMembrane source, SpikingMembrane target)
	{
		int sid = source.getId();
		int tid = target.getId();
		
		ArrayList<Short>	bst = binarySpikeTrain.get(sid);
		ArrayList<Integer>	nst = naturalSpikeTrain.get(sid);
		
		if(bst == null || nst == null)
			;	// do nothing
		else
		{
			binarySpikeTrain.put(tid, bst);
			naturalSpikeTrain.put(tid, nst);
		}
		

	}
	
	public HashMap<Long,Long> getInputSequence()
	{
		return inputSequence;
	}
	
	public void setInputSequence (HashMap<Long,Long> inputSequence)
	{
		this.inputSequence = inputSequence;
	}
	
	public long getStepsTaken()
	{
		/*
		Set<Integer> keySet = binarySpikeTrain.keySet();
		
		if(keySet.isEmpty())
			return 0L;
		else
			return binarySpikeTrain.get(binarySpikeTrain.keySet().iterator().next()).size();
				
		*/
		
		return stepsTaken;
	
	}
	
	public long increaseStepsTaken()
	{
		stepsTaken++;
		return stepsTaken;
	}
	
	public long setStepsTaken(long stepsTaken)
	{
		this.stepsTaken = stepsTaken;
		return this.stepsTaken;
	}
	
	public long getInputSequenceValue(long step)
	{
		long result = 0L;
		
		if(step < 0L)
			return result;
		
		if(inputSequence.isEmpty())
			return result;
		
		if(inputSequence.containsKey(step))
			result = inputSequence.get(step);
		
		return result;
	}
	
	@Override
	public void dissolve() throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public ChangeableMembrane divide() throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}



}
