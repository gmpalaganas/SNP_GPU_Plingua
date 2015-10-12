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
 * This class represents a specific object within the alphabet of a P system
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 *
 */
public class AlphabetObject implements Comparable<AlphabetObject>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4367150243594694785L;

	private String name;

	private int[] indexes;

	private String strObj;

	public AlphabetObject(String object) {
		if (object == null)
			throw new NullPointerException(
					"Object constructor argument shouldn't be null");
		if (object.equals(""))
			throw new IllegalArgumentException(
					"Object constructor argument shouldn't be empty");

		strObj = "";
		int i = object.indexOf("{");
		if (i != -1) {
			int j = object.indexOf("}");
			if (j == object.length() - 1) {
				strObj = "{";
				name = object.substring(0, i);
				String substring = object.substring(i + 1, j);
				String[] strIndexes = substring.split(",");
				indexes = new int[strIndexes.length];
				for (int k = 0; k < strIndexes.length; k++) {
					indexes[k] = Integer.parseInt(strIndexes[k]);
					strObj += indexes[k];
					if (k + 1 < strIndexes.length)
						strObj += ",";

				}
				strObj += "}";

			} else
				name = object;
		} else
			name = object;
		if (indexes == null)
			indexes = new int[0];
		strObj = name + strObj;
	}

	/**
	 * 
	 * @return The name of the object. For instance, the name of "a{1,2}" is "a"
	 */
	public String getName() {
		return name;
	}
	/**
	 * 
	 * @return The indexes of the object. For instance, the indexes of "a{1,2}" is "1,2"
	 */
	public int[] getIndexes() {
		return indexes;
	}

	@Override
	public int compareTo(AlphabetObject arg0) {
		if (arg0 == null)
			return 1;
		if (this == arg0)
			return 0;
		final AlphabetObject other = arg0;
		if (indexes.length < other.indexes.length)
			return -1;
		if (indexes.length > other.indexes.length)
			return 1;
		int cn = name.compareTo(other.name);
		if (cn != 0)
			return cn;
		int c = 0;
		int i = 0;
		while (i < indexes.length && c == 0) {
			if (indexes[i] < other.indexes[i])
				c = -1;
			else if (indexes[i] > other.indexes[i])
				c = 1;
			i++;
		}

		return c;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((strObj == null) ? 0 : strObj.hashCode());
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
		final AlphabetObject other = (AlphabetObject) obj;
		if (strObj == null) {
			if (other.strObj != null)
				return false;
		} else if (!strObj.equals(other.strObj))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return strObj;

	}

}
