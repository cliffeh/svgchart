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
package net.cliftonsnyder.svgchart.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataSet implements Iterable<double[]> {

	private String name;
	public double[] minima, maxima;
	private List<double[]> points;

	/**
	 * create an empty dataset
	 */
	public DataSet() {
		this(null, new ArrayList<double[]>());
	}

	/**
	 * create an empty dataset with name <em>name</em>
	 * 
	 * @param name
	 *            the name of the dataset
	 */
	public DataSet(String name) {
		this(name, new ArrayList<double[]>());
	}

	/**
	 * create an empty dataset with name <em>name</em> initialized with data
	 * points <em>points</em>
	 * 
	 * @param name
	 *            the name of the dataset
	 * @param points
	 *            the data points
	 */
	public DataSet(String name, List<double[]> points) {
		this.name = name;
		setPoints(points);
	}

	public void addPoint(double... p) {
		points.add(p);
		checkMinima(p);
		checkMaxima(p);
	}

	public String getName() {
		return name;
	}

	public List<double[]> getPoints() {
		return points;
	}

	@Override
	public Iterator<double[]> iterator() {
		return points.iterator();
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPoints(List<double[]> points) {
		this.points = points;
		// complete reset of errything
		minima = null;
		maxima = null;
		for (double[] d : points) {
			checkMinima(d);
			checkMaxima(d);
		}
	}

	private void checkMinima(double[] d) {
		if (minima == null) {
			minima = new double[d.length];
			for (int i = 0; i < minima.length; i++)
				minima[i] = Double.MAX_VALUE;
		}
		for (int i = 0; i < minima.length; i++) {
			minima[i] = Math.min(minima[i], d[i]);
		}
	}

	private void checkMaxima(double[] d) {
		if (maxima == null) {
			maxima = new double[d.length];
			for (int i = 0; i < maxima.length; i++)
				maxima[i] = Double.MIN_VALUE;
		}
		for (int i = 0; i < maxima.length; i++) {
			maxima[i] = Math.max(maxima[i], d[i]);
		}
	}
}
