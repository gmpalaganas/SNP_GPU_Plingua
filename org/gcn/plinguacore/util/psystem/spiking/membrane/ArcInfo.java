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

package org.gcn.plinguacore.util.psystem.spiking.membrane;

import java.util.ArrayList;
import java.util.List;


public class ArcInfo {

	// Attributes
	
	private Long weight = 1L;
	private boolean inhibitory = false;	// this attribute is used to turn spikes into anti-spikes
	
	private List<String> astList = null;	// should be converted to a Map<String,Long> to implement budding & division
	private List<String> astCtrlList = null;
	private String astType = null;
	private Long spikesInput = 0L;
	private Long spikesOutput = 0L;
	private boolean inhibited = false;
	
	private Integer sourceId = -1;
	private Integer targetId = -1;
	
	public ArcInfo(Integer sourceId, Integer targetId)
	{
		
		if(sourceId >= 0 && targetId >= 0)
		{
			this.setSourceId(sourceId);
			this.setTargetId(targetId);
			this.astList = new ArrayList<String>();
			this.astCtrlList = new ArrayList<String>();
			this.astType = new String("none");
		}
		else
			throw new IllegalArgumentException("Membrane Identifiers must be equal or greater than zero.");
		
		
	}
	
	public ArcInfo(Integer sourceId, Integer targetId, ArcInfo a)
	{
		this.setSourceId(sourceId);
		this.setTargetId(targetId);
		this.weight = a.weight;
		this.inhibitory = a.inhibitory;
		this.astType = a.astType;

		this.astList = a.astList;
		this.astCtrlList = a.astCtrlList;

		// we can assign these attributes safely since the clone is done when the original arc is not involved in a firing
		
		this.spikesInput = a.spikesInput;
		this.spikesOutput = a.spikesOutput;
		this.inhibited = a.inhibited;

		
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	public Integer getSourceId() {
		return sourceId;
	}

	public void setTargetId(Integer targetId) {
		this.targetId = targetId;
	}

	public Integer getTargetId() {
		return targetId;
	}
	
	public Long getSpikesInput() {
		return spikesInput;
	}

	public void setSpikesInput(Long spikesInput) {
		this.spikesInput = spikesInput;
	}

	public Long getSpikesOutput() {
		return spikesOutput;
	}

	public void setSpikesOutput(Long spikesOutput) {
		this.spikesOutput = spikesOutput;
	}

	public boolean getInhibited() {
		return inhibited;
	}

	public void setInhibited(boolean inhibited) {
		this.inhibited = inhibited;
	}

	
	public void setAstrocyteList(List<String> astList) {
		this.astList = astList;
	}

	public List<String> getAstrocyteList() {
		return this.astList;
	}
	
	public void setAstrocyteCtrlList(List<String> astCtrlList) {
		this.astCtrlList = astCtrlList;
	}

	public List<String> getAstrocyteCtrlList() {
		return this.astCtrlList;
	}

	public String getAstType()
	{
		return astType;
	}
	
	public void setAstType(String astType)
	{
		this.astType = astType;
	}
	
	public void setWeight(Long weight) {
		this.weight = weight;
	}

	public Long getWeight() {
		return weight;
	}
	
	public void setInhibitory(boolean inhibitory) {
		this.inhibitory = inhibitory;
	}

	public boolean getInhibitory() {
		return inhibitory;
	}
	

	@Override
	public String toString()
	{
		String str = new String("");
		
		str += "<" + "edges=" + "<" + sourceId + "," + targetId + ">" + "," + "w=" + weight + "," + "ast=" + astList + ">";
		
		return str;
		
	}

	
}
