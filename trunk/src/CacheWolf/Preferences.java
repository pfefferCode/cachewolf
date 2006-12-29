package CacheWolf;

import ewe.io.*;
import ewe.sys.*;
import ewe.ui.*;
import ewesoft.xml.*;
import ewesoft.xml.sax.*;
import ewe.filechooser.*;

/**
*	A class to hold the preferences that were loaded upon start up of CacheWolf.
*	This class is also capable of parsing the prefs.xml file as well as
*	saving the current settings of preferences.
*  Last change:
*    20061123 salzkammergut Added garminConn, used MyLocale
*/
public class Preferences extends MinML{
	
	public int tablePrefs[] = {1,1,1,1,1,1,1,1,1,1,1,1};
	public int tableWidth[] = {20,20,20,20,65,135,135,100,60,50,50,50};
	
	static protected final int PROFILE_SELECTOR_FORCED_ON=0;
	static protected final int PROFILE_SELECTOR_FORCED_OFF=1;
	static protected final int PROFILE_SELECTOR_ONOROFF=2;
	
	/** The currently used centre point, can be different from the profile's centrepoint. This is used
	 *  for spidering */
	public CWPoint curCentrePt=new CWPoint();
	/** Name of last used profile */
	public String lastProfile=new String(); 
	/** If true, the last profile is reloaded automatically without a dialogue */
	public boolean autoReloadLastProfile=false; 
	/** The base directory contains one subdirectory for each profile*/
	public String baseDir = new String();  // TODO Set this initially to mydataDir ??

	public String myproxy = new String();    
	public String myproxyport = new String();
	/** This is the login alias for geocaching.com and opencaching.de */
	public String myAlias = new String();
	/** This is an alternative alias used to identify found caches (i.e. if using multiple IDs) 
	 *  It is currently not used yet */
	public String myAlias2 = new String();
	/** The path to the browser */
	public String browser = new String();
		
	public int myAppHeight = 0;
	public int myAppWidth = 0;
	//public int nLogs = 5;
	public boolean dirty = false;
	
	public int currProfile = 0;
	public String profiles[] = new String[4];
	public String profdirs[] = new String[4];
	public String lats[] = new String[4];
	public String longs[] = new String[4];
	public String lastSyncOC[] = new String[4];
	public String lastDistOC[] = new String[4];
	public String garminConn="com1";  // The type of connection which GPSBABEL uses: com1 OR usb.
	// TODO Add garminConn to user interface. For the time being this can only be set by manually editing the pref file
		
	public String last_sync_opencaching = new String();
	public String distOC = new String();
	public boolean downloadPicsOC = true; //TODO Sollten die auch im Profil gespeichert werden mit Preferences als default Werte ?
	public boolean downloadMapsOC = true;
	public boolean downloadmissingOC = false;
	public boolean fixSIP = false;
	
	public String digSeparator = new String();
	public boolean debug = false;
	public SerialPortOptions mySPO = new SerialPortOptions();
	public boolean forwardGPS = false;
	public String forwardGpsHost = new String();
	public int fontSize = 14;
	// Helper variables for XML parser 
	private StringBuffer collectElement=null; 
	private String lastName; // The string to the last XML that was processed
	
	private String LOGFILENAME="log.txt";
	
	// The following declarations may eventually be moved to a separate class
	/** The actual directory of a profile, for new profiles this is a direct child of baseDir */
	//TODO Find all references amd move to profile.dataDir
	//public String mydatadir = new String();  //Redundant ??
	/** The centre as read from the profile */
	public CWPoint profileCentrePt;
	
    /**
     * Singleton pattern - return reference to Preferences
     * @return Singleton Preferences object
     */
	
	public static Preferences getPrefObject() {
      if (_reference == null)
          // it's ok, we can call this constructor
          _reference = new Preferences();
      return _reference;
    }

    private static Preferences _reference;

