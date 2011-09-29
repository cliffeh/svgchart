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
package net.cliftonsnyder.svgchart.parse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import net.cliftonsnyder.svgchart.data.DataSet;

public class BarChartDataParser {

	private List<double[]> xData;
	private Collection<DataSet> yData;

	public BarChartDataParser() {
		// xData = new ArrayList<Point>(); // only initialize this if we need
		// it!
		yData = new ArrayList<DataSet>();
	}

	public List<double[]> getXData() {
		return xData;
	}

	public Collection<DataSet> getYData() {
		return yData;
	}

	/**
	 * valid histogram input includes:
	 * 
	 * <ul>
	 * <li>one-column*: a list of uncounted items to be counted and represented
	 * in the histogram; one item per line (no x data)</li>
	 * <li>two-column*: a list of already-counted items; two items per line -
	 * (name,value) pairs delimited by one of ParsePatterns.DELIMITERS (no x
	 * data)</li>
	 * <li>three-column (UNIMPLEMENTED!): a list of already-counted items; three
	 * items per line - (x,name,count) tuples delimited by one of
	 * ParsePatterns.DELIMITERS (x data in first column; parser expects input to
	 * be sorted by x column)</li>
	 * </ul>
	 * 
	 * * note: one- and two-column datasets can be mixed; the parser will assume
	 * that there are partial sums included in the input and act accordingly. if
	 * a three-column dataset is included along with one- and/or two-column
	 * datasets, a ParseException will be thrown
	 * 
	 * @param in
	 *            the InputStream from which to read
	 * @return a Collection of DataSet objects suitable for producing a
	 *         histogram if the input is understood; otherwise, null
	 * 
	 * @throws IOException
	 *             if an I/O error occurs
	 * 
	 * @throws ParseException
	 *             if the input is invalid
	 */
	public void parseInput(InputStream in) throws IOException, ParseException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line = br.readLine();

		if (line != null) { // could have empty input
			String[] items = line.split(ParsePatterns.DELIMITERS);

			switch (items.length) {
			case 1: // let's count these up
			case 2: // already counted; we just need to collect the data
			{
				Hashtable<String, Double> counts = new Hashtable<String, Double>();
				do {
					// TODO ignore empty lines
					items = line.split(ParsePatterns.DELIMITERS);
					String key = line;
					double value = 1;
					if (items.length == 2) {
						key = items[0];
						value = Double.parseDouble(items[1]);
					} else if (items.length > 2) {
						throw new ParseException("unexpected data value '"
								+ line + "'");
					}
					double count = counts.containsKey(key) ? counts.get(key)
							+ value : value;
					counts.put(key, count);

				} while ((line = br.readLine()) != null);

				for (String key : counts.keySet()) {
					DataSet data = new DataSet(key);
					data.addPoint(counts.get(key));
					yData.add(data);
				}

				xData = null;
			}
				break;
			case 3: // counted, *with* x data
			{
				// TODO implement 3-column histogram input
				throw new ParseException(
						"3-column histogram input is UNIMPLEMENTED");
			}// break;
			default:
				throw new ParseException("parse error at line '" + line + "'");
			}
		}
	}
}
