package se.grupp4.minbusskompis.api.datatypes.vt;

import java.util.HashMap;
import java.util.Map.Entry;

public class KeyValueBase
{
	private HashMap<String, String> kvb;
	
	public KeyValueBase()
	{
		kvb = new HashMap<String, String>();
	}
	
	public void add(String key, String value)
	{
		kvb.put(key, value);
	}
	
	public String getValue(String key)
	{
		return kvb.get(key);
	}
	
	public String toString()
	{
		String s = "";
		
		for(Entry<String, String> ks : kvb.entrySet())
		{
			s += ks.getKey() + ": " + ks.getValue() + ", ";			
		}
		
		return s.substring(0, s.length()-2);
	}
}