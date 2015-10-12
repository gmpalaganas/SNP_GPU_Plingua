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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.gcn.plinguacore.util.InfiniteMultiSet;
import org.gcn.plinguacore.util.Pair;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeSkinMembrane;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.membrane.MembraneStructure;
import org.gcn.plinguacore.util.psystem.spiking.SpikingConstants;

public class SpikingMembraneStructure implements MembraneStructure {

	
	private Map<Integer,SpikingMembrane>cellsById;
	private Map<String,List<SpikingMembrane>>cellsByLabel;
	private SpikingEnvironment environment = null;
	private Integer input = null;
	private Map<String,Set<String>> dictionary = null;
	private Map<Integer,Integer> cloneMap = null;
	private Map<Integer,Set<Integer>> graph = null;
	private Map<Integer,Set<Integer>> rgraph = null;
	
	// attributes to store / recover data associated to arcs

	private Map<String,Astrocyte> astrocytes = null;
	private Map<Pair<Integer,Integer>,ArcInfo> arcsInfo = null;
	private Map<String,List<Pair<Integer,Integer>>> astroToArcs = null;
	private Map<String,List<Pair<Integer,Integer>>> astroToCtrlArcs = null;
	private Map<String,EvaluableFunction> astroFunctions = null;	
	
	// the output membranes are in two ways calculated:
	// - as the predecessors to the environment (for spiking reasons)
	// - as members of the following set (for efficiency reasons)
	
	private Set<Integer> output = null;
	
	// the next attributes are needed only in terms of simulation parameters
	
	private boolean showBinarySequence = false;
	private List<Object> showNaturalSequence = null;
	private boolean showSummatories = false;
	private int sequentialMode = 0;
	private int asynchMode = 0;
	private Map<String, Long> asynchValidConfiguration = null; 
	
