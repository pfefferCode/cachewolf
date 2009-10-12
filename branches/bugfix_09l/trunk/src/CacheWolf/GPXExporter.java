package CacheWolf;
import ewe.util.*;
import ewe.sys.*;
import ewe.io.*;
import ewe.filechooser.*;
import ewe.ui.*;
import ewe.util.*;
/**
*	Class to export the cache database to a GPX file with gc.com
*	extensions.<br>
*	Export of logs is not that nice. The cause is that CacheWolf does not spider
*	logs individually, rather all logs as a single entity.
*	ClassID = 2000
*/
public class GPXExporter{
	Vector cacheDB;
	Preferences myPreferences;
	ProgressBarForm pbf = new ProgressBarForm();
	Locale l = Vm.getLocale();
	LocalResource lr = l.getLocalResource("cachewolf.Languages",true);
	
	public GPXExporter(Vector db, Preferences pref){
		cacheDB = db;
		myPreferences = pref;
	}
	
	public void doIt(int variant){
		CacheHolder holder = new CacheHolder();
		String saveStr = new String();
		String latlonstr = new String();
		String cwd = new String();
		cwd = File.getProgramDirectory();
		File saveTo = new File(cwd + "/temp.gpx");
		if(variant == 1) {
			FileChooser fc = new FileChooser(FileChooser.SAVE, myPreferences.mydatadir);
			fc.setTitle("Select target file:");
			if(fc.execute() != fc.IDCANCEL) saveTo = fc.getChosenFile();
		}
		try{
			PrintWriter outp =  new PrintWriter(new BufferedWriter(new FileWriter(saveTo)));
			outp.print("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
			outp.print("<gpx xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"1.0\" creator=\"Groundspeak Pocket Query\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd http://www.groundspeak.com/cache/1/0 http://www.groundspeak.com/cache/1/0/cache.xsd\" xmlns=\"http://www.topografix.com/GPX/1/0\">\r\n");
			outp.print("  <desc>Geocache file generated by CacheWolf</desc>\r\n");
			outp.print("  <author>CacheWolf</author>\r\n");
			outp.print("  <email>test@test.com</email>\r\n");
			Time tim = new Time();
			tim = tim.setFormat("yyyy-MM-dd");
			tim = tim.setToCurrentTime();
			outp.print(" <time>"+tim.toString()+"T00:00:00.0000000-07:00</time>\r\n");
			CacheReaderWriter crw;
			ParseLatLon pll;
			String msg = new String();
			for(int i = 0; i<cacheDB.size(); i++){
				if (i%5 == 0){
					msg = "Export " + Convert.toString(i) + " of " + Convert.toString(cacheDB.size());
					pbf.display("GPX Export",msg,null);
				}
				holder = new CacheHolder();
				holder=(CacheHolder)cacheDB.get(i);
				if(holder.is_black == false && holder.is_filtered == false){
					//KHF read cachedata only if needed 
					crw = new CacheReaderWriter();
					crw.readCache(holder, myPreferences.mydatadir);
					pll = new ParseLatLon(holder.LatLon, ".");
					pll.parse();
					outp.print("  <wpt lat=\""+pll.getLatDeg()+"\" lon=\""+pll.getLonDeg()+"\">\r\n");
					tim.parse(holder.DateHidden, "M/d/y");
					outp.print("    <time>"+tim.toString()+"T00:00:00.0000000-07:00</time>\r\n");
					outp.print("    <name>"+holder.wayPoint+"</name>\r\n");
					outp.print("    <desc>"+SafeXML.cleanGPX(holder.CacheName)+" by "+SafeXML.cleanGPX(holder.CacheOwner)+"</desc>\r\n");
					outp.print("    <url>http://www.geocaching.com/seek/cache_details.aspx?wp="+holder.wayPoint+"&amp;Submit6=Find</url>\r\n");
					outp.print("    <urlname>"+SafeXML.cleanGPX(holder.CacheName)+" by "+SafeXML.cleanGPX(holder.CacheOwner)+"</urlname>\r\n");
					outp.print("    <sym>Geocache</sym>\r\n");
					outp.print("    <type>Geocache|"+Common.transType(holder.type)+"</type>\r\n");
					//outp.print("    <type>Geocache|Geocache</type>\r\n");
					String dummyAvailable = holder.is_available ? "True":"False";
					String dummyArchived = holder.is_archived ? "True":"False";
					outp.print("    <groundspeak:cache available=\""+ dummyAvailable + "\" archived=\"" + dummyArchived+ "\" xmlns:groundspeak=\"http://www.groundspeak.com/cache/1/0\">\r\n");
					outp.print("      <groundspeak:name>"+SafeXML.cleanGPX(holder.CacheName)+"</groundspeak:name>\r\n");
					outp.print("      <groundspeak:placed_by>"+SafeXML.cleanGPX(holder.CacheOwner)+"</groundspeak:placed_by>\r\n");
					outp.print("      <groundspeak:owner>"+SafeXML.cleanGPX(holder.CacheOwner)+"</groundspeak:owner>\r\n");
					outp.print("      <groundspeak:type>"+Common.transType(holder.type)+"</groundspeak:type>\r\n");
					outp.print("      <groundspeak:container>"+holder.CacheSize+"</groundspeak:container>\r\n");
					//KHF use '.' instead of ','
					outp.print("      <groundspeak:difficulty>"+holder.hard.replace(',','.')+"</groundspeak:difficulty>\r\n");
					outp.print("      <groundspeak:terrain>"+holder.terrain.replace(',','.')+"</groundspeak:terrain>\r\n");
					String dummyHTML = holder.is_HTML ? "True":"False";
					outp.print("      <groundspeak:long_description html=\"" + dummyHTML + "\">\r\n");
					outp.print("      "+SafeXML.cleanGPX(holder.LongDescription));
					outp.print("      \n</groundspeak:long_description>\r\n");
					outp.print("	  <groundspeak:encoded_hints>"+SafeXML.cleanGPX(Common.rot13(holder.Hints))+"</groundspeak:encoded_hints>\r\n");
					outp.print("      <groundspeak:logs>\r\n");
					/*
					for(int j = 0; j<holder.CacheLogs.size(); j++){
						outp.print("	    <groundspeak:log>\r\n");
						outp.print("            <groundspeak:date>");
						outp.print("T08:00:00</groundspeak:date>\r\n");
						outp.print("<groundspeak:finder>");
						outp.print("</groundspeak:finder>");
						outp.print("		<groundspeak:text encoded=\"False\">\r\n");
						outp.print("		</groundspeak:text>\r\n");
						outp.print("	    </groundspeak:log>\r\n");
					}
					*/
					outp.print("      </groundspeak:logs>\r\n");
					outp.print("      <groundspeak:travelbugs />\r\n");
					outp.print("    </groundspeak:cache>\r\n");
					outp.print("  </wpt>\r\n");
				} // if holder ==
			}//for
			pbf.clear();
			outp.print("</gpx>");
			outp.close();
		}catch(Exception e){
			Vm.debug("Problem writing to GPX file");
		}//try
	}
	
	
	
	/* Replace all instances of a String in a String.
		 *   @param  s  String to alter.
		 *   @param  f  String to look for.
		 *   @param  r  String to replace it with, or null to just remove it.
		 */ 
		private String replace( String s, String f, String r )
		{
		   if (s == null)  return s;
		   if (f == null)  return s;
		   if (r == null)  r = "";
		
		   int index01 = s.indexOf( f );
		   while (index01 != -1)
		   {
			  s = s.substring(0,index01) + r + s.substring(index01+f.length());
			  index01 += r.length();
			  index01 = s.indexOf( f, index01 );
		   }
		   return s;
		}
}