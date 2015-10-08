package se.grupp4.minbusskompis.api.datatypes.vt;

public class VTLocation
{
	private String name;
	private Coord coord;
	
	public VTLocation(String name, Coord coord)
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