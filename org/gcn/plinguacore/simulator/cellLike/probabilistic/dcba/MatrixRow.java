package org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba;

import java.util.Map;

import org.gcn.plinguacore.util.Pair;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeSkinMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;

public class MatrixRow {

	private Pair<String,String>data;
	
	public MatrixRow(String object, String label) {
		super();
		data = new Pair<String,String>(object,label);
	}
	public String getObject() {
		return data.getFirst();
	}
	public String getLabel() {
		return data.getSecond();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
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
		MatrixRow other = (MatrixRow) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return data.toString();
	}


	public boolean retainRow(CellLikeSkinMembrane ms,Map<String,Integer>map, String environment)
	{
		Membrane m=StaticMethods.getMembrane( data.getSecond(), environment, ms, map);
		if (m==null)
			return false;
		return m.getMultiSet().contains(data.getFirst());
		
	}
	
	
	
	

}