	/**
	 * Constructor is private for a singleton object
	 *
	 */
	private Preferences(){
		digSeparator=MyLocale.getDigSeparator();
		//Vm.debug("Separ: " + digSeparator);
		mySPO.bits = 8;
		mySPO.parity = SerialPort.NOPARITY;
		mySPO.stopBits = 1;
		mySPO.baudRate = 4800;
		// Ensure that logfile does not grow infinitely. Not really needed as every spider resets it
		File logFile = new File(LOGFILENAME);
		if (logFile.length()>60000) logInit();
	}
	
	/**
	* Returns true if coordinates have been set.
	* Does not validate! if coordinates are real.
	*/
	public boolean existCenter(){
		return curCentrePt.latDec!=0.0 && curCentrePt.lonDec!=0.0; 
	}
	
	/**
	* Method to open and parse the pref.xml file. Results are stored in the
	* public variables of this class.
	*/
	public void readPrefFile(){
		try{
			String datei = File.getProgramDirectory() + "/" + "pref.xml";
			datei = datei.replace('\\', '/');
			ewe.io.Reader r = new ewe.io.InputStreamReader(new ewe.io.FileInputStream(datei));
			parse(r);
			r.close();
		}catch(Exception e){
			if (e instanceof NullPointerException)
				Vm.debug("NullPointerException in Element "+lastName +". Wrong attribute?");
			else 
				Vm.debug(e.toString());
		}
	}
	
	/**
	 * Open Profile selector screen 
	 * @param prof
	 * @param showProfileSelector
	 * @return True if a profile was selected
	 */
	
	public boolean selectProfile(Profile prof, int showProfileSelector, boolean hasNewButton) {
		// If datadir is empty, ask for one
		if (baseDir.length()==0) {
			FileChooser fc = new FileChooser(FileChooser.DIRECTORY_SELECT,null);
			fc.title = "Select base directory for cache data";
			// If no base directory given, terminate
			if (fc.execute() == FileChooser.IDCANCEL) ewe.sys.Vm.exit(0);
			baseDir = fc.getChosenFile().toString();
		}
		if (!baseDir.endsWith("/")) baseDir+="/";
		//Vm.showWait(false);
		if((showProfileSelector==PROFILE_SELECTOR_FORCED_ON) || 
		   (showProfileSelector==PROFILE_SELECTOR_ONOROFF && !autoReloadLastProfile)){ // Ask for the profile
		   ProfilesForm f = new ProfilesForm(baseDir,profiles,lastProfile,hasNewButton);
		   int code = f.execute();
		   // If no profile chosen (includes a new one), terminate
		   if (code==-1) return false; // Cancel pressed
		   prof.clearProfile();
		   curCentrePt.set(0,0); // No centre yet
		   lastProfile=prof.name=f.newSelectedProfile;
		}
		currProfile=-1;
		if (lastProfile.equals(profiles[0])) openOldProfile(prof, 0);
		else if (lastProfile.equals(profiles[1])) openOldProfile(prof, 1);
		else if (lastProfile.equals(profiles[2])) openOldProfile(prof, 2);
		else if (lastProfile.equals(profiles[3])) openOldProfile(prof, 3);
		else { 
			prof.dataDir=baseDir+lastProfile+"/";
			//mydatadir=prof.dataDir;
		}
		savePreferences();
		return true;
		
/*		
		//Check if there are "profiles" entries. If yes display a form
		//so the user may choose a profile.
		try{
		if(showProfileSelector){
			if(profiles[0].equals("null")) profiles[0] = "";
			if(profiles[1].equals("null")) profiles[1] = "";
			if(profiles[2].equals("null")) profiles[2] = "";
			if(profiles[3].equals("null")) profiles[3] = "";
			if(profiles[0].length()>0 ||
			   profiles[1].length()>0 ||
			   profiles[2].length()>0 ||
			   profiles[3].length()>0){
				   Vm.showWait(false);
				   Form f = new ProfilesForm(profiles);
				   int code = f.execute(); // 0 for cancel, 1-4 for profile
				   currProfile = code;
				   Vm.showWait(true);
				   if(code > 0){
					   if(profiles[code-1].length()>0){
							mydatadir = profdirs[code-1];
							if(lastSyncOC[code-1] == null || lastSyncOC[code-1].endsWith("null")){
								last_sync_opencaching = "20050801000000";
							}
							else {
								last_sync_opencaching = lastSyncOC[code-1];
							}
							if(lastDistOC[code-1] == null || lastDistOC[code-1].endsWith("null")){
								distOC = "0";
							}
							else {
								distOC = lastDistOC[code-1];
							}
							curCentrePt.set(lats[code-1]+" "+longs[code-1]);
							// Copy it into profile
							prof.last_sync_opencaching=last_sync_opencaching;
							prof.distOC=distOC;
							prof.centre.set(lats[code-1]+" "+longs[code-1]);
							prof.dataDir=profdirs[code-1];
					   }
				   } else {
					   // No profile selected
					   prof.dataDir=mydatadir;
				   }
				   //if(mydatadir.indexOf('.') > 0){
				   //String cwd = File.getProgramDirectory();
				   //mydatadir = cwd + "/" + mydatadir.substring(1, mydatadir.length()-2);
				   //Vm.debug("Datadir? " + mydatadir);
				   //}
			   }
		}
		}catch(Exception e){
			Vm.debug(e.toString());
		}
*/		
	}
	
