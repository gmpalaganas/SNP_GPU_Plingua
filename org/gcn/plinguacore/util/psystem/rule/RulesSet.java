/* 
 * pLinguaCore: A JAVA library for Membrane Computing
 *              http://www.p-lingua.org
 *
 * Copyright (C) 2009  Research Group on Natural Computing
 *                     http://www.gcn.us.es
 *                      
 * This file is part of pLinguaCore.
 *
 * pLinguaCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pLinguaCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pLinguaCore.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gcn.plinguacore.util.psystem.rule;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;


import java.util.NoSuchElementException;

import org.gcn.plinguacore.util.RandomNumbersGenerator;
import org.gcn.plinguacore.util.ShuffleIterator;
import org.gcn.plinguacore.util.psystem.AlphabetObject;
import org.gcn.plinguacore.util.psystem.Label;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule;

/**
 * A Set for rules
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 *
 */
public class RulesSet implements Set<IRule>, Serializable {

	/**
	 * 
	 */
	private static RuleComparator ruleComparator = new RuleComparator();
	private static final long serialVersionUID = 244011999390422931L;
	private CheckRule checkRule;
	private static final Iterator<IRule> emptyIterator = (new LinkedHashSet<IRule>())
			.iterator();
	private Map<String, List<IRule>[]> rules = null;
	private Map<String,List<IRule>[]>rulesByEnv =null;
	private Set<IRule> allRules=null;
	
	
	private SortedSet<AlphabetObject> alphabet = null;
/**
 * Constructs an empty RulesSet
 */
	public RulesSet() {
		super();
		rules = new HashMap<String,List<IRule>[]>();
		rulesByEnv=new HashMap<String,List<IRule>[]>();
		allRules= new LinkedHashSet<IRule>();
	
		
	}

	/**
	 * Constructs a RulesSet with initial rules 
	 *  @param rules The initial rules
	 */
	public RulesSet(Collection<? extends IRule> rules) {
		this();
		addAll(rules);
	}
	
/**
 * Constructs a RulesSet and update the alphabet passed as argument each time a new IRule is added to the set
 * @param alphabet The alphabet for this RulesSet
 */
	public RulesSet(SortedSet<AlphabetObject> alphabet) {
		this();
		this.alphabet = alphabet;
	}
	/**
	 * Returns true if the IRule passed as argument complies the model conditions
	 * @param r The IRule to be checked
	 * @return true if the IRule complies the model conditions
	 */
	public boolean checkRule(IRule r) {
		if (checkRule == null)
			return true;
		return checkRule.checkRule(r);
	}
	
	/**
	 * Returns true if all the rules within the RuleSet complies the model conditions
	 * @return true if all the rules within the RuleSet complies the model conditions
	 */
	 

	public boolean checkRules() {
		if (checkRule == null)
			return true;
		Iterator<IRule> itRule = iterator();
		while (itRule.hasNext()) {
			if (!checkRule.checkRule(itRule.next()))
				return false;
		}
		return true;
	}

	/**
	 * Returns the CheckRule object for this RulesSet
	 * @return The CheckRule associated to this RulesSet
	 */
	public CheckRule getCheckRule() {
		return checkRule;
	}
	/**
	 * Set the CheckRule object for this RulesSet 
	 * @param cr The CheckRule to be associated
	 */
	public void setCheckRule(CheckRule cr) {
		if (cr == null)
			throw new NullPointerException(
					"CheckRule parameter shouldn't be null");
		this.checkRule = cr;
	}
	
	/**
	 * Returns an Iterator<IRule> for rules with a specific label, charge and environmentID
	 * @param labelID
	 * @param environmentID
	 * @param charge
	 * @return An Iterator<IRule> object for rules with label, charge and environment specified as arguments.
	 * Tambien se incluyen las reglas con la misma carga, etiqueta e identificador de entorno vacio.
	 */
	public Iterator<IRule>iterator(String labelID,String environmentID,int charge,boolean shuffle)
	{
		if (environmentID==null)
			return iterator(labelID,charge,shuffle);
		
		if (!shuffle)
			return new RuleIterator(labelID,environmentID,charge);
		else
			return new ShuffleRuleIterator(labelID,environmentID,charge);
		
	}
	
	public int getNumberOfRules(String labID,String envID,int charge)
	{
		int index = toIndex(charge);
		
		if (envID==null)
		{
			List<IRule>[] set = rules.get(labID);
			if (set == null)
				return 0;
			return set[index].size();
		}
		else
		{
			int size=0;
			if (!envID.equals(""))
			{
				Label label = new Label(labID,envID);
				List<IRule>[] l1 = rulesByEnv.get(label.toString());	
				if (l1!=null)
					size+=l1[index].size();
			}
			
			List<IRule>[] l2 = rulesByEnv.get(labID);
			
			if (l2!=null)
				size+=l2[index].size();
			
			return size;
			
			
		}
		
	}
	
