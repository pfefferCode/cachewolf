package exp;

import utils.FileBugfix;
import CacheWolf.CWPoint;
import CacheWolf.CacheHolder;
import CacheWolf.CacheSize;
import CacheWolf.CacheTerrDiff;
import CacheWolf.CacheType;
import CacheWolf.Global;
import CacheWolf.Log;
import CacheWolf.LogList;
import CacheWolf.SafeXML;

import com.stevesoft.ewe_pat.Regex;
import com.stevesoft.ewe_pat.Transformer;

import ewe.filechooser.FileChooser;
import ewe.filechooser.FileChooserBase;
import ewe.io.BufferedWriter;
import ewe.io.File;
import ewe.io.FileBase;
import ewe.io.FileWriter;
import ewe.io.PrintWriter;
import ewe.sys.Date;
import ewe.sys.Handle;
import ewe.sys.Vm;
import ewe.ui.CheckBoxGroup;
import ewe.ui.Control;
import ewe.ui.ControlEvent;
import ewe.ui.Event;
import ewe.ui.Form;
import ewe.ui.FormBase;
import ewe.ui.ProgressBarForm;
import ewe.ui.mButton;
import ewe.ui.mCheckBox;
import ewe.util.Enumeration;
import ewe.util.Hashtable;
import ewe.util.Iterator;
import ewe.util.Random;

//TODO: use safexml a lot more (at least start using it ;) )

/**
 * experimental GPX exporter that should better handle the various tasks that
 * can be accomplished with GPX it is not yet linked to any menu, so if you want
 * to play around with it, first you have to create a menu item
 * 
 */
public class GpxExportNg {

	/** export is in compact format */
	final static int GPX_COMPACT = 0;
	/** export is PQ like */
	final static int GPX_PQLIKE = 1;
	/** export follows gc.com myfinds format */
	final static int GPX_MYFINDSPQ = 2;

	final static String expName = "GpxExportNG";
	final static String TRUE = "True";
	final static String FALSE = "False";
	private static GarminMap gm;

	final static String GPXHEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			.concat("<gpx xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" version=\"1.0\" creator=\"CacheWolf\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd http://www.groundspeak.com/cache/1/0 http://www.groundspeak.com/cache/1/0/cache.xsd\" xmlns=\"http://www.topografix.com/GPX/1/0\">\n")
			.concat("<name>Waypoints for Cache Listings, Generated by CacheWolf</name>\n")
			.concat("<desc>This is a list of waypoints for geocaches generated by CacheWolf</desc>\n")
			.concat("<author>Various users from geocaching.com and/or opencaching.de</author>\n")
			.concat("<email>contact@cachewolf.de</email>\n")
			.concat("<url>http://www.cachewolf.de/</url>\n")
			.concat("<urlname>CacheWolf - Paperless Geocaching</urlname>\n")
			.concat("<time>@@CREATEDATE@@T00:00:00Z</time>\n")
			.concat("<keywords>cache, geocache, waypoints</keywords>\n")
	// TODO: is it worth a second loop?
	// .concat("<bounds minlat=\"50.91695\" minlon=\"6.876383\" maxlat=\"50.935183\" maxlon=\"6.918817\" />")
			;

	final static String GPXCOMPACT = "\t<wpt lat=\"@@WPLAT@@\" lon=\"@@WPLON@@\">\n"
			.concat("\t\t<time>@@CACHETIME@@T00:00:00</time>\n")
			.concat("\t\t<name>@@WPNAME@@</name>\n")
			.concat("\t\t<cmt>@@WPCMT@@</cmt>\n")
			.concat("\t\t<desc>@@WPDESC@@</desc>\n")
			.concat("\t\t<url>@@WPURL@@</url>\n")
			.concat("\t\t<urlname>@@WPURLNAME@@</urlname>\n")
			.concat("\t\t<sym>@@WPSYMBOL@@</sym>\n")
			.concat("\t\t<type>@@WPTYPE@@</type>\n");

