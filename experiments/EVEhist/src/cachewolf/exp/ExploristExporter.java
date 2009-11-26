package cachewolf.exp;

import eve.sys.*;
import eve.ui.filechooser.FileChooser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import eve.io.File;
import java.io.*;
import eve.ui.ProgressBarForm;
import java.util.*;
import java.io.IOException;

import cachewolf.*;
import cachewolf.utils.Common;


/**
 * @author Kalle
 * @author TweetyHH Class for Exporting direct to Magellans *.gs Files. Caches
 *         will be exported in files with maximum of 200 Caches.
 */

public class ExploristExporter {
	// starts with no ui for file selection
	final static int TMP_FILE = 0;
	// brings up a screen to select a file
	final static int ASK_FILE = 1;

	// selection, which method should be called
	final static int NO_PARAMS = 0;
	final static int LAT_LON = 1;
	final static int COUNT = 2;

	Vector cacheDB;
	Preferences pref;
	Profile profile;
	// mask in file chooser
	String mask = "*.gs";
	// decimal separator for lat- and lon-String
	//char decimalSeparator = '.';
	// if true, the complete cache details are read
	// before a call to the record method is made
	boolean needCacheDetails = true;

	// name of exporter for saving pathname
	String expName;

	public ExploristExporter(Preferences p, Profile prof) {
		profile = prof;
		pref = p;
		cacheDB = profile.cacheDB;
		expName = this.getClass().getName();
		// remove package
		expName = expName.substring(expName.indexOf(".") + 1);
	}

	public void doIt() {
		File configFile = new File("magellan.cfg");
		if (configFile.exists()) {
			FileChooser fc = new FileChooser(FileChooser.DIRECTORY_SELECT, pref.getExportPath(expName+"Dir"));
			fc.title=(MyLocale.getMsg(2104, "Choose directory for exporting .gs files"));
			String targetDir;
			if(fc.execute() != FileChooser.IDCANCEL){
				targetDir = fc.getChosen() + "/";
				pref.setExportPath(expName+"Dir", targetDir);

				CWPoint centre = profile.centre;
				try {
					LineNumberReader reader = new LineNumberReader(new BufferedReader(new FileReader(configFile.getName())));
					String line, fileName, coordinate;
					while ((line = reader.readLine()) != null)  {
						StringTokenizer tokenizer = new StringTokenizer(line,"=");
						fileName = targetDir + tokenizer.nextToken().trim() + ".gs";
						coordinate = tokenizer.nextToken().trim();
						CWPoint point = new CWPoint(coordinate);
						DistanceComparer dc = new DistanceComparer(point);
						eve.util.Utils.sort(new Handle(),cacheDB,dc, false);
						doIt(fileName);
					}
					reader.close();
				} catch (FileNotFoundException e) {
					InfoBox info = new InfoBox(MyLocale.getMsg(2100, "Magellan Exporter"),MyLocale.getMsg(2101, "Failure at loading magellan.cfg\n" + e.getMessage()));
					info.show();
				} catch (IOException e) {
					InfoBox info = new InfoBox(MyLocale.getMsg(2100, "Magellan Exporter"),MyLocale.getMsg(2103, "Failure at reading magellan.cfg\n" + e.getMessage()));
					info.show();
				} finally {
					eve.util.Utils.sort(new Handle(), cacheDB, new DistanceComparer(centre),false);
				}
			}
		}
		else {
			doIt(null);
		}
	}
	
	/**
	 * Does the most work for exporting data
	 */
	public void doIt(String baseFileName) {
		File outFile;
		String fileBaseName;
		String str = null;
		CacheHolder ch;
		CacheHolderDetail chD;
		ProgressBarForm pbf = new ProgressBarForm();
		Handle h = new Handle();

		if (baseFileName == null) {
			outFile = getOutputFile();
			if (outFile == null)
				return;
		} else {
			outFile = new File(baseFileName);
		}

		fileBaseName = outFile.getFullPath();
		// cut .gs
		fileBaseName = fileBaseName.substring(0, fileBaseName.length() - 3);

		pbf.showMainTask = false;
		pbf.setTask(h, "Exporting ...");
		pbf.exec();

		int counter = 0;
		int expCount = 0;
		for (int i = 0; i < cacheDB.size(); i++) {
			ch = (CacheHolder) cacheDB.get(i);
			if (ch.is_black == false && ch.is_filtered == false)
				counter++;
		}

		try {
			PrintWriter outp = null;
			for (int i = 0; i < cacheDB.size(); i++) {
				ch = (CacheHolder) cacheDB.get(i);
				if (ch.is_black == false && ch.is_filtered == false) {
					// all 200 caches we need a new file
					if (expCount % 200 == 0) {
						if (outp != null) {
							outp.close();
						}
						outp = new PrintWriter(new BufferedWriter(
								new FileWriter(new java.io.File(fileBaseName + expCount
										/ 200 + ".gs"))));
					}

					chD = new CacheHolderDetail(ch);
					expCount++;
					h.progress = (float) expCount / (float) counter;
					h.changed();
					try {
						if (needCacheDetails) {
							chD.readCache(profile.dataDir);
						}
					} catch (IOException e) {
						continue;
					}
					str = record(chD);
					if (str != null)
						outp.print(str);
				}// if

			}// for
			str = trailer();

			if (str != null)
				outp.print(str);

			outp.close();
			pbf.exit(0);
		} catch (IOException ioE) {
			Vm.debug("Error opening " + outFile.getName());
		}
		// try
	}

