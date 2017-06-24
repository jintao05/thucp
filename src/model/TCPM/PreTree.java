package model.TCPM;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import model.TCPM.PreTreeNode.NodeType;

public class PreTree {
	PreTreeNode root;
	boolean shouldDelRoot; // 记录是否请求删除根节点

	PreTree() {
		root = null;
		shouldDelRoot = false;
	}

	PreTree(String label, Map<String, Integer> labelMap) { // label的格式为1,5
		String[] labels = label.split(",");
		int labelSize = labels.length; // 该label包含topic数量

		root = new PreTreeNode(label);
		root.nodeType = NodeType.pre;

		List<PreTreeNode> children = new ArrayList<PreTreeNode>();

		int count = labelMap.get(label);
		if (count > 0) { // 如果count大于0，说明不是一个空的pre节点，为其生成一个leaf节点
			PreTreeNode selfNode = new PreTreeNode(NodeType.leaf, label, count, root, null); // 构建一个leaf
																								// node
			children.add(selfNode);
		}

		Iterator<Entry<String, Integer>> it = labelMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> entry = it.next();
			String key = entry.getKey();
			int value = entry.getValue();
			int len = key.split(",").length;
			// if (len == labelSize + 1 && key.indexOf(label) == 0) {
			if (len == labelSize + 1 && isPreLabel(label, key)) {
				PreTree subTree = new PreTree(key, labelMap);
				children.add(subTree.root); // 将该子树的root，添加到当前root的child列表
				count += subTree.root.count; // 更新当前root的count（累加所有child的count）
				subTree.root.parent = this.root; // 将该字数root的parent，设置为当前root
			}
		}

