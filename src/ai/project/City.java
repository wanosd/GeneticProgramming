package ai.project;

public class City {
	
	private String name; 
	private double x, y;
	private int id = 0;
	
	public City(String name, double x, double y, int id)
	{
		this.name = name;
		this.x = x;
		this.y = y;
		this.id = id;
	}
	

	public String getName()
	{
		return name;
	}
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
	
	public void setID(int id)
	{
		this.id = id;
	}
	
	public int getID()
	{
		return id;
	}
}
