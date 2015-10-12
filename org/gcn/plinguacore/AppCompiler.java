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

package org.gcn.plinguacore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.util.Iterator;

import org.gcn.plinguacore.parser.input.InputParser;
import org.gcn.plinguacore.parser.input.InputParserFactory;
import org.gcn.plinguacore.parser.output.OutputParser;
import org.gcn.plinguacore.parser.output.OutputParserFactory;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.parser.AbstractParserFactory;

/**
 * This class provides an entry point for compiling p-systems
 * 
 * @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */

public abstract class AppCompiler {

	/**
	 * Main method for compiling
	 * 
	 * @param args
	 *            the arguments passed to the main compiling method. See the
	 *            documentation for more details
	 *           
	 */
	public static void main(String[] args) {

		compile(args);
	}
	/**
	 * The main method for the compiler program
	 * 
	 * @param args
	 *            the arguments passed to the main compiling method. See the
	 *            documentation for more details
	 */
	private static void compile(String[] args)
	{
		
		int i=0;
		boolean help=false,formats=false,verbosityChanged=false;
		InputParserFactory ipf = new InputParserFactory();
		String inputFile=null,outputFile=null;
		String inputFormat = "P-Lingua";
		String outputFormat = "XML";
		int verbosity=3;
		while(i < args.length && !help && !formats)
		{
			if (args[i].toUpperCase().equals("-H"))
				help=true;
			else
			if (args[i].toUpperCase().equals("-V") && !verbosityChanged)
			{
				if (i+1< args.length)
				{
					i++;
					try {
						verbosity = Integer.parseInt(args[i]);
						if (verbosity<0 || verbosity>5)
							help=true;
					} catch (NumberFormatException ex) {
						help = true;
					}
				}
				else
					help=true;
				verbosityChanged=true;
			}
			else
			if (args[i].toUpperCase().equals("-FORMATS"))
			{
				formats=true;
			}
			else if (AbstractParserFactory.getParserInfo()
					.hasCommandLineOption(args[i].toUpperCase()))
			{
				if (i+1<args.length)
				{
					String formatID = AbstractParserFactory.getParserInfo()
						.getFormatByCommandLineOption(args[i].toUpperCase());
					if (inputFile == null && 
							AbstractParserFactory.getParserInfo().hasInputFormat(formatID))
					{
						inputFormat = formatID;
						i++;
						inputFile=args[i];
					}
					else
					if (outputFile == null && 
							AbstractParserFactory.getParserInfo()
								.hasOutputFormat(formatID)) {
						{
											
					outputFormat = formatID;
					i++;
					outputFile=args[i];
						}
				} else
					help = true;
				}
				else
					help=true;
				
			}
			else
			if (inputFile==null)
				inputFile=args[i];
			else
			if (outputFile==null)
				outputFile=args[i];
			else
				help=true;
			i++;
		}
		
		
		if (formats) {
			Iterator<String> it = AbstractParserFactory.getParserInfo()
					.getInputFormatsIterator();
			System.out.println("Input formats:");
			String key;
			while (it.hasNext()) {
				key = it.next();
				System.out.println(key
						+ ": "
						+ AbstractParserFactory.getParserInfo()
								.getCommandLineOption(key));
			}
			it = AbstractParserFactory.getParserInfo()
					.getOutputFormatsIterator();
			System.out.println("");
			System.out.println("Output formats:");
			while (it.hasNext()) {
				key = it.next();
				System.out.println(key
						+ ": "
						+ AbstractParserFactory.getParserInfo()
								.getCommandLineOption(key));
			}
		} else if (inputFile == null || help)
			printCompilerHelp();

		else {
			if (outputFile == null)
				outputFile = "output.xml";
			/* By default, the output file is "output.xml" */
			try {
				FileInputStream stream = new FileInputStream(inputFile);

				try {
					if (verbosity > 0)
						printCompilerInitMsg();
					/* The input parser is created according to the format */
					InputParser inputParser = (InputParser) ipf
							.createParser(inputFormat);
					inputParser.setVerbosityLevel(verbosity);
					/* The p-system is parsed */
					Psystem ps = inputParser.parse(stream,
							new String[] { inputFile });
					if (ps != null) {
						inputParser.getInfoChannel().println(
								"Generating " + outputFormat + " file...");
						try {
							OutputParserFactory opf = new OutputParserFactory();
							/*
							 * The output parser is created according to the
							 * format
							 */
							OutputParser op = (OutputParser) opf
									.createParser(outputFormat);
							OutputStream file = new FileOutputStream(outputFile);
							
							/* The p-system is parsed onto the output file */
							op.parse(ps, file);

						} catch (IOException ex) {
							inputParser.getErrorChannel().println(
									ex.getMessage());
						}
						inputParser.getInfoChannel().println(
								outputFormat + " file generated");
					}

				} catch (PlinguaCoreException ex) {

					System.out.println(ex.getMessage());
				}
			} catch (FileNotFoundException ex) {

				System.out.println(ex.getMessage());
			}
		}
		
	}

	
	

	/**
	 * A method to show the initial message for the command-line compiler.
	 */
	private static void printCompilerInitMsg() {
		System.out.println("P-LINGUA COMMAND-LINE COMPILER");
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
		System.out.println();
	}

	/**
	 * A method to show the help message for the command-line compiler
	 */
	public static void printCompilerHelp() {

		System.out.println("\nP-Lingua command-line compiler:");
		System.out
				.println("plingua [-h] [-formats] [-input_format] input_file [-output_format] output_file [-v verbosity_level]");
		System.out.println("\nOptions:");
		System.out
				.println("[-input_format]: A valid input format. P-Lingua format by default.");
		System.out.println("input_file: The input file.");
		System.out
				.println("[-output_format]: A valid output format. XML format by default.");
		System.out.println("output_file: The output file.");
		System.out
				.println("[-v verbosity_level]: Verbosity level between 0 and 5. 3 by default.");
		System.out.println("[-h]: Shows this help.");
		System.out.println("[-formats]: Shows available file formats.");
		System.out.println("");
		System.out.println("Examples:");
		System.out.println("plingua sat.pli -bin sat.bin -v 5");
		System.out.println("plingua -xml sat.xml -bin sat.bin");
		System.out.println("plingua -formats");
		System.out.println("plingua -h");

	}

}
