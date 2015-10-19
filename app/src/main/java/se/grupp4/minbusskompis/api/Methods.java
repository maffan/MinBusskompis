package se.grupp4.minbusskompis.api;

import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import se.grupp4.minbusskompis.api.datatypes.ip.JourneyInfo;
import se.grupp4.minbusskompis.api.datatypes.vt.Coord;
import se.grupp4.minbusskompis.api.datatypes.vt.VTLocation;
import se.grupp4.minbusskompis.api.datatypes.vt.StopLocation;
import se.grupp4.minbusskompis.api.datatypes.vt.Trip;

/**
 * Contains methods for API calls
 * to Innovation Plattform and Västtrafik.
 */
public class Methods 
{
	/**
	 *
	 * @param lat
	 * @param lng
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	public static ArrayList<StopLocation> getNearbyStops(String lat, String lng) throws IOException, JSONException
	{
		return VastTrafik.getNearbyStops(lat, lng);
	}
	
	public static ArrayList<VTLocation> getLocationNamesByString(String input) throws IOException, JSONException
	{
		return VastTrafik.getLocationNamesByString(input);
	}

	/**
	 * Returns a list of Trip's between from and to.
	 * @param from Coord
	 * @param to Coord
	 * @return ArrayList<Trip>
	 * @see Trip
	 * @see Coord
	 */
	public static ArrayList<Trip> getTriplist(Coord from, Coord to)
	{
		ArrayList<Trip> tripList = null;

		try{
			tripList = VastTrafik.getTripList(from, to);
		} catch(JSONException e)		{
			Log.e("API", "getTriplist(): " + e.getStackTrace().toString());
		}

		return tripList;
	}

	/**
	 * Gets the nearest trip from current time.
	 * @param from Coord
	 * @param to Coord
	 * @return Trip
	 * @see Trip
	 * @see Coord
	 */
	public static Trip getClosestTrip(Coord from, Coord to)
	{
		ArrayList<Trip> tripList = getTriplist(from, to);

		if(tripList == null)
			return null;

		return tripList.get(0);
	}
	
	public static ArrayList<Coord> getGeometry(String url)
	{
		ArrayList<Coord> coordList;
		try{
			coordList = VastTrafik.getGeometry(url);
		}catch(IOException | JSONException e){
			coordList = new ArrayList<>();
			coordList.add(new Coord("0","0"));
		}

		return coordList;
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

	public static HashMap<String, String> getAllJourneyNames() throws IOException, JSONException
	{
		return InnovationPlatform.getAllJourneyNames();
	}

	/**
	 * Checks if bus is at bus stop.
	 * @param dgw Bus id
	 * @return boolean
	 */
	public static boolean isAtStop(String dgw)
	{
		boolean value = false;
		try{
			value = InnovationPlatform.isAtStop(dgw);
		}catch(JSONException|IOException e){
			Log.e("API", "isAtStop(): " + e.getStackTrace().toString());
		}
		return value;
	}

	/**
	 * Checks if the bus stop button is pressed.
	 * @param dgw Bus id
	 * @return boolean
	 */
	public static boolean isStopPressed(String dgw)
	{
		boolean value = false;
		try{
			value = InnovationPlatform.isStopPressed(dgw);
		}catch(JSONException|IOException e){
			Log.e("API", "isStopPressed(): " + e.getStackTrace().toString());
		}
		return value;
	}
}