/*
 * svgchart - create and print charts using SVG
 * 
 * Copyright (c) 2011 Clifton Snyder <cliff@cliftonsnyder.net>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301, USA.
 */
package net.cliftonsnyder.svgchart;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.cliftonsnyder.svgchart.types.Histogram;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

	private static final String USAGE = "svgchart -t TYPE [OPTIONS]";

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("c", "stylesheet", true, "CSS stylesheet (default: "
				+ SVGChart.DEFAULT_STYLESHEET + ")");
		options.addOption("h", "height", true, "chart height");
		options.addOption("i", "input-file", true,
				"input file [default: stdin]");
		options.addOption("o", "output-file", true,
				"output file [default: stdout]");
		options.addOption("w", "width", true, "chart width");
		options.addOption("?", "help", false, "print a brief help message");

		Option type = new Option("t", "type", true, "chart type "
				+ Arrays.toString(SVGChart.TYPES));
		type.setRequired(true);
		options.addOption(type);

		CommandLineParser parser = new GnuParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine line = null;
		try {
			// parse the command line arguments
			line = parser.parse(options, args);
			if (line.hasOption("help")) {
				formatter.printHelp(USAGE, options);
				System.exit(0);
			}
		} catch (ParseException exp) {
			// oops, something went wrong
			System.err.println("unable to parse command line: "
					+ exp.getMessage());
			formatter.printHelp(USAGE, options);
			System.exit(1);
		}

		SVGChart chart = null;
		String tmp = line.getOptionValue("type");
		Matcher m = null;
		for (Pattern p : SVGChart.TYPE_PATTERNS) {
			if ((m = p.matcher(tmp)).matches()) {
				switch (m.group().charAt(0)) {
				case 'l':
					// DEBUG
					System.err.println("line");
					break;
				case 'h':
					System.err.println("histogram");
					chart = new Histogram();
					break;
				case 'b':
					System.err.println("bar");
					break;
				case 'p':
					System.err.println("pie");
					break;
				default:
					System.err.println("unknown or unimplemented chart type: '"
							+ tmp + "'");
					System.exit(1);
				}
			}
		}

		try {
			chart.setWidth(Double.parseDouble(line.getOptionValue("width", ""
					+ SVGChart.DEFAULT_WIDTH)));
		} catch (NumberFormatException e) {
			System.err
					.println("unable to parse command line: invalid width value '"
							+ line.getOptionValue("width") + "'");
			System.exit(1);
		}

		try {
			chart.setHeight(Double.parseDouble(line.getOptionValue("height", ""
					+ SVGChart.DEFAULT_HEIGHT)));
		} catch (NumberFormatException e) {
			System.err
					.println("unable to parse command line: invalid height value '"
							+ line.getOptionValue("height") + "'");
			System.exit(1);
		}

		InputStream in = System.in;
		tmp = line.getOptionValue("input-file", "-");
		if ("-".equals(tmp)) {
			in = System.in;
		} else {
			try {
				in = new FileInputStream(tmp);
			} catch (FileNotFoundException e) {
				System.err.println("input file not found: '" + tmp + "'");
				System.exit(1);
			}
		}

		PrintStream out = System.out;
		tmp = line.getOptionValue("output-file", "-");
		if ("-".equals(tmp)) {
			out = System.out;
		} else {
			try {
				out = new PrintStream(new FileOutputStream(tmp));
			} catch (FileNotFoundException e) {
				System.err.println("output file not found: '" + tmp + "'");
				System.exit(1);
			}
		}

		tmp = line.getOptionValue("stylesheet", SVGChart.DEFAULT_STYLESHEET);
		chart.setStyleSheet(tmp);

		try {
			chart.parseInput(in);
		} catch (IOException e) {
			System.err.println("I/O error while reading input");
			System.exit(1);
		} catch (net.cliftonsnyder.svgchart.parse.ParseException e) {
			System.err.println("error parsing input: " + e.getMessage());
		}

		chart.createChart();

		try {
			chart.printChart(out, true);
		} catch (IOException e) {
			System.err.println("error serializing output");
			System.exit(1);
		}
	}
}