	/**
	 * Open an old Profile (stored in preferences)
	 * @param i 0-3 for profiles 1-4
	 */
	private void openOldProfile(Profile prof, int i) {
		currProfile=i+1;
		curCentrePt.set(lats[i]+" "+longs[i]);
		//mydatadir=profdirs[i];
		if(lastSyncOC[i] == null || lastSyncOC[i].endsWith("null")){
			last_sync_opencaching = "20050801000000";
		}else {
			last_sync_opencaching = lastSyncOC[i];
		}
		if(lastDistOC[i] == null || lastDistOC[i].endsWith("null")){
			distOC = "0";
		} else {
			distOC = lastDistOC[i];
		}
		prof.last_sync_opencaching=last_sync_opencaching;
		prof.distOC=distOC;
		prof.centre.set(lats[i]+" "+longs[i]);
		prof.dataDir=profdirs[i];
	}
	

	
	
	/**
	* Method that gets called when a new element has been identified in pref.xml
	*/
	public void startElement(String name, AttributeList atts){
		//Vm.debug("name = "+name);
		lastName=name;
		String tmp;
		if(name.equals("browser")) browser = atts.getValue("name");
		if(name.equals("syncOC")) {
			if (atts.getValue("date") == null || atts.getValue("date").endsWith("null")){
				last_sync_opencaching = "20050801000000";
			}
			else {
				last_sync_opencaching = atts.getValue("date");
			}
			if (atts.getValue("dist") == null || atts.getValue("dist").endsWith("null")){
				distOC = "0";
			}
			else {
				distOC =  atts.getValue("dist");
			}
		}
		if(name.equals("fixedsip")) {
			if(atts.getValue("state").equals("true")) {
				fixSIP = true;
			}
		}
		if(name.equals("font")) fontSize = Convert.toInt(atts.getValue("size"));
		if(name.equals("alias")) myAlias = atts.getValue("name");
		if(name.equals("alias2")) myAlias2 = atts.getValue("name");
		if(name.equals("location")){
			curCentrePt.set(atts.getValue("lat")+" "+atts.getValue("long"));
		}
		if(name.equals("port")){
			mySPO.portName = atts.getValue("portname");
			mySPO.baudRate = Convert.toInt(atts.getValue("baud"));
		}
		if(name.equals("portforward")) {
			forwardGPS = Convert.toBoolean(atts.getValue("active"));
			forwardGpsHost = atts.getValue("destinationHost");
		}
		//if(name.equals("logs")){
		//	nLogs = Convert.parseInt(atts.getValue("number"));
		//}
		if(name.equals("profile1")){
			profiles[0] = atts.getValue("name");
			profdirs[0] = atts.getValue("dir");
			lats[0] = atts.getValue("lat");
			longs[0] = atts.getValue("lon");
			lastSyncOC[0] = atts.getValue("lastsyncoc");
			lastDistOC[0] = atts.getValue("lastdistoc");
		}
		if(name.equals("profile2")){
			profiles[1] = atts.getValue("name");
			profdirs[1] = atts.getValue("dir");
			lats[1] = atts.getValue("lat");
			longs[1] = atts.getValue("lon");
			lastSyncOC[1] = atts.getValue("lastsyncoc");
			lastDistOC[1] = atts.getValue("lastdistoc");
		}
		if(name.equals("profile3")){
			profiles[2] = atts.getValue("name");
			profdirs[2] = atts.getValue("dir");
			lats[2] = atts.getValue("lat");
			longs[2] = atts.getValue("lon");
			lastSyncOC[2] = atts.getValue("lastsyncoc");
			lastDistOC[2] = atts.getValue("lastdistoc");
		}
		if(name.equals("profile4")){
			profiles[3] = atts.getValue("name");
			profdirs[3] = atts.getValue("dir");
			lats[3] = atts.getValue("lat");
			longs[3] = atts.getValue("lon");
			lastSyncOC[3] = atts.getValue("lastsyncoc");
			lastDistOC[3] = atts.getValue("lastdistoc");
		}
		if (name.equals("lastprofile")) {
			collectElement=new StringBuffer(50);
			if (atts.getValue("autoreload").equals("true")) autoReloadLastProfile=true;
		}
		
		//if(name.equals("datadir")) {
			//mydatadir = atts.getValue("dir");
			//profile.dataDir=mydatadir;
		//}
		if(name.equals("basedir")) {
			baseDir = atts.getValue("dir");
		}
		if (name.equals("opencaching")) {
			downloadPicsOC = Boolean.valueOf(atts.getValue("downloadPics")).booleanValue();
			downloadMapsOC = Boolean.valueOf(atts.getValue("downloadMaps")).booleanValue();
			downloadmissingOC = Boolean.valueOf(atts.getValue("downloadmissing")).booleanValue();
			
		}
		if(name.equals("proxy")) {
			myproxy = atts.getValue("prx");
			myproxyport = atts.getValue("prt");
		}
		if (name.equals("garmin")) {
			garminConn=atts.getValue("connection");
		}
		
		if(name.equals("tableType")){ 
			tablePrefs[1] = Convert.parseInt(atts.getValue("active"));
			tmp = atts.getValue("width");
			if (tmp != null) tableWidth[1] = Convert.parseInt(tmp);
		}
		if(name.equals("tableD")){
			tablePrefs[2] = Convert.parseInt(atts.getValue("active"));
			tmp = atts.getValue("width");
			if (tmp != null) tableWidth[2] = Convert.parseInt(tmp);
		}
		if(name.equals("tableT")){
			tablePrefs[3] = Convert.parseInt(atts.getValue("active"));
			tmp = atts.getValue("width");
			if (tmp != null) tableWidth[3] = Convert.parseInt(tmp);
		}
		if(name.equals("tableWay")) {
			tablePrefs[4] = Convert.parseInt(atts.getValue("active"));
			tmp = atts.getValue("width");
			if (tmp != null) tableWidth[4] = Convert.parseInt(tmp);
		}
		if(name.equals("tableName")){
			tablePrefs[5] = Convert.parseInt(atts.getValue("active"));
			tmp = atts.getValue("width");
			if (tmp != null) tableWidth[5] = Convert.parseInt(tmp);
		}
		if(name.equals("tableLoc")){
			tablePrefs[6] = Convert.parseInt(atts.getValue("active"));
			tmp = atts.getValue("width");
			if (tmp != null) tableWidth[6] = Convert.parseInt(tmp);
		}
		if(name.equals("tableOwn")){
			tablePrefs[7] = Convert.parseInt(atts.getValue("active"));
			tmp = atts.getValue("width");
			if (tmp != null) tableWidth[7] = Convert.parseInt(tmp);
		}
		if(name.equals("tableHide")){
			tablePrefs[8] = Convert.parseInt(atts.getValue("active"));
			tmp = atts.getValue("width");
			if (tmp != null) tableWidth[8] = Convert.parseInt(tmp);
		}
		if(name.equals("tableStat")){
			tablePrefs[9] = Convert.parseInt(atts.getValue("active"));
			tmp = atts.getValue("width");
			if (tmp != null) tableWidth[9] = Convert.parseInt(tmp);
		}
		if(name.equals("tableDist")){
			tablePrefs[10] = Convert.parseInt(atts.getValue("active"));
			tmp = atts.getValue("width");
			if (tmp != null) tableWidth[10] = Convert.parseInt(tmp);
		}
		if(name.equals("tableBear")){
			tablePrefs[11] = Convert.parseInt(atts.getValue("active"));
			tmp = atts.getValue("width");
			if (tmp != null) tableWidth[11] = Convert.parseInt(tmp);
		}
	}

