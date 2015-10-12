package org.gcn.plinguacore.simulator.cellLike.probabilistic;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import java.util.Map;



import org.gcn.plinguacore.simulator.AbstractSimulator;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.ShuffleIterator;
import org.gcn.plinguacore.util.Triple;
import org.gcn.plinguacore.util.psystem.Configuration;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeNoSkinMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeSkinMembrane;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.membrane.MembraneStructure;
import org.gcn.plinguacore.util.psystem.rule.IRule;


public abstract class AbstractDnd2Sim extends AbstractSimulator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8435837428038650914L;

	private int K=10;
	
	
	

	private List<Dnd2Thread> threadsList;
	private Configuration Cp;
	private boolean multiThread=true;
	private boolean rules;
	private boolean stopThreads;
	private boolean firstTime;
	private ThreadBarrier selection;
	private ThreadBarrier execution;
	private ThreadBarrier universalClock;

	
	public AbstractDnd2Sim(Psystem psystem,boolean multiThread) {
		
		
		super(psystem);
	
		Map<String,Dnd2Thread>threadsMap= new HashMap<String,Dnd2Thread>();
		threadsList=new ArrayList<Dnd2Thread>();
		stopThreads=true;
		firstTime=true;
		rules=false;
		this.multiThread=multiThread;
		Iterator<? extends Membrane> it=getPsystem().getMembraneStructure().getAllMembranes().iterator();
	
		while(it.hasNext())
		{
			CellLikeMembrane m = (CellLikeMembrane)it.next();
			String environment=m.getLabelObj().getEnvironmentID();
			Dnd2Thread thread =threadsMap.get(environment);
		
			if (thread==null)
			{
				thread=new Dnd2Thread(this,environment);
				threadsMap.put(environment,thread);
			}
			thread.addMembrane(m);
		}
	
		Iterator<Dnd2Thread>it1 = threadsMap.values().iterator();
		while(it1.hasNext())
		{
			Dnd2Thread thread=it1.next();
			if (thread.initDj())
				threadsList.add(thread);
		}
	
		threadsMap=null;		
		selection=new ThreadBarrier(threadsList.size()+1);
		execution=new ThreadBarrier(threadsList.size()+1);
		universalClock = new ThreadBarrier(threadsList.size()+1);
		
	}
	
	
	
	
	public boolean isMultiThread() {
		return multiThread;
	}




	public final void stopThreads()
	{
		stopThreads=true;
		universalClock.notifyAll();
		
		
	}
	
	
	
	
	@Override
	public void setCurrentConfig(Configuration configuration) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
		
	}


	protected final boolean isStopThreads() {
		return stopThreads;
	}


	@Override
	public void setPsystem(Psystem psystem) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
		
	}


	

	protected final ThreadBarrier getUniversalClock() {
		return universalClock;
	}
	
	



	protected final Configuration getCp() {
		return Cp;
	}
	
	
	protected final ThreadBarrier getExecution() {
		return execution;
	}
	protected final ThreadBarrier getSelection() {
		return selection;
	}
	protected final boolean thereAreRules() {
		return rules;
	}
	protected final synchronized void thereAreRules(boolean rules) {
	
		this.rules = rules;
	}
		
	@Override
	public boolean step() throws PlinguaCoreException {
		rules=false;
		Cp = (Configuration)currentConfig.clone();
		
		if (firstTime)
		{
			if (getVerbosity()>0)
			{
				if (getVerbosity() > 1)
					printInfo(true);
				else
					printInfoShort(true);
			}
			initDate();
			firstTime=false;
		}
		
		if (multiThread)
		{
			
			if (stopThreads)
			{
			
				stopThreads=false;
				Iterator<Dnd2Thread>it = new ShuffleIterator<Dnd2Thread>(threadsList);
				
				while(it.hasNext())
				{
					
					(new Thread(it.next())).start();
				}
			}
			else
				universalClock.sync();
			
		
			selection.sync();
			execution.sync();
		}
		else
		{
			Iterator<Dnd2Thread>it = new ShuffleIterator<Dnd2Thread>(threadsList);
			while(it.hasNext())
				it.next().runSelLoop();
			it = new ShuffleIterator<Dnd2Thread>(threadsList);
			while(it.hasNext())
				it.next().runExecLoop();
		}
		currentConfig=Cp;
		
		if (getVerbosity()>0)
		{
		if (getVerbosity() > 1)
			printInfo(false);
		else
			printInfoShort(false);
		}
		return rules;
	}

	private void printInfoShort(boolean first) {
		if (this.getInfoChannel()==null)
			return;
		if (rules || first) {

			getInfoChannel().println(
					"***********************************************");
			getInfoChannel().println();
			getInfoChannel().println(
					"    CONFIGURATION: " + (currentConfig.getNumber()));
			if (isTimed()) {
				long mem = Runtime.getRuntime().maxMemory()
						- Runtime.getRuntime().freeMemory();
				mem = mem / 1024;
				getInfoChannel().println("    TIME: " + getTime() + " s.");
				getInfoChannel().println("    MEMORY: " + mem + " Kb");
			}
			getInfoChannel().println();

			
			printInfoMembraneShort(currentConfig.getMembraneStructure());
			if (!currentConfig.getEnvironment().isEmpty()) {
				getInfoChannel().println(
						"    ENVIRONMENT: " + currentConfig.getEnvironment());
				getInfoChannel().println();
			}
		} else {
			Iterator<? extends Membrane> it = currentConfig.getMembraneStructure().getAllMembranes()
					.iterator();
			while (it.hasNext())
				printInfoMembrane((ChangeableMembrane)it.next());
			if (!currentConfig.getEnvironment().isEmpty()) {
				getInfoChannel().println(
						"    ENVIRONMENT: " + currentConfig.getEnvironment());
				getInfoChannel().println();
			}
			getInfoChannel()
					.println(
							"Halting configuration (No rule can be selected to be executed in the next step)");
		}

	}

	protected void printInfoMembrane(ChangeableMembrane membrane) {
		if (!(membrane instanceof CellLikeMembrane))
			throw new IllegalArgumentException("Illegal arguments");
		CellLikeMembrane m = (CellLikeMembrane)membrane;
		getInfoChannel().println("    " + getHead(m));
		getInfoChannel().println("    Multiset: " + m.getMultiSet());
		if (!m.getChildMembranes().isEmpty())
			getInfoChannel().println(
					"    Internal membranes count: "
							+ m.getChildMembranes().size());
		if (!m.isSkinMembrane())
			getInfoChannel().println(
					"    Parent membrane ID: "
							+ ((CellLikeNoSkinMembrane) m).getParentMembrane()
									.getId());

		getInfoChannel().println();

	}

	protected void printInfoMembraneShort(MembraneStructure membraneStructure) {
		if (!(membraneStructure instanceof CellLikeSkinMembrane))
			throw new IllegalArgumentException("Illegal arguments");
		printInfoMembrane((CellLikeSkinMembrane)membraneStructure);
		
	}
	
	protected String getHead(ChangeableMembrane membrane) {
		if (!(membrane instanceof CellLikeMembrane))
			throw new IllegalArgumentException("Illegal arguments");
		CellLikeMembrane m = (CellLikeMembrane)membrane;
		String str = "";
		if (m.isSkinMembrane())
			str += "SKIN MEMBRANE ID: ";
		else
			str += "MEMBRANE ID: ";
		str += m.getId() + ", Label: " + m.getLabelObj() + ", Charge: "
				+ Membrane.getChargeSymbol(m.getCharge());
		return str;
	}
	

		
		
		private void printInfo(boolean first)
		{
			if (this.getInfoChannel()==null)
				return;
			
			if (rules || first)
			{
				if (!first)
				{
				getInfoChannel().println(
				"-----------------------------------------------");
				getInfoChannel().println();
				getInfoChannel().println(
						"    STEP: " + currentConfig.getNumber());
				
				Iterator<Dnd2Thread>threadIterator = threadsList.listIterator();
				while(threadIterator.hasNext())
				{
					Dnd2Thread thread = threadIterator.next();
					Iterator<Triple<IRule,Float,Long>> it1 = thread.getSelectedRulesIterator();
					getInfoChannel().println();
					if (!thread.getEnvironmentId().equals(""))
						getInfoChannel().println(
							"    Rules selected for environment "+thread.getEnvironmentId());
					while (it1.hasNext()) {
						Triple<IRule,Float,Long> data = it1.next();
						getInfoChannel().println(
								"    " + data.getThird() + " * " + data.getFirst());
					}
						
				}
				}
				
				getInfoChannel().println();
				getInfoChannel().println(
						"***********************************************");
					getInfoChannel().println();
					getInfoChannel().println(
							"    CONFIGURATION: " + (currentConfig.getNumber()));
					if (isTimed()) {
						getInfoChannel().println("    TIME: " + getTime() + " s.");
						getInfoChannel().println(
								"    MEMORY USED: "
										+ Runtime.getRuntime().totalMemory() / 1024);
						getInfoChannel().println(
								"    FREE MEMORY: " + Runtime.getRuntime().freeMemory()
										/ 1024);
						getInfoChannel().println(
								"    TOTAL MEMORY: " + Runtime.getRuntime().maxMemory()
										/ 1024);
					}
					getInfoChannel().println();

					Iterator<? extends Membrane> it = currentConfig.getMembraneStructure().getAllMembranes()
							.iterator();
					while (it.hasNext())
						printInfoMembrane((ChangeableMembrane)it.next());
					if (!currentConfig.getEnvironment().isEmpty()) {
						getInfoChannel().println(
								"    ENVIRONMENT: " + currentConfig.getEnvironment());
						getInfoChannel().println();
					}

				
			}
			else
			getInfoChannel().println(
				"Halting configuration (No rule can be selected to be executed in the next step)");
		
			
			
		}
	
	@Override
	public void reset() {
		super.reset();
		firstTime=true;
	}




	public int getK() {
		return K;
	}




	public void setK(int k) {
		K = k;
	}




	@Override
	protected boolean specificStep() throws PlinguaCoreException {
		// TODO Auto-generated method stub
		return false;
	}

	

	

}