	final static String GPXEXTENSION = "\t\t<groundspeak:cache id=\"@@CACHEID@@\" available=\"@@CACHEAVAILABLE@@\" archived=\"@@CACHEARCHIVED\" xmlns:groundspeak=\"http://www.groundspeak.com/cache/1/0\">\n"
			.concat("\t\t\t<groundspeak:name>@@CACHENAME@@</groundspeak:name>\n")
			.concat("\t\t\t<groundspeak:placed_by>@@CACHEPLACEDBY@@</groundspeak:placed_by>\n")
			.concat("\t\t\t<groundspeak:owner id=\"@@CACHEOWNERID@@\">@@CACHEOWNER@@</groundspeak:owner>\n")
			.concat("\t\t\t<groundspeak:type>@@CACHETYPE@@</groundspeak:type>\n")
			.concat("\t\t\t<groundspeak:container>@@CACHECONTAINER@@</groundspeak:container>\n")
			.concat("\t\t\t<groundspeak:difficulty>@@CACHEDIFFICULTY@@</groundspeak:difficulty>\n")
			.concat("\t\t\t<groundspeak:terrain>@@CACHETERRAIN@@</groundspeak:terrain>\n")
			.concat("\t\t\t<groundspeak:country>@@CACHECOUNTRY@@</groundspeak:country>\n")
			.concat("\t\t\t<groundspeak:state>@@CACHESTATE@@</groundspeak:state>\n")
			.concat("\t\t\t<groundspeak:short_description html=\"@@CACHEHTML@@\">@@CACHESHORTDESCRIPTION@@</groundspeak:short_description>\n")
			.concat("\t\t\t<groundspeak:long_description html=\"@@CACHEHTML@@\">@@CACHELONGDESCRIPTION@@</groundspeak:long_description>\n")
			.concat("\t\t\t<groundspeak:encoded_hints>@@CACHEHINT@@</groundspeak:encoded_hints>\n");

	final static String GPXLOG = "\t\t\t\t<groundspeak:log id=\"@@LOGID@@\">\n"
			.concat("\t\t\t\t\t<groundspeak:date>@@LOGDATE@@T00:00:00</groundspeak:date>\n")
			.concat("\t\t\t\t\t<groundspeak:type>@@LOGTYPE@@</groundspeak:type>\n")
			.concat("\t\t\t\t\t<groundspeak:finder id=\"@@LOGFINDERID@@\">@@LOGFINDER@@</groundspeak:finder>\n")
			.concat("\t\t\t\t\t<groundspeak:text encoded=\"@@LOGENCODE@@\">@@LOGTEXT@@</groundspeak:text>\n")
			.concat("\t\t\t\t</groundspeak:log>\n");

	final static String GPXTB = "\t\t\t\t<groundspeak:travelbug id=\"@@TBID@@\" ref=\"@@TBREF@@\">\n"
			.concat("\t\t\t\t\t<groundspeak:name>@@TBNAME@@</groundspeak:name>\n")
			.concat("\t\t\t\t</groundspeak:travelbug>\n");

	// FIXME: don't use this until GPX import can strip this off as well
	final static String GPXADDIINMAIN = "@@ADDIID@@ - @@ADDISHORT@@@@ADDIDELIM@@"
			.concat("@@ADDILAT@@ @@ADDILON@@@@ADDIDELIM@@")
			.concat("@@ADDILONG@@@@ADDIDELIM@@");

	static boolean smartIds;
	static boolean customIcons;
	static boolean separateFiles;
	static boolean sendToGarmin;
	static int outType;

