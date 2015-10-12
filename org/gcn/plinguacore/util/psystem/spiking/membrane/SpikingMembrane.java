package org.gcn.plinguacore.util.psystem.spiking.membrane;


import org.gcn.plinguacore.util.HashMultiSet;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.Label;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.rule.spiking.SpikingRule;
import org.gcn.plinguacore.util.psystem.spiking.SpikingConstants;



public class SpikingMembrane extends ChangeableMembrane  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -991479572199736842L;
	private long stepsToOpen = 0L;
	private SpikingRule selectedRule = null;
	private SpikingMembraneStructure structure = null;
		
	//private boolean isOutput = false;
	private boolean buddingdivision = false;

	private String object = SpikingConstants.spikeSymbol;
	
	// The next attributes are considered only for efficiency reasons
	private boolean efficiencyAttributesProcessed = false;
	private String membraneSpikingString = null;
	private long membraneSpikingStringSize = 0;


	
	
	// Improvement: Add clone() method so one can use the simulation algorithms safely;
	
	public static SpikingMembrane buildMembrane(String label, long elements, SpikingMembraneStructure structure, boolean computeEfficiencyAttributes)
	{
		String object = SpikingConstants.spikeSymbol;
		
		SpikingMembrane result = new SpikingMembrane(label, object, elements, structure, computeEfficiencyAttributes);
		
		structure.add(result);
		
		return result;
	}
	
		
	// protected SpikingMembrane(String label, long elements, SpikingMembraneStructure structure, boolean computeEfficiencyAttributes)
	protected SpikingMembrane(String label, String object, long elements, SpikingMembraneStructure structure, boolean computeEfficiencyAttributes)
	{
		super(new Label(label), (byte)0, new HashMultiSet<String>());
		
		
		if(elements < 0)
			// throw new IllegalArgumentException("Spiking Membranes must content a non-negative amount of spikes");
			throw new IllegalArgumentException("Spiking Membranes must contain a non-negative amount of spikes or antispikes");
	
		if((object.equals(SpikingConstants.spikeSymbol) || object.equals(SpikingConstants.antiSpikeSymbol)) == false)
			throw new IllegalArgumentException("Spiking Membranes must contain only spikes or antispikes");
		
		getMultiSet().add(object, elements);
		this.object = object;
		
		if(computeEfficiencyAttributes)
		{
			membraneSpikingStringSize = elements;
			membraneSpikingString = this.getMembraneSpikingString();
			efficiencyAttributesProcessed = true;
		}
	
		initMembraneStructure(structure);
	}
	
	
	protected SpikingMembrane(String label,MultiSet<String> multiSet,SpikingMembraneStructure structure, boolean computeEfficiencyAttributes) {
		
		super(new Label(label), (byte)0, multiSet);
		
		updateObject();
		
		if(computeEfficiencyAttributes)
		{
			//membraneSpikingStringSize = multiSet.count(SpikingConstants.spikeSymbol);
			membraneSpikingStringSize = this.getObjectCount();
			membraneSpikingString = this.getMembraneSpikingString();
			efficiencyAttributesProcessed = true;
		}

		
		initMembraneStructure(structure);
		
	}

	
	protected SpikingMembrane(Membrane membrane,SpikingMembraneStructure structure)
	{
		//this(membrane.getLabel(), membrane.getMultiSet().count(SpikingConstants.spikeSymbol), structure, false);
		this(membrane.getLabel(), getObject(membrane), getObjectCount(membrane),structure, false);
		
		this.setCharge(membrane.getCharge());
	
		if (membrane instanceof CellLikeMembrane)
		{
			CellLikeMembrane clm=(CellLikeMembrane)membrane;
			if (!clm.getChildMembranes().isEmpty())
				throw new IllegalArgumentException("Spiking membranes must be elemental membranes");
		}
		//getMultiSet().addAll(membrane.getMultiSet()); --> Calling in constructor in order to show the cloning
	
		if (membrane instanceof SpikingMembrane)
		{
		
			this.selectedRule = ((SpikingMembrane)membrane).selectedRule;
			this.stepsToOpen = ((SpikingMembrane)membrane).stepsToOpen;
//			this.isOutput = ((SpikingMembrane)membrane).isOutput;
			this.buddingdivision = ((SpikingMembrane)membrane).buddingdivision;
			
			efficiencyAttributesProcessed = ((SpikingMembrane)membrane).efficiencyAttributesProcessed;
			
			if(efficiencyAttributesProcessed)
			{
				membraneSpikingString = new String(((SpikingMembrane)membrane).membraneSpikingString);
				membraneSpikingStringSize = ((SpikingMembrane)membrane).membraneSpikingStringSize;
			}
		
		}
		
	}
	
	
