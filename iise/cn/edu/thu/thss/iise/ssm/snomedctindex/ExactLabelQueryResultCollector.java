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

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.wordnet.SynonymMap;

/**
 * @author Tao Jin
 * 
 */
public class ExactLabelQueryResultCollector extends Collector {
	private int docBase;
	private Scorer scorer;
	private IndexReader reader;
	private String queryLabel;
	private boolean existQueryLabel = false;

	public ExactLabelQueryResultCollector(IndexReader reader, String queryLabel) {
		this.reader = reader;
		this.queryLabel = queryLabel;
	}

	@Override
	public boolean acceptsDocsOutOfOrder() {
		return true;
	}

	@Override
	public void collect(int doc) throws IOException {
		int docNum = doc + docBase;
		String docLabel = reader.document(docNum)
				.get(LabelDocument.FIELD_LABEL);
		if (queryLabel.equals(docLabel)) {
			this.existQueryLabel = true;
		}
	}

	@Override
	public void setNextReader(IndexReader reader, int docBase)
			throws IOException {
		this.docBase = docBase;
	}

	@Override
	public void setScorer(Scorer scorer) throws IOException {
		this.scorer = scorer;
	}

	/**
	 * @return the existQueryLabel
	 */
	public boolean isExistQueryLabel() {
		return existQueryLabel;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
