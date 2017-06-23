package model.TCPM;

import java.util.List;

public class PreTreeNode {
	enum NodeType {
		pre, leaf
	};

	NodeType nodeType;
	String labels;
	int count;
	PreTreeNode parent;
	List<PreTreeNode> children;

	PreTreeNode(String lab) {
		this.labels = lab;
	}

	PreTreeNode(NodeType nt, String lab, int cnt, PreTreeNode par,
			List<PreTreeNode> chi) {
		this.nodeType = nt;
		this.labels = lab;
		this.count = cnt;
		this.parent = par;
		this.children = chi;
	}

	public static void printPreTreeNode(PreTreeNode ptn, String sss) {
		if(ptn == null)
			return;
		System.out.print(sss);
		String str = "{" + ptn.nodeType.toString() + ": [" + ptn.labels + "] "
				+ ptn.count + "}";
		System.out.println(str);
		if (ptn.children != null) {
			for (PreTreeNode child : ptn.children) {
				printPreTreeNode(child, sss+"\t");
			}
		}
	}
}