	public GpxExportNg() {
		GpxExportNgForm exportOptions;
		int ret;

		exportOptions = new GpxExportNgForm();
		ret = exportOptions.execute();

		if (FormBase.IDCANCEL == ret) {
			return;
		}

		outType = exportOptions.getExportType();
		smartIds = exportOptions.getSmartIds();
		separateFiles = exportOptions.getSeparateFiles();
		sendToGarmin = exportOptions.getSendToGarmin();
		customIcons = exportOptions.getCustomIcons();

		if (separateFiles) {
			final Hashtable fileHandles = new Hashtable();
			final String outDir;
			final String tempDir;
			final String baseDir = FileBase.getProgramDirectory();
			final String prefix="GC-";
			final FileChooser fc;
			
			if (sendToGarmin) { 
				fc = new FileChooser(FileChooserBase.DIRECTORY_SELECT,Global.getPref().getExportPath(expName+"-GPI"));
			} else {
				fc = new FileChooser(FileChooserBase.DIRECTORY_SELECT,Global.getPref().getExportPath(expName+"-POI"));
			}
			
			fc.setTitle("Select target directory:");
			
			if (fc.execute() == FormBase.IDCANCEL)
				return;

			outDir = fc.getChosenFile().getFullPath();
			if (sendToGarmin) {
				Global.getPref().setExportPath(expName+"-GPI", outDir);
			} else {
				Global.getPref().setExportPath(expName+"-POI", outDir);
			}
			
			if ((new File(baseDir+"/garminmap.xml")).exists()) {
				gm=new GarminMap();
				gm.readGarminMap();
			} else {
				//TODO: display warning
				Global.getPref().log("unable to load garminmap.xml");
				return;
			}
			
			if (sendToGarmin) {
				tempDir = baseDir+File.separator+this.getClass().toString(); //FIXME: get from dialog
				new File(tempDir).mkdir();
			} else {
				tempDir = outDir;
				String tmp[] = new FileBugfix(tempDir).list(prefix + "*.gpx", ewe.io.FileBase.LIST_FILES_ONLY);
				for (int i=0; i < tmp.length;i++){
					FileBugfix tmpFile = new FileBugfix(tempDir + tmp[i]);
					tmpFile.delete();
				}
				tmp = new FileBugfix(tempDir).list(prefix + "*.bmp", ewe.io.FileBase.LIST_FILES_ONLY);
				for (int i=0; i < tmp.length;i++){
					FileBugfix tmpFile = new FileBugfix(tempDir + tmp[i]);
					tmpFile.delete();
				}
			}

			ProgressBarForm pbf = new ProgressBarForm();
			try {
				Handle h = new Handle();

				int expCount = 0;
				int totalCount = Global.getProfile().cacheDB.countVisible();
				
				pbf.showMainTask = false;
				pbf.setTask(h, "Exporting ...");
				pbf.exec();
				
				for (int i = 0; i < Global.getProfile().cacheDB.size(); i++) {
					CacheHolder ch = Global.getProfile().cacheDB.get(i);
					if (!ch.isVisible()) {
						continue;
					} else if (ch.is_incomplete()) {
						Global.getPref().log(
								"skipping export of incomplete waypoint "
										+ ch.getWayPoint());
					} else {
						String poiId = gm.getPoiId(ch);
						if (null == poiId) {
							Global.getPref().log("unmatched POI ID for "+ch.getWayPoint());
						} else {
							File outFile;
							PrintWriter writer;
							if (fileHandles.containsKey(poiId)) {
								writer = (PrintWriter) fileHandles.get(poiId);
							} else {
								writer = new PrintWriter(new BufferedWriter(new FileWriter(new File(tempDir + File.separator + prefix+poiId+".gpx"))));
								fileHandles.put(poiId, writer);
								writer.print(formatHeader());
							}
							writer.print(formatCache(ch));
						}
						
					}
					expCount++;
					h.progress = (float) expCount / (float) totalCount;
					h.changed();
				}
				
				Enumeration keys = fileHandles.keys();
				while (keys.hasMoreElements()) {
					String key = (String) keys.nextElement();
					PrintWriter writer = (PrintWriter) fileHandles.get(key);
					writer.print("</gpx>\n");
					writer.close();
				}
				
				if (sendToGarmin) {
					String tmp[] = new FileBugfix(outDir).list(prefix + "*.gpi", ewe.io.FileBase.LIST_FILES_ONLY);
					for (int i=0; i < tmp.length;i++){
						FileBugfix tmpFile = new FileBugfix(tempDir + tmp[i]);
						tmpFile.delete();
					}				

					// TODO: create GPI files
					FileBugfix tmpdir = new FileBugfix(tempDir);
					tmpdir.delete();
				}
				
				pbf.exit(0);
				
				//TODO: connect with image and build garmin poi file
			} catch (Exception e) {
				e.printStackTrace();
				pbf.exit(0);
			}
		} else {
			if (customIcons) {
				if ((new File(FileBase.getProgramDirectory()+"/garminmap.xml")).exists()) {
					gm=new GarminMap();
					gm.readGarminMap();
				} else {
					customIcons = false;
					Global.getPref().log("unable to load garminmap.xml");
				}
			}
			final File file;
			final FileChooser fc = new FileChooser(FileChooserBase.SAVE, 
					Global.getPref().getExportPath(expName+"-GPX"));
			
			fc.setTitle("Select target GPX file:");
			fc.addMask("*.gpx");
			
			if (fc.execute() == FormBase.IDCANCEL)
				return;

			file = fc.getChosenFile();
			Global.getPref().setExportPath(expName+"-GPX", file.getPath());

			try {
				ProgressBarForm pbf = new ProgressBarForm();
				Handle h = new Handle();
				PrintWriter outp = new PrintWriter(new BufferedWriter(
						new FileWriter(file)));
				int expCount = 0;
				int totalCount = Global.getProfile().cacheDB.countVisible();

				outp.print(formatHeader());

				pbf.showMainTask = false;
				pbf.setTask(h, "Exporting ...");
				pbf.exec();

				for (int i = 0; i < Global.getProfile().cacheDB.size(); i++) {
					CacheHolder ch = Global.getProfile().cacheDB.get(i);
					if (!ch.isVisible()) {
						continue;
					} else if (ch.is_incomplete()) {
						Global.getPref().log(
								"skipping export of incomplete waypoint "
										+ ch.getWayPoint());
					} else {
						outp.print(formatCache(ch));
					}
					expCount++;
					h.progress = (float) expCount / (float) totalCount;
					h.changed();
				}

				pbf.exit(0);

				outp.print("</gpx>\n");
				outp.close();
			} catch (Exception ex) {
				if (Global.getPref().debug)
					Global.getPref().log("unable to write GPX output to " + file.toString(),ex);
				else
					Global.getPref().log("unable to write GPX output to " + file.toString());
				// TODO: give a message to the user
			}
		}
	}

