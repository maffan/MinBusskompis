package se.grupp4.minbusskompis.api.datatypes.vt;

import java.util.ArrayList;

public class Leg extends KeyValueBase
{
	// name, sname, type, id, cancelled, reachable, direction, booking, night
	// fgColor, bgColor, stroke, accessibility, kcal, percentBikeRoad
	
	private Origin orig;
	private Destination dest;
	private ArrayList<Note> notes; // TODO Order by priority
	private String journeyDetailRef;
	private String geometryRef;
	
	public Leg()
	{
		super();
		orig = new Origin();
		dest = new Destination();
		journeyDetailRef = "";
		geometryRef = "";
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
	
	public String getJourneyDetailRef()
	{
		return journeyDetailRef;
	}

	public void setGeometryRef(String ref)
	{
		geometryRef = ref;
	}
	
	public String getGeometryRef()
	{
		return geometryRef;
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