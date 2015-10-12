package se.grupp4.minbusskompis.api.datatypes.vt;

import java.util.ArrayList;
import java.util.HashMap;

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

	public Boolean hasElectricityInTrip()
	{
		for(int i = 0; i < legs.size(); i++)
		{
			if(legs.get(i).getValue("type").equals("BUS"))
			{
				if(legs.get(i).getValue("sname").equals("55"))
				{
					System.out.println(legs.get(i));
					return true;
				}
			}
		}
		
		return false;
	}
}