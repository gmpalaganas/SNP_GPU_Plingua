package org.gcn.plinguacore.simulator.cellLike.probabilistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import java.util.List;
@Deprecated
public class Dnd3ProbabilisticSimulator extends AbstractProbabilisticSimulator{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8435837428038650914L;
	
	private List<ParteIzquierda>bloques;
	
	public Dnd3ProbabilisticSimulator(Psystem psystem) {
		super(psystem);
		bloques=calculaBloquesReglas(psystem);
		// TODO Auto-generated constructor stub
	}
	
	public static List<ParteIzquierda> calculaBloquesReglas(Psystem psystem)
	{
		List<ParteIzquierda> bloques = new ArrayList<ParteIzquierda>();
		
		Iterator<? extends Membrane>it=psystem.getFirstConfiguration().getMembraneStructure().getAllMembranes().iterator();
		while(it.hasNext())
		{
			Membrane m = it.next();
			for (int carga=-1;carga<=1;carga++)
			{
				Iterator<IRule> it1 = psystem.getRules().iterator(
						m.getLabel(),
						m.getLabelObj().getEnvironmentID(),
						carga,true);
				while(it1.hasNext())
				{
					IRule r = it1.next();
					ParteIzquierda p = new ParteIzquierda(r,(CellLikeMembrane)m);
					if (!bloques.contains(p))
					{
						p.getReglas().add(r);
						bloques.add(p);
					}
					else
					{
						int i=bloques.indexOf(p);
						bloques.get(i).getReglas().add(r);
					}
				}
			}
		}
		return bloques;
	}
	

	@Override
	protected void microStepInit() {
		// TODO Auto-generated method stub
		super.microStepInit();
		
	}



	@Override
	protected void microStepSelectRules(ChangeableMembrane membrane,
			ChangeableMembrane tempMembrane) {
		// TODO Auto-generated method stub
		
	}

}
