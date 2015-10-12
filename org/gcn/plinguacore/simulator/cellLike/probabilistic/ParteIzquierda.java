package org.gcn.plinguacore.simulator.cellLike.probabilistic;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.gcn.plinguacore.util.HashMultiSet;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.Triple;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeNoSkinMembrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.InnerRuleMembrane;

@Deprecated
public class ParteIzquierda {
	
	private MultiSet<Triple<String,Integer,Byte>> parteIzquierda;
	private Set<IRule> reglas=null;
	
	
	public ParteIzquierda(IRule r, CellLikeMembrane m)
	{
		parteIzquierda = new HashMultiSet<Triple<String,Integer,Byte>>();
		
		MultiSet<String>ms;
		
		ms = r.getLeftHandRule().getOuterRuleMembrane().getMultiSet();
		addMultiSet(ms,m.getId(),r.getLeftHandRule().getOuterRuleMembrane().getCharge());
	
		if (m instanceof CellLikeNoSkinMembrane)
		{
			int id = ((CellLikeNoSkinMembrane)m).getParentMembrane().getId();
			ms=r.getLeftHandRule().getMultiSet();
			addMultiSet(ms,id,(byte)0);
		}
		
		Iterator<InnerRuleMembrane> it1=r.getLeftHandRule().getOuterRuleMembrane().getInnerRuleMembranes().iterator();
		while(it1.hasNext())
		{
			InnerRuleMembrane irm = it1.next();
			int id = buscaMembranaHijaId(irm.getLabel(),m);
			addMultiSet(irm.getMultiSet(),id,irm.getCharge());
		}
		
	}
	
	private int buscaMembranaHijaId(String label,CellLikeMembrane m)
	{
		Iterator<CellLikeNoSkinMembrane> it=m.getChildMembranes().iterator();
		while(it.hasNext())
		{
			CellLikeNoSkinMembrane m1 = it.next();
			if (m1.getLabel().equals(label))
			{
				return m1.getId();
			}
			
		}
		return -1;
	}
	
	public Set<IRule> getReglas() {
		if (reglas==null)
			reglas= new HashSet<IRule>();
		return reglas;
	}

	private void addMultiSet(MultiSet<String> ms,int id,byte charge)
	{
		Iterator<String>it=ms.entrySet().iterator();
		
		while(it.hasNext())
		{
			String o = it.next();
			long mul = ms.count(o);
			addObject(o,id,charge,mul);
		}
		
	}


	private void addObject(String object, int membraneID,byte charge,long multiplicity)
	{
		parteIzquierda.add(new Triple<String,Integer,Byte>(object,membraneID,charge),multiplicity);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((parteIzquierda == null) ? 0 : parteIzquierda.hashCode());
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
		ParteIzquierda other = (ParteIzquierda) obj;
		if (parteIzquierda == null) {
			if (other.parteIzquierda != null)
				return false;
		} else if (!parteIzquierda.equals(other.parteIzquierda))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ParteIzquierda [parteIzquierda=" + parteIzquierda + ", reglas="
				+ reglas + "]";
	}
	
	

}
