package se.grupp4.minbusskompis.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.grupp4.minbusskompis.api.datatypes.vt.Coord;
import se.grupp4.minbusskompis.api.datatypes.vt.Destination;
import se.grupp4.minbusskompis.api.datatypes.vt.Leg;
import se.grupp4.minbusskompis.api.datatypes.vt.VTLocation;
import se.grupp4.minbusskompis.api.datatypes.vt.Origin;
import se.grupp4.minbusskompis.api.datatypes.vt.StopLocation;
import se.grupp4.minbusskompis.api.datatypes.vt.Trip;

class VastTrafik
{
	private static String key = "a0af190e-e343-4b0d-a1dc-6fdbff217050";
	private static String baseurl = "http://api.vasttrafik.se/bin/rest.exe/v1/#?authKey=" + key;

	private static String httpGet(String urlStr) throws IOException
	{
		URL url = new URL(urlStr);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		
		
		if (con.getResponseCode() != 200) {
			throw new IOException(con.getResponseMessage());
		}

		BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuilder sb = new StringBuilder();
		
		String line;
		while ((line = rd.readLine()) != null)
		{
			sb.append(line);
		}
		rd.close();
		con.disconnect();
		
		return sb.toString();
	}
	
	public static ArrayList<StopLocation> getNearbyStops(String lat, String lng) throws IOException, JSONException
	{
		UrlCallBuilder uBuilder = new UrlCallBuilder(baseurl, "location.nearbystops");
		uBuilder.AddParameter("originCoordLat", lat);
		uBuilder.AddParameter("originCoordLong", lng);
//		uBuilder.AddParameter("maxNo", maxNo);		TODO Add as optional, default = 10
//		uBuilder.AddParameter("maxDist", maxDist);	TODO Add as optional, default = 1000

		String response = httpGet(uBuilder.getUrl() + "&format=json");
		//String response = JsonTestResponses.getNearbyStops;
		
		JSONObject jsonData = new JSONObject(response);
		JSONArray list = jsonData.getJSONObject("LocationList").getJSONArray("StopLocation");
		
		ArrayList<StopLocation> locationList = new ArrayList<StopLocation>();
		
		for (int i = 0; i < list.length(); i++)
		{
			JSONObject jso = (JSONObject) list.get(i);

			String name = jso.getString("name");
			String id = jso.getString("id");
			String latitude = jso.getString("lat");
			String longitude = jso.getString("lon");
			String track;
			try {
				track = jso.getString("track");
			} catch (JSONException e) {
				track = "";
			}
			
			StopLocation loc = new StopLocation(name, id, latitude, longitude, track);

			locationList.add(loc);
		}
		
		return locationList;
	}
	
	public static ArrayList<VTLocation> getLocationNamesByString(String input) throws IOException, JSONException
	{
		UrlCallBuilder uBuilder = new UrlCallBuilder(baseurl, "location.name");
		uBuilder.AddParameter("input", getISO88591String(input));
		
		String response = httpGet(uBuilder.getUrl() + "&format=json");
		//String response = JsonTestResponses.getLocationNamesByStringArr;
		//String response = JsonTestResponses.getLocationNamesByStringObjAndArr;

		JSONObject jsonData = new JSONObject(response);
		
		ArrayList<VTLocation> locationList = new ArrayList<VTLocation>();

		Iterator<String> keysIter = jsonData.getJSONObject("LocationList").keys();
		while (keysIter.hasNext())
		{
			String key = keysIter.next();

			if(key.equals("serverdate") || key.equals("servertime") || key.equals("noNamespaceSchemaLocation"))
				continue;

			Object locations = jsonData.getJSONObject("LocationList").get(key);
			
			if (locations instanceof JSONArray)
			{
				JSONArray stopLocArr = (JSONArray) locations;
				
				for (int i = 0; i < stopLocArr.length(); i++)
				{
					VTLocation loc = getJSONLocation((JSONObject) stopLocArr.get(i));

					locationList.add(loc);
				}
			}
			else
			{
				VTLocation loc = getJSONLocation((JSONObject) locations);

				locationList.add(loc);
			}
		}
		
		return locationList;
	}
	
	public static ArrayList<Trip> getTripList(Coord from, Coord to) throws IOException, JSONException
	{
		UrlCallBuilder uBuilder = new UrlCallBuilder(baseurl, "trip");

		uBuilder.AddParameter("originCoordLat", from.getLatitude());
		uBuilder.AddParameter("originCoordLong", from.getLongitude());
		uBuilder.AddParameter("originCoordName", "0");
		uBuilder.AddParameter("destCoordLat", to.getLatitude());
		uBuilder.AddParameter("destCoordLong", to.getLongitude());
		uBuilder.AddParameter("destCoordName", "0");
		// TODO needGeo=1
		
		String response = httpGet(uBuilder.getUrl() + "&format=json");
		//String response = JsonTestResponses.getTripList;

		JSONObject jsonData = new JSONObject(response);
		JSONArray tripList = jsonData.getJSONObject("TripList").getJSONArray("Trip");

		ArrayList<Trip> triplist = new ArrayList<Trip>();
		
		for(int i = 0; i < tripList.length(); i++)
		{
			JSONObject tripJSON = (JSONObject) tripList.get(i);
			
			JSONArray legArr = (JSONArray) tripJSON.get("Leg");
			
			Trip trip = new Trip();
			
			for(int j = 0; j < legArr.length(); j++)
			{
				JSONObject legJSON = (JSONObject)legArr.get(j);
				
				Leg leg = new Leg();
				Origin orig = new Origin();
				Destination dest = new Destination();
				
				Iterator<String> legJSONIter = legJSON.keys();
				while (legJSONIter.hasNext())
				{
					String key = legJSONIter.next();
					
					if(key.equals("Origin") || key.equals("Destination"))
					{
						JSONObject arrJSON = legJSON.getJSONObject(key);
						
						Iterator<String> legArrIter = arrJSON.keys();
						
						while (legArrIter.hasNext())
						{
							String iKey = legArrIter.next();
							
							if(key.equals("Origin"))
								orig.add(iKey, arrJSON.getString(iKey));
							else if(key.equals("Destination"))
								dest.add(iKey, arrJSON.getString(iKey));
						}
					}
					else if(key.equals("JourneyDetailRef"))
					{
						String ref = legJSON.getJSONObject(key).getString("ref");
						leg.setJourneyDetailRef(ref);
					}
					else
						leg.add(key, legJSON.getString(key));
				}

				leg.addOriginAndDestination(orig, dest);
				
				trip.AddTrip(leg);
			}
			
			triplist.add(trip);
		}
		
		return triplist;
	}
	
	private static VTLocation getJSONLocation(JSONObject location) throws JSONException
	{
		String name = location.getString("name");
		Coord coord = new Coord(location.getString("lat"),location.getString("lon"));
		
		return new VTLocation(name, coord);
	}
	
	private static String getISO88591String(String input) throws UnsupportedEncodingException
	{
		return URLEncoder.encode(input, "ISO-8859-1");
	}
}