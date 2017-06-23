package model.TCPM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessMining {
	public TopicSequences topicSequences;
	public Map<String, Integer> oriLabels2num;
	public Map<String, String> ori2tgtLabels;

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
		System.out.println("delete labels.size: " + ori2tgtLabels.size());
		return oriLabels2num.size() - ori2tgtLabels.size();
	}

}
