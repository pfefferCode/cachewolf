    /*
    GNU General Public License
    CacheWolf is a software for PocketPC, Win and Linux that
    enables paperless caching.
    It supports the sites geocaching.com and opencaching.de

    Copyright (C) 2006  CacheWolf development team
    See http://developer.berlios.de/projects/cachewolf/
    for more information.
    Contact: 	bilbowolf@users.berlios.de
    			kalli@users.berlios.de

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; version 2 of the License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
    */
package CacheWolf;

public final class OC {

	/** thou shallst not instantiate this object */
	private OC() {
		// Nothing to do
	}

	public final static int OC_HOSTNAME = 0; 
	public final static int OC_PREFIX = 1; 
	public final static String[][] OCSites = {
		{"www.opencaching.de", "OC"},
		{"www.opencaching.pl", "OP"},
		{"www.opencaching.cz", "OZ"},
		{"www.opencaching.org.uk", "OK"},
		{"www.opencaching.se", "OS"},
		{"www.opencaching.no", "ON"},
		{"www.opencaching.us", "OU"}
		};

	public final static String[] OCHostNames() {
		String[] ret=new String[OCSites.length];
		for (int i = 0; i < OCSites.length; i++) {
			ret[i]=OCSites[i][OC_HOSTNAME];
		}
		return ret;
	}
	
	public final static String getOCHostName(String wpName){
		for (int i = 0; i < OCSites.length; i++) {
			if(wpName.startsWith(OCSites[i][OC_PREFIX])) {
				return OCSites[i][OC_HOSTNAME];
			}
		}
		return null;
	}
	
	public final static boolean isOC(String wpName) {
		return (getOCHostName(wpName.toUpperCase()) != null);		
	}
	
	public final static int getSiteIndex(String site) {
		for (int i = 0; i < OCSites.length; i++) {
			if(site.equalsIgnoreCase(OCSites[i][OC_HOSTNAME])) {
				return i;
			}
		}
		return 0; // don't get a fault
	}
}