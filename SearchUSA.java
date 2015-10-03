/* Author: Amrita Das
 * Unity ID: adas5
*/

import java.util.*;

class City{
    String name;
    double latitude, longitude;
    int ctid;
    ArrayList<Road> roads = new ArrayList<Road>();
    public City(String c, double lt, double lo, int id){
        name = c; 
        latitude = lt;
        longitude = lo;
        ctid = id;
    }
}

class Road{
	String roadTo;
	int roadDist;
	int roadToId;
	Road(String rt, int rti, int rd){
		roadTo = rt;
		roadToId = rti;
		roadDist = rd;
	}
}

class Path{
	double cost = 0.0;
	double clscost = 0.0;
	ArrayList<Integer> citys;
	Path(){
		citys = new ArrayList<Integer>();
	}
	void add(int ctid, double cst){
		citys.add(ctid);
		cost += cst;
	}
	void setCls(double cls){
		clscost = cls;
	}
	void copy(Path p){
		cost = p.cost;
		citys = (ArrayList<Integer>) p.citys.clone();
		clscost = p.clscost;
	}
	boolean equalsPath(Path p){
		if ((cost!= p.cost) || (clscost!=p.clscost))
			return false;
		else{
			int i=0;
			for(int x:p.citys){
				if(citys.get(i) != x)
					return false;
				i++;
			}
		}
		return true;
	}
}

class PathCompUni implements Comparator<Path>{
	@Override
	public int compare(Path p1, Path p2){
		if(p1.cost > p2.cost)
			return 1;
		else
			return -1;
	}
}

class PathCompGree implements Comparator<Path>{
	@Override
	public int compare(Path p1, Path p2){
		if(p1.clscost > p2.clscost)
			return 1;
		else
			return -1;
	}
}

class PathCompAStar implements Comparator<Path>{
	@Override
	public int compare(Path p1, Path p2){
		if((p1.cost + p1.clscost) > (p2.cost + p2.clscost))
			return 1;
		else
			return -1;
	}
}

public class SearchUSA {

	int noC = 112;
	City[] cities = new City[noC];
	int cityID = -1;
	String src, dest, type;
	int keep=1;
	int srccity = 0, destcity = 0;
	Path fndPath;
	ArrayList<Integer> expanded;
	public static void main(String[] args) {
        if(args.length<3){
            System.out.println("Not enough arguments");
            System.exit(1);
        }
        SearchUSA sus = new SearchUSA();
        sus.makeCities();
        sus.src = args[1];
        sus.dest = args[2];
        sus.type = args[0];
        sus.srccity = sus.checkCity(sus.src, 0, sus.noC-1);
        sus.destcity = sus.checkCity(sus.dest, 0, sus.noC-1);
        if(sus.srccity==-1 || sus.destcity==-1)
        	System.out.println("City not found on map");
        else if(!(sus.type.equalsIgnoreCase("uniform") || sus.type.equalsIgnoreCase("greedy") || sus.type.equalsIgnoreCase("astar"))){
        	System.out.println("Search type should be uniform, greedy or astar");
        }
        else{
        	sus.expanded = new ArrayList<Integer>();
        	sus.makeRoads();
        	if(sus.type.equalsIgnoreCase("uniform"))
            	sus.uniform(sus.srccity, sus.destcity);
            else if(sus.type.equalsIgnoreCase("greedy"))
            	sus.greedy(sus.srccity, sus.destcity);
            else if(sus.type.equalsIgnoreCase("astar"))
            	sus.astar(sus.srccity, sus.destcity);
        }
	}
	
	ArrayList<Road> expand(int node, Path parent){
		keep++;
		expanded.add(node);
		ArrayList<Road> children = new ArrayList<Road>();
		for(Road r:cities[node].roads){
			if(!parent.citys.contains(r.roadToId)){
				children.add(r);
			}
		}
		return children;
	}
	
	double getCls(int city){
    	double p1 = Math.pow(69.5 * (Math.abs(cities[city].latitude - cities[destcity].latitude)), 2);
		double p2 = 69.5 * (Math.abs(cities[city].longitude - cities[destcity].longitude));
		double p3 = Math.cos(3.14 * (cities[city].latitude + cities[destcity].latitude) * (1/360));
		double p = Math.sqrt(p1 + Math.pow(p2*p3, 2));
		return p;
	}
	
