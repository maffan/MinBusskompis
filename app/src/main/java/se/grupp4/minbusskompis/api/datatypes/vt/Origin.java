package se.grupp4.minbusskompis.api.datatypes.vt;

import java.util.ArrayList;

public class Origin extends KeyValueBase
{
//	name, type, id, routeIdx, time, date, directtime, directdate
//	track, rTime, rDate, rtTrack, cancelled;
	
	private ArrayList<Note> notes; // TODO Order by priority
	
	public Origin()
	{
		super();
		notes = new ArrayList<Note>();
	}
	
	public void addNote(Note note)
	{
		notes.add(note);
	}
}