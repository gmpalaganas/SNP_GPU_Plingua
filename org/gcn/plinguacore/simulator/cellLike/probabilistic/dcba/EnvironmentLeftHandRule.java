package org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba;

import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.InnerRuleMembrane;

public class EnvironmentLeftHandRule implements Cloneable{
	
	private String object;
	private String environment;
	
	public EnvironmentLeftHandRule()
	{
		
	}
	
	public EnvironmentLeftHandRule(String object,String environment)
	{
		this.object=object;
		this.environment=environment;
		
	}
	
	
	public String getEnvironment() {
		return environment;
	}

	public EnvironmentLeftHandRule(IRule r)
	{
		set(r);
	}
	
	public void set(IRule r)
	{
		if (r.getLeftHandRule().getOuterRuleMembrane().getInnerRuleMembranes().isEmpty())
		{
			object=r.getLeftHandRule().getOuterRuleMembrane().getMultiSet().iterator().next();
			environment=r.getLeftHandRule().getOuterRuleMembrane().getLabel();
		}
		else
		{
			for (InnerRuleMembrane irm:r.getLeftHandRule().getOuterRuleMembrane().getInnerRuleMembranes())
			{
				if (!irm.getMultiSet().isEmpty())
				{
					object = irm.getMultiSet().iterator().next();
					environment=irm.getLabel();
					break;
				}
			}
		}	
	}

	public String getObject() {
		return object;
	}

	
	@Override
	protected Object clone() {
		// TODO Auto-generated method stub
		return new EnvironmentLeftHandRule(object,environment);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "("+object+")'"+environment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((environment == null) ? 0 : environment.hashCode());
		result = prime * result + ((object == null) ? 0 : object.hashCode());
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
		EnvironmentLeftHandRule other = (EnvironmentLeftHandRule) obj;
		if (environment == null) {
			if (other.environment != null)
				return false;
		} else if (!environment.equals(other.environment))
			return false;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		return true;
	}

	

}
