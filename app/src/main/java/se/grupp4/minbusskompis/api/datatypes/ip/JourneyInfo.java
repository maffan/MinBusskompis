package se.grupp4.minbusskompis.api.datatypes.ip;

public class JourneyInfo
{
	private String name;
	private String destination;
	
	public JourneyInfo(String name, String destination)
	{
		this.name = name;
		this.destination = destination;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getDestination()
	{
		return this.destination;
	}
	
	public String toString()
	{
		return "Name: " + name + ", Destination: " + destination;
	}
}