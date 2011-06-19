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

public class ParsePatterns {
	public static final String DELIMITERS = "[,:;\\s]";
	public static final String NON_DELIMITERS = "[^,:;\\s]";
	public static final String NUMBER = "(((\\d+)?\\.)?\\d+)";

	// public static final Pattern DELIMITED_LINE_PATTERN = Pattern.compile("("
	// + NON_DELIMITERS + "+" + DELIMITERS + ")+" + NON_DELIMITERS + "+");
	// public static final Pattern NON_DELIMITED_LINE_PATTERN = Pattern
	// .compile("(" + NON_DELIMITERS + ")+");

}
