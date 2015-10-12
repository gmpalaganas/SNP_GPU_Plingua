package org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba;

import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;


public abstract class SkeletonHandRule {
	
	private byte mainMembraneCharge;
	private MultiSet<String> mainMultiSet;
	private MultiSet<String> parentMultiSet;
	public SkeletonHandRule()
	{
		
	}
	public SkeletonHandRule(byte mainMembraneCharge,
			MultiSet<String> mainMultiSet, MultiSet<String> parentMultiSet) {
		super();
		this.mainMembraneCharge = mainMembraneCharge;
		this.mainMultiSet = mainMultiSet;
		this.parentMultiSet = parentMultiSet;
	}
	public byte getMainMembraneCharge() {
		return mainMembraneCharge;
	}
	public MultiSet<String> getMainMultiSet() {
		return mainMultiSet;
	}
	public MultiSet<String> getParentMultiSet() {
		return parentMultiSet;
	}

	
	public abstract void set(IRule r);
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mainMembraneCharge;
		result = prime * result
				+ ((mainMultiSet == null) ? 0 : mainMultiSet.hashCode());
		result = prime * result
				+ ((parentMultiSet == null) ? 0 : parentMultiSet.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SkeletonHandRule other = (SkeletonHandRule) obj;
		if (mainMembraneCharge != other.mainMembraneCharge)
			return false;
		if (mainMultiSet == null) {
			if (other.mainMultiSet != null)
				return false;
		} else if (!mainMultiSet.equals(other.mainMultiSet))
			return false;
		if (parentMultiSet == null) {
			if (other.parentMultiSet != null)
				return false;
		} else if (!parentMultiSet.equals(other.parentMultiSet))
			return false;
		return true;
	}
	public void setMainMembraneCharge(byte mainMembraneCharge) {
		this.mainMembraneCharge = mainMembraneCharge;
	}
	public void setMainMultiSet(MultiSet<String> mainMultiSet) {
		this.mainMultiSet = mainMultiSet;
	}
	public void setParentMultiSet(MultiSet<String> parentMultiSet) {
		this.parentMultiSet = parentMultiSet;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		
		String str="";
		if (!parentMultiSet.isEmpty())
			str+= parentMultiSet.toString();
		if (mainMembraneCharge!=0)
			str+=Membrane.getChargeSymbol(mainMembraneCharge);
		str+="[";
		if (!mainMultiSet.isEmpty())
			str+=mainMultiSet.toString();
		str+="]";
		return str;
	}
	

	

}
