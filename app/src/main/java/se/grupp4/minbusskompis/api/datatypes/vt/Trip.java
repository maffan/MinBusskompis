package se.grupp4.minbusskompis.api.datatypes.vt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Trip
{
	private ArrayList<Leg> legs;
	private HashMap<String, String> trip;
	// alternative, valid, travelWarranty, type

	public Trip()
	{
		legs = new ArrayList<Leg>();
		trip = new HashMap<String, String>();
	}
	
	public void AddTrip(Leg leg)
	{
		legs.add(leg);
	}
	
	public void add(String key, String value)
	{
		trip.put(key, value);
	}
	
	public String getValueOf(String key)
	{
		return trip.get(key);
	}
	
	public ArrayList<Leg> getLegs()
	{
		return legs;
	}
}