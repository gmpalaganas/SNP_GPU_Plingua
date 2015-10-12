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

package org.gcn.plinguacore.parser.input.plingua;

import java.util.List;

import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.Label;
import org.gcn.plinguacore.util.psystem.rule.InnerRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.OuterRuleMembrane;

class OuterRuleMembraneWithDissolutionOption extends OuterRuleMembrane{

   
	/**
	 * 
	 */
	private static final long serialVersionUID = 1814777767095511544L;
	private boolean dissolves=false;

    public OuterRuleMembraneWithDissolutionOption(Label label, byte charge,boolean dissolves) {
            super(label, charge);
            this.dissolves=dissolves;
    }


    public OuterRuleMembraneWithDissolutionOption(Label label, byte charge, MultiSet<String> multiset, List<InnerRuleMembrane> innerMembranes,boolean dissolves) {
            super(label, charge, multiset, innerMembranes);
            this.dissolves=dissolves;
    }


    public boolean dissolves(){
            return dissolves;
    }
}
