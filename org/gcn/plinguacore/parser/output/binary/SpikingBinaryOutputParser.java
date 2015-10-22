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

    private Map<String, Integer> neurons;

    private List<Integer> spikes;

    private List<SpikingRuleBlock> spikingRules;

    public SpikingBinaryOutputParser(){

        neurons = new LinkedHashMap<String,Integer>();
        spikes = new ArrayList<Integer>();
        spikingRules = new ArrayList<SpikingRuleBlock>();

    }

    public void readRules(){

        spikingRules.clear();

        for(IRule rule : getPsystem().getRules()){

            SpikingRuleBlock ruleBlock = new SpikingRuleBlock((SpikingRule)rule);
            spikingRules.add(ruleBlock);

        }

    }

    public void writeHeader() throws IOException{

        System.out.println(0xAF);
        System.out.println(0x12);
        System.out.println(0xFB);

        getStream().write(0xAF);
        getStream().write(0x12);
        getStream().write(0xFB);

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
            getStream().writeByte(spikes);
            initConfig += spikes;
            if(it.hasNext())
                initConfig += ", ";
        }

        initConfig += "}";

        System.out.println(initConfig);


    }


    public void writeSpikingRules() throws IOException{

        for(SpikingRuleBlock rule: spikingRules){
            System.out.println(rule.toString());

            if(rule.getRegExp().length() == 0)
                getStream().writeByte(0);
            else
                getStream().writeBytes(rule.getRegExp());

            getStream().writeByte(rule.getConsumedSpikes());
            getStream().writeByte(rule.getProducedSpikes());
            getStream().writeByte(rule.getDelay());
        }
        
    }

    public void writeLabels() throws IOException{
        //TODO write labels according to format
        return;

    }

    public void writeSynapses() throws IOException{

        SpikingMembraneStructure structure = (SpikingMembraneStructure)getPsystem().getMembraneStructure();
        int size = structure.getAllMembranes().size() - 1;
        
        int[][] matrix = structure.getMatrixRepresentation();

        String binary = "";
        
        System.out.println("Matrix Representation");
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                binary += Integer.toString(matrix[i][j]);
                
                String out = Integer.toString(matrix[i][j]);
                out += (j < size - 1)?",":"";
                System.out.print(out);
            }
            System.out.println("");
        }

        int intRepr = Integer.parseInt(binary,2);

        System.out.println("Integer Encoding: " + intRepr);

        getStream().write(intRepr);

    }

    public void writeFile() throws IOException{ readRules(); System.out.println("===HEADER===");

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
}
