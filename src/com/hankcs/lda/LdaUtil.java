/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2015/1/29 19:07</create-date>
 *
 * <copyright file="LdaUtil.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package com.hankcs.lda;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author hankcs
 */
public class LdaUtil
{
    /**
     * To translate a LDA matrix to readable result
     * @param phi the LDA model
     * @param vocabulary
     * @param limit limit of max words in a topic
     * @return a map array
     */
	public static Comparator<Entry> com=new Comparator<Entry>() {
		
		public int compare(Entry arg0, Entry arg1) {
			// TODO Auto-generated method stub
			return arg1.freq.compareTo(arg0.getFreq());
		}
	};
    public static void saveTheta(double[][] theta){
    	Map<String, Double>[] result = new Map[theta.length];
    	for (int k = 0; k < theta.length; k++)
        {
    		ArrayList<Entry> list=new ArrayList<Entry>();
            for (int i = 0; i < theta[k].length; i++)
            {
            	Entry temp=new Entry(i+"",theta[k][i]);
                list.add(temp);
            }
            Collections.sort(list,com);
            result[k] = new LinkedHashMap<String, Double>();
            for(Entry it:list)
            {
                result[k].put(it.getName(), it.getFreq());
            }
        }
    	File file_out = new File("data/OutlierDetection/topic/doc2topics/docToTopic.csv");
        try {
        	BufferedWriter bw_out = new BufferedWriter(new FileWriter(file_out, false));//
        	for(Map<String, Double> map:result){
        		for (Map.Entry<String, Double> entry : map.entrySet()){
        			bw_out.write(entry.toString()+",");
        		}
        		bw_out.newLine();
        	}
			bw_out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	public static Map<String, Double>[] translate(double[][] phi, Vocabulary vocabulary, int limit)
    {
        limit = Math.min(limit, phi[0].length);
        Map<String, Double>[] result = new Map[phi.length];
        for (int k = 0; k < phi.length; k++)
        {
            ArrayList<Entry> list=new ArrayList<Entry>();
            for (int i = 0; i < phi[k].length; i++)
            {
            	Entry temp=new Entry(vocabulary.getWord(i),phi[k][i]);
                list.add(temp);
            }
            Collections.sort(list,com);
            result[k] = new LinkedHashMap<String, Double>();
            for (Entry it:list)
            {
                result[k].put(it.getName(),it.getFreq());
            }
        }
        return result;
    }

    public static Map<String, Double> translate(double[] tp, double[][] phi, Vocabulary vocabulary, int limit)
    {
        Map<String, Double>[] topicMapArray = translate(phi, vocabulary, limit);
        double p = -1.0;
        int t = -1;
        for (int k = 0; k < tp.length; k++)
        {
            if (tp[k] > p)
            {
                p = tp[k];
                t = k;
            }
        }
        return topicMapArray[t];
    }

    /**
     * To print the result in a well formatted form
     * @param result
     */
    public static void explain(Map<String, Double>[] result)
    {
        int i = 0;
        for (Map<String, Double> topicMap : result)
        {
//            System.out.printf("topic %d :\n", i++);
//            explain(topicMap);
//            System.out.println();
            
            File file_out = new File("data/OutlierDetection/topic/topic2items/"+i+".txt");
//            System.out.println(i+":");
            i++;
            try {
            	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_out), Charset.forName("gb2312")));
				int j=0;
            	for (Map.Entry<String, Double> entry : topicMap.entrySet()){
					bw.write(entry.toString());
//					if(j<15)
//						System.out.println(entry.toString());
					j++;
					bw.newLine();
				}
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    public static void explain(Map<String, Double> topicMap)
    {
        for (Map.Entry<String, Double> entry : topicMap.entrySet())
        {
            System.out.println(entry);
        }
    }
}
