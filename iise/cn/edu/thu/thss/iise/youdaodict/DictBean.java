package cn.edu.thu.thss.iise.youdaodict;


public class DictBean {
	// 错误代码
	private int errorCode;
	// 有道翻译
	private String translation;
	// 查询的词
	private String query;
	// 释义
	private String explains;
	// 音标\拼音
	private String phonetic;
	// 英式发音
	private String uk_phonetic;
	// 美式发音
	private String us_phonetic;
	// 网络释义
	private String webInterpretation;
	
	public DictBean() {
		super();
	}

	public DictBean(int errorCode, String translation, String query,
			String explains, String phonetic, String uk_phonetic,
			String us_phonetic, String webInterpretation) {
		super();
		this.errorCode = errorCode;
		this.translation = translation;
		this.query = query;
		this.explains = explains;
		this.phonetic = phonetic;
		this.uk_phonetic = uk_phonetic;
		this.us_phonetic = us_phonetic;
		this.webInterpretation = webInterpretation;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getTranslation() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getExplains() {
		return explains;
	}

	public void setExplains(String explains) {
		this.explains = explains;
	}

	public String getPhonetic() {
		return phonetic;
	}

	public void setPhonetic(String phonetic) {
		this.phonetic = phonetic;
	}

	public String getUk_phonetic() {
		return uk_phonetic;
	}

	public void setUk_phonetic(String uk_phonetic) {
		this.uk_phonetic = uk_phonetic;
	}

	public String getUs_phonetic() {
		return us_phonetic;
	}

	public void setUs_phonetic(String us_phonetic) {
		this.us_phonetic = us_phonetic;
	}

	public String getWebInterpretation() {
		return webInterpretation;
	}

	public void setWebInterpretation(String webInterpretation) {
		this.webInterpretation = webInterpretation;
	}

	@Override
	public String toString() {
		return "DictBean [\n错误代码=" + errorCode + ",\n有道翻译="
				+ translation + ",\n查询的词=" + query + ",\n释义=" + explains
				+ ",\n音标\\拼音=" + phonetic + ",\n英式发音=" + uk_phonetic
				+ ",\n美式发音=" + us_phonetic + ",\n网络释义="
				+ webInterpretation + "]";
	}
	

	


}