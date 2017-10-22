package cn.edu.thu.thss.iise.youdaodict;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Tao Jin
 *
 * json数据格式举例
 *	http://fanyi.youdao.com/openapi.do?keyfrom=<keyfrom>&key=<key>&type=data&doctype=json&version=1.1&q=good
 *	{
 *	    "errorCode":0
 *	    "query":"good",
 *	    "translation":["好"], // 有道翻译
 *	    "basic":{ // 有道词典-基本词典
 *	        "phonetic":"gʊd"
 *	        "uk-phonetic":"gʊd" //英式发音
 *	        "us-phonetic":"ɡʊd" //美式发音
 *	        "explains":[
 *	            "好处",
 *	            "好的"
 *	            "好"
 *	        ]
 *	    },
 *	    "web":[ // 有道词典-网络释义
 *	        {
 *	            "key":"good",
 *	            "value":["良好","善","美好"]
 *	        },
 *	        {...}
 *	    ]
 *	}
 */
public class JsonUtil {
	/**
	 * 解析Json
	 */
	public static DictBean Analytical(String strJson) {
		DictBean dict = new DictBean();
		JSONObject basic = null;
		JSONObject jso = null;
		JSONArray web = null;
		try {
			// json对象
			jso = new JSONObject(strJson);
			String query = "";
			try {
				// 查询的词
				query = jso.get("query").toString();
				dict.setQuery(query);
			} catch (Exception e) {
				//System.err.println("查询的词为空！");
			}
			// 有道词典-基本词典
			try {
				basic = jso.getJSONObject("basic");
			} catch (Exception e) {
				//System.err.println(query + ":没有有道词典-基本词典！");
			}

			try {
				// 有道词典-网络释义数据
				web = jso.getJSONArray("web");
			} catch (Exception e) {
				//System.err.println(query + ":没有有道词典-网络释义数据！");
			}

			/************* 具体数据 ********************/
			try {
				// 错误代码
				int errorCode = jso.getInt("errorCode");
				dict.setErrorCode(errorCode);
			} catch (Exception e) {
				//System.err.println(query + ":没有错误代码！");
			}
			try {
				// 有道翻译
				String translation = jso.get("translation").toString();
				translation = translation.substring(2, translation.length()-2);
				dict.setTranslation(translation);
			} catch (Exception e) {
				//System.err.println(query + ":没有翻译！");
			}
			try {
				// 释义
				String explains = basic.get("explains").toString();
				explains = explains.substring(2, explains.length()-2).replaceAll("\",\"", "\n");						
				dict.setExplains(explains);
			} catch (Exception e) {
				//System.err.println(query + ":没有释义！");
			}

			try {
				// 音标\拼音
				String phonetic = basic.get("phonetic").toString();
				dict.setPhonetic(phonetic);
			} catch (Exception e) {
				//System.err.println(query + ":没有音标\\拼音！");
			}

			try {
				// 英式发音
				String uk_phonetic = basic.get("uk-phonetic").toString();
				dict.setUk_phonetic(uk_phonetic);
			} catch (Exception e) {
				//System.err.println(query + ":没有英式发音！");
			}

			try {
				// 美式发音
				String us_phonetic = basic.get("us-phonetic").toString();
				dict.setUs_phonetic(us_phonetic);
			} catch (Exception e) {
				//System.err.println(query + ":没有美式发音！");
			}

			try {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < web.length(); i++) {
					JSONObject webv = (JSONObject) web.get(i);
					String webkey = webv.get("key").toString();
					String webvalue = webv.get("value").toString();
					webvalue = webvalue.substring(2, webvalue.length() - 2).replaceAll("\",\"", ",");
					String webInterpretation = webkey + ":" + webvalue + "\n";
					sb.append(webInterpretation);
				}
				dict.setWebInterpretation(sb.toString());
			} catch (Exception e) {
				//System.err.println(query + ":没有网络释义！");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return dict;
	}
}
