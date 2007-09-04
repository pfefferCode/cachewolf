package CacheWolf;

import ewe.sys.*;
import ewe.ui.*;
import ewe.fx.*;
import ewe.io.IOException;
import ewe.util.*;

/**
 *	Implements the user interaction of the list view. Works together with myTableModel and TablePanel
 */
public class myTableControl extends TableControl{

	public Preferences pref;
	public Profile profile;
	public Vector cacheDB;
	public TablePanel tbp;
	private Menu mFull = new Menu(new String[]{
			MyLocale.getMsg(1021,"Open description"),
			MyLocale.getMsg(1010,"Goto"),
			MyLocale.getMsg(1019,"Enter"),
			"-",
			MyLocale.getMsg(1020,"Open in $browser online"),
			MyLocale.getMsg(1018,"Open in browser offline"),
			"-",
			MyLocale.getMsg(1011,"Filter"),
			MyLocale.getMsg(1012,"Delete"),
			MyLocale.getMsg(1014,"Update"),
			"-",
			MyLocale.getMsg(1015,"Select all"),
			MyLocale.getMsg(1016,"De-select all")},
			MyLocale.getMsg(1013,"With selection"));
	private Menu mSmall = new Menu(new String[]{
			MyLocale.getMsg(1021,"Open description"),
			MyLocale.getMsg(1010,"Goto"),
			MyLocale.getMsg(1019,"Enter"),
			"-",
			MyLocale.getMsg(1020,"Open in $browser online"),
			MyLocale.getMsg(1018,"Open in browser offline")},
			MyLocale.getMsg(1013,"With selection"));

	myTableControl(TablePanel tablePanel) {
		profile=Global.getProfile();
		cacheDB = profile.cacheDB;
		pref = Global.getPref();
		tbp =tablePanel;
		allowDragSelection = false; // allow only one row to be selected at one time
	}

	/** Full menu when listview includes checkbox */
	public void setMenuFull() {
		setMenu(mFull);
		if (!Vm.getPlatform().equals("Win32") && !Vm.getPlatform().equals("Java"))
		   ((MenuItem)mFull.items.get(5)).modifiers|=MenuItem.Disabled;
	}
	
	/** Small menu when listview does not include checkbox */
	public void setMenuSmall() {
		setMenu(mSmall);
		if (!Vm.getPlatform().equals("Win32") && !Vm.getPlatform().equals("Java"))
			   ((MenuItem)mSmall.items.get(5)).modifiers|=MenuItem.Disabled;
	}
	
	public void penRightReleased(Point p){
		if (cacheDB.size()>0) // No context menu when DB is empty
			menuState.doShowMenu(p,true,null); // direct call (not through doMenu) is neccesary because it will exclude the whole table
	}
	public void penHeld(Point p){
		if (cacheDB.size()>0) // No context menu when DB is empty
			menuState.doShowMenu(p,true,null); 
	}

	public void onKeyEvent(KeyEvent ev) {
		if (ev.type == KeyEvent.KEY_PRESS && ev.target == this){
			if ( (ev.modifiers & IKeys.CONTROL) > 0 && ev.key == 1){ // <ctrl-a> gives 1, <ctrl-b> == 2
				// select all on <ctrl-a>
				setSelectForAll(true);
				ev.consumed = true;
			}
			else if (ev.key == IKeys.HOME) Global.mainTab.tbP.selectRow(0); //  cursorTo(0,cursor.x+listMode,true);
			else if (ev.key == IKeys.END) Global.mainTab.tbP.selectRow(model.numRows-1); //cursorTo(model.numRows-1,cursor.x+listMode,true);
			else if (ev.key == IKeys.PAGE_DOWN) Global.mainTab.tbP.selectRow(java.lang.Math.min(cursor.y+ getOnScreen(null).height-1, model.numRows-1)); //cursorTo(java.lang.Math.min(cursor.y+ getOnScreen(null).height-1, model.numRows-1),cursor.x+listMode,true); // I don't know why this doesn't work: tbp.doScroll(IScroll.Vertical, IScroll.PageHigher, 1);
			else if (ev.key == IKeys.PAGE_UP) Global.mainTab.tbP.selectRow(java.lang.Math.max(cursor.y-getOnScreen(null).height+1, 0)); // cursorTo(java.lang.Math.max(cursor.y-getOnScreen(null).height+1, 0),cursor.x+listMode,true);
			else if (ev.key == IKeys.ACTION || ev.key == IKeys.ENTER) Global.mainTab.select(Global.mainTab.descP);
			else if (ev.key == IKeys.DOWN) Global.mainTab.tbP.selectRow(java.lang.Math.min(cursor.y+ 1, model.numRows-1)); 
			else if (ev.key == IKeys.UP) Global.mainTab.tbP.selectRow(java.lang.Math.max(cursor.y-1, 0));
			else if (ev.key == 6 ) MainMenu.search(); // (char)6 == ctrl + f 
			else super.onKeyEvent(ev);
		}
		else super.onKeyEvent(ev);
	}

