package org.gcn.plinguacore.simulator.cellLike.probabilistic;

import java.util.ArrayList;
import java.util.List;


import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;

class DndProbabilisticSimulatorData {

	private int id;
	private CellLikeMembrane membrane,tempMembrane;
	private List<IRule> rules;
	private int max;
	private boolean b; 
	
	DndProbabilisticSimulatorData(int id)
	{
		this.id=id;
		rules = new ArrayList<IRule>();
		max=0;
	}

	

	protected final int getId() {
		return id;
	}

	


	protected final int getMax() {
		return max;
	}



	protected final void setMax(int max) {
		this.max = max;
	}



	protected final List<IRule> getRules() {
		return rules;
	}



	protected final boolean getBoolean() {
		return b;
	}



	protected final void setBoolean(boolean b) {
		this.b = b;
	}



	@Override
	public String toString() {
		return "DndProbabilisticSimulatorData [id=" + id + ", max=" + max
				+ ", rules=" + rules + "]";
	}



	protected final CellLikeMembrane getMembrane() {
		return membrane;
	}



	protected final void setMembrane(CellLikeMembrane membrane) {
		this.membrane = membrane;
	}



	protected final CellLikeMembrane getTempMembrane() {
		return tempMembrane;
	}



	protected final void setTempMembrane(CellLikeMembrane tempMembrane) {
		this.tempMembrane = tempMembrane;
	}



	
	

}