	void uniform(int srccity, int destcity){
		ArrayList<Path> ps = new ArrayList<Path>();
		fndPath = new Path();
		fndPath.cost=9999999.9;
		Path p1 = new Path();
		p1.add(srccity, 0.0);
		ps.add(p1);
		int node = srccity, parent = srccity;
		Path curr = p1;
		
		while(keep>0){
			ArrayList<Road> children = expand(node, curr);
			for(Road i:children){
				Path p = new Path();
				p.copy(curr);
				p.add(i.roadToId, (double)i.roadDist);
				ps.add(p);
				if(p.citys.contains(destcity))
					if(p.cost < fndPath.cost)
						fndPath.copy(p);
			}
			ps.remove(0);
			Collections.sort(ps, new PathCompUni());
			curr.copy(ps.get(0));
			node = curr.citys.get(curr.citys.size()-1);
			parent = curr.citys.get(curr.citys.size()-2);
			if(curr.equalsPath(fndPath))
				keep=0;
		}
		
		printResult();
	}
	
	void greedy(int srccity, int destcity){
		ArrayList<Path> ps = new ArrayList<Path>();
		fndPath = new Path();
		fndPath.cost=9999999.9;
		fndPath.clscost=9999999.9;
		Path p1 = new Path();
		p1.add(srccity, 0.0);
		p1.setCls(getCls(srccity));
		ps.add(p1);
		int node = srccity, parent = srccity;
		Path curr = p1;

		while(keep>0){
			ArrayList<Road> children = expand(node, curr);
			for(Road i:children){
				Path p = new Path();
				p.copy(curr);
				p.add(i.roadToId, (double)i.roadDist);
				p.setCls(getCls(i.roadToId));
				ps.add(p);
				if(p.citys.contains(destcity))
					if(p.clscost < fndPath.clscost)
						fndPath.copy(p);
			}
			ps.remove(0);
			Collections.sort(ps, new PathCompGree());
			curr.copy(ps.get(0));
			node = curr.citys.get(curr.citys.size()-1);
			parent = curr.citys.get(curr.citys.size()-2);
			if(curr.equalsPath(fndPath))
				keep=0;
		}
		printResult();
		
	}
	
	void astar(int srccity, int destcity){
		ArrayList<Path> ps = new ArrayList<Path>();
		fndPath = new Path();
		fndPath.cost=9999999.9;
		fndPath.clscost=9999999.9;
		Path p1 = new Path();
		p1.add(srccity, 0.0);
		p1.setCls(getCls(srccity));
		ps.add(p1);
		int node = srccity, parent = srccity;
		Path curr = p1;
		
		while(keep>0){
			ArrayList<Road> children = expand(node, curr);
			for(Road i:children){
				Path p = new Path();
				p.copy(curr);
				p.add(i.roadToId, (double)i.roadDist);
				p.setCls(getCls(i.roadToId));
				ps.add(p);
				if(p.citys.contains(destcity))
					if((p.cost + p.clscost) < (fndPath.cost + fndPath.clscost))
						fndPath.copy(p);
			}
			ps.remove(0);
			Collections.sort(ps, new PathCompAStar());
			curr.copy(ps.get(0));
			node = curr.citys.get(curr.citys.size()-1);
			parent = curr.citys.get(curr.citys.size()-2);
			if(curr.equalsPath(fndPath))
				keep=0;
		}
		
		printResult();
		
	}
	
	void printResult(){
		System.out.print("List of nodes expanded: ");
		for(int e : expanded)
			System.out.print(cities[e].name + ", ");
		System.out.println("\nTotal number of nodes expanded: " + expanded.size());
		System.out.print("List of nodes in solution path: ");
		for (int i:fndPath.citys){
			System.out.print(cities[i].name + ", ");
		}
		System.out.println("\nNumber of nodes in solution path: " + fndPath.citys.size());
		System.out.println("Cost of solution path: " + fndPath.cost);
	}

