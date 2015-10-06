package se.grupp4.minbusskompis.api.datatypes.vt;

public class Coord
{
	private String latitude;
	private String longitude;
	
	public Coord(String Latitude, String Longitude)
	{
		this.latitude = Latitude;
		this.longitude = Longitude;
	}
	
	public String getLatitude()
	{
		return this.latitude;
	}
	
	public String getLongitude()
	{
		return this.longitude;
	}
	
	public String toString()
	{
		return this.latitude + "," + this.longitude;
	}
}