	private String formatCache(CacheHolder ch) {
		// no addis or custom in MyFindsPq - and of course only finds
		if ((GPX_MYFINDSPQ == outType) && ((ch.getType() == CacheType.CW_TYPE_CUSTOM) || ch.isAddiWpt() || !ch.is_found()))
			return "";
		
		if (!ch.pos.isValid()) return ""; 

		StringBuffer ret = new StringBuffer();

		ret.append(formatCompact(ch));
		
		if (outType != GPX_COMPACT && !(ch.getType() == CacheType.CW_TYPE_CUSTOM || ch.isAddiWpt())) {
			ret.append(formatPqExtensions(ch));
		}
		
		ret.append("\t</wpt>\n");

		return ret.toString();
	}

	private String formatCompact(CacheHolder ch) {
		
		Transformer trans = new Transformer(true);

		trans.add(new Regex("@@WPLAT@@", String.valueOf(ch.pos.latDec)));

		trans.add(new Regex("@@WPLON@@", String.valueOf(ch.pos.lonDec)));

		if (ch.isAddiWpt()) {
			try {
				trans.add(new Regex("@@CACHETIME@@", ch.mainCache.getDateHidden()));
			} catch (Exception e) {
				Global.getPref().log(ch.getWayPoint()+" has no parent");
				trans.add(new Regex("@@CACHETIME@@", "1970-01-01"));
			}
		} else if (ch.getType() == CacheType.CW_TYPE_CUSTOM) {
			trans.add(new Regex("@@CACHETIME@@", "1970-01-01"));
		} else {
			trans.add(new Regex("@@CACHETIME@@", ch.getDateHidden()));
		}

		if (smartIds && ch.getType() != CacheType.CW_TYPE_CUSTOM) {
			if (ch.isAddiWpt()) {
				trans.add(new Regex("@@WPNAME@@", ch.mainCache.getWayPoint()
						.concat(" ").concat(ch.getWayPoint().substring(0, 2))));
			} else {
				trans.add(new Regex("@@WPNAME@@", ch.getWayPoint()
						.concat(" ")
						.concat(CacheType.getExportShortId(ch.getType()))
						.concat(String.valueOf(ch.getTerrain()))
						.concat(String.valueOf(ch.getHard()))
						.concat(CacheSize.getExportShortId(ch.getCacheSize()))));
			}
		} else {
			trans.add(new Regex("@@WPNAME@@", ch.getWayPoint()));
		}

		if (ch.isAddiWpt() || ch.getType() == CacheType.CW_TYPE_CUSTOM) {
			trans.add(new Regex("@@WPCMT@@",
					SafeXML.cleanGPX(ch.getFreshDetails().LongDescription)));
		} else {
			trans.add(new Regex("@@WPCMT@@", ""));
		}

		if (ch.isAddiWpt()) {
			trans.add(new Regex("@@WPDESC@@", SafeXML.cleanGPX(ch.getCacheName())));
		} else {
			trans.add(new Regex("@@WPDESC@@", SafeXML.cleanGPX(ch.getCacheName()
					.concat(" by ")
					.concat(ch.getCacheOwner())
					.concat(", ")
					.concat(CacheType.cw2ExportString(ch.getType()))
					.concat(" (").concat(CacheTerrDiff.shortDT(ch.getHard()))
					.concat("/").concat(CacheTerrDiff.shortDT(ch.getTerrain()))
					.concat(")"))));
		}

		if (ch.getType() == CacheType.CW_TYPE_CUSTOM) {
			trans.add(new Regex("@@WPURL@@", ""));
		} else {
			if (ch.isAddiWpt()) {
				// TODO: find out URL schema for additional waypoints
				// TODO: check for OC caches
				trans.add(new Regex("@@WPURL@@",
						"http://www.geocaching.com/seek/wpt.aspx?wp="
						.concat(ch.getWayPoint())));
			} else {
				// TODO: check for OC caches
				trans.add(new Regex("@@WPURL@@",
						"http://www.geocaching.com/seek/cache_details.aspx?wp="
						.concat(ch.getWayPoint())));
			}
		}

		if (ch.getType() == CacheType.CW_TYPE_CUSTOM) {
			trans.add(new Regex("@@WPURLNAME@@", ""));
		} else {
			trans.add(new Regex("@@WPURLNAME@@", SafeXML.cleanGPX(ch.getCacheName())));
		}

		if (customIcons) {
			trans.add(new Regex("@@WPSYMBOL@@", gm.getIcon(ch)));
		} else {
			if (ch.isAddiWpt()) {
				trans.add(new Regex("@@WPSYMBOL@@", CacheType.id2GpxString(
					ch.getType()).substring(CacheType.id2GpxString(ch.getType()).indexOf("|") + 1)
					));
			} else if (ch.getType() == CacheType.CW_TYPE_CUSTOM) {
				trans.add(new Regex("@@WPSYMBOL@@", "Custom"));
			} else if (ch.is_found()) {
				trans.add(new Regex("@@WPSYMBOL@@", "Geocache found"));
			} else {
				trans.add(new Regex("@@WPSYMBOL@@", "Geocache"));
			}
		}

		trans.add(new Regex("@@WPTYPE@@", CacheType.id2GpxString(ch.getType())));

		return trans.replaceFirst(GPXCOMPACT);
	}

