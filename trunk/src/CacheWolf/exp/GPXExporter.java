package CacheWolf.exp;
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
		this.setHowManyParams(LAT_LON|COUNT);
		this.setTmpFileName(FileBase.getProgramDirectory() + "/temp.gpx");
	}

	public GPXExporter(Preferences p, Profile prof){
		super();
		this.setMask("*.gpx");
		this.setNeedCacheDetails(true);
		this.setHowManyParams(LAT_LON|COUNT);
		this.setTmpFileName(FileBase.getProgramDirectory() + "/temp.gpx");
	}

	public String header() {
		StringBuffer strBuf = new StringBuffer(200);
		Time tim = new Time();

		strBuf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
		strBuf.append("<gpx xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"1.0\" creator=\"Groundspeak Pocket Query\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd http://www.groundspeak.com/cache/1/0 http://www.groundspeak.com/cache/1/0/cache.xsd\" xmlns=\"http://www.topografix.com/GPX/1/0\">\r\n");
		if ( Global.getPref().exportGpxAsMyFinds ) {
			strBuf.append("  <name>My Finds Pocket Query</name>\r\n");
		}
		strBuf.append("  <desc>Geocache file generated by CacheWolf</desc>\r\n");
		strBuf.append("  <author>CacheWolf</author>\r\n");
		strBuf.append("  <email>test@test.com</email>\r\n");
		tim = tim.setFormat("yyyy-MM-dd'T'HH:mm:dd'Z'");
		tim = tim.setToCurrentTime();
		strBuf.append(" <time>"+tim.toString()+"</time>\r\n");

		return strBuf.toString();
	}

	public String record(CacheHolder ch, String lat, String lon, int counter) {
		StringBuffer strBuf = new StringBuffer(1000);
		CacheHolderDetail det = ch.getCacheDetails(true);
		try{
			strBuf.append("  <wpt lat=\""+lat+"\" lon=\""+lon+"\">\r\n");

			String tim = ch.getDateHidden().length()>0 ? ch.getDateHidden() : DEFAULT_DATE;
			strBuf.append("    <time>").append(tim.toString()).append("T07:00:00Z</time>\r\n");
			strBuf.append("    <name>").append(ch.getWayPoint()).append("</name>\r\n");
			if (ch.isAddiWpt()){
				strBuf.append("    <cmt>").append(SafeXML.cleanGPX(det.LongDescription)).append("</cmt>\r\n");
			}
			strBuf.append("    <desc>").append(SafeXML.cleanGPX(ch.getCacheName())).append(" by ").append(SafeXML.cleanGPX(ch.getCacheOwner())).append("</desc>\r\n");
			strBuf.append("    <url>http://www.geocaching.com/seek/cache_details.aspx?wp=").append(ch.getWayPoint()).append("&amp;Submit6=Find</url>\r\n");
			strBuf.append("    <urlname>").append(SafeXML.cleanGPX(ch.getCacheName())).append(" by ").append(SafeXML.cleanGPX(ch.getCacheOwner())).append("</urlname>\r\n");
			if (!ch.isAddiWpt()){
				if ( ch.is_found() ) {
					strBuf.append("    <sym>Geocache Found</sym>\r\n");
				} else {
					strBuf.append("    <sym>Geocache</sym>\r\n");
				}
				strBuf.append("    <type>").append(CacheType.type2TypeTag(ch.getType())).append("</type>\r\n");
				String dummyAvailable = ch.is_available() ? STRING_TRUE:STRING_FALSE;
				String dummyArchived = ch.is_archived() ? STRING_TRUE:STRING_FALSE;
				strBuf.append("    <groundspeak:cache id=\"").append( ch.GetCacheID() ).append( "\" available=\"" ).append( dummyAvailable ).append( "\" archived=\"" ).append( dummyArchived).append( "\" xmlns:groundspeak=\"http://www.groundspeak.com/cache/1/0\">\r\n");
				strBuf.append("      <groundspeak:name>").append(SafeXML.cleanGPX(ch.getCacheName())).append("</groundspeak:name>\r\n");
				strBuf.append("      <groundspeak:placed_by>").append(SafeXML.cleanGPX(ch.getCacheOwner())).append("</groundspeak:placed_by>\r\n");
				//todo low prio: correct owner-id
				strBuf.append("      <groundspeak:owner id=\"23\">").append(SafeXML.cleanGPX(ch.getCacheOwner())+"</groundspeak:owner>\r\n");
				strBuf.append("      <groundspeak:type>").append(CacheType.type2GSTypeTag(ch.getType())).append("</groundspeak:type>\r\n");
				strBuf.append("      <groundspeak:container>").append(CacheSize.cw2ExportString(ch.getCacheSize())).append("</groundspeak:container>\r\n");
				//for Colorado/Oregon: 2.0 -> 2
				String diffTerr = CacheTerrDiff.shortDT(ch.getHard());

				strBuf.append("      <groundspeak:difficulty>").append(diffTerr).append("</groundspeak:difficulty>\r\n");
				diffTerr = CacheTerrDiff.shortDT(ch.getTerrain());
				strBuf.append("      <groundspeak:terrain>").append(diffTerr).append("</groundspeak:terrain>\r\n");

				strBuf.append("      <groundspeak:country>").append(SafeXML.cleanGPX(det.Country)+"</groundspeak:country>\r\n");
				strBuf.append("      <groundspeak:state>").append(SafeXML.cleanGPX(det.State)+"</groundspeak:state>\r\n");

				String dummyHTML = ch.is_HTML() ? STRING_TRUE:STRING_FALSE;
				strBuf.append("      <groundspeak:long_description html=\"" ).append( dummyHTML ).append( "\">\r\n");
				strBuf.append("      ").append(SafeXML.cleanGPX(det.LongDescription));
				strBuf.append("      \n</groundspeak:long_description>\r\n");
				strBuf.append("	  <groundspeak:encoded_hints>").append(SafeXML.cleanGPX(Common.rot13(det.Hints))).append("</groundspeak:encoded_hints>\r\n");
				strBuf.append("      <groundspeak:logs>\r\n");
				if ( Global.getPref().exportGpxAsMyFinds && ch.is_found() ) {
					if ( det.OwnLogId.length() != 0 ) {
						strBuf.append("        <groundspeak:log id=\"" ).append( det.OwnLogId ).append( "\">\r\n");
					} else {
						strBuf.append("        <groundspeak:log id=\"" ).append( Integer.toString(counter) ).append( "\">\r\n");
					}
					strBuf.append("          <groundspeak:date>").append(SafeXML.cleanGPX(ch.getStatusDate())).append("T").append(SafeXML.cleanGPX(ch.getStatusTime())).append(":00</groundspeak:date>\r\n");
					if ( det.OwnLog != null ) {
						strBuf.append("          <groundspeak:type>").append(image2TypeText(det.OwnLog.getIcon())).append("</groundspeak:type>\r\n");
					} else {
						strBuf.append("          <groundspeak:type>Found it</groundspeak:type>\r\n");
					}
					strBuf.append("          <groundspeak:finder id=\"").append(SafeXML.cleanGPX(Global.getPref().gcMemberId)).append("\">").append(SafeXML.cleanGPX(Global.getPref().myAlias)).append("</groundspeak:finder>\r\n");
					if ( det.OwnLog != null ) {
						strBuf.append("          <groundspeak:text encoded=\"False\">").append(SafeXML.cleanGPX(det.OwnLog.getMessage())).append("</groundspeak:text>\r\n");
					} else {
						strBuf.append("          <groundspeak:text encoded=\"False\"></groundspeak:text>\r\n");
					}
					strBuf.append("        </groundspeak:log>\r\n");
				} else {
					int numberOfLogs = java.lang.Math.min(Global.getPref().numberOfLogsToExport, det.CacheLogs.size());
					if (numberOfLogs < 0) numberOfLogs = det.CacheLogs.size();
					for (int i = 0; i < numberOfLogs; i++) {
						strBuf.append("        <groundspeak:log id=\"" ).append( Integer.toString(i) ).append( "\">\r\n");
						strBuf.append("          <groundspeak:date>").append(SafeXML.cleanGPX(det.CacheLogs.getLog(i).getDate())).append("T00:00:00</groundspeak:date>\r\n");
						strBuf.append("          <groundspeak:type>").append(image2TypeText(det.CacheLogs.getLog(i).getIcon())).append("</groundspeak:type>\r\n");
						strBuf.append("          <groundspeak:finder id=\"\">").append(SafeXML.cleanGPX(det.CacheLogs.getLog(i).getLogger())).append("</groundspeak:finder>\r\n");
						strBuf.append("          <groundspeak:text encoded=\"False\">").append(SafeXML.cleanGPX(det.CacheLogs.getLog(i).getMessage())).append("</groundspeak:text>\r\n");
						strBuf.append("        </groundspeak:log>\r\n");
					}
				}
				strBuf.append("      </groundspeak:logs>\r\n");
				if ( Global.getPref().exportTravelbugs && (det.Travelbugs.size() > 0) ) {
					det.Travelbugs.size();
					strBuf.append("      <groundspeak:travelbugs>\r\n");
					for (int i = 0; i < det.Travelbugs.size(); i++) {
						strBuf.append("        <groundspeak:travelbug id=\"").append(Integer.toString(i)).append("\" ref=\"TB\">\r\n");
						strBuf.append("          <groundspeak:name>").append(SafeXML.cleanGPX(det.Travelbugs.getTB(i).getName())).append("</groundspeak:name>\r\n");
						strBuf.append("        </groundspeak:travelbug>\r\n");
					}
					strBuf.append("      </groundspeak:travelbugs>\r\n");
				} else {
					strBuf.append("      <groundspeak:travelbugs />\r\n");
				}
				strBuf.append("    </groundspeak:cache>\r\n");
			}else {
				// there is no HTML in the description of addi wpts
				strBuf.append("    <sym>").append(CacheType.type2SymTag(ch.getType())).append("</sym>\r\n");
				strBuf.append("    <type>").append(CacheType.type2TypeTag(ch.getType())).append("</type>\r\n");
			}
			strBuf.append("  </wpt>\r\n");
		}catch(Exception e){
			Vm.debug(ch.getWayPoint());
			e.printStackTrace();
			return null;
		}//try

		return strBuf.toString();
	}

	public String trailer(int total) {
		return "</gpx>\r\n";
	}

	public static String image2TypeText(String image){
		if (image.equals("icon_smile.gif")) return "Found it";
		if (image.equals("icon_sad.gif")) return "Didn't find it";
		if (image.equals("icon_note.gif")) return "Write note";
		if (image.equals("icon_enabled.gif")) return "Enable Listing";
		if (image.equals("icon_disabled.gif")) return "Temporarily Disable Listing";
		if (image.equals("icon_camera.gif")) return "Webcam Photo Taken";
		if (image.equals("11.png")) return "Webcam Photo Taken";
		if (image.equals("icon_attended.gif")) return "Attended";
		if (image.equals("icon_greenlight.gif")) return "Publish Listing";
		if (image.equals("icon_rsvp.gif")) return "Will Attend";
		if (image.equals("big_smile.gif")) return "Post Reviewer Note";
		if (image.equals("traffic_cone.gif")) return "Archive (show)";
		if (image.equals("icon_maint.gif")) return "Owner Maintenance";
		if (image.equals("icon_needsmaint.gif")) return "Needs Maintenance";
		if (image.equals("coord_update.gif")) return "Update Coordinates";
		if (image.equals("icon_remove.gif")) return "Needs Archived";

		return image;
	}

}