		root.children = children; // 设置当前root的child
		root.count = count;
	}

	public static Map<String, Integer> shuffleLabelMap( // 根据实际labelMap，创建缺失的preLabel
			Map<String, Integer> labelMap) {
		Map<String, Integer> preTreeMap = new HashMap<String, Integer>();

		int maxLabelLen = 0;
		Iterator<Entry<String, Integer>> it = labelMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> entry = it.next();
			String key = entry.getKey();
			int value = entry.getValue();
			int len = key.split(",").length;
			if (len > maxLabelLen) {
				maxLabelLen = len;
			}
			preTreeMap.put(key, value);
		}

		for (int i = 1; i <= maxLabelLen; i++) {
			it = labelMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Integer> entry = it.next();
				String key = entry.getKey();
				int value = entry.getValue();
				String[] labels = key.split(",");
				int len = labels.length;
				if (len >= i) {
					String preLabel = getPreLabel(key, i);
					if (!preTreeMap.containsKey(preLabel)) { // 如果preTreeMap不包含该前缀label，则将其添入，数量为0
						preTreeMap.put(preLabel, 0);
					}
				}
			}
		}

		return preTreeMap;
	}

	public Map<String, String> deleteNodes(Map<Integer, ArrayList<String>> len2DelCandidLabels, int minNum) {
		Map<String, String> targetLabelsMap = new HashMap<String, String>();

		int maxLabelLen = 0;
		Set<Integer> ks = len2DelCandidLabels.keySet();
		for (int l : ks) {
			if (l > maxLabelLen) {
				maxLabelLen = l;
			}
		}

		for (int i = maxLabelLen; i >= 1; i--) { // 从最长的label开始删除
			int len = i;
			if (len2DelCandidLabels.containsKey(len)) {
				List<String> aList = len2DelCandidLabels.get(len);
				for (String lab : aList) {
					PreTreeNode targetNode = this.deleteNode(lab);
					if (targetNode == null) {
						System.out.println("删除失败，label: " + lab);
						continue;
					}
					String targetLabel = targetNode.labels;
					int targetLen = targetLabel.split(",").length; // 根据删除算法，targetLen应该是len-1
					assert (targetLen != len - 1) : "长度错啦！";
					int targetCount = targetNode.count;
					ArrayList<String> tempList = len2DelCandidLabels.get(targetLen);
					if (tempList == null) {
						tempList = new ArrayList<String>();
					}
					if (!tempList.contains(targetLabel) && targetCount < minNum) { // 如果删除当前节点生成的目标节点，其count小于阈值，且不在删除列表中，则将其添入删除列表，等待下一轮删除
						tempList.add(targetLabel);
						len2DelCandidLabels.put(targetLen, tempList);
					} else if (tempList.contains(targetLabel) && targetCount >= minNum) { // 如果目标节点也在删除列表中，且其count大于等于阈值，则无需删除，将其移出删除列表
						tempList.remove(targetLabel);
						len2DelCandidLabels.put(targetLen, tempList);
					}
				}
			}
		}

		Iterator<Entry<Integer, ArrayList<String>>> it = len2DelCandidLabels.entrySet().iterator();
		while (it.hasNext()) { // 遍历需要删除的所有节点（部分原本需要删除的节点，由于节点合并无需删除，不在现有len2DelCandidLabels中）
			Entry<Integer, ArrayList<String>> entry = it.next();
			List<String> aList = entry.getValue();
			for (String lab : aList) {
				int labSize = lab.split(",").length;
				// for (int i = lab.length(); i >= 0; i = i - 2) {
				for (int i = labSize; i > 0; i--) { // 从长到短，取lab的前缀，在树中搜索
					String preLab = getPreLabel(lab, i);
					PreTreeNode ptn = this.queryNode(this.root, preLab, NodeType.leaf);
					if (ptn != null) { // 如果找到该前缀对应的叶子节点，则表明删除的lab现在对应于该叶子节点
						targetLabelsMap.put(lab, preLab);
						break;
					}
				}
			}
		}

		return targetLabelsMap;
	}

	public PreTreeNode deleteNode(String delLabel) {
		if (delLabel.equals(this.root.labels)) { // 如果需要删除的节点是root节点，则直接返回
			System.out.println("删除root?");
			shouldDelRoot = true;
			return null;
		}

		PreTreeNode ptn = this.queryNode(this.root, delLabel, NodeType.leaf);
		if (ptn == null)
			return null;

		PreTreeNode parentNode = ptn.parent;
		PreTreeNode parentParentNode = parentNode.parent;
		parentNode.children.remove(ptn); // 从父节点的children中移除该节点（父节点肯定是pre节点）
		parentNode.count -= ptn.count; // 改变父节点的count
		if (parentNode.children.size() == 0) {
			parentParentNode.children.remove(parentNode); // 如果移除该节点后，父节点children为空，则从父父节点中删除该父节点
		}
		PreTreeNode targetNode = this.queryNode(parentParentNode, parentParentNode.labels, NodeType.leaf); // 寻找目标leaf节点
		if (targetNode != null) { // 如果目标节点存在，则更改其count
			targetNode.count += ptn.count;
		} else { // 如果目标节点不存在，则新建
			// String newLabel = ptn.labels.substring(0, ptn.labels.length() -
			// 2);
			String newLabel = parentParentNode.labels;
			targetNode = new PreTreeNode(NodeType.leaf, newLabel, ptn.count, parentParentNode, null);
			parentParentNode.children.add(targetNode);
		}

		return targetNode;
	}

	public PreTreeNode queryNode(PreTreeNode currentNode, String queryLabels, NodeType type) {
		PreTreeNode ptn = null;
		if (currentNode.labels.equals(queryLabels) && currentNode.nodeType == type) { // 如果当前节点就是搜索节点，直接返回该节点
			return currentNode;
		} else {
			if (currentNode.children != null) {
				for (PreTreeNode p : currentNode.children) { // 遍历当前节点所有子节点（这里可以优化，判断前缀，决定是否遍历）
					ptn = queryNode(p, queryLabels, type);
					if (ptn != null)
						return ptn;
				}
			}
		}
		return ptn;
	}

	public static void printPreTree(PreTree pt) {
		PreTreeNode.printPreTreeNode(pt.root, "");
	}

	public static void main(String args[]) {
		testCreateTree();
		System.out.println("Game Over!!!");
	}

	/**
	 *
	 * @param wholeLabelMap
	 *            所有label，key是label（如1,5），value是该label的个数
	 * @param minNum
	 *            minNum是最少节点个数，小于minNum的节点，需要被删除
	 * @return 返回所有需要修改的节点映射关系
	 */
	public static Map<String, String> processLabels(Map<String, Integer> wholeLabelMap, int minNum) {
		Map<String, String> resultMap = new HashMap<String, String>(); // 删除节点的映射关系（key是需要删除的label，value是改变后的label）

		// key是起始label，value为key开头的所有label映射
		// 例如{"1":{"1":5}{"1,2":4} {"1,3,6":1}} {"2":{"2":3} {"2,1":2}}
		Map<String, HashMap<String, Integer>> mMap = new HashMap<String, HashMap<String, Integer>>();
		Iterator<Entry<String, Integer>> wlIt = wholeLabelMap.entrySet().iterator();
		while (wlIt.hasNext()) {
			Entry<String, Integer> entry = wlIt.next();
			String key = entry.getKey();
			int value = entry.getValue();
			String startChar = key.split(",")[0]; // 取第一个label
			if (!mMap.containsKey(startChar)) { // 如果还没有该label起始的map，则添加一个
				HashMap<String, Integer> map = new HashMap<String, Integer>();
				map.put(key, value);
				mMap.put(startChar, map);
			} else { // 如果已经有该label起始的map，则取出、添入、添入
				HashMap<String, Integer> map = mMap.get(startChar);
				map.put(key, value);
				mMap.put(startChar, map);
			}
		}

		// 保存所有需要删除的labels
		List<String> delCandidList = new ArrayList<String>();
		int rootShouldDelNum = 0; // 纪录需要被删除的根节点数量

		Iterator<Entry<String, HashMap<String, Integer>>> mmit = mMap.entrySet().iterator();
		while (mmit.hasNext()) {
			Map.Entry<String, HashMap<String, Integer>> entry = mmit.next();
			String startChar = entry.getKey();
			HashMap<String, Integer> labelMap = entry.getValue();

			// 按照label长度保存需要删除labels（都以startChar为起始label），key是label长度，value是该长度label列表
			// 跟delCandid不同，前者是所有的待删除label列表
			Map<Integer, ArrayList<String>> len2DelCandidLabels = new HashMap<Integer, ArrayList<String>>();
			Iterator<Entry<String, Integer>> it = labelMap.entrySet().iterator();
			while (it.hasNext()) { // 将个数小于阈值的topic组合，添入delCandidMap（待删除的节点）
				Map.Entry<String, Integer> ent = it.next();
				String key = ent.getKey();
				int value = ent.getValue();
				int len = key.split(",").length;
				if (value < minNum) {
					delCandidList.add(key);
					if (!len2DelCandidLabels.containsKey(len)) {
						ArrayList<String> aList = new ArrayList<String>();
						aList.add(key);
						len2DelCandidLabels.put(len, aList);
					} else {
						ArrayList<String> aList = len2DelCandidLabels.get(len); // 取出该长度所有labels
						aList.add(key);
						len2DelCandidLabels.put(len, aList);
					}
				}
			}

			labelMap = (HashMap<String, Integer>) PreTree.shuffleLabelMap(labelMap); // 先对labelMap进行shuffle，添加必要的pre
																						// label
			PreTree pt = new PreTree(startChar, labelMap); // 建树
			// System.out.println("PreTree start with [" + startChar + "]");
			// System.out.println("--------------\noriginal tree:");
			// --PreTree.printPreTree(pt);
			resultMap.putAll(pt.deleteNodes(len2DelCandidLabels, minNum)); // 删除指定label节点，将结果添入resultMap
			if (pt.shouldDelRoot) {
				rootShouldDelNum++;
			}
			// System.out.println("--------------\npruned tree:");
			// --PreTree.printPreTree(pt);
			// System.out.println("**************************************************************");
		}

		// System.out.println("rootShouldDelNum:" + rootShouldDelNum);

		// resultMap的keySet与delCandidList的交集，才是需要修改的label
		Map<String, String> tempMap = new HashMap<String, String>();
		for (String lab : delCandidList) {
			if (resultMap.containsKey(lab)) {
				String targetLab = resultMap.get(lab);
				tempMap.put(lab, targetLab);
			}
		}
		resultMap = tempMap;

		return resultMap;
	}

	public static void testCreateTree() {
		// Map<String, HashMap<String, Integer>> mMap = new HashMap<String,
		// HashMap<String, Integer>>();
		HashMap<String, Integer> labelMap = new HashMap<String, Integer>();
		labelMap.put("1", 5);
		labelMap.put("2", 3);
		labelMap.put("1,5", 1);
		labelMap.put("1,2", 4);
		labelMap.put("1,5,7,2,2,2,2,2", 1);
		// labelMap.put("1,5,2,3,2,2,2,3", 1);
		labelMap.put("1,5,8", 6);
		labelMap.put("1,2,4", 1);

		// mMap.put("1", labelMap);

		// PreTreeNode ptn = pt.queryNode(pt.root, "1,2,4", NodeType.leaf);
		// PreTreeNode.printPreTreeNode(ptn, "");
		// PreTreeNode targetNode = pt.deleteNode("1");
		// System.out.println("target:" + targetNode.labels);

		// PreTree.printPreTree(pt);

		Map<String, String> resultMap = PreTree.processLabels(labelMap, 2);
		Iterator<Entry<String, String>> it = resultMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			String oriLab = entry.getKey();
			String newLab = entry.getValue();
			System.out.println(oriLab + " --- " + newLab);
		}

		System.out.println("Game Over!!!");
	}

	public static boolean isPreLabel(String sStr, String lStr) { // 如果sStr是lStr的前缀label，则返回true
		String[] sStrs = sStr.split(",");
		String[] lStrs = lStr.split(",");

		if (sStrs.length > lStrs.length)
			return false;

		for (int i = 0; i < sStrs.length; i++) {
			if (!sStrs[i].equals(lStrs[i])) {
				return false;
			}
		}

		return true;
	}

	public static String getPreLabel(String str, int preLen) {
		String preLabel = "";
		String[] strs = str.split(",");

		if (strs.length < preLen)
			return null;

		for (int i = 0; i < preLen; i++) {
			preLabel += strs[i] + ",";
		}

		preLabel = preLabel.substring(0, preLabel.length() - 1); // 去末尾逗号

		return preLabel;
	}
}