	int checkCity(String cityName, int left, int right){
		int found = -1;
		if(left > right)
			return found;
		int mid = (left + right)/2;
		if(cities[mid].name.compareToIgnoreCase(cityName.toLowerCase()) > 0)
			found = checkCity(cityName, left, mid-1);
		else if(cities[mid].name.compareToIgnoreCase(cityName.toLowerCase()) < 0)
			found = checkCity(cityName, mid+1, right);
		else
			found = cities[mid].ctid;
		return found;
	}
	
	void makeRoad(String c1, String c2, int dist){
		int sc = checkCity(c1, 0, noC-1);
		int dc = checkCity(c2, 0, noC-1);
		cities[sc].roads.add(new Road(cities[dc].name, dc, dist));
		cities[dc].roads.add(new Road(cities[sc].name, sc, dist));
	}
	
	void makeCities(){
		cities[++cityID] = new City("albanyGA", 31.58,  84.17, cityID);		cities[++cityID] = new City("albanyNY", 42.66,  73.78, cityID);
		cities[++cityID] = new City("albuquerque", 35.11, 106.61, cityID);		cities[++cityID] = new City("atlanta", 33.76,  84.40, cityID);
		cities[++cityID] = new City("augusta", 33.43,  82.02, cityID);		cities[++cityID] = new City("austin", 30.30,  97.75, cityID);
		cities[++cityID] = new City("bakersfield", 35.36, 119.03, cityID);		cities[++cityID] = new City("baltimore", 39.31,  76.62, cityID);
		cities[++cityID] = new City("batonRouge", 30.46,  91.14, cityID);		cities[++cityID] = new City("beaumont", 30.08,  94.13, cityID);
		cities[++cityID] = new City("boise", 43.61, 116.24, cityID);		cities[++cityID] = new City("boston", 42.32,  71.09, cityID);
		cities[++cityID] = new City("buffalo", 42.90,  78.85, cityID);		cities[++cityID] = new City("calgary", 51.00, 114.00, cityID);
		cities[++cityID] = new City("charlotte", 35.21,  80.83, cityID);		cities[++cityID] = new City("chattanooga", 35.05,  85.27, cityID);
		cities[++cityID] = new City("chicago", 41.84,  87.68, cityID);		cities[++cityID] = new City("cincinnati", 39.14,  84.50, cityID);
		cities[++cityID] = new City("cleveland", 41.48,  81.67, cityID);		cities[++cityID] = new City("coloradoSprings", 38.86, 104.79, cityID);
		cities[++cityID] = new City("columbus", 39.99,  82.99, cityID);		cities[++cityID] = new City("dallas", 32.80,  96.79, cityID);
		cities[++cityID] = new City("dayton", 39.76,  84.20, cityID);		cities[++cityID] = new City("daytonaBeach", 29.21,  81.04, cityID);
		cities[++cityID] = new City("denver", 39.73, 104.97, cityID);		cities[++cityID] = new City("desMoines", 41.59,  93.62, cityID);
		cities[++cityID] = new City("elPaso", 31.79, 106.42, cityID);		cities[++cityID] = new City("eugene", 44.06, 123.11, cityID);
		cities[++cityID] = new City("europe", 48.87,  -2.33, cityID);		cities[++cityID] = new City("fresno", 36.78, 119.79, cityID);		
		cities[++cityID] = new City("ftWorth", 32.74,  97.33, cityID);		cities[++cityID] = new City("grandJunction", 39.08, 108.56, cityID);
		cities[++cityID] = new City("greenBay", 44.51,  88.02, cityID);		cities[++cityID] = new City("greensboro", 36.08,  79.82, cityID);
		cities[++cityID] = new City("houston", 29.76,  95.38, cityID);		cities[++cityID] = new City("indianapolis", 39.79,  86.15, cityID);
		cities[++cityID] = new City("jacksonville", 30.32,  81.66, cityID);		cities[++cityID] = new City("japan", 35.68, 220.23, cityID);
		cities[++cityID] = new City("kansasCity", 39.08,  94.56, cityID);		cities[++cityID] = new City("keyWest", 24.56,  81.78, cityID);
		cities[++cityID] = new City("lafayette", 30.21,  92.03, cityID);		cities[++cityID] = new City("lakeCity", 30.19,  82.64, cityID);
		cities[++cityID] = new City("laredo", 27.52,  99.49, cityID);		cities[++cityID] = new City("lasVegas", 36.19, 115.22, cityID);
		cities[++cityID] = new City("lincoln", 40.81,  96.68, cityID);		cities[++cityID] = new City("littleRock", 34.74,  92.33, cityID);
		cities[++cityID] = new City("losAngeles", 34.03, 118.17, cityID);		cities[++cityID] = new City("macon", 32.83,  83.65, cityID);
		cities[++cityID] = new City("medford", 42.33, 122.86, cityID);		cities[++cityID] = new City("memphis", 35.12,  89.97, cityID);
		cities[++cityID] = new City("mexia", 31.68,  96.48, cityID);		cities[++cityID] = new City("mexico", 19.40,  99.12, cityID);
		cities[++cityID] = new City("miami", 25.79,  80.22, cityID);		cities[++cityID] = new City("midland", 43.62,  84.23, cityID);
		cities[++cityID] = new City("milwaukee", 43.05,  87.96, cityID);		cities[++cityID] = new City("minneapolis", 44.96,  93.27, cityID);
		cities[++cityID] = new City("modesto", 37.66, 120.99, cityID);		cities[++cityID] = new City("montreal", 45.50,  73.67, cityID);
		cities[++cityID] = new City("nashville", 36.15,  86.76, cityID);		cities[++cityID] = new City("newHaven", 41.31,  72.92, cityID);
		cities[++cityID] = new City("newOrleans", 29.97,  90.06, cityID);		cities[++cityID] = new City("newYork", 40.70,  73.92, cityID);
		cities[++cityID] = new City("norfolk", 36.89,  76.26, cityID);		cities[++cityID] = new City("oakland", 37.80, 122.23, cityID);
		cities[++cityID] = new City("oklahomaCity", 35.48,  97.53, cityID);		cities[++cityID] = new City("omaha", 41.26,  96.01, cityID);
		cities[++cityID] = new City("orlando", 28.53,  81.38, cityID);		cities[++cityID] = new City("ottawa", 45.42,  75.69, cityID);
		cities[++cityID] = new City("pensacola", 30.44,  87.21, cityID);		cities[++cityID] = new City("philadelphia", 40.72,  76.12, cityID);
		cities[++cityID] = new City("phoenix", 33.53, 112.08, cityID);		cities[++cityID] = new City("pittsburgh", 40.40,  79.84, cityID);
		cities[++cityID] = new City("pointReyes", 38.07, 122.81, cityID);		cities[++cityID] = new City("portland", 45.52, 122.64, cityID);
		cities[++cityID] = new City("providence", 41.80,  71.36, cityID);		cities[++cityID] = new City("provo", 40.24, 111.66, cityID);
		cities[++cityID] = new City("raleigh", 35.82,  78.64, cityID);		cities[++cityID] = new City("redding", 40.58, 122.37, cityID);
		cities[++cityID] = new City("reno", 39.53, 119.82, cityID);		cities[++cityID] = new City("richmond", 37.54,  77.46, cityID);
		cities[++cityID] = new City("rochester", 43.17,  77.61, cityID);		cities[++cityID] = new City("sacramento", 38.56, 121.47, cityID);
		cities[++cityID] = new City("salem", 44.93, 123.03, cityID);		cities[++cityID] = new City("salinas", 36.68, 121.64, cityID);
		cities[++cityID] = new City("saltLakeCity", 40.75, 111.89, cityID);		cities[++cityID] = new City("sanAntonio", 29.45,  98.51, cityID);
		cities[++cityID] = new City("sanDiego", 32.78, 117.15, cityID);		cities[++cityID] = new City("sanFrancisco", 37.76, 122.44, cityID);
		cities[++cityID] = new City("sanJose", 37.30, 121.87, cityID);		cities[++cityID] = new City("sanLuisObispo", 35.27, 120.66, cityID);
		cities[++cityID] = new City("santaFe", 35.67, 105.96, cityID);		cities[++cityID] = new City("saultSteMarie", 46.49,  84.35, cityID);
		cities[++cityID] = new City("savannah", 32.05,  81.10, cityID);		cities[++cityID] = new City("seattle", 47.63, 122.33, cityID);
		cities[++cityID] = new City("stamford", 41.07,  73.54, cityID);		cities[++cityID] = new City("stLouis", 38.63,  90.24, cityID);
		cities[++cityID] = new City("stockton", 37.98, 121.30, cityID);		cities[++cityID] = new City("tallahassee", 30.45,  84.27, cityID);
		cities[++cityID] = new City("tampa", 27.97,  82.46, cityID);		cities[++cityID] = new City("thunderBay", 48.38,  89.25, cityID);
		cities[++cityID] = new City("toledo", 41.67,  83.58, cityID);		cities[++cityID] = new City("toronto", 43.65,  79.38, cityID);
		cities[++cityID] = new City("tucson", 32.21, 110.92, cityID);		cities[++cityID] = new City("tulsa", 36.13,  95.94, cityID);
		cities[++cityID] = new City("uk1", 51.30,   0.00, cityID);		cities[++cityID] = new City("uk2", 51.30,   0.00, cityID);		
		cities[++cityID] = new City("vancouver", 49.25, 123.10, cityID);		cities[++cityID] = new City("washington", 38.91,  77.01, cityID);
		cities[++cityID] = new City("westPalmBeach", 26.71,  80.05, cityID);		cities[++cityID] = new City("wichita", 37.69,  97.34, cityID);
		cities[++cityID] = new City("winnipeg", 49.90,  97.13, cityID);		cities[++cityID] = new City("yuma", 32.69, 114.62, cityID);
	}
	
