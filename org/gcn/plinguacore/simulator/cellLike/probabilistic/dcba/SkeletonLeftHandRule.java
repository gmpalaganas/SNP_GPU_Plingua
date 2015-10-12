package org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba;

import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.rule.IRule;

public class SkeletonLeftHandRule extends SkeletonHandRule implements Cloneable{
	
	
	private String mainMembraneLabel;
	
	public SkeletonLeftHandRule()
	{
		super();
	}
	
	public SkeletonLeftHandRule(IRule r)
	{
		this(r.getLeftHandRule().getOuterRuleMembrane().getLabel(),
				r.getLeftHandRule().getOuterRuleMembrane().getCharge(),
				r.getLeftHandRule().getOuterRuleMembrane().getMultiSet(),
				r.getLeftHandRule().getMultiSet());
		
	}

	public SkeletonLeftHandRule(String mainMembraneLabel, byte mainMembraneCharge,
			MultiSet<String> mainMultiSet, MultiSet<String> parentMultiSet) {
		super(mainMembraneCharge, mainMultiSet, parentMultiSet);
		this.mainMembraneLabel=mainMembraneLabel;
		// TODO Auto-generated constructor stub
	}

	public String getMainMembraneLabel() {
		return mainMembraneLabel;
	}
	
	
	

	public void setMainMembraneLabel(String mainMembraneLabel) {
		this.mainMembraneLabel = mainMembraneLabel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((mainMembraneLabel == null) ? 0 : mainMembraneLabel
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SkeletonLeftHandRule other = (SkeletonLeftHandRule) obj;
		if (mainMembraneLabel == null) {
			if (other.mainMembraneLabel != null)
				return false;
		} else if (!mainMembraneLabel.equals(other.mainMembraneLabel))
			return false;
		return true;
	}

	@Override
	protected Object clone() {
		// TODO Auto-generated method stub
		return new SkeletonLeftHandRule(this.getMainMembraneLabel(),this.getMainMembraneCharge(),this.getMainMultiSet(),this.getParentMultiSet());
	}
	@Override
	public void set(IRule r)
	{
		setMainMembraneLabel(r.getLeftHandRule().getOuterRuleMembrane().getLabel());
		setMainMembraneCharge(r.getLeftHandRule().getOuterRuleMembrane().getCharge());
		setMainMultiSet(r.getLeftHandRule().getOuterRuleMembrane().getMultiSet());
		setParentMultiSet(r.getLeftHandRule().getMultiSet());
		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString()+"'"+mainMembraneLabel;
	}
	
	
	

	
	
	

}
