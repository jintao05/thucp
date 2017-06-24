package model.TCPM;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ProcessMining {
	public TopicSequences topicSequences;
	public TopicSequences noLoopTopicSequences;
	public TopicSequences maxTopicSequences;
	public Map<String, Integer> oriLabels2num;
	public Map<String, String> ori2tgtLabels;
	Map<String, String> noLoopSeqStr2maxSequStr;

	public ProcessMining(TopicSequences topicSequences) {
		this.topicSequences = topicSequences;
		this.oriLabels2num = new HashMap<String, Integer>();
		this.ori2tgtLabels = new HashMap<String, String>();
	}

	public void calcLabels2Num() {
		for (TopicSequence ts : topicSequences.sequences) {
			for (String tLabel : ts.tLabels) {
				if (!oriLabels2num.containsKey(tLabel)) {
					oriLabels2num.put(tLabel, 1);
				} else {
					int c = oriLabels2num.get(tLabel);
					oriLabels2num.put(tLabel, c + 1);
				}
			}
		}
	}

	// 合并topic，需要修改的label存在ori2tgtLabels中，返回的是剩余标签数
	public int pruneNodes(int minNum) {
		ori2tgtLabels.putAll(PreTree.processLabels(oriLabels2num, minNum)); // 得到需要删除label的对应关系
		// System.out.println("delete labels.size: " + ori2tgtLabels.size());
		return oriLabels2num.size() - ori2tgtLabels.size();
	}

	public void removeLoop() {
		noLoopTopicSequences = new TopicSequences(topicSequences);
		for (TopicSequence ts : noLoopTopicSequences.sequences) {
			ArrayList<String> noSelfLoopTLabels = removeSelfLoop(ts.tLabels);
			ArrayList<String> noInclusionLoopTLabels = removeInclusionLoop(noSelfLoopTLabels);
			ts.tLabels = noInclusionLoopTLabels;
		}
	}

	public void mergeSubSequence() {
		maxTopicSequences = new TopicSequences(noLoopTopicSequences);
		noLoopSeqStr2maxSequStr = new HashMap<String, String>();
		List<TopicSequence> tsList = noLoopTopicSequences.sequences;
		Collections.sort(tsList, new SortTSListByTLabelSize());
		for (int i = tsList.size() - 1; i >= 0; i--) { // 从长度最短的process开始，与最长的process逐一比较
			String iStr = tsList.get(i).tLabels.toString();
			for (int j = 0; j < i; j++) {
				String jStr = tsList.get(j).tLabels.toString();
				boolean flag;
				if (iStr.equals(jStr)) { // 如果两个sequence相同，不认为是sub关系
					flag = false;
				} else {
					flag = isSubSequence(jStr, iStr);
				}
				if (flag) { // 如果iStr是jStr的子序，则将其添入map
					noLoopSeqStr2maxSequStr.put(iStr, jStr);
					TopicSequence newTs = new TopicSequence(tsList.get(i));
					ArrayList<String> list = new ArrayList<String>();
					String[] strs = jStr.split("\t");
					list.addAll(Arrays.asList(strs));
					newTs.tLabels = list;
				}
			}
		}
	}

	public ArrayList<String> removeSelfLoop(ArrayList<String> tLabels) {
		ArrayList<String> aList = new ArrayList<String>();
		String preStr = "";
		for (String str : tLabels) {
			if (str.equals(preStr)) { // 如果当前str与前一str相同，则跳过（自循环）
				continue;
			}
			preStr = str;
			aList.add(str);
		}

		return aList;
	}

	public ArrayList<String> removeInclusionLoop(ArrayList<String> tLabels) {
		ArrayList<String> resultList = new ArrayList<String>();
		List<ArrayList<Integer>> lList = new ArrayList<ArrayList<Integer>>();
		for (String str : tLabels) {
			ArrayList<Integer> aList = new ArrayList<Integer>();
			String[] strs = str.split(",");
			for (String s : strs) {
				int l = Integer.parseInt(s);
				aList.add(l);
			}
			lList.add(aList);
		}

		List<ArrayList<Integer>> noLoopLList = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < lList.size(); i++) {
			String iLabels = lList.get(i).toString();
			int j = i + 2; // 由于已经进行了self loop压缩，所以只需要考虑i和i+2这两个label之间的关系
			if (j < lList.size()) {
				String jLabels = lList.get(j).toString();
				if (iLabels.equals(jLabels)) {
					boolean flag = isSubLabel(lList.get(i), lList.get(i + 1)); // 如果i是否完全包含i+1，如果包含，则压缩
					if (flag) {
						i++;
						continue;
					}
				}
			} else {
				for (int k = i; k < lList.size(); k++) {
					noLoopLList.add(lList.get(k));
				}
				break;
			}
			noLoopLList.add(lList.get(i));
		}

		for (int i = 0; i < noLoopLList.size(); i++) {
			String str = "";
			for (int j = 0; j < noLoopLList.get(i).size(); j++) {
				str += noLoopLList.get(i).get(j) + ",";
			}
			str = str.substring(0, str.length() - 1);
			resultList.add(str);
		}
		return resultList;
	}

	// 判断pLab是否包含sLab，例如1,2,5包含1,2，但是不包含1,3（即必须完全包含，而不是有共同部分）
	public boolean isSubLabel(List<Integer> pLab, List<Integer> sLab) {
		boolean flag = true;
		for (Integer s : sLab) { // 对于sLab中的每一个topic，判断是否
			if (!pLab.contains(s)) {
				return false;
			}
		}

		return flag;
	}

	public boolean isSubSequence(String parentStr, String subStr) {
		boolean flag = false;
		parentStr += "\t";
		subStr += "\t";
		String pattern = "(^|\t)";
		for (String str : subStr.split("\t")) {
			pattern += str + "\t" + "(((\\d)+,)*(\\d)+\t)*";
		}
		Pattern r = Pattern.compile(pattern);
		flag = r.matcher(parentStr).find();
		return flag;
	}

	class SortTSListByTLabelSize implements Comparator<Object> {
		@Override
		public int compare(Object o1, Object o2) {
			TopicSequence ts1 = (TopicSequence) o1;
			TopicSequence ts2 = (TopicSequence) o2;
			int size1 = ts1.tLabels.size();
			int size2 = ts2.tLabels.size();
			if (size1 < size2) {
				return 1;
			} else if (size1 > size2) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	public void generateXESFile(String xesFilePath) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();

		Element logEle = doc.createElement("log");
		logEle.setAttribute("xes.version", "1.0");
		logEle.setAttribute("xes.features", "nested-attributes");
		logEle.setAttribute("openxes.version", "1.0RC7");
		logEle.setAttribute("xmlns", "http://www.xes-standard.org/");
		doc.appendChild(logEle);

		for (TopicSequence ts : maxTopicSequences.sequences) {
			String pID = ts.pID;

			Element traceEle = doc.createElement("trace");
			logEle.appendChild(traceEle);

			Element stringEle = doc.createElement("string");
			stringEle.setAttribute("key", "concept:name");
			stringEle.setAttribute("value", pID);

			int year = 1900;
			for (String tLabel : ts.tLabels) {
				String start = year + "-01-01";

				Element eventEle = doc.createElement("event");

				Element stringEle1 = doc.createElement("string");
				stringEle1.setAttribute("key", "org:resource");
				stringEle1.setAttribute("value", "UNDEFINED");

				Element dateEle = doc.createElement("date");
				dateEle.setAttribute("key", "time:timestamp");
				dateEle.setAttribute("value", start + "T00:00:00.000+00:00");

				Element stringEle2 = doc.createElement("string");
				stringEle2.setAttribute("key", "concept:name");
				stringEle2.setAttribute("value", tLabel);

				Element stringEle3 = doc.createElement("string");
				stringEle3.setAttribute("key", "liftcycle:transition");
				stringEle3.setAttribute("value", "complete");

				eventEle.appendChild(stringEle1);
				eventEle.appendChild(dateEle);
				eventEle.appendChild(stringEle2);
				eventEle.appendChild(stringEle3);

				traceEle.appendChild(eventEle);
				year++;
			}
		}

		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.setOutputProperty(OutputKeys.INDENT, "yes");
		t.setOutputProperty(OutputKeys.METHOD, "xml");
		t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(new File(xesFilePath))));
	}

	public void generateDiscoFile(String discoFilePath) throws Exception {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(discoFilePath), "UTF-8"));

		for (TopicSequence ts : maxTopicSequences.sequences) {
			String pID = ts.pID;

			int year = 1900;
			for (String tLabel : ts.tLabels) {
				String date = year + "-01-01";
				tLabel = tLabel.replace(",", " ");
				tLabel = "[" + tLabel + "]";
				bw.append(pID + "," + tLabel + "," + date + "," + date + "\r\n");
				year++;
			}
		}

		bw.close();
	}
}
