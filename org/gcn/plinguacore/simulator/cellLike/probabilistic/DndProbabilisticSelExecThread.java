package org.gcn.plinguacore.simulator.cellLike.probabilistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.gcn.plinguacore.util.HashMultiSet;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.RandomNumbersGenerator;
import org.gcn.plinguacore.util.Triple;
import org.gcn.plinguacore.util.psystem.Configuration;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeNoSkinMembrane;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.rule.IConstantRule;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.InnerRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.LeftHandRule;

public class DndProbabilisticSelExecThread implements Runnable {
	
	private List<DndProbabilisticSimulatorData> L;
	private List<Triple<DndProbabilisticSimulatorData,IRule,Long>> Rsel;
	private SortedMap<Float,List<Triple<DndProbabilisticSimulatorData,IRule,Long>>> sortedRsel;
	private Map<Integer,MultiSet<IRule>> rulesMap;
	private AbstractDndProbabilisticSimulator simulator;
	private Configuration cnf,tmpCnf;
	
	public DndProbabilisticSelExecThread(AbstractDndProbabilisticSimulator simulator)
	{
		L = new ArrayList<DndProbabilisticSimulatorData>();
		Rsel= new ArrayList<Triple<DndProbabilisticSimulatorData,IRule,Long>>();
		sortedRsel= new TreeMap<Float,List<Triple<DndProbabilisticSimulatorData,IRule,Long>>>(); 
		rulesMap = new HashMap<Integer,MultiSet<IRule>>();
		
		this.simulator=simulator;
	}
	
	public Map<Integer,MultiSet<IRule>> getRulesMap()
	{
		return rulesMap;
	}
	

	protected final List<DndProbabilisticSimulatorData> getL() {
		return L;
	}
	
	private int initSelection(Configuration cnf,Configuration tmpCnf)
	{
		int max = L.size();
		int i=0;
		while(i<max)
		{
			DndProbabilisticSimulatorData data = L.get(i);
			data.getRules().clear();
			data.setBoolean(false);
			CellLikeMembrane m = (CellLikeMembrane)cnf.getMembraneStructure().getMembrane(data.getId());
			Iterator<IRule> it=simulator.getPsystem().getRules().iterator(m.getLabel(),m.getLabelObj().getEnvironmentID(),m.getCharge());
			if (!it.hasNext())
			{
				data.setMax(0);
				data.setMembrane(null);
				data.setTempMembrane(null);
				DndProbabilisticSimulatorData aux = L.get(max-1);
				L.set(max-1, data);
				L.set(i,aux);
				max--;
			}
			else
			{
				while(it.hasNext())
					data.getRules().add(it.next());
				data.setMax(data.getRules().size());
				CellLikeMembrane tmpM = (CellLikeMembrane)tmpCnf.getMembraneStructure().getMembrane(data.getId());
				data.setMembrane(m);
				data.setTempMembrane(tmpM);
				i++;
			}
		}
		return max;
	}
	
	private static boolean checkConsistency(IRule r,CellLikeMembrane m)
	{
		boolean check=true;
		
		byte rc=r.getRightHandRule().getOuterRuleMembrane().getCharge();
		byte mc=m.getCharge();
		if (rc!=mc)
			check=false;
		
		return check;
	}
	
	private static float evaluateProb(IRule r)
	{
		float p;
		if (!(r instanceof IConstantRule))
			p=1;
		else
			p=((IConstantRule)r).getConstant();
		
		return p;
	}
	
	protected static void removeLeftHandRuleObjects(ChangeableMembrane membrane, IRule r,
			long count) {
		if (!(membrane instanceof CellLikeMembrane))
			throw new IllegalArgumentException("Illegal arguments");
		CellLikeMembrane m= (CellLikeMembrane)membrane;
		LeftHandRule lhr = r.getLeftHandRule();
		if (!m.isSkinMembrane()) {
			CellLikeNoSkinMembrane noSkin = (CellLikeNoSkinMembrane) m;
			noSkin.getParentMembrane().getMultiSet().subtraction(
					lhr.getMultiSet(), count);
		}
		m.getMultiSet().subtraction(lhr.getOuterRuleMembrane().getMultiSet(),
				count);
		ListIterator<InnerRuleMembrane> it = lhr.getOuterRuleMembrane()
				.getInnerRuleMembranes().listIterator();

		while (it.hasNext()) {
			InnerRuleMembrane im = it.next();
			Iterator<CellLikeNoSkinMembrane> it1 = m.getChildMembranes()
					.iterator();
			while (it1.hasNext()) {
				CellLikeNoSkinMembrane m1 = it1.next();
				if (m1.getCharge() == im.getCharge()
						&& m1.getLabel().equals(im.getLabel())) {
					m1.getMultiSet().subtraction(im.getMultiSet(), count);
					break;
				}
			}
		}
	}

