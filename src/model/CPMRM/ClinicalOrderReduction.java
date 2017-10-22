package model.CPMRM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cn.edu.thu.thss.iise.ssm.SnomedCTSSM;
import cn.edu.thu.thss.iise.youdaodict.*;
import com.huaban.analysis.jieba.JiebaSegmenter;

public class ClinicalOrderReduction {
	String clinicalOrder[][];
	final double SIMTHRESHOLD = 0.80;

	StringBuffer result= new StringBuffer();

	//基于语义的同义医嘱消解
	public String[][] semanticReduction(String Orders[][]) {
		this.clinicalOrder = Orders;
		YoudaoDict translator = new YoudaoDict();

		HashMap<String, String> c2eHashMap = new HashMap<String,String>();//医嘱汉英对照集
		HashMap<String, String> c2cidHashMap = new HashMap<String,String>();//医嘱英文-cid对照集
		HashMap<String, String> c2cHashMap = new HashMap<String,String>();//相似医嘱集
		HashMap<String, Double> simHashMap = new HashMap<String,Double>();//相似度集
		//遍历医嘱集
		String chiName = "", engName = "", simMostEngName = "";
		double sim = 0.0, maxSim = 0.0;
		SnomedCTSSM ssm = SnomedCTSSM.getSnomedCTSSM();
		result.append("\n 基于语义的同义医嘱消解 \n");
		for(int i = 0; i < Orders.length; i++) {
			chiName = Orders[i][1];
			String simEngName = "", simChiNameString = "", disChiName = "";
			//将新医嘱加入医嘱列表 并求其相似度最高且大于0.80的医嘱, 更新医嘱映射表c2cHashMap、医嘱相似度表simHashMap
			if(!c2eHashMap.containsKey(chiName)) {

				engName = translator.english2chinese(chiName);
				engName = seqClean(engName);
//				simMostEngName = ssm.getMostSimilarConcept(engName, 0.5f);
//				if(!(simMostEngName == null || simMostEngName == "")) engName = simMostEngName;
				c2eHashMap.put(chiName, engName);
//				System.out.println(chiName+"|---|"+engName);
				String res;
				res = ssm.getConceptId(engName);
				if(!(res == null || res.equals(""))) {
//					System.out.println(ssm.getConceptId(engName));
					c2cidHashMap.put(chiName, res);
					maxSim = 0.0;
					for(Map.Entry<String, String> entry:c2cidHashMap.entrySet()) {
						if(entry.getKey() == chiName) continue;
						sim = ssm.getSimilarity(engName, c2eHashMap.get(entry.getKey()));
						if (sim > SIMTHRESHOLD) {
							//System.out.println(chiName+"<-->"+entry.getKey()+"，相似度:"+sim+"\n");
							result.append(" "+chiName+"<-->"+entry.getKey()+"，相似度:"+sim+"\n");
						}
						if (sim > SIMTHRESHOLD && sim > maxSim) {
							maxSim = sim;
							simEngName = entry.getValue();
							simChiNameString = entry.getKey();
						}
					}
					if(maxSim != 0.0) {
						String MICAterm;
						MICAterm = ssm.getTermOfMICA(engName, simEngName);
						if(ssm.getSimilarity(MICAterm, engName) > ssm.getSimilarity(MICAterm, simEngName)) {
							disChiName = chiName;
						}
						else disChiName = simChiNameString;
						c2cHashMap.put(chiName, disChiName);
						simHashMap.put(chiName, maxSim);
						if(simHashMap.containsKey(simChiNameString) && simHashMap.get(simChiNameString) < maxSim) {
							c2cHashMap.put(simChiNameString, disChiName);
							simHashMap.put(simChiNameString, maxSim);
						}
					}
					else {
						c2cHashMap.put(chiName, chiName);
						simHashMap.put(chiName, maxSim);
					}
				}
			}
		}
		for(int i = 0; i < Orders.length; i++) {
			chiName = Orders[i][1];
			if(c2cHashMap.containsKey(chiName))
				this.clinicalOrder[i][1] = c2cHashMap.get(chiName);
		}

		//System.out.println(c2cHashMap.size());

		return clinicalOrder;
	}

