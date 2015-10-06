package se.grupp4.minbusskompis.api;

import java.util.Map;
import java.util.HashMap;

class UrlCallBuilder
{
	private HashMap<String,String> parameters;
	private String url;
	private boolean isBuilt;
	
	public UrlCallBuilder(String baseurl, String function)
	{
		parameters = new HashMap<String, String>();
		url = baseurl.replace("#", function);
		isBuilt = false;
	}
	
	public String getUrl()
	{
		if(isBuilt)
			return url;
		
		for(Map.Entry<String, String> s : parameters.entrySet())
		{
			url += "&" + s.getKey() + "=" + s.getValue();
		}
		
		isBuilt = true;
		
		return url;
	}
	
	public void AddParameter(String param, String value)
	{
		parameters.put(param, value);
	}
}