//public boolean isOutput()
//{
//	return (isOutput == true);
//}
	
//public void setOutput(boolean output)
//{
//	isOutput = true;
//}

public boolean isOpen()
{
	
	return !isClosed();
	
}

public boolean isClosed()
{
	
	return stepsToOpen > 0L || buddingdivision;

}

public long getStepsToOpen()
{

	return stepsToOpen;

}

public boolean getBuddingDivision()
{

	return buddingdivision;

}

public SpikingRule getSelectedRule()
{

	return selectedRule ;

}

public boolean decreaseStepsToOpen()
{

	boolean result = isClosed();
	
	if(result)
		stepsToOpen --;
	else
		; // do nothing
	
	return result; // return true iif the decreasing is performed
	
}

public void setMembraneClosedToSpike(long stepsToOpen, SpikingRule selectedRule)
{

	if(stepsToOpen < 0)
		throw new IllegalArgumentException("Steps must be non-negative");
	
	if(selectedRule == null && stepsToOpen > 0)
		throw new IllegalArgumentException("A Closed Spiking Membrane can't have no Selected Rule");
	
	if(selectedRule != null && selectedRule.getDelay() < stepsToOpen)
		throw new IllegalArgumentException("A closed Spiking Membrane can't have a Delayed Rule with less delay time than the number of steps to open");

	if(selectedRule != null && selectedRule.isFiringRule() && !selectedRule.getLeftHandRule().getOuterRuleMembrane().getLabel().equals(this.getLabel()))
		throw new IllegalArgumentException("A Firing Selected Rule must be contained in the firing rules set");
	else if(selectedRule != null && selectedRule.isForgettingRule() && !selectedRule.getLeftHandRule().getOuterRuleMembrane().getLabel().equals(this.getLabel()))
		throw new IllegalArgumentException("A Forgetting Selected Rule must be contained in the firing rules set");

	
	this.selectedRule = selectedRule;
	this.stepsToOpen = stepsToOpen;
	

}

public void setMembraneClosedToBuddingOrDivision(SpikingRule selectedRule)
{

	if(selectedRule != null && selectedRule.isBuddingRule() && !selectedRule.getLeftHandRule().getOuterRuleMembrane().getLabel().equals(this.getLabel()))
		throw new IllegalArgumentException("A Budding Selected Rule must be contained in the firing rules set");
	else if(selectedRule != null && selectedRule.isDivisionRule() && !selectedRule.getLeftHandRule().getOuterRuleMembrane().getLabel().equals(this.getLabel()))
		throw new IllegalArgumentException("A Division Selected Rule must be contained in the firing rules set");

	
	this.selectedRule = selectedRule;
	
	this.buddingdivision = true;

}

// this is used to close the child membranes

public void setMembraneClosedToBuddingOrDivision()
{
	
	this.buddingdivision = true;

}

public void setSelectedRule(SpikingRule selectedRule)
{
	
	if(selectedRule != null && selectedRule.isBuddingRule() || selectedRule.isDivisionRule())
		setMembraneClosedToBuddingOrDivision(selectedRule);
	else
		setMembraneClosedToSpike(selectedRule.getDelay(), selectedRule);
}


public void setMembraneOpen()
{

	this.stepsToOpen = 0L;
	this.selectedRule = null;
	this.buddingdivision = false;
	
	
}




public SpikingMembraneStructure getStructure()
{

	return structure;

}

public String getMembraneSpikingString()
{
	
		if(efficiencyAttributesProcessed)
			return membraneSpikingString;
			
	
		String ss = new String("");

		//long max = getMultiSet().count(SpikingConstants.spikeSymbol);
		long max = getObjectCount();
		
			
		for(long i = 0; i < max; i++)
		{
			// ss = ss + SpikingConstants.spikeSymbol;
			ss = ss + getObject();
		}
		
		return ss;
	

	
}

public long getMembraneSpikingStringSize()
{
	if(efficiencyAttributesProcessed)
		return membraneSpikingStringSize;
	
	// return this.getMultiSet().count(SpikingConstants.spikeSymbol); 
	return this.getObjectCount();
	
	
}

