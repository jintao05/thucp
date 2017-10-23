/**
 * 
 */
package cn.edu.thu.thss.iise.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import cn.edu.thu.thss.iise.ssm.SnomedCTSSM;
import cn.edu.thu.thss.iise.youdaodict.YoudaoDict;

/**
 * @author Tao Jin
 *
 */
public class Translator {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("C:/test/ncx-national-items.txt"), "UTF-8"));	
		HashSet<String> items = new HashSet<String>();
		String line = br.readLine();
		while (line != null) {
			//String[] data = line.split(",");
			//items.add(data[1].substring(1, data[1].length()-1));
			items.add(line);
			//System.out.println(data[1].substring(1, data[1].length()-1));		
			line = br.readLine();
		}
		ArrayList<String> itemArray = new ArrayList<String>();
		itemArray.addAll(items);
		Collections.sort(itemArray);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:/test/ncx-national-items-c2e2concept.txt"), "UTF-8"));
		SnomedCTSSM snomedCTSSM = SnomedCTSSM.getSnomedCTSSM();
		for (String str : itemArray) {
			String itemE = YoudaoDict.chinese2english(str);
			String concept = snomedCTSSM.getMostSimilarConcept(itemE, 0.5f);
			if (itemE == null || itemE == "" || concept == null || concept == "") {
				System.out.println(str + " ==> " + itemE + " ==> " + concept);
			}
			bw.write(str + " ==> " + itemE + " ==> " + concept + "\n");
		}
		bw.close();
		br.close();

	}

}
