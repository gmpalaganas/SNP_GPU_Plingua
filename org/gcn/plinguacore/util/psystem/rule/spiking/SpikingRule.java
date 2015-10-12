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

package org.gcn.plinguacore.util.psystem.rule.spiking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.gcn.plinguacore.util.HashMultiSet;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.Pair;
import org.gcn.plinguacore.util.psystem.Configuration;
import org.gcn.plinguacore.util.psystem.Label;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.rule.AbstractRule;
import org.gcn.plinguacore.util.psystem.rule.HandRule;
import org.gcn.plinguacore.util.psystem.rule.InnerRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.LeftHandRule;
import org.gcn.plinguacore.util.psystem.rule.OuterRuleMembrane;
import org.gcn.plinguacore.util.psystem.rule.RightHandRule;
import org.gcn.plinguacore.util.psystem.spiking.membrane.ArcInfo;
import org.gcn.plinguacore.util.psystem.spiking.membrane.Astrocyte;
import org.gcn.plinguacore.util.psystem.spiking.membrane.SpikingMembrane;
import org.gcn.plinguacore.util.psystem.spiking.membrane.SpikingMembraneStructure;
import org.gcn.plinguacore.util.psystem.spiking.SpikingConstants;


public final class SpikingRule extends AbstractRule  {

	private static final long serialVersionUID = 4165214584338096187L;

	
		
	private Pattern regExp = null;
	private long delay = 0L;
	

	
	private static String noBuddingDivision = new String("none");
	private static String budding = new String("budding");
	private static String division = new String("division");
	
	// The next attributes are considered only for efficiency reasons

	private boolean efficiencyAttributesProcessed = false;
	private boolean isFiring = false;
	private boolean isForgetting = false;
	private boolean isBudding = false;
	private boolean isDivision = false;
	private String buddingdivision = noBuddingDivision;
	private String ruleSpikingString = null;
	private long ruleSpikingStringSize = 0;
	private long rightHandRuleSpikingStringSize = 0;
	
	private String leftObject = null;
	private String rightObject = null;
	
	// Builder for any rule (spiking, budding, division) by using LeftHandRule y RightHandRule
	// (necessary for the parser)
	
	public static SpikingRule buildRule(LeftHandRule lhr, RightHandRule rhr, String regExp, long delay, String type,boolean computeEfficiencyAtributes)
	{
		if (lhr==null)
			throw new NullPointerException("Null LeftHandRule");
		if (rhr==null)
			throw new NullPointerException("Null RightHandRule");
		
		if (!lhr.getMultiSet().isEmpty() || !rhr.getMultiSet().isEmpty())
			throw new IllegalArgumentException("External rule objects are not allowed");
		
		if (lhr.getOuterRuleMembrane().getCharge()!=0 || rhr.getOuterRuleMembrane().getCharge()!=0)
			throw new IllegalArgumentException("Charges are not allowed");
		
		if (lhr.getOuterRuleMembrane().getMultiSet().entrySet().size()>1 ||
				rhr.getOuterRuleMembrane().getMultiSet().entrySet().size()>1)
			//throw new IllegalArgumentException("The only allowed symbol is the spike symbol (a)");
			throw new IllegalArgumentException("Only a symbol type is allowed per rule side (" + SpikingConstants.spikeSymbol + "," + SpikingConstants.antiSpikeSymbol +")");
			
		
		if (rhr.getSecondOuterRuleMembrane()!=null)
		{
			if (rhr.getSecondOuterRuleMembrane().getCharge()!=0)
				throw new IllegalArgumentException("Charges are not allowed");
			
			//if (rhr.getSecondOuterRuleMembrane().getMultiSet().entrySet().size()>1)
				//throw new IllegalArgumentException("The only allowed symbol is the spike symbol (a)");
			
			if (lhr.getOuterRuleMembrane().getMultiSet().entrySet().size()>0)	
				throw new IllegalArgumentException("No symbols allowed the left hand side of a rule of this kind");
			
			if (rhr.getOuterRuleMembrane().getMultiSet().entrySet().size()>0)	
				throw new IllegalArgumentException("No symbols allowed the left hand side of a rule of this kind");
			
			if (rhr.getSecondOuterRuleMembrane().getMultiSet().entrySet().size()>0)	
				throw new IllegalArgumentException("No symbols allowed the left hand side of a rule of this kind");
			
		
			return buildRule(lhr.getOuterRuleMembrane().getLabel(),
					rhr.getOuterRuleMembrane().getLabel(),
					rhr.getSecondOuterRuleMembrane().getLabel(),
					type,
					regExp,
					computeEfficiencyAtributes);
		}
		else
		{
			if (!lhr.getOuterRuleMembrane().getLabel().equals(rhr.getOuterRuleMembrane().getLabel()))
				throw new IllegalArgumentException("Labels in left-hand-rule and right-hand-rule must be equal");
			
			long leftSpikesMultiplicity = lhr.getOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol);
			long leftAntiSpikesMultiplicity = lhr.getOuterRuleMembrane().getMultiSet().count(SpikingConstants.antiSpikeSymbol);
			long rightSpikesMultiplicity = rhr.getOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol);
			long rightAntiSpikesMultiplicity = rhr.getOuterRuleMembrane().getMultiSet().count(SpikingConstants.antiSpikeSymbol);
			
			String lObject = null;
			String rObject = null;
			long leftObjectMultiplicity = 0L;
			long rightObjectMultiplicity = 0L;
			
			if(leftSpikesMultiplicity == 0L && leftAntiSpikesMultiplicity > 0L)
			{
				lObject = SpikingConstants.antiSpikeSymbol;
				leftObjectMultiplicity = leftAntiSpikesMultiplicity;
			}
			else
			{
				lObject = SpikingConstants.spikeSymbol;
				leftObjectMultiplicity = leftSpikesMultiplicity;
			}
			