	public static void sortByPriority(List<IRule>rulesList)
	{
		Collections.sort(rulesList,ruleComparator);
	}
	
	public Iterator<IRule>iterator(String labelID,String environmentID,int charge)
	{
		return iterator(labelID,environmentID,charge,false);
	}
	
	/**
	 * Returns an iterator<IRule> for rules with a specific label and charge.
	 * @param labelID
	 * @param charge
	 * @return An iterator<IRule> object for rules with label and charge passed as arguments.
	 */
	public Iterator<IRule> iterator(String labelID, int charge,boolean shuffle) {
		List<IRule>[] set = rules.get(labelID);
		if (set == null)
			return emptyIterator;
		int index = toIndex(charge);
		if (shuffle)
			return new ShuffleIterator<IRule>(set[index]);
		else	
			return set[index].iterator();
	}
	
	public Iterator<IRule> iterator(String labelID,int charge)
	{
		return iterator(labelID,charge,false);
	}

	private int toIndex(int charge) {
		int index;
		if (charge < 0)
			index = 0;
		else if (charge == 0)
			index = 1;
		else
			index = 2;
		return index;
	}

	@Override
	public boolean add(IRule IRule) {
	
		
		if (allRules.contains(IRule))
			return false;
		
	
		
		allRules.add(IRule);
		
		if (alphabet != null) {
			Iterator<String> objs = IRule.getObjects().iterator();
			while (objs.hasNext())
				alphabet.add(new AlphabetObject(objs.next()));
		}
		
		String label = IRule.getLeftHandRule().getOuterRuleMembrane().getLabel();
		
		String env = IRule.getLeftHandRule().getOuterRuleMembrane().getLabelObj().toString();
		
		int index = toIndex(IRule.getLeftHandRule().getOuterRuleMembrane()
				.getCharge());
		
		
	
		List<IRule>[] list = rules.get(label);
		if (list == null) {
			List<IRule>[] rulesByCharge = new List[3];
			for (int i = 0; i < 3; i++)
				rulesByCharge[i] = new ArrayList<IRule>();
			rules.put(label, rulesByCharge);
			list = rulesByCharge;
		}
	
		list[index].add(IRule);
		
		
		
		list = rulesByEnv.get(env);
		if (list == null) {
			List<IRule>[] rulesByCharge = new List[3];
			for (int i = 0; i < 3; i++)
				rulesByCharge[i] = new ArrayList<IRule>();
		
			rulesByEnv.put(env, rulesByCharge);
			list = rulesByCharge;
		}
	
		list[index].add(IRule);
		
		
		
		
		return true;

	}

	@Override
	public String toString() {

		String str = "";
		Iterator<String> it = rules.keySet().iterator();

		while (it.hasNext()) {
			String label = it.next();
			List<IRule>[] s = rules.get(label);
			for (int i = 0; i < 3; i++) {
				if (s[i].size() > 0)
					str += "\nLabel: " + label + ", Charge: "
							+ Membrane.getChargeSymbol((byte) (i - 1)) + "\r\n";
				Iterator<IRule> it1 = s[i].iterator();
				while (it1.hasNext())
					str += it1.next() + "\r\n";
			}
		}

		return str;
	}

	@Override
	public boolean addAll(Collection<? extends IRule> arg0) {
		Iterator<? extends IRule> it = arg0.iterator();
		boolean b = false;
		while (it.hasNext())
			b = add(it.next()) || b;
		return b;
	}

	@Override
	public void clear() {
		rules.clear();
		rulesByEnv.clear();
		allRules.clear();
		
	}

	@Override
	public boolean contains(Object arg0) {
		
		return allRules.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		Iterator<?> it = arg0.iterator();
		boolean b = true;
		while (it.hasNext() && b)
			b = b && contains(it.next());
		return b;

	}

	@Override
	public boolean isEmpty() {
		return allRules.isEmpty();
	}

	@Override
	public Iterator<IRule> iterator() {
		return allRules.iterator();
	}

