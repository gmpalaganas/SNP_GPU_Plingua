package org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba;

import org.gcn.plinguacore.util.Pair;

public class MatrixKey {
	
	private Pair<MatrixRow,MatrixColumn> key;
	
	public MatrixKey()
	{
		key=new Pair<MatrixRow,MatrixColumn>(null,null);
	}
	
	public MatrixKey(MatrixRow row,MatrixColumn column)
	{
		key = new Pair<MatrixRow,MatrixColumn>(row,column);
	}
	
	public MatrixRow getRow()
	{
		return key.getFirst();
	}
	public MatrixColumn getColumn()
	{
		return key.getSecond();
	}
	
	public void setRow(MatrixRow matrixRow)
	{
		key.setFirst(matrixRow);
	}

	public void setColumn(MatrixColumn matrixColumn)
	{
		key.setSecond(matrixColumn);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
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
		MatrixKey other = (MatrixKey) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return key.toString();
	}
	
	/*public Object clone(){
		MatrixKey returnedKey = new MatrixKey(new Pair(MatrixRow)row.clone(), (MatrixColumn));
		
	}*/

}
