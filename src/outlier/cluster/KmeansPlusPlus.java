package outlier.cluster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Random;
import java.util.Set;

public class KmeansPlusPlus {

	private List<Point> dataset = null;
	public double evaluate = 0;

	public KmeansPlusPlus(String docToTopicFilePath,String idFromCorpusForMapping) throws IOException {
		initDataSet(docToTopicFilePath,idFromCorpusForMapping);
	}

	/**
	 * 初始化数据集
	 * 
	 * @throws IOException
	 */
	private void initDataSet(String path,String idFromCorpusForMapping) throws IOException {
		dataset = new ArrayList<Point>();

		ArrayList<Point> points = new ArrayList<Point>();
		ArrayList<String> id = new ArrayList<String>();
		Map<String, ArrayList<Double>> map = new HashMap<String, ArrayList<Double>>();
		try {
			File folder = new File(idFromCorpusForMapping);
			for (File file : folder.listFiles()) {
				id.add(file.getName());
			}
			BufferedReader reader;
			reader = new BufferedReader(new FileReader(path));
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
				map.put(id.get(j), arrayTemp);
				j++;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Integer i = 0; i < id.size(); i++) {
			Point temp = new Point(id.get(i), map.get(id.get(i)));
			points.add(temp);
		}
		dataset = points;
	}

	/**
	 * @param k
	 *            聚类的数目
	 */
	public Map<Integer, List<Point>> kcluster(int k) {
		// 随机从样本集合中选取k个样本点作为聚簇中心
		// 每个聚簇中心有哪些点
		Set<Integer> set = new HashSet<Integer>();
		Map<Integer, List<Point>> nowClusterCenterMap = new HashMap<Integer, List<Point>>();
		List<Point> nowCenter = new ArrayList<Point>();
		List<Point> lastCenter = null;
		Random random = new Random();
		int num =random.nextInt(dataset.size());
		set.add(num);
		nowCenter.add(dataset.get(num));
		ArrayList<Integer> candinate=new ArrayList<Integer>();
		ArrayList<Double> dis=new ArrayList<Double>();
		Double sum=0.0;
		for (int i = 0; i < k - 1; i++) {
			candinate.clear();
			dis.clear();
			sum=0.0;
			for (int j = 0; j < dataset.size(); j++) {
				if (set.contains(j))
					continue;
				double min=Double.MAX_VALUE;
				for (Integer it : set) {
					Point cu = dataset.get(it);
					double disTemp = distance(dataset.get(j), cu);
					if(disTemp<min){
						min=disTemp;
					}
				}
				candinate.add(j);
				dis.add(min);
				sum+=min;
			}
			int bound=sum.intValue();
			int rand=random.nextInt(bound+1);
			Double it=rand*1.0;
			int h=candinate.size()-1;
			while(it.compareTo(0.0)>=0){
				it-=dis.get(h);
				h--;
			}
			set.add(candinate.get(h+1));
			nowCenter.add(dataset.get(candinate.get(h+1)));
		}

		// 上一次的聚簇中心
		Map<Integer, List<Point>> lastClusterCenterMap = null;

		// 找到离中心最近的点,然后加入以该中心为map键的list中
		while (true) {
			for (Point point : dataset) {
				double shortest = Double.MAX_VALUE;
				int key = -1;
				for (int i = 0; i < nowCenter.size(); i++) {
					double distance = distance(point, nowCenter.get(i));
					if (distance < shortest) {
						shortest = distance;
						key = i;
					}
				}
				if (nowClusterCenterMap.containsKey(key)) {
					nowClusterCenterMap.get(key).add(point);
				} else {
					ArrayList<Point> temp = new ArrayList<Point>();
					temp.add(point);
					nowClusterCenterMap.put(key, temp);
				}
			}
			// 如果结果与上一次相同，则整个过程结束
			if (isEqualCenter(nowCenter, lastCenter)) {
				break;
			}
			// System.out.println("ok---------------------"+nowClusterCenterMap.size());
			// for(Map.Entry<Integer, List<Point>>
			// e:nowClusterCenterMap.entrySet()){
			// System.out.println(e.getValue().size()+"**********");
			// }
			// System.out.println(lastCenter);
			// System.out.println(nowCenter);
			lastClusterCenterMap = nowClusterCenterMap;
			lastCenter = nowCenter;
			nowClusterCenterMap = new HashMap<Integer, List<Point>>();
			nowCenter = new ArrayList<Point>();
			// 把中心点移到其所有成员的平均位置处,并构建新的聚簇中心
			for (int i = 0; i < lastCenter.size(); i++) {
				List<Point> temp = lastClusterCenterMap.get(i);
				if (temp == null || temp.size() == 0) {
					nowCenter.add(lastCenter.get(i));
				} else {
					nowCenter.add(getNewCenterPoint(temp));
				}
			}
		}
		double temp = evaluate(nowClusterCenterMap, nowCenter);
		// System.out.println(k + "," + temp);
		evaluate = temp;
		return nowClusterCenterMap;
	}