private void initMembraneStructure(SpikingMembraneStructure structure)
{
	if (structure==null)
		throw new NullPointerException("Null membrane structure");
	setId(structure.getNextId());
	this.structure=structure;
}

public void addSpikes(String object, long spikes)
{
	// add SpikingHashInfiniteMultiSet and SpikingHashMultiSet to the package
	
	double valueThis = getObjectCount();
	double signThis = getObject().equals(SpikingConstants.spikeSymbol) ? 1 : -1;
	
	double valueOther = spikes;
	double signOther = object.equals(SpikingConstants.spikeSymbol) ? 1 : -1;
	
	double result = signThis * valueThis + signOther * valueOther;
	
	long signThisNew = result >= 0 ? 1 : -1; 
	long valueThisNew = (long) (signThisNew * result);
	
	String elementThisNew = signThisNew > 0 ? SpikingConstants.spikeSymbol : SpikingConstants.antiSpikeSymbol;
	
	// this.getMultiSet().add(SpikingConstants.spikeSymbol, spikes);

	this.getMultiSet().clear();
	this.getMultiSet().add(elementThisNew, valueThisNew);
	this.object = elementThisNew;
	
	if(efficiencyAttributesProcessed)
	{
		// membraneSpikingStringSize += spikes;
		
		membraneSpikingStringSize = valueThisNew;
		
		membraneSpikingString = "";
		
		//for(int i = 0; i < spikes;i++)
		for(int i = 0; i < valueThisNew;i++)
			// membraneSpikingString = membraneSpikingString + SpikingConstants.spikeSymbol;
			membraneSpikingString = membraneSpikingString + elementThisNew;
	}
	
}


public void removeSpikes(long spikes)
{
	//this.getMultiSet().remove(SpikingConstants.spikeSymbol, spikes);
	
	this.getMultiSet().remove(getObject(), spikes);
	
	if(this.getMultiSet().size() == 0)
		object = SpikingConstants.spikeSymbol;
	
	if(efficiencyAttributesProcessed)
	{
		membraneSpikingStringSize -= spikes;
		//membraneSpikingString = membraneSpikingString.substring(0, (int) (membraneSpikingString.length() - spikes));
		membraneSpikingString = membraneSpikingString.substring(0, (int) (membraneSpikingString.length() - spikes)*getObject().length());
		
	}


}

public void renewLabel(String newLabel)
{
	
	String oldLabel = this.getLabel();
	
	super.label = new Label(newLabel);
	
	if(structure != null)
		structure.renewLabel(this, oldLabel, newLabel);
	
}



public void updateObject()
{
	
	if(multiSet.count(SpikingConstants.antiSpikeSymbol) > 0)
		object = SpikingConstants.antiSpikeSymbol;
	else
		object = SpikingConstants.spikeSymbol;
	
}

public String getObject()
{
	return object;
}

public long getObjectCount()
{
	return multiSet.count(object);
}

public static String getObject(Membrane m)
{
	String result = SpikingConstants.spikeSymbol;
	
	if(m instanceof SpikingMembrane)
		result = m.getMultiSet().count(SpikingConstants.antiSpikeSymbol) > 0 ? SpikingConstants.antiSpikeSymbol : SpikingConstants.spikeSymbol; 
	
	return result;
}

public static long getObjectCount(Membrane m)
{
	String object = getObject(m);	
	long result = m.getMultiSet().count(object);
		
	return result;
}

@Override
public String toString()  {
	
	return this.getId() + ":" + super.toString(); 
}

@Override
public void dissolve() throws UnsupportedOperationException {
	throw new UnsupportedOperationException();

}
@Override
public ChangeableMembrane divide() throws UnsupportedOperationException {
	// TODO Auto-generated method stub
	if (getLabel().equals(structure.getEnvironmentLabel()))
		throw new UnsupportedOperationException("The environment cannot be divided");
	SpikingMembrane mem= new SpikingMembrane(this,structure);
	structure.add(mem);
	return mem;

}

public ChangeableMembrane doBuddy() throws UnsupportedOperationException {
	// TODO Auto-generated method stub
	if (getLabel().equals(structure.getEnvironmentLabel()))
		throw new UnsupportedOperationException("The environment cannot be budded");
	SpikingMembrane mem= new SpikingMembrane(this,structure);
	structure.add(mem);
	return mem;

}

}
