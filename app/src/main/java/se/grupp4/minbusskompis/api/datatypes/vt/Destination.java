package se.grupp4.minbusskompis.api.datatypes.vt;

import java.util.ArrayList;
import java.util.HashMap;

public class Destination
{
	private HashMap<String, String> dest;
//	name, type, id, routeIdx, time, date, directtime, directdate
//	track, rTime, rDate, rtTrack, cancelled;
	
	private ArrayList<Note> notes; // TODO Order by priority
	
	public Destination()
	{
		dest = new HashMap<String, String>();
	}
	
	public void add(String key, String value)
	{
		dest.put(key, value);
	}
	
	public String getValueOf(String key)
	{
		return dest.get(key);
	}
}