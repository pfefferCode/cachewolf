package CacheWolf;

import ewe.ui.*;
import ewe.graphics.*;
import ewe.io.IOException;
import ewe.sys.*;
import ewe.fx.*;
import ewe.util.Vector;

/**
*	Class to handle a moving map.
*/
public class MovingMap extends Form{
	Preferences pref;
	MovingMapPanel mmp;
	AniImage mapImage;
	Vector maps;
	GotoPanel gotoPanel;
	Vector cacheDB;
	MapInfoObject currentMap;
	AniImage statusImageHaveSignal = new AniImage("center_green.png");
	AniImage statusImageNoSignal = new AniImage("center_yellow.png");
	AniImage statusImageNoGps = new AniImage("center.png");
	AniImage arrowUp = new AniImage("arrow_up.png");
	AniImage arrowDown = new AniImage("arrow_down.png");
	AniImage arrowLeft = new AniImage("arrow_left.png");
	AniImage arrowRight = new AniImage("arrow_right.png");
	AniImage posCircle = new AniImage("position.png");
	int centerx,centery = 0;
	
	public MovingMap(Preferences pref, Vector maps, GotoPanel gP, Vector cacheDB){
		this.cacheDB = cacheDB;
		gotoPanel = gP;
		this.maps = maps;
		this.pref = pref;
		this.setPreferredSize(pref.myAppWidth, pref.myAppHeight);
		this.title = "Moving Map";
		currentMap = new MapInfoObject();
		mmp = new MovingMapPanel(this, maps, gotoPanel, cacheDB);
		this.addLast(mmp);
	}
	
	public void loadMap(){
		//Create index of all world files
		//Create form
//		if(gotoPanel.toPoint.latDec == 0 && gotoPanel.toPoint.latDec == 0 && maps.size()>0){
			try{
				statusImageNoGps.setLocation(10,10);
				statusImageNoGps.properties = AniImage.AlwaysOnTop;
				arrowUp.setLocation(pref.myAppWidth/2, 10);
				arrowDown.setLocation(pref.myAppWidth/2, pref.myAppHeight-20);
				arrowLeft.setLocation(10, pref.myAppHeight/2+7);
				arrowRight.setLocation(pref.myAppWidth-25, pref.myAppHeight/2+7);
				arrowUp.properties = AniImage.AlwaysOnTop;
				arrowDown.properties = AniImage.AlwaysOnTop;
				arrowLeft.properties = AniImage.AlwaysOnTop;
				arrowRight.properties = AniImage.AlwaysOnTop;
				mmp.addImage(arrowUp);
				mmp.addImage(arrowDown);
				mmp.addImage(arrowLeft);
				mmp.addImage(arrowRight);
				mmp.addImage(statusImageNoGps);
				centerx = pref.myAppWidth/2;
				centery = pref.myAppHeight/2;
				// GPS has been switched on
				//This means we display the correct map if we have a fix
				//if(gotoPanel.displayTimer != 0){
				//Vm.debug("Und: " +gotoPanel.gpsPosition.latDec);
				if(gotoPanel.gpsPosition.latDec != 0){
					ListBox l = new ListBox(maps, true, gotoPanel.gpsPosition);
					if (l.execute()==FormBase.IDOK){
						posCircle.setLocation(pref.myAppWidth/2-10,pref.myAppHeight/2-10);
						posCircle.properties = AniImage.AlwaysOnTop;
						mmp.addImage(posCircle);
						
						mapImage = new AniImage(l.selectedMap.fileName);
						this.title = l.selectedMap.mapName;
						this.currentMap = l.selectedMap;
						updatePosition(gotoPanel.gpsPosition.latDec, gotoPanel.gpsPosition.lonDec);
						mmp.addImage(mapImage);
						mmp.setMap(mapImage);
						this.repaintNow();
					}
					else this.currentMap = null;  
				}else{ //Default: display the first map in the list.
					try {
						MapInfoObject mo = (MapInfoObject)maps.get(0);
						currentMap = mo;
						mapImage = new AniImage(mo.fileName);
						this.title = "Mov. Map: " + mo.mapName;
						mapImage.setLocation(0,0);
						mmp.addImage(mapImage);
						mmp.setMap(mapImage);
					} catch (IndexOutOfBoundsException ex) { // wird von maps.get geworfen, wenn die Liste der Maps leer ist
						Locale l = Vm.getLocale();
						LocalResource lr = l.getLocalResource("cachewolf.Languages",true);
						MessageBox tmpMB = new MessageBox((String)lr.get(321, "Error"), (String)lr.get(326, "Es steht keine kalibrierte Karte zur Verf�gung"), MessageBox.OKB);
						tmpMB.execute();
					}
				}
			}catch (NumberFormatException ex){ // veraltet - hier vielleicht auch einen MemoryError behandlung hin?
				Vm.debug("Problem loading map image file!");
			}
	//	}
	}
	
