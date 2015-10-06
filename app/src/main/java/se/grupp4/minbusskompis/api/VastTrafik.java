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
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.grupp4.minbusskompis.api.datatypes.vt.Coord;
import se.grupp4.minbusskompis.api.datatypes.vt.Destination;
import se.grupp4.minbusskompis.api.datatypes.vt.Leg;
import se.grupp4.minbusskompis.api.datatypes.vt.Location;
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

//		String response = httpGet(uBuilder.getUrl() + "&format=json");
		String response = "{\"LocationList\":{  \"noNamespaceSchemaLocation\":\"http://api.vasttrafik.se/v1/hafasRestLocation.xsd\",  \"servertime\":\"22:06\",  \"serverdate\":\"2015-09-30\",  \"StopLocation\":[{    \"name\":\"Polhemsplatsen, Göteborg\",    \"id\":\"9022014005137002\",    \"lat\":\"57.708940\",    \"lon\":\"11.977353\",    \"track\":\"B\"    },{    \"name\":\"Polhemsplatsen, Göteborg\",    \"id\":\"9022014005137003\",    \"lat\":\"57.707645\",    \"lon\":\"11.979537\",    \"track\":\"C\"    },{    \"name\":\"Polhemsplatsen, Göteborg\",    \"id\":\"9021014005137000\",    \"lat\":\"57.708320\",    \"lon\":\"11.977317\"    },{    \"name\":\"Ullevi Norra, Göteborg\",    \"id\":\"9022014007171005\",    \"lat\":\"57.707987\",    \"lon\":\"11.985029\",    \"track\":\"E\"    },{    \"name\":\"Ullevi Norra, Göteborg\",    \"id\":\"9022014007171006\",    \"lat\":\"57.707978\",    \"lon\":\"11.985074\",    \"track\":\"F\"    },{    \"name\":\"Polhemsplatsen, Göteborg\",    \"id\":\"9022014005137001\",    \"lat\":\"57.707825\",    \"lon\":\"11.977389\",    \"track\":\"A\"    },{    \"name\":\"Ullevi Norra, Göteborg\",    \"id\":\"9022014007171001\",    \"lat\":\"57.708131\",    \"lon\":\"11.986198\",    \"track\":\"A\"    },{    \"name\":\"Ullevi Norra, Göteborg\",    \"id\":\"9022014007171002\",    \"lat\":\"57.708167\",    \"lon\":\"11.986315\",    \"track\":\"B\"    },{    \"name\":\"Ullevi Norra, Göteborg\",    \"id\":\"9021014007171000\",    \"lat\":\"57.707780\",    \"lon\":\"11.985883\"    },{    \"name\":\"Ullevi Norra, Göteborg\",    \"id\":\"9022014007171003\",    \"lat\":\"57.707322\",    \"lon\":\"11.985569\",    \"track\":\"C\"    }]  }}";
		
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
	
	public static ArrayList<Location> getLocationNamesByString(String input) throws IOException, JSONException
	{
		UrlCallBuilder uBuilder = new UrlCallBuilder(baseurl, "location.name");
		uBuilder.AddParameter("input", getISO88591String(input));
		
//		String response = httpGet(uBuilder.getUrl() + "&format=json");
		
		// All in array
		String response = "{\"LocationList\":{  \"noNamespaceSchemaLocation\":\"http://api.vasttrafik.se/v1/hafasRestLocation.xsd\",  \"servertime\":\"23:39\",  \"serverdate\":\"2015-09-30\",  \"StopLocation\":[{    \"name\":\"GÖTEBORG\",    \"lon\":\"11.972391\",    \"lat\":\"57.709461\",    \"id\":\"0000000800000022\",    \"idx\":\"1\"    },{    \"name\":\"Göteborg C, Göteborg\",    \"lon\":\"11.973658\",    \"lat\":\"57.709299\",    \"id\":\"9021014008000000\",    \"idx\":\"2\"    },{    \"name\":\"Centralstationen, Göteborg\",    \"lon\":\"11.973739\",    \"lat\":\"57.707897\",    \"id\":\"9021014001950000\",    \"idx\":\"3\"    },{    \"name\":\"Göteborgsvägen, Borås\",    \"lon\":\"12.865873\",    \"lat\":\"57.715025\",    \"id\":\"9021014082937000\",    \"idx\":\"4\"    },{    \"name\":\"Göteborgsvägen, Uddevalla\",    \"lon\":\"11.919049\",    \"lat\":\"58.341583\",    \"id\":\"9021014021155000\",    \"idx\":\"5\"    },{    \"name\":\"Götaplatsen, Göteborg\",    \"lon\":\"11.978971\",    \"lat\":\"57.697622\",    \"id\":\"9021014003020000\",    \"idx\":\"6\"    },{    \"name\":\"Gamlestaden station, Göteborg\",    \"lon\":\"12.004410\",    \"lat\":\"57.729148\",    \"id\":\"9021014002672000\",    \"idx\":\"7\"    },{    \"name\":\"Grönsakstorget, Göteborg\",    \"lon\":\"11.964435\",    \"lat\":\"57.702495\",    \"id\":\"9021014002850000\",    \"idx\":\"8\"    },{    \"name\":\"Gamlestadstorget, Göteborg\",    \"lon\":\"12.005911\",    \"lat\":\"57.728321\",    \"id\":\"9021014002670000\",    \"idx\":\"9\"    },{    \"name\":\"Göddered, Göteborg\",    \"lon\":\"11.986522\",    \"lat\":\"57.854835\",    \"id\":\"9021014002990000\",    \"idx\":\"10\"    },{    \"name\":\"Göpåsgatan, Göteborg\",    \"lon\":\"11.994064\",    \"lat\":\"57.777662\",    \"id\":\"9021014002995000\",    \"idx\":\"11\"    },{    \"name\":\"Göpåstunneln, Göteborg\",    \"lon\":\"11.999143\",    \"lat\":\"57.777806\",    \"id\":\"9021014002997000\",    \"idx\":\"12\"    },{    \"name\":\"Gropegårdsgatan, Göteborg\",    \"lon\":\"11.918437\",    \"lat\":\"57.718235\",    \"id\":\"9021014002790000\",    \"idx\":\"13\"    },{    \"name\":\"Galileis Gata, Göteborg\",    \"lon\":\"12.051577\",    \"lat\":\"57.760520\",    \"id\":\"9021014002630000\",    \"idx\":\"14\"    },{    \"name\":\"Godhemsgatan, Göteborg\",    \"lon\":\"11.925997\",    \"lat\":\"57.684858\",    \"id\":\"9021014002730000\",    \"idx\":\"15\"    },{    \"name\":\"Getebergsäng, Göteborg\",    \"lon\":\"11.991942\",    \"lat\":\"57.691842\",    \"id\":\"9021014002700000\",    \"idx\":\"16\"    },{    \"name\":\"Bellevue, Göteborg\",    \"lon\":\"12.023234\",    \"lat\":\"57.732177\",    \"id\":\"9021014001310000\",    \"idx\":\"17\"    },{    \"name\":\"Chalmers, Göteborg\",    \"lon\":\"11.972930\",    \"lat\":\"57.689955\",    \"id\":\"9021014001960000\",    \"idx\":\"18\"    },{    \"name\":\"Domkyrkan, Göteborg\",    \"lon\":\"11.963707\",    \"lat\":\"57.704292\",    \"id\":\"9021014002130000\",    \"idx\":\"19\"    },{    \"name\":\"Brunnsparken, Göteborg\",    \"lon\":\"11.967842\",    \"lat\":\"57.706944\",    \"id\":\"9021014001760000\",    \"idx\":\"20\"    },{    \"name\":\"Grimbo, Göteborg\",    \"lon\":\"11.940317\",    \"lat\":\"57.741912\",    \"id\":\"9021014002780000\",    \"idx\":\"21\"    },{    \"name\":\"Hjalmar Brantingsplatsen, Göteborg\",    \"lon\":\"11.953675\",    \"lat\":\"57.720833\",    \"id\":\"9021014003180000\",    \"idx\":\"22\"    },{    \"name\":\"Granhäll, Göteborg\",    \"lon\":\"11.892018\",    \"lat\":\"57.775226\",    \"id\":\"9021014002745000\",    \"idx\":\"23\"    },{    \"name\":\"Gåsmossen, Göteborg\",    \"lon\":\"11.959806\",    \"lat\":\"57.633763\",    \"id\":\"9021014002977000\",    \"idx\":\"24\"    },{    \"name\":\"Gasverket, Göteborg\",    \"lon\":\"11.984490\",    \"lat\":\"57.719394\",    \"id\":\"9021014002690000\",    \"idx\":\"25\"    },{    \"name\":\"Gillegatan, Göteborg\",    \"lon\":\"12.057321\",    \"lat\":\"57.741274\",    \"id\":\"9021014002710000\",    \"idx\":\"26\"    },{    \"name\":\"Godsgatan, Göteborg\",    \"lon\":\"11.821102\",    \"lat\":\"57.699348\",    \"id\":\"9021014002737000\",    \"idx\":\"27\"    },{    \"name\":\"Gråberget, Göteborg\",    \"lon\":\"11.924820\",    \"lat\":\"57.689460\",    \"id\":\"9021014002810000\",    \"idx\":\"28\"    },{    \"name\":\"Gräddgatan, Göteborg\",    \"lon\":\"12.008428\",    \"lat\":\"57.683069\",    \"id\":\"9021014002820000\",    \"idx\":\"29\"    },{    \"name\":\"Granliden, Göteborg\",    \"lon\":\"11.941144\",    \"lat\":\"57.612432\",    \"id\":\"9021014002755000\",    \"idx\":\"30\"    },{    \"name\":\"Frihamnen, Göteborg\",    \"lon\":\"11.960039\",    \"lat\":\"57.720311\",    \"id\":\"9021014002470000\",    \"idx\":\"31\"    },{    \"name\":\"Gårdstensliden, Göteborg\",    \"lon\":\"12.037203\",    \"lat\":\"57.797546\",    \"id\":\"9021014002970000\",    \"idx\":\"32\"    },{    \"name\":\"Gropens Gård, Göteborg\",    \"lon\":\"12.034965\",    \"lat\":\"57.783281\",    \"id\":\"9021014002795000\",    \"idx\":\"33\"    },{    \"name\":\"Gunnaredsvägen, Göteborg\",    \"lon\":\"12.047442\",    \"lat\":\"57.808549\",    \"id\":\"9021014002880000\",    \"idx\":\"34\"    },{    \"name\":\"Gunnesgärde, Göteborg\",    \"lon\":\"11.933297\",    \"lat\":\"57.748906\",    \"id\":\"9021014002920000\",    \"idx\":\"35\"    },{    \"name\":\"Gamla Hangaren, Göteborg\",    \"lon\":\"11.777460\",    \"lat\":\"57.702297\",    \"id\":\"9021014002640000\",    \"idx\":\"36\"    },{    \"name\":\"Gärdsås Torg, Göteborg\",    \"lon\":\"12.051244\",    \"lat\":\"57.760799\",    \"id\":\"9021014002982000\",    \"idx\":\"37\"    },{    \"name\":\"Gärdsåsgatan, Göteborg\",    \"lon\":\"12.047873\",    \"lat\":\"57.751657\",    \"id\":\"9021014002980000\",    \"idx\":\"38\"    },{    \"name\":\"Gjutegården, Göteborg\",    \"lon\":\"11.917835\",    \"lat\":\"57.632819\",    \"id\":\"9021014002715000\",    \"idx\":\"39\"    },{    \"name\":\"Glöstorpsvägen, Göteborg\",    \"lon\":\"11.938888\",    \"lat\":\"57.752942\",    \"id\":\"9021014002720000\",    \"idx\":\"40\"    },{    \"name\":\"Gröna Viken, Göteborg\",    \"lon\":\"11.767517\",    \"lat\":\"57.743243\",    \"id\":\"9021014002830000\",    \"idx\":\"41\"    },{    \"name\":\"Gunnesby Bro, Göteborg\",    \"lon\":\"11.906455\",    \"lat\":\"57.812666\",    \"id\":\"9021014002900000\",    \"idx\":\"42\"    },{    \"name\":\"Gunnesby Skola, Göteborg\",    \"lon\":\"11.909008\",    \"lat\":\"57.815696\",    \"id\":\"9021014002910000\",    \"idx\":\"43\"    },{    \"name\":\"Gunnilse Skola, Göteborg\",    \"lon\":\"12.090446\",    \"lat\":\"57.785789\",    \"id\":\"9021014002940000\",    \"idx\":\"44\"    },{    \"name\":\"Gustavsplatsen, Göteborg\",    \"lon\":\"12.009831\",    \"lat\":\"57.723332\",    \"id\":\"9021014002960000\",    \"idx\":\"45\"    },{    \"name\":\"Beväringsgatan, Göteborg\",    \"lon\":\"12.028564\",    \"lat\":\"57.739872\",    \"id\":\"9021014001450000\",    \"idx\":\"46\"    },{    \"name\":\"Gårdsten Centrum, Göteborg\",    \"lon\":\"12.033005\",    \"lat\":\"57.803749\",    \"id\":\"9021014004880000\",    \"idx\":\"47\"    },{    \"name\":\"Gamla Lillebyvägen, Göteborg\",    \"lon\":\"11.828680\",    \"lat\":\"57.750479\",    \"id\":\"9021014002660000\",    \"idx\":\"48\"    },{    \"name\":\"Gamla Torslandavägen, Göteborg\",    \"lon\":\"11.796894\",    \"lat\":\"57.738793\",    \"id\":\"9021014002665000\",    \"idx\":\"49\"    },{    \"name\":\"Gamlestadstorget Ers.hpl, Göteborg\",    \"lon\":\"12.006190\",    \"lat\":\"57.728392\",    \"id\":\"9021014002671000\",    \"idx\":\"50\"    }]  }}";
		// One object and array
//		String response = "{\"LocationList\":{  \"noNamespaceSchemaLocation\":\"http://api.vasttrafik.se/v1/hafasRestLocation.xsd\",  \"servertime\":\"23:40\",  \"serverdate\":\"2015-09-30\",  \"StopLocation\":{    \"name\":\"Fritsla station, Mark\",    \"lon\":\"12.789960\",    \"lat\":\"57.558326\",    \"id\":\"9021014063209000\",    \"idx\":\"1\"    },  \"CoordLocation\":[{    \"name\":\"Fritsla Kyrka, 511 71 Fritsla\",    \"lon\":\"12.792333\",    \"lat\":\"57.557319\",    \"type\":\"ADR\",    \"idx\":\"2\"    },{    \"name\":\"Fritslavägen, 511 73 Fritsla\",    \"lon\":\"12.758030\",    \"lat\":\"57.543664\",    \"type\":\"ADR\",    \"idx\":\"3\"    },{    \"name\":\"Fridhemsvägen, 511 73 Fritsla\",    \"lon\":\"12.800234\",    \"lat\":\"57.563926\",    \"type\":\"ADR\",    \"idx\":\"4\"    },{    \"name\":\"Fritslavägen, 511 41 Kinna\",    \"lon\":\"12.719763\",    \"lat\":\"57.522863\",    \"type\":\"ADR\",    \"idx\":\"5\"    },{    \"name\":\"Fritslavägen, 511 42 Kinnahult\",    \"lon\":\"12.729831\",    \"lat\":\"57.527484\",    \"type\":\"ADR\",    \"idx\":\"6\"    },{    \"name\":\"Fritslavägen, 511 57 Kinna\",    \"lon\":\"12.711376\",    \"lat\":\"57.514476\",    \"type\":\"ADR\",    \"idx\":\"7\"    },{    \"name\":\"Fritslavägen, 515 35 Viskafors\",    \"lon\":\"12.875780\",    \"lat\":\"57.622365\",    \"type\":\"ADR\",    \"idx\":\"8\"    },{    \"name\":\"Fritslavägen, 515 92 Kinnarumma\",    \"lon\":\"12.877488\",    \"lat\":\"57.599865\",    \"type\":\"ADR\",    \"idx\":\"9\"    },{    \"name\":\"Framsidan i Fritsla, Gärdebovägen 3, Fritsla\",    \"lon\":\"12.784800\",    \"lat\":\"57.555296\",    \"type\":\"POI\",    \"idx\":\"10\"    },{    \"name\":\"Finabovägen, 511 72 Fritsla\",    \"lon\":\"12.773626\",    \"lat\":\"57.558802\",    \"type\":\"ADR\",    \"idx\":\"11\"    },{    \"name\":\"Förläggarevägen, 511 72 Fritsla\",    \"lon\":\"12.785051\",    \"lat\":\"57.556132\",    \"type\":\"ADR\",    \"idx\":\"12\"    },{    \"name\":\"Furubergsvägen, 511 72 Fritsla\",    \"lon\":\"12.795254\",    \"lat\":\"57.564258\",    \"type\":\"ADR\",    \"idx\":\"13\"    },{    \"name\":\"Furåsvägen, 511 71 Fritsla\",    \"lon\":\"12.802769\",    \"lat\":\"57.552411\",    \"type\":\"ADR\",    \"idx\":\"14\"    },{    \"name\":\"Kungabergsbadet, Fritsla\",    \"lon\":\"12.792477\",    \"lat\":\"57.551871\",    \"type\":\"POI\",    \"idx\":\"15\"    },{    \"name\":\"Ramilio Pizzeria, Fritsla\",    \"lon\":\"12.784162\",    \"lat\":\"57.556546\",    \"type\":\"POI\",    \"idx\":\"16\"    },{    \"name\":\"Alstervägen, 511 71 Fritsla\",    \"lon\":\"12.779946\",    \"lat\":\"57.551314\",    \"type\":\"ADR\",    \"idx\":\"17\"    },{    \"name\":\"Ängsätravägen, 511 71 Fritsla\",    \"lon\":\"12.777968\",    \"lat\":\"57.551341\",    \"type\":\"ADR\",    \"idx\":\"18\"    },{    \"name\":\"Ängsbovägen, 511 72 Fritsla\",    \"lon\":\"12.778274\",    \"lat\":\"57.553319\",    \"type\":\"ADR\",    \"idx\":\"19\"    },{    \"name\":\"Annehultsvägen, 511 71 Fritsla\",    \"lon\":\"12.786607\",    \"lat\":\"57.546244\",    \"type\":\"ADR\",    \"idx\":\"20\"    },{    \"name\":\"Aplakullsvägen, 511 71 Fritsla\",    \"lon\":\"12.787020\",    \"lat\":\"57.550999\",    \"type\":\"ADR\",    \"idx\":\"21\"    },{    \"name\":\"Aratorpsvägen, 511 71 Fritsla\",    \"lon\":\"12.788620\",    \"lat\":\"57.553112\",    \"type\":\"ADR\",    \"idx\":\"22\"    },{    \"name\":\"Arlavägen, 511 72 Fritsla\",    \"lon\":\"12.793322\",    \"lat\":\"57.562425\",    \"type\":\"ADR\",    \"idx\":\"23\"    },{    \"name\":\"Bäckabovägen, 511 71 Fritsla\",    \"lon\":\"12.796683\",    \"lat\":\"57.549309\",    \"type\":\"ADR\",    \"idx\":\"24\"    },{    \"name\":\"Basterås Gård, 511 72 Fritsla\",    \"lon\":\"12.747926\",    \"lat\":\"57.551709\",    \"type\":\"ADR\",    \"idx\":\"25\"    },{    \"name\":\"Basteråsvägen, 511 72 Fritsla\",    \"lon\":\"12.770795\",    \"lat\":\"57.552968\",    \"type\":\"ADR\",    \"idx\":\"26\"    },{    \"name\":\"Bastukärrsvägen, 511 73 Fritsla\",    \"lon\":\"12.762983\",    \"lat\":\"57.560285\",    \"type\":\"ADR\",    \"idx\":\"27\"    },{    \"name\":\"Bergagårdsvägen, 511 72 Fritsla\",    \"lon\":\"12.778157\",    \"lat\":\"57.559368\",    \"type\":\"ADR\",    \"idx\":\"28\"    },{    \"name\":\"Berghällsvägen, 511 72 Fritsla\",    \"lon\":\"12.781411\",    \"lat\":\"57.561652\",    \"type\":\"ADR\",    \"idx\":\"29\"    },{    \"name\":\"Bergklintsvägen, 511 72 Fritsla\",    \"lon\":\"12.775811\",    \"lat\":\"57.556051\",    \"type\":\"ADR\",    \"idx\":\"30\"    },{    \"name\":\"Bjälbovägen, 511 71 Fritsla\",    \"lon\":\"12.787604\",    \"lat\":\"57.556438\",    \"type\":\"ADR\",    \"idx\":\"31\"    },{    \"name\":\"Björsvägen, 511 71 Fritsla\",    \"lon\":\"12.784018\",    \"lat\":\"57.553894\",    \"type\":\"ADR\",    \"idx\":\"32\"    },{    \"name\":\"Blixtavägen, 511 72 Fritsla\",    \"lon\":\"12.796692\",    \"lat\":\"57.566209\",    \"type\":\"ADR\",    \"idx\":\"33\"    },{    \"name\":\"Blåklintsvägen, 511 72 Fritsla\",    \"lon\":\"12.780224\",    \"lat\":\"57.554892\",    \"type\":\"ADR\",    \"idx\":\"34\"    },{    \"name\":\"Borrarevägen, 511 72 Fritsla\",    \"lon\":\"12.776449\",    \"lat\":\"57.552321\",    \"type\":\"ADR\",    \"idx\":\"35\"    },{    \"name\":\"Brattehallsvägen, 511 71 Fritsla\",    \"lon\":\"12.800810\",    \"lat\":\"57.554541\",    \"type\":\"ADR\",    \"idx\":\"36\"    },{    \"name\":\"Danska Vägen, 511 71 Fritsla\",    \"lon\":\"12.798113\",    \"lat\":\"57.555242\",    \"type\":\"ADR\",    \"idx\":\"37\"    },{    \"name\":\"Edeslättsvägen, 511 73 Fritsla\",    \"lon\":\"12.764331\",    \"lat\":\"57.556168\",    \"type\":\"ADR\",    \"idx\":\"38\"    },{    \"name\":\"Ekhamravägen, 511 71 Fritsla\",    \"lon\":\"12.779604\",    \"lat\":\"57.552150\",    \"type\":\"ADR\",    \"idx\":\"39\"    },{    \"name\":\"Emaljvägen, 511 71 Fritsla\",    \"lon\":\"12.792207\",    \"lat\":\"57.550972\",    \"type\":\"ADR\",    \"idx\":\"40\"    },{    \"name\":\"Gärdebovägen, 511 71 Fritsla\",    \"lon\":\"12.784944\",    \"lat\":\"57.554217\",    \"type\":\"ADR\",    \"idx\":\"41\"    },{    \"name\":\"Gelbgjutarevägen, 511 72 Fritsla\",    \"lon\":\"12.794481\",    \"lat\":\"57.562622\",    \"type\":\"ADR\",    \"idx\":\"42\"    },{    \"name\":\"Getakärrsvägen, 511 72 Fritsla\",    \"lon\":\"12.792575\",    \"lat\":\"57.563728\",    \"type\":\"ADR\",    \"idx\":\"43\"    },{    \"name\":\"Glasbergsvägen, 511 73 Fritsla\",    \"lon\":\"12.774444\",    \"lat\":\"57.568115\",    \"type\":\"ADR\",    \"idx\":\"44\"    },{    \"name\":\"Gårdaslättsvägen, 511 73 Fritsla\",    \"lon\":\"12.773258\",    \"lat\":\"57.545660\",    \"type\":\"ADR\",    \"idx\":\"45\"    },{    \"name\":\"Gåsörtsvägen, 511 72 Fritsla\",    \"lon\":\"12.778543\",    \"lat\":\"57.556923\",    \"type\":\"ADR\",    \"idx\":\"46\"    },{    \"name\":\"Hagalundsvägen, 511 72 Fritsla\",    \"lon\":\"12.788899\",    \"lat\":\"57.560807\",    \"type\":\"ADR\",    \"idx\":\"47\"    },{    \"name\":\"Hägnevägen, 511 72 Fritsla\",    \"lon\":\"12.785600\",    \"lat\":\"57.566775\",    \"type\":\"ADR\",    \"idx\":\"48\"    },{    \"name\":\"Hallagärdesvägen, 511 73 Fritsla\",    \"lon\":\"12.772098\",    \"lat\":\"57.564492\",    \"type\":\"ADR\",    \"idx\":\"49\"    },{    \"name\":\"Hallsvägen, 511 71 Fritsla\",    \"lon\":\"12.787730\",    \"lat\":\"57.543269\",    \"type\":\"ADR\",    \"idx\":\"50\"    }]  }}";

		JSONObject jsonData = new JSONObject(response);
		
//		Set<String> keys = jsonData.getJSONObject("LocationList").keySet();
//		keys.remove("serverdate");
//		keys.remove("servertime");
//		keys.remove("noNamespaceSchemaLocation");
		
		ArrayList<Location> locationList = new ArrayList<Location>();

		Iterator<String> keysIter = jsonData.getJSONObject("LocationList").keys();
		while (keysIter.hasNext())
		{
			key = keysIter.next();

			if(key.equals("serverdate") || key.equals("servertime") || key.equals("noNamespaceSchemaLocation"))
				continue;
//		}
		
//		for(String key : keys)
//		{
			Object locations = jsonData.getJSONObject("LocationList").get(key);
			
			if (locations instanceof JSONArray)
			{
				JSONArray stopLocArr = (JSONArray) locations;
				
				for (int i = 0; i < stopLocArr.length(); i++)
				{
					Location loc = getJSONLocation((JSONObject) stopLocArr.get(i));

					locationList.add(loc);
				}
			}
			else
			{
				Location loc = getJSONLocation((JSONObject) locations);

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
//		
//		System.out.println(response);
		
//		String response = "{\"TripList\":{  \"noNamespaceSchemaLocation\":\"http://api.vasttrafik.se/v1/hafasRestTrip.xsd\",  \"servertime\":\"22:44\",  \"serverdate\":\"2015-10-04\",  \"Trip\":[{    \"Leg\":[{      \"name\":\"Gå\",      \"type\":\"WALK\",      \"Origin\":{        \"name\":\"0\",        \"type\":\"ADR\",        \"time\":\"05:15\",        \"date\":\"2015-10-05\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Fritsla station, Mark\",        \"type\":\"ST\",        \"id\":\"9022014063209002\",        \"time\":\"05:16\",        \"date\":\"2015-10-05\",        \"track\":\"B\",        \"$\":\"\n\"        }      },{      \"name\":\"Buss 450\",      \"sname\":\"450\",      \"type\":\"BUS\",      \"id\":\"9015014245000001\",      \"direction\":\"Borås\",      \"fgColor\":\"#00A5DC\",      \"bgColor\":\"#ffffff\",      \"stroke\":\"Solid\",      \"Origin\":{        \"name\":\"Fritsla station, Mark\",        \"type\":\"ST\",        \"id\":\"9022014063209002\",        \"routeIdx\":\"22\",        \"time\":\"05:16\",        \"date\":\"2015-10-05\",        \"track\":\"B\",        \"rtTime\":\"05:16\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Borås resecentrum, Borås\",        \"type\":\"ST\",        \"id\":\"9022014082017001\",        \"routeIdx\":\"36\",        \"time\":\"05:43\",        \"date\":\"2015-10-05\",        \"track\":\"F\",        \"rtTime\":\"05:43\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"JourneyDetailRef\":{        \"ref\":\"http://api.vasttrafik.se/bin/rest.exe/v1/journeyDetail?ref=558702%2F189784%2F45480%2F163494%2F80%3Fdate%3D2015-10-05%26station_evaId%3D63209002%26station_type%3Ddep%26authKey%3Da0af190e-e343-4b0d-a1dc-6fdbff217050%26format%3Djson%26\"        }      },{      \"name\":\"Gå\",      \"type\":\"WALK\",      \"Origin\":{        \"name\":\"Borås resecentrum, Borås\",        \"type\":\"ST\",        \"id\":\"9022014082017001\",        \"time\":\"05:48\",        \"date\":\"2015-10-05\",        \"track\":\"F\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Borås resecentrum, Borås\",        \"type\":\"ST\",        \"id\":\"9022014082017014\",        \"time\":\"05:48\",        \"date\":\"2015-10-05\",        \"track\":\"N\",        \"$\":\"\n\"        }      },{      \"name\":\"Buss 100\",      \"sname\":\"100\",      \"type\":\"BUS\",      \"id\":\"9015014210000006\",      \"direction\":\"Göteborg\",      \"fgColor\":\"#00A5DC\",      \"bgColor\":\"#ffffff\",      \"stroke\":\"Solid\",      \"Origin\":{        \"name\":\"Borås resecentrum, Borås\",        \"type\":\"ST\",        \"id\":\"9022014082017014\",        \"routeIdx\":\"0\",        \"time\":\"05:50\",        \"date\":\"2015-10-05\",        \"track\":\"N\",        \"rtTime\":\"05:50\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Korsvägen, Göteborg\",        \"type\":\"ST\",        \"id\":\"9022014003980007\",        \"routeIdx\":\"2\",        \"time\":\"06:37\",        \"date\":\"2015-10-05\",        \"track\":\"G\",        \"rtTime\":\"06:37\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"JourneyDetailRef\":{        \"ref\":\"http://api.vasttrafik.se/bin/rest.exe/v1/journeyDetail?ref=495510%2F169279%2F669248%2F169454%2F80%3Fdate%3D2015-10-05%26station_evaId%3D82017014%26station_type%3Ddep%26authKey%3Da0af190e-e343-4b0d-a1dc-6fdbff217050%26format%3Djson%26\"        }      },{      \"name\":\"Gå\",      \"type\":\"WALK\",      \"Origin\":{        \"name\":\"Korsvägen, Göteborg\",        \"type\":\"ST\",        \"id\":\"9022014003980007\",        \"time\":\"06:42\",        \"date\":\"2015-10-05\",        \"track\":\"G\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Korsvägen, Göteborg\",        \"type\":\"ST\",        \"id\":\"9022014003980005\",        \"time\":\"06:42\",        \"date\":\"2015-10-05\",        \"track\":\"E\",        \"$\":\"\n\"        }      },{      \"name\":\"Spårvagn 2\",      \"sname\":\"2\",      \"type\":\"TRAM\",      \"id\":\"9015014500200012\",      \"direction\":\"Högsbotorp\",      \"fgColor\":\"#fff014\",      \"bgColor\":\"#00abe5\",      \"stroke\":\"Solid\",      \"Origin\":{        \"name\":\"Korsvägen, Göteborg\",        \"type\":\"ST\",        \"id\":\"9022014003980005\",        \"routeIdx\":\"10\",        \"time\":\"06:43\",        \"date\":\"2015-10-05\",        \"track\":\"E\",        \"rtTime\":\"06:43\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Centralstationen, Göteborg\",        \"type\":\"ST\",        \"id\":\"9022014001950003\",        \"routeIdx\":\"13\",        \"time\":\"06:49\",        \"date\":\"2015-10-05\",        \"track\":\"C\",        \"rtTime\":\"06:49\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"JourneyDetailRef\":{        \"ref\":\"http://api.vasttrafik.se/bin/rest.exe/v1/journeyDetail?ref=751386%2F264142%2F465450%2F17737%2F80%3Fdate%3D2015-10-05%26station_evaId%3D3980005%26station_type%3Ddep%26authKey%3Da0af190e-e343-4b0d-a1dc-6fdbff217050%26format%3Djson%26\"        }      },{      \"name\":\"Gå\",      \"type\":\"WALK\",      \"Origin\":{        \"name\":\"Centralstationen, Göteborg\",        \"type\":\"ST\",        \"id\":\"9022014001950003\",        \"time\":\"06:49\",        \"date\":\"2015-10-05\",        \"track\":\"C\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"0\",        \"type\":\"ADR\",        \"time\":\"06:51\",        \"date\":\"2015-10-05\",        \"$\":\"\n\"        }      }]    },{    \"Leg\":[{      \"name\":\"Gå\",      \"type\":\"WALK\",      \"Origin\":{        \"name\":\"0\",        \"type\":\"ADR\",        \"time\":\"05:15\",        \"date\":\"2015-10-05\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Fritsla station, Mark\",        \"type\":\"ST\",        \"id\":\"9022014063209002\",        \"time\":\"05:16\",        \"date\":\"2015-10-05\",        \"track\":\"B\",        \"$\":\"\n\"        }      },{      \"name\":\"Buss 450\",      \"sname\":\"450\",      \"type\":\"BUS\",      \"id\":\"9015014245000001\",      \"direction\":\"Borås\",      \"fgColor\":\"#00A5DC\",      \"bgColor\":\"#ffffff\",      \"stroke\":\"Solid\",      \"Origin\":{        \"name\":\"Fritsla station, Mark\",        \"type\":\"ST\",        \"id\":\"9022014063209002\",        \"routeIdx\":\"22\",        \"time\":\"05:16\",        \"date\":\"2015-10-05\",        \"track\":\"B\",        \"rtTime\":\"05:16\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Borås resecentrum, Borås\",        \"type\":\"ST\",        \"id\":\"9022014082017001\",        \"routeIdx\":\"36\",        \"time\":\"05:43\",        \"date\":\"2015-10-05\",        \"track\":\"F\",        \"rtTime\":\"05:43\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"JourneyDetailRef\":{        \"ref\":\"http://api.vasttrafik.se/bin/rest.exe/v1/journeyDetail?ref=365052%2F125234%2F74524%2F84422%2F80%3Fdate%3D2015-10-05%26station_evaId%3D63209002%26station_type%3Ddep%26authKey%3Da0af190e-e343-4b0d-a1dc-6fdbff217050%26format%3Djson%26\"        }      },{      \"name\":\"Gå\",      \"type\":\"WALK\",      \"Origin\":{        \"name\":\"Borås resecentrum, Borås\",        \"type\":\"ST\",        \"id\":\"9022014082017001\",        \"time\":\"05:48\",        \"date\":\"2015-10-05\",        \"track\":\"F\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Borås resecentrum, Borås\",        \"type\":\"ST\",        \"id\":\"9022014082017014\",        \"time\":\"05:48\",        \"date\":\"2015-10-05\",        \"track\":\"N\",        \"$\":\"\n\"        }      },{      \"name\":\"Buss 100\",      \"sname\":\"100\",      \"type\":\"BUS\",      \"id\":\"9015014210000006\",      \"direction\":\"Göteborg\",      \"fgColor\":\"#00A5DC\",      \"bgColor\":\"#ffffff\",      \"stroke\":\"Solid\",      \"Origin\":{        \"name\":\"Borås resecentrum, Borås\",        \"type\":\"ST\",        \"id\":\"9022014082017014\",        \"routeIdx\":\"0\",        \"time\":\"05:50\",        \"date\":\"2015-10-05\",        \"track\":\"N\",        \"rtTime\":\"05:50\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Nils Ericson terminalen, Göteborg\",        \"type\":\"ST\",        \"id\":\"9022014004940018\",        \"routeIdx\":\"3\",        \"time\":\"06:50\",        \"date\":\"2015-10-05\",        \"rtTime\":\"06:50\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"JourneyDetailRef\":{        \"ref\":\"http://api.vasttrafik.se/bin/rest.exe/v1/journeyDetail?ref=46518%2F19615%2F452322%2F210655%2F80%3Fdate%3D2015-10-05%26station_evaId%3D82017014%26station_type%3Ddep%26authKey%3Da0af190e-e343-4b0d-a1dc-6fdbff217050%26format%3Djson%26\"        }      },{      \"name\":\"Gå\",      \"type\":\"WALK\",      \"Origin\":{        \"name\":\"Nils Ericson terminalen, Göteborg\",        \"type\":\"ST\",        \"id\":\"9022014004940018\",        \"time\":\"06:50\",        \"date\":\"2015-10-05\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"0\",        \"type\":\"ADR\",        \"time\":\"06:54\",        \"date\":\"2015-10-05\",        \"$\":\"\n\"        }      }]    },{    \"Leg\":[{      \"name\":\"Gå\",      \"type\":\"WALK\",      \"Origin\":{        \"name\":\"0\",        \"type\":\"ADR\",        \"time\":\"05:50\",        \"date\":\"2015-10-05\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Fritsla station, Mark\",        \"type\":\"ST\",        \"id\":\"9022014063209002\",        \"time\":\"05:51\",        \"date\":\"2015-10-05\",        \"track\":\"B\",        \"$\":\"\n\"        }      },{      \"name\":\"Buss 450\",      \"sname\":\"450\",      \"type\":\"BUS\",      \"id\":\"9015014245000003\",      \"direction\":\"Borås\",      \"fgColor\":\"#00A5DC\",      \"bgColor\":\"#ffffff\",      \"stroke\":\"Solid\",      \"Origin\":{        \"name\":\"Fritsla station, Mark\",        \"type\":\"ST\",        \"id\":\"9022014063209002\",        \"routeIdx\":\"22\",        \"time\":\"05:51\",        \"date\":\"2015-10-05\",        \"track\":\"B\",        \"rtTime\":\"05:51\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Borås resecentrum, Borås\",        \"type\":\"ST\",        \"id\":\"9022014082017001\",        \"routeIdx\":\"36\",        \"time\":\"06:18\",        \"date\":\"2015-10-05\",        \"track\":\"F\",        \"rtTime\":\"06:18\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"JourneyDetailRef\":{        \"ref\":\"http://api.vasttrafik.se/bin/rest.exe/v1/journeyDetail?ref=358599%2F123084%2F470758%2F115846%2F80%3Fdate%3D2015-10-05%26station_evaId%3D63209002%26station_type%3Ddep%26authKey%3Da0af190e-e343-4b0d-a1dc-6fdbff217050%26format%3Djson%26\"        }      },{      \"name\":\"Gå\",      \"type\":\"WALK\",      \"Origin\":{        \"name\":\"Borås resecentrum, Borås\",        \"type\":\"ST\",        \"id\":\"9022014082017001\",        \"time\":\"06:23\",        \"date\":\"2015-10-05\",        \"track\":\"F\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Borås resecentrum, Borås\",        \"type\":\"ST\",        \"id\":\"9022014082017014\",        \"time\":\"06:23\",        \"date\":\"2015-10-05\",        \"track\":\"N\",        \"$\":\"\n\"        }      },{      \"name\":\"Buss 100\",      \"sname\":\"100\",      \"type\":\"BUS\",      \"id\":\"9015014210000912\",      \"direction\":\"Göteborg\",      \"fgColor\":\"#00A5DC\",      \"bgColor\":\"#ffffff\",      \"stroke\":\"Solid\",      \"Origin\":{        \"name\":\"Borås resecentrum, Borås\",        \"type\":\"ST\",        \"id\":\"9022014082017014\",        \"routeIdx\":\"0\",        \"time\":\"06:25\",        \"date\":\"2015-10-05\",        \"track\":\"N\",        \"rtTime\":\"06:25\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Korsvägen, Göteborg\",        \"type\":\"ST\",        \"id\":\"9022014003980007\",        \"routeIdx\":\"2\",        \"time\":\"07:12\",        \"date\":\"2015-10-05\",        \"track\":\"G\",        \"rtTime\":\"07:12\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"JourneyDetailRef\":{        \"ref\":\"http://api.vasttrafik.se/bin/rest.exe/v1/journeyDetail?ref=570309%2F194233%2F294126%2F43040%2F80%3Fdate%3D2015-10-05%26station_evaId%3D82017014%26station_type%3Ddep%26authKey%3Da0af190e-e343-4b0d-a1dc-6fdbff217050%26format%3Djson%26\"        }      },{      \"name\":\"Gå\",      \"type\":\"WALK\",      \"Origin\":{        \"name\":\"Korsvägen, Göteborg\",        \"type\":\"ST\",        \"id\":\"9022014003980007\",        \"time\":\"07:17\",        \"date\":\"2015-10-05\",        \"track\":\"G\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Korsvägen, Göteborg\",        \"type\":\"ST\",        \"id\":\"9022014003980005\",        \"time\":\"07:17\",        \"date\":\"2015-10-05\",        \"track\":\"E\",        \"$\":\"\n\"        }      },{      \"name\":\"Spårvagn 2\",      \"sname\":\"2\",      \"type\":\"TRAM\",      \"id\":\"9015014500200024\",      \"direction\":\"Högsbotorp\",      \"fgColor\":\"#fff014\",      \"bgColor\":\"#00abe5\",      \"stroke\":\"Solid\",      \"Origin\":{        \"name\":\"Korsvägen, Göteborg\",        \"type\":\"ST\",        \"id\":\"9022014003980005\",        \"routeIdx\":\"10\",        \"time\":\"07:17\",        \"date\":\"2015-10-05\",        \"track\":\"E\",        \"rtTime\":\"07:17\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Centralstationen, Göteborg\",        \"type\":\"ST\",        \"id\":\"9022014001950003\",        \"routeIdx\":\"13\",        \"time\":\"07:24\",        \"date\":\"2015-10-05\",        \"track\":\"C\",        \"rtTime\":\"07:24\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"JourneyDetailRef\":{        \"ref\":\"http://api.vasttrafik.se/bin/rest.exe/v1/journeyDetail?ref=117156%2F52741%2F467762%2F194829%2F80%3Fdate%3D2015-10-05%26station_evaId%3D3980005%26station_type%3Ddep%26authKey%3Da0af190e-e343-4b0d-a1dc-6fdbff217050%26format%3Djson%26\"        }      },{      \"name\":\"Gå\",      \"type\":\"WALK\",      \"Origin\":{        \"name\":\"Centralstationen, Göteborg\",        \"type\":\"ST\",        \"id\":\"9022014001950003\",        \"time\":\"07:24\",        \"date\":\"2015-10-05\",        \"track\":\"C\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"0\",        \"type\":\"ADR\",        \"time\":\"07:26\",        \"date\":\"2015-10-05\",        \"$\":\"\n\"        }      }]    },{    \"Leg\":[{      \"name\":\"Gå\",      \"type\":\"WALK\",      \"Origin\":{        \"name\":\"0\",        \"type\":\"ADR\",        \"time\":\"05:50\",        \"date\":\"2015-10-05\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Fritsla station, Mark\",        \"type\":\"ST\",        \"id\":\"9022014063209002\",        \"time\":\"05:51\",        \"date\":\"2015-10-05\",        \"track\":\"B\",        \"$\":\"\n\"        }      },{      \"name\":\"Buss 450\",      \"sname\":\"450\",      \"type\":\"BUS\",      \"id\":\"9015014245000003\",      \"direction\":\"Borås\",      \"fgColor\":\"#00A5DC\",      \"bgColor\":\"#ffffff\",      \"stroke\":\"Solid\",      \"Origin\":{        \"name\":\"Fritsla station, Mark\",        \"type\":\"ST\",        \"id\":\"9022014063209002\",        \"routeIdx\":\"22\",        \"time\":\"05:51\",        \"date\":\"2015-10-05\",        \"track\":\"B\",        \"rtTime\":\"05:51\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Borås resecentrum, Borås\",        \"type\":\"ST\",        \"id\":\"9022014082017001\",        \"routeIdx\":\"36\",        \"time\":\"06:18\",        \"date\":\"2015-10-05\",        \"track\":\"F\",        \"rtTime\":\"06:18\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"JourneyDetailRef\":{        \"ref\":\"http://api.vasttrafik.se/bin/rest.exe/v1/journeyDetail?ref=76029%2F28894%2F354952%2F152133%2F80%3Fdate%3D2015-10-05%26station_evaId%3D63209002%26station_type%3Ddep%26authKey%3Da0af190e-e343-4b0d-a1dc-6fdbff217050%26format%3Djson%26\"        }      },{      \"name\":\"Gå\",      \"type\":\"WALK\",      \"Origin\":{        \"name\":\"Borås resecentrum, Borås\",        \"type\":\"ST\",        \"id\":\"9022014082017001\",        \"time\":\"06:23\",        \"date\":\"2015-10-05\",        \"track\":\"F\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Borås resecentrum, Borås\",        \"type\":\"ST\",        \"id\":\"9022014082017014\",        \"time\":\"06:23\",        \"date\":\"2015-10-05\",        \"track\":\"N\",        \"$\":\"\n\"        }      },{      \"name\":\"Buss 100\",      \"sname\":\"100\",      \"type\":\"BUS\",      \"id\":\"9015014210000912\",      \"direction\":\"Göteborg\",      \"fgColor\":\"#00A5DC\",      \"bgColor\":\"#ffffff\",      \"stroke\":\"Solid\",      \"Origin\":{        \"name\":\"Borås resecentrum, Borås\",        \"type\":\"ST\",        \"id\":\"9022014082017014\",        \"routeIdx\":\"0\",        \"time\":\"06:25\",        \"date\":\"2015-10-05\",        \"track\":\"N\",        \"rtTime\":\"06:25\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Nils Ericson terminalen, Göteborg\",        \"type\":\"ST\",        \"id\":\"9022014004940018\",        \"routeIdx\":\"3\",        \"time\":\"07:25\",        \"date\":\"2015-10-05\",        \"rtTime\":\"07:25\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"JourneyDetailRef\":{        \"ref\":\"http://api.vasttrafik.se/bin/rest.exe/v1/journeyDetail?ref=968661%2F327017%2F93256%2F276259%2F80%3Fdate%3D2015-10-05%26station_evaId%3D82017014%26station_type%3Ddep%26authKey%3Da0af190e-e343-4b0d-a1dc-6fdbff217050%26format%3Djson%26\"        }      },{      \"name\":\"Gå\",      \"type\":\"WALK\",      \"Origin\":{        \"name\":\"Nils Ericson terminalen, Göteborg\",        \"type\":\"ST\",        \"id\":\"9022014004940018\",        \"time\":\"07:25\",        \"date\":\"2015-10-05\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"0\",        \"type\":\"ADR\",        \"time\":\"07:29\",        \"date\":\"2015-10-05\",        \"$\":\"\n\"        }      }]    },{    \"Leg\":[{      \"name\":\"Gå\",      \"type\":\"WALK\",      \"Origin\":{        \"name\":\"0\",        \"type\":\"ADR\",        \"time\":\"06:13\",        \"date\":\"2015-10-05\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Fritsla station, Mark\",        \"type\":\"ST\",        \"id\":\"9022014063209003\",        \"time\":\"06:14\",        \"date\":\"2015-10-05\",        \"track\":\"C\",        \"$\":\"\n\"        }      },{      \"name\":\"Buss 450\",      \"sname\":\"450\",      \"type\":\"BUS\",      \"id\":\"9015014245000002\",      \"direction\":\"Skene\",      \"fgColor\":\"#00A5DC\",      \"bgColor\":\"#ffffff\",      \"stroke\":\"Solid\",      \"Origin\":{        \"name\":\"Fritsla station, Mark\",        \"type\":\"ST\",        \"id\":\"9022014063209003\",        \"routeIdx\":\"14\",        \"time\":\"06:14\",        \"date\":\"2015-10-05\",        \"track\":\"C\",        \"rtTime\":\"06:14\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Kinna resecentrum, Mark\",        \"type\":\"ST\",        \"id\":\"9022014063017003\",        \"routeIdx\":\"30\",        \"time\":\"06:30\",        \"date\":\"2015-10-05\",        \"track\":\"C\",        \"rtTime\":\"06:30\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"JourneyDetailRef\":{        \"ref\":\"http://api.vasttrafik.se/bin/rest.exe/v1/journeyDetail?ref=294378%2F114802%2F902140%2F352944%2F80%3Fdate%3D2015-10-05%26station_evaId%3D63209003%26station_type%3Ddep%26authKey%3Da0af190e-e343-4b0d-a1dc-6fdbff217050%26format%3Djson%26\"        }      },{      \"name\":\"Gå\",      \"type\":\"WALK\",      \"Origin\":{        \"name\":\"Kinna resecentrum, Mark\",        \"type\":\"ST\",        \"id\":\"9022014063017003\",        \"time\":\"06:35\",        \"date\":\"2015-10-05\",        \"track\":\"C\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Kinna resecentrum, Mark\",        \"type\":\"ST\",        \"id\":\"9022014063017002\",        \"time\":\"06:35\",        \"date\":\"2015-10-05\",        \"track\":\"D\",        \"$\":\"\n\"        }      },{      \"name\":\"Expbuss 300\",      \"sname\":\"300\",      \"type\":\"BUS\",      \"id\":\"9015014230000007\",      \"direction\":\"Göteborg\",      \"fgColor\":\"#00A5DC\",      \"bgColor\":\"#ffffff\",      \"stroke\":\"Solid\",      \"Origin\":{        \"name\":\"Kinna resecentrum, Mark\",        \"type\":\"ST\",        \"id\":\"9022014063017002\",        \"routeIdx\":\"0\",        \"time\":\"06:40\",        \"date\":\"2015-10-05\",        \"track\":\"D\",        \"rtTime\":\"06:40\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"Nils Ericson terminalen, Göteborg\",        \"type\":\"ST\",        \"id\":\"9022014004940018\",        \"routeIdx\":\"21\",        \"time\":\"07:50\",        \"date\":\"2015-10-05\",        \"rtTime\":\"07:50\",        \"rtDate\":\"2015-10-05\",        \"$\":\"\n\"        },      \"JourneyDetailRef\":{        \"ref\":\"http://api.vasttrafik.se/bin/rest.exe/v1/journeyDetail?ref=800832%2F269637%2F637156%2F51634%2F80%3Fdate%3D2015-10-05%26station_evaId%3D63017002%26station_type%3Ddep%26authKey%3Da0af190e-e343-4b0d-a1dc-6fdbff217050%26format%3Djson%26\"        }      },{      \"name\":\"Gå\",      \"type\":\"WALK\",      \"Origin\":{        \"name\":\"Nils Ericson terminalen, Göteborg\",        \"type\":\"ST\",        \"id\":\"9022014004940018\",        \"time\":\"07:50\",        \"date\":\"2015-10-05\",        \"$\":\"\n\"        },      \"Destination\":{        \"name\":\"0\",        \"type\":\"ADR\",        \"time\":\"07:54\",        \"date\":\"2015-10-05\",        \"$\":\"\n\"        }      }]    }]  }}";
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
				

				JSONObject legOrigin = legJSON.getJSONObject("Origin");
				Origin orig = new Origin();

				Iterator<String> legOriginIter = legOrigin.keys();
				while (legOriginIter.hasNext())
				{
					key = legOriginIter.next();
					orig.add(key, legOrigin.getString(key));
				}

//				for(String key : legOrigin.keys())
//				{
//					orig.add(key, legOrigin.getString(key));
//				}
				
				JSONObject legDestination = legJSON.getJSONObject("Destination");
				Destination dest = new Destination();

				Iterator<String> legDestinationIter = legDestination.keys();
				while (legDestinationIter.hasNext())
				{
					key = legDestinationIter.next();
					orig.add(key, legOrigin.getString(key));
				}

//				for(String key : legDestination.keySet())
//				{
//					dest.add(key, legDestination.getString(key));
//				}

				Leg leg = new Leg(orig, dest);
				
				trip.AddTrip(leg);
				
			}
			
			triplist.add(trip);
		}
		
		return triplist;
	}
	
	private static Location getJSONLocation(JSONObject location) throws JSONException
	{
		String name = location.getString("name");
		Coord coord = new Coord(location.getString("lat"),location.getString("lon"));
		
		return new Location(name, coord);
	}
	
	private static String getISO88591String(String input) throws UnsupportedEncodingException
	{
		return URLEncoder.encode(input, "ISO-8859-1");
	}
}