	void makeRoads(){
		makeRoad("albanyNY", "montreal", 226);		makeRoad("albanyNY", "boston", 166);		makeRoad("albanyNY", "rochester", 148);
		makeRoad("albanyGA", "tallahassee", 120);		makeRoad("albanyGA", "macon", 106);
		makeRoad("albuquerque", "elPaso", 267);		makeRoad("albuquerque", "santaFe", 61);
		makeRoad("atlanta", "macon", 82);		makeRoad("atlanta", "chattanooga", 117);
		makeRoad("augusta", "charlotte", 161);		makeRoad("augusta", "savannah", 131);
		makeRoad("austin", "houston", 186);		makeRoad("austin", "sanAntonio", 79);
		makeRoad("bakersfield", "losAngeles", 112);		makeRoad("bakersfield", "fresno", 107);
		makeRoad("baltimore", "philadelphia", 102);		makeRoad("baltimore", "washington", 45);
		makeRoad("batonRouge", "lafayette", 50);		makeRoad("batonRouge", "newOrleans", 80);
		makeRoad("beaumont", "houston", 69);		makeRoad("beaumont", "lafayette", 122);
		makeRoad("boise", "saltLakeCity", 349);		makeRoad("boise", "portland", 428);
		makeRoad("boston", "providence", 51);		makeRoad("buffalo", "toronto", 105);
		makeRoad("buffalo", "rochester", 64);		makeRoad("buffalo", "cleveland", 191);
		makeRoad("calgary", "vancouver", 605);		makeRoad("calgary", "winnipeg", 829);
		makeRoad("charlotte", "greensboro", 91);		makeRoad("chattanooga", "nashville", 129);
		makeRoad("chicago", "milwaukee", 90);		makeRoad("chicago", "midland", 279);
		makeRoad("cincinnati", "indianapolis", 110);		makeRoad("cincinnati", "dayton", 56);
		makeRoad("cleveland", "pittsburgh", 157);		makeRoad("cleveland", "columbus", 142);
		makeRoad("coloradoSprings", "denver", 70);		makeRoad("coloradoSprings", "santaFe", 316);
		makeRoad("columbus", "dayton", 72);		makeRoad("dallas", "denver", 792);		makeRoad("dallas", "mexia", 83);
		makeRoad("daytonaBeach", "jacksonville", 92);		makeRoad("daytonaBeach", "orlando", 54);
		makeRoad("denver", "wichita", 523);		makeRoad("denver", "grandJunction", 246);
		makeRoad("desMoines", "omaha", 135);		makeRoad("desMoines", "minneapolis", 246);
		makeRoad("elPaso", "sanAntonio", 580);		makeRoad("elPaso", "tucson", 320);
		makeRoad("eugene", "salem", 63);		makeRoad("eugene", "medford", 165);
		makeRoad("europe", "philadelphia", 3939);		makeRoad("ftWorth", "oklahomaCity", 209);
		makeRoad("fresno", "modesto", 109);		makeRoad("grandJunction", "provo", 220);
		makeRoad("greenBay", "minneapolis", 304);		makeRoad("greenBay", "milwaukee", 117);
		makeRoad("greensboro", "raleigh", 74);		makeRoad("houston", "mexia", 165);		makeRoad("indianapolis", "stLouis", 246);
		makeRoad("jacksonville", "savannah", 140);		makeRoad("jacksonville", "lakeCity", 113);
		makeRoad("japan", "pointReyes", 5131);		makeRoad("japan", "sanLuisObispo", 5451);
		makeRoad("kansasCity", "tulsa", 249);		makeRoad("kansasCity", "stLouis", 256);
		makeRoad("kansasCity", "wichita", 190);		makeRoad("keyWest", "tampa", 446);
		makeRoad("lakeCity", "tampa", 169);		makeRoad("lakeCity", "tallahassee", 104);
		makeRoad("laredo", "sanAntonio", 154);		makeRoad("laredo", "mexico", 741);
		makeRoad("lasVegas", "losAngeles", 275);		makeRoad("lasVegas", "saltLakeCity", 486);
		makeRoad("lincoln", "wichita", 277);		makeRoad("lincoln", "omaha", 58);
		makeRoad("littleRock", "memphis", 137);		makeRoad("littleRock", "tulsa", 276);
		makeRoad("losAngeles", "sanDiego", 124);		makeRoad("losAngeles", "sanLuisObispo", 182);
		makeRoad("medford", "redding", 150);		makeRoad("memphis", "nashville", 210);
		makeRoad("miami", "westPalmBeach", 67);		makeRoad("midland", "toledo", 82);		makeRoad("minneapolis", "winnipeg", 463);
		makeRoad("modesto", "stockton", 29);		makeRoad("montreal", "ottawa", 132);
		makeRoad("newHaven", "providence", 110);		makeRoad("newHaven", "stamford", 92);
		makeRoad("newOrleans", "pensacola", 268);		makeRoad("newYork", "philadelphia", 101);
		makeRoad("norfolk", "richmond", 92);		makeRoad("norfolk", "raleigh", 174);
		makeRoad("oakland", "sanFrancisco", 8);		makeRoad("oakland", "sanJose", 42);
		makeRoad("oklahomaCity", "tulsa", 105);		makeRoad("orlando", "westPalmBeach", 168);
		makeRoad("orlando", "tampa", 84);		makeRoad("ottawa", "toronto", 269);
		makeRoad("pensacola", "tallahassee", 120);		makeRoad("philadelphia", "pittsburgh", 319);
		makeRoad("philadelphia", "newYork", 101);		makeRoad("philadelphia", "uk1", 3548);		makeRoad("philadelphia", "uk2", 3548);
		makeRoad("phoenix", "tucson", 117);		makeRoad("phoenix", "yuma", 178);
		makeRoad("pointReyes", "redding", 215);		makeRoad("pointReyes", "sacramento", 115);
		makeRoad("portland", "seattle", 174);		makeRoad("portland", "salem", 47);
		makeRoad("reno", "saltLakeCity", 520);		makeRoad("reno", "sacramento", 133);
		makeRoad("richmond", "washington", 105);		makeRoad("sacramento", "sanFrancisco", 95);
		makeRoad("sacramento", "stockton", 51);		makeRoad("salinas", "sanJose", 31);
		makeRoad("salinas", "sanLuisObispo", 137);		makeRoad("sanDiego", "yuma", 172);
		makeRoad("saultSteMarie", "thunderBay", 442);		makeRoad("saultSteMarie", "toronto", 436);
		makeRoad("seattle", "vancouver", 115);		makeRoad("thunderBay", "winnipeg", 440);
	}
}