	/**
	* Method to calculate bitmap x,y of the current map using
	* lat and lon target coordinates
	*/
	public int[] calcMapXY(double lat, double lon){
		//x_ = affine[0]*x + affine[2]*y + affine[4]; lat
		//y_ = affine[1]*x + affine[3]*y + affine[5]; lon
		
		// Benutze Cramersche Regel: http://de.wikipedia.org/wiki/Cramersche_Regel
		Matrix matrix = new Matrix(2,2);
		double mapx,mapy;
		int coords[] = new int[2];
		double a[][] = new double[2][2];
		double b[] = new double[2];
		double a1[][] = new double[2][2];
		double a2[][] = new double[2][2];
		a[0][0] = currentMap.affine[0]; a[0][1] = currentMap.affine[2];
		a[1][0] = currentMap.affine[1]; a[1][1] = currentMap.affine[3];
		b[0] = lat - currentMap.affine[4];
		b[1] = lon - currentMap.affine[5];
		a1[0][0] = b[0]; a1[0][1] = a[0][1];
		a1[1][0] = b[1]; a1[1][1] = a[1][1];
		a2[0][0] = a[0][0]; a2[0][1] = b[0];
		a2[1][0] = a[1][0]; a2[1][1] = b[1];
		mapx = matrix.Determinant(a1)/matrix.Determinant(a);
		mapy = matrix.Determinant(a2)/matrix.Determinant(a);
		coords[0] = (int)mapx;
		coords[1] = (int)mapy;
		return coords;
	}
	
	
	/**
	* Method to reset the position of the moving map.
	*/
	public void updatePosition(double lat, double lon){
		if(lat != 0 && lon != 0 && currentMap != null){
			int pos[] = new int[2];
			int posy,posx = 0;
			pos = calcMapXY(lat, lon);
			posy = centery - pos[1];
			posx = centerx - pos[0];
			//Vm.debug("mapx = " + mapx);
			//Vm.debug("mapy = " + mapy);
			mapImage.move(posx,posy);
			mmp.repaintNow();
		}
	}
	
	public void onEvent(Event ev){
		if(ev instanceof FormEvent && ev.type == FormEvent.CLOSED){
			gotoPanel.runMovingMap = false;
			//gotoPanel.stopTheTimer();
		}
		super.onEvent(ev);
	}
}

/**
*	Class to display the map bitmap and to select another bitmap to display.
*/
class MovingMapPanel extends InteractivePanel{
	MovingMap mm;
	Vector maps;
	CellPanel gotoPanel;
	AniImage mapImage;
	Vector cacheDB;
	public MovingMapPanel(MovingMap f, Vector maps, GotoPanel gP, Vector cacheDB){
		this.cacheDB = cacheDB;
		gotoPanel = gP;
		this.mm = f;
		this.maps = maps;
	}
	
