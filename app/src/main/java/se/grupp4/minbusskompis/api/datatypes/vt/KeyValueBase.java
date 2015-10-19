package se.grupp4.minbusskompis.api.datatypes.vt;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Base class for other classes with similar attributes.
 */
public class KeyValueBase
{
	/**
	 * Contains all key value pairs.
	 */
	private HashMap<String, String> kvb;

	/**
	 * Constructor
	 */
	public KeyValueBase()
	{
		kvb = new HashMap<String, String>();
	}

	/**
	 * Add a key value pair.
	 * @param key
	 * @param value
	 */
	public void add(String key, String value)
	{
		kvb.put(key, value);
	}

	/**
	 * Get value for "key"
	 * @param key
	 * @return String
	 */
	public String getValue(String key)
	{
		String value = kvb.get(key);

		return value == null ? "" : value;
	}

	/**
	 * Returns a string of all key value pairs in the object.
	 * @return String
	 */
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