package org.gcn.plinguacore.parser.output.binary;

import java.io.IOException;
import java.io.OutputStream;

import java.nio.ByteOrder;
import java.util.Collection;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.gcn.plinguacore.simulator.spiking.SpikingSimulator;

import org.gcn.plinguacore.util.ByteOrderDataOutputStream;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.PlinguaCoreException;

import org.gcn.plinguacore.util.psystem.AlphabetObject;
import org.gcn.plinguacore.util.psystem.Configuration;
import org.gcn.plinguacore.util.psystem.Psystem;

import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.membrane.MembraneStructure;

import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.spiking.SpikingRule;
import org.gcn.plinguacore.util.psystem.rule.spiking.SpikingRuleBlock;
import org.gcn.plinguacore.util.psystem.rule.spiking.SpikingRegExpChar;

import org.gcn.plinguacore.util.psystem.spiking.SpikingPsystem;

import org.gcn.plinguacore.util.psystem.spiking.membrane.SpikingEnvironment;
import org.gcn.plinguacore.util.psystem.spiking.membrane.SpikingMembrane;
import org.gcn.plinguacore.util.psystem.spiking.membrane.SpikingMembraneStructure;
import org.gcn.plinguacore.util.psystem.spiking.membrane.ArcInfo;

//import org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba.StaticMethods;
//import org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba.DCBAProbabilisticSimulator;
//import org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba.EnvironmentRulesBlock;
//import org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba.MatrixColumn;
//import org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba.SkeletonRulesBlock;
//import org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba.StaticMethods;
//import org.gcn.plinguacore.util.ByteOrderDataOutputStream;
//import org.gcn.plinguacore.util.MultiSet;
//import org.gcn.plinguacore.util.PlinguaCoreException;
//import org.gcn.plinguacore.util.psystem.AlphabetObject;
//import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
//import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeSkinMembrane;

class SpikingBinaryOutputParser extends AbstractBinaryOutputParser{
    
    private final String REGEXP_NONE = "0";

    private List<SpikingMembrane> neurons;

    private List<Integer> spikes;

    private List<SpikingRuleBlock> spikingRules;

    public SpikingBinaryOutputParser(){

        neurons  = new ArrayList<SpikingMembrane>();
        spikes = new ArrayList<Integer>();
        spikingRules = new ArrayList<SpikingRuleBlock>();

    }

    public void readNeurons(){
        SpikingMembraneStructure structure = (SpikingMembraneStructure)getPsystem().getMembraneStructure();
        Iterator<SpikingMembrane> it = (Iterator<SpikingMembrane>)structure.getAllMembranes().iterator();
       
        while(it.hasNext()){
            SpikingMembrane neuron = it.next();
            neurons.add(neuron);
        }
    }

    public void readRules(){

        spikingRules.clear();

        for(SpikingMembrane neuron : neurons){
            Iterator<IRule> it = getPsystem().getRules().iterator(neuron.getLabel(), 0);

            while(it.hasNext()){
                IRule rule = it.next();
                SpikingRuleBlock ruleBlock = new SpikingRuleBlock(neuron.getId(),(SpikingRule)rule);
                spikingRules.add(ruleBlock);
            }
        }

    }

    public void writeHeader() throws IOException{

        System.out.println(0xAF);
        System.out.println(0x12);
        System.out.println(0xFB);

        getStream().writeByte(0xAF);
        getStream().writeByte(0x12);
        getStream().writeByte(0xFB);

    }

    public void writeSubHeader() throws IOException{

        writeReservedCharacters();
        writeRuleCount();
        writeNeuronCount();
        writeInitialConfiguration();
        
    }

    public void writeReservedCharacters(){

        return;

    }

    public void writeRuleCount() throws IOException{

        int ruleCount = spikingRules.size();

        System.out.println("Number of Rules: " + ruleCount);

        getStream().write(ruleCount);

    }

    public void writeNeuronCount() throws IOException{

        SpikingMembraneStructure structure = (SpikingMembraneStructure)getPsystem().getMembraneStructure();

        //The MembraneStructure considers the environment as another neuron
        int neuronCount = structure.getAllMembranes().size() - 1;

        System.out.println("Number of Neurons: " + neuronCount);

        getStream().write(neuronCount);

    }

