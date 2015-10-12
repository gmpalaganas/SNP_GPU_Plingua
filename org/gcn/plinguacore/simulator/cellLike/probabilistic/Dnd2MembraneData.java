package org.gcn.plinguacore.simulator.cellLike.probabilistic;

import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;


class Dnd2MembraneData {

	private int membraneId;
	private CellLikeMembrane membraneFromCt,membraneFromCpt;
	private boolean b; 
	
	Dnd2MembraneData(int membraneId)
	{
		this.membraneId=membraneId;
	}

	public int getMembraneId() {
		return membraneId;
	}

	public CellLikeMembrane getMembraneFromCt() {
		return membraneFromCt;
	}

	public void setMembraneFromCt(CellLikeMembrane membraneFromCt) {
		this.membraneFromCt = membraneFromCt;
	}

	public CellLikeMembrane getMembraneFromCpt() {
		return membraneFromCpt;
	}

	public void setMembraneFromCpt(CellLikeMembrane membraneFromCpt) {
		this.membraneFromCpt = membraneFromCpt;
	}

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}


	


	
	

}
