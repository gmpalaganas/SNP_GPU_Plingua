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


package org.gcn.plinguacore.util;


import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to handle strings in P-systems. The name, GeneString, is due to the
 * analogy between this strings of symbols and the DNA string (main use of this
 * kind of object in modeling)
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 */
public class GeneString {

	private Pattern pattern; // the pattern that this string represents
	private int numberOfGroups; // number of "?" that this gene string contains
	private String string;
	/**
	 * @param s
	 *      	The string to test
	 * @return true if the given string is a gene string, that is, if it begins
	 *         with "<" and ends with ">"
	 */
	public static boolean isGeneString(String s) {
		return (s.startsWith("<") && s.endsWith(">"));
	}
	
	/**
	 * @param s 
	 * 			A string of the form: "<w1w2...>"
	 * @return the string within the separators: w1w2...
	 */
	public static String getString(String s) {
		return s.substring(1, s.length() - 1);
	}

	
	/**
	 * @param s
	 * 			A gene string, that is, a string of the form:
	 *          "<s1[.s2. ... .sN]>", where s_i can be a string or the symbol
	 *          ?. The symbol ?, which represents any possible string can not
	 *          appear at the beginning or at the end of the gene string. In
	 *          this case, ? will be ignored
	 */
	public GeneString(String s) {
		String [] aux = s.substring(1, s.length() - 1).split("\\.");
		numberOfGroups = 0;
		pattern = Pattern.compile(buildPattern(aux));
		this.string = s;
	}

	
	/*
	 * Builds the pattern used as regular expression to look in a string given
	 * if this gene string is a substring of it. 
	 * For example, given the gene string <aa.?.bb>, the pattern would be
	 * "(.*)?aa\.(.*)?bb(.*)?"
	 * parts is an array of strings that contains the different parts of the gene string
	 * that is, the string splitted by '?' 
	 */
	private String buildPattern(String [] parts) {
		String aux = "(.*?)";
		for (int i=0; i<parts.length; i++) {
			if (i>0 && !parts[i-1].equals("?"))
				// add \. only after symbol ('?' at the beginning of the string are ignored)
				aux += "\\.";
			if (parts[i].equals("?") && i>0 && i<parts.length-1) {
				numberOfGroups++; // numberOfGroups = number of ? in the string
				aux +="(.*?)";
			}
			else if (!parts[i].equals("?"))
				aux +=parts[i];
			
		}
		aux += "(.*?)";
		
		return aux;
	}
	
	/**
	 * @param s
	 * @return true if, and only if, the entire region sequence defined by s matches 
	 * this matcher's pattern
	 */
	public boolean matches(String s) {
		Matcher m = pattern.matcher(s);
		return m.matches();
	}
	
	/**
	 * 
	 * @param content MultiSet with symbols and strings
	 * @return The number of substring in the multiset that this gene string matches
	 */
	public int count(MultiSet<String> content) {
		int res = 0;
		Iterator<String> it = content.entrySet().iterator();
		while (it.hasNext()) {
			String s = it.next();
			if (isGeneString(s)) {
				Matcher m = pattern.matcher(getString(s));
				// the method find() attempts to find the next subsequence 
				// of the input sequence that matches the pattern. 
				int numberOfMatchingInS = 0;
				while (m.find()) 
					numberOfMatchingInS++;
				res += numberOfMatchingInS * content.count(s);
			}
		}
		return res;
	}
	
	/**
	 * Replaces the string s according to this gene string with the
	 * string replacement. 	
	 * @param replacement
	 * 						The replacement string
	 * @param s
	 * 						The string to be replaced
	 * @return The result of replacing s with replacement according to the pattern
	 * defined by this gene string.
	 * 
	 * For example, if this Gene String is "<aa.?.bb>", the given string to be
	 * replaced is "aa.ccc.bb.ccc", which obviously matches the gene string's 
	 * pattern, and the replacement string is "dd.?.dd", the result would be
	 * "dd.ccc.dd.ccc"
	 * 
	 * That is, the substring "aa" is replaced with "dd". After that, an arbitrary
	 * string ? comes and is maintained until we find the substring "bb", which
	 * is replaced with "dd". The rest of the string to be replaced is not modified.
	 */
	public String replace(String replacement, String s) {
		String res = s; // nothing has been replaced yet
		Matcher m = pattern.matcher(s); 
		// if the replacement string has ?, that means that there may be substring
		// that are not going to be modified
		String [] aux = replacement.split("\\?.");
		// m.groupCount() is the number of groups in which the string to be
		// replaced is divided by the matcher
		// for example, if the gene string is "<aa.?.bb>", the pattern will
		// be (.*?)aa.(.*?).bb(.*?). If the string s is "aa.ccc.bb.ccc"
		// the groups will be "ccc" (matches with (.*?) and "ccc" at the end (which
		// also matches with (.*?)). We need to keep the groups because they
		// represent the substring that have to be "replacement-safe"
		if (m.matches() && (aux.length==1 || m.groupCount() == aux.length + 1)) {
			res = m.group(1);
			if (aux.length == 1) {
				if (aux[0].isEmpty() && m.group(m.groupCount()).startsWith("."))
					res += aux[0] + m.group(m.groupCount()).substring(1);
				else
					res += aux[0] + m.group(m.groupCount());
			}
			else 
				for (int i=0; i<aux.length; i++) {
					res += aux[i];
					if ((i+2) <= m.groupCount())
						res += m.group(i+2);
				}
		}
		return res;
	}

	/**
	 * @return the number of ? that this gene string contains 
	 */
	public int getNumberOfGroups() {
		return numberOfGroups;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return string;
	}
	
	

}
