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
package net.cliftonsnyder.svgchart.types;

import java.io.IOException;
import java.io.InputStream;

import net.cliftonsnyder.svgchart.SVGChart;
import net.cliftonsnyder.svgchart.data.DataSet;
import net.cliftonsnyder.svgchart.parse.BarChartDataParser;
import net.cliftonsnyder.svgchart.parse.ParseException;

import org.w3c.dom.Element;

public class BarChart extends SVGChart {
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.cliftonsnyder.svgchart.SVGChart#createChart(org.w3c.dom.Element)
	 */
	public void createChart(Element canvas) {
		// TODO xData?
		double chartX = calculateChartX();
		double chartY = calculateChartY();
		double chartHeight = calculateChartHeight();
		System.err.println("ch: " + chartHeight);
		double chartWidth = calculateChartWidth();

		double globalMinimum = Double.MAX_VALUE;
		double globalMaximum = Double.MIN_VALUE;

		// first pass: find global minimum/maximum
		for (DataSet data : yData) {
			globalMinimum = Math.min(globalMinimum, data.minima[0]);
			globalMaximum = Math.max(globalMaximum, data.maxima[0]);
		}

		double scale = chartHeight / globalMaximum;

		double plotWidth = chartWidth / yData.size();

		// second pass: append the chart to the DOM
		int i = 0;
		for (DataSet data : yData) {
			double x = chartX + plotWidth * i++;
			double rectHeight = data.getPoints().get(0)[0] * scale;

			Element rect = xmldoc.createElement("rect");
			rect.setAttribute("x", "" + x);
			rect.setAttribute("y", "" + (chartHeight + chartY - rectHeight));
			rect.setAttribute("width", "" + plotWidth);
			rect.setAttribute("height", "" + rectHeight);
			rect.setAttribute("class", "_" + data.getName());
			canvas.appendChild(rect);

			if (bottomMargin > 0) { // we have some room to put labels
				Element text = xmldoc.createElement("text");
				text.setAttribute("class", "xlabel");
				text.setAttribute("x", "" + (x + plotWidth / 2));
				text.setAttribute("y", "" + (height - bottomMargin / 2));
				text.appendChild(xmldoc.createTextNode(data.getName()));
				canvas.appendChild(text);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.cliftonsnyder.svgchart.parse.HistogramDataParser#parseInput(java.
	 * io.InputStream)
	 */
	public void parseInput(InputStream in) throws IOException, ParseException {
		BarChartDataParser parser = new BarChartDataParser();
		parser.parseInput(in);
		xData = parser.getXData();
		yData = parser.getYData();
	}
}
