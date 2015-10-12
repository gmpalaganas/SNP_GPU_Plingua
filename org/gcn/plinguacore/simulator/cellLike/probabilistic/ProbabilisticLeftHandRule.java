package org.gcn.plinguacore.simulator.cellLike.probabilistic;

import java.util.Iterator;

import org.gcn.plinguacore.util.HashMultiSet;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeNoSkinMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.InnerRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.LeftHandRule;
import org.gcn.plinguacore.util.psystem.rule.RightHandRule;

public class ProbabilisticLeftHandRule {
	
	private int mainMembraneId;
	private byte mainMembraneLeftCharge;
	private byte mainMembraneRightCharge;
	private String mainMembraneLabel;
	private String mainMembraneEnvironment;
	private MultiSet<String> mainMembraneMultiSet;
	private MultiSet<String> parentMembraneMultiSet;
	
	
	public ProbabilisticLeftHandRule(IRule rule,CellLikeMembrane membrane)
	{
		LeftHandRule lhr = rule.getLeftHandRule();
		RightHandRule rhr = rule.getRightHandRule();
		
		if (lhr.getOuterRuleMembrane().getInnerRuleMembranes().isEmpty())
		{
			mainMembraneId = membrane.getId();
			mainMembraneLeftCharge = lhr.getOuterRuleMembrane().getCharge();
			mainMembraneRightCharge = rhr.getOuterRuleMembrane().getCharge();
			mainMembraneLabel = lhr.getOuterRuleMembrane().getLabel();
			mainMembraneEnvironment=membrane.getLabelObj().getEnvironmentID();
			mainMembraneMultiSet = lhr.getOuterRuleMembrane().getMultiSet();
			parentMembraneMultiSet = lhr.getMultiSet();
		}
		else
		{
			
			boolean enc=false;
					
			Iterator<InnerRuleMembrane>it=lhr.getOuterRuleMembrane().getInnerRuleMembranes().iterator();
			
			while(it.hasNext() && !enc)
			{
				InnerRuleMembrane irm = it.next();
				if (!irm.getMultiSet().isEmpty())
				{
					mainMembraneLeftCharge=irm.getCharge();
					mainMembraneRightCharge=irm.getCharge();
					mainMembraneLabel=irm.getLabel();
					mainMembraneMultiSet=irm.getMultiSet();
					enc=true;
				}
				
			}
			
			if (!enc)
				throw new IllegalArgumentException("Rule with empty inner membranes");
			
			
			
			Iterator<CellLikeNoSkinMembrane>it1=membrane.getChildMembranes().iterator();
			enc=false;
			
			while(it1.hasNext() && !enc)
			{
				CellLikeNoSkinMembrane m = it1.next();
				if (m.getLabel().equals(mainMembraneLabel))
				{
					mainMembraneId = m.getId();
					mainMembraneEnvironment = m.getLabelObj().getEnvironmentID();
					enc=true;
				}
			}
			
			if (!enc)
				throw new IllegalArgumentException("The active membrane label cannot be found");
			
			
			
			parentMembraneMultiSet = new HashMultiSet<String>();
		}
		
	}
	
	public long countExecutions(Membrane membrane)
	{
		if (!(membrane instanceof CellLikeMembrane))
			return 0;
		CellLikeMembrane m = (CellLikeMembrane)membrane;
		byte blockCharge = getMainMembraneCharge();
		byte currentCharge = m.getCharge();
		if(blockCharge!=currentCharge) return 0;
		long subSetsMainMembrane = m.getMultiSet().countSubSets(getMainMembraneMultiSet());
		if (m.isSkinMembrane())
			return subSetsMainMembrane;
		long subSetsParentMembrane=((CellLikeNoSkinMembrane)m).getParentMembrane().getMultiSet().countSubSets(getParentMembraneMultiSet());
		return Math.min(subSetsMainMembrane, subSetsParentMembrane);
		
	}
	
	public boolean isApplicable1(Membrane membrane)
	{
		return countExecutions(membrane)>0;
	}
	
	

	public int getMainMembraneId() {
		return mainMembraneId;
	}

	public byte getMainMembraneCharge() {
		return mainMembraneLeftCharge;
	}

	public String getMainMembraneLabel() {
		return mainMembraneLabel;
	}

	public String getMainMembraneEnvironment() {
		return mainMembraneEnvironment;
	}

	public MultiSet<String> getMainMembraneMultiSet() {
		return mainMembraneMultiSet;
	}

	
	public MultiSet<String> getParentMembraneMultiSet() {
		return parentMembraneMultiSet;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((mainMembraneEnvironment == null) ? 0
						: mainMembraneEnvironment.hashCode());
		result = prime * result + mainMembraneId;
		result = prime
				* result
				+ ((mainMembraneLabel == null) ? 0 : mainMembraneLabel
						.hashCode());
		result = prime * result + mainMembraneLeftCharge;
		result = prime
				* result
				+ ((mainMembraneMultiSet == null) ? 0 : mainMembraneMultiSet
						.hashCode());
		result = prime * result + mainMembraneRightCharge;
		result = prime
				* result
				+ ((parentMembraneMultiSet == null) ? 0
						: parentMembraneMultiSet.hashCode());
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
		ProbabilisticLeftHandRule other = (ProbabilisticLeftHandRule) obj;
		if (mainMembraneEnvironment == null) {
			if (other.mainMembraneEnvironment != null)
				return false;
		} else if (!mainMembraneEnvironment
				.equals(other.mainMembraneEnvironment))
			return false;
		if (mainMembraneId != other.mainMembraneId)
			return false;
		if (mainMembraneLabel == null) {
			if (other.mainMembraneLabel != null)
				return false;
		} else if (!mainMembraneLabel.equals(other.mainMembraneLabel))
			return false;
		if (mainMembraneLeftCharge != other.mainMembraneLeftCharge)
			return false;
		if (mainMembraneMultiSet == null) {
			if (other.mainMembraneMultiSet != null)
				return false;
		} else if (!mainMembraneMultiSet.equals(other.mainMembraneMultiSet))
			return false;
		if (mainMembraneRightCharge != other.mainMembraneRightCharge)
			return false;
		if (parentMembraneMultiSet == null) {
			if (other.parentMembraneMultiSet != null)
				return false;
		} else if (!parentMembraneMultiSet.equals(other.parentMembraneMultiSet))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String str="";
		if (!parentMembraneMultiSet.isEmpty())
			str+=parentMembraneMultiSet+" ";
		if (mainMembraneLeftCharge!=0)
			str+=Membrane.getChargeSymbol(mainMembraneLeftCharge);
		str+="[";
		str+=mainMembraneMultiSet;
		str+="]'"+mainMembraneLabel;
		if (!mainMembraneEnvironment.equals(""))
			str+=mainMembraneEnvironment;
		str+=" (ID = "+mainMembraneId+")";
		return str;
	}

	
	
	

}
