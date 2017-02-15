package pojo;
/**
 * @author Jiawen Sun
 */
public class Route {
	private String start;
	 private String end;
	 public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public double getLenth() {
		return lenth;
	}
	public void setLenth(double lenth) {
		this.lenth = lenth;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFlow() {
		return flow;
	}
	public void setFlow(String flow) {
		this.flow = flow;
	}
	public String getWeather() {
		return weather;
	}
	public void setWeather(String weather) {
		this.weather = weather;
	}
	private String time;
	 private double lenth;
	 private String name;
	 private String flow;
	 private String weather;
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}//简单定义一条路，具体描述之后添加
	 

}
