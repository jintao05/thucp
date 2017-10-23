package cn.edu.thu.thss.iise.pojo;
import java.util.Date;

/**
 *
 */

/**
 * @author Tao Jin
 *
 */
public class Sfmx {
	private String grbm;
	private String mc;
	private String dl;
	private float dj;
	private float zl;
	private float zje;
	private Date rq;

	public Sfmx() {

	}

	/**
	 * @return the grbm
	 */
	public String getGrbm() {
		return grbm;
	}
	/**
	 * @param grbm the grbm to set
	 */
	public void setGrbm(String grbm) {
		this.grbm = grbm;
	}
	/**
	 * @return the mc
	 */
	public String getMc() {
		return mc;
	}
	/**
	 * @param mc the mc to set
	 */
	public void setMc(String mc) {
		this.mc = mc;
	}
	/**
	 * @return the dl
	 */
	public String getDl() {
		return dl;
	}

	/**
	 * @param dl the dl to set
	 */
	public void setDl(String dl) {
		this.dl = dl;
	}

	/**
	 * @return the dj
	 */
	public float getDj() {
		return dj;
	}
	/**
	 * @param dj the dj to set
	 */
	public void setDj(float dj) {
		this.dj = dj;
	}
	/**
	 * @return the zl
	 */
	public float getZl() {
		return zl;
	}
	/**
	 * @param zl the zl to set
	 */
	public void setZl(float zl) {
		this.zl = zl;
	}
	/**
	 * @return the zje
	 */
	public float getZje() {
		return zje;
	}
	/**
	 * @param zje the zje to set
	 */
	public void setZje(float zje) {
		this.zje = zje;
	}
	/**
	 * @return the rq
	 */
	public Date getRq() {
		return rq;
	}
	/**
	 * @param rq the rq to set
	 */
	public void setRq(Date rq) {
		this.rq = rq;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "grbm:" + grbm + ",mc:" + mc +",dl:" + dl + ",dj:" + dj + ",zl:" + zl + ",zje:" + zje + ",rq:" + rq;
	}

}
