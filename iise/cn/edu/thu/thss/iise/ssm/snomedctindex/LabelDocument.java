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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;

/**
 * @author Tao Jin
 * 
 *         get lucene document from every label
 * 
 */
public class LabelDocument {

	public static final String FIELD_LABEL = "label";

	private LabelDocument() {

	}

	public static Document Document(String label) {
		Document doc = new Document();
		Field fLabel = new Field(FIELD_LABEL, label, Field.Store.YES,
				Field.Index.ANALYZED_NO_NORMS, Field.TermVector.YES);
		fLabel.setOmitTermFreqAndPositions(true);
		doc.add(fLabel);
		return doc;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