	@Override
	public boolean remove(Object arg0) {
		
		boolean b=allRules.remove(arg0);
		if (!b)
			return false;
		
		IRule r = (IRule) arg0;
		List<IRule>[] s = rules.get(r.getLeftHandRule().getOuterRuleMembrane().getLabel());
		if (s == null)
			return false;
		int index = toIndex(r.getLeftHandRule().getOuterRuleMembrane()
				.getCharge());
		s[index].remove(arg0);
		
	
		s = rulesByEnv.get(r.getLeftHandRule().getOuterRuleMembrane()
				.getLabelObj().toString());
		if (s == null)
			return false;
		s[index].remove(arg0);

		return true;
		
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		Iterator<?> it = arg0.iterator();
		boolean b = false;
		while (it.hasNext())
			/* QUERY: the former code didn't care about lazy evaluation */
			b = remove(it.next()) || b;
		return b;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		Iterator<IRule> it = iterator();
		boolean b = false;
		while (it.hasNext()) {
			IRule r = it.next();
			if (!(arg0.contains(r))) {
				it.remove();
				b = true;
			}
		}
		return b;
	}

	@Override
	public int size() {
		return allRules.size();
	}

	@Override
	public Object[] toArray() {
		Object o[] = new Object[allRules.size()];
		Iterator<IRule> it = iterator();
		int i = 0;
		while (it.hasNext()) {
			o[i] = it.next();
			i++;
		}
		return o;
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		T[] aux;
		if (arg0 == null)
			throw new NullPointerException();
		if (arg0.length < allRules.size())
			aux = (T[]) new Object[allRules.size()];
		else if (arg0.length == allRules.size())
			aux = arg0;
		else {
			aux = arg0;
			aux[allRules.size()] = null;
		}

		Iterator<IRule> it = iterator();
		int i = 0;
		while (it.hasNext()) {

			Object obj = it.next();
			if (aux[i].getClass().isAssignableFrom(obj.getClass()))
				aux[i] = (T) obj;
			else
				throw new ArrayStoreException();
			i++;
		}
		return aux;
	}

	class ShuffleRuleIterator implements Iterator<IRule> {

		private Iterator<IRule> it1=null,it2=null;
		
		public ShuffleRuleIterator(String labelID,String envID,int charge)
		{
			int index = toIndex(charge);
			if (!envID.equals(""))
			{
				Label label = new Label(labelID,envID);
				List<IRule>[] l1 = rulesByEnv.get(label.toString());	
				if (l1!=null)
					it1 = new ShuffleIterator<IRule>(l1[index]);
			}
			
			List<IRule>[] l2 = rulesByEnv.get(labelID);
			
			if (l2!=null)
				it2= new ShuffleIterator<IRule>(l2[index]);
				
		}
		
		private boolean hasNext(Iterator<IRule> it)
		{
			return it!=null && it.hasNext();
		}
		
		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return hasNext(it1) || hasNext(it2);
		}
		@Override
		public IRule next() {
			// TODO Auto-generated method stub
			
			if (!hasNext())
				throw new NoSuchElementException();
			
			Iterator<IRule>it;
			
			
			if (hasNext(it1) && hasNext(it2))
				it = RandomNumbersGenerator.getInstance().nextInt(2)!=0 ? it1 : it2;
			else
			if (hasNext(it1))
				it=it1;
			else
				it=it2;		
			
			return it.next();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
			// TODO Auto-generated method stub
			
		}
		
	}

	
	 class RuleIterator implements Iterator<IRule> {

			
			private List<IRule>l1=null;
			private List<IRule>l2=null;
		
			int index=0;
			
			public RuleIterator(String labelID,String envID,int charge)
			{	
				int index = toIndex(charge);
				
				if (!envID.equals(""))
				{
					Label label = new Label(labelID,envID);
					List<IRule>[] l1 = rulesByEnv.get(label.toString());	
					if (l1!=null)
					{
						this.l1 = l1[index];
					}
				}
						
				List<IRule>[] l2 = rulesByEnv.get(labelID);
						
				if (l2!=null)
				{
					this.l2= l2[index];
				}
			}
			
			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				return (l1!=null && !l1.isEmpty())|| (l2!=null && !l2.isEmpty());
				
			}
			
		
			@Override
			public IRule next() {
				// TODO Auto-generated method stub
				
				
				
				if (l1!=null && !l1.isEmpty())
				{
					IRule r= l1.get(index);
					index++;
					if (index==l1.size())
					{
						index=0;
						l1=null;
					}
					return r;
				}
				else
				if (l2!=null && !l2.isEmpty())
				{
					IRule r= l2.get(index);
					index++;
					if (index==l2.size())
					{
						index=0;
						l2=null;
					}
					return r;
				}
				else 
					throw new NoSuchElementException();
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
				throw new UnsupportedOperationException();
			}

		}


}