	/**
	 * uses a filechooser to get the name of the export file
	 * 
	 * @return
	 */
	public File getOutputFile() {
		eve.io.File file;
		FileChooser fc = new FileChooser(FileChooser.SAVE, pref
				.getExportPath(expName));
		fc.title=(MyLocale.getMsg(2102, "Select target file:"));
		fc.addMask(mask);
		if (fc.execute() != FileChooser.IDCANCEL) {
			file = fc.getChosenFile();
			pref.setExportPath(expName, file.getDrivePath());
			return file;
		} 
		return null;
	}

	/**
	 * this method can be overided by an exporter class
	 * 
	 * @param ch
	 *            cachedata
	 * @return formated cache data
	 */
	public String record(CacheHolderDetail chD) {
		/*
		static protected final int GC_AW_PARKING = 50;
		static protected final int GC_AW_STAGE_OF_MULTI = 51;
		static protected final int GC_AW_QUESTION = 52;
		static protected final int GC_AW_FINAL = 53;
		static protected final int GC_AW_TRAILHEAD = 54;
		static protected final int GC_AW_REFERENCE = 55;
		*/
		StringBuffer sb = new StringBuffer();
		sb.append("$PMGNGEO,");
		sb.append(chD.pos.getLatDeg(CWPoint.DMM));
		sb.append(chD.pos.getLatMin(CWPoint.DMM));
		sb.append(",");
		sb.append("N,");
		sb.append(chD.pos.getLonDeg(CWPoint.DMM));
		sb.append(chD.pos.getLonMin(CWPoint.DMM));
		sb.append(",");
		sb.append("E,");
		sb.append("0000,"); // Height
		sb.append("M,"); // in meter
		sb.append(chD.wayPoint);
		sb.append(",");
		String add = "";
		if (chD.isAddiWpt()) {
			if (chD.type==50) {
				add = "Pa:";
			} else if (chD.type==51) {
				add = "St:";
			} else if (chD.type==52) {
				add = "Qu:"; 
			} else if (chD.type==53) {	
				add = "Fi:";
			} else if (chD.type==54) {
				add = "Tr:";
			} else if (chD.type==55) {	
				add = "Re:";
			}
			sb.append(add).append(removeCommas(chD.cacheName));
		} else {
			sb.append(removeCommas(chD.cacheName));
		}		
		sb.append(",");
		sb.append(removeCommas(chD.cacheOwner));
		sb.append(",");
		sb.append(removeCommas(Common.rot13(chD.hints)));
		sb.append(",");
		
		if (!add.equals("")) { // Set Picture in Explorist to Virtual
			sb.append("Virtual Cache");
		} else if (!(chD.type==8)) { // Rewrite Unknown Caches
			sb.append(CacheType.transType(chD.type));
		} else {
			sb.append("Mystery Cache");
		}
		sb.append(",");
		sb.append(toGsDateFormat(chD.dateHidden));  // created - DDMMYYY, YYY = year - 1900
		sb.append(",");
		String lastFound = "0000";
		for (int i = 0; i < chD.cacheLogs.size(); i++) {
			if (chD.cacheLogs.getLog(i).isFoundLog() && chD.cacheLogs.getLog(i).getDate().compareTo(lastFound) > 0 ) {
				lastFound = chD.cacheLogs.getLog(i).getDate();
			}
		}
		
		sb.append(toGsDateFormat(lastFound)); // lastFound - DDMMYYY, YYY = year - 1900
		sb.append(",");
		sb.append(removeCommas(chD.hard));
		sb.append(",");
		sb.append(removeCommas(chD.terrain));
		sb.append("*41");
		return Exporter.simplifyString(sb.toString() + "\r\n");
	}

	/**
	 * this method can be overided by an exporter class
	 * 
	 * @return formated trailer data
	 */
	public String trailer() {
		return "$PMGNCMD,END*3D\n";
	}
	
	/**
	 * Changes "," in "." in the input String
	 * @param input
	 * @return changed String
	 */
	private String removeCommas(String input) {
		return input.replace(',', '.');
	}
	
	/**
	 * change the Dateformat from "yyyy-mm-dd" to ddmmyyy, where yyy is years after 1900 
	 * @param input Date in yyyy-mm-dd
 	 * @return Date in ddmmyyy
	 */
	private String toGsDateFormat(String input) {
		if (input.length() >= 10) {
			return input.substring(8, 10) + input.substring(5, 7) + "1" + input.substring(2, 4);
		} 
		return "";
	}
	
}