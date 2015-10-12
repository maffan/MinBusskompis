package se.grupp4.minbusskompis.api.datatypes.vt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Leg
{
	private HashMap<String, String> leg;
	// name, sname, type, id, cancelled, reachable, direction, booking, night
	// fgColor, bgColor, stroke, accessibility, kcal, percentBikeRoad
	
	private Origin orig;
	private Destination dest;
	private ArrayList<Note> notes; // TODO Order by priority
	private String journeyDetailRef;
	private String geometryRef;
	
	public Leg()
	{
		leg = new HashMap<String, String>();
		orig = new Origin();
		dest = new Destination();
		journeyDetailRef = "";
	}
	
	public void addOriginAndDestination(Origin orig, Destination dest)
	{
		this.orig = orig;
		this.dest = dest;
	}
	
	public void setJourneyDetailRef(String ref)
	{
		journeyDetailRef = ref;
	}
	
	public void add(String key, String value)
	{
		leg.put(key, value);
	}
	
	public String getValueOf(String key)
	{
		return leg.get(key);
	}
	
	public Origin getOrigin()
	{
		return orig;
	}
	
	public Destination getDestination()
	{
		return dest;
	}
	
	public String toString()
	{
		String s = "";
		
		for(Entry<String, String> ks : leg.entrySet())
		{
			s += ks.getKey() + ": " + ks.getValue() + ", ";			
		}
		
		return s;
	}
}