	public void setMap(AniImage map){
		mapImage = map;
	}
	/**
	*	Method to react to user.
	*/
	public void imageClicked(AniImage which, Point pos){
		if(which == mm.statusImageNoGps){
			ListBox l = new ListBox(maps, false, null);
//			l.execute();
//			if(l.selected == true){
			if(l.execute() == FormBase.IDOK){
				this.removeImage(mapImage);
				try{
					Vm.debug("Trying map: " + l.selectedMap.fileName);
					mapImage = new AniImage(l.selectedMap.fileName);
					mm.title = l.selectedMap.mapName;
					mm.currentMap = l.selectedMap;
					//Go through cache db to paint caches that are in bounds of the map
					/*
					CWPoint tempPoint;
					CacheHolder ch = new CacheHolder();
					Graphics g = new Graphics(mapImage);
					for(int i = 0; i < cacheDB.size();i++){
						ch = (CacheHolder)cacheDB.get(i);
						tempPoint = new CWPoint(ch.LatLon, CWPoint.CW);
						if(mm.currentMap.inBound(tempPoint) == true) { //yes cache is on map!
							
						}
					}
					g.free();
					*/
					mapImage.setLocation(0,0);
					this.addImage(mapImage);
				}catch (Exception ex){
					Vm.debug("Problem loading map image file!" +ex.toString());
				}
				this.repaintNow();
			}
		}
		if(which == mm.arrowRight){
			Point p = new Point();
			p = mapImage.getLocation(null);
			mapImage.move(p.x-10,p.y);
			this.repaintNow();
		}
		if(which == mm.arrowLeft){
			Point p = new Point();
			p = mapImage.getLocation(null);
			mapImage.move(p.x+10,p.y);
			this.repaintNow();
		}
		if(which == mm.arrowDown){
			Point p = new Point();
			p = mapImage.getLocation(null);
			mapImage.move(p.x,p.y-10);
			this.repaintNow();
		}
		if(which == mm.arrowUp){
			Point p = new Point();
			p = mapImage.getLocation(null);
			mapImage.move(p.x,p.y+10);
			this.repaintNow();
		}
	}
}

/**
*	Class to display maps to choose from
*/
class ListBox extends Form{
	public MapInfoObject selectedMap = new MapInfoObject();
	mButton cancelButton, okButton;
	mList list = new mList(4,1,false);
	public boolean selected = false;
	Vector maps;
	
	public ListBox(Vector maps, boolean showInBoundOnly, CWGPSPoint position){
		this.title = "Maps";
		this.setPreferredSize(200,100);
		this.maps = maps;
		MapInfoObject map;
		ScrollBarPanel scb;
		for(int i = 0; i<maps.size();i++){
			map = new MapInfoObject();
			map = (MapInfoObject)maps.get(i);
			if(showInBoundOnly == true) {
				if(map.inBound(position) == true) list.addItem(i + ": " + map.mapName);
			} else list.addItem(i + ": " + map.mapName);
		}
		this.addLast(scb = new ScrollBarPanel(list),this.STRETCH, this.FILL);
		this.addNext(cancelButton = new mButton("Cancel"),this.STRETCH, this.FILL);
		this.addLast(okButton = new mButton("Select"),this.STRETCH, this.FILL);
	}
	
	public void onEvent(Event ev){
		if(ev instanceof ControlEvent && ev.type == ControlEvent.PRESSED){
			if (ev.target == cancelButton){
				selectedMap = null;
				selected = false;
				this.close(FormBase.IDCANCEL);
			}
			if (ev.target == okButton){
				selectedMap = null;
				int i,mapNum = 0;
				String it = new String();
				it = list.getText();
				if (it != ""){
					it = it.substring(0,it.indexOf(':'));
					mapNum = Convert.toInt(it);
				//	Vm.debug("Kartennummer: " + mapNum);
					selectedMap = (MapInfoObject)maps.get(mapNum);
					selected = true;
					this.close(FormBase.IDOK);
				}
				else {
					selected = false;
					this.close(FormBase.IDCANCEL);
				}
				
			}
		}
		super.onEvent(ev);
	}
}