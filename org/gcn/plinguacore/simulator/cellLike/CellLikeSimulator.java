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

package org.gcn.plinguacore.simulator.cellLike;


import java.util.Iterator;
import java.util.ListIterator;


import org.gcn.plinguacore.simulator.AbstractSelectionExecutionSimulator;

import org.gcn.plinguacore.util.psystem.Psystem;

import org.gcn.plinguacore.util.psystem.cellLike.CellLikePsystem;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeNoSkinMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeSkinMembrane;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.membrane.MembraneStructure;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.InnerRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.LeftHandRule;


/**
 * A simulator for cell-like P systems
 * 
 * @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public abstract class CellLikeSimulator extends AbstractSelectionExecutionSimulator {

	private static final long serialVersionUID = 2539727644482041974L;

	

	/**
	 * @param psystem
	 */
	public CellLikeSimulator(Psystem psystem) {
		super(psystem);
		if (!(psystem instanceof CellLikePsystem))
			throw new IllegalArgumentException(
					"psystem argument must be a Cell-Like Psystem");
	

	}

	

	
	


	@Override
	protected String getHead(ChangeableMembrane membrane) {
		if (!(membrane instanceof CellLikeMembrane))
			throw new IllegalArgumentException("Illegal arguments");
		CellLikeMembrane m = (CellLikeMembrane)membrane;
		String str = "";
		if (m.isSkinMembrane())
			str += "SKIN MEMBRANE ID: ";
		else
			str += "MEMBRANE ID: ";
		str += m.getId() + ", Label: " + m.getLabelObj() + ", Charge: "
				+ Membrane.getChargeSymbol(m.getCharge());
		return str;
	}
	@Override
	protected void printInfoMembrane(ChangeableMembrane membrane) {
		if (!(membrane instanceof CellLikeMembrane))
			throw new IllegalArgumentException("Illegal arguments");
		CellLikeMembrane m = (CellLikeMembrane)membrane;
		getInfoChannel().println("    " + getHead(m));
		getInfoChannel().println("    Multiset: " + m.getMultiSet());
		if (!m.getChildMembranes().isEmpty())
			getInfoChannel().println(
					"    Internal membranes count: "
							+ m.getChildMembranes().size());
		if (!m.isSkinMembrane())
			getInfoChannel().println(
					"    Parent membrane ID: "
							+ ((CellLikeNoSkinMembrane) m).getParentMembrane()
									.getId());

		getInfoChannel().println();

	}

	


	
	
	@Override
	protected void printInfoMembraneShort(MembraneStructure membraneStructure) {
		if (!(membraneStructure instanceof CellLikeSkinMembrane))
			throw new IllegalArgumentException("Illegal arguments");
		printInfoMembrane((CellLikeSkinMembrane)membraneStructure);
		
	}



	@Override
	protected void removeLeftHandRuleObjects(ChangeableMembrane membrane, IRule r,
			long count) {
		if (!(membrane instanceof CellLikeMembrane))
			throw new IllegalArgumentException("Illegal arguments");
		CellLikeMembrane m= (CellLikeMembrane)membrane;
		LeftHandRule lhr = r.getLeftHandRule();
		if (!m.isSkinMembrane()) {
			CellLikeNoSkinMembrane noSkin = (CellLikeNoSkinMembrane) m;
			noSkin.getParentMembrane().getMultiSet().subtraction(
					lhr.getMultiSet(), count);
		}
		m.getMultiSet().subtraction(lhr.getOuterRuleMembrane().getMultiSet(),
				count);
		ListIterator<InnerRuleMembrane> it = lhr.getOuterRuleMembrane()
				.getInnerRuleMembranes().listIterator();

		while (it.hasNext()) {
			InnerRuleMembrane im = it.next();
			Iterator<CellLikeNoSkinMembrane> it1 = m.getChildMembranes()
					.iterator();
			while (it1.hasNext()) {
				CellLikeNoSkinMembrane m1 = it1.next();
				if (m1.getCharge() == im.getCharge()
						&& m1.getLabel().equals(im.getLabel())) {
					m1.getMultiSet().subtraction(im.getMultiSet(), count);
					break;
				}
			}
		}
	}

	

}
