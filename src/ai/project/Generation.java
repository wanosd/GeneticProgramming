package ai.project;

public class Generation {
	private String bestPath;
	private String bestDist;
	private int genNo;
	private double average;
	
	public Generation(int genNo, String bestPath, String bestDist, double average){
		this.genNo = genNo;
		this.bestPath = bestPath;
		this.bestDist = bestDist;
		this.average = average;
	}
	
	public int getGenNo(){
		return genNo;
	}
	
	public String getBestP(){
		return bestPath;
	}
	
	public String getBestD(){
		return bestDist;
	}
	
	public double getAverage(){
		return average;
	}
}
