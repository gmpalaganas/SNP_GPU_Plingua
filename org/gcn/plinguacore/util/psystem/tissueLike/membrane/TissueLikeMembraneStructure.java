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

package org.gcn.plinguacore.util.psystem.tissueLike.membrane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.gcn.plinguacore.util.InfiniteMultiSet;
import org.gcn.plinguacore.util.ShuffleIterator;
import org.gcn.plinguacore.util.UnionIterator;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeSkinMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.membrane.MembraneStructure;

public class TissueLikeMembraneStructure implements MembraneStructure {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6393182806738062204L;
	private Map<String,List<TissueLikeMembrane>> cells;
	private Map<Integer,TissueLikeMembrane>cellsById;
	private TissueLikeEnvironment environment;
	private static final Iterator<TissueLikeMembrane>emptyIt= (new HashSet<TissueLikeMembrane>()).iterator();
	private Psystem psystem;
	
	
	
	public TissueLikeMembraneStructure(MembraneStructure membrane,Psystem psystem)
	{
		super();
		if (psystem==null)
			throw new NullPointerException();
		this.psystem=psystem;
		
		Iterator<? extends Membrane>it;
		
		cells = new HashMap<String,List<TissueLikeMembrane>>();
		cellsById=new LinkedHashMap<Integer,TissueLikeMembrane>();
		
		if (membrane instanceof CellLikeSkinMembrane)
		{
			CellLikeSkinMembrane clm = (CellLikeSkinMembrane)membrane;
			it = clm.getChildMembranes().iterator();
			String environmentLabel = clm.getLabel();
			environment = new TissueLikeEnvironment(environmentLabel,this);
			secureAdd(environment);
			
		}
		else
		if (membrane instanceof TissueLikeMembraneStructure)
		{
			TissueLikeMembraneStructure tls = (TissueLikeMembraneStructure)membrane;
			environment = new TissueLikeEnvironment(tls.getEnvironmentLabel(),this);
			environment.getMultiSet().addAll(tls.getEnvironment());
			secureAdd(environment);
			it = membrane.getAllMembranes().iterator();
			
		}
		else
			throw new IllegalArgumentException("The membrane structure must be tissue-like");
		
		
		while(it.hasNext())
		{
			Membrane m = it.next();
			if (m.getLabel().equals(environment.getLabel()))
			{
				if (membrane instanceof CellLikeSkinMembrane)
					throw new IllegalArgumentException("The environment label cannot be used as membrane label");
			}
			else
				add(new TissueLikeMembrane(m,this));
			
		}
		
		
	}


	public Psystem getPsystem() {
		return psystem;
	}


	protected int getNextId()
	{
		
		return cellsById.size();
	}


	public String getEnvironmentLabel() {
		return environment.getLabel();
	}
	@Override
	public Object clone()
	{
		return new TissueLikeMembraneStructure(this,psystem);
	}
	@Override
	public Collection<? extends Membrane> getAllMembranes() {
		// TODO Auto-generated method stub
		return new TissueLikeMembraneCollection();
	}
	
	
	
	@Override
	public Membrane getMembrane(int id) {
		// TODO Auto-generated method stub
		return getCell(id);
	}


	public TissueLikeMembrane getCell(int id)
	{
		return cellsById.get(id);
	}
	
	private boolean secureAdd(TissueLikeMembrane arg0)
	{
		String label = arg0.getLabel();
		List<TissueLikeMembrane>l;
		if (!cells.containsKey(label))
		{
			l=new ArrayList<TissueLikeMembrane>();
			cells.put(label, l);
		}
		else
			l=cells.get(label);
		
		l.add(arg0);
		cellsById.put(arg0.getId(), arg0);
		
		
		return true;
	}
	
	protected boolean add(TissueLikeMembrane arg0)
	{
		// TODO Auto-generated method stub
		
		if (arg0.getLabel().equals(getEnvironmentLabel()))
			throw new IllegalArgumentException("Environment label");
		return secureAdd(arg0);
		
	}
	
	public Iterator<TissueLikeMembrane>iterator(String label)
	{
		if (!cells.containsKey(label))
			return emptyIt;
		return cells.get(label).iterator();
	}
	
	public Iterator<TissueLikeMembrane>iterator(String label,boolean shuffle)
	{
		if (!cells.containsKey(label))
			return emptyIt;
		return shuffle?new ShuffleIterator<TissueLikeMembrane>(cells.get(label)):iterator(label);
	}

	public InfiniteMultiSet<String> getEnvironment()
	{
		return (InfiniteMultiSet<String>)environment.getMultiSet();
	}
	
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String str="Cells: ";
		Iterator<? extends Membrane>it = getAllMembranes().iterator();
		while (it.hasNext())
		{
			str+=it.next().toString();
			if (it.hasNext())
				str+=", ";
		}
		return str;
	}



	class TissueLikeMembraneCollection implements Collection<TissueLikeMembrane>
	{
		
		

		public TissueLikeMembraneCollection() {
			super();
			// TODO Auto-generated constructor stub
		}


	
		
	

	

		@Override
		public boolean add(TissueLikeMembrane arg0) {
			throw new UnsupportedOperationException();
		}



		@Override
		public boolean addAll(Collection<? extends TissueLikeMembrane> arg0) {
			throw new UnsupportedOperationException();
		}



		@Override
		public void clear() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}



		@Override
		public boolean contains(Object arg0) {
			// TODO Auto-generated method stub
			if (!(arg0 instanceof TissueLikeMembrane))
				return false;
			TissueLikeMembrane tlm = (TissueLikeMembrane)arg0;
			if (!cells.containsKey(tlm.getLabel()))
				return false;
			return cells.get(tlm.getLabel()).contains(arg0);
		}



		@Override
		public boolean containsAll(Collection<?> arg0) {
			// TODO Auto-generated method stub
			Iterator<?>it = arg0.iterator();
			boolean contains=true;
			while(contains && it.hasNext())
				contains=contains(it.next());
			return contains;
		}



		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return cellsById.isEmpty();
		}



		@Override
		public Iterator<TissueLikeMembrane> iterator() {
			// TODO Auto-generated method stub
			return cellsById.values().iterator();
		}

		
		

		@Override
		public boolean remove(Object arg0) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}



		@Override
		public boolean removeAll(Collection<?> arg0) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}



		@Override
		public boolean retainAll(Collection<?> arg0) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}



		@Override
		public int size() {
			// TODO Auto-generated method stub
			return cellsById.size();
		}



		@Override
		public Object[] toArray() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
			
		}



		@Override
		public <T> T[] toArray(T[] arg0) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		
		
		
	}
   
	

}
