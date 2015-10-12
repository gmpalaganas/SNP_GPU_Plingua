package org.gcn.plinguacore.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/* Esta clase implementa los mismos métodos que LinkedHashSet, pero incluye el método:
 *
 *  public E get(Object o)
 * 
 */

public class ExtendedLinkedHashSet<E> implements Set<E> {
	
	private Map<E,E> map;
	
	public ExtendedLinkedHashSet()
	{
		map = new LinkedHashMap<E,E>();
	}
	
	public ExtendedLinkedHashSet(Collection <? extends E> c)
	{
		this();
		addAll(c);
	}
	
	
	@Override
	public boolean add(E e) {
		// TODO Auto-generated method stub
		map.put(e, e);
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		// TODO Auto-generated method stub
		for (E e:c)
			map.put(e, e);
		return true;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		map.clear();
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return map.containsKey(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return map.keySet().containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return map.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return map.keySet().iterator();
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return map.keySet().remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return map.keySet().removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return map.keySet().retainAll(c);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return map.size();
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return map.keySet().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return map.keySet().toArray(a);
	}
	
	public E get(Object o)
	{
		return map.get(o);
	}

	

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return map.keySet().toString();
	}
	
	

}