	private static void updateRightHandRuleCharge(IRule r,CellLikeMembrane m)
	{
		byte rc=r.getRightHandRule().getOuterRuleMembrane().getCharge();
		m.setCharge(rc);
	}
	
	private void selectRulesBackwardSorting()
	{
		Rsel.clear();
		Iterator<Float>it = sortedRsel.keySet().iterator();
		while(it.hasNext())
		{
			List<Triple<DndProbabilisticSimulatorData,IRule,Long>>l = sortedRsel.get(it.next());
			Iterator <Triple<DndProbabilisticSimulatorData,IRule,Long>>it1 = l.iterator();
			while(it1.hasNext())
			{
				Triple<DndProbabilisticSimulatorData,IRule,Long>data=it1.next();
				CellLikeMembrane tmpM = data.getFirst().getTempMembrane();
				IRule r = data.getSecond();
				long n = data.getThird();
				long Np = r.countExecutions(tmpM);
				if (Np>0)
				{
					removeLeftHandRuleObjects(tmpM,r,Np);
					n=n+Np;
					data = new Triple<DndProbabilisticSimulatorData,IRule,Long>(data.getFirst(),r,n);
				}
				Rsel.add(data);
			}
		}
	}
	
	private void selectRulesBackward()
	{
		int max = Rsel.size();
		while(max>0)
		{
			int rand = RandomNumbersGenerator.getInstance().nextInt(max);
			Triple<DndProbabilisticSimulatorData,IRule,Long>data = Rsel.get(rand);
			CellLikeMembrane tmpM = data.getFirst().getTempMembrane();
			IRule r = data.getSecond();
			long n = data.getThird();
			long Np = r.countExecutions(tmpM);
			if (Np>0)
			{
				removeLeftHandRuleObjects(tmpM,r,Np);
				n=n+Np;
				data = new Triple<DndProbabilisticSimulatorData,IRule,Long>(data.getFirst(),r,n);
			}
			Triple<DndProbabilisticSimulatorData,IRule,Long>aux = Rsel.get(max-1);
			Rsel.set(max-1,data);
			Rsel.set(rand,aux);
			max--;
		}
		
	}
	
	private void selectionRulesForwardPhase(int max)
	{
		int K=simulator.getMaxIterations();
		for (int k=0;k<K;k++)
		{
			int c=max;
			while(c>0)
			{
				int randC = RandomNumbersGenerator.getInstance().nextInt(c);
				DndProbabilisticSimulatorData data = L.get(randC);
				int d = data.getMax();
				int randD = RandomNumbersGenerator.getInstance().nextInt(d);
				IRule r = data.getRules().get(randD);
				CellLikeMembrane tempM = data.getTempMembrane();
				CellLikeMembrane m = data.getMembrane();
				long Np;
				if (data.getBoolean() && !checkConsistency(r,tempM))
					Np=0;
				else
					Np = r.countExecutions(tempM);
				if (Np>0)
				{
					long n= -1;
					float pR = evaluateProb(r);
					if (pR==1)
						n=RandomNumbersGenerator.getInstance().nextLongBi(Np, 0.5);
					else
					if (pR>0)
					{
						long N = r.countExecutions(m);
						n=RandomNumbersGenerator.getInstance().nextLongBi(N, pR);
						if (n>Np)
							n=Np;
					}
					if (n>0)
						removeLeftHandRuleObjects(tempM,r,n);
					if (n!=-1)
					{
						if (!data.getBoolean())
						{
							updateRightHandRuleCharge(r,tempM);
							data.setBoolean(true);
						}
						Triple<DndProbabilisticSimulatorData,IRule,Long>triple = new Triple<DndProbabilisticSimulatorData,IRule,Long>(data,r,n);
						Rsel.add(triple);
						Float index = new Float(1-pR);
						List<Triple<DndProbabilisticSimulatorData,IRule,Long>>l=sortedRsel.get(index);
						if (l==null)
						{
							l= new ArrayList<Triple<DndProbabilisticSimulatorData,IRule,Long>>();
							sortedRsel.put(index, l);
						}
						l.add(triple);
						
					}
				}
				IRule aux = data.getRules().get(d-1);
				data.getRules().set(d-1, r);
				data.getRules().set(randD, aux);
				d--;
				data.setMax(d);
				if (d==0)
				{
					data.setMax(data.getRules().size());
					DndProbabilisticSimulatorData aux1 = L.get(c-1);
					L.set(c-1, data);
					L.set(randC,aux1);
					c--;
				}
			}
		}
    }
	
