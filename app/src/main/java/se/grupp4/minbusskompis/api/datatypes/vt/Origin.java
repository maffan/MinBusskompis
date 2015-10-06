package se.grupp4.minbusskompis.api.datatypes.vt;

import java.util.ArrayList;
import java.util.HashMap;

public class Origin
{
	private HashMap<String, String> orig;
//	name, type, id, routeIdx, time, date, directtime, directdate
//	track, rTime, rDate, rtTrack, cancelled;
	
	private ArrayList<Note> notes; // TODO Order by priority
	
	public Origin()
	{
		orig = new HashMap<String, String>();
		notes = new ArrayList<Note>();
	}
	
	public void addNote(Note note)
	{
		notes.add(note);
	}
	
	public void add(String key, String value)
	{
		orig.put(key, value);
	}
	
	public String getValueOf(String key)
	{
		return orig.get(key);
	}
}