	public void characters( char ch[], int start, int length )
	{
		if (collectElement!=null) {
			collectElement.append(ch,start,length); // Collect the name of the last profile
		}
	}	
	
	/**
	* Method that gets called when the end of an element has been identified in pref.xml
	*/
	public void endElement(String tag){
		if (tag.equals("lastprofile")) {
			if (collectElement!=null) lastProfile=collectElement.toString();
		}
		collectElement=null;
	}
	

	/**
	* Method to save current preferences in the pref.xml file
	*/
	public void savePreferences(){
		String datei = File.getProgramDirectory() + "/" + "pref.xml";
		datei = datei.replace('\\', '/');
		last_sync_opencaching = last_sync_opencaching==null?"20050801000000":last_sync_opencaching;
		distOC = distOC==null?"0":distOC;
		if (currProfile > 0) {
			lastSyncOC[currProfile -1] = last_sync_opencaching;
			lastDistOC[currProfile - 1] = distOC;
		}

		try{
			PrintWriter outp =  new PrintWriter(new BufferedWriter(new FileWriter(datei)));
			outp.print("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
			outp.print("<preferences>\n");
			outp.print("	<alias name =\""+ SafeXML.clean(myAlias) +"\"/>\n");
			outp.print("	<alias2 name =\""+ SafeXML.clean(myAlias2) +"\"/>\n");
			outp.print("	<basedir dir = \""+ baseDir +"\"/>\n");
			outp.print("	<proxy prx = \""+ myproxy+"\" prt = \""+ myproxyport + "\"/>\n");
			outp.print("	<port portname = \""+ mySPO.portName +"\" baud = \""+ mySPO.baudRate+"\"/>\n");
			outp.print("	<portforward active= \""+ Convert.toString(forwardGPS)+"\" destinationHost = \""+ forwardGpsHost+"\"/>\n");
						outp.print("	<tableType active = \"1\" width = \""+Convert.toString(tableWidth[1])+"\"/>\n");
			outp.print("	<tableD active = \""+Convert.toString(tablePrefs[2])+ "\"" +
					               " width = \""+Convert.toString(tableWidth[2])+"\"/>\n");
			outp.print("	<tableT active = \""+Convert.toString(tablePrefs[3])+ "\"" +
								   " width = \""+Convert.toString(tableWidth[3])+"\"/>\n");
			outp.print("	<tableWay active = \"1\" width = \""+Convert.toString(tableWidth[4])+"\"/>\n");
			outp.print("	<tableName active = \"1\" width = \""+Convert.toString(tableWidth[5])+"\"/>\n");
			outp.print("	<tableLoc active = \""+Convert.toString(tablePrefs[6])+ "\"" +
					   				 " width = \""+Convert.toString(tableWidth[6])+"\"/>\n");
			outp.print("	<tableOwn active = \""+Convert.toString(tablePrefs[7])+ "\"" +
					   				 " width = \""+Convert.toString(tableWidth[7])+"\"/>\n");
			outp.print("	<tableHide active = \""+Convert.toString(tablePrefs[8])+ "\"" +
					   				  " width = \""+Convert.toString(tableWidth[8])+"\"/>\n");
			outp.print("	<tableStat active = \""+Convert.toString(tablePrefs[9])+ "\"" +
					   				  " width = \""+Convert.toString(tableWidth[9])+"\"/>\n");
			outp.print("	<tableDist active = \""+Convert.toString(tablePrefs[10])+ "\"" +
					   				  " width = \""+Convert.toString(tableWidth[10])+"\"/>\n");
			outp.print("	<tableBear active = \""+Convert.toString(tablePrefs[11])+ "\"" +
					   				  " width = \""+Convert.toString(tableWidth[11])+"\"/>\n");
			outp.print("    <font size =\""+fontSize+"\"/>\n");
			outp.print("	<browser name = \""+browser+"\"/>\n");
			outp.print("    <fixedsip state = \""+fixSIP+"\"/>\n");
			outp.print("    <garmin connection = \""+garminConn+"\"/>\n");
			outp.print("    <lastprofile autoreload=\""+autoReloadLastProfile+"\">"+lastProfile+"</lastprofile>\n"); //RB
			outp.print("    <opencaching downloadPicsOC=\""+downloadPicsOC+"\" downloadMaps=\""+downloadMapsOC+"\" downloadMissing=\""+downloadmissingOC+"\"/>\n");
			// Obsolete data kept for backward compatibility
			outp.print("	<syncOC date = \"" + last_sync_opencaching + "\" dist = \"" + distOC +  "\"/>\n");
			outp.print("	<location lat = \""+curCentrePt.getLatDeg(CWPoint.DD)+"\" long = \""+curCentrePt.getLonDeg(CWPoint.DD)+"\"/>\n");
			//outp.print("	<datadir dir = \""+ mydatadir +"\"/>\n");
			outp.print("	<profile1 name = \""+profiles[0]+"\" lat = \""+ lats[0] +"\" lon = \""+ longs[0] +"\" dir = \""+ profdirs[0] +"\" lastsyncoc= \"" + lastSyncOC[0] + "\" lastdistoc= \"" + lastDistOC[0] + "\" />\n");
			outp.print("	<profile2 name = \""+profiles[1]+"\" lat = \""+ lats[1] +"\" lon = \""+ longs[1] +"\" dir = \""+ profdirs[1] +"\" lastsyncoc= \"" + lastSyncOC[1] + "\" lastdistoc= \"" + lastDistOC[1] + "\" />\n");
			outp.print("	<profile3 name = \""+profiles[2]+"\" lat = \""+ lats[2] +"\" lon = \""+ longs[2] +"\" dir = \""+ profdirs[2] +"\" lastsyncoc= \"" + lastSyncOC[2] + "\" lastdistoc= \"" + lastDistOC[2] + "\" />\n");
			outp.print("	<profile4 name = \""+profiles[3]+"\" lat = \""+ lats[3] +"\" lon = \""+ longs[3] +"\" dir = \""+ profdirs[3] +"\" lastsyncoc= \"" + lastSyncOC[3] + "\" lastdistoc= \"" + lastDistOC[3] + "\" />\n");
			outp.print("</preferences>");
			outp.close();
		} catch (Exception e) {
			Vm.debug("Problem saving: " +datei);
			Vm.debug("Error: " +e.toString());
    		}
	}
	
	/**
	 * Method to log messages to a file called log.txt
	 * It will always append to an existing file.
	 * @param text
	 */
	public void log(String text){
		Time dtm = new Time();
		dtm.getTime();
		dtm.setFormat("dd.MM.yyyy'/'HH:mm");
		text = dtm.toString()+ ": "+ text + "\n";
		File logFile = new File(LOGFILENAME);
		Stream strout = null;
		try{
			strout = logFile.toWritableStream(true);
			strout.write(text.getBytes());
		}catch(Exception ex){
			Vm.debug("Error writing to log file!");
		}finally{
			strout.close();
		}
	}
	
	/**
	 * Method to delete an existing log file. Something like a "reset".
	 * Should be used "from time to time" to make sure the log file does not grow
	 * to a huge size! Called on every SpiderGC
	 */
	public void logInit(){
		File logFile = new File(LOGFILENAME);
		logFile.delete();
	}
}
