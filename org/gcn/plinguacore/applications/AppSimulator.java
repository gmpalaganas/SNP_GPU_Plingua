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

package org.gcn.plinguacore.applications;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import org.gcn.plinguacore.parser.AbstractParserFactory;
import org.gcn.plinguacore.parser.input.InputParser;
import org.gcn.plinguacore.parser.input.InputParserFactory;
import org.gcn.plinguacore.simulator.ISimulator;
import org.gcn.plinguacore.util.psystem.Psystem;

/**
 * This class provides an entry point for simulating p-systems
 * 
 * @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public abstract class AppSimulator {

	/**
	 * Main method for simulating
	 * 
	 * @param args
	 *            the arguments passed to the main simulating method. See the
	 *            documentation for more details
	 */
	public static void main(String[] args) {

		AppSimulator.simulate(args);
	}

	/*
	 * Prints simulator help
	 */
	public static void printSimulatorHelp() {
		System.out.println("\nP-Lingua command-line simulator:");
		System.out
				.println("plingua_sim [-h] [-input_format] input_file -o output_file [-to timeout] [-st steps] [-mode simulatorID] [-a] [-b] [-v verbosity_level]");
		System.out.println("\nOptions:");
		System.out.println("[-h]: Print this help information.");
		System.out.println("[-input_format]: Select an input format file.");
		System.out.println("input_file: The input file.");
		System.out.println("output_file: The output file.");
		System.out.println("[-to timeout]: A timeout in milliseconds.");
		System.out.println("[-st steps]: Number of steps of simulation.");
		System.out.println("[-a]: Simulation with alternative steps");
		System.out.println("[-b]: Simulation with steps back");
		System.out.println("[-v verbosity]: Verbosity level, 0-5");
	}

	/**
	 * prints simulator initial message
	 */
	private static void printSimulatorInitMsg() {
		System.out.println("P-LINGUA COMMAND-LINE SIMULATOR");
		System.out.println("");
		System.out
				.println("Copyright (C) 2009 Research Group On Natural Computing");
		System.out.println("                   http://www.gcn.us.es/");
		System.out.println("");
		System.out.println("This program comes with ABSOLUTELY NO WARRANTY.");
		System.out
				.println("This is free software, and you are welcome to redistribute it");
		System.out
				.println("under the conditions of the GNU General Public License version 3,");
		System.out
				.println("for details see the file gpl.txt or http://www.gnu.org/licenses/gpl.html");
		System.out.println("");
		System.out
				.println("For more information about P-Lingua see http://www.p-lingua.org/");
	}

	/**
	 * Simulates a P-system according to the given parameters
	 * 
	 * @param args
	 *            the arguments passed to the simulation. See the documentation
	 *            for more details
	 */
	private static void simulate(String[] args) {

		String inputFile = null, outputFile = null;
		String inputFormat = "XML";
		boolean verbose = false, help = false, modes = false, input = false, output = false, mode = false;
		boolean stepBackSupported = false, alternativeStepsSupported = false, st = false, to = false;
		int verbosity = 3;
		String simulatorID = null;
		int timeOut = 0;
		int steps = 0;

		for (int i = 0; i < args.length && !help && !modes; i++) {
			if (mode) {
				simulatorID = args[i];
				mode = false;
			} else if (input) {
				inputFile = args[i];
				input = false;
				File file = new File(inputFile);
				if (!file.exists() || !file.isFile())
					help = true;
			} else if (output) {
				outputFile = args[i];
				output = false;
			} else if (verbose) {
				try {
					verbosity = Integer.parseInt(args[i]);
					verbose = false;
				} catch (NumberFormatException ex) {
					help = true;
				}
			} else if (st) {
				try {
					steps = Integer.parseInt(args[i]);
					st = false;
				} catch (NumberFormatException ex) {
					help = true;
				}
			} else if (to) {
				try {
					timeOut = Integer.parseInt(args[i]);
					to = false;
				} catch (NumberFormatException ex) {
					help = true;
				}
			} else if (args[i].toUpperCase().equals("-V"))
				verbose = true;
			else if (args[i].toUpperCase().equals("-MODES"))
				modes = true;
			else if (args[i].toUpperCase().equals("-H"))
				modes = true;
			else if (args[i].toUpperCase().equals("-O"))
				output = true;
			else if (args[i].toUpperCase().equals("-A"))
				alternativeStepsSupported = true;
			else if (args[i].toUpperCase().equals("-B"))
				stepBackSupported = true;
			else if (args[i].toUpperCase().equals("-ST"))
				st = true;
			else if (args[i].toUpperCase().equals("-TO"))
				to = true;
			else if (args[i].toUpperCase().equals("-MODE"))
				mode = true;

			else if (AbstractParserFactory.getParserInfo()
					.hasCommandLineOption(args[i].toUpperCase())) {
				String formatID = AbstractParserFactory.getParserInfo()
						.getFormatByCommandLineOption(args[i].toUpperCase());
				if (inputFile == null
						&& AbstractParserFactory.getParserInfo()
								.hasInputFormat(formatID)) {
					inputFormat = formatID;
					input = true;
				} else
					help = true;
			} else if (inputFile == null) {
				inputFile = args[i];
				File file = new File(inputFile);
				if (!file.exists() || !file.isFile())
					help = true;
			} else
				help = true;

		}

		if (inputFile == null || outputFile == null)
			help = true;

		if (help)
			printSimulatorHelp();
		else

		{

			try {
				printSimulatorInitMsg();
				/* For simulating, the input file should fulfill XML format */
				InputParserFactory ipf = new InputParserFactory();
				InputParser parser = (InputParser) ipf
						.createParser(inputFormat);
				parser.setVerbosityLevel(verbosity);
				Psystem ps = parser.parse(new FileInputStream(inputFile),
						new String[] { inputFile });
				ISimulator sim;
				if (simulatorID == null)
					/*
					 * The simulator is created according to the options
					 * selected
					 */
					sim = ps.createSimulator(stepBackSupported,
							alternativeStepsSupported);
				else
					sim = ps.createSimulator(stepBackSupported,
							alternativeStepsSupported, simulatorID);
				FileOutputStream stream = new FileOutputStream(outputFile);
				sim.setInfoChannel(new PrintStream(stream));
				/* The simulation is committed according to the parameters given */
				sim.setTimed(true);
				if (timeOut > 0) {
					if (steps > 0)
						sim.runUntilTimeOutorSteps(timeOut, steps);
					else
						sim.runUntilTimeOut(timeOut);
				} else {
					if (steps > 0)
						sim.runSteps(steps);
					else
						sim.run();
				}
				printSimulationInfo(sim);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}

	}

	/*
	 * Print simulation info
	 */
	private static void printSimulationInfo(ISimulator sim) {
		System.out.println("Environment: "
				+ sim.getCurrentConfig().getEnvironment());
		System.out.println("Steps: " + sim.getCurrentConfig().getNumber());
		System.out.println("Time: " + sim.getTime() / 1000 + " s.");
		System.out
				.println("Halting configuration (No rule can be selected to be executed in the next step)");
	}

}
