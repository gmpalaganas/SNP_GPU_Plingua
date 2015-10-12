package org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcn.plinguacore.util.Triple;
import org.gcn.plinguacore.util.psystem.Label;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeSkinMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.membrane.MembraneStructure;


public abstract class MatrixColumn{
	
	// min: minimum number, number of times for the block to be applied.
	
	private long min;

	/* Devuelve una coleccion de ternas: objeto, etiqueta y multiplicidad para
	 * todos los objetos implicados en la parte izquierda de la regla de la columna.
	 * Se pasa como argumento un mapa <h,f(h)> que almacena pares de etiquetas de membrana
	 * <h, f(h)> en donde f(h) es padre de h
	 */
	public abstract Collection<Triple<String,String,Long>>getLeftHandRuleObjects(Map<String,String>parents);
	public abstract boolean retainColumn(CellLikeSkinMembrane ms,Map<String,Integer>map, String environment);
	/* devuelve true si quedan objetos */
	public abstract boolean removeLeftHandRuleObjects(CellLikeSkinMembrane ms,Map<String,Integer>map, String environment,long multiplicity);
	
	public abstract long countApplications(CellLikeSkinMembrane ms,Map<String,Integer>map, String environment);

	public abstract List<? extends IRightHandRule>getRightHandRules();
	
	public abstract String getMainLabel();
	
	public abstract String matrixColumnToString();

	public abstract boolean isSkeletonColumn();
	
	public abstract boolean isEnvironmentColumn();
	
	public long getMin() {
		return min;
	}


	public void setMin(long min) {
		this.min = min;
	}


	
}
