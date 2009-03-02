package exp;
import ewe.sys.*;
import ewe.io.FileBase;
import CacheWolf.*;
/**
*	Class to export the cache database to a GPX file with gc.com
*	extensions.<br>
*	Export of logs is not that nice. The cause is that CacheWolf does not spider
*	logs individually, rather all logs as a single entity.
*	ClassID = 2000
*/
public class GPXExporter extends Exporter{
	
	private final static String STRING_TRUE = "True";
	private final static String STRING_FALSE = "False";
	private final static String DEFAULT_DATE = "2000-01-01";
	
	public GPXExporter(){
		super();
		this.setMask("*.gpx");
		this.setNeedCacheDetails(true);
		this.setHowManyParams(LAT_LON);
		this.setTmpFileName(FileBase.getProgramDirectory() + "/temp.gpx");
	}
	
	public GPXExporter(Preferences p, Profile prof){
		super();
		this.setMask("*.gpx");
		this.setNeedCacheDetails(true);
		this.setHowManyParams(LAT_LON);
		this.setTmpFileName(FileBase.getProgramDirectory() + "/temp.gpx");
	}
	
	public String header() {
		StringBuffer strBuf = new StringBuffer(200);
		Time tim = new Time();
		
		strBuf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
		strBuf.append("<gpx xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"1.0\" creator=\"Groundspeak Pocket Query\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd http://www.groundspeak.com/cache/1/0 http://www.groundspeak.com/cache/1/0/cache.xsd\" xmlns=\"http://www.topografix.com/GPX/1/0\">\r\n");
		strBuf.append("  <desc>Geocache file generated by CacheWolf</desc>\r\n");
		strBuf.append("  <author>CacheWolf</author>\r\n");
		strBuf.append("  <email>test@test.com</email>\r\n");
		tim = tim.setFormat("yyyy-MM-dd");
		tim = tim.setToCurrentTime();
		strBuf.append(" <time>"+tim.toString()+"T00:00:00.0000000-07:00</time>\r\n");

		return strBuf.toString();
	}
	
	public String record(CacheHolderDetail ch, String lat, String lon) {
		StringBuffer strBuf = new StringBuffer(1000);

		try{
			strBuf.append("  <wpt lat=\""+lat+"\" lon=\""+lon+"\">\r\n");
		
			String tim = ch.DateHidden.length()>0 ? ch.DateHidden : DEFAULT_DATE;
			strBuf.append("    <time>").append(tim.toString()).append("T00:00:00.0000000-07:00</time>\r\n");
			strBuf.append("    <name>").append(ch.wayPoint).append("</name>\r\n");
			if (ch.isAddiWpt()){
				strBuf.append("    <cmt>").append(SafeXML.cleanGPX(ch.LongDescription)).append("</cmt>\r\n");
			}
			strBuf.append("    <desc>").append(SafeXML.cleanGPX(ch.CacheName)).append(" by ").append(SafeXML.cleanGPX(ch.CacheOwner)).append("</desc>\r\n");
			strBuf.append("    <url>http://www.geocaching.com/seek/cache_details.aspx?wp=").append(ch.wayPoint).append("&amp;Submit6=Find</url>\r\n");
			strBuf.append("    <urlname>").append(SafeXML.cleanGPX(ch.CacheName)).append(" by ").append(SafeXML.cleanGPX(ch.CacheOwner)).append("</urlname>\r\n");
			if (!ch.isAddiWpt()){
				if ( ch.is_found ) {
					strBuf.append("    <sym>Geocache Found</sym>\r\n");					
				} else {
					strBuf.append("    <sym>Geocache</sym>\r\n");
				}
				strBuf.append("    <type>Geocache|").append(CacheType.transType(ch.type)).append("</type>\r\n");
				String dummyAvailable = ch.is_available ? STRING_TRUE:STRING_FALSE;
				String dummyArchived = ch.is_archived ? STRING_TRUE:STRING_FALSE;
				strBuf.append("    <groundspeak:cache id=\"").append( ch.GetCacheID() ).append( "\" available=\"" ).append( dummyAvailable ).append( "\" archived=\"" ).append( dummyArchived).append( "\" xmlns:groundspeak=\"http://www.groundspeak.com/cache/1/0\">\r\n");
				strBuf.append("      <groundspeak:name>").append(SafeXML.cleanGPX(ch.CacheName)).append("</groundspeak:name>\r\n");
				strBuf.append("      <groundspeak:placed_by>").append(SafeXML.cleanGPX(ch.CacheOwner)).append("</groundspeak:placed_by>\r\n");
				strBuf.append("      <groundspeak:owner>").append(SafeXML.cleanGPX(ch.CacheOwner)+"</groundspeak:owner>\r\n");
				strBuf.append("      <groundspeak:type>").append(CacheType.transType(ch.type)).append("</groundspeak:type>\r\n");
				strBuf.append("      <groundspeak:container>").append(ch.CacheSize).append("</groundspeak:container>\r\n");
				//for Colorado/Oregon: 2.0 -> 2
				String diffTerr = ch.hard.replace(',','.');
				if ( diffTerr.endsWith( ".0" ) ) {
					diffTerr = diffTerr.substring(0, 1);
				}
				strBuf.append("      <groundspeak:difficulty>").append(diffTerr).append("</groundspeak:difficulty>\r\n");
				diffTerr = ch.terrain.replace(',','.');
				if ( diffTerr.endsWith( ".0" ) ) {
					diffTerr = diffTerr.substring(0, 1);
				}
				strBuf.append("      <groundspeak:terrain>").append(diffTerr).append("</groundspeak:terrain>\r\n");
				String dummyHTML = ch.is_HTML ? STRING_TRUE:STRING_FALSE;
				strBuf.append("      <groundspeak:long_description html=\"" ).append( dummyHTML ).append( "\">\r\n");
				strBuf.append("      ").append(SafeXML.cleanGPX(ch.LongDescription));
				strBuf.append("      \n</groundspeak:long_description>\r\n");
				strBuf.append("	  <groundspeak:encoded_hints>").append(SafeXML.cleanGPX(Common.rot13(ch.Hints))).append("</groundspeak:encoded_hints>\r\n");
				strBuf.append("      <groundspeak:logs>\r\n");
				strBuf.append("      </groundspeak:logs>\r\n");
				strBuf.append("      <groundspeak:travelbugs />\r\n");
				strBuf.append("    </groundspeak:cache>\r\n");
			}else {
				// there is no HTML in the description of addi wpts
				strBuf.append("    <sym>").append(CacheType.transType(ch.type)).append("</sym>\r\n");
				strBuf.append("    <type>Waypoint|").append(CacheType.transType(ch.type)).append("</type>\r\n");
			}
			strBuf.append("  </wpt>\r\n");
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}//try

		return strBuf.toString();
	}
	
	public String trailer() {
		return "</gpx>\r\n";
	}
	
}