	public SpikingMembraneStructure(String envLabel)
	{
		super();
		
		cellsById = new LinkedHashMap<Integer,SpikingMembrane>();
		cellsByLabel = new LinkedHashMap<String,List<SpikingMembrane>>();
		environment = new SpikingEnvironment(envLabel,this);
		secureAdd(environment);
		environment.setStepsTaken(0L);
		
		input = null;
		dictionary = new LinkedHashMap<String,Set<String>>();
		cloneMap = null;
				
		graph = new LinkedHashMap<Integer, Set<Integer>>();
		rgraph = new LinkedHashMap<Integer, Set<Integer>>(); 
		
		astrocytes = new LinkedHashMap<String,Astrocyte>();
		arcsInfo = new LinkedHashMap<Pair<Integer,Integer>,ArcInfo>();
		astroToArcs = new LinkedHashMap<String,List<Pair<Integer,Integer>>>();
		astroToCtrlArcs = new LinkedHashMap<String,List<Pair<Integer,Integer>>>();
		
		astroFunctions = new LinkedHashMap<String,EvaluableFunction>(); 		
		
		EvaluableFunction afIdentity = new AstrocyteFunction();
		try {
			afIdentity.storeFunction("identity(x1)=x1", 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		astroFunctions.put("identity(x1)", afIdentity);
		
		EvaluableFunction afZero = new AstrocyteFunction();
		try {
			afZero.storeFunction("zero(x1)=0", 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		astroFunctions.put("zero(x1)", afZero);
		
		EvaluableFunction afPol = new AstrocyteFunction();
		try {
			afPol.storeFunction("pol()=0", 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		astroFunctions.put("pol()", afPol);
		
		EvaluableFunction afSub = new AstrocyteFunction();
		try {
			afSub.storeFunction("sub()=0", 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		astroFunctions.put("sub()", afSub);
		
		output = new HashSet<Integer>(); 
		
		showBinarySequence = false;
		showNaturalSequence = null;
		showSummatories = false;
		sequentialMode = 0;
		asynchMode = 0;
		asynchValidConfiguration = new HashMap<String, Long>();
		

	}

	public SpikingMembraneStructure(MembraneStructure membrane)
	{
		super();
				
		cellsById = new LinkedHashMap<Integer,SpikingMembrane>();
		cellsByLabel = new LinkedHashMap<String,List<SpikingMembrane>>();
		input = null;
		dictionary = null;
		cloneMap = new LinkedHashMap<Integer,Integer>();	// this is used to clone the structure mapping  to new id's
		
		graph = new LinkedHashMap<Integer, Set<Integer>>();
		rgraph = new LinkedHashMap<Integer, Set<Integer>>(); 
		
		astrocytes = new LinkedHashMap<String,Astrocyte>();
		arcsInfo = new LinkedHashMap<Pair<Integer,Integer>,ArcInfo>();
		astroToArcs = new LinkedHashMap<String,List<Pair<Integer,Integer>>>();
		astroToCtrlArcs = new LinkedHashMap<String,List<Pair<Integer,Integer>>>();
		astroFunctions = new LinkedHashMap<String,EvaluableFunction>(); 
		
		output = new HashSet<Integer>(); 
		
		showBinarySequence = false;
		showNaturalSequence = null;
		showSummatories = false;
		sequentialMode = 0;
		asynchMode = 0;
		asynchValidConfiguration = new HashMap<String, Long>();
		
		
		Iterator<? extends Membrane>it;

		if (membrane instanceof SpikingMembraneStructure)
		{
			SpikingMembraneStructure sps = (SpikingMembraneStructure)membrane;
			environment = new SpikingEnvironment(sps.getEnvironmentLabel(),this);
			environment.getMultiSet().addAll(sps.getEnvironment());
			secureAdd(environment);							// this will add the environment in any case
			environment.setStepsTaken(sps.environment.getStepsTaken());
			
			this.showBinarySequence = sps.showBinarySequence;
			this.showNaturalSequence = sps.showNaturalSequence;
			this.showSummatories = sps.showSummatories;
			this.sequentialMode = sps.sequentialMode;
			this.asynchMode = sps.asynchMode;
			this.asynchValidConfiguration = sps.asynchValidConfiguration;
			
			it = membrane.getAllMembranes().iterator();

			while(it.hasNext())
			{
				Membrane m = it.next();
				
				if (m.getLabel().equals(environment.getLabel()))
				{
					if (membrane instanceof CellLikeSkinMembrane)
						throw new IllegalArgumentException("The environment label cannot be used as membrane label");
				
					if (m instanceof ChangeableMembrane)
						cloneMap.put(((ChangeableMembrane)m).getId(), environment.getId());
					else
						throw new IllegalArgumentException("Changeable Membranes are needed in otder to Map Ids");
				}	
				else
				{
					SpikingMembrane mem = new SpikingMembrane(m,this);
					add(mem);
					
					if (m instanceof ChangeableMembrane)
						cloneMap.put(((ChangeableMembrane)m).getId(), mem.getId());
					else
						throw new IllegalArgumentException("Changeable Membranes are needed in otder to Map Ids");
				
				}
				
			}
			
			input = ((sps.input == null) ? null: cloneMap.get(sps.input));	// with this, we get the right id
			
			if(input != null)	
				environment.setInputSequence(sps.environment.getInputSequence());
			
			// Now we clone the dictionary
		
			dictionary = sps.dictionary;			// As the dictionary itself is static we can assign it safely.
			
			
			// Astrocyte Functions are static
			this.astroFunctions = sps.astroFunctions;
			
			// Now we clone the astrocyte: note: also direct assign is supposed to work at the current state
			
			Iterator<Astrocyte> itAstro = sps.astrocytes.values().iterator();
			
			while(itAstro.hasNext())
			{
				Astrocyte spsAstro = (Astrocyte) itAstro.next();
				
				if(spsAstro instanceof WangAstrocyte)
				{
					WangAstrocyte wSpsAstro = (WangAstrocyte) spsAstro;					
					Astrocyte thisAstro = new WangAstrocyte(wSpsAstro.getLabel(),this,wSpsAstro);
					addAstrocyte(thisAstro);
				}
				else if(spsAstro instanceof BinderAstrocyte)
				{
					BinderAstrocyte bSpsAstro = (BinderAstrocyte) spsAstro;					
					Astrocyte thisAstro = new BinderAstrocyte(bSpsAstro.getLabel(),this,bSpsAstro);
					addAstrocyte(thisAstro);					
				}
				
			}
			
			// the output membranes are calculated as the predecessors to the environment
			// the output list can be automatically created as the edges are built
			// but for efficiency reasons we build also an output set
			// additionally for the output membranes we must clone their spike trains also
			// so we define the necessary variables first
			
			HashMap<Integer, ArrayList<Short>> spsBinarySpikeTrain = sps.environment.getBinarySpikeTrain();
			HashMap<Integer, ArrayList<Short>> thisBinarySpikeTrain = this.environment.getBinarySpikeTrain();
			HashMap<Integer, ArrayList<Integer>> spsNaturalSpikeTrain = sps.environment.getNaturalSpikeTrain();
			HashMap<Integer, ArrayList<Integer>> thisNaturalSpikeTrain = this.environment.getNaturalSpikeTrain();
			
			Iterator<Integer> keys = sps.graph.keySet().iterator();
			
			while(keys.hasNext())
			{
				Integer key = keys.next();
				
				Iterator<Integer> values = sps.graph.get(key).iterator();
				
				while(values.hasNext())
				{
					Integer value = values.next();
					
					int spsSourceId, spsTargetId, thisSourceId, thisTargetId;
					
					spsSourceId 		= key;
					spsTargetId 		= value;
				
					thisSourceId 		= cloneMap.get(spsSourceId);
					thisTargetId 		= cloneMap.get(spsTargetId);
					
					// now we extract the ArcInfo object and we pass it through to the connect method
					
					ArcInfo spsArcInfo = sps.getArcInfo(spsSourceId, spsTargetId);
					
					connect(thisSourceId, thisTargetId, spsArcInfo, false, false);
					// we don't need to build the dictionary as it can be assigned safely
					// we don't need to initialize the spike train as it's going to be cloned
					
					SpikingMembrane thisTarget = this.getCellById(thisTargetId);
					
					// if target is the environment then source is an output membrane and we have to clone its spike trains
					if(thisTarget.getLabel().equals(environment.getLabel()))
					{
						ArrayList<Short> binarySpikeTrainArray = (ArrayList<Short>) spsBinarySpikeTrain.get(spsSourceId).clone();
						thisBinarySpikeTrain.put(thisSourceId, binarySpikeTrainArray);
						ArrayList<Integer> naturalSpikeTrainArray = (ArrayList<Integer>) spsNaturalSpikeTrain.get(spsSourceId).clone();
						thisNaturalSpikeTrain.put(thisSourceId, naturalSpikeTrainArray);
						
						// we add the membrane to the output set
						output.add(thisSourceId);
					
					}
					
					
				}
				
				
			}
								

			
		}
		else
			throw new IllegalArgumentException("The membrane structure must be kinda Spiking one");
		
		cloneMap = null;	// this line is not strictly necessary as memory will be cleaned up.
				
	}
	
	
	public ArcInfo getArcInfo(Integer sourceId, Integer targetId)
	{
		ArcInfo arcInfo = null;
		
		Pair<Integer,Integer> arc = new Pair<Integer,Integer>(sourceId,targetId);
		arcInfo = arcsInfo.get(arc);
		
		return arcInfo;
	}
	
	public Astrocyte getAstrocyte(String label)
	{
		Astrocyte ast = null;
		
		ast = astrocytes.get(label);
		
		return ast;
	}

	protected int getNextId()
	{
		
		return cellsById.size();
	}
	

	public String getEnvironmentLabel() {
		return environment.getLabel();
	}
	
	public SpikingEnvironment getEnvironmentMembrane()
	{
		return environment;
	}
	
	@Override
	public Object clone()
	{
		SpikingMembraneStructure clone = new SpikingMembraneStructure(this);
		return clone;
	}
	
	@Override
	public Collection<? extends Membrane> getAllMembranes() {
		// TODO Auto-generated method stub
		return new SpikingMembraneCollection();
	}
	
	
	

	public SpikingMembrane getCellById(int id)
	{
		return cellsById.get(id);
	}
	
	
	public List<SpikingMembrane> getCellsByLabel(String label)
	{
		return cellsByLabel.get(label);
	}
	

	protected boolean renewLabel(SpikingMembrane m, String Label, String newLabel)
	{
		
		List<SpikingMembrane> l = cellsByLabel.get(Label);
		
		int i = 0;
		boolean stop = false;
		while(i < l.size() && !stop)
		{
			SpikingMembrane mem = (SpikingMembrane) l.get(i);
			
			if(mem.getId() == m.getId())
				stop = true;
			else
				i++;
			
		}
		
		if(stop)
		{
			l.remove(i);
			
			if(l.isEmpty())
				cellsByLabel.remove(Label);
		
			if (!cellsByLabel.containsKey(newLabel))
			{
				l = new ArrayList<SpikingMembrane>();
				cellsByLabel.put(newLabel, l);
			}
			else
				l = cellsByLabel.get(newLabel);
			
			l.add(m);	// The membrane is always added to the list
			
			return true;
			
		}
		else
			return false;
	}
	
	
	private boolean secureAdd(SpikingMembrane arg0)
	{
		if (!cellsById.containsKey(arg0.getId()))
		{
			cellsById.put(arg0.getId(), arg0);
								
			String label = arg0.getLabel();
			
			List<SpikingMembrane>l;
			
			if (!cellsByLabel.containsKey(label))
			{
				l = new ArrayList<SpikingMembrane>();
				cellsByLabel.put(label, l);
			}
			else
				l = cellsByLabel.get(label);
			
			l.add(arg0);	// The membrane is always added to the list
			
			return true;
		}
		
		return false;
	}

	public boolean add(SpikingMembrane arg0)
	{
		// TODO Auto-generated method stub
		
		if(astrocytes.containsKey(arg0.getLabel()))
		{
			System.out.println("Adding membrane with a label corresponding to an existing astrocyte!!! - skipping");
			return false;
		}
		
		if (arg0.getLabel().equals(getEnvironmentLabel()))
			throw new IllegalArgumentException("Environment label");
		return secureAdd(arg0);
		
	}
	
	// This method is called from the clone method
	public boolean addAstrocyte(Astrocyte arg0)
	{
		if(cellsByLabel.containsKey(arg0.getLabel()))
		{
			System.out.println("Adding astrocyte with a label corresponding to an existing membrane!!! - skipping");
			return false;
		}
		

		if (!astrocytes.containsKey(arg0.getLabel()))
		{
			astrocytes.put(arg0.getLabel(), arg0);		
			return true;
		}
		
		return false;
		
	}
	

	// This method is called from the astrocyte class which is called from the parser
	public boolean addAstrocyte(Astrocyte arg0, List<Pair<String,String>> arcs, boolean ctrlArcs)
	{	
		if(cellsByLabel.containsKey(arg0.getLabel()))
		{
			System.out.println("Adding astrocyte with a label corresponding to an existing membrane!!! - skipping");
			return false;
		}
	
		
		Iterator<Pair<String,String>> itArcs = arcs.iterator();
		
		while(itArcs.hasNext())
		{
			Pair<String,String> arc = (Pair<String,String>) itArcs.next();
			addAstrocyte(arg0,arc,ctrlArcs);
		}
		
				
		return true;
		
	}
	
	
	public boolean addAstrocyte(Astrocyte arg0, Pair<String,String> plabel, boolean ctrlArc)
	{

		String labelSource = plabel.getFirst();
		String labelTarget = plabel.getSecond();
		
		List<SpikingMembrane> sources = this.getCellsByLabel(labelSource);
		List<SpikingMembrane> targets = this.getCellsByLabel(labelTarget);
		
		if (sources==null || sources.isEmpty())
			throw new IllegalArgumentException("There's no source membranes with the specified label");
		if (targets==null || targets.isEmpty())
			throw new IllegalArgumentException("There's no target membranes with the specified label");
		
		if (!astrocytes.containsKey(arg0.getLabel()))
		{
			astrocytes.put(arg0.getLabel(), arg0);
		}
		
		boolean result = true;
		
		Iterator<SpikingMembrane> its = sources.iterator();
		
		while(result && its.hasNext())
		{
			SpikingMembrane s = (SpikingMembrane) its.next();
			
			Iterator<SpikingMembrane> itt = targets.iterator();
			
			while(result && itt.hasNext())
			{
				SpikingMembrane t = (SpikingMembrane) itt.next();
		
				Pair<Integer,Integer> p = new Pair<Integer,Integer>(s.getId(),t.getId());
				
				if(!existsArc(p))
				{
					System.out.println("Can't associate an astrocyte to an arc that doesn't exists!!! - skipping");
				}
				else
				{
										
					ArcInfo arcInfo = arcsInfo.get(p);
					
					boolean canBeAssociated = ctrlArc || ((arcInfo != null) && ( (arcInfo.getAstrocyteList().isEmpty()) || (arcInfo.getAstType().equals(arg0.getType()))));
					
					if(!canBeAssociated)
					{
						System.out.println("Impossible to assign different kind of astrocytes to the arc!!! - skipping");
						
					}
					else
					{
					
						if(!ctrlArc && arcInfo.getAstrocyteList().isEmpty())
						{
							arcInfo.setAstType(arg0.getType());
							
						}
						
						String astLabel = arg0.getLabel();

						
						if(ctrlArc)
						{

							// We update both structures

							// First: arcsInfo, to inform that the arc is now controlling astrocyte arg0 behavior
						
							List<String> astCtrlList = arcInfo.getAstrocyteCtrlList();
							
							if(!astCtrlList.contains(astLabel))
								astCtrlList.add(astLabel);
							
							arcInfo.setAstrocyteCtrlList(astCtrlList);

							arcsInfo.put(p, arcInfo);
							
							// Second: astroToCtrlArcs, to inform that the astrocyte is controlled by the arc
							
							ArrayList<Pair<Integer,Integer>> arcsCtrlList = null;

							if(astroToCtrlArcs.containsKey(astLabel))
								arcsCtrlList = (ArrayList<Pair<Integer,Integer>>) astroToCtrlArcs.get(astLabel);
							else
								arcsCtrlList = new ArrayList<Pair<Integer,Integer>>();

							arcsCtrlList.add(p);
							astroToCtrlArcs.put(astLabel, arcsCtrlList);


						}
						else
						{

							// We update both structures

							// First: arcsInfo, to inform that the arc is now under astrocyte arg0 surveillance

							List<String> astList = arcInfo.getAstrocyteList();
							
							if(!astList.contains(astLabel))
								astList.add(astLabel);
							
							arcInfo.setAstrocyteList(astList);

							arcsInfo.put(p, arcInfo);

							// Second: astroToArcs, to inform that the astrocyte now controls traffic associated to the arc

							ArrayList<Pair<Integer,Integer>> arcsList = null;

							if(astroToArcs.containsKey(astLabel))
								arcsList = (ArrayList<Pair<Integer,Integer>>) astroToArcs.get(astLabel);
							else
								arcsList = new ArrayList<Pair<Integer,Integer>>();

							arcsList.add(p);
							astroToArcs.put(astLabel, arcsList);

							
						}

					}

				}

			}

		} 
		
		return true;
		
	}
	

	public boolean existsArc(Pair<Integer,Integer> p)
	{
		
		Integer sourceId = p.getFirst();
		Integer targetId = p.getSecond();
		
		if(!cellsById.containsKey(sourceId) || !cellsById.containsKey(targetId))
		{
			System.out.println("Adding astrocyte to a inexistentent arc!!!");
			return false;
		}
		
		Set<Integer> targetSet = (Set<Integer>) graph.get(sourceId);
		
		return targetSet.contains(targetId);
	}
	
	public boolean addAstrocyteFunction(String name, String body, int numParams)
	{
		boolean result = false;
		
		if (!astroFunctions.containsKey(name))
		{
			
			try
			{
				EvaluableFunction af = (EvaluableFunction) new AstrocyteFunction();
				af.storeFunction(new String(body), numParams);
				astroFunctions.put(name, af);
				result = true;
			
			}
			catch (Exception e)
			{
				result = false;
				e.printStackTrace();
			}
			
		}
		
		return result;
	}
	
	public AstrocyteFunction getAstrocyteFunction(String name)
	{
		AstrocyteFunction af = null;
		
		af = (AstrocyteFunction) astroFunctions.get(name);
		
		if(af == null)
			af = (AstrocyteFunction) astroFunctions.get("identity(x1)");
		
		return af;
	}
	
	public List<Pair<Integer,Integer>> getAstrocyteArcs(String astroLabel)
	{
		return astroToArcs.get(astroLabel);
	}
	
	public List<Pair<Integer,Integer>> getAstrocyteCtrlArcs(String astroLabel)
	{
		return astroToCtrlArcs.get(astroLabel);
	}

	
	public boolean flushAstrocytes()
	{
		System.out.println("Executing flush!!!!!");
		
		boolean result = true;
				
		Set<Pair<Integer,Integer>> setArcs = new HashSet<Pair<Integer,Integer>>();
		
		Iterator<Astrocyte> itAstro = astrocytes.values().iterator();
		
		
		while(itAstro.hasNext())
		{
			Astrocyte ast = (Astrocyte) itAstro.next();
			ast.flush();
			setArcs.addAll(ast.getArcs());
		}
		
		Iterator<Pair<Integer,Integer>> itpairs = setArcs.iterator();
		
		while(itpairs.hasNext())
		{
			Pair<Integer,Integer> p = (Pair<Integer,Integer>) itpairs.next();
			
			ArcInfo aInfo = arcsInfo.get(p);
			
			Long spikes = aInfo.getSpikesOutput();
			aInfo.setSpikesInput(0L);
			aInfo.setSpikesOutput(0L);
			
			boolean inhibited = aInfo.getInhibited();
			aInfo.setInhibited(false);

			if(!inhibited)
			{
				SpikingMembrane target = cellsById.get(p.getSecond());
				
				if(target.isOpen())
					//target.addSpikes(spikes);
					target.addSpikes(SpikingConstants.spikeSymbol,spikes);
			}
			
	
		}
		
		System.out.println(this.toString());

		return result;
		
	}
	
	
	public Long evalFunction(String function, List<Object> params)
	{
		Long result = 0L;
		
		
		try {
			result = (Long) this.getAstrocyteFunction(function).evaluateFunction(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return result;
	}
	
	public Long evalFunction(EvaluableFunction function, List<Object> params)
	{
		Long result = 0L;
		
		
		try {
			result = (Long) function.evaluateFunction(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return result;
	}
	
	public InfiniteMultiSet<String> getEnvironment()
	{
		return (InfiniteMultiSet<String>)environment.getMultiSet();
	}
	
	
	// this method is called from the clone() method.
	
	public boolean connect(Integer sourceId, Integer targetId, ArcInfo arcInfo, boolean updateDictionary, boolean initializeSpikeTrain)
	{
		SpikingMembrane source = this.getCellById(sourceId);
		SpikingMembrane target = this.getCellById(targetId);
		
		if (source==null)
			throw new IllegalArgumentException("The source membrane doesn't exist");
		if (target==null)
			throw new IllegalArgumentException("The target membrane doesn't exist");
		
		
		return connect(source,target, arcInfo, updateDictionary,initializeSpikeTrain);
	}
	
	// this method is called from the parser
	
	public boolean connect(String labelSource,String labelTarget)
	{
		return connect(labelSource,labelTarget,null,true,true);
	}
	

	
	public boolean connect(String labelSource,String labelTarget, ArcInfo arcInfo, boolean updateDictionary, boolean initializeSpikeTrain)
	{
		List<SpikingMembrane> sources = this.getCellsByLabel(labelSource);
		List<SpikingMembrane> targets = this.getCellsByLabel(labelTarget);
		if (sources==null || sources.isEmpty())
			throw new IllegalArgumentException("There's no source membranes with the specified label");
		if (targets==null || targets.isEmpty())
			throw new IllegalArgumentException("There's no target membranes with the specified label");
		
		boolean result = true;
		
		Iterator<SpikingMembrane> its = sources.iterator();
		
		while(result && its.hasNext())
		{
			SpikingMembrane s = (SpikingMembrane) its.next();
			
			Iterator<SpikingMembrane> itt = targets.iterator();
			
			while(result && itt.hasNext())
			{
				SpikingMembrane t = (SpikingMembrane) itt.next();
				
				ArcInfo checkedArcInfo = null;
				
				if(arcInfo == null)
					checkedArcInfo = new ArcInfo(s.getId(),t.getId());
				else
					checkedArcInfo = arcInfo;
				
				result = connect(s,t,checkedArcInfo, updateDictionary,initializeSpikeTrain);
			}
		}
		
		
		return result;
		
	}	
	
	public boolean connect(SpikingMembrane source, SpikingMembrane target, ArcInfo arcInfo, boolean updateDictionary, boolean initializeSpikeTrain)
	{
		boolean result = true;
		
		if(cellsById.containsKey(source.getId()) && cellsById.containsKey(target.getId()))
		{
		
				HashSet<Integer> set = null;
				
				if(graph.containsKey(source.getId()))
					set = (HashSet<Integer>) graph.get(source.getId());
				else
					set = new HashSet<Integer>();
				
				set.add(target.getId());
				graph.put(source.getId(), set);
				
				if(rgraph.containsKey(target.getId()))
					set = (HashSet<Integer>) rgraph.get(target.getId());
				else
					set = new HashSet<Integer>();
				
				set.add(source.getId());
				rgraph.put(target.getId(), set);
	
				// now we add the code for the attributes
				
				Integer sourceId = source.getId();
				Integer targetId = target.getId();
				
				Pair<Integer,Integer> newArc = new Pair<Integer,Integer>(sourceId,targetId);
				ArcInfo newArcInfo = new ArcInfo(source.getId(),target.getId(),arcInfo);
				
				arcsInfo.put(newArc, newArcInfo);
				
				// it's possible to have more than one arc associated to an astrocyte
				
				Iterator<String> itAstro = newArcInfo.getAstrocyteList().iterator();
				
				while(itAstro.hasNext())
				{
					String astLabel = (String) itAstro.next();
					
					Astrocyte ast = null;
					
					if(astLabel != null && !astLabel.equals(""))
						ast = astrocytes.get(astLabel); 		
					
					if(ast != null)
					{
											
						List<Pair<Integer,Integer>> arcsList = null;
						
						if(astroToArcs.containsKey(astLabel))
							arcsList = (List<Pair<Integer,Integer>>) astroToArcs.get(astLabel);
						else
							arcsList = new ArrayList<Pair<Integer,Integer>>();
						
						arcsList.add(newArc);
						astroToArcs.put(astLabel, arcsList);
						
					}				

				}
				
				// the same applies for the control arcs
				
				Iterator<String> itCtrlAstro = newArcInfo.getAstrocyteCtrlList().iterator();
				
				while(itCtrlAstro.hasNext())
				{
					String astLabel = (String) itCtrlAstro.next();
					
					Astrocyte ast = null;
					
					if(astLabel != null && !astLabel.equals(""))
						ast = astrocytes.get(astLabel); 		
					
					if(ast != null)
					{
											
						List<Pair<Integer,Integer>> arcsList = null;
						
						if(astroToCtrlArcs.containsKey(astLabel))
							arcsList = (List<Pair<Integer,Integer>>) astroToCtrlArcs.get(astLabel);
						else
							arcsList = new ArrayList<Pair<Integer,Integer>>();
						
						arcsList.add(newArc);
						astroToCtrlArcs.put(astLabel, arcsList);
						
					}				

				}
				
		}
		else
			result = false;
		
				
		// Now we update the dictionary but only when
		// - we are creating post sn p system creation synapses (budding / division) i. e. updateDictionary = true
		// - the connection went ok i. e. result = true
		// - we are not connecting to the environment as the environment has a special label not contained in the dictionary
		
		if(updateDictionary && result && !target.getLabel().equals(this.getEnvironmentLabel()))
		{
			String sourceLabel = source.getLabel();
			String targetLabel = target.getLabel();
			updateDictionary(sourceLabel,targetLabel);

		}
		
		// if we are connecting a membrane to the environment then we are setting an output membrane
		// so we have to mark it as output and initialize its spike trains
		
		if(result && target.getLabel().equals(this.getEnvironmentLabel()))
		{
			output.add(source.getId());
			
			if(initializeSpikeTrain)
				this.environment.initializeSpikeTrain(source);
		}
		
		return result;
	}
	
	public boolean connect(SpikingMembrane source, SpikingMembrane target, boolean updateDictionary, boolean initializeSpikeTrain)
	{
		
		ArcInfo arcInfo = new ArcInfo(source.getId(),target.getId());
		
		return connect(source,target,arcInfo,updateDictionary,initializeSpikeTrain);
		
	}
	
	
	public boolean updateDictionary(String sourceLabel, String targetLabel)
	{
		boolean result = true;
		
		HashSet<String> set = null;
		
		if(dictionary.containsKey(sourceLabel))
			set = (HashSet<String>) dictionary.get(sourceLabel);
		else
			set = new HashSet<String>();
		
		set.add(targetLabel);
		dictionary.put(sourceLabel, set);
			
		return result;
		
	}
	
	public boolean disconnect(SpikingMembrane source, SpikingMembrane target)
	{
		boolean result = true;
		
		if(cellsById.containsKey(source.getId()) && cellsById.containsKey(target.getId()))
		{
		
				HashSet<Integer> set = null;
				
				if(graph.containsKey(source.getId()))
					set = (HashSet<Integer>) graph.get(source.getId());
				else
					set = new HashSet<Integer>();
				
				if(set.contains(target.getId()))
					set.remove(target.getId());
				
				if(set.isEmpty())
					graph.remove(source.getId());
				else
					graph.put(source.getId(), set);
				
				
				if(rgraph.containsKey(target.getId()))
					set = (HashSet<Integer>) rgraph.get(target.getId());
				else
					set = new HashSet<Integer>();
				
				if(set.contains(source.getId()))
					set.remove(source.getId());
				
				if(set.isEmpty())
					rgraph.remove(target.getId());
				else
					rgraph.put(target.getId(), set);
				
				
				// now we add the code for the attributes
				
				Integer sourceId = source.getId();
				Integer targetId = target.getId();
				
				Pair<Integer,Integer> disposableArc = new Pair<Integer,Integer>(sourceId,targetId);	
				ArcInfo disposableArcInfo = arcsInfo.get(disposableArc);
				
				// astroToArcs
				
				Iterator<String> itAstro = disposableArcInfo.getAstrocyteList().iterator();
				
				while(itAstro.hasNext())
				{
					String astLabel = (String) itAstro.next();
					
					Astrocyte ast = null;
					
					if(astLabel != null && !astLabel.equals(""))
						ast = astrocytes.get(astLabel); 		
					
					if(ast != null)
					{
										
						List<Pair<Integer, Integer>> arcsList = null;
						
						if(astroToArcs.containsKey(astLabel))
							arcsList = (List<Pair<Integer,Integer>>) astroToArcs.get(astLabel);
						else
							arcsList = new ArrayList<Pair<Integer,Integer>>();			
						
						if(arcsList.contains(disposableArc))
							arcsList.remove(disposableArc);

						astroToArcs.put(astLabel, arcsList);
						
					}

				}
				
				// astroToCtrlArcs
				
				Iterator<String> itCtrlAstro = disposableArcInfo.getAstrocyteCtrlList().iterator();
				
				while(itCtrlAstro.hasNext())
				{
					String astLabel = (String) itCtrlAstro.next();
					
					Astrocyte ast = null;
					
					if(astLabel != null && !astLabel.equals(""))
						ast = astrocytes.get(astLabel); 		
					
					if(ast != null)
					{
										
						List<Pair<Integer, Integer>> arcsList = null;
						
						if(astroToCtrlArcs.containsKey(astLabel))
							arcsList = (List<Pair<Integer,Integer>>) astroToCtrlArcs.get(astLabel);
						else
							arcsList = new ArrayList<Pair<Integer,Integer>>();			
						
						if(arcsList.contains(disposableArc))
							arcsList.remove(disposableArc);

						astroToCtrlArcs.put(astLabel, arcsList);
						
					}

				}
								
				// and don't forget to delete the arcInfo
				
				arcsInfo.remove(disposableArcInfo);
				
		}
		else
			result = false;
		
		// if we are disconnecting a membrane from the environment we have to mark it as no output and clear its spike trains
		
		if(result && target.getLabel().equals(this.getEnvironmentLabel()))
		{
			output.remove(source.getId());
			this.environment.destroySpikeTrain(source);
		}
		
		return result;
	}
	
	public Map<String,Set<String>> getDictionary()
	{
		return dictionary;
		
	}
	
	public List<SpikingMembrane> getPredecessors(SpikingMembrane m)
	{

		List<SpikingMembrane> predecessors = new ArrayList<SpikingMembrane>();
		
		if(rgraph.containsKey(m.getId()))
		{
			Iterator<Integer>itedges = rgraph.get(m.getId()).iterator();
			
			while(itedges.hasNext())
			{
				Integer e = (Integer) itedges.next();
				
				SpikingMembrane s = this.getCellById(e);
		
				predecessors.add(s);			
			}
		}
		
				
		return predecessors;
	}
		
	
	public List<SpikingMembrane> getSuccessors(SpikingMembrane m)
	{

		
		List<SpikingMembrane> successors = new ArrayList<SpikingMembrane>();
		
		if(graph.containsKey(m.getId()))
		{
		
			Iterator<Integer>itedges = graph.get(m.getId()).iterator();
								
			while(itedges.hasNext())
			{
				Integer e = (Integer) itedges.next();
				
				SpikingMembrane s = this.getCellById(e);
				
				successors.add(s);			
			}
			
		}
		
		return successors;
	}
	
	public void setInputMembrane(String inputMembraneLabel,boolean check)
	{
		List<SpikingMembrane>l = this.getCellsByLabel(inputMembraneLabel);
		
		if (l==null || l.isEmpty())
			throw new IllegalArgumentException("The input membrane doesn't exist");
		setInputMembrane(l.get(0),check);
	}
		
	public void setInputMembrane(SpikingMembrane m, boolean check)
	{
		if(check)
			if(m == null || cellsById.containsKey(m.getId()) == false)
				throw new IllegalArgumentException("The membrane is not contained in the structure");
		
		this.input = m.getId();
		
		HashMap<Long,Long> inputSequence = new HashMap<Long,Long>();
		environment.setInputSequence(inputSequence);
				
	}
	
	public void setInputMembrane(SpikingMembrane m, HashMap<Long,Long> inputSequence, boolean check)
	{
		if(check)
			if(m == null || cellsById.containsKey(m.getId()) == false)
				throw new IllegalArgumentException("The membrane is not contained in the structure");
		
		this.input = m.getId();
		
		environment.setInputSequence(inputSequence);
		
		
	}
	
	public SpikingMembrane getInputMembrane()
	{
		return ((input == null) ? null : cellsById.get(input));
	}
		
	public void setOutputMembrane(String outputMembraneLabel,boolean check)
	{
		List<SpikingMembrane>l = this.getCellsByLabel(outputMembraneLabel);
		
		if (l==null || l.isEmpty())
			throw new IllegalArgumentException("The output membrane doesn't exist");
		
		
		setOutputMembranes(l,check);
		
	}
	
	public void setOutputMembranes(List<SpikingMembrane> o, boolean check)
	{
		if(check)
			if (o == null || cellsById.values().containsAll(o) == false)
				throw new IllegalArgumentException("The membranes are not contained in the structure");
			
		Iterator<SpikingMembrane> it = o.iterator();
		
		while(it.hasNext())
		{
			SpikingMembrane mem = (SpikingMembrane) it.next();
			connect(mem, environment,false, true);
			
			// we don't need to update the dictionary as we are setting output membranes
			// we have to initialize the spike train as we are setting output membranes
			
		}
						

	}
	
	public List<SpikingMembrane> getOutputMembranes()
	{
		return this.getPredecessors(environment);
	}
	
	public boolean isOutput(SpikingMembrane in)
	{
		boolean result = false;
		
		result = output.contains(in.getId());
		
		return result;
	}
	
	public boolean getShowBinarySequence()
	{
		return this.showBinarySequence;
	}
	
	public void setShowBinarySequence(boolean s)
	{
		this.showBinarySequence = s;
	}
	
	public List<Object> getShowNaturalSequence()
	{
		return this.showNaturalSequence;
	}
	
	public void setShowNaturalSequence(ArrayList<Object> s)
	{
		this.showNaturalSequence = s;
	}
	
	public boolean getShowSummatories()
	{
		return this.showSummatories;
	}
	
	public void setShowSummatories(boolean s)
	{
		this.showSummatories = s;
	}
	
	public int getSequentialMode()
	{
		return this.sequentialMode;
	}
	
	public void setSequentialMode(int i)
	{
		this.sequentialMode = i;
	}
	
	public int getAsynchMode()
	{
		return this.asynchMode;
	}
	
	public void setAsynchMode(int i)
	{
		this.asynchMode = i;
	}
	
	public Map<String, Long> getAsynchValidConfiguration()
	{
		return this.asynchValidConfiguration;
	}
	
	public void updateAsynchValidConfiguration(String label, long spikes)
	{
		this.asynchValidConfiguration.put(label, spikes);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String str="Cells: ";
		Iterator<? extends Membrane>it = getAllMembranes().iterator();
		while (it.hasNext())
		{
			str+=it.next().toString();
			if (it.hasNext())
				str+=", ";
		}
		
		str+='\n';
		
		str+="Arcs: ";
		
		str += '\n' + graph.toString();
				
		str+='\n';

		str+="Arcs Info: ";
		
		str += '\n' + arcsInfo.toString();
				
		str+='\n';
		
		str+="Dictionary: ";
		str+='\n';
		Iterator<? extends String>itdict = dictionary.keySet().iterator();
		while (itdict.hasNext())
		{
			String key = itdict.next().toString(); 
			str+=key + ":" + dictionary.get(key).toString();
			if (itdict.hasNext())
				str+='\n';
		}
		
		str+='\n';
		
		str+="Input Membrane: ";
		str+= input == null ? "" : getCellById(input).getId();
		
		str+='\n';
		
		str+="Ouput Membranes: ";
		
		Iterator<? extends SpikingMembrane>it3 = this.getOutputMembranes().iterator();
		while (it3.hasNext())
		{
			str+= ((SpikingMembrane) it3.next()).getId();
			if (it3.hasNext())
				str+=", ";
		}
		
		return str;
	}
	
	


	class SpikingMembraneCollection implements Collection<SpikingMembrane>
	{
		
		public SpikingMembraneCollection() {
			super();
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean add(SpikingMembrane arg0) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean addAll(Collection<? extends SpikingMembrane> arg0) {
			throw new UnsupportedOperationException();
		}

		
		@Override
		public void clear() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}


		@Override
		public boolean contains(Object arg0) {
			// TODO Auto-generated method stub
			if (!(arg0 instanceof SpikingMembrane))
				return false;
			SpikingMembrane tlm = (SpikingMembrane)arg0;
			
			return cellsById.containsKey(tlm.getId());
		}

		public List<SpikingMembrane> getByLabel(Object arg0) {
			// TODO Auto-generated method stub
			if (!(arg0 instanceof String))
				return null;
			
			String label = (String)arg0;
			
			return cellsByLabel.get(label);
			
		}
		
		public SpikingMembrane getById(Object arg0) {
			// TODO Auto-generated method stub
			if (!(arg0 instanceof Integer))
				return null;
			
			Integer id = ((Integer)arg0).intValue();
			
			return cellsById.get(id);
			
		}

		
		@Override
		public boolean containsAll(Collection<?> arg0) {
			// TODO Auto-generated method stub
			Iterator<?>it = arg0.iterator();
			boolean contains=true;
			while(contains && it.hasNext())
				contains=contains(it.next());
			return contains;
		}


		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return cellsById.isEmpty();
		}



		@Override
		public Iterator<SpikingMembrane> iterator() {
			// TODO Auto-generated method stub
			return cellsById.values().iterator();
		}

		
		

		@Override
		public boolean remove(Object arg0) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}



		@Override
		public boolean removeAll(Collection<?> arg0) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}



		@Override
		public boolean retainAll(Collection<?> arg0) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}



		@Override
		public int size() {
			// TODO Auto-generated method stub
			return cellsById.size();
		}



		@Override
		public Object[] toArray() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
			
		}



		@Override
		public <T> T[] toArray(T[] arg0) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}


	}

	@Override
	public Membrane getMembrane(int id) {
		// TODO Auto-generated method stub
		return cellsById.get(id);
	}
   
	

}
