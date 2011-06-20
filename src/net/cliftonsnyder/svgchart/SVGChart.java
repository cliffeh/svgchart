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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.cliftonsnyder.svgchart.data.DataSet;
import net.cliftonsnyder.svgchart.parse.ParseException;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

public abstract class SVGChart {

	public static final double DEFAULT_WIDTH = 800.0;
	public static final double DEFAULT_HEIGHT = 600.0;
	public static final double DEFAULT_BOTTOM_MARGIN = 20.0;
	public static final double DEFAULT_TOP_MARGIN = 20.0;
	public static final double DEFAULT_RIGHT_MARGIN = 20.0;
	public static final double DEFAULT_LEFT_MARGIN = 20.0;

	public static final String DEFAULT_STYLESHEET = "svgchart-style.css";

	public static final String[] TYPES = { "(l)ine", "((h)ist)ogram", "(p)ie",
			"(b)ar" };

	public static final Pattern[] TYPE_PATTERNS = {
			Pattern.compile("(l(ine)?)"),
			Pattern.compile("(h((ist(ogram)?)?))"),
			Pattern.compile("(p(ie)?)"), Pattern.compile("(b(ar)?)") };

	protected Document xmldoc;
	private Element svg;
	protected double width = DEFAULT_WIDTH, height = DEFAULT_HEIGHT,
			topMargin = DEFAULT_TOP_MARGIN,
			bottomMargin = DEFAULT_BOTTOM_MARGIN,
			rightMargin = DEFAULT_RIGHT_MARGIN,
			leftMargin = DEFAULT_LEFT_MARGIN;
	protected String styleSheet = DEFAULT_STYLESHEET;

	protected Collection<DataSet> yData;

	protected List<double[]> xData;

	public SVGChart() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public SVGChart(double width, double height) {
		this.width = width;
		this.height = height;

		// want xData to be null (may not be used!)
		// xData = new ArrayList<Point>();
		yData = new ArrayList<DataSet>();

		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);

			DocumentBuilder builder = factory.newDocumentBuilder();
			xmldoc = builder.newDocument();

			ProcessingInstruction pi = (ProcessingInstruction) xmldoc
					.createProcessingInstruction("xml-stylesheet",
							"type=\"text/css\" href=\"" + styleSheet + "\"");
			xmldoc.appendChild(pi);

			System.err.println("*** " + xmldoc.getDocumentElement());

			DocumentType docType = builder.getDOMImplementation()
					.createDocumentType("svg", "-//W3C//DTD SVG 1.1//EN",
							"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd");
			xmldoc.appendChild(docType);

			svg = xmldoc.createElement("svg");

			svg.setAttribute("xmlns", "http://www.w3.org/2000/svg");
			svg.setAttribute("width", "" + width);
			svg.setAttribute("height", "" + height);

			xmldoc.appendChild(svg);
		} catch (ParserConfigurationException e) {
			// this shouldn't happen...right?
			System.err.println("error generating a new XML document");
			System.exit(1);
		}
	}

	public double calculateChartHeight() {
		return height - (topMargin + bottomMargin);
	}

	public double calculateChartWidth() {
		return width - (leftMargin + rightMargin);
	}

	public double calculateChartX() {
		return leftMargin;
	}

	public double calculateChartY() {
		return topMargin;
	}

	public void createChart() {
		Element canvas = xmldoc.createElement("g");
		canvas.setAttribute("id", "canvas");

		svg.appendChild(canvas);
		createChart(canvas);
	}

	public abstract void createChart(Element canvas);

	public double getBottomMargin() {
		return bottomMargin;
	}

	public double getHeight() {
		return height;
	}

	public double getLeftMargin() {
		return leftMargin;
	}

	public double getRightMargin() {
		return rightMargin;
	}

	public String getStyleSheet() {
		return styleSheet;
	}

	public double getTopMargin() {
		return topMargin;
	}

	public double getWidth() {
		return width;
	}

	public abstract void parseInput(InputStream in) throws IOException,
			ParseException;

	public void printChart(PrintStream out, boolean indent) throws IOException {
		DOMImplementationRegistry registry = null;
		try {
			registry = DOMImplementationRegistry.newInstance();
		} catch (ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DOMImplementationLS impl = (DOMImplementationLS) registry
				.getDOMImplementation("LS");

		LSSerializer writer = impl.createLSSerializer();
		writer.getDomConfig().setParameter("format-pretty-print", true);
		LSOutput output = impl.createLSOutput();
		output.setByteStream(out);
		writer.write(xmldoc, output);

	}

	public void setBottomMargin(double bottomMargin) {
		this.bottomMargin = bottomMargin;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public void setLeftMargin(double leftMargin) {
		this.leftMargin = leftMargin;
		calculateChartX();
		calculateChartWidth();
	}

	public void setRightMargin(double rightMargin) {
		this.rightMargin = rightMargin;
	}

	public void setStyleSheet(String styleSheet) {
		this.styleSheet = styleSheet;
	}

	public void setTopMargin(double topMargin) {
		this.topMargin = topMargin;
	}

	public void setWidth(double width) {
		this.width = width;
	}
}