	/** Set all caches either as selected or as deselected, depending on argument */
	private void setSelectForAll(boolean selectStatus) {
		Global.getProfile().setSelectForAll(selectStatus);
		tbp.refreshTable();
	}

	public void popupMenuEvent(Object selectedItem){
		CacheHolder ch;

		if (selectedItem.toString().equals(MyLocale.getMsg(1015,"Select all"))){
			setSelectForAll(true);
		}

		if (selectedItem.toString().equals(MyLocale.getMsg(1016,"De-select all"))){
			setSelectForAll(false);
		}

		if (selectedItem.toString().equals(MyLocale.getMsg(1011,"Filter"))){
			for(int i = cacheDB.size()-1; i >=0; i--){
				ch = (CacheHolder)cacheDB.get(i);
				// incremental filter. Keeps status of all marked caches and
				// adds unmarked caches to filtered list
				ch.is_filtered = !ch.is_Checked || ch.is_filtered;
			}
			tbp.refreshTable();
		}
		if (selectedItem.toString().equals(MyLocale.getMsg(1012,"Delete"))){
			if ((new MessageBox(MyLocale.getMsg(144,"Warnung"),MyLocale.getMsg(1022, "Delete all caches that have a tick?"), MessageBox.YESB | MessageBox.NOB)).execute() != Form.IDYES) return;
			DataMover dm=new DataMover();
			Vm.showWait(true);
			for(int i = cacheDB.size()-1; i >=0; i--){
				ch = (CacheHolder)cacheDB.get(i);
				if(ch.is_Checked == true) {
					dm.deleteCacheFiles(ch.wayPoint,profile.dataDir);
					cacheDB.remove(ch);
				}
			}
			Vm.showWait(false);
			profile.saveIndex(pref,true);	
			tbp.refreshTable();
		}

		if (selectedItem.toString().equals(MyLocale.getMsg(1014,"Update"))){
			SpiderGC spider = new SpiderGC(pref, profile, false);
			OCXMLImporter ocSync = new OCXMLImporter(pref, profile);
			//Vm.debug("ByPass? " + profile.byPassIndexActive);
			Vm.showWait(true);
			boolean alreadySaid = false;
			boolean alreadySaid2 = false;
			boolean test = true;
			InfoBox infB = new InfoBox("Info", "Loading", InfoBox.PROGRESS_WITH_WARNINGS);
			infB.exec();
			for(int i = 0; i <	cacheDB.size(); i++){
				ch = (CacheHolder)cacheDB.get(i);
				if(ch.is_Checked == true) {
					if ( ch.wayPoint.length()>1 && (ch.wayPoint.substring(0,2).equalsIgnoreCase("GC") 
							 					 || ch.wayPoint.substring(0,2).equalsIgnoreCase("OC")))
//						if ( (ch.wayPoint.length() > 1 && ch.wayPoint.substring(0,2).equalsIgnoreCase("GC")))
//						Notiz: Wenn es ein addi Wpt ist, sollte eigentlich der Maincache gespidert werden
//						Alter code pr�ft aber nur ob ein Maincache von GC existiert und versucht dann den addi direkt zu spidern, was nicht funktioniert
//						TODO: Diese Meldungen vor dem Einloggen darstellen						
					{
					    infB.setInfo("Loading: " + ch.wayPoint);
					    infB.redisplay();
					    if (ch.wayPoint.substring(0,2).equalsIgnoreCase("GC"))   
					    	test = spider.spiderSingle(i, infB);
					    else  
					    	test = ocSync.syncSingle(i, infB);
					    if (!test) {
							infB.close(0);
							break;
						} else 
							profile.hasUnsavedChanges=true;	
					} else { 
						if (ch.isAddiWpt() && !ch.mainCache.is_Checked && !alreadySaid2) { // Is the father ticked?
							alreadySaid2=true;
							(new MessageBox("Information","Hilfswegpunkte k�nnnen nicht direkt gespidert werden\nBitte zus�tzlich den Vater anhaken", MessageBox.OKB)).execute();
						}
						if (!ch.isAddiWpt() && !alreadySaid) {
							alreadySaid = true;
							(new MessageBox("Information",ch.wayPoint+ ": Diese Funktion steht gegenw�rtig nur f�r Geocaching.com und Opencaching.de zur Verf�gung", MessageBox.OKB)).execute();
			    		}
					}
				}

//				cacheDB.clear();
//				profile.readIndex();
			}
			infB.close(0);
//			profile.hasUnsavedChanges=true;	
			profile.saveIndex(pref,Profile.NO_SHOW_PROGRESS_BAR);
			profile.restoreFilter();
			profile.updateBearingDistance();
			tbp.refreshTable();
			Vm.showWait(false);
		}
		if (selectedItem.toString().equals(MyLocale.getMsg(1019,"Center"))){
			CacheHolder thisCache = (CacheHolder)cacheDB.get(tbp.getSelectedCache());
			CWPoint cp=new CWPoint(thisCache.LatLon);
			if (!cp.isValid()){
				MessageBox tmpMB = new MessageBox(MyLocale.getMsg(321,"Error"), MyLocale.getMsg(4111,"Coordinates must be entered in the format N DD MM.MMM E DDD MM.MMM"), MessageBox.OKB);
				tmpMB.execute();
			} else {				
				pref.curCentrePt.set(cp);
				Global.mainTab.updateBearDist(); // Update the distances with a warning message
				tbp.refreshTable();
			}
		}

		if (selectedItem.toString().equals(MyLocale.getMsg(1010,"Goto"))){
			ch = (CacheHolder)cacheDB.get(tbp.getSelectedCache());
			Global.mainTab.gotoPoint(ch.LatLon);
		}
		if (selectedItem.toString().equalsIgnoreCase(MyLocale.getMsg(1020,"Open online in Browser"))){
			ch = (CacheHolder)cacheDB.get(tbp.getSelectedCache());
			CacheHolderDetail chD=new CacheHolderDetail(ch);
			try{
				chD.readCache(profile.dataDir);
			}catch(IOException ex){	(new MessageBox(MyLocale.getMsg(321,"Error"), "Cannot read cache data\n"+ex.toString()+"\nCache: "+ch.wayPoint,MessageBox.OKB)).execute(); }
			try {
				String cmd = "\""+pref.browser+ "\" \"" + chD.URL+"\"";
				Vm.exec(cmd);
			} catch (IOException ex) {
				(new MessageBox("Error", "Cannot start browser!\n"+ex.toString()+"\nThe are two possible reasons:\n * path to internet browser in \npreferences not correct\n * An bug in ewe VM, please be \npatient for an update",MessageBox.OKB)).execute();
			}
		}
		if (selectedItem.toString().equalsIgnoreCase(MyLocale.getMsg(1018,"Open in browser offline"))) {
			ShowCacheInBrowser sc=new ShowCacheInBrowser();
			sc.showCache(new CacheHolderDetail((CacheHolder)cacheDB.get(tbp.getSelectedCache())));
		}
		if (selectedItem.toString().equalsIgnoreCase(MyLocale.getMsg(1021,"Open description"))){
			penDoubleClicked(null);
		}

	}

