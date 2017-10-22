/**
 * BeehiveZ is a business process model and instance management system.
 * Copyright (C) 2011  
 * Institute of Information System and Engineering, School of Software, Tsinghua University,
 * Beijing, China
 *
 * Contact: jintao05@gmail.com 
 *
 * This program is a free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation with the version of 2.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package cn.edu.thu.thss.iise.ssm.snomedctindex;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * @author Tao Jin
 * 
 */
public class SimilarLabelQueryResult implements
		Comparable<SimilarLabelQueryResult> {

	private String label;
	private float similarity = 1;

	public SimilarLabelQueryResult(String label, float similarity) {
		this.label = label;
		this.similarity = similarity;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the similarity
	 */
	public float getSimilarity() {
		return similarity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SimilarLabelQueryResult) {
			SimilarLabelQueryResult temp = (SimilarLabelQueryResult) obj;
			if (this.label.equals(temp.label)
					&& this.similarity == temp.similarity) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(label =" + this.label + " , similarity = " + this.similarity
				+ ")";
	}

	@Override
	public int compareTo(SimilarLabelQueryResult o) {
		if (this.similarity > o.similarity) {
			return 1;
		} else if (this.similarity < o.similarity) {
			return -1;
		} else {
			return this.label.compareTo(o.label);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SimilarLabelQueryResult obj1 = new SimilarLabelQueryResult("a", 0.9f);
		SimilarLabelQueryResult obj2 = new SimilarLabelQueryResult("b", 0.7f);
		SimilarLabelQueryResult obj3 = new SimilarLabelQueryResult("aa",0.9f);
		TreeSet<SimilarLabelQueryResult> set = new TreeSet<SimilarLabelQueryResult>();
		set.add(obj1);
		set.add(obj2);
		set.add(obj3);
		Iterator<SimilarLabelQueryResult> iter = set.iterator();
		while (iter.hasNext()) {
			SimilarLabelQueryResult obj = iter.next();
			System.out.println(obj.toString());
		}
	}

}
