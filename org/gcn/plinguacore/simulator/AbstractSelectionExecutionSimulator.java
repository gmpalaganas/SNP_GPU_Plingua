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

package org.gcn.plinguacore.simulator;


import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.gcn.plinguacore.util.HashMultiSet;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.Pair;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.Configuration;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.membrane.MembraneStructure;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoEvolution;

/**
 * An abstract class for simulators which execute simulation steps in three microsteps:
 * init step, select rules for the whole configuration, execute rules for the whole configuration
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */

public abstract class AbstractSelectionExecutionSimulator extends AbstractSimulator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1997974622465213429L;
	private boolean firstTime = true;
	
	private Map<Integer, Pair<ChangeableMembrane, MultiSet<Object>>> selectedRules;
	


	protected static final CheckRule noEvolution = new NoEvolution();
	

	public AbstractSelectionExecutionSimulator(Psystem psystem) {
		super(psystem);
		selectedRules = new LinkedHashMap<Integer, Pair<ChangeableMembrane, MultiSet<Object>>>();

		// TODO Auto-generated constructor stub
	}
	
	protected final Map<Integer, Pair<ChangeableMembrane, MultiSet<Object>>> getSelectedRules() {
		return selectedRules;
	}

	protected abstract String getHead(ChangeableMembrane m);
	
	/**
	 * Print short information in the info-channel about current configuration
	 * 
	 * @param first
	 *            It is true if this is the first configuration
	 */
	private void printInfoShort(boolean first) {
		if (!selectedRules.isEmpty() || first) {

			getInfoChannel().println(
					"***********************************************");
			getInfoChannel().println();
			getInfoChannel().println(
					"    CONFIGURATION: " + (currentConfig.getNumber()));
			if (isTimed()) {
				long mem = Runtime.getRuntime().maxMemory()
						- Runtime.getRuntime().freeMemory();
				mem = mem / 1024;
				getInfoChannel().println("    TIME: " + getTime() + " s.");
				getInfoChannel().println("    MEMORY: " + mem + " Kb");
			}
			getInfoChannel().println();

			
			printInfoMembraneShort(currentConfig.getMembraneStructure());
			if (!currentConfig.getEnvironment().isEmpty()) {
				getInfoChannel().println(
						"    ENVIRONMENT: " + currentConfig.getEnvironment());
				getInfoChannel().println();
			}
		} else {
			Iterator<? extends Membrane> it = currentConfig.getMembraneStructure().getAllMembranes()
					.iterator();
			while (it.hasNext())
				printInfoMembrane((ChangeableMembrane)it.next());
			if (!currentConfig.getEnvironment().isEmpty()) {
				getInfoChannel().println(
						"    ENVIRONMENT: " + currentConfig.getEnvironment());
				getInfoChannel().println();
			}
			getInfoChannel()
					.println(
							"Halting configuration (No rule can be selected to be executed in the next step)");
		}

	}
	

	/**
	 * Print large information in the info-channel about current configuration
	 * 
	 * @param first
	 *            It is true if this is the first configuration
	 */
	private void printInfo(boolean first) {
		if (hasSelectedRules() || first) {
			if (!first) {
				getInfoChannel().println(
						"-----------------------------------------------");
				getInfoChannel().println();
				getInfoChannel().println(
						"    STEP: " + currentConfig.getNumber());

			}
			Iterator<Pair<ChangeableMembrane, MultiSet<Object>>> it1 = selectedRules
					.values().iterator();

			while (it1.hasNext()) {
				Pair<ChangeableMembrane, MultiSet<Object>> pair = it1.next();
				MultiSet<Object> rules = pair.getSecond();
				Iterator<Object> it = rules.entrySet().iterator();
				if (it.hasNext()) {
					getInfoChannel().println();
					getInfoChannel().println(
							"    Rules selected for "
									+ getHead(pair.getFirst()));

				}
				while (it.hasNext()) {
					Object r = it.next();
					getInfoChannel().println(
							"    " + rules.count(r) + " * " + r.toString());
				}
			}
			getInfoChannel().println();
			getInfoChannel().println(
					"***********************************************");
			getInfoChannel().println();
			getInfoChannel().println(
					"    CONFIGURATION: " + (currentConfig.getNumber()));
			if (isTimed()) {
				getInfoChannel().println("    TIME: " + getTime() + " s.");
				getInfoChannel().println(
						"    MEMORY USED: "
								+ Runtime.getRuntime().totalMemory() / 1024);
				getInfoChannel().println(
						"    FREE MEMORY: " + Runtime.getRuntime().freeMemory()
								/ 1024);
				getInfoChannel().println(
						"    TOTAL MEMORY: " + Runtime.getRuntime().maxMemory()
								/ 1024);
			}
			getInfoChannel().println();

			Iterator<? extends Membrane> it = currentConfig.getMembraneStructure().getAllMembranes()
					.iterator();
			while (it.hasNext())
				printInfoMembrane((ChangeableMembrane)it.next());
			if (!currentConfig.getEnvironment().isEmpty()) {
				getInfoChannel().println(
						"    ENVIRONMENT: " + currentConfig.getEnvironment());
				getInfoChannel().println();
			}
		} else {
			getInfoChannel()
					.println(
							"Halting configuration (No rule can be selected to be executed in the next step)");
		}

	}
	@Override
	public void reset() {
		super.reset();
		if (getVerbosity()>0)
		{
		if (getVerbosity() > 1)
			printInfo(true);
		else
			printInfoShort(true);
		}
	}
	@Override
	protected final boolean specificStep() throws PlinguaCoreException {

		if (firstTime) {
			if (getVerbosity()>0)
			{
			if (getVerbosity() > 1)
				printInfo(true);
			else
				printInfoShort(true);
			}
			firstTime = false;
		}
		microStepInit();
		microStepSelectRules();
		
		if (hasSelectedRules()) {
			microStepExecuteRules();
			//currentConfig = (Configuration)currentConfig.clone();
			currentConfig.setNumber(currentConfig.getNumber()+1);
		}
		if (getVerbosity()>0)
		{
		if (getVerbosity() > 1)
			printInfo(false);
		else
			printInfoShort(false);
		}
		return hasSelectedRules();
	}
	
	protected boolean hasSelectedRules()
	{
		return !selectedRules.isEmpty();
	}
	
	protected void executeRule(IRule r,ChangeableMembrane m,MultiSet<String>environment,long count)
	{
		
		r.execute(m,environment,count);
	}
	
	protected void microStepExecuteRules() {

		Iterator<Pair<ChangeableMembrane, MultiSet<Object>>> it = selectedRules
				.values().iterator();
		MultiSet<IRule> ms1 = new HashMultiSet<IRule>();
		MultiSet<IRule> ms2 = new HashMultiSet<IRule>();
		while (it.hasNext()) {
			Pair<ChangeableMembrane, MultiSet<Object>> p = it.next();
			
			MultiSet<Object> ms = p.getSecond();
			ChangeableMembrane m = p.getFirst();
			ms1.clear();
			ms2.clear();
			Iterator<Object> it1 = ms.entrySet().iterator();

			while (it1.hasNext()) {
				Object o = it1.next();
				if (o instanceof IRule)
				{
				IRule r = (IRule)o;
				if (r.dissolves())
				{
					ms2.add(r);
				}
				else
				if (noEvolution.checkRule(r))
				{
					ms1.add(r, ms.count(r));
					//System.out.println("REGLA: "+r.toString());
				}
				else
					executeRule(r,m,currentConfig.getEnvironment(),ms.count(r));
				}
			}
			Iterator<IRule>it2= ms1.entrySet().iterator();
			while (it2.hasNext()) {
				IRule r = it2.next();
				executeRule(r,m,currentConfig.getEnvironment(),ms1.count(r));
				
			}
			it2 = ms2.entrySet().iterator();
			
			while (it2.hasNext()) {
				IRule r = it2.next();
				executeRule(r,m,currentConfig.getEnvironment(),ms2.count(r));
				
			}

		}
		
	}
	

	public void selectRule(Object r, ChangeableMembrane m, long count) {
		Pair<ChangeableMembrane, MultiSet<Object>> p;
		if (selectedRules.containsKey(m.getId()))
			p = selectedRules.get(m.getId());
		else {
			p = new Pair<ChangeableMembrane, MultiSet<Object>>(m,
					new HashMultiSet<Object>());
			selectedRules.put(m.getId(), p);
		}
		p.getSecond().add(r, count);
	}
	
	
	
	protected void microStepSelectRules() throws PlinguaCoreException {

		microStepSelectRules(currentConfig,(Configuration)currentConfig.clone());

	}
	
	
	
	protected void microStepSelectRules(Configuration cnf, Configuration tmpCnf)
	{
		Iterator<? extends Membrane> it = tmpCnf.getMembraneStructure().getAllMembranes()
		.iterator();
		Iterator<? extends Membrane> it1 = cnf.getMembraneStructure().getAllMembranes().iterator();
		while (it.hasNext()) {
			ChangeableMembrane tempMembrane = (ChangeableMembrane) it.next();
			ChangeableMembrane m = (ChangeableMembrane)it1.next();
			microStepSelectRules(m,tempMembrane);

		}
	}
	
	protected void microStepInit() {

		selectedRules.clear();
		initDate();
	}

	/**
	 * Select rules for a specific membrane
	 * 
	 * @param m
	 *            A membrane
	 */
	protected abstract void microStepSelectRules(ChangeableMembrane membrane,
			ChangeableMembrane tempMembrane);

	
	protected abstract void printInfoMembrane(ChangeableMembrane membrane);
	protected abstract void printInfoMembraneShort(MembraneStructure membranes);
	
	protected abstract void removeLeftHandRuleObjects(ChangeableMembrane membrane,
			IRule r, long count);

}