			if(rightSpikesMultiplicity == 0L && rightAntiSpikesMultiplicity > 0L)
			{
				rObject = SpikingConstants.antiSpikeSymbol;
				rightObjectMultiplicity = rightAntiSpikesMultiplicity;
			}
			else
			{
				rObject = SpikingConstants.spikeSymbol;
				rightObjectMultiplicity = rightSpikesMultiplicity;
			}
			
			
			/*
			return buildRule(lhr.getOuterRuleMembrane().getLabel(),
					lhr.getOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol);,
					rhr.getOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol);,
					regExp,delay,computeEfficiencyAtributes);
			*/
			return buildRule(lhr.getOuterRuleMembrane().getLabel(),
					lObject,
					leftObjectMultiplicity,
					rObject,
					rightObjectMultiplicity,
					regExp,delay,computeEfficiencyAtributes);
		}
		
	}
	
	
	// this is the builder for the Spiking Rules
	//public static SpikingRule buildRule(String leftLabel, long left, long right, String regExp, long delay, boolean computeEfficiencyAttributes)
	public static SpikingRule buildRule(String leftLabel, String lObject, long left, String rObject, long right, String regExp, long delay, boolean computeEfficiencyAttributes)
	{	
		
		String rightLabel = leftLabel;
		
		if(leftLabel == null || leftLabel.equals(""))
			throw new IllegalArgumentException("Label can't be empty");
		
		if(left <= 0)
			throw new IllegalArgumentException("Left Hand Rule can't imply zero or less than zero spikes");
		
		if(right < 0)
			throw new IllegalArgumentException("Right Hand Rule can't imply less than zero spikes");
	
		HashMultiSet<String> mstemp = null;
		
		mstemp = new HashMultiSet<String>();
		//mstemp.add(SpikingConstants.spikeSymbol, left);
		mstemp.add(lObject, left);
	
		LeftHandRule leftHand = new LeftHandRule(new OuterRuleMembrane(new Label(leftLabel), (byte) 0, mstemp, new ArrayList<InnerRuleMembrane>()),new HashMultiSet<String>());
		
		mstemp = new HashMultiSet<String>();
		//mstemp.add(SpikingConstants.spikeSymbol, right);
		mstemp.add(rObject, right);
			
		RightHandRule rightHand = new RightHandRule(new OuterRuleMembrane(new Label(rightLabel), (byte) 0, mstemp, new ArrayList<InnerRuleMembrane>()),new HashMultiSet<String>());
		
		return new SpikingRule(leftHand,rightHand,regExp,delay, computeEfficiencyAttributes);
	
	}
	
	// this is the constructor for the Spiking Rules
	protected SpikingRule(LeftHandRule leftHandRule, RightHandRule rightHandRule, String regExp, long delay, boolean computeEfficiencyAttributes)
	{
		super(false, leftHandRule, rightHandRule);
			
		//if (leftHandRule == null || (leftHandRule != null && leftHandRule.getOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol) == 0))
		if (leftHandRule == null || (leftHandRule != null && leftHandRule.getOuterRuleMembrane().getMultiSet().size() == 0))	
			throw new IllegalArgumentException("Left Hand Rule can't be empty for spiking rules");
		
		if (!(regExp == null || regExp.equals("") ))
		{
			// String validChars = SpikingConstants.spikeSymbol + "()[]{},^*+?|";
			String validChars = SpikingConstants.spikeSymbol + "()[]{},^*+?|" + SpikingConstants.negSymbol;
			int occurrences = regExp.length();
			int success = 0;
			
			for (int i=0; i < regExp.length() ;i++)
				if (validChars.indexOf(regExp.charAt(i)) >= 0)
					success++;
				else
					break;
				
			if (occurrences < success)
				throw new IllegalArgumentException("Regular Expressions must have only valid construct chars plus the spike char");
			else try
			{
				this.regExp = Pattern.compile(regExp);
			}
			catch (PatternSyntaxException e)
			{
				throw new IllegalArgumentException("Regular Expressions must be well-formed");
			}
		}
		
		if (delay < 0)
			throw new IllegalArgumentException("Delay must be non-negative");
		
		//if ((rightHandRule == null || (rightHandRule != null && rightHandRule.getOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol) == 0)) && delay != 0)
		if ((rightHandRule == null || (rightHandRule != null && rightHandRule.getOuterRuleMembrane().getMultiSet().size() == 0)) && delay != 0)	
			throw new IllegalArgumentException("Forgetting Rules must have no delay");
				
		if (leftHandRule != null && rightHandRule != null)
			if (getHandRuleSpikingStringSize(leftHandRule) < getHandRuleSpikingStringSize(rightHandRule))
				throw new IllegalArgumentException("Firing Rules can't have less occurrences of spike in the Left Hand Rule than in the Right Hand Rule");
				
		
		// Let's construct the object at last
				
		this.delay = delay;
		this.buddingdivision = noBuddingDivision;

		
		if(computeEfficiencyAttributes)
			computeEfficiencyAttributes(rightHandRule);
		
		
		long leftSpikesMultiplicity = leftHandRule.getOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol);
		long leftAntiSpikesMultiplicity = leftHandRule.getOuterRuleMembrane().getMultiSet().count(SpikingConstants.antiSpikeSymbol);
		long rightSpikesMultiplicity = rightHandRule.getOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol);
		long rightAntiSpikesMultiplicity = rightHandRule.getOuterRuleMembrane().getMultiSet().count(SpikingConstants.antiSpikeSymbol);
		
		if(leftSpikesMultiplicity == 0L && leftAntiSpikesMultiplicity > 0L)
		{
			leftObject = SpikingConstants.antiSpikeSymbol;

		}
		else
		{
			leftObject = SpikingConstants.spikeSymbol;

		}
		
		if(rightSpikesMultiplicity == 0L && rightAntiSpikesMultiplicity > 0L)
		{
			rightObject = SpikingConstants.antiSpikeSymbol;

		}
		else
		{
			rightObject = SpikingConstants.spikeSymbol;

		}
		
		
					
	}
	
	// this is the builder for the Budding and Division Rules
	public static SpikingRule buildRule(String leftLabel, String rightLabel1, String rightLabel2, String type, String regExp, boolean computeEfficiencyAttributes)
	{	
		
		if(leftLabel == null || leftLabel.equals(""))
			throw new IllegalArgumentException("Left Label can't be empty");
		
		if(rightLabel1 == null || rightLabel1.equals(""))
			throw new IllegalArgumentException("First Right Label can't be empty");
		
		if(rightLabel2 == null || rightLabel2.equals(""))
			throw new IllegalArgumentException("Second Right Label can't be empty");
		
		if(type == null || type.equals(""))
			throw new IllegalArgumentException("Type can't be empty");
		
		if ((type.equals("budding") || type.equals("division")) == false)
			throw new IllegalArgumentException("Type must be 'budding' or 'division'");
		
		if(regExp == null || regExp.equals(""))
			throw new IllegalArgumentException("The Regular Expression can't be empty");
		
		HashMultiSet<String> mstemp1 = null;
		HashMultiSet<String> mstemp2 = null;
		
		mstemp1 = new HashMultiSet<String>();
		mstemp1.add(SpikingConstants.spikeSymbol, 0);
	
		LeftHandRule leftHand = new LeftHandRule(new OuterRuleMembrane(new Label(leftLabel), (byte) 0, mstemp1, new ArrayList<InnerRuleMembrane>()),new HashMultiSet<String>());
		
		mstemp1 = new HashMultiSet<String>();
		mstemp1.add(SpikingConstants.spikeSymbol, 0);
		
		mstemp2 = new HashMultiSet<String>();
		mstemp2.add(SpikingConstants.spikeSymbol, 0);
			
		RightHandRule rightHand = new RightHandRule(
				new OuterRuleMembrane(new Label(rightLabel1), (byte) 0, mstemp1, new ArrayList<InnerRuleMembrane>()),
				new OuterRuleMembrane(new Label(rightLabel2), (byte) 0, mstemp2, new ArrayList<InnerRuleMembrane>()),
				new HashMultiSet<String>());
		
		return new SpikingRule(leftHand,rightHand,regExp,type,computeEfficiencyAttributes);
	
	}
	
	// this is the constructor for the Budding and Division Rules
	protected SpikingRule(LeftHandRule leftHandRule, RightHandRule rightHandRule, String regExp, String type, boolean computeEfficiencyAttributes)
	{
		super(false, leftHandRule, rightHandRule);
			
		//if (leftHandRule == null || (leftHandRule != null && leftHandRule.getOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol) > 0))
		if (leftHandRule == null || (leftHandRule != null && leftHandRule.getOuterRuleMembrane().getMultiSet().size() > 0))
			throw new IllegalArgumentException("Left Hand Rule must be empty for budding or division rules");
		
		//if (rightHandRule == null || (rightHandRule != null && rightHandRule.getOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol) > 0))
		if (rightHandRule == null || (rightHandRule != null && rightHandRule.getOuterRuleMembrane().getMultiSet().size() > 0))
			throw new IllegalArgumentException("First Right Hand Rule Membrane must be empty for budding or division rules");
		
		//if (rightHandRule == null || (rightHandRule != null && rightHandRule.getSecondOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol) > 0))
		if (rightHandRule == null || (rightHandRule != null && rightHandRule.getSecondOuterRuleMembrane().getMultiSet().size() > 0))
			throw new IllegalArgumentException("Second Right Hand Rule Membrane must be empty for budding or division rules");
		
		if (!(regExp == null || regExp.equals("") ))
		{
			// String validChars = SpikingConstants.spikeSymbol + "()[]{},^*+?|";
			String validChars = SpikingConstants.spikeSymbol + "()[]{},^*+?|" + SpikingConstants.negSymbol;
			int occurrences = regExp.length();
			int success = 0;
			
			for (int i=0; i < regExp.length() ;i++)
				if (validChars.indexOf(regExp.charAt(i)) >= 0)
					success++;
				else
					break;
				
			if (occurrences < success)
				throw new IllegalArgumentException("Regular Expressions must have only valid construct chars plus the spike char");
			else try
			{
				this.regExp = Pattern.compile(regExp);
			}
			catch (PatternSyntaxException e)
			{
				throw new IllegalArgumentException("Regular Expressions must be well-formed");
			}
		}
		else
			throw new IllegalArgumentException("The Regular Expression can't be empty");
		
		// Let's construct the object at last
				
		this.delay = 0L;
		this.buddingdivision = type;
		
		if(computeEfficiencyAttributes)
			computeEfficiencyAttributes(rightHandRule);
		
		leftObject = SpikingConstants.spikeSymbol;
		rightObject = SpikingConstants.spikeSymbol; 
					
	}
	
	
	private void computeEfficiencyAttributes(RightHandRule rightHandRule)
	{
		if(this.buddingdivision.equals(noBuddingDivision))
		{
			//isForgetting = (rightHandRule == null || (rightHandRule != null && rightHandRule.getOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol) == 0)); 
			isForgetting = (rightHandRule == null || (rightHandRule != null && rightHandRule.getOuterRuleMembrane().getMultiSet().size() == 0));
			
			isFiring = !isForgetting;
			
			//ruleSpikingStringSize = getLeftHandRule().getOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol);
			ruleSpikingStringSize = getLeftHandRule().getOuterRuleMembrane().getMultiSet().count(leftObject);
			
			//ruleSpikingString = new String(SpikingConstants.spikeSymbol + "{" + ruleSpikingStringSize + "}");
			ruleSpikingString = new String(leftObject + "{" + ruleSpikingStringSize + "}");
			
			
			//rightHandRuleSpikingStringSize = isFiringRule() ? getRightHandRule().getOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol) : 0L;
			rightHandRuleSpikingStringSize = isFiringRule() ? getRightHandRule().getOuterRuleMembrane().getMultiSet().count(rightObject) : 0L;
			
		}
		else
		{
			if(this.buddingdivision.equals(budding))
				isBudding = true;
			else
				isDivision = true;
			
			ruleSpikingStringSize = 0L;
			ruleSpikingString = new String(SpikingConstants.spikeSymbol + "{" + ruleSpikingStringSize + "}");
			rightHandRuleSpikingStringSize = 0L;
		}
		
		efficiencyAttributesProcessed = true;
		
	}
	
		
	public boolean isFiringRule()
	{
		if(efficiencyAttributesProcessed)
			return isFiring;
		
		if(getRightHandRule().getSecondOuterRuleMembrane() != null)
			return false;
				
		//return !(getRightHandRule().getOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol) == 0);
		return !(getRightHandRule().getOuterRuleMembrane().getMultiSet().size() == 0);
		
	}
	
	
	public boolean isForgettingRule()
	{
		if(efficiencyAttributesProcessed)
			return isForgetting;
		
		if(getRightHandRule().getSecondOuterRuleMembrane() != null)
			return false;
						
		return !(isFiringRule());
	}
		
	
	public boolean isBuddingRule()
	{
		if(efficiencyAttributesProcessed)
			return isBudding;
		
		return buddingdivision.equals(budding);
								
	}

	public boolean isDivisionRule()
	{
		if(efficiencyAttributesProcessed)
			return isDivision;
		
		return buddingdivision.equals(division);
								
	}
	
	public boolean allowFiring(SpikingMembrane membrane)
	{
		
		if(!isFiringRule()) return false;
		
		long ruleSpikingStringSize = getLeftHandRuleSpikingStringSize();
		long membraneSpikingStringSize = membrane.getMembraneSpikingStringSize();
		
		boolean matchElements = 
			membraneSpikingStringSize >= ruleSpikingStringSize;							
							
		if(!matchElements) return false;
			
		Matcher matcher = null;

		String membraneSpikingString = membrane.getMembraneSpikingString();

		if(getRegExp() == null)
		{
			String ruleSpikingString = this.getLeftHandRuleSpikingString();
			matcher = Pattern.compile(ruleSpikingString).matcher(membraneSpikingString);
		}
		else
		{
			matcher = getRegExp().matcher(membraneSpikingString);
		}
		
		return matcher.matches();
				
	}
	
	public boolean allowForgetting(SpikingMembrane membrane)
	{
		
		if(!isForgettingRule()) return false;
		
		long ruleSpikingStringSize = getLeftHandRuleSpikingStringSize();
		long membraneSpikingStringSize = membrane.getMembraneSpikingStringSize();
		
		boolean matchElements = 
			membraneSpikingStringSize >= ruleSpikingStringSize;							
							

		if(!matchElements) return false;
			
		Matcher matcher = null;

		String membraneSpikingString = membrane.getMembraneSpikingString();

		if(getRegExp() == null)
		{
			String ruleSpikingString = this.getLeftHandRuleSpikingString();
			matcher = Pattern.compile(ruleSpikingString).matcher(membraneSpikingString);
		}
		else
		{
			matcher = getRegExp().matcher(membraneSpikingString);
		}
		
		return matcher.matches();

	}

	public boolean allowBudding(SpikingMembrane membrane, SpikingMembraneStructure structure)
	{
		
		if(!isBuddingRule()) return false;
		
		long ruleSpikingStringSize = getLeftHandRuleSpikingStringSize();
		long membraneSpikingStringSize = membrane.getMembraneSpikingStringSize();
		
		boolean matchElements = 
			membraneSpikingStringSize >= ruleSpikingStringSize;							
							
		if(!matchElements) return false;
			
		Matcher matcher = null;

		String membraneSpikingString = membrane.getMembraneSpikingString();

		matcher = getRegExp().matcher(membraneSpikingString);

		if (!matcher.matches()) return false;
		
		// Now we have to check the structure
		
		boolean stopSearch = false;
		
		String memLabel = membrane.getLabel();
		String leftRuleLabel = this.getLeftHandRule().getOuterRuleMembrane().getLabel();
		String firstRightRuleLabel = this.getRightHandRule().getOuterRuleMembrane().getLabel();
		String secondRightRuleLabel = this.getRightHandRule().getSecondOuterRuleMembrane().getLabel();
		
		if(memLabel.equals(leftRuleLabel) == false)
			return false;
		
		if(leftRuleLabel.equals(firstRightRuleLabel)== false)
			return false;
		
		List<SpikingMembrane> suc = structure.getSuccessors(membrane);
		
		Iterator<SpikingMembrane> it = suc.iterator();
		
		while(stopSearch == false && it.hasNext())
		{
			SpikingMembrane mem = (SpikingMembrane) it.next();
			
			if(mem.getLabel().equals(secondRightRuleLabel))
				stopSearch = true;
						
		}
		
		if(stopSearch)
			return false;
		else
			return true;

	}
	
	public boolean allowDivision(SpikingMembrane membrane, SpikingMembraneStructure structure)
	{
		
		if(!isDivisionRule()) return false;
		
		long ruleSpikingStringSize = getLeftHandRuleSpikingStringSize();
		long membraneSpikingStringSize = membrane.getMembraneSpikingStringSize();
		
		boolean matchElements = 
			membraneSpikingStringSize >= ruleSpikingStringSize;							
							
		if(!matchElements) return false;
			
		Matcher matcher = null;

		String membraneSpikingString = membrane.getMembraneSpikingString();

		matcher = getRegExp().matcher(membraneSpikingString);

		if (!matcher.matches()) return false;
		
		// Now we have to check the structure
		
		boolean stopSearch = false;
		
		String memLabel = membrane.getLabel();
		String leftRuleLabel = this.getLeftHandRule().getOuterRuleMembrane().getLabel();
		String firstRightRuleLabel = this.getRightHandRule().getOuterRuleMembrane().getLabel();
		String secondRightRuleLabel = this.getRightHandRule().getSecondOuterRuleMembrane().getLabel();
		
		if(memLabel.equals(leftRuleLabel) == false)
			return false;
		
		List<SpikingMembrane> pred = structure.getPredecessors(membrane);
		
		Iterator<SpikingMembrane> itp = pred.iterator();
		
		while(stopSearch == false && itp.hasNext())
		{
			SpikingMembrane mem = (SpikingMembrane) itp.next();
			
			if(mem.getLabel().equals(firstRightRuleLabel) || mem.getLabel().equals(secondRightRuleLabel))
				stopSearch = true;
						
		}
		
		if (stopSearch)
			return false;
		
		stopSearch = false;
		
		List<SpikingMembrane> suc = structure.getSuccessors(membrane);
		
		Iterator<SpikingMembrane> its = suc.iterator();
		
		while(stopSearch == false && its.hasNext())
		{
			SpikingMembrane mem = (SpikingMembrane) its.next();
			
			if(mem.getLabel().equals(firstRightRuleLabel) || mem.getLabel().equals(secondRightRuleLabel))
				stopSearch = true;
						
		}
		
		if(stopSearch)
			return false;
		else
			return true;

	}

	
	public String getLeftHandRuleSpikingString()
	{
		
		if(efficiencyAttributesProcessed)
			return ruleSpikingString;
		
		// String ss = new String(SpikingConstants.spikeSymbol + "{" + getLeftHandRuleSpikingStringSize() + "}");

		String ss = new String(leftObject + "{" + getLeftHandRuleSpikingStringSize() + "}");
		
		return ss;
	}
	
	
	public long getLeftHandRuleSpikingStringSize()
	{
		
		if(efficiencyAttributesProcessed)
			return ruleSpikingStringSize;
		
		// return getLeftHandRule().getOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol);
		
		return getLeftHandRule().getOuterRuleMembrane().getMultiSet().count(leftObject);
		
	}
		
	public long getRightHandRuleSpikingStringSize()
	{
		
		if(efficiencyAttributesProcessed)
			return rightHandRuleSpikingStringSize;
		
		// return isFiringRule() ? getRightHandRule().getOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol) : 0L;
		
		return isFiringRule() ? getRightHandRule().getOuterRuleMembrane().getMultiSet().count(rightObject) : 0L;
		
	}
	
		
	public long getHandRuleSpikingStringSize(HandRule r)
	{
		
		// return r.getOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol);
		
		long spikes = r.getOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol);
		long antiSpikes = r.getOuterRuleMembrane().getMultiSet().count(SpikingConstants.antiSpikeSymbol);
		
	
		return antiSpikes > 0 ? antiSpikes : spikes;
		
	}
	
	
	public boolean canBeFired(SpikingMembrane membrane)
	{
		if(membrane.isClosed())
			return false;
		
		if(!isFiringRule())
			return false;
		
		return allowFiring(membrane);
		
	}
	
	public boolean canBeForgotten(SpikingMembrane membrane)
	{
		if(membrane.isClosed())
			return false;
		
		if(!isForgettingRule())
			return false;
		
		return allowForgetting(membrane);
		
	}
	
	public boolean canBeBudding(SpikingMembrane membrane, SpikingMembraneStructure structure)
	{
		if(membrane.isClosed())
			return false;
		
		if(!isBuddingRule())
			return false;
		
		return allowBudding(membrane,structure);
		
	}
	
	public boolean canBeDivision(SpikingMembrane membrane, SpikingMembraneStructure structure)
	{
		if(membrane.isClosed())
			return false;
		
		if(!isDivisionRule())
			return false;
		
		return allowDivision(membrane,structure);
		
	}
	
	public boolean canBeExecuted(SpikingMembrane membrane, SpikingMembraneStructure structure)
	{
		if(membrane.isClosed())
			return false;
		
		return 
			canBeForgotten(membrane) 			|| 
			canBeFired(membrane) 				|| 
			canBeBudding(membrane,structure) 	|| 
			canBeDivision(membrane,structure);
		
	}
		
	protected void checkState() {

		if(getRightHandRule().getSecondOuterRuleMembrane() != null)	// we have a budding or division rule
		{
			// if (getLeftHandRule() == null || (getLeftHandRule() != null && getLeftHandRule().getOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol) > 0))
			if (getLeftHandRule() == null || (getLeftHandRule() != null && getLeftHandRule().getOuterRuleMembrane().getMultiSet().size() > 0))				
				throw new IllegalArgumentException("Left Hand Rule must be empty for budding or division rules");
			
			// if (getRightHandRule() == null || (getRightHandRule() != null && getRightHandRule().getOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol) > 0))
			if (getRightHandRule() == null || (getRightHandRule() != null && getRightHandRule().getOuterRuleMembrane().getMultiSet().size() > 0))
				throw new IllegalArgumentException("First Right Hand Rule Membrane must be empty for budding or division rules");
			
			// if (getRightHandRule() == null || (getRightHandRule() != null && getRightHandRule().getSecondOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol) > 0))
			if (getRightHandRule() == null || (getRightHandRule() != null && getRightHandRule().getSecondOuterRuleMembrane().getMultiSet().size() > 0))
				throw new IllegalArgumentException("Second Right Hand Rule Membrane must be empty for budding or division rules");
			
			if(delay != 0)
				throw new IllegalArgumentException("Delay must be zero for or budding or division rules");
			
		}
		else
		{
			// if (getLeftHandRule() == null || (getLeftHandRule()!= null && getLeftHandRule().getOuterRuleMembrane().getMultiSet().count(SpikingConstants.spikeSymbol) == 0))
			if (getLeftHandRule() == null || (getLeftHandRule()!= null && getLeftHandRule().getOuterRuleMembrane().getMultiSet().size() == 0))
				throw new IllegalArgumentException("Left Hand Rule can't be empty for spiking rules");
						
			if (delay < 0)
				throw new IllegalArgumentException("Delay must be non-negative for spiking rules");
			
			if (isForgettingRule() && delay != 0)
				throw new IllegalArgumentException("Forgetting Rules can't have delay");
			
			if (getLeftHandRule() != null && getRightHandRule() != null)
				if (getHandRuleSpikingStringSize(getLeftHandRule()) < getHandRuleSpikingStringSize(getRightHandRule()))
					throw new IllegalArgumentException("Firing Rules can't have less occurrences of spike in the Left Hand Rule than in the Right Hand Rule");
		}									
	}

	public boolean executeSafeSpiking(ChangeableMembrane membrane,
			Configuration currentConfig) {
		
		boolean result = false;
		
		SpikingRule rule = this;
		SpikingMembrane source = (SpikingMembrane) membrane;
		SpikingMembraneStructure structure = ((SpikingMembraneStructure) currentConfig.getMembraneStructure());
		
		long ruleLeftHandRuleMultisetSize = rule.getLeftHandRuleSpikingStringSize();
		long ruleRightHandRuleMultisetSize = rule.getRightHandRuleSpikingStringSize();
		
		String ruleObject = rightObject;
		
		if(rule.isFiringRule() && source.isOpen()) // If I have to fire now...
		{
			
			List<SpikingMembrane> successors = null; 
				
			successors = structure.getSuccessors(source);
			
		
			Iterator<SpikingMembrane> iterator = successors.iterator();
					
			while(iterator.hasNext())
			{
				
				SpikingMembrane target = (SpikingMembrane) iterator.next();
				
				System.out.println("Target Membrane");
				System.out.println(target);
				
				// we consider for simplicity, that astrpcytes work only with absolute values, i. e. antispikes are converted automatically into spikes
				
				ArcInfo arcInfo = structure.getArcInfo(source.getId(), target.getId());
				
				Long weight = arcInfo.getWeight().longValue();
				boolean inhibitory = arcInfo.getInhibitory();

				// Unconditionally if the arc is controlling Binder astrocytes, we must load the spikes into them.
				
				if(!arcInfo.getAstrocyteCtrlList().isEmpty())
				{
					Iterator<String> itAstro = arcInfo.getAstrocyteCtrlList().iterator();
					
					while(itAstro.hasNext())
					{
						String astroLabel = (String) itAstro.next();
						Astrocyte ast = structure.getAstrocyte(astroLabel);
						
						// We load the spikes into the astrocyte
						ast.loadSpikes(ruleRightHandRuleMultisetSize*weight);
					}
					
					
					System.out.println("This arc controls Binder Astrocytes... Spikes loaded!!!");
					System.out.println(arcInfo.getAstrocyteCtrlList());
				}
	
				// Before firing to target, we have to check if there's an astrocyte controlling the arc...
				
				if(!arcInfo.getAstType().equals("none"))
				{
					
					if(arcInfo.getAstType().equals("wang"))
					{
						Iterator<String> itAstro = arcInfo.getAstrocyteList().iterator();
						
						while(itAstro.hasNext())
						{
							String astroLabel = (String) itAstro.next();
							Astrocyte ast = structure.getAstrocyte(astroLabel);
							
							// We load the spikes into the astrocyte
							ast.loadSpikes(ruleRightHandRuleMultisetSize*weight);
						}

						System.out.println("This arc is under control of Wang Astrocytes... Spikes loaded!!!");
						System.out.println(arcInfo.getAstrocyteList());					
					}
					else if(arcInfo.getAstType().equals("binder"))
					{
						System.out.println("This arc is under control of Binder Astrocytes... Nothing loaded!!!");
					}
					
					
					// Final step: unconditionally we load the spikes into the arc buffer so we have the input for astrocytes operation
					arcInfo.setSpikesInput(ruleRightHandRuleMultisetSize*weight);
					
				}
				else
				{
				
					if(target.isOpen()){
						// target.addSpikes(ruleRightHandRuleMultisetSize);
						if(inhibitory == false)	// if we have a non inhibitory arc we just fire the corresponding spikes
							target.addSpikes(ruleObject,ruleRightHandRuleMultisetSize*weight);
						else					// if we have an inhitory arc, we always fire anti-spikes
							target.addSpikes(SpikingConstants.antiSpikeSymbol,ruleRightHandRuleMultisetSize*weight);
						
						System.out.println("After execution Target Membrane");
						System.out.println(target);
						
					}
				
				}
			}
			
			source.removeSpikes(ruleLeftHandRuleMultisetSize);
			
			result = true;
			
		}
		
		else if(rule.isForgettingRule() && source.isOpen())
		{
			
			source.removeSpikes(ruleLeftHandRuleMultisetSize); // Sets spiking numbers to forget
			result = true;		
		}

		
		System.out.println(source.getStructure());
		
		return result;
		
	}

	public boolean executeSafeBuddingDivisionPhaseOne(ChangeableMembrane membrane,
			Configuration currentConfig, 
			ArrayList<SpikingMembrane> buddyList, 
			ArrayList<Pair<SpikingMembrane,SpikingMembrane>> divisionList) {
	
		// TODO Auto-generated method stub
		
		boolean result = false;
		
		SpikingRule rule = this;
		SpikingMembrane source = (SpikingMembrane) membrane;
		SpikingMembraneStructure structure = ((SpikingMembraneStructure) currentConfig.getMembraneStructure());
		
		if(rule.isBuddingRule() && source.isClosed())
		{
				
			// 1. We clean up the source membrane
			source.removeSpikes(source.getMembraneSpikingStringSize());
			
			// 2. We create the buddy and close it.
			SpikingMembrane buddy = SpikingMembrane.buildMembrane(rule.getRightHandRule().getSecondOuterRuleMembrane().getLabel(), 0L, structure, false);
			buddy.setMembraneClosedToBuddingOrDivision();

			// 3. We clone the spike train in the case we have source as an output membrane
			
			if(structure.isOutput(source))
				structure.getEnvironmentMembrane().cloneSpikeTran(source, buddy);
			
			// 4. We get all the successors of source membrane and iterate over them
			
			List<SpikingMembrane> successors = structure.getSuccessors(source);
			
			Iterator<SpikingMembrane> iterator = successors.iterator();
			
			while(iterator.hasNext())
			{
				SpikingMembrane target = (SpikingMembrane) iterator.next();
				
				System.out.println("Target Membrane");
				System.out.println(target);

				// 5. We disconnect the synapse from source to target and connect a synapse from buddy to target 
								
				System.out.println("Saving arc info...");	
				ArcInfo arcInfo = structure.getArcInfo(source.getId(), target.getId());
				
				System.out.println("Disconnect: (" +  source + "," + target + ")");
				structure.disconnect(source, target);
				System.out.println("Connect: (" +  buddy + "," + target + ")");
				structure.connect(buddy, target, arcInfo, false, false);
				
			}
			
			// 6. We create a synapse between the source and the buddy
			System.out.println("Connect: (" +  source + "," + buddy + ")");
			structure.connect(source, buddy, false, false);
			
			// 7. We update the buddyList for phase two.
			buddyList.add(buddy);
			
			result = true;
		}
		else if(rule.isDivisionRule() && source.isClosed())
		{
				
			// 1. We clean up the source membrane and relabel it
			source.removeSpikes(source.getMembraneSpikingStringSize());
			source.renewLabel(rule.getRightHandRule().getOuterRuleMembrane().getLabel());
			
			// 2. We create the buddy, and close it (the relabeling is implicit).
			SpikingMembrane buddy = SpikingMembrane.buildMembrane(rule.getRightHandRule().getSecondOuterRuleMembrane().getLabel(), 0L, structure, false);
			buddy.setMembraneClosedToBuddingOrDivision();
			
			// 3. We clone the spike train in the case we have source as an output membrane
			
			if(structure.isOutput(source))
				structure.getEnvironmentMembrane().cloneSpikeTran(source, buddy);
			
			// 4. We process the predecessors and the successors
			
			// 4.1 We get all the predecessors of source membrane and iterate over them
			
			List<SpikingMembrane> predecessors = structure.getPredecessors(source);
			
			Iterator<SpikingMembrane> itpred = predecessors.iterator();
			
			while(itpred.hasNext())
			{
				SpikingMembrane pred = (SpikingMembrane) itpred.next();
				
				System.out.println("Predecessor Membrane");
				System.out.println(pred);
				
				// 4.1.1 We get the ArcInfo

				System.out.println("Getting arc info...");	
				ArcInfo arcInfo = structure.getArcInfo(pred.getId(),source.getId());
				
				// 4.1.2 We connect a synapse from target to buddy 
				System.out.println("Connect: (" +  pred + "," + buddy + ")");
				structure.connect(pred, buddy, arcInfo, false, false);
				
				System.out.println("Pred membrane: " + structure.getSuccessors(pred));
				System.out.println("Suc buddy: " + structure.getPredecessors(buddy));
				
			}
			
			System.out.println();
			
			// 4.2 We get all the successors of source membrane and iterate over them
			
			List<SpikingMembrane> successors = structure.getSuccessors(source);
			
			Iterator<SpikingMembrane> itsuc = successors.iterator();
			
			while(itsuc.hasNext())
			{
				SpikingMembrane suc = (SpikingMembrane) itsuc.next();
				
				System.out.println("Successor Membrane");
				System.out.println(suc);

				// 4.2.1 We get the ArcInfo
				System.out.println("Getting arc info...");	
				ArcInfo arcInfo = structure.getArcInfo(source.getId(),suc.getId());
				
				// 4.2.2 We connect a synapse from buddy to target 
				System.out.println("Connect: (" +  buddy + "," + suc + ")");
				structure.connect(buddy, suc, arcInfo, false, false);
				
				System.out.println("Pred buddy: " + structure.getPredecessors(buddy));
				System.out.println("Suc membrane: " + structure.getSuccessors(suc));

				
			}
			
			// 5. We update the divisionList for the phase two.
			Pair<SpikingMembrane, SpikingMembrane> pair = new Pair<SpikingMembrane,SpikingMembrane>(source,buddy);
			divisionList.add(pair);
			
			
			result = true;
		}
		
		System.out.println(source.getStructure());
		
		return result;
		
	}
	
	public static boolean executeSafeBuddingDivisionPhaseTwo(
			ChangeableMembrane membrane1,	ChangeableMembrane membrane2,
			Configuration currentConfig,	boolean isBudding) {
	
		
		boolean result = false;
		
		SpikingMembrane buddy1	=	(SpikingMembrane) membrane1;
		SpikingMembrane buddy2 =	(SpikingMembrane) membrane2;
		SpikingMembraneStructure structure = ((SpikingMembraneStructure) currentConfig.getMembraneStructure());
		
	
		if(!isBudding && buddy1.isClosed() && buddy2.isClosed())
		{
			// We create new synapses for the buddy according to the synapse dictionary
			
			Map<String, Set<String>> dictionary = structure.getDictionary();
						
			Set<String> labelSet = null;
			
			Iterator<String> itKeySet = dictionary.keySet().iterator();
			
			while(itKeySet.hasNext())
			{
			
				String label = (String) itKeySet.next();
				
				labelSet = dictionary.get(label);
				
				// First buddy
				
				if(label.equals(buddy1.getLabel()))
				{

					// we are in the (j,h) case
					// So we have to create synapses in the form (buddy1,rowLabel)	
					// for each element rowLabel in labelSet
					
					Iterator<String> itrow = labelSet.iterator();
					
					while(itrow.hasNext())
					{
						String rowLabel = (String) itrow.next();
										
						Iterator <SpikingMembrane> itmem = structure.getCellsByLabel(rowLabel).iterator();
							
						while(itmem.hasNext())
						{
							SpikingMembrane itmembrane = (SpikingMembrane) itmem.next();
							structure.connect(buddy1, itmembrane, false, false);
						}

							
					}
			
				}
				else if(labelSet.contains(buddy1.getLabel()))
				{
					// we are in the (h,j) case
					// So we have to create synapses in the form (rowLabel,buddy1)
					// for each element rowLabel in labelSet
						
					Iterator <SpikingMembrane> itmem = structure.getCellsByLabel(label).iterator();
						
					while(itmem.hasNext())
					{
						SpikingMembrane itmembrane = (SpikingMembrane) itmem.next();
						structure.connect(itmembrane,buddy1,false, false);
					}

						
				}
				
				// Second buddy
				
				if(label.equals(buddy2.getLabel()))
				{

					// we are in the (j,h) case
					// So we have to create synapses in the form (buddy2,rowLabel)	
					// for each element rowLabel in labelSet
					
					Iterator<String> itrow = labelSet.iterator();
					
					while(itrow.hasNext())
					{
						String rowLabel = (String) itrow.next();
										
						Iterator <SpikingMembrane> itmem = structure.getCellsByLabel(rowLabel).iterator();
							
						while(itmem.hasNext())
						{
							SpikingMembrane itmembrane = (SpikingMembrane) itmem.next();
							structure.connect(buddy2, itmembrane, false, false);
						}

							
					}
			
				}
				else if(labelSet.contains(buddy2.getLabel()))
				{
					// we are in the (h,j) case
					// So we have to create synapses in the form (rowLabel,buddy1)
					// for each element rowLabel in labelSet
						
					Iterator <SpikingMembrane> itmem = structure.getCellsByLabel(label).iterator();
						
					while(itmem.hasNext())
					{
						SpikingMembrane itmembrane = (SpikingMembrane) itmem.next();
						structure.connect(itmembrane,buddy2,false, false);
					}

						
				}
				
			}
			
			result = true;
		}
		else if(isBudding && buddy1.isClosed())
		{
			// We create new synapses for the buddy according to the synapse dictionary
			
			Map<String, Set<String>> dictionary = structure.getDictionary();
						
			Set<String> labelSet = null;
			
			Iterator<String> itKeySet = dictionary.keySet().iterator();
			
			while(itKeySet.hasNext())
			{
			
				String label = (String) itKeySet.next();
				
				labelSet = dictionary.get(label);
				
				// First buddy
				
				if(label.equals(buddy1.getLabel()))
				{

					// we are in the (j,h) case
					// So we have to create synapses in the form (buddy1,rowLabel)	
					// for each element rowLabel in labelSet
					
					Iterator<String> itrow = labelSet.iterator();
					
					while(itrow.hasNext())
					{
						String rowLabel = (String) itrow.next();
										
						Iterator <SpikingMembrane> itmem = structure.getCellsByLabel(rowLabel).iterator();
							
						while(itmem.hasNext())
						{
							SpikingMembrane itmembrane = (SpikingMembrane) itmem.next();
							structure.connect(buddy1, itmembrane, false, false);
						}

							
					}
			
				}
				else if(labelSet.contains(buddy1.getLabel()))
				{
					// we are in the (h,j) case
					// So we have to create synapses in the form (rowLabel,buddy1)
					// for each element rowLabel in labelSet
						
					Iterator <SpikingMembrane> itmem = structure.getCellsByLabel(label).iterator();
						
					while(itmem.hasNext())
					{
						SpikingMembrane itmembrane = (SpikingMembrane) itmem.next();
						structure.connect(itmembrane,buddy1,false, false);
					}

						
				}
				
				
			}
			
			result = true;
		}
		
		System.out.println(buddy1.getStructure());
		
		return result;
		
	}
	
	public static boolean executeSafeInputSpiking(ChangeableMembrane membrane, long spikes)
	{
		// TODO Auto-generated method stub
		
		boolean result = false;

		SpikingMembrane input = (SpikingMembrane) membrane;

		
		if(input.isOpen() && spikes >= 0L) // If I have to fire now...
		{
				
				System.out.println("Input Membrane");
				System.out.println(input);
				System.out.println("Adding " + spikes + " spikes in this step");
				//input.addSpikes(spikes);
				input.addSpikes(SpikingConstants.spikeSymbol,spikes);
				System.out.println("After execution Input Membrane");
				System.out.println(input);
				
				result = true;
					
		}

		System.out.println(input.getStructure());
		
		return result;
		
	}

	
	

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {


		String buddingdivision = "";
		
		if(this.isBuddingRule() || this.isDivisionRule())
		{
			String label1 = this.getRightHandRule().getOuterRuleMembrane().getLabel();
			String label2 = this.getRightHandRule().getSecondOuterRuleMembrane().getLabel();
			
			if(this.isBuddingRule())
				buddingdivision = " (" + label1 + " / "  + label2 + ")";
			else
				buddingdivision = " (" + label1 + " || " + label2 + ")";
		}
		
	
		String regExp = (!(this.regExp == null || this.regExp.equals("") )) ?  this.regExp.toString() + " / " : "";
		String delay = (!(this.delay == 0))  ? " ; " + this.delay  : "";
		return regExp + super.toString() + delay + buddingdivision;


	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result +  1237;
		result = PRIME * result
				+ ((getLeftHandRule() == null) ? 0 : getLeftHandRule().hashCode());
		result = PRIME * result
				+ ((getRightHandRule() == null) ? 0 : getRightHandRule().hashCode());
		result = PRIME * result
				+ ((regExp == null) ? 0 : regExp.hashCode());
		result = PRIME * result
		+ ((delay == 0) ? 0 : (int)delay);

		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SpikingRule other = (SpikingRule) obj;
		if (dissolves() != other.dissolves())
			return false;
		if (delay != other.delay)
			return false;
		if (getLeftHandRule() == null) {
			if (other.getLeftHandRule() != null)
				return false;
		} else if (!getLeftHandRule().equals(other.getLeftHandRule()))
			return false;
		if (getRightHandRule() == null) {
			if (other.getRightHandRule() != null)
				return false;
		} else if (!getRightHandRule().equals(other.getRightHandRule()))
			return false;
		if (regExp == null) {
			if (other.regExp != null)
				return false;
		} else if (!regExp.equals(other.regExp))
			return false;
		
		return true;
	}

	@Override
	public boolean dissolves() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<String> getObjects() {
		Set<String> result = new HashSet<String>();
        result.add(SpikingConstants.spikeSymbol);
        result.add(SpikingConstants.antiSpikeSymbol);
        return result;
	}

	@Override
	public boolean hasNewMembranes() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public Pattern getRegExp()
	{
		return this.regExp;
	}
	
	public long getDelay()
	{

		return delay;

	}

	@Override
	public long countExecutions(ChangeableMembrane membrane) {
		SpikingMembrane spmembrane = (SpikingMembrane) membrane;
		boolean exec = this.canBeExecuted(spmembrane, spmembrane.getStructure());
		int num = exec? 1 : 0;
		return num;
	}

	@Override
	protected boolean executeSafe(ChangeableMembrane membrane,
			MultiSet<String> environment, long executions) {
		// TODO Auto-generated method stub
		return false;
	}




}
