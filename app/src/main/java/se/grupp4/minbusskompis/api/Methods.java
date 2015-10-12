package se.grupp4.minbusskompis.api;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import se.grupp4.minbusskompis.api.datatypes.ip.JourneyInfo;
import se.grupp4.minbusskompis.api.datatypes.vt.Coord;
import se.grupp4.minbusskompis.api.datatypes.vt.VTLocation;
import se.grupp4.minbusskompis.api.datatypes.vt.StopLocation;
import se.grupp4.minbusskompis.api.datatypes.vt.Trip;

public class Methods 
{
	// V�sttrafik
	
	public static ArrayList<StopLocation> getNearbyStops(String lat, String lng) throws IOException, JSONException
	{
		return VastTrafik.getNearbyStops(lat, lng);
	}
	
	public static ArrayList<VTLocation> getLocationNamesByString(String input) throws IOException, JSONException
	{
		return VastTrafik.getLocationNamesByString(input);
	}
	
	public static ArrayList<Trip> getTriplist(Coord from, Coord to) throws IOException, JSONException
	{
		return VastTrafik.getTripList(from, to);
	}
	
	public static ArrayList<Coord> getGeometry(String url) throws IOException, JSONException
	{
		return VastTrafik.getGeometry(url);
	}
	
	// Innovation Platform
	
	public static String getNextStop(String dgw) throws IOException, JSONException
	{
		return InnovationPlatform.getNextStop(dgw);
	}
	
	public static Coord getLatestCordOf(String dqw) throws IOException, JSONException
	{
		return InnovationPlatform.getLatestCoordOf(dqw);
	}

	public static JourneyInfo getJourneyInfo(String dgw) throws IOException, JSONException
	{
		return InnovationPlatform.getJourneyInfo(dgw);
	}

	public static String getOutsideTemperature(String dgw) throws IOException, JSONException
	{
		return InnovationPlatform.getOutsideTemperature(dgw);
	}

	public static HashMap<String, String> getAllJourneyNamesIP() throws IOException, JSONException
	{
		return InnovationPlatform.getAllJourneyNames();
	}
}