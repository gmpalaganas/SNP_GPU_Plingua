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

import org.gcn.plinguacore.util.psystem.rule.IRule;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

//"Compact" representation of spiking rules
public class SpikingRuleBlock{

    private final String SPIKE_OBJECT = "a";

    private String regEx;
    private int consumedSpikes;
    private int producedSpikes;
    private int delay;

    public SpikingRuleBlock(String e,int c,int p,int d){
        regEx = e;
        consumedSpikes = c;
        producedSpikes = p;
        delay = d;
    }

    public SpikingRuleBlock(SpikingRule rule){
        String leftHand = rule.getLeftHandRule().toString();
        String rightHand = rule.getRightHandRule().toString();

        consumedSpikes = this.extractSpikeNumber(leftHand);
        producedSpikes = this.extractSpikeNumber(rightHand);
        delay = (int)rule.getDelay();

        if( rule.getRegExp() != null)
            regEx = rule.getRegExp().toString();
        else
            regEx = "";
       
    }

    public String getRegExp(){
        return regEx;
    }

    public int getConsumedSpikes(){
        return consumedSpikes;
    }

    public int getProducedSpikes(){
        return producedSpikes;
    }
    
    public int getDelay(){
        return delay;
    }
    
    //Extract the number of spikes from the LHR or RHR given
    private int extractSpikeNumber(String rule){
        int spikes = 0;
        Pattern pattern = Pattern.compile("\\[.*\\]");
        Matcher matcher = pattern.matcher(rule);

        if (matcher.find()){
            String extracted = matcher.group(0).replaceAll("[\\[\\]\\s]","");
            if(extracted.equals(SPIKE_OBJECT))
                spikes = 1;
            else{
                Pattern numberExtractPattern = Pattern.compile("\\d+");
                Matcher numberExtractMatcher = numberExtractPattern.matcher(extracted);
                if(numberExtractMatcher.find())
                    spikes = Integer.parseInt(numberExtractMatcher.group(0));
            }
        } else{
            spikes = 0;
        }
        return spikes;
    }

    public String toString(){
        String ret = "";
        ret += (regEx.equals(""))?"":regEx + "/";
        ret += "a";
        ret += (consumedSpikes == 0)?"":"*" + consumedSpikes;
        ret += "-->";
        ret += (producedSpikes == 0)?"#":(producedSpikes == 1)?"a":"a*" + producedSpikes;
        ret += (delay == 0)?"":";" + delay;

        return ret;
    }

}
