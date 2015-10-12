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


package org.gcn.plinguacore.util.psystem;

import java.io.Serializable;

/**
 * This class represents a membrane/rule label with optional information about the environment
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 *
 */
public class Label implements Cloneable,Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 382343707864922136L;
	private String labelID="";
	private String environmentID="";
	
	/**
	 * Constructs a label
	 * @param labelID The string for the label
	 */
	public Label(String labelID)
	{
		if (labelID==null)
			throw new NullPointerException("Null label");
		if (labelID.equals(""))
			throw new IllegalArgumentException("Empty label");
		this.labelID=labelID;
	}
	/**
	 * Constructs a label and associates an environment ID for this label.
	 * @param labelID The string for the label
	 * @param environmentID The string for the environment
	 */
	public Label(String labelID,String environmentID)
	{
		this(labelID);
		if (environmentID==null)
			throw new NullPointerException("Null environment");
		if (environmentID.equals(""))
			throw new IllegalArgumentException("Empty environment");
		this.environmentID=environmentID;
	}
	/**
	 * 
	 * @return gets a string which represents the label
	 */
	public String getLabelID() {
		return labelID;
	}

	/**
	 * 
	 * @return gets a string which represents the enviroment ID associated to this label. If there is not any environment ID, then it returns the empty string ""
	 */
	public String getEnvironmentID() {
		return environmentID;
	}


	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((environmentID == null) ? 0 : environmentID.hashCode());
		result = prime * result + ((labelID == null) ? 0 : labelID.hashCode());
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
		Label other = (Label) obj;
		if (environmentID == null) {
			if (other.environmentID != null)
				return false;
		} else if (!environmentID.equals(other.environmentID))
			return false;
		if (labelID == null) {
			if (other.labelID != null)
				return false;
		} else if (!labelID.equals(other.labelID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String str=labelID;
		if (!environmentID.equals(""))
			str+=","+environmentID;
		return str;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		if (!environmentID.equals(""))
			return new Label(getLabelID(),getEnvironmentID());
		else
			return new Label(getLabelID());
	}
	
	

}
