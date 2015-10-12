package org.gcn.plinguacore.simulator.tissueLike;

import org.gcn.plinguacore.simulator.CreateSimulator;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.rule.AbstractRuleFactory;
import org.gcn.plinguacore.util.psystem.rule.tissueLike.TSCSRuleFactory;
import org.gcn.plinguacore.util.psystem.rule.tissueLike.TissueLikeRuleFactory;
import org.gcn.plinguacore.util.psystem.tissueLike.TissueLikePsystem;


public class TSCSPsystemFactory extends
		TissueLikePsystemFactory {

	private static TSCSPsystemFactory singleton=null;
	
	
	private TSCSPsystemFactory()
	{
		
	}

	
	public static TSCSPsystemFactory getInstance()
	{
		if (singleton==null)
			singleton= new TSCSPsystemFactory();
		return singleton;
	}
	@Override
	protected AbstractRuleFactory newRuleFactory() {
		// TODO Auto-generated method stub
		return new TSCSRuleFactory();
	}
	
	
	@Override
	public CreateSimulator getCreateSimulator() throws PlinguaCoreException {
		// TODO Auto-generated method stub
		return new TSCCreateSimulator(getModelName());

	}

	@Override
	protected Psystem newPsystem() {
		// TODO Auto-generated method stub
		return new TissueLikePsystem();
	}

	
	
}
