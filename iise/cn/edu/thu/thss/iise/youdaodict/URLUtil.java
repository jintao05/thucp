package cn.edu.thu.thss.iise.youdaodict;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class URLUtil {
	public static String loadURl(String loadURL) throws MalformedURLException {
		StringBuffer sb = null;
		try {
			URL url = new URL(loadURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			sb = new StringBuffer();
			String line = null;
			while((line = br.readLine()) != null){
				sb.append(line);
			}
			br.close();
			conn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();

	}
}
