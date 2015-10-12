package org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba;



import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

import org.gcn.plinguacore.util.RandomNumbersGenerator;
import org.gcn.plinguacore.util.psystem.Label;

import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeNoSkinMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeSkinMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;

import org.gcn.plinguacore.util.psystem.rule.IConstantRule;
import org.gcn.plinguacore.util.psystem.rule.IRule;


public class StaticMethods {
	
	/* Dada la configuración inicial, devuelve la lista de pares de etiquetas de membrana 
	 * de entorno e identificador de entorno
	 * Suponemos que la estructura de membranas es como sigue:
	 * [ [   ]'a,x  [   ]'b,y   [    ]'c,z .... ]'p
	 * En donde hay una piel "virtual" y los entornos son las membranas hijas de la piel.
	 * Las etiquetas de las membrananas de entorno son: a,b,c
	 * Los identificadores de entorno son: x,y,z
	 */
	
	public static final String GENERIC_ENVIRONMENT = "_e";
		
	public static Map<String,String> getEnvironments(CellLikeSkinMembrane ms)
	{
		Map<String,String>map = new HashMap<String,String>();
		for (CellLikeNoSkinMembrane m:ms.getChildMembranes())
			map.put(m.getLabel(),m.getLabelObj().getEnvironmentID());
		return map;
	}
	
		
	
	public static boolean isSkeletonRule(IRule r,Map<String,String> environmentLabels)
	{
		//  r.getLeftHandRule().getOuterRuleMembrane().getInnerRuleMembranes().isEmpty() == there are NO inner membranes in the left side of the rule
		// !r.getLeftHandRule().getOuterRuleMembrane().getInnerRuleMembranes().isEmpty() == there are inner membranes in the left side of the rule
		boolean b;
		if (!r.getLeftHandRule().getOuterRuleMembrane().getInnerRuleMembranes().isEmpty())
			b=false;
		else
		if (!r.getRightHandRule().getOuterRuleMembrane().getInnerRuleMembranes().isEmpty())
			b=false;
		else		
			b= !environmentLabels.containsKey(r.getLeftHandRule().getOuterRuleMembrane().getLabel());
		
		return b;
	}
	
	public static boolean isEnvironmentRule(IRule r,Map<String,String> environmentLabels)
	{
		return !isSkeletonRule(r, environmentLabels);
	}
	
	
	public static float getProbability(IRule r)
	{
		if (!(r instanceof IConstantRule))
			return 1;
		return ((IConstantRule)r).getConstant();
	}
	
	public static String getEnvironment(IRule r)
	{
	
		return r.getLeftHandRule().getOuterRuleMembrane().getLabelObj().getEnvironmentID();
	}
	
	/*Obtiene todos los pares etiqueta_hijo, etiqueta_padre */
	public static Map<String,String>getParents(CellLikeMembrane m,Map<String,String>map)
	{
	
		getParentsRec(m,map);
		return map;
		
	}
	public static Map<String,String>getParents(CellLikeMembrane m)
	{
		return getParents(m,new HashMap<String,String>());
		
	}
	public static Map<String,Integer>getMembraneIdsByLabelAndEnvironment(CellLikeSkinMembrane ms)
	{
		Map<String,Integer>map = new HashMap<String,Integer>();
		
		for (Membrane m:ms.getAllMembranes())
			map.put(m.getLabel()+","+m.getLabelObj().getEnvironmentID(), ((CellLikeMembrane)m).getId());
		
		
		return map;
	}
	
	private static void getParentsRec(CellLikeMembrane m,Map<String,String>parents)
	{
		for (CellLikeMembrane m1:m.getChildMembranes())
		{
			parents.put(m1.getLabel(), m.getLabel());
			getParentsRec(m1,parents);
		}
	}
	

	public static Membrane getMembrane(String label, String environment, CellLikeSkinMembrane ms,Map<String,Integer>membranesIndexes)
	{
		
		if (label.equals(GENERIC_ENVIRONMENT))
			label=environment;
		
		String labelAndEnvironment=label+","+environment;

		
		Integer id = membranesIndexes.get(labelAndEnvironment);
		
		if (id ==null)
			return null;
		
		return ms.getMembrane(id);
	}
	
	
}
