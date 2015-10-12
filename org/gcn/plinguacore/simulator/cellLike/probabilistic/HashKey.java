package org.gcn.plinguacore.simulator.cellLike.probabilistic;



public class HashKey {
	
	private ProbabilisticLeftHandRule lhr;
	private String object;
	private int id;
	public HashKey(ProbabilisticLeftHandRule lhr, String object, int id) {
		super();
		this.lhr = lhr;
		this.object = object;
		this.id = id;
	}
	public ProbabilisticLeftHandRule getLhr() {
		return lhr;
	}
	public String getObject() {
		return object;
	}
	public int getId() {
		return id;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((lhr == null) ? 0 : lhr.hashCode());
		result = prime * result
				+ ((object == null) ? 0 : object.hashCode());
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
		HashKey other = (HashKey) obj;
		if (id != other.id)
			return false;
		if (lhr == null) {
			if (other.lhr != null)
				return false;
		} else if (!lhr.equals(other.lhr))
			return false;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "("+object+","+id+") "+lhr.toString();
	}
	

}