	private String formatPqExtensions(CacheHolder ch) {
		// no details pq details for addis or custom waypoints
		if (ch.getType() == CacheType.CW_TYPE_CUSTOM || ch.isAddiWpt())
			return "";

		StringBuffer ret = new StringBuffer();
		Transformer trans = new Transformer(true);
		ch.getFreshDetails();
		trans.add(new Regex("@@CACHEID@@", ch.GetCacheID()));
		trans.add(new Regex("@@CACHEAVAILABLE@@", ch.is_available() ? TRUE : FALSE));
		trans.add(new Regex("@@CACHEARCHIVED", ch.is_archived() ? TRUE : FALSE));
		trans.add(new Regex("@@CACHENAME@@", SafeXML.cleanGPX(ch.getCacheName())));
		trans.add(new Regex("@@CACHEPLACEDBY@@", SafeXML.cleanGPX(ch.getCacheOwner())));
		trans.add(new Regex("@@CACHEOWNERID@@", "31415"));
		trans.add(new Regex("@@CACHEOWNER@@", SafeXML.cleanGPX(ch.getCacheOwner())));
		trans.add(new Regex("@@CACHETYPE@@", CacheType.id2GpxString(ch.getType())));
		trans.add(new Regex("@@CACHECONTAINER@@", CacheSize.cw2ExportString(ch.getCacheSize())));
		trans.add(new Regex("@@CACHEDIFFICULTY@@", CacheTerrDiff.shortDT(ch.getHard())));
		trans.add(new Regex("@@CACHETERRAIN@@", CacheTerrDiff.shortDT(ch.getTerrain())));
		trans.add(new Regex("@@CACHECOUNTRY@@", SafeXML.cleanGPX(ch.details.Country)));
		trans.add(new Regex("@@CACHESTATE@@", SafeXML.cleanGPX((ch.details.State))));
		trans.add(new Regex("@@CACHEHTML@@", ch.is_HTML() ? TRUE : FALSE));
		trans.add(new Regex("@@CACHESHORTDESCRIPTION@@",
				"CacheWolf can not provide a short description"));
		trans.add(new Regex("@@CACHELONGDESCRIPTION@@",
				SafeXML.cleanGPX(formatLongDescription(ch))));
		trans.add(new Regex("@@CACHEHINT@@", SafeXML.cleanGPX(ch.details.Hints)));

		ret.append(trans.replaceAll(GPXEXTENSION));

		ret.append("\t\t\t<groundspeak:logs>\n");
		ret.append(formatLogs(ch));
		ret.append("\t\t\t</groundspeak:logs>\n");

		// ret.append("\t\t\t<groundspeak:travelbugs>\n");
		// ret.append(formatTbs(ch));
		// ret.append("\t\t\t</groundspeak:travelbugs>\n");

		ret.append("\t\t</groundspeak:cache>\n");
		return ret.toString();
	}

