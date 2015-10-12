package org.gcn.plinguacore.util.psystem.spiking.membrane;

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



import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.gcn.plinguacore.util.HashMultiSet;
import org.gcn.plinguacore.util.Pair;

import java.util.Iterator;


public class Astrocyte {

	private String label;
	private Long potential;
	private SortedSet<Long> lThresholds;
	private List<String> lFunctionNames;
	private SpikingMembraneStructure structure = null;
	private HashMultiSet<String> spikes = null;
	private String type = null;
	
	private static String spikeSymbol = new String("a");
	
	
	// this method is called from the parser
	public Astrocyte(String label, List<Pair<String,String>> arcs, SortedSet<Long> lThresholds, List<String> lFunctionNames, Long potential, String type, SpikingMembraneStructure structure)
	{
		
		if (structure==null)
			throw new NullPointerException("Null membrane structure");

		this.structure=structure;
		
		this.setLabel(new String(label));
		this.setPotential(potential);
		this.setLThresholds(lThresholds);
		this.setLFunctionNames(lFunctionNames);

				
		this.spikes = new HashMultiSet<String>();
		this.type = new String(type);
		
		structure.addAstrocyte(this, arcs, false);
		
	}
	
	// this method is called from the clone method in SpikingMembraneStructure
	public Astrocyte(String label, SpikingMembraneStructure structure, Astrocyte a)
	{
		if (structure==null)
			throw new NullPointerException("Null membrane structure");

		this.structure=structure;
		
		this.setLabel(new String(label));
		this.potential = a.potential;
		this.lThresholds = a.lThresholds;
		this.lFunctionNames = a.lFunctionNames;
		this.spikes = new HashMultiSet<String>();
		spikes.add(spikeSymbol, a.getSpikes().longValue());
		this.type = a.type;
	}
	
	
	// this method should be refined in the inherited classes
	
	public boolean flush()
	{

		return false;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}


	public void setLFunctionNames(List<String> lFunctionNames)
	{
		this.lFunctionNames = new ArrayList<String>();
		
		Iterator<String> it = lFunctionNames.iterator();
		
		while(it.hasNext())
		{
			String fName = (String) it.next();
			
			if(structure.getAstrocyteFunction(fName) != null)
				this.lFunctionNames.add(fName);
			else
				this.lFunctionNames.add(new String("identity(x1)"));
			
		}
				
	}
	
	
	public List<String> getLFunctionNames() {
		return lFunctionNames;
	}

	

	public void setLThresholds(SortedSet<Long> lThresholds)
	{
		this.lThresholds = new TreeSet<Long>();
		
		Iterator<Long> it = lThresholds.iterator();
		
		while(it.hasNext())
		{
			Long threshold = (Long) it.next();
			
			if(threshold >= 0L)
				this.lThresholds.add(threshold);
			else
				this.lThresholds.add(0L);
			
			
		}
				
	}

	
	public SortedSet<Long> getLThresholds() {
		return lThresholds;
	}

	
	public Long getPotential() {
		return potential;
	}

	public void setPotential(Long potential) {
		this.potential = potential;
	}

	public void loadSpikes(Long spikesToLoad)
	{
		if(spikesToLoad >= 0L)
			spikes.add(spikeSymbol, spikesToLoad);
	}
	
	public Long getSpikes()
	{
		return this.spikes.count(spikeSymbol);
	}
	
	public void clearSpikes()
	{
		this.spikes.clear();
	}

	public List<Pair<Integer,Integer>> getArcs()
	{
		if(structure == null)
			return new ArrayList<Pair<Integer,Integer>>();
		else
			return structure.getAstrocyteArcs(this.getLabel());
	}
	
	
	public String getType()
	{
		return type;
	}
	
	public SpikingMembraneStructure getStructure()
	{
		return structure;
	}
	
	@Override
	public String toString()
	{
		
		ArrayList<Object> presenter = new ArrayList<Object>();
		presenter.add(label);
		presenter.add(lThresholds);
		presenter.add(lFunctionNames);
		presenter.add(spikes);
		presenter.add(potential);
		return presenter.toString();
		
	}
	
}


