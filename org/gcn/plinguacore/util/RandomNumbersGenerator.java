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

import java.util.Date;

import cern.jet.random.Binomial;
import cern.jet.random.engine.MersenneTwister;

public class RandomNumbersGenerator extends MersenneTwister {

	/**
	 * 
	 */
	private static RandomNumbersGenerator singleton=null;
	private Binomial binomial=null;
	private static final long serialVersionUID = -2655083177269543028L;

	private RandomNumbersGenerator() {
		super(new Date());
	
		// TODO Auto-generated constructor stub
	}
	
	public static final RandomNumbersGenerator getInstance()
	{
		if (singleton==null)
			singleton = new RandomNumbersGenerator();
		return singleton;
	}

	
	public synchronized long nextLong(long n)
	{
		 if (n<=0)
				throw new IllegalArgumentException("n must be positive");

		  long val = nextLong();
		  if (val==Long.MIN_VALUE)
			  val--;
		   return Math.abs(val)%n;

	}
	
	public synchronized int nextInt(int n)
	{
		  if (n<=0)
				throw new IllegalArgumentException("n must be positive");

		  int val = nextInt();
		  if (val==Integer.MIN_VALUE)
			  val--;
		   return Math.abs(val)%n;

	}
	
	public synchronized int nextIntBi(int n,double p)
	{
		
		if (n==0)
			return 0;
		
		if (binomial==null)
			binomial=new Binomial(Integer.MAX_VALUE,p,this);
		
		return binomial.nextInt(n,p);
		
		
	
		
	}
	
	
	public synchronized long nextLongBi(long n,double p)
	{
		
		if (n==0)
			return 0;
		
		if (binomial==null)
			binomial=new Binomial(Integer.MAX_VALUE,p,this);
		
		if (n<=Integer.MAX_VALUE)
			return binomial.nextInt((int)n,p);
		
		
		int m =(int) (n / Integer.MAX_VALUE);
		int b = binomial.nextInt(Integer.MAX_VALUE,p);
	
		return b*m; 
		
	}

}
