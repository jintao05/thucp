package outlier.cluster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetLabelForCluster {//每个cluster的label仅供参考，目的是了解该cluster大致是做什么诊疗
	public Comparator<EntityTopicFreq> com=new Comparator<EntityTopicFreq>() {
		
		@Override
		public int compare(EntityTopicFreq arg0, EntityTopicFreq arg1) {
			// TODO Auto-generated method stub
			return arg1.freq.compareTo(arg0.freq);
		}
	};
//	public static void main(String[] args) throws IOException{
//		GetLabelForCluster ins=new GetLabelForCluster();
//		ins.getClusterToItems("D:/data4code/dataclean/topic","D:/data4code/cluster/clusterToItems.csv", 10);//每个topic，最多选10个代表词
//	}
	public Map<String,ArrayList<String>> getClusterToDays(String filename) throws IOException{
		Map<String,ArrayList<String>> re=new HashMap<String,ArrayList<String>>();
		File folder = new File(filename);
		BufferedReader reader = new BufferedReader(new FileReader(folder));
		String line=reader.readLine();
		while((line=reader.readLine())!=null){
			String[] strs=line.split(",");
			String key=strs[1];
			String value=strs[0]+"#"+strs[2];
			if(re.containsKey(key))
				re.get(key).add(value);
			else{
				ArrayList<String> arrayValue=new ArrayList<String>();
				arrayValue.add(value);
				re.put(key, arrayValue);
			}
		}
//		for(Map.Entry<String, ArrayList<String>> entry:re.entrySet()){
//			System.out.println(entry.getValue().size());
//		}
		return re;
	}
	public Map<String, ArrayList<Double>> getDayToTopic(String docToTopicPath,String corpusFilePath){
		Map<String, ArrayList<Double>> re=new HashMap<String, ArrayList<Double>>();
		try {
			ArrayList<String> id = new ArrayList<String>();
			File folder = new File(corpusFilePath);//("D:/data4code/dataclean/corpus");
			for (File file : folder.listFiles()) {
				id.add(file.getName());
			}
			BufferedReader reader;
			reader = new BufferedReader(new FileReader(docToTopicPath));
			String line = null;
			int j = 0;
			while ((line = reader.readLine()) != null) {
				Map<String, Double> temp = new HashMap<String, Double>();
				String[] strs = line.split(",");
				for (int i = 0; i < strs.length; i++) {
					temp.put(strs[i].split("=")[0], Double.valueOf(strs[i].split("=")[1]));
				}
				ArrayList<Double> arrayTemp = new ArrayList<Double>();
				for (Integer k = 0; k < temp.size(); k++)
					arrayTemp.add(temp.get(k.toString()));
				// System.out.println(arrayTemp.toString());
				re.put(id.get(j).substring(0, id.get(j).length()-4), arrayTemp);
				j++;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println(re.size());
		return re;
	}
	public Map<String,ArrayList<String>> getTopicToItems(String filename,Integer K,double theta) throws IOException{
		Map<String,ArrayList<String>> re = new HashMap<String,ArrayList<String>>();
		for(Integer i=0;i<K;i++){
			File file=new File(filename+"/"+i.toString()+".txt");
			BufferedReader reader;
			reader = new BufferedReader(new FileReader(file));
			String line=null;
			double sum=0;
			ArrayList<String> temp=new ArrayList<String>();
			while ((line = reader.readLine()) != null) {
				String[] strs=line.split("=");
				temp.add(strs[0]);
				sum+=Double.parseDouble(strs[1]);
				if(sum>theta) break;
			}
			re.put(i.toString(), temp);
		}
		return re;
	}
	public Map<String,ArrayList<String>> getClusterToTopics(Integer clusterSize,Integer TopicSize,double theta, double ratio,String logPath ,String docToTopicPath,String corpusFilePath) throws IOException{//ratio含义见函数中注释//K为kmeans簇的个数
		Map<String,ArrayList<String>> re=new HashMap<String,ArrayList<String>>();
		Map<String,ArrayList<String>> clusterToDays=getClusterToDays(logPath);//("D:/data4code/cluster/LogBasedOnKmeansPlusPlus-14.csv");
		Map<String, ArrayList<Double>> dayToTopic=getDayToTopic(docToTopicPath,corpusFilePath);//("D:/data4code/dataclean/topic/docToTopic.csv","D:/data4code/dataclean/corpus");
		for(Integer i=0;i<clusterSize;i++){
			ArrayList<String> days=clusterToDays.get(i.toString());
			double[] temp=new double[TopicSize];
			for(int j=0;j<TopicSize;j++){
				temp[j]=0.0;
			}
			for(String day:days){
				ArrayList<Double> eachDayTOTopic=dayToTopic.get(day);
				for(int h=0;h<TopicSize;h++){
					temp[h]+=eachDayTOTopic.get(h);
				}
			}
			for(int h=0;h<TopicSize;h++){
				temp[h]/=days.size();
			}
			ArrayList<EntityTopicFreq> forSort=new ArrayList<EntityTopicFreq>();
			for(Integer h=0;h<TopicSize;h++){
				EntityTopicFreq entityTemp=new EntityTopicFreq(h.toString()	, temp[h]);
				forSort.add(entityTemp);
			}
			Collections.sort(forSort,com);
			double sum=0;
			double mean=1.0/TopicSize*ratio;//当概率小于平均值的ratio倍时则认为该主题与当天诊疗活动无关
			ArrayList<String> eachClusterToTopic=new ArrayList<String>();
			for(EntityTopicFreq e:forSort){
				sum+=e.freq;
				eachClusterToTopic.add(e.id);
				if(sum>theta) break;
				if(e.freq<mean) break;
			}
			re.put(i.toString(), eachClusterToTopic);
		}
		return re;
	}
	public void getClusterToItems(Map<Integer, List<Point>> result,String filenameTopic,String filenameSaveTo,int theta,String logPath,String docToTopicPath,String corpusFilePath,int topicK,int clusterK) throws IOException{
		Map<String,ArrayList<String>> clusterToTopics=getClusterToTopics(clusterK, topicK,0.8, 1.5,logPath,docToTopicPath,corpusFilePath);
		Map<String,ArrayList<String>> topicToItems=getTopicToItems(filenameTopic, topicK, 0.8);
		BufferedWriter bw_out = new BufferedWriter(new FileWriter(new File(filenameSaveTo), false));//
		for(Integer i=0;i<clusterToTopics.size();i++){
			String temp="";
			String key=i.toString();
			temp+="cluster-"+key+" ( 类簇大小:"+result.get(i).size()+")"+"\n";
			ArrayList<String> value=clusterToTopics.get(key);
			for(String eachTopic:value){
				temp+="topic-"+eachTopic+": ";
				ArrayList<String> items=topicToItems.get(eachTopic);
				int count=0;
				for(String eachItem:items){
					temp+=eachItem+",";
					count++;
					if(count>theta) break;
				}
				temp+="\n";
			}
			bw_out.write(temp);
			bw_out.newLine();
			bw_out.newLine();
		}
		bw_out.flush();
		bw_out.close();
	}
}