	public void doit() {

	}

	public String formatTbs(CacheHolder ch) {
		Transformer trans = new Transformer(true);
		return "";
		// return trans.replaceFirst(GPXTB);
	}

	public String formatLogs(CacheHolder ch) {
		LogList logs = ch.getFreshDetails().CacheLogs;
		StringBuffer ret = new StringBuffer();
		
		if (0 == logs.size())
			return "";
		
		for (int i = 0; i < logs.size(); i++) {
			Log log = logs.getLog(i);
			
			if (outType == GPX_MYFINDSPQ
					&& !log.getLogger().equals(Global.getPref().myAlias))
				continue;
			
			Transformer trans = new Transformer(true);
			trans.add(new Regex("@@LOGID@@", ""));
			trans.add(new Regex("@@LOGDATE@@", log.getDate()));
			trans.add(new Regex("@@LOGTYPE@@", image2TypeText(log.getIcon())));
			trans.add(new Regex("@@LOGFINDERID@@", ""));
			trans.add(new Regex("@@LOGFINDER@@", SafeXML.cleanGPX(log.getLogger())));
			trans.add(new Regex("@@LOGENCODE@@", ""));
			trans.add(new Regex("@@LOGTEXT@@", SafeXML.cleanGPX(log.getMessage())));
			ret.append(trans.replaceAll(GPXLOG));
		}
		
		return ret.toString();
	}

	public String formatHeader() {
		Transformer trans = new Transformer(true);
		trans.add(new Regex("@@CREATEDATE@@", new Date()
				.setFormat("yyyy-MM-dd").toString()));
		return trans.replaceFirst(GPXHEADER);
	}

