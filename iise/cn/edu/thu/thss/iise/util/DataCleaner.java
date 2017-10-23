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

/**
 * @author Tao Jin
 *
 */
public class DataCleaner {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("C:/test/ncx-cleaned.csv"), "UTF-8"));		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:/test/ncx-cleaned1.csv"), "UTF-8"));
		String line = br.readLine();
		while (line != null) {
			if (line.contains("床位费") || line.contains("采暖费") || line.contains("自费") || line.contains("其它费")) {
				System.out.println(line);
			} else {
				bw.write(line + "\n");
			}
			line = br.readLine();
		}		
		bw.close();
		br.close();

	}

}