	//基于文本的同义医嘱消解
	public String[][] textReduction(String Orders[][], double simInput){
		this.clinicalOrder = Orders;
		int signArray[] = new int[Orders.length];
		ArrayList<Integer> similarOrderIndex = new ArrayList<Integer>();
		HashSet<String> similarOrderList = new HashSet<String>();
		int  count = 0;
		result.append(" 基于文本的同义语义消解结果 \n");
		for(int i = 0; i < Orders.length; i++){
			//求相似医嘱集
			if(signArray[i] == 1) continue;
			signArray[i] = 1;
			similarOrderIndex.add(i);
			similarOrderList.add(Orders[i][1]);
			for(int j = i+1; j < Orders.length; j++){
				if(signArray[j] == 1) continue;
				int ed = getOrderSimilarity(Orders[i][1], Orders[j][1]);
				if(ed < 0) System.out.println(ed);
				if(ed > 0){
					double sim = 1 - (double)ed/Math.max(Orders[i][1].length(),Orders[j][1].length());
					if (sim > simInput) {
						signArray[j] = 1;
						similarOrderIndex.add(j);
						if(!similarOrderList.contains(Orders[j][1]))
							similarOrderList.add(Orders[j][1]);
					}
				}
				else if(ed == 0){
					signArray[j] = 1;
					similarOrderIndex.add(j);
				}
			}
			//求相似医嘱集内的主体短语
			if(similarOrderList.size() == 1){
				int index;
				for(int k = 0; k < similarOrderIndex.size(); k++) {
					index = similarOrderIndex.get(k);
					clinicalOrder[index] = Orders[index];
				}
			}
			else {
				String corewords = "";

				corewords = getCoreWords(similarOrderList);
				int index;
				for(int k = 0; k < similarOrderIndex.size(); k++) {
					index = similarOrderIndex.get(k);
					clinicalOrder[index] = Orders[index];
					clinicalOrder[index][1] = corewords;
				}
				count += similarOrderIndex.size();
			}
			similarOrderIndex.clear();
			similarOrderList.clear();
		}
		if (count == 0) {
			//System.out.println("没有找到文本相似的同义医嘱。");
			result.append(" 没有找到文本相似的同义医嘱。"+"\n");
		}
		return clinicalOrder;
	}

	public String seqClean(String str){
		return upperCase(delParentheses(delbracket(str))).replaceAll("[^a-z^A-Z^0-9^\\s]", "").trim();
	}

	public String delbracket(String str) {
		int index1,index2;
		index1 = str.indexOf('[');
		index2 = str.indexOf(']');
		if (index1 == -1 || index2 == -1) {
			return str;
		}
		else return str.substring(0,index1)+str.substring(index2+2);

	}
	public String delParentheses(String str) {
		int index1,index2;
		index1 = str.indexOf('(');
		index2 = str.indexOf(')');
		if (index1 == -1 || index2 == -1) {
			return str;
		}
		else return str.substring(0,index1)+str.substring(index2+1);

	}

	public String upperCase(String str) {
		if (str.length()>1) {
			return str.substring(0, 1).toUpperCase() + str.substring(1);
		}
		return str.toUpperCase();
	}

	//识别主体短语
	private String getCoreWords(HashSet<String> similarOrderList) {
		// TODO Auto-generated method stub
		//对每个医嘱求分词集，并加入词典；求词典中在每个医嘱中都出现的词
		HashSet<String> dict = new HashSet<String>();
		HashMap<String, ArrayList<String>> orderDictMap = new HashMap<String, ArrayList<String>>();

		String[] sp = null;
		String str;
		ArrayList<String> coreOrder = new ArrayList<String>();
		JiebaSegmenter segmenter = new JiebaSegmenter();
		for (String s : similarOrderList) {
			System.out.println(s);
			result.append(" "+s);
			result.append(" ");
			str = segmenter.sentenceProcess(s).toString();
			str = str.replace(" ", "");
			str = str.substring(1,str.length()-1);
			sp = str.split(",");
			if(!orderDictMap.containsKey(s)) {
				ArrayList<String> orderDictList = new ArrayList<String>();
				for(String word : sp) {
					orderDictList.add(word);
					if(!dict.contains(word))
						dict.add(word);
				}
				orderDictMap.put(s, orderDictList);
			}
		}
		ArrayList<String> mapValuesList = new ArrayList<String>();
		for (String word : dict) {
			boolean sign = true;
			for(String s : similarOrderList) {
				if(!orderDictMap.isEmpty() && orderDictMap.containsKey(s)) {
					mapValuesList = orderDictMap.get(s);
					if(!mapValuesList.contains(word))
						{
							sign = false;
							break;
						}
				}
			}
			if(sign) coreOrder.add(word);

		}
//		System.out.println(coreOrder.toString());
		String res1 = "";
		for(int i = 0; i < sp.length; i++) {
			if(coreOrder.contains(sp[i])) {
				res1 += sp[i];
			}
		}
		res1 = res1.replace("()", "");
	    result.append("==>"+res1+"\n") ;

		System.out.println("==>"+res1);
		System.out.println("------");
		return res1;
	}

	public int getOrderSimilarity(String str1, String str2){
		int ed = editDistance(str1,str2);
//		return 1 - (double)ed/Math.max(str1.length(),str2.length());
		return ed;
	}

    public int min(int one, int two, int three) {
        int min = one;
        if (two < min) {
            min = two;
        }
        if (three < min) {
            min = three;
        }
        return min;
    }


    public int editDistance(String str1, String str2) {
        int d[][];
        int y = str1.length();
        int x = str2.length();
        char ch1;
        char ch2;
        int temp;
        if (y == 0) {
            return x;
        }
        if (x == 0) {
            return y;
        }
        d = new int[y + 1][x + 1];
        for (int j = 0; j <= x; j++) {
            d[0][j] = j;
        }
        for (int i = 0; i <= y; i++) {
            d[i][0] = i;
        }
        for (int i = 1; i <= y; i++) {
            ch1 = str1.charAt(i - 1);
            for (int j = 1; j <= x; j++) {
                ch2 = str2.charAt(j - 1);
                if (ch1 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
            }
        }
        return d[y][x];
    }

  public StringBuffer getResultBuffer()
  {
	   return result;
  }

}
