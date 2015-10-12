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

import java.nio.ByteOrder;
import java.util.Collection;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import java.util.Map;
import java.util.Set;


import org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba.DCBAProbabilisticSimulator;
import org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba.EnvironmentRulesBlock;
import org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba.MatrixColumn;
import org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba.SkeletonRulesBlock;
import org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba.StaticMethods;
import org.gcn.plinguacore.util.ByteOrderDataOutputStream;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.AlphabetObject;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeSkinMembrane;



/**
 * This class writes a probabilistic P system in a binary file (under development)
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
class ProbabilisticBinaryOutputParser extends AbstractBinaryOutputParser {

	
	private DCBAProbabilisticSimulator simulator;
	private Map<String,Integer>environments;
	private Map<String,Integer>membranes;
	private Map<String,Integer>alphabet;
	private Map<String,String>parentMembranes;
	private Map<EnvironmentRulesBlock,Integer>environmentRulesBlocks;
	private Map<SkeletonRulesBlock,Integer>skeletonRulesBlocks;
	private int alphabetAccuracy;
	private int environmentsAccuracy;
	private int membranesAccuracy;
	private int skeletonRulesBlocksAccuracy;
	private int environmentRulesBlocksAccuracy;
	
	public ProbabilisticBinaryOutputParser() {
		super();
		environments = new LinkedHashMap<String,Integer>();
		membranes = new LinkedHashMap<String,Integer>();
		parentMembranes = new LinkedHashMap<String,String>();
		alphabet = new LinkedHashMap<String,Integer>();
		environmentRulesBlocks=new LinkedHashMap<EnvironmentRulesBlock,Integer>();
		skeletonRulesBlocks=new LinkedHashMap<SkeletonRulesBlock,Integer>();
		// TODO Auto-generated constructor stub
	}

	

	
	
	private void setAlphabet() throws IOException
	{
		alphabet.clear();
		int i=0;
		for (AlphabetObject object:getPsystem().getAlphabet())
		{
			alphabet.put(object.toString(), i);
			i++;
		}
		alphabetAccuracy = ByteOrderDataOutputStream.getAccuracy(alphabet.size());
	}
	
	private void setMembranes() throws IOException
	{
		membranes.clear();
		parentMembranes.clear();
		CellLikeSkinMembrane skin=(CellLikeSkinMembrane) getPsystem().getMembraneStructure();
		if (skin.getChildMembranes().isEmpty())
			return;
		CellLikeMembrane firstEnvironment = skin.getChildMembranes().iterator().next();
		StaticMethods.getParents(firstEnvironment,parentMembranes);
		int i=1;
		for (String m:parentMembranes.keySet())
		{
			membranes.put(m,i);
			i++;
		}
		membranesAccuracy = ByteOrderDataOutputStream.getAccuracy(membranes.size()+1);
	}
	
	private void setEnvironments() throws IOException
	{
		environments.clear();
		int environmentCounter=0;
		CellLikeSkinMembrane skin=(CellLikeSkinMembrane) getPsystem().getMembraneStructure();
		for (CellLikeMembrane environment:skin.getChildMembranes())
		{
			environments.put(environment.getLabel(), environmentCounter);
			environmentCounter++;
		}
		environmentsAccuracy = ByteOrderDataOutputStream.getAccuracy(environments.size());
	}
	private void setBlocks() throws IOException
	{
		environmentRulesBlocks.clear();
		skeletonRulesBlocks.clear();
		int environmentCounter=0;
		int skeletonCounter=0;
		for (MatrixColumn c:simulator.getStaticMatrix().getColumns())
		{
			if (c.isEnvironmentColumn())
			{
				environmentRulesBlocks.put((EnvironmentRulesBlock)c,environmentCounter);
				environmentCounter++;
			}
			else
			{
				skeletonRulesBlocks.put((SkeletonRulesBlock)c,skeletonCounter);
				skeletonCounter++;
			}
		}
		environmentRulesBlocksAccuracy=ByteOrderDataOutputStream.getAccuracy(environmentRulesBlocks.size());
		skeletonRulesBlocksAccuracy=ByteOrderDataOutputStream.getAccuracy(skeletonRulesBlocks.size());
	}
	
	private void writeSubHeader() throws IOException
	{
		int b1,b2,b3;
		
		b1 = alphabetAccuracy;
		b1 = b1<<2;
		b1 = b1 | environmentsAccuracy;
		b1 = b1<<2;
		b1 = b1 | membranesAccuracy;
		b1 = b1<<2;
		b1 = b1 | skeletonRulesBlocksAccuracy;
		
		b2 = environmentRulesBlocksAccuracy;
		b2 = b2<<4;
		
	
	
	
		
	}	
	
		
	
	private void writeStringCollection(Collection<String>collection,int accuracy) throws IOException
	{
		int n= collection.size();
		getStream().writeNumber(n,accuracy);
		for (String str:collection)
		{
			getStream().writeBytes(str);
			getStream().writeByte(0);
		}
	}
	private void writeAlphabet() throws IOException
	{
		writeStringCollection(alphabet.keySet(),alphabetAccuracy);
	}

	private void writeEnvironments() throws IOException
	{
		writeStringCollection(environments.keySet(),environmentsAccuracy);
	}
	
	private void writeMembranes() throws IOException
	{
		getStream().writeNumber(membranes.size(),membranesAccuracy);
		for (String m:membranes.keySet())
		{
			String parent = parentMembranes.get(m);
			if (membranes.containsKey(parent))
				getStream().writeNumber(membranes.get(parent), membranesAccuracy);
			else
				getStream().writeNumber(0,membranesAccuracy);
			getStream().writeBytes(m);
			getStream().writeByte(0);
		}
	}
	
	@Override
	protected ByteOrder getByteOrder() {
		// TODO Auto-generated method stub
		return ByteOrder.LITTLE_ENDIAN;
	}

	private void writeBlockSizes() throws IOException
	{
		getStream().writeNumber(skeletonRulesBlocks.size(), skeletonRulesBlocksAccuracy);
		getStream().writeNumber(environmentRulesBlocks.size(), environmentRulesBlocksAccuracy);
	}
	
	private static long getMaxMultiplicity(MultiSet<?>ms)
	{
		long max=0;
		for (Object o:ms.entrySet())
		{
			if (ms.count(o)>max)
				max=ms.count(o);
		}
		return max;
	}
	
	private void writeSkeletonBlockInformation()
	{
		for (SkeletonRulesBlock block:skeletonRulesBlocks.keySet())
		{
			long ms1=getMaxMultiplicity(block.getSkeletonLeftHandRule().getMainMultiSet());
			long ms2=getMaxMultiplicity(block.getSkeletonLeftHandRule().getParentMultiSet());
			long max1 = ms1>ms2?ms1:ms2;
			
			ms1 = block.getSkeletonLeftHandRule().getMainMultiSet().entrySet().size();
			ms2 = block.getSkeletonLeftHandRule().getParentMultiSet().entrySet().size();
			long max2 = ms1>ms2?ms1:ms2;
			
			
			
		}
	}
	
	
	private void setSimulator() throws IOException
	{
		try {
			simulator = new DCBAProbabilisticSimulator(getPsystem());
		} catch (PlinguaCoreException e) {
			// TODO Auto-generated catch block
			throw new IOException(e);
		}
	}
	
	
	
	
	@Override
	protected void writeFile() throws IOException {
		// TODO Auto-generated method stub
		setSimulator();
		setAlphabet();
		setEnvironments();
		setMembranes();
		setBlocks();
		writeSubHeader();
		writeAlphabet();
		writeEnvironments();
		writeMembranes();
		writeBlockSizes();
		
		
	}

	@Override
	protected byte getFileId() {
		// TODO Auto-generated method stub
		return 0x21;
	}

	

	
	

}
