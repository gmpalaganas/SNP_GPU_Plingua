package org.gcn.plinguacore.simulator.cellLike.probabilistic;


import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;


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

class Dnd2Thread implements Runnable {
	
	private Map<String,Dnd2MembraneData> membraneDataMap;
	private AbstractDnd2Sim simulator;
	private Configuration Ct,Cpt;
	private int maxDj=0;
	private String environmentId;
	private static final RuleComparator ruleComparator=new RuleComparator();
	private Triple<IRule,Float,Long>[] Dj=null;


	public Dnd2Thread(AbstractDnd2Sim simulator,String environmentId)
	{
		membraneDataMap = new HashMap<String,Dnd2MembraneData>();
		this.simulator=simulator;
		this.environmentId=environmentId;
	}
	
	
	
	public String getEnvironmentId() {
		return environmentId;
	}



	public void addMembrane(CellLikeMembrane m)
	{
		String h= m.getLabel();
		String j=m.getLabelObj().getEnvironmentID();
	
		membraneDataMap.put(h,new Dnd2MembraneData(m.getId()));
		
		int max=0;
		for (byte alpha=-1;alpha<=1;alpha++)
		{
			
			int numRules=simulator.getPsystem().getRules().getNumberOfRules(h,j,alpha);
		
			if (numRules>max)
				max=numRules;
		}
		
		maxDj+=max;
		
	}
	
	public Iterator<Triple<IRule,Float,Long>> getSelectedRulesIterator(){
		return new DjIterator();
	}
	
	private static float evaluateProb(IRule r,Configuration Ct)
	{
		float p;
		if (!(r instanceof IConstantRule))
			p=1;
		else
			p=((IConstantRule)r).getConstant();
		
		return p;
	}
	