	/**
	 * 判断前后两次是否是相同的聚簇中心，若是则程序结束，否则继续,知道相同
	 * 
	 * @param lastClusterCenterMap
	 * @param nowClusterCenterMap
	 * @return bool
	 */
	private boolean isEqualCenter(List<Point> nowCenter, List<Point> lastCenter) {
		if (lastCenter == null) {
			return false;
		} else {
			for (int i = 0; i < nowCenter.size(); i++) {
				if (!nowCenter.get(i).equals(lastCenter.get(i)))
					return false;
			}
		}
		return true;
	}

	/**
	 * 计算新的中心
	 * 
	 * @param value
	 * @return Point
	 */
	private Point getNewCenterPoint(List<Point> value) {
		double[] sum = new double[value.get(0).value.size()];
		for (Point point : value) {
			for (int i = 0; i < sum.length; i++) {
				sum[i] += point.value.get(i);
			}
		}
		ArrayList<Double> valueTemp = new ArrayList<Double>();
		for (int i = 0; i < sum.length; i++) {
			valueTemp.add(sum[i] / value.size());
		}
		Point point = new Point("center", valueTemp);
		return point;
	}

	/**
	 * 使用欧几里得算法计算两点之间距离
	 * 
	 * @param point1
	 * @param point2
	 * @return 两点之间距离
	 */
	private double distance(Point point1, Point point2) {
		ArrayList<Double> x1 = point1.value;
		ArrayList<Double> x2 = point2.value;
		double distance = 0;
		for (int i = 0; i < x1.size(); i++) {
			distance += Math.pow(x1.get(i) - x2.get(i), 2);
		}
		distance = Math.sqrt(distance);
		return distance;
	}

	public double evaluate(Map<Integer, List<Point>> cluster, List<Point> center) {
		double sum = 0;
		for (int i = 0; i < center.size(); i++) {
			List<Point> temp = cluster.get(i);
			if (temp == null)
				continue;
			for (Point p : temp) {
				sum += distance(p, center.get(i));
			}
		}
		return sum;
	}

//	public static void main(String[] args) throws IOException {
//		Set<String> id = new HashSet<String>();
//		double min = Double.MAX_VALUE;
//		Map<Integer, List<Point>> result=null;
//		int minIndex=-1;
//		for (int j = 0; j < 1000; j++) {
//			int i = 14;
//			KmeansPlusPlus kmeansClustering = new KmeansPlusPlus();
//			Map<Integer, List<Point>> tempResult = kmeansClustering.kcluster(i);
//			System.out.println(kmeansClustering.evaluate);
//			if (kmeansClustering.evaluate < min) {
//				min = kmeansClustering.evaluate;
//				result=tempResult;
//				minIndex=i;
//			} else
//				continue;
//		}
//		File file_out = new File("D:/data4code/cluster/LogBasedOnKmeansPlusPlus-"+minIndex+".csv");
//		BufferedWriter bw_out = new BufferedWriter(new FileWriter(file_out, false));//
//		bw_out.write("\"BRBM\",\"XM\",\"RQ\"");
//		bw_out.newLine();
//		for (Entry<Integer, List<Point>> entry : result.entrySet()) {
//			// System.out.println("===============聚簇中心为：" + entry.getKey() +
//			// "================");
//			// System.out.println(entry.getValue().size());
//			for (Point point : entry.getValue()) {
//				String[] strs = point.id.split("#");
//				String caseId = strs[0];
//				id.add(caseId);
//				String riqi = strs[1].split("\\.")[0];
//				bw_out.write(caseId + "," + entry.getKey() + "," + riqi);
//				bw_out.newLine();
//			}
//		}
//		for (String it : id) {
//			bw_out.write(it + "," + "s" + "," + "1900-1-1");
//			bw_out.newLine();
//			bw_out.write(it + "," + "e" + "," + "2018-1-1");
//			bw_out.newLine();
//		}
//		bw_out.flush();
//		bw_out.close();
//	}

	public int comPareDate(String x1, String x2) throws ParseException {
		SimpleDateFormat sf = new SimpleDateFormat("YYYY-MM-DD");
		Date d1 = sf.parse(x1);
		Date d2 = sf.parse(x2);
		return d1.compareTo(d2);

	}

}
