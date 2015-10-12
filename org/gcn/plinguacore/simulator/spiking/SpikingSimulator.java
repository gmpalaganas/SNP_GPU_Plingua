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

package org.gcn.plinguacore.simulator.spiking;





import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.gcn.plinguacore.simulator.AbstractSelectionExecutionSimulator;

import org.gcn.plinguacore.util.HashMultiSet;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.Pair;
import org.gcn.plinguacore.util.RandomNumbersGenerator;

import org.gcn.plinguacore.util.psystem.Configuration;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.membrane.MembraneStructure;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.spiking.SpikingRule;
import org.gcn.plinguacore.util.psystem.spiking.membrane.SpikingEnvironment;
import org.gcn.plinguacore.util.psystem.spiking.membrane.SpikingMembrane;
import org.gcn.plinguacore.util.psystem.spiking.membrane.SpikingMembraneStructure;

import java.util.ArrayList;

public class SpikingSimulator extends AbstractSelectionExecutionSimulator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6170733461524439912L;
	/**
	 * 
	 */

	protected int asynch = 0; 		// 0: false, 1: normal asynch, 2: pre-defined value asynch
	protected int sequential = 0;	// 0: false, 1: normal sequen, 2: max pseudo-seq, 3: max seq, 4: min pseudo-seq, 5: min seq
	protected Map<String, Long> asynchValidConfiguration = null;
	protected long executionStep = 0L;	// Only usable when having and output membrane at least otw it returns zero.
	
	protected ArrayList<Pair<ChangeableMembrane, MultiSet<Object>>> division = null;
	protected ArrayList<Pair<ChangeableMembrane, MultiSet<Object>>> budding = null;
	protected ArrayList<Pair<ChangeableMembrane, MultiSet<Object>>> spiking = null;
	
	protected boolean writeToFile = true;
	
	protected boolean lastExecutionStep = false;
	
	protected boolean showBinarySequence = false;
	protected ArrayList showNaturalSequence = null;
	protected boolean showSummatories = false;
	
		
	public SpikingSimulator(Psystem psystem) {
		super(psystem);
		this.asynch = 0;
		this.sequential = 0;
		this.asynchValidConfiguration = new HashMap<String, Long>();
		this.executionStep = 0L;

		// TODO Auto-generated constructor stub
	} 

	public void setAsynch(int asynch) {
		if (asynch >= 0 && asynch <= 2)
			this.asynch = asynch;
		else
		{
			this.asynch = 0; // if not valid asynch parameter then set asynch off
			this.asynchValidConfiguration = new HashMap<String, Long>();
		}

		// TODO Auto-generated constructor stub
	}

	public int getAsynch() {
		return this.asynch;
	}

	public void setSequential(int sequential) {
		if (sequential >= 0 && sequential <= 5)
			this.sequential = sequential;
		else
			this.sequential = 0; // if not valid sequential parameter then set sequential off

		// TODO Auto-generated constructor stub
		
	}

	public int getSequential() {
		return this.sequential;
	}

	public void setAsynchValidConfiguration(Map<String, Long> validSpikes) {
		this.asynchValidConfiguration = new HashMap<String, Long>(validSpikes);
	}
	
	public Map<String, Long> getAsynchValidConfiguration() {
		return this.asynchValidConfiguration;
	}

	protected boolean decideAsynch(SpikingRule r) {
		

		if (this.asynch == 0)
			return true;

		
		RandomNumbersGenerator rgenerator = RandomNumbersGenerator
				.getInstance();
		
		boolean result = (rgenerator.nextInt(2) == 0);

		return result;
	}

	protected void decideSequential() {

		if (this.sequential == 0)
			return;

		ArrayList<Pair<ChangeableMembrane, MultiSet<Object>>> fireables = new ArrayList<Pair<ChangeableMembrane, MultiSet<Object>>>();

		Iterator<Integer> it = getSelectedRules().keySet().iterator();

		long max = Long.MIN_VALUE;
		long min = Long.MAX_VALUE;
		
		while (it.hasNext()) {
			int id = (int) it.next();

			Pair<ChangeableMembrane, MultiSet<Object>> p  = getSelectedRules().get(id);

			SpikingMembrane m = (SpikingMembrane) p.getFirst();

			MultiSet<Object> set = p.getSecond();

			if (m.isOpen())
				if (set.isEmpty() == false) // if depends on how we consider the sequential SN P-System
				{
					Iterator<Object> itr = set.iterator();
					SpikingRule r = (SpikingRule) itr.next();
                    System.out.println(r.getLeftHandRule());

					// This checking is for fast code modification
					boolean sequentialDecision = 
						r.isFiringRule()	|| r.isForgettingRule() || 
						r.isDivisionRule()	|| r.isBuddingRule();
					// We consider that both firing rules and forgetting rules can be selected
					
					if (sequentialDecision)
					{	
						fireables.add(p); 

						long spikeSize = m.getMembraneSpikingStringSize();
						
						if(spikeSize > max)
							max = spikeSize;
						
						if(spikeSize < min)
							min = spikeSize;
					
					}

				}

			// instead of cleaning the whole map, we clean element by element
			// and after add the right one
			
			getSelectedRules().put(
					id,
					new Pair<ChangeableMembrane, MultiSet<Object>>(m,
							new HashMultiSet<Object>()));

		}

		if (fireables.size() > 0) {
			
			selectSequentialMembranes(fireables,min,max);
						
		} else
			getSelectedRules().clear(); // we do this in order to not to process
										// the vector

	}

	public void selectSequentialMembranes(ArrayList<Pair<ChangeableMembrane, MultiSet<Object>>> fireables, long min, long max)
	{
		// 0: false, 1: normal sequen, 2: max pseudo-seq, 3: max seq, 4: min pseudo-seq, 5: min seq
		
		int sequentialMode = this.getSequential();
	
		if(sequentialMode == 1)		// normal sequential
		{
			RandomNumbersGenerator rgenerator = RandomNumbersGenerator.getInstance();
			
			int selected = rgenerator.nextInt(fireables.size());
			
			Pair<ChangeableMembrane, MultiSet<Object>> p = (Pair<ChangeableMembrane, MultiSet<Object>>) fireables
				.get(selected);
			
			getSelectedRules().put(p.getFirst().getId(), p);
		
		}
		else if(sequentialMode == 2)	// max pseudo-sequential
		{
			
			Iterator <Pair<ChangeableMembrane, MultiSet<Object>>> it = fireables.iterator();
			
			while(it.hasNext())
			{
				Pair<ChangeableMembrane, MultiSet<Object>> p = (Pair<ChangeableMembrane, MultiSet<Object>>) it.next();
				
				SpikingMembrane m = (SpikingMembrane) p.getFirst();
				
				if(m.getMembraneSpikingStringSize() == max)
					getSelectedRules().put(p.getFirst().getId(), p);
					
			}
			
		}
		else if(sequentialMode == 3)	// max sequential
		{
			ArrayList<Pair<ChangeableMembrane, MultiSet<Object>>> fireablesAux = new ArrayList<Pair<ChangeableMembrane, MultiSet<Object>>>();
			
			Iterator <Pair<ChangeableMembrane, MultiSet<Object>>> it = fireables.iterator();
			
			while(it.hasNext())
			{
				Pair<ChangeableMembrane, MultiSet<Object>> p = (Pair<ChangeableMembrane, MultiSet<Object>>) it.next();
				
				SpikingMembrane m = (SpikingMembrane) p.getFirst();
				
				if(m.getMembraneSpikingStringSize() == max)
					fireablesAux.add(p);
					
			}
			
			RandomNumbersGenerator rgenerator = RandomNumbersGenerator.getInstance();
			
			int selected = rgenerator.nextInt(fireablesAux.size());
			
			Pair<ChangeableMembrane, MultiSet<Object>> p = (Pair<ChangeableMembrane, MultiSet<Object>>) fireablesAux
				.get(selected);
			
			getSelectedRules().put(p.getFirst().getId(), p);

		}
		else if(sequentialMode == 4)	// min pseudo-sequential
		{
			
			Iterator <Pair<ChangeableMembrane, MultiSet<Object>>> it = fireables.iterator();
			
			while(it.hasNext())
			{
				Pair<ChangeableMembrane, MultiSet<Object>> p = (Pair<ChangeableMembrane, MultiSet<Object>>) it.next();
				
				SpikingMembrane m = (SpikingMembrane) p.getFirst();
				
				if(m.getMembraneSpikingStringSize() == min)
					getSelectedRules().put(p.getFirst().getId(), p);
					
			}
			
		}
		else if(sequentialMode == 5)	// min sequential
		{
			ArrayList<Pair<ChangeableMembrane, MultiSet<Object>>> fireablesAux = new ArrayList<Pair<ChangeableMembrane, MultiSet<Object>>>();
			
			Iterator <Pair<ChangeableMembrane, MultiSet<Object>>> it = fireables.iterator();
			
			while(it.hasNext())
			{
				Pair<ChangeableMembrane, MultiSet<Object>> p = (Pair<ChangeableMembrane, MultiSet<Object>>) it.next();
				
				SpikingMembrane m = (SpikingMembrane) p.getFirst();
				
				if(m.getMembraneSpikingStringSize() == min)
					fireablesAux.add(p);
					
			}
			
			RandomNumbersGenerator rgenerator = RandomNumbersGenerator.getInstance();
			
			int selected = rgenerator.nextInt(fireablesAux.size());
			
			Pair<ChangeableMembrane, MultiSet<Object>> p = (Pair<ChangeableMembrane, MultiSet<Object>>) fireablesAux
				.get(selected);
			
			getSelectedRules().put(p.getFirst().getId(), p);

		}
		
		
	}
	
	public ArrayList getExecutionResult() {
		
		SpikingMembraneStructure structure = (SpikingMembraneStructure) currentConfig
				.getMembraneStructure();

		String envLabel = structure.getEnvironmentLabel();

		SpikingEnvironment env = (SpikingEnvironment) structure.getEnvironmentMembrane();
						
		long envSpikes = env.getMembraneSpikingStringSize();
		
		HashMap<Integer, ArrayList<Short>> binarySpikeTrain = env.getBinarySpikeTrain();

		HashMap<Integer, ArrayList<Integer>> naturalSpikeTrain = env.getNaturalSpikeTrain();
		
		HashMap<Integer, String> membraneMapping = new HashMap<Integer, String>();
		
		HashMap<Integer, Long> spikes = new HashMap<Integer, Long>();
		
		boolean validAsynchExecution = true;
		
		boolean strongCase = true;

		Iterator<SpikingMembrane> itmem = (Iterator<SpikingMembrane>) structure
				.getAllMembranes().iterator();

		while (itmem.hasNext()) {
			SpikingMembrane mem = (SpikingMembrane) itmem.next();

			int id = mem.getId();

			String label = mem.getLabel();
			
			membraneMapping.put(id, label);
			
			long size = mem.getMembraneSpikingStringSize();

			spikes.put(id, size);

			if (size > 0L && !mem.getLabel().equals(envLabel))
				strongCase = false;

		}
		
		if(this.asynch == 2)
			validAsynchExecution = computeValidAsynchExecution();
				
		ArrayList executionResult = new ArrayList();

		executionResult.add(membraneMapping);
		executionResult.add(envSpikes);
		executionResult.add(spikes);
		executionResult.add(validAsynchExecution);
		executionResult.add(strongCase);
		executionResult.add(binarySpikeTrain);
		executionResult.add(naturalSpikeTrain);

		return executionResult;

	}
	
	public HashMap<Integer, ArrayList<Short>> computeBinarySequence()
	{
		
		SpikingMembraneStructure structure = (SpikingMembraneStructure) currentConfig
		.getMembraneStructure();

		SpikingEnvironment env = (SpikingEnvironment) structure.getEnvironmentMembrane();
						
		HashMap<Integer, ArrayList<Short>> binarySequence = env.getBinarySpikeTrain();
		
		return binarySequence;
	}
	
	public HashMap<Integer, HashSet<Integer>> computeNaturalSequence(long k, boolean strong, boolean alternate)
	{
		
		HashMap<Integer, HashSet<Integer>> result = new HashMap<Integer, HashSet<Integer>>();

		if(k < 2)
			return result;
		
		SpikingMembraneStructure structure = (SpikingMembraneStructure) currentConfig
		.getMembraneStructure();
		
		SpikingEnvironment env = (SpikingEnvironment) structure.getEnvironmentMembrane();
		
		HashMap<Integer, ArrayList<Integer>> naturalSpikeTrain = env.getNaturalSpikeTrain();

		HashMap<Integer, ArrayList<Integer>> natualSequence = env.getNaturalSpikeTrain();
						
		if(natualSequence.size() == 0)	// if we don't have any results (i. e. no output membranes, 
			return result;				// no computations...) then we are done
		
		Iterator<Integer> it = natualSequence.keySet().iterator();
		
		while(it.hasNext())
		{
			int key = (int) it.next();
			ArrayList<Integer> sequence = natualSequence.get(key);
			HashSet<Integer> keyResult = new HashSet<Integer>();
			
			long spikeCount = sequence.size(); 

			if((strong == true && spikeCount != k) || (strong == false && spikeCount < k))
				result.put(key, keyResult);
			else
			{
				keyResult = computeSingleNaturalSequence(k,alternate,sequence);
				result.put(key, keyResult);
			}
			
		}
			
		return result;
	}
	
	
	private HashSet<Integer> computeSingleNaturalSequence(long k, boolean alternate, ArrayList<Integer>sequence)
	{
		
		HashSet<Integer> result = new HashSet<Integer>();
		
		boolean flagAlternate = true;
		
		int i = 0;
		
		while(i < sequence.size() - 1 && result.size() < k)
		{
			int first = sequence.get(i);
			int second = sequence.get(i+1);
			
			i++;
			
			if(alternate)
			{	
				if(flagAlternate)
				{
					result.add(second-first);
					flagAlternate = !flagAlternate;
				}
			}
			else
			{
				result.add(second-first);
			}
			
		}
		
		return result;
	}
	
	public HashMap<Integer, Long> computeOutputSummatories()
	{
		
		HashMap<Integer, Long> result = new HashMap<Integer, Long>();

		
		HashMap<Integer, ArrayList<Integer>> natualSequence = 
						(HashMap<Integer, ArrayList<Integer>>) this.getExecutionResult().get(6);
		
		if(natualSequence.size() == 0)	// if we don't have any results (i. e. no output membranes, 
			return result;				// no computations...) then we are done
		
		Iterator<Integer> it = natualSequence.keySet().iterator();
		
		while(it.hasNext())
		{
			int key = (int) it.next();
			ArrayList<Integer> sequence = natualSequence.get(key);
			long spikeCount = sequence.size(); 
			result.put(key, spikeCount);
			
		}
			
		return result;
	}
	
	public boolean computeValidAsynchExecution()
	{
		boolean result = true;
		
		SpikingMembraneStructure structure = (SpikingMembraneStructure) currentConfig
		.getMembraneStructure();

		Map<String,Long> valid = this.getAsynchValidConfiguration();
		
		Iterator<String> it = valid.keySet().iterator();
		
		while(it.hasNext() && result)
		{
			String label = (String) it.next();
			long value = valid.get(label);
			
			Iterator<SpikingMembrane> itm = structure.getCellsByLabel(label).iterator();
			
			while(itm.hasNext() && result)
			{
				SpikingMembrane m = (SpikingMembrane) itm.next();
				
				long spikes = m.getMembraneSpikingStringSize();
				
				if(spikes != value)
					result = false;
			}
		}
		
		return result;
	}
	
	@Override
	protected String getHead(ChangeableMembrane m) {
		// TODO Auto-generated method stub
		String str;
		str = "CELL ID: " + m.getId();
		str += ", Label: " + m.getLabelObj();
		return str;
	}

	@Override
	public void microStepInit() {
		// TODO Auto-generated method stub
		
		super.microStepInit();
		SpikingEnvironment env = ((SpikingMembraneStructure) currentConfig.getMembraneStructure()).getEnvironmentMembrane();
		executionStep = env.increaseStepsTaken();
		
		if(writeToFile)
		{
			PrintStream sout = System.out;
			PrintStream serr = System.err;
					
			try 
			{

				String filename = System.getProperty("user.dir") + "/steps/step-" + executionStep + ".txt";
				OutputStream output = new FileOutputStream(filename);
				PrintStream printOut = new PrintStream(output);
				
				System.setOut(printOut);
				System.setErr(printOut);     
			} 
			catch (Exception e)
			{
				System.setOut(sout);
				System.setErr(serr);
				writeToFile = false;
			}			
		}
		
		if(this.executionStep == 1L)
		{

			SpikingMembraneStructure s = 
				(SpikingMembraneStructure) currentConfig.getMembraneStructure();
			
			this.showBinarySequence = s.getShowBinarySequence();
			this.showNaturalSequence = (ArrayList) s.getShowNaturalSequence();
			this.showSummatories = s.getShowSummatories();
			this.setSequential(s.getSequentialMode());
			this.setAsynch(s.getAsynchMode());
			this.setAsynchValidConfiguration(s.getAsynchValidConfiguration());
			
		}
		
	}

	@Override
	public void microStepSelectRules(Configuration cnf, Configuration tmpCnf) {
		// TODO Auto-generated method stub

		// cnf is the configuration to write
		// tmpCnf is the configuration to operate with

		Iterator<? extends Membrane> it = tmpCnf.getMembraneStructure()
				.getAllMembranes().iterator();
		Iterator<? extends Membrane> it1 = cnf.getMembraneStructure()
				.getAllMembranes().iterator();

		while (it.hasNext()) {
			ChangeableMembrane tempMembrane = (ChangeableMembrane) it.next();
			ChangeableMembrane m = (ChangeableMembrane) it1.next();
			microStepSelectRules(m, tempMembrane);
		}

		if (this.getSequential() != 0)	// if we have a sequential scenario then process the selected rules
			this.decideSequential();
		
		this.lastExecutionStep = getSelectedRules().isEmpty();
		
		if(this.lastExecutionStep)
			microStepPrintFinalResults();
		
	}

	@Override
	protected void microStepSelectRules(ChangeableMembrane m,
			ChangeableMembrane temp) {

		SpikingMembrane s = (SpikingMembrane) m;
		SpikingMembraneStructure structure = (SpikingMembraneStructure) currentConfig.getMembraneStructure();

		// If the neuron is closed and was doing budding or division, 
		// we open it first of all so can select new rules.

		if(s.isClosed() && s.getBuddingDivision())
			s.setMembraneOpen();
		
		if (s.isClosed()) // If the neuron is closed, no rule can be selected
		{
			// but we must decrease the stepsToOpen counter
			s.decreaseStepsToOpen();

			Pair<ChangeableMembrane, MultiSet<Object>> p = new Pair<ChangeableMembrane, MultiSet<Object>>(
					m, new HashMultiSet<Object>());

			getSelectedRules().put(m.getId(), p);
			p.getSecond().add(s.getSelectedRule());

		} 
		else
		{
			ArrayList<SpikingRule> activeRules = new ArrayList<SpikingRule>();

			Iterator<IRule> it = this.getPsystem().getRules()
					.iterator(s.getLabel(), s.getCharge());

			while (it.hasNext())
			{
				SpikingRule frule = (SpikingRule) it.next();
				
				if (frule.canBeExecuted(s,structure))
				{
					if(this.getAsynch() != 0)			// This checking is for fast code modification	
					{
						boolean asynchDecision = 
							frule.isFiringRule()	|| frule.isForgettingRule() || 
							frule.isBuddingRule()	|| frule.isDivisionRule();
						
						if(asynchDecision)	// This checking is for fast code modification
							activeRules.add(frule);		// In Asynch mode we consider both kind of rules
					}
					else
						activeRules.add(frule);

				}

			}

			if (activeRules.size() == 0) {
				s.setMembraneOpen(); // No selected rules for this step we open the membrane

			} else
			{
				RandomNumbersGenerator rgenerator = RandomNumbersGenerator
						.getInstance();
				
				int selected = rgenerator.nextInt(activeRules.size());
				SpikingRule selectedRule = activeRules.get(selected);
				s.setSelectedRule(selectedRule);

				Pair<ChangeableMembrane, MultiSet<Object>> p = new Pair<ChangeableMembrane, MultiSet<Object>>(
						m, new HashMultiSet<Object>());

				getSelectedRules().put(m.getId(), p);
				p.getSecond().add(s.getSelectedRule());

			}

		}

	}

	@Override
	public void microStepExecuteRules() {
		// TODO Auto-generated method stub

		Iterator<Pair<ChangeableMembrane, MultiSet<Object>>> it = getSelectedRules()
				.values().iterator();

		division		= new ArrayList<Pair<ChangeableMembrane, MultiSet<Object>>>();
		budding			= new ArrayList<Pair<ChangeableMembrane, MultiSet<Object>>>();
		spiking 	= new ArrayList<Pair<ChangeableMembrane, MultiSet<Object>>>();
		
		SpikingRule rule = null;

		while (it.hasNext()) {
			Pair<ChangeableMembrane, MultiSet<Object>> p = (Pair<ChangeableMembrane, MultiSet<Object>>) it
					.next();

			// SpikingMembrane s = (SpikingMembrane) p.getFirst(); -> not necessary
			MultiSet<Object> mset = (MultiSet<Object>) p.getSecond();
						
			if (mset.size() == 0)
				rule = null;
			else
				rule = (SpikingRule) mset.iterator().next();
			
			if(rule.isFiringRule() || rule.isForgettingRule())	// we have an spiking rule
				spiking.add(p);
			else if(rule.isBuddingRule())
				budding.add(p);
			else if(rule.isDivisionRule())
				division.add(p);
			
			}
		
		microStepExecuteBuddingDivisionRules();
		microStepExecuteSpikingRules();
		microStepExecuteFlushAstrocytes();
		microStepExecuteInputSequence();
		microStepExecuteOutputMembranes();
		microStepPrintResults();
	}

	protected void microStepExecuteBuddingDivisionRules() {
		


		SpikingMembraneStructure structure = (SpikingMembraneStructure) currentConfig
		.getMembraneStructure();

		SpikingEnvironment env = (SpikingEnvironment) structure.getEnvironmentMembrane();
						
		HashMap<Integer, ArrayList<Short>> binarySpikeTrain = env.getBinarySpikeTrain();
		
		SpikingRule rule = null;
		
		ArrayList<SpikingMembrane>
			buddyList = new ArrayList<SpikingMembrane>(); 

		ArrayList<Pair<SpikingMembrane,SpikingMembrane>> 
			divisionList = new ArrayList<Pair<SpikingMembrane, SpikingMembrane>>();
		
		Iterator<Pair<ChangeableMembrane, MultiSet<Object>>> it = null;

		// 1. We execute division phase one
		
		it = division.iterator();

		while (it.hasNext())
		{
			Pair<ChangeableMembrane, MultiSet<Object>> p = 
				(Pair<ChangeableMembrane, MultiSet<Object>>) it.next();

			SpikingMembrane s = (SpikingMembrane) p.getFirst();
			MultiSet<Object> mset = (MultiSet<Object>) p.getSecond();
			
			boolean isOutput	= structure.isOutput(s);
			boolean skipped		= false;

			System.out.println();
			System.out.println("Trying execution for...");
			System.out.println(s);

			rule = (SpikingRule) mset.iterator().next(); // this returns the first rule (the one)

			if (this.decideAsynch(rule))
			{
			
				System.out.println("Executing rule...");
				System.out.println(rule);
			
				rule.executeSafeBuddingDivisionPhaseOne(s, currentConfig, buddyList, divisionList);
			}
			else
			{
				System.out.println("Asynch P-System: skipping execution for rule...");
				System.out.println(rule);
				skipped = true;
				
			}
			
			if (isOutput && skipped == false)
			{

				ArrayList<Short> result = null;
				
				SpikingMembrane buddy1 = (SpikingMembrane) divisionList.get(buddyList.size() - 1).getFirst();
				
				result = binarySpikeTrain.get(buddy1.getId());
				result.add(new Short((short) 0));
				binarySpikeTrain.put(buddy1.getId(), result);
		
				SpikingMembrane buddy2 = (SpikingMembrane) divisionList.get(buddyList.size() - 1).getSecond();
				
				result = binarySpikeTrain.get(buddy2.getId());
				result.add(new Short((short) 0));
				binarySpikeTrain.put(buddy2.getId(), result);
				
			}
			else if (isOutput && skipped == true)
			{
				
				ArrayList<Short> result = binarySpikeTrain.get(s.getId());
				result.add(new Short((short) 0));
				binarySpikeTrain.put(s.getId(), result);
				
			}
				

		}	
		
		// 2. We execute budding phase one

		it = budding.iterator();

		while (it.hasNext())
		{
			Pair<ChangeableMembrane, MultiSet<Object>> p = 
				(Pair<ChangeableMembrane, MultiSet<Object>>) it.next();

			SpikingMembrane s = (SpikingMembrane) p.getFirst();
			MultiSet<Object> mset = (MultiSet<Object>) p.getSecond();
			
			boolean isOutput	= structure.isOutput(s);
			boolean skipped		= false;

			System.out.println();
			System.out.println("Trying execution for...");
			System.out.println(s);

			rule = (SpikingRule) mset.iterator().next(); // this returns the first rule (the one)

			if (this.decideAsynch(rule))
			{
			
				System.out.println("Executing rule...");
				System.out.println(rule);
			
				rule.executeSafeBuddingDivisionPhaseOne(s, currentConfig, buddyList, divisionList);
			}
			else
			{
				System.out.println("Asynch P-System: skipping execution for rule...");
				System.out.println(rule);
				skipped = true;
				
			}
			
			if (isOutput && skipped == false)
			{

				ArrayList<Short> result = null;
				
				SpikingMembrane buddy = (SpikingMembrane) buddyList.get(buddyList.size() - 1);
				
				result = binarySpikeTrain.get(buddy.getId());
				result.add(new Short((short) 0));
				binarySpikeTrain.put(buddy.getId(), result);
		
			}
			else if (isOutput && skipped == true)
			{
				
				ArrayList<Short> result = binarySpikeTrain.get(s.getId());
				result.add(new Short((short) 0));
				binarySpikeTrain.put(s.getId(), result);
				
			}

		}	
		
		// At the end of phase one, neurons are created, the synapses are inherited and the output spike trains are updated.
		// Let's continue with phase two, iterating over divisionList and buddyList.

		boolean isBudding;
		
		// 3. We execute division phase two
		
		isBudding = false;
		
		Iterator<Pair<SpikingMembrane,SpikingMembrane>> itdivision = divisionList.iterator();
		
		while(itdivision.hasNext())
		{
			Pair<SpikingMembrane,SpikingMembrane> pair = (Pair<SpikingMembrane,SpikingMembrane>) itdivision.next();
			
			SpikingMembrane s1 = pair.getFirst();
			SpikingMembrane s2 = pair.getSecond();
			SpikingRule.executeSafeBuddingDivisionPhaseTwo(s1, s2, currentConfig, isBudding);			
			
		}
		
		// 4. We execute budding phase two
		
		isBudding = true;
		
		Iterator<SpikingMembrane> itbuddy = buddyList.iterator();
		
		while(itbuddy.hasNext())
		{
			SpikingMembrane s = (SpikingMembrane) itbuddy.next();
			SpikingRule.executeSafeBuddingDivisionPhaseTwo(s, null, currentConfig, isBudding);
		}
		

	}
	
	protected void microStepExecuteSpikingRules()
	{
		// TODO Auto-generated method stub

		Iterator<Pair<ChangeableMembrane, MultiSet<Object>>> it = spiking.iterator();

		SpikingMembraneStructure structure = (SpikingMembraneStructure) currentConfig
		.getMembraneStructure();
		
		SpikingEnvironment env = (SpikingEnvironment) structure.getEnvironmentMembrane();
						
		HashMap<Integer, ArrayList<Short>> binarySpikeTrain = env.getBinarySpikeTrain();
		
		HashMap<Integer, ArrayList<Integer>> naturalSpikeTrain = env.getNaturalSpikeTrain();

		SpikingRule rule = null;

		while (it.hasNext()) {
			
			Pair<ChangeableMembrane, MultiSet<Object>> p = 
				(Pair<ChangeableMembrane, MultiSet<Object>>) it.next();

			SpikingMembrane s = (SpikingMembrane) p.getFirst();
			MultiSet<Object> mset = (MultiSet<Object>) p.getSecond();
			
			boolean isOutput = structure.isOutput(s);

			System.out.println();
			System.out.println("Trying execution for...");
			System.out.println(s);

			if (s.isClosed()) // If the neuron is closed, we can't fire
			{
				rule = (SpikingRule) mset.iterator().next(); // we set the rule in order to write the output
				
				if (isOutput)
				{
					ArrayList<Short> result = binarySpikeTrain.get(s.getId());
					result.add(new Short((short) 0));
					binarySpikeTrain.put(s.getId(), result);
				}

				System.out.println("membrane closed, not firing");
			}

			else if (mset.size() > 0) // so we have an open neuron with a selected rule and can try execution
			{

				rule = (SpikingRule) mset.iterator().next(); // this returns the first rule (the one)

				if (this.decideAsynch(rule))
				{

					System.out.println("Executing rule...");
					System.out.println(rule);

					rule.executeSafeSpiking(s, currentConfig);

					if (rule.isForgettingRule() && isOutput)
					{
						ArrayList<Short> result = binarySpikeTrain.get(s.getId());
						result.add(new Short((short) 0));
						binarySpikeTrain.put(s.getId(), result);
					}
					else if (rule.isFiringRule() && isOutput)
					{
						ArrayList<Short> result = binarySpikeTrain.get(s.getId());
						result.add(new Short((short) 1));
						binarySpikeTrain.put(s.getId(), result);
					}

				}
				else
				{
					System.out.println("Asynch P-System: skipping execution for rule...");
					System.out.println(rule);

					if (isOutput)
					{
						ArrayList<Short> result = binarySpikeTrain.get(s.getId());
						result.add(new Short((short) 0));
						binarySpikeTrain.put(s.getId(), result);
					}

				}
				
				if(isOutput)
				{
					ArrayList<Short> binaryResult = binarySpikeTrain.get(s.getId());
					
					if( ((short) binaryResult.get(binaryResult.size() - 1)) == 1 )
					{
						ArrayList<Integer> naturalResult = naturalSpikeTrain.get(s.getId());
						naturalResult.add(new Integer(binaryResult.size()));
						naturalSpikeTrain.put(s.getId(), naturalResult);
					}
				}
				
			}

			System.out.println();
			System.out.println("********************************************************************");
			System.out.println("For membrane: " + s.getLabel().toString());
			System.out.println("Rule selected: " + rule);
			System.out.println("Rule fired?: " + s.isOpen());
			System.out.println("Steps to open and fire (if zero fired now): "
					+ s.getStepsToOpen());
			System.out.println("********************************************************************");

		}
		
	}
	
	protected void microStepExecuteFlushAstrocytes() {
		
		SpikingMembraneStructure structure = (SpikingMembraneStructure) currentConfig
		.getMembraneStructure();

		structure.flushAstrocytes();
	}
	
	protected void microStepExecuteInputSequence()
	{
		SpikingMembraneStructure structure = (SpikingMembraneStructure) currentConfig
		.getMembraneStructure();
		
		SpikingEnvironment env = (SpikingEnvironment) structure.getEnvironmentMembrane();
		
		SpikingMembrane input = structure.getInputMembrane();
		
		if(input != null)
		{

	
			System.out.println();
			System.out.println("********************************************************************");
			System.out.println("Input Membrane: " + input);
			System.out.println("Step: " + this.executionStep);
			System.out.println("Trying to receive input spikes from the input spike train...");
			
			if(input.isClosed())
			{
				System.out.println("membrane closed, not doing anything");
			}
			else
			{
				long spikes = env.getInputSequenceValue(this.executionStep);
				
				if(spikes == 0L)
					System.out.println("zero input spikes for this step, not doing anything");
				else
					SpikingRule.executeSafeInputSpiking(input, spikes);
			}
		
			System.out.println("********************************************************************");
		}
	
	}
	
	protected void microStepExecuteOutputMembranes()
	{
		
		SpikingMembraneStructure structure = (SpikingMembraneStructure) currentConfig
		.getMembraneStructure();
		
		SpikingEnvironment env = (SpikingEnvironment) structure.getEnvironmentMembrane();
		
		HashMap<Integer, ArrayList<Short>> binarySpikeTrain = env.getBinarySpikeTrain();
		
		ArrayList<Short> result = null;
		
		Iterator<Integer> it = binarySpikeTrain.keySet().iterator();
		
		while(it.hasNext())
		{
			int key = it.next();
			
			result = binarySpikeTrain.get(key);

			if(result.size() == this.executionStep - 1)
			{
				result.add(new Short((short) 0));
				binarySpikeTrain.put(key, result);
								
			}

		}
		
	}
	
	protected void microStepPrintResults()
	{
		SpikingMembraneStructure structure = (SpikingMembraneStructure) currentConfig
		.getMembraneStructure();
		
		SpikingEnvironment env = (SpikingEnvironment) structure.getEnvironmentMembrane();
		
		System.out.println("Execution Results at step: "+ this.executionStep);
		System.out.println();
		System.out.println("Binary Sequence:");
		System.out.println();
		System.out.println(env.getBinarySpikeTrain());
		System.out.println();
		System.out.println("Natural Sequence:");
		System.out.println();
		System.out.println(env.getNaturalSpikeTrain());
		System.out.println();
		
		
	}
	
	protected void microStepPrintFinalResults()
	{
		if(this.lastExecutionStep)
		{
			if(this.showBinarySequence)
			{
				System.out.println("For the halting configuration, Binary Sequence");
				System.out.println(this.computeBinarySequence());
				System.out.println();
			}
			
			if(this.showNaturalSequence != null)
			{
							
				long k = (Long) this.showNaturalSequence.get(0);
				boolean strong  = (Boolean) this.showNaturalSequence.get(1);
				boolean alternate = (Boolean) this.showNaturalSequence.get(2);
				
				System.out.println("For the halting configuration, Natural Sequence");
				System.out.println(this.computeNaturalSequence(k,strong,alternate));
				System.out.println();
			}
			
			if(this.showSummatories)
			{
				System.out.println("For the halting configuration, Output Summatories");
				System.out.println(this.computeOutputSummatories());
				System.out.println();
			}
			
			if(this.asynch == 2)
			{
				System.out.println("For the halting configuration, Valid configuration");
				System.out.println(this.computeValidAsynchExecution());
				System.out.println();
			}	
		}
	}
	
	@Override
	protected void printInfoMembrane(ChangeableMembrane membrane) {
		// TODO Auto-generated method stub

		getInfoChannel().println("    " + getHead(membrane));
		getInfoChannel().println("    Multiset: " + membrane.getMultiSet());
		getInfoChannel().println();
	}

	@Override
	protected void removeLeftHandRuleObjects(ChangeableMembrane membrane,
			IRule r, long count) {
		// TODO Auto-generated method stub
		MultiSet<String> ms = r.getLeftHandRule().getOuterRuleMembrane()
				.getMultiSet();
		if (!ms.isEmpty())
			membrane.getMultiSet().subtraction(ms, count);
	}

	@Override
	protected void printInfoMembraneShort(MembraneStructure membranes) {
		// TODO Auto-generated method stub

	}

}
