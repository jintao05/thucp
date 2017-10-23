package cn.edu.thu.thss.iise.youdaodict;

import java.net.MalformedURLException;
import java.net.URLEncoder;
/**
 * 
 * @author Tao Jin
 * 
 * http://fanyi.youdao.com/openapi?path=data-mode
 * 
 * http://fanyi.youdao.com/openapi.do?keyfrom=<keyfrom>&key=<key>&type=data&doctype=<doctype>&version=1.1&q=要翻译的文本
 *	版本：1.1，请求方式：get，编码方式：utf-8
 *	主要功能：中英互译，同时获得有道翻译结果和有道词典结果（可能没有）
 *	参数说明：
 *	　type - 返回结果的类型，固定为data
 *	　doctype - 返回结果的数据格式，xml或json或jsonp
 *	　version - 版本，当前最新版本为1.1
 * 	　q - 要翻译的文本，必须是UTF-8编码，字符长度不能超过200个字符，需要进行urlencode编码
 *	　only - 可选参数，dict表示只获取词典数据，translate表示只获取翻译数据，默认为都获取
 *	　注： 词典结果只支持中英互译，翻译结果支持英日韩法俄西到中文的翻译以及中文到英语的翻译
 *	errorCode：
 *	　0 - 正常
 *	　20 - 要翻译的文本过长
 *	　30 - 无法进行有效的翻译
 *	　40 - 不支持的语言类型
 *	　50 - 无效的key
 *	　60 - 无词典结果，仅在获取词典结果生效
 *
 */
public class YoudaoDict {
	private static String[] keyfrom = {"fgfdgdfg", "asd54645654", "asd54645656", "asd5464", "a5464dd", "youdaofanyi1111", "medical"};
	private static String[] key = {"1612287005", "1310810993", "1310810991", "720121073", "1755541118", "1065174416", "940776081"};
	private static String type = "data";
	private static String doctype = "json";
	private static String version = "1.1";
	// private static String only = "dict";
	
	/**
	 * 查询
	 * 
	 * @return
	 */
	private static DictBean dQuery(String qTerm, int keyIdx) {
		DictBean dict = null;
		try {
			String loadURL = "http://fanyi.youdao.com/openapi.do?" + "keyfrom="	+ keyfrom[keyIdx] + "&key=" + key[keyIdx] + "&type=" + type 
					+ "&doctype=" + doctype + "&version=" + version// + "&only=" + only
					+ "&q=" + URLEncoder.encode(qTerm, "utf-8");
			String json = URLUtil.loadURl(loadURL);
			dict = JsonUtil.Analytical(json);
		} catch (Exception e) {
			e.printStackTrace();
			//return dQuery(qTerm);
		}
		return dict;
	}
	
	private static DictBean dQuery(String qTerm) {
		int j = (int) (Math.random() * keyfrom.length);		
		return dQuery(qTerm, j);
	}
	
	public static String english2chinese(String qEnglish) {
		String result = "";
		DictBean bean = dQuery(qEnglish);
		String explains = bean.getExplains();
		if (explains != null) {
			result = explains.split("\n")[0];
			return result;
		}
		String translation = bean.getTranslation();
		if (translation != null) {
			result = translation;
		} else {
			String webInterpretation = bean.getWebInterpretation();
			if (webInterpretation != null) {
				result = webInterpretation.split("\n")[0].split(":")[1].split(",")[0];
			}
		}
		return result;
	}
	
	public static String chinese2english(String qChinese) {
		return english2chinese(qChinese);
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String qEnglish = "very good";
		System.out.println(qEnglish + " ==> " + english2chinese(qEnglish));
		
		String qChinese = "风湿三项";
		System.out.println(qChinese + " ==> " + english2chinese(qChinese));
	}
	
}
