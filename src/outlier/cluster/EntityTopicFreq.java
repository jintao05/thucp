package outlier.cluster;

public class EntityTopicFreq {
	public String id;
	public Double freq;
	public EntityTopicFreq(String id,Double freq){
		this.id=id;
		this.freq=freq;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Double getFreq() {
		return freq;
	}
	public void setFreq(Double freq) {
		this.freq = freq;
	}
}