	private void executeRule(CellLikeMembrane m,MultiSet<String>environment,IRule r,long n)
	{
		MultiSet<String> parentMultiSet = r.getRightHandRule().getMultiSet();
		if (!parentMultiSet.isEmpty())
		{
			if (m.isSkinMembrane())
				environment.addAll(parentMultiSet, n);
			else
			{
				CellLikeNoSkinMembrane m1 = (CellLikeNoSkinMembrane)m;
				m1.getParentMembrane().getMultiSet().addAll(parentMultiSet,n);
			}
		}
		
		MultiSet<String> ms = r.getRightHandRule().getOuterRuleMembrane().getMultiSet();
		if (!ms.isEmpty())
			m.getMultiSet().addAll(ms,n);
		
		List<InnerRuleMembrane> l=r.getRightHandRule().getOuterRuleMembrane().getInnerRuleMembranes();
		Iterator<InnerRuleMembrane>it = l.iterator();
		while(it.hasNext())
		{
			InnerRuleMembrane irm = it.next();
			if (!irm.getMultiSet().isEmpty())
			{
				Iterator<CellLikeNoSkinMembrane> it1 = m.getChildMembranes()
				.iterator();
				boolean found=false;
				while (it1.hasNext() && !found)
				{
					CellLikeNoSkinMembrane m1 = it1.next();
					if (m1.getLabel().equals(irm.getLabel()))
					{
						m1.getMultiSet().addAll(irm.getMultiSet(), n);
						found=true;
					}
				}
			}
		}
		
		
	}
	
	private void executeRules(MultiSet<String>globalEnvironment)
	{
		for (int i=0;i<Rsel.size();i++)
		{
			Triple<DndProbabilisticSimulatorData,IRule,Long>data = Rsel.get(i);
			CellLikeMembrane tempM = data.getFirst().getTempMembrane();
			IRule r = data.getSecond();
			long n = data.getThird();
			
			MultiSet<IRule> mr = rulesMap.get(tempM.getId());
			if (mr==null)
			{
				mr=new HashMultiSet<IRule>();
				rulesMap.put(tempM.getId(), mr);
			}
		
			mr.add(r, n);
			
			executeRule(tempM,globalEnvironment,r,n);
		}
	}
	
	protected void runSelLoop()
	{
		Rsel.clear();
		sortedRsel.clear();
		rulesMap.clear();
		tmpCnf = simulator.getCp();
		cnf = simulator.getCurrentConfig();
		int max = initSelection(cnf,tmpCnf);
		selectionRulesForwardPhase(max);
		//selectRulesBackward();
		selectRulesBackwardSorting();
		if (!Rsel.isEmpty() && !simulator.thereAreRules())
			simulator.thereAreRules(true);
	
		if (simulator.isMultiThread())
			simulator.getSelection().sync();
	
	}
	
	protected void runExecLoop()
	{
		executeRules(tmpCnf.getEnvironment());
		
		if (simulator.isMultiThread())
			simulator.getExecution().sync();

	}

	
	@Override 
	public void run() {
		while(!simulator.isStopThreads())
		{
			runSelLoop();
			runExecLoop();
			simulator.getUniversalClock().sync();
		}
	}
	
	
}
