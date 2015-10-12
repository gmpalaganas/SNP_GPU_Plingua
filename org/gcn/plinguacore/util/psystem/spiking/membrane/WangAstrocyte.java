package org.gcn.plinguacore.util.psystem.spiking.membrane;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.gcn.plinguacore.util.Pair;
import org.gcn.plinguacore.util.RandomNumbersGenerator;

public class WangAstrocyte extends Astrocyte {

	public WangAstrocyte(String label, List<Pair<String, String>> arcs, Long potential, SpikingMembraneStructure structure) {
		super(label, arcs, new TreeSet<Long>(), new ArrayList<String>(), potential, "wang", structure);
		// TODO Auto-generated constructor stub
	}
	
	
	public WangAstrocyte(String label,
			SpikingMembraneStructure structure,
			WangAstrocyte a) {
		// TODO Auto-generated constructor stub
		super(label,structure,a);
	}


	public boolean flush()
	{
		
		long spikes = getSpikes().longValue();
		this.clearSpikes();		
		
		long threshold = getPotential().longValue();
		
		boolean inhibitTraffic = false;

		if(spikes < threshold)
		{
			inhibitTraffic = false;
		}
		else if(spikes > threshold)
		{
			inhibitTraffic = true;
		}
		else if(spikes == threshold)
		{
			RandomNumbersGenerator rgenerator = RandomNumbersGenerator
			.getInstance();

			int randomInt = rgenerator.nextInt(2);
			
			boolean inhibitory = (randomInt == 0);
			
			System.out.println("Random Number for Wang Astrocyte: " + randomInt);

			if(inhibitory)
			{
				inhibitTraffic = true;
				
			}
			else
			{
				inhibitTraffic = false;
			}
		}
		
		
		return updateArcs(inhibitTraffic);
		
		
	}
	
	boolean updateArcs(boolean inhibitTraffic)
	{
		boolean result = true;
		
		List<Pair<Integer,Integer>> lArcs = getArcs();
		
		SpikingMembraneStructure structure = getStructure();
		
		Iterator<Pair<Integer,Integer>> it = lArcs.iterator();
		
		while(it.hasNext())
		{
			Pair<Integer,Integer> p = (Pair<Integer,Integer>) it.next();
			ArcInfo aInfo = structure.getArcInfo(p.getFirst(), p.getSecond());
			boolean inhibitArc = aInfo.getInhibited() || inhibitTraffic;
			
			if(inhibitArc)
			{
				aInfo.setInhibited(true);
				aInfo.setSpikesOutput(0L);
			}
			else
			{
				aInfo.setInhibited(false);
				//aInfo.setSpikesOutput(aInfo.getSpikesInput());
				aInfo.setSpikesOutput(aInfo.getSpikesOutput() + aInfo.getSpikesInput());
			}
			
		}
		
		return result;
	}

}