	public String formatLongDescription(CacheHolder ch) {
		if (ch.isAddiWpt() || ch.getType() == CacheType.CW_TYPE_CUSTOM) {
			return ch.details.LongDescription;
		} else {
			StringBuffer ret = new StringBuffer();
			String delim = "";
			ret.append(ch.details.LongDescription);
			if (ch.is_HTML()) {
				delim = "<br />";
			} else {
				delim = "\n";
			}
			// FIXME: format is not quite right yet
			// FIXME: cut Addis off in GPXimporter otherwise people who use GPX to feed CacheWolf have them doubled
			if (ch.addiWpts.size() > 0) {
				if (ch.is_HTML()) {
					ret.append("\n\n<p>Additional Waypoints</p>");
				} else {
					ret.append("\n\nAdditional Waypoints\n");
				}

				Iterator iter = ch.addiWpts.iterator();
				while (iter.hasNext()) {
					CacheHolder addi = (CacheHolder) iter.next();
					Transformer trans = new Transformer(true);
					trans.add(new Regex("@@ADDIID@@", addi.getWayPoint()));
					trans.add(new Regex("@@ADDISHORT@@", addi.getCacheName()));
					trans.add(new Regex("@@ADDIDELIM@@", delim));
					trans.add(new Regex("@@ADDILAT@@", formatAddiLatLon(addi.pos)));
					trans.add(new Regex("@@ADDILON@@", ""));
					trans.add(new Regex("@@ADDILONG@@", addi.getFreshDetails().LongDescription));
					ret.append(trans.replaceAll(GPXADDIINMAIN));
				}
				ret.append(delim).append("\n");
			}
			return ret.toString();
		}
	}

	public static String image2TypeText(String image) {
		if (image.equals("icon_smile.gif"))
			return "Found it";
		if (image.equals("icon_sad.gif"))
			return "Didn't find it";
		if (image.equals("icon_note.gif"))
			return "Write note";
		if (image.equals("icon_enabled.gif"))
			return "Enable Listing";
		if (image.equals("icon_disabled.gif"))
			return "Temporarily Disable Listing";
		if (image.equals("icon_camera.gif"))
			return "Webcam Photo Taken";
		if (image.equals("11.png"))
			return "Webcam Photo Taken";
		if (image.equals("icon_attended.gif"))
			return "Attended";
		if (image.equals("green.gif"))
			return "Publish Listing";
		if (image.equals("icon_rsvp.gif"))
			return "Will Attend";
		if (image.equals("big_smile.gif"))
			return "Post Reviewer Note";
		if (image.equals("traffic_cone.gif"))
			return "Archive (show)";
		if (image.equals("icon_maint.gif"))
			return "Owner Maintenance";
		if (image.equals("icon_needsmaint.gif"))
			return "Needs Maintenance";
		if (image.equals("coord_update.gif"))
			return "Update Coordinates";

		return image;
	}

	private String formatAddiLatLon(CWPoint pos) {
		if (pos.isValid()) {
			return pos.toString();
		} else {
			return "N/S  __ � __ . ___ W/E ___ � __ . ___";
		}
	}

	/**
	 * dialog to set the GPX exporter options
	 */
	private class GpxExportNgForm extends Form {
		private CheckBoxGroup cbgExportType;
		private mCheckBox cbCompact, cbPqLike, cbMyFinds, cbCustomIcons,
				cbSeperateFiles, cbSendToGarmin, cbSmartId;
		private mButton btnOk, btnCancel;

