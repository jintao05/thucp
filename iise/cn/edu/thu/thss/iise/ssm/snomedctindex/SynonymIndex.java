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

import java.io.FileInputStream;

import org.apache.lucene.wordnet.SynonymMap;

/**
 * used for getting all the synonyms of a given word quickly. this index will be
 * constructed only once.
 *
 * @author Tao Jin
 *
 */
public class SynonymIndex {

	private static SynonymMap synMap = null;

	private SynonymIndex() {

	}

	public static SynonymMap getSynonymMap() {
		try {
			if (synMap == null) {
				synMap = new SynonymMap(new FileInputStream("wn_s.pl"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return synMap;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SynonymMap synMap = SynonymIndex.getSynonymMap();
		System.out.println("the synonyms of ship are as follows.");
		for (String s : synMap.getSynonyms("ship")) {
			System.out.print(s + ", ");
		}

		System.out.println();
		System.out.println("the synonyms of send are as follows.");
		for (String s : synMap.getSynonyms("send")) {
			System.out.print(s + ", ");
		}
	}

}
