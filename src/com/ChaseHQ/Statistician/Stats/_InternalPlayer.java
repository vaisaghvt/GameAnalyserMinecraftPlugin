package com.ChaseHQ.Statistician.Stats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.bukkit.Location;

import com.ChaseHQ.Statistician.Config.Config;
import com.ChaseHQ.Statistician.Utils.Position;


public class _InternalPlayer {

	public static HashSet<Position> positions;
	public String UUID = new String();
	public HashMap<Integer, Integer> BlockDestroyed = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> BlockPlaced = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> ItemPickup = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> ItemDropped = new HashMap<Integer, Integer>();
	public Map<Long, Integer> timeXMapping = new HashMap<Long, Integer>();
	public Map<Long, Integer> timeYMapping = new HashMap<Long, Integer>();
	public Map<Long, Integer> timeZMapping = new HashMap<Long, Integer>();
	public ArrayList<KillTag> KillTags = new ArrayList<KillTag>();
	private HashSet<Location> openPrisonLocations = new HashSet<Location> ();
	private HashMap<Location, Long> prisonLocationTimeMapping = new HashMap<Location, Long> ();


	Integer Distance = new Integer(0);
	Integer DistanceInMinecart = new Integer(0);
	Integer DistanceInBoat = new Integer(0);
	Integer DistanceOnPig = new Integer(0);
	Location LastLocation;
	public boolean DestroyAndCleanup = false;
	public boolean RenewMe = false;
	Long LastUpdateTime = System.currentTimeMillis() / 1000;
	int TimeAlteration = 0;
	Long RenewMeTime = 0L;
	Long LogOutTime = 0L;
	public Collection<Long> timeSteps = new TreeSet<Long>();
	public Location previousPosition;
	private long startTime;
	private boolean gameComplete = false;
	private long completionTime;
	private long escapeStartTime;
	private Position lastPrisonLocation = null;
	private final String displayName;

	public _InternalPlayer(String UUID, Location location, String displayName) {
		this.UUID = UUID;
		this.LastLocation = location;
		previousPosition =location;
		this.displayName = displayName;

	}

	public void _resetInternal() {
		this.BlockDestroyed.clear();
		this.BlockPlaced.clear();
		this.Distance = 0;
		this.DistanceInMinecart = 0;
		this.DistanceInBoat = 0;
		this.DistanceOnPig = 0;
		this.TimeAlteration = 0;
		this.KillTags.clear();
		this.ItemPickup.clear();
		this.ItemDropped.clear();
	}

	public void addOpenPrisonLocation(Location location, long time) {
		synchronized(openPrisonLocations){
			openPrisonLocations.add(location);
			if(this.getnumberOfPrisonsOpened() == Config.getConfig().getNumberOfPrisons() -1 ){
//				System.out.println("Finding last location");
				lastPrisonLocation = findMissingLocation();
//				System.out.println("the last location is"+ lastPrisonLocation.getX());
			}

		}
		synchronized(prisonLocationTimeMapping){
			prisonLocationTimeMapping.put(location, time);
		}
//		System.out.println(openPrisonLocations.size());
	}

	private Position findMissingLocation() {


		HashSet<Position> locations = positions;
		locations.remove(new Position(-98,36,-124));
		locations.remove(new Position(-41,28,-79));
		locations.remove(new Position(-68,20,-146));
		locations.remove(new Position(-92,28,-110));
		for(Location location: openPrisonLocations){
			Position openLocation = new Position(location.getBlockX(), location.getBlockY(), location.getBlockZ());
//			System.out.println(openLocation);
			locations.remove(openLocation);
		}
		if(locations.size()!=1){
			return null;
		}else {
			return locations.iterator().next();
		}


	}

	

	public void removeOpenPrisonLocation(Location location) {
		synchronized(openPrisonLocations){
			openPrisonLocations.remove(location);
		}
		synchronized(prisonLocationTimeMapping){
			prisonLocationTimeMapping.remove(location);
		}
//		System.out.println(openPrisonLocations.size());
	}

	public int getnumberOfPrisonsOpened() {

		return Math.max(0,(openPrisonLocations.size()-1));
	}

	public void addStartTime(long time) {
		this.startTime = time;

	}

	public long getTimeElapsed() {
		return System.currentTimeMillis() - startTime;
	}

	public boolean isGameComplete() {

		return this.gameComplete ;
	}

	public void setGameCompleted(){
		this.gameComplete = true;
		writeTimesToFile();
	}

	public long setEscapeStartTime(long currentTimeMillis) {
		this.escapeStartTime = (currentTimeMillis-startTime);
		return escapeStartTime/1000;
	}

	public long setCompletionTime(long currentTimeMillis) {
		this.completionTime = (currentTimeMillis-startTime);

		return completionTime/1000;

	}

	public void writeTimesToFile() {
		TreeMap<Long, Location> sortedTimeLocationMap = new TreeMap<Long, Location> ();
		for (Location location: this.prisonLocationTimeMapping.keySet()){

			sortedTimeLocationMap.put(prisonLocationTimeMapping.get(location),location);
		}


		File file = new File("/home/ec2-user/settings/scripts/times.txt");
	     PrintWriter writer = null;
	     try {
	            writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	     writer.println("Start Time="+ this.startTime);
	     writer.println("Escape Start Time="+this.escapeStartTime);
	     writer.println("Completion Time="+this.completionTime);
	     for(Long time: sortedTimeLocationMap.keySet()){
	    	 writer.println(time +"="+sortedTimeLocationMap.get(time));
	     }

	     writer.close();

	}

	public String getLastPrisonLevel(){
		if( lastPrisonLocation == null) {
			return "still null";
		}else {
			int yLoc = lastPrisonLocation.getY();
			if(yLoc == 20 || yLoc == 21){
				return "Level 1";
			}else if (yLoc == 28){
				return "Level 2";
			}else if (yLoc == 36){
				return "Level 3";
			}
			return "Unknown!";
		}

	}

	public String getDisplayName() {
		return this.displayName;
	}

}