	private  void removeLeftHandRuleObjects(IRule r,
			long count) {
		
		ChangeableMembrane membrane = getMembraneDataFromRule(r).getMembraneFromCpt();
		
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

	private void updateCharge(IRule r)
	{
		Dnd2MembraneData data=getMembraneDataFromRule(r);
		
		if (!data.isB())
		{
			data.setB(true);
			byte alpha=r.getRightHandRule().getOuterRuleMembrane().getCharge();
			data.getMembraneFromCpt().setCharge(alpha);
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
	
	private void executeRules()
	{
		for (int i=0;i<maxDj;i++)
		{
			IRule r = Dj[i].getFirst();
			long n = Dj[i].getThird();
			executeRule(getMembraneDataFromRule(r).getMembraneFromCpt(),Cpt.getEnvironment(),r,n);
		}
	}
	@SuppressWarnings("unchecked")	
	public boolean initDj()
	{
		if (maxDj==0)
			return false;
		Dj = (Triple<IRule,Float,Long>[])new Triple[maxDj];
		return true;
	}
	

	private void initSelection()
	{
		Ct = simulator.getCurrentConfig();
		Cpt = simulator.getCp();
		maxDj=0;
		Iterator<Dnd2MembraneData>membraneIterator = membraneDataMap.values().iterator();
	
		while(membraneIterator.hasNext())
		{
			
			Dnd2MembraneData data=membraneIterator.next();
			data.setB(false);
			data.setMembraneFromCt((CellLikeMembrane)Ct.getMembraneStructure().getMembrane(data.getMembraneId()));
			data.setMembraneFromCpt((CellLikeMembrane)Cpt.getMembraneStructure().getMembrane(data.getMembraneId()));
			byte alpha = data.getMembraneFromCt().getCharge();
			String h=data.getMembraneFromCt().getLabel();
			String j=data.getMembraneFromCt().getLabelObj().getEnvironmentID();
			Iterator<IRule>ruleIterator=simulator.getPsystem().getRules().iterator(h, j, alpha, false);
			while(ruleIterator.hasNext())
			{
				
				IRule r = ruleIterator.next();
				float p = evaluateProb(r,Ct);
				if (p>0)
				{
					Dj[maxDj++]=new Triple<IRule,Float,Long>(r,p,(long)0);
					
				}
			}
		}
	}
	
	private static int Fu(int max)
	{
		return RandomNumbersGenerator.getInstance().nextInt(max);
	}
	
	private static long Fb(long N,double p)
	{
		return RandomNumbersGenerator.getInstance().nextLongBi(N, p);
	}
	
	private Dnd2MembraneData getMembraneDataFromRule(IRule r)
	{
		String h=r.getLeftHandRule().getOuterRuleMembrane().getLabel();
		return membraneDataMap.get(h);
	}
	
	private boolean isConsistent(IRule r)
	{
		Dnd2MembraneData data = getMembraneDataFromRule(r);
		if (!data.isB())
			return true;
		byte alphaRule = r.getRightHandRule().getOuterRuleMembrane().getCharge();
		byte alphaMemb = data.getMembraneFromCpt().getCharge();
		return alphaRule==alphaMemb;
	}
	
	private long countApplicationsFromCt(IRule r)
	{
		Dnd2MembraneData data = getMembraneDataFromRule(r);
		return r.countExecutions(data.getMembraneFromCt());
	}
		
	private long countApplicationsFromCpt(IRule r)
	{
		Dnd2MembraneData data = getMembraneDataFromRule(r);
		return r.countExecutions(data.getMembraneFromCpt());
	}
	
	private void swapDj(int firstIndex,int secondIndex)
	{
		if (firstIndex!=secondIndex)
		{
			Triple<IRule,Float,Long> aux = Dj[secondIndex];
			Dj[secondIndex]=Dj[firstIndex];
			Dj[firstIndex]=aux;
		}
	}
	

	
	private void firstSelectionPhase()
	{
	
		for (int k=0;k<simulator.getK();k++)
		{
			int maxK = maxDj;
			while(maxK>0)
			{
				int i = Fu(maxK);
				IRule r = Dj[i].getFirst();
				float p = Dj[i].getSecond();
				long n = Dj[i].getThird();
				if (isConsistent(r))
				{
					long Np = countApplicationsFromCpt(r);
					if (Np>0)
					{
						long ni;
						if (p==1)
							//ni = Fb(Np,0.5);
							ni=Np;
						else
						{
							long N = countApplicationsFromCt(r);
							ni = Fb(N,p);
							if (ni>Np)
								ni=Np;
						}
						if (ni>0)
						{
							removeLeftHandRuleObjects(r,ni);
							updateCharge(r);
							Dj[i].setThird(n+ni);
						}
						swapDj(i,maxK-1);
						maxK--;
					}
					else if(n==0)
					{
						swapDj(i,maxK-1);
						swapDj(maxK-1,maxDj-1);
						maxK--;
						maxDj--;
					}
					else
					{
						swapDj(i,maxK-1);
						maxK--;
					}
				}
				else
				{
					swapDj(i,maxK-1);
					swapDj(maxK-1,maxDj-1);
					maxK--;
					maxDj--;
				}
			}
		}
	}
	
	private void secondSelectionPhase()
	{
		Arrays.sort(Dj,0,maxDj,ruleComparator);
		for (int i=0;i<maxDj;i++)
		{
			IRule r = Dj[i].getFirst();
			long n = Dj[i].getThird();
			if (n>0 || isConsistent(r))
			{
				long Np = countApplicationsFromCpt(r);
				if (Np>0)
				{
					Dj[i].setThird(n+Np);
					removeLeftHandRuleObjects(r,Np);
					if(n==0)
						updateCharge(r);
				}
			}
		}
	}
	
	public void runSelLoop()
	{
		initSelection();
		firstSelectionPhase();
		if (maxDj>0)
		{
			
			secondSelectionPhase();
			if (!simulator.thereAreRules())
				simulator.thereAreRules(true);	
		}
		if (simulator.isMultiThread())
			simulator.getSelection().sync();
	
	}
	
	protected void runExecLoop()
	{
		executeRules();
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
	
	static class RuleComparator implements Comparator<Triple<IRule,Float,Long>>
	{

		@Override
		public int compare(Triple<IRule, Float, Long> o1,
				Triple<IRule, Float, Long> o2) {
			// TODO Auto-generated method stub
			
			if (o1.getSecond()>o2.getSecond())
				return -1;
			else
			if (o1.getSecond()<o2.getSecond())
				return 1;
			else 
				return 0;
		}	
	}
	
	class DjIterator implements Iterator<Triple<IRule,Float,Long>>
	{
		private int index;

		public DjIterator() {
			index=0;
		}
		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return index<maxDj;
		}

		@Override
		public Triple<IRule, Float, Long> next() {
			// TODO Auto-generated method stub
			if (!hasNext())
				throw new NoSuchElementException();
			
			return Dj[index++];
			
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException ();
		}
		
	}
	
}
