package se.grupp4.minbusskompis.api;

import java.util.Arrays;
import java.util.List;

/**
 * Contains all public data on all buses
 * incorporated in the Innovation Platform API
 */
public class BusData
{
	/**
	 * Bus class for data for individual buses.
	 * Contains dgw, VIN, reg and Mac.
	 */
	private static class Bus
	{
		public String dgw;
		public String VIN;
		public String reg;
		public String Mac;

		/**
		 * Bus constructor.
		 * @param dgw
		 * @param VIN
		 * @param reg
		 * @param Mac
		 */
		Bus(String dgw, String VIN, String reg, String Mac)
		{
			this.dgw = dgw;
			this.VIN = VIN;
			this.reg = reg;
			this.Mac = Mac;
		}
	}

	/**
	 * Returns the dgw string for the simulated bus in the IP API
	 * @return String
	 */
	public static String getSimDgw()
	{
		return BusSim.dgw;
	}

	/**
	 * Contains the busdata for the simulated bus.
	 */
	private static Bus BusSim = new Bus("Ericsson$Vin_Num_001","","","");

	/**
	 * Contains the busdata for all real buses.
	 * Electric (3) + Hybrid (7)
	 */
	private static List<Bus> busList = (List<Bus>) Arrays.asList(
			new Bus("Ericsson$100020","YV3U0V222FA100020","EPO 131","0013951349f5"),
			new Bus("Ericsson$100021","YV3U0V222FA100021","EPO 136","001395134bbe"), 
			new Bus("Ericsson$100022","YV3U0V222FA100022","EPO 143","001395143bf0"),
			new Bus("Ericsson$171164","YV3T1U22XF1171164","EOG 604","00139514698a"),
			new Bus("Ericsson$171234","YV3T1U225F1171234","EOG 606","0013951349f7"),
			new Bus("Ericsson$171235","YV3T1U227F1171235","EOG 616","0013950f92a4"),
			new Bus("Ericsson$171327","YV3T1U221F1171327","EOG 622","001395136296"),
			new Bus("Ericsson$171328","YV3T1U223F1171328","EOG 627","001395134bbc"),
			new Bus("Ericsson$171329","YV3T1U225F1171329","EOG 631","001395143bf2"),
			new Bus("Ericsson$171330","YV3T1U223F1171330","EOG 634","001395135f20"));

	/**
	 * Finds and return the dgw of the bus with corresponding mac
	 * @param mac Bus mac
	 * @return String
	 */
	public static String getDgwByMac(String mac)
	{
		for(Bus b : busList)
		{
			if(b.Mac.equals(mac))
				return b.dgw;
		}
		return "";
	}

	/**
	 * Finds and return the mac of the bus with corresponding dgw
	 * @param dgw Bus id
	 * @return String
	 */
	public static String getMacByDgw(String dgw)
	{
		for(Bus b : busList)
		{
			if(b.dgw.equals(dgw))
				return b.Mac;
		}
		return "";
	}
}