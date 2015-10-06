package se.grupp4.minbusskompis.api.datatypes.vt;

public class Location
{
	private String name;
	private Coord coord;
	
	public Location(String name, Coord coord)
	{
		this.name = name;
		this.coord = coord;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public Coord getCoord()
	{
		return this.coord;
	}
	
	public String toString()
	{
		return name + " " + coord.toString();
	}
}