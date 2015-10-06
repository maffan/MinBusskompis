package se.grupp4.minbusskompis.api.datatypes.vt;

public class StopLocation
{
	public String name;
	public String id;
	public String latitude;
	public String longitude;
	public String track;

	public StopLocation(String name, String id, String latitude, String longitude, String track)
	{
		this.name = name;
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.track = track;
	}
	
	public String toString()
	{
		return "Name: " + name +
				" ID: "+ id +
				", Latitude: " + latitude +
				", Longitude: " + longitude +
				", Track: " + track;
	}
}