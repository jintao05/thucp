package outlier.cluster;

import java.util.ArrayList;

public class Point {
	
	public String id;
	public ArrayList<Double> value;
	
	public Point(String id,ArrayList<Double> value){
		this.id=id;
		this.value=value;
	}
	
	@Override
	public boolean equals(Object obj) {
		Point point = (Point) obj;
		for(int i=0;i<value.size();i++){
			if(!value.get(i).equals(point.value.get(i)))
				return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return id+":"+value.size()+"size:"+value.toString(); 
	}
}