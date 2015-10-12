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


package org.gcn.plinguacore.parser.output.binary;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import java.util.List;



import org.gcn.plinguacore.util.ByteOrderDataOutputStream;
import org.gcn.plinguacore.util.HashMultiSet;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.AlphabetObject;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeNoSkinMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeSkinMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoDissolution;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoDivision;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoEvolution;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoLeftExternalMultiSet;

/**
 * This class writes an active membranes P system in a binary file
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
class AMBinaryOutputParser extends AbstractBinaryOutputParser {


	
	private Map<String, Integer> alphabet = null;
	private Map<String, Integer> membranes = null;
	private Map<Integer, MultiSet<String>> multiSets = null;
	

	private List<IRule> evolutionRules = null;
	private List<IRule> sendInRules = null;
	private List<IRule> sendOutRules = null;
	private List<IRule> divisionRules = null;
	private List<IRule> dissolutionRules = null;

	private static final CheckRule noEvolutionRule = new NoEvolution(
			);

	private static final CheckRule noDivisionRule = new NoDivision(
			);

	private static final CheckRule noDissolutionRule = new NoDissolution(
			);

	private static final CheckRule noLeftExternalMultiSet = new NoLeftExternalMultiSet(
			);

	public AMBinaryOutputParser() {
	
		
		multiSets = new LinkedHashMap<Integer, MultiSet<String>>();
		alphabet = new LinkedHashMap<String, Integer>();
		membranes = new LinkedHashMap<String, Integer>();
		evolutionRules = new ArrayList<IRule>();
		sendInRules = new ArrayList<IRule>();
		sendOutRules = new ArrayList<IRule>();
		divisionRules = new ArrayList<IRule>();
		dissolutionRules = new ArrayList<IRule>();
	}

	
	

	private void readRules() {
		evolutionRules.clear();
		sendInRules.clear();
		sendOutRules.clear();
		divisionRules.clear();
		dissolutionRules.clear();

		Iterator<IRule> it = getPsystem().getRules().iterator();

		while (it.hasNext()) {
			IRule r = it.next();

			if (!noEvolutionRule.checkRule(r))
				evolutionRules.add(r);
			else if (!noDivisionRule.checkRule(r))
				divisionRules.add(r);
			else if (!noDissolutionRule.checkRule(r))
				dissolutionRules.add(r);
			else if (noLeftExternalMultiSet.checkRule(r))
				sendOutRules.add(r);
			else
				sendInRules.add(r);

		}
	}
	
	
	

	private void writeSendInRules() throws IOException {
		getStream().writeChar(sendInRules.size());
		Iterator<IRule> it = sendInRules.iterator();
		while(it.hasNext())
		{
			IRule r = it.next();
			writeRuleHead(r);
			writeCharge(r.getRightHandRule().getOuterRuleMembrane().getCharge());
			writeObjects(r.getLeftHandRule().getMultiSet(),false,true);
			writeObjects(r.getRightHandRule().getOuterRuleMembrane().getMultiSet(),false,true);
		}
	}
	
	private void writeDissolutionRules() throws IOException {
		getStream().writeChar(dissolutionRules.size());
		Iterator<IRule> it = dissolutionRules.iterator();
		while(it.hasNext())
		{
			IRule r = it.next();
			writeRuleHead(r);
			writeObjects(r.getLeftHandRule().getOuterRuleMembrane().getMultiSet(),false,true);
			if (!r.getRightHandRule().getMultiSet().isEmpty())
				writeObjects(r.getRightHandRule().getMultiSet(),false,true);
			else
				writeObjects(r.getRightHandRule().getOuterRuleMembrane().getMultiSet(),false,true);
		}
	}
	
	private void writeDivisionRules() throws IOException {
		getStream().writeChar(divisionRules.size());
		Iterator<IRule> it = divisionRules.iterator();
		while(it.hasNext())
		{
			IRule r = it.next();
			writeRuleHead(r);
			writeCharge(r.getRightHandRule().getOuterRuleMembrane().getCharge());
			writeCharge(r.getRightHandRule().getSecondOuterRuleMembrane().getCharge());
			writeObjects(r.getLeftHandRule().getOuterRuleMembrane().getMultiSet(),false,true);
			writeObjects(r.getRightHandRule().getOuterRuleMembrane().getMultiSet(),false,true);
			writeObjects(r.getRightHandRule().getSecondOuterRuleMembrane().getMultiSet(),false,true);
		}
	}
	
	
	private void writeSendOutRules() throws IOException {
		getStream().writeChar(sendOutRules.size());
		Iterator<IRule> it = sendOutRules.iterator();
		while(it.hasNext())
		{
			IRule r = it.next();
			writeRuleHead(r);
			writeCharge(r.getRightHandRule().getOuterRuleMembrane().getCharge());
			writeObjects(r.getLeftHandRule().getOuterRuleMembrane().getMultiSet(),false,true);
			writeObjects(r.getRightHandRule().getMultiSet(),false,true);
		}
	}
	
	private void writeEvolutionRules() throws IOException {
		getStream().writeChar(evolutionRules.size());
		Iterator<IRule> it = evolutionRules.iterator();
		while (it.hasNext()) {
			IRule r = it.next();
			writeRuleHead(r);
			writeObjects(r.getLeftHandRule().getOuterRuleMembrane().getMultiSet(),false,true);
			getStream().writeChar(r.getRightHandRule().getOuterRuleMembrane().getMultiSet().entrySet().size());
			writeObjects(r.getRightHandRule().getOuterRuleMembrane().getMultiSet(),true,false);
		}
	}

	protected void writeRules() throws IOException {
		readRules();
		writeEvolutionRules();
		writeSendInRules();
		writeSendOutRules();
		writeDissolutionRules();
		writeDivisionRules();
	}






	@Override
	protected ByteOrder getByteOrder() {
		// TODO Auto-generated method stub
		return ByteOrder.BIG_ENDIAN;
	}

	
	
	private void writeAlphabet() throws IOException {
		alphabet.clear();
		int n = getPsystem().getAlphabet().size();
		getStream().writeChar(n+1);
		alphabet.put("#", 0);
		getStream().writeBytes("#");
		getStream().writeByte(0);
		Iterator<AlphabetObject> it = getPsystem().getAlphabet().iterator();
		int i = 1;
		while (it.hasNext()) {
			String obj = it.next().toString();
			if (!alphabet.containsKey(obj)) {
				alphabet.put(obj, i);
				i++;
				getStream().writeBytes(obj);
				getStream().writeByte(0);
			}
		}

	}
	
	@Override
	protected void writeFile() throws IOException{
		// TODO Auto-generated method stub
		writeAlphabet();
		writeLabels();
		writeMembranes();
		writeMultiSets();
		writeRules();
		
	}




	@Override
	protected byte getFileId() {
		// TODO Auto-generated method stub
		return 0x11;
	}




	
	
	protected void writeLabels() throws IOException {
		membranes.clear();
		Iterator<? extends Membrane> it = getPsystem().getMembraneStructure().getAllMembranes().iterator();
		int i = 0;
		while (it.hasNext()) {
			String label = it.next().getLabelObj().toString();
			if (!membranes.containsKey(label)) {
				membranes.put(label, i);
				i++;
			}
		}
		getStream().writeChar(membranes.size());
		Iterator<String> it1 = membranes.keySet().iterator();
		while (it1.hasNext()) {
			getStream().writeBytes(it1.next());
			getStream().writeByte(0);
		}

	}
	
	protected void writeMembranes() throws IOException {
		int n = getPsystem().getMembraneStructure().getAllMembranes().size();
		multiSets.clear();
		getStream().writeChar(n);
		CellLikeSkinMembrane skin = (CellLikeSkinMembrane)getPsystem().getMembraneStructure();
			
		
		if (skin != null)
			writeMembrane(skin);

	}
	protected void writeId(int id, int size) throws IOException {
		if (size > 0xFF)
			getStream().writeChar(id);
		else
			getStream().writeByte(id);
	}
	
	protected void writeCharge(byte charge) throws IOException
	{
		if (charge == 0)
			getStream().writeByte(0);
		else if (charge > 0)
			getStream().writeByte(1);
		else
			getStream().writeByte(2);

	}
	
	protected void writeObject(String obj) throws IOException
	{
		int id = alphabet.get(obj);
		writeObjectId(id);
	}
	

	protected void writeObjects(MultiSet<String> objs,boolean withMultiplicity,boolean forced) throws IOException
	{
		if (objs.size()==0 && forced)
		{
			writeObject("#");
			if (withMultiplicity)
				getStream().writeChar(1);
		}
		else
		{
			Iterator<String> it=objs.entrySet().iterator();
			while(it.hasNext())
			{
				String obj = it.next();
				writeObject(obj);
				if (withMultiplicity)
					getStream().writeChar((int)objs.count(obj));
			}
		}
	}
	
	
	
	protected void writeRuleHead(IRule r) throws IOException
	{
		writeLabelId(membranes.get(r.getLeftHandRule().getOuterRuleMembrane()
				.getLabel()));
		writeCharge(r.getLeftHandRule().getOuterRuleMembrane().getCharge());
	}

	
	protected void writeMembraneId(int id) throws IOException {
		writeId(id, getPsystem().getMembraneStructure().getAllMembranes().size());
	}

	protected void writeLabelId(int id) throws IOException {
		writeId(id, membranes.size());
	}

	protected void writeObjectId(int id) throws IOException {
		writeId(id, alphabet.size());
	}
	
	private void writeMembrane(CellLikeMembrane membrane) throws IOException {
		if (membrane instanceof CellLikeSkinMembrane)
			writeMembraneId(0);
		else
			writeMembraneId(((CellLikeNoSkinMembrane) membrane)
					.getParentMembrane().getId());
		
		String labelWithEnvironment = membrane.getLabelObj().toString();
		String label = membrane.getLabel();
		MultiSet<String> initMs = new HashMultiSet<String>();
		initMs.addAll(membrane.getMultiSet());
		
		if (getPsystem().getInitialMultiSets().get(label)!=null && 
				!getPsystem().getInitialMultiSets().get(label).isEmpty())
			initMs.addAll(getPsystem().getInitialMultiSets().get(label));
	
		if (!initMs.isEmpty())
			multiSets.put(membrane.getId(), initMs);
		
		writeLabelId(membranes.get(labelWithEnvironment));
		if (membrane.getCharge() == 0)
			getStream().writeByte(0);
		else if (membrane.getCharge() > 0)
			getStream().writeByte(1);
		else
			getStream().writeByte(2);
		Iterator<CellLikeNoSkinMembrane> it = membrane.getChildMembranes()
				.iterator();
		while (it.hasNext())
			writeMembrane(it.next());

	}

	
	protected void writeMultiSets() throws IOException {
		getStream().writeChar(multiSets.size());
		Iterator<Integer> it = multiSets.keySet().iterator();
		while (it.hasNext()) {
			int id = it.next();
			writeMembraneId(id);
			MultiSet<String> ms = multiSets.get(id);
			getStream().writeChar(ms.entrySet().size());
			Iterator<String> it1 = ms.entrySet().iterator();
			while (it1.hasNext()) {
				String obj = it1.next();
				long mul = ms.count(obj);
				int objId = alphabet.get(obj);
				writeObjectId(objId);
				getStream().writeChar((int)mul);
			}
		}
	}

	
	
	

}
