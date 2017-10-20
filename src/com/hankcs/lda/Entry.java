package com.hankcs.lda;

public class Entry {
	String name;
	Double freq;
	public Entry(String name,Double freq){
		this.name=name;
		this.freq=freq;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getFreq() {
		return freq;
	}
	public void setFreq(Double freq) {
		this.freq = freq;
	}
}