    public void writeInitialConfiguration() throws IOException{

        SpikingPsystem psystem = (SpikingPsystem)getPsystem();
        Configuration config = psystem.getFirstConfiguration();
        
        MembraneStructure struct = config.getMembraneStructure();
        Iterator<? extends Membrane> it = struct.getAllMembranes().iterator(); 
        
        //Skip Envinronment
        it.next();

        String initConfig = "Initial Configuration: {";

        while(it.hasNext()){
            int spikes = (int)it.next().getMultiSet().count("a"); 
            getStream().writeInt(spikes);
            initConfig += spikes;
            if(it.hasNext())
                initConfig += ", ";
        }

        initConfig += "}";

        System.out.println(initConfig);


    }


    public void writeSpikingRules() throws IOException{

        writeRuleIdBlocks();
        writeRuleLHSBlocks();
        writeRuleRHSBlocks();

    }

    public void writeRuleIdBlocks() throws IOException{
        for(SpikingRuleBlock rule : spikingRules)
            getStream().writeInt(rule.getNeuronId());
    }

    public void writeRuleLHSBlocks() throws IOException{

        for(SpikingRuleBlock rule : spikingRules)
            writeRuleLHS(rule);

    }

    public void writeRuleRHSBlocks() throws IOException{

        for(SpikingRuleBlock rule : spikingRules)
            writeRuleRHS(rule);

    }

    public void writeRuleLHS(SpikingRuleBlock rule) throws IOException{

        System.out.println(rule.toString());

        writeRuleRegExp(rule);
        getStream().writeInt(rule.getConsumedSpikes());

    }

    public void writeRuleRHS(SpikingRuleBlock rule) throws IOException{

        getStream().writeInt(rule.getProducedSpikes());
        getStream().writeInt(rule.getDelay());

    }

    public void writeRuleRegExp(SpikingRuleBlock rule) throws IOException{

        String encodedRegExp = encodeRegExp(rule.getRegExp());
        int regExpInt = Integer.parseInt(encodedRegExp,2);
        int regExpLen = encodedRegExp.length();

        String regExp = (rule.getRegExp().length() == 0)?"NONE":rule.getRegExp();

        System.out.println("Regular Expression: " + regExp);
        System.out.println("Integer encoding: " + regExpInt);
        System.out.println("Binary encoding: " + Integer.toBinaryString(regExpInt));
        System.out.println("Hex encoding: " + Integer.toHexString(regExpInt));

        getStream().writeInt(regExpLen);
        getStream().writeInt(regExpInt);

    }

    public void writeLabels() throws IOException{
        //TODO write labels according to format
        return;

    }

    public void writeSynapses() throws IOException{

        SpikingMembraneStructure structure = (SpikingMembraneStructure)getPsystem().getMembraneStructure();
        int size = structure.getAllMembranes().size() - 1;
        
        int[][] matrix = structure.getMatrixRepresentation();

        
        System.out.println("Matrix Representation");
        for(int i = 0; i < size; i++){
            String binary = "";

            for(int j = 0; j < size; j++){
                binary += Integer.toString(matrix[i][j]);
  
                String out = Integer.toString(matrix[i][j]);
                out += (j < size - 1)?",":"";
                System.out.print(out);
            }

            int intRepr = Integer.parseInt(binary,2);
            getStream().writeLong(intRepr);
            System.out.println(" Integer Repr: " + intRepr);
        }
    }

    public void writeFile() throws IOException{ 

        readNeurons();
        readRules();
        System.out.println("===HEADER===");
        writeHeader();
        System.out.println("===SUB-HEADER===");
        writeSubHeader();
        System.out.println("===RULES===");
        writeSpikingRules();
        writeLabels();
        System.out.println("===SYNAPSES===");
        writeSynapses();
        System.out.println("===END===");
        
    }

    public ByteOrder getByteOrder(){

        return ByteOrder.BIG_ENDIAN;

    }

    public byte getFileId(){

        return 0x31;

    }
    
    public String encodeRegExp(String regExp){
        
        if(regExp.length() == 0)
            return REGEXP_NONE; 
        
        String encodedRegExp = "";

        for(int i = 0; i < regExp.length(); i++){
            char curChar = regExp.charAt(i);
            encodedRegExp += encodeRegExpChar(curChar);
        }

        return encodedRegExp;

    }

    public String encodeRegExpChar(char c){
        
        String encodedChar = REGEXP_NONE;

        for(SpikingRegExpChar r : SpikingRegExpChar.values())
            if(c == r.regExpChar()){
                encodedChar = r.encoded();
                break;
            }

       return encodedChar;

    }
}