	public void penDoubleClicked(Point where) {
		Global.mainTab.select(Global.mainTab.descP);
	}
	public void onEvent(Event ev) {
	    if (ev instanceof PenEvent) Global.mainTab.tbP.myMod.penEventModifiers=((PenEvent)ev).modifiers;
		super.onEvent(ev);
	}
    ///////////////////////////////////////////////////
	//  Allow the caches to be dragged into a cachelist
    ///////////////////////////////////////////////////
	
	IconAndText imgDrag;
	String wayPoint;
	int row;
	
	public void startDragging(DragContext dc) {//TODO Dragging of header widths
		 Vector cacheDB=Global.getProfile().cacheDB;
		 Point p=cellAtPoint(dc.start.x,dc.start.y,null);
		 wayPoint=null;
		 if (p.y>=0) { 
			 row=p.y;
			 CacheHolder ch=(CacheHolder)cacheDB.get(p.y);
			 wayPoint=ch.wayPoint;
			 //Vm.debug("Waypoint : "+ch.wayPoint);
			 imgDrag=new IconAndText();
			 imgDrag.addColumn((IImage) Global.mainTab.tbP.myMod.cacheImages[Convert.parseInt(ch.type)]);
			 imgDrag.addColumn(ch.wayPoint);
			 dc.dragData=dc.startImageDrag(imgDrag,new Point(8,8),this);
		 } else super.startDragging(dc);
	 }

	 public void stopDragging(DragContext dc) {
		 if (wayPoint!=null) {
			 //Vm.debug("Stop  Dragging"+dc.curPoint.x+"/"+dc.curPoint.y);
			 dc.stopImageDrag(true);
			 Point p = Gui.getPosInParent(this,getWindow());
			 p.x += dc.curPoint.x;
			 p.y += dc.curPoint.y;
			 Control c = getWindow().findChild(p.x,p.y);
		     if (c instanceof mList && c.text.equals("CacheList")) {
		    	 if (Global.mainForm.cacheList.addCache(wayPoint)) {
		    		 c.repaintNow();
		    		 ((mList) c).makeItemVisible(((mList)c).itemsSize()-1);
		    	 }
		     }
		     Global.mainTab.tbP.selectRow(row);
			 //Vm.debug("Control "+c.toString()+"/"+c.text);
		 }else super.stopDragging(dc);
	 }
	 
	 public void dragged(DragContext dc) {
	 	if (wayPoint!=null)
		   dc.imageDrag();
	 	else
	 		super.dragged(dc);
	 }

}
