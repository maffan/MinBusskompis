package se.grupp4.minbusskompis.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.grupp4.minbusskompis.api.datatypes.ip.JourneyInfo;
import se.grupp4.minbusskompis.api.datatypes.vt.Coord;

class InnovationPlatform
{
	private static String key = "Basic Z3JwMjE6dlJ0Q2xydE9tMg==";
	private static String baseurl = "https://ece01.ericsson.net:4443/ecity?";

	private static String httpGet(int sec, String params) throws IOException
	{
		long t2 = System.currentTimeMillis() - 5000;
		long t1 = t2 - (1000 * sec);

		String url = baseurl + params + "&t1=" + t1 + "&t2=" + t2;

		URL requestURL = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) requestURL.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("Authorization", key);

		try {
			if (con.getResponseCode() != 200) {
				throw new IOException(con.getResponseMessage());
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return "";
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuffer response = new StringBuffer();

		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		con.disconnect();

		return response.toString();
	}

	private static String getLatestStringValueOf(String parameter, JSONArray data) throws JSONException
	{
		TimedValue timedValue = new TimedValue("", 0);

		for(int i = 0; i < data.length(); i++)
		{
			Object jso = data.get(i);

			int time = ((JSONObject)jso).getInt("timestamp");
			String key = ((JSONObject)jso).getString("resourceSpec");
			String value = ((JSONObject)jso).getString("value");

			if(key.equals(parameter))
				if(time > timedValue.getTime())
					timedValue.setValue(value, time);
		}

		return timedValue.value;
	}

	private static class TimedValue
	{
		private String value;
		private int time;

		TimedValue(String value, int time)
		{
			this.value = value;
			this.time = time;
		}

		public int getTime()
		{
			return this.time;
		}

		public void setValue(String value, int time)
		{
			this.value = value;
			this.time = time;
		}
	}

	public static String getNextStop(String dgw) throws IOException, JSONException
	{
		String params = "dgw=" + dgw + "&sensorSpec=Ericsson$Next_Stop";

		String response = httpGet(50, params);

		JSONArray data = new JSONArray(response);

		System.out.println(response);

		String name = getLatestStringValueOf("Bus_Stop_Name_Value", data);

		return name;
	}

	public static Coord getLatestCoordOf(String dgw) throws IOException, JSONException
	{
		String params = "dgw=" + dgw + "&sensorSpec=Ericsson$GPS";

//		String response = httpGet(5, params);
		String response = "[{\"resourceSpec\":\"Course_Value\",\"timestamp\":1443458957144,\"value\":\"266\",\"gatewayId\":\"Vin_Num_001\"},{\"resourceSpec\":\"Latitude_Value\",\"timestamp\":1443458957144,\"value\":\"57.719976\",\"gatewayId\":\"Vin_Num_001\"},{\"resourceSpec\":\"Speed_Value\",\"timestamp\":1443458957144,\"value\":\"66.1\",\"gatewayId\":\"Vin_Num_001\"},{\"resourceSpec\":\"Longitude_Value\",\"timestamp\":1443458957144,\"value\":\"11.960828\",\"gatewayId\":\"Vin_Num_001\"}]";

		JSONArray data = new JSONArray(response);

		String lat = getLatestStringValueOf("Latitude_Value", data);
		String lon = getLatestStringValueOf("Longitude_Value", data);

		return new Coord(lat, lon);
	}

	public static JourneyInfo getJourneyInfo(String dgw) throws IOException, JSONException
	{
		String params = "dgw=" + dgw + "&sensorSpec=Ericsson$Journey_Info";

//		String response = httpGet(5, params);
		String response = "[{\"resourceSpec\":\"Destination_Value\",\"timestamp\":1443469928208,\"value\":\"Lindholmen\",\"gatewayId\":\"Vin_Num_001\"},{\"resourceSpec\":\"Destination_Value\",\"timestamp\":1443469938209,\"value\":\"Lindholmen\",\"gatewayId\":\"Vin_Num_001\"},{\"resourceSpec\":\"Destination_Value\",\"timestamp\":1443469943272,\"value\":\"Lindholmen\",\"gatewayId\":\"Vin_Num_001\"},{\"resourceSpec\":\"Destination_Value\",\"timestamp\":1443469948209,\"value\":\"Lindholmen\",\"gatewayId\":\"Vin_Num_001\"},{\"resourceSpec\":\"Journey_Name_Value\",\"timestamp\":1443469928208,\"value\":\"45\",\"gatewayId\":\"Vin_Num_001\"},{\"resourceSpec\":\"Journey_Name_Value\",\"timestamp\":1443469938209,\"value\":\"45\",\"gatewayId\":\"Vin_Num_001\"},{\"resourceSpec\":\"Journey_Name_Value\",\"timestamp\":1443469943272,\"value\":\"45\",\"gatewayId\":\"Vin_Num_001\"},{\"resourceSpec\":\"Journey_Name_Value\",\"timestamp\":1443469948209,\"value\":\"45\",\"gatewayId\":\"Vin_Num_001\"}]";

		JSONArray data = new JSONArray(response);

		String name = getLatestStringValueOf("Destination_Value", data);
		String dest = getLatestStringValueOf("Journey_Name_Value", data);

		return new JourneyInfo(name, dest);
	}
	
	public static HashMap<String, String> getAllJourneyNames() throws IOException, JSONException
	{
		String params = "sensorSpec=Ericsson$Journey_Info";

//		String response = httpGet(50, params);
		String response = "[{\"resourceSpec\":\"Destination_Value\",\"timestamp\":1443600133779,\"value\":\"Lindholmen\",\"gatewayId\":\"Vin_Num_001\"},{\"resourceSpec\":\"Destination_Value\",\"timestamp\":1443600148765,\"value\":\"Lindholmen\",\"gatewayId\":\"Vin_Num_001\"},{\"resourceSpec\":\"Destination_Value\",\"timestamp\":1443600158765,\"value\":\"Lindholmen\",\"gatewayId\":\"Vin_Num_001\"},{\"resourceSpec\":\"Destination_Value\",\"timestamp\":1443600163765,\"value\":\"Lindholmen\",\"gatewayId\":\"Vin_Num_001\"},{\"resourceSpec\":\"Journey_Name_Value\",\"timestamp\":1443600133779,\"value\":\"45\",\"gatewayId\":\"Vin_Num_001\"},{\"resourceSpec\":\"Journey_Name_Value\",\"timestamp\":1443600148765,\"value\":\"45\",\"gatewayId\":\"Vin_Num_001\"},{\"resourceSpec\":\"Journey_Name_Value\",\"timestamp\":1443600158765,\"value\":\"45\",\"gatewayId\":\"Vin_Num_001\"},{\"resourceSpec\":\"Journey_Name_Value\",\"timestamp\":1443600163765,\"value\":\"45\",\"gatewayId\":\"Vin_Num_001\"},{\"resourceSpec\":\"Destination_Value\",\"timestamp\":1443600148416,\"value\":\"Johanneberg\",\"gatewayId\":\"100021\"},{\"resourceSpec\":\"Destination_Value\",\"timestamp\":1443600153980,\"value\":\"Johanneberg\",\"gatewayId\":\"100021\"},{\"resourceSpec\":\"Destination_Value\",\"timestamp\":1443600178378,\"value\":\"Johanneberg\",\"gatewayId\":\"100021\"},{\"resourceSpec\":\"Journey_Name_Value\",\"timestamp\":1443600148416,\"value\":\"49\",\"gatewayId\":\"100021\"},{\"resourceSpec\":\"Journey_Name_Value\",\"timestamp\":1443600153980,\"value\":\"49\",\"gatewayId\":\"100021\"},{\"resourceSpec\":\"Journey_Name_Value\",\"timestamp\":1443600178378,\"value\":\"49\",\"gatewayId\":\"100021\"}]";
		
		JSONArray data = new JSONArray(response);
		HashMap<String, String> busses = new HashMap<String, String>();
		
		for(int i = 0; i < data.length(); i++)
		{
			Object jso = data.get(i);

			String resourceSpec = ((JSONObject)jso).getString("resourceSpec");
			
			if(resourceSpec.equals("Journey_Name_Value"))
			{
				String gatewayId = ((JSONObject)jso).getString("gatewayId");
				String journeyNameValue = ((JSONObject)jso).getString("value");
				busses.put(gatewayId, journeyNameValue);
			}
		}

		return busses;
	}
	
	public static String getOutsideTemperature(String dgw) throws IOException, JSONException
	{
		String params = "dgw=" + dgw + "&sensorSpec=Ericsson$Ambient_Temperature";

		String response = httpGet(50, params);
//		String response = "[{\"resourceSpec\":\"Ambient_Temperature_Value\",\"timestamp\":1443599508901,\"value\":\"29.061187\",\"gatewayId\":\"Vin_Num_001\"},{\"resourceSpec\":\"Ambient_Temperature_Value\",\"timestamp\":1443599518558,\"value\":\"26.408052\",\"gatewayId\":\"Vin_Num_001\"},{\"resourceSpec\":\"Ambient_Temperature_Value\",\"timestamp\":1443599523558,\"value\":\"29.020434\",\"gatewayId\":\"Vin_Num_001\"},{\"resourceSpec\":\"Ambient_Temperature_Value\",\"timestamp\":1443599538684,\"value\":\"26.257422\",\"gatewayId\":\"Vin_Num_001\"},{\"resourceSpec\":\"Ambient_Temperature_Value\",\"timestamp\":1443599548606,\"value\":\"24.682406\",\"gatewayId\":\"Vin_Num_001\"}]";
		
		System.out.println(response);
		
		JSONArray data = new JSONArray(response);

		String temp = getLatestStringValueOf("Ambient_Temperature_Value", data);

		return temp;
	}
}