		/**
		 * set up the form / dialog
		 */
		public GpxExportNgForm() {
			// TODO: get defaults from profile

			this.setTitle("GPX Export");

			cbgExportType = new CheckBoxGroup();

			cbCompact = new mCheckBox("Compact");
			cbCompact.setGroup(cbgExportType);

			cbPqLike = new mCheckBox("PQ like");
			cbPqLike.setGroup(cbgExportType);

			cbMyFinds = new mCheckBox("MyFinds");
			cbMyFinds.setGroup(cbgExportType);

			cbgExportType.setText("Compact");

			cbCustomIcons = new mCheckBox("custom icons");

			cbSeperateFiles = new mCheckBox("one file per type");

			cbSendToGarmin = new mCheckBox("send to Garmin GPSr");
			cbSendToGarmin.modify(Control.Disabled, 0); // not yet

			cbSmartId = new mCheckBox("use smart IDs");

			btnOk = new mButton("OK");
			btnCancel = new mButton("Cancel");

			addNext(cbCustomIcons);
			addLast(cbCompact);
			addNext(cbSeperateFiles);
			addLast(cbPqLike);
			addNext(cbSendToGarmin);
			addLast(cbMyFinds);
			addLast(cbSmartId);

			addButton(btnOk);
			addButton(btnCancel);
		}

		/**
		 * react to GUI events and toogle access to the checkboxes according to
		 * radio button settings pass everything else to <code>super()</code>
		 */
		public void onEvent(Event ev) {
			if (ev instanceof ControlEvent && ev.type == ControlEvent.PRESSED) {

				if (ev.target == cbgExportType) {
					if (cbgExportType.getSelected() == cbCompact) {
						if (cbCustomIcons.change(0, Control.Disabled))
							cbCustomIcons.repaint();
						if (cbSeperateFiles.change(0, Control.Disabled))
							cbSeperateFiles.repaint();
						// if (cbSendToGarmin.change(0,Control.Disabled))
						// cbSendToGarmin.repaint();
						if (cbSmartId.change(0, Control.Disabled))
							cbSmartId.repaint();
					} else if (cbgExportType.getSelected() == cbPqLike) {
						cbSeperateFiles.setState(false);
						if (cbCustomIcons.change(0, Control.Disabled))
							cbCustomIcons.repaint();
						if (cbSeperateFiles.change(Control.Disabled, 0))
							cbSeperateFiles.repaint();
						// if (cbSendToGarmin.change(0,Control.Disabled))
						// cbSendToGarmin.repaint();
						if (cbSmartId.change(0, Control.Disabled))
							cbSmartId.repaint();
					} else if (cbgExportType.getSelected() == cbMyFinds) {
						cbCustomIcons.setState(false);
						cbSeperateFiles.setState(false);
						cbSendToGarmin.setState(false);
						cbSmartId.setState(false);
						if (cbCustomIcons.change(Control.Disabled, 0))
							cbCustomIcons.repaint();
						if (cbSeperateFiles.change(Control.Disabled, 0))
							cbSeperateFiles.repaint();
						// if (cbSendToGarmin.change(Control.Disabled,0))
						// cbSendToGarmin.repaint();
						if (cbSmartId.change(Control.Disabled, 0))
							cbSmartId.repaint();
					}
				} else if (ev.target == btnOk) {
					close(1);
				} else if (ev.target == btnCancel) {
					close(-1);
				}
			}
			super.onEvent(ev);
		}

		/**
		 * get the export type the user selected
		 * 
		 * @return index of selected option in checkboxgroup
		 * @see GpxExportNg
		 */
		public int getExportType() {
			return cbgExportType.getSelectedIndex();
		}

		/**
		 * check if the user wants smart IDs
		 * 
		 * @return true for smart IDs, false otherwise
		 */
		public boolean getSmartIds() {
			return cbSmartId.state;
		}

		/**
		 * check if user wants to send output straight to a Garmin GPSr
		 * 
		 * @return true for GPSr transfer, false otherwise
		 */
		public boolean getSendToGarmin() {
			return cbSendToGarmin.state;
		}

		/**
		 * check if user wants custom icons
		 * 
		 * @return true if user wants custom icons, false otherwise
		 */
		public boolean getCustomIcons() {
			return cbCustomIcons.state;
		}

		/**
		 * check if user wants separate files (POI loader)
		 * 
		 * @return true for separate files, false for single file
		 */
		public boolean getSeparateFiles() {
			return cbSeperateFiles.state;
		}
	}
}
