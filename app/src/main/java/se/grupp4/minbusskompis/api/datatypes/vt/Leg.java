package se.grupp4.minbusskompis.api.datatypes.vt;

import java.util.ArrayList;
import java.util.HashMap;

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
	
	public Leg(Origin orig, Destination dest)
	{
		this.orig = orig;
		this.dest = dest;
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
}