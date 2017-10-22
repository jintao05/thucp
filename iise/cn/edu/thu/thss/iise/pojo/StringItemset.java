/**
 * 
 */
package cn.edu.thu.thss.iise.pojo;

import java.util.ArrayList;

/**
 * @author Tao Jin
 *
 */
public class StringItemset {
	
	private double supRatio = 0;
	private ArrayList<String> strItemset = new ArrayList<String>();
	
	public StringItemset(double supRatio, ArrayList<String> strItemset) {
		this.supRatio = supRatio;
		this.strItemset = strItemset;
	}

	/**
	 * @return the supRatio
	 */
	public double getSupRatio() {
		return supRatio;
	}



	/**
	 * @param supRatio the supRatio to set
	 */
	public void setSupRatio(double supRatio) {
		this.supRatio = supRatio;
	}



	/**
	 * @return the strItemset
	 */
	public ArrayList<String> getStrItemset() {
		return strItemset;
	}



	/**
	 * @param strItemset the strItemset to set
	 */
	public void setStrItemset(ArrayList<String> strItemset) {
		this.strItemset = strItemset;
	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
