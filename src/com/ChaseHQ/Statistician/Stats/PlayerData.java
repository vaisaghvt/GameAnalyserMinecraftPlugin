package com.ChaseHQ.Statistician.Stats;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import com.ChaseHQ.Statistician.Config.Config;
import com.ChaseHQ.Statistician.Database.DBSynchDataGetSet;
import com.ChaseHQ.Statistician.Database.DataValues.DBDataValues_Players;
import com.ChaseHQ.Statistician.Database.DataValues.DataStores;
import com.ChaseHQ.Statistician.EventDataHandlers.PlayerMessageTimer;
import com.ChaseHQ.Statistician.Utils.StringHandler;

public class PlayerData implements IProcessable {
	private HashMap<String, _InternalPlayer> _watchedPlayers = new HashMap<String, _InternalPlayer>();
	private final String SECOND_TASK_LOCATION = "library";
	private long lastTimeStep = 0;

	public synchronized void addPlayerToWatch(String UUID, Location loc) {
		// See if one still lives
		_InternalPlayer ip = this._watchedPlayers.get(UUID);
		if (ip == null) {
			this._watchedPlayers.put(UUID, new _InternalPlayer(UUID, loc));
		} else {
			ip.RenewMe = true;
			ip.RenewMeTime = System.currentTimeMillis() / 1000;
		}
	}

	public synchronized void removePlayerToWatch(String UUID) {
		_InternalPlayer ip = this._watchedPlayers.get(UUID);
		Long logoutTime = System.currentTimeMillis() / 1000;
		if (ip != null) {
			if (ip.RenewMe) {
				ip.TimeAlteration += logoutTime.intValue() - ip.LastUpdateTime.intValue() - (logoutTime.intValue() - ip.RenewMeTime.intValue());
				ip.RenewMe = false;
				ip.LastUpdateTime = logoutTime;
			}
			ip.DestroyAndCleanup = true;
			ip.LogOutTime = logoutTime;
		}
	}

	public synchronized void addBlockBreak(String UUID, Integer blockID) {
		_InternalPlayer ip = this._watchedPlayers.get(UUID);
		if (ip == null) //Log.ConsoleLog("Tried to set data values on player that was not added to watch '" + UUID + "'");
		return;
		Integer pbb = ip.BlockDestroyed.get(blockID);
		if (pbb == null) {
			pbb = new Integer(0);
		}

		pbb++;

		ip.BlockDestroyed.put(blockID, pbb);
	}

	//public void incrementStepsTaken(String UUID, Location loc, boolean inMinecart, boolean onPig, boolean inBoat) {
	public void incrementStepsTaken(String UUID, Location loc, Class<? extends Entity> vehicleType) {
		_InternalPlayer ip = this._watchedPlayers.get(UUID);
		if (ip == null) //Log.ConsoleLog("Tried to set data values on player that was not added to watch '" + UUID + "'");
		return;
		try {
			int newDist = (int)ip.LastLocation.distance(loc);

			if (newDist > 0) {
				ip.Distance += newDist;
				if (vehicleType != null) {
					if (Minecart.class.isAssignableFrom(vehicleType)) {
						ip.DistanceInMinecart += newDist;
					}
					if (Pig.class.isAssignableFrom(vehicleType)) {
						ip.DistanceOnPig += newDist;
					}
					if (Boat.class.isAssignableFrom(vehicleType)) {
						ip.DistanceInBoat += newDist;
					}
				}
				ip.LastLocation = loc;
			}
		} catch (IllegalArgumentException e) {
			ip.LastLocation = loc;
		}
	}


	public boolean isGameComplete(String UUID) {
		_InternalPlayer ip = this._watchedPlayers.get(UUID);
		if (ip == null) //Log.ConsoleLog("Tried to set data values on player that was not added to watch '" + UUID + "'");
			return false;
		else
			return ip.isGameComplete();


	}


	public void updateInteractEvent(Player player, String UUID, Action action,
			Block clickedBlock) {


		if (clickedBlock.getType() != Material.LEVER || (action != Action.RIGHT_CLICK_BLOCK && action != Action.LEFT_CLICK_BLOCK)){
			return;
		}



		_InternalPlayer ip = this._watchedPlayers.get(UUID);
		if (ip == null) //Log.ConsoleLog("Tried to set data values on player that was not added to watch '" + UUID + "'");
			return;

		Location leverLocation = clickedBlock.getLocation();


		if((clickedBlock.getData()&0x8) == 8){ // powered

			ip.addOpenPrisonLocation(leverLocation,(long) ((new Date()).getTime()%1e12));

			if(ip.getnumberOfPrisonsOpened() == Config.getConfig().getNumberOfPrisons()){ //escape step 1 : go to top floor
				player.sendMessage(StringHandler.formatForChat("Good work so far! You need to escape asap now. Go to the gallery with all the pictures on Level 3. The Wall behind the desk holds the key.", player));
				PlayerMessageTimer.currentAim = "Picture Gallery (3rd floor)";
			}else if(ip.getnumberOfPrisonsOpened() == Config.getConfig().getNumberOfPrisons()+1){ // escape step 2 : knowledge testing path planning
				player.sendMessage(StringHandler.formatForChat("Nice! One more step to freedom. Second floor-"+SECOND_TASK_LOCATION +". Another secret wall.", player));
				ip.setEscapeStartTime((long) ((new Date()).getTime()%1e12));			
				PlayerMessageTimer.currentAim = SECOND_TASK_LOCATION+" (2nd floor)";
			}else if(ip.getnumberOfPrisonsOpened() == Config.getConfig().getNumberOfPrisons()+2){ // escape step 3 : get to escape. Again knowledge testing and finale. 
				player.sendMessage(StringHandler.formatForChat("Awesome work soldier! Proceeed to your starting location on the ground floor.", player));
				PlayerMessageTimer.currentAim = "Starting Location (1st Floor)";
			}else if (ip.getnumberOfPrisonsOpened() < Config.getConfig().getNumberOfPrisons()) {
				if(ip.getnumberOfPrisonsOpened()>0){
					player.sendMessage(StringHandler.formatForChat(ip.getnumberOfPrisonsOpened()+ " of "+ Config.getConfig().getNumberOfPrisons()+" prisons opened", player));
				}
			}else {
				long timeAtComplete = (long) ((new Date()).getTime()%1e12);
				long timeToComplete = ip.setCompletionTime(timeAtComplete);
				player.sendMessage(StringHandler.formatForChat("Congratulations! You completed the task in "+ timeToComplete +" seconds. Thank you for playing!", player));
				ip.setGameCompleted();
			}
		}else {
			ip.removeOpenPrisonLocation(leverLocation);
			player.sendMessage(StringHandler.formatForChat("Warning! You just closed a lever. All levers need to open to complete the game and escape.", player));
		}

	}

	public void updatePlayerLocation(String UUID, Location location, long time) {
		time%=1e12; // Not necessary to store so big a value and requires reconfiguring of mysql database.
		_InternalPlayer player = this._watchedPlayers.get(UUID);

		if (player == null) //Log.ConsoleLog("Tried to set data values on player that was not added to watch '" + UUID + "'");
			return;
		try {
			// Minimize unnecessary updating.
			if(player.timeSteps.contains(time)){ 
				return;
			}

			int x=location.getBlockX();
			int y=location.getBlockY();
			int z=location.getBlockZ();

			// Minimize unnecsesary updating: Don't update if same location
			if(player.previousPosition!=null && player.previousPosition.getBlockX()==x &&
					player.previousPosition.getBlockY()==y &&
					player.previousPosition.getBlockZ()==z ){

				return;
			}
			synchronized(player.timeSteps){
				player.timeSteps.add(time);
			}
			player.timeXMapping.put(time, x);
			player.timeYMapping.put(time, y);
			player.timeZMapping.put(time, z);
			player.previousPosition = location;
		} catch (IllegalArgumentException e) {
//			player.LastLocation = location;
		}

	}

	public int getPlayerPrisonersFreed(String string) {

		_InternalPlayer player = this._watchedPlayers.get(string);

		if (player == null) //Log.ConsoleLog("Tried to set data values on player that was not added to watch '" + UUID + "'");
			return -1;
		return player.getnumberOfPrisonsOpened();
	}

	public String getLastPrisonLevel(String string) {
		_InternalPlayer player = this._watchedPlayers.get(string);

		if (player == null) //Log.ConsoleLog("Tried to set data values on player that was not added to watch '" + UUID + "'");
			return null;
		return player.getLastPrisonLevel();
	}
	public synchronized void addBlockPlaced(String UUID, Integer blockID) {
		_InternalPlayer ip = this._watchedPlayers.get(UUID);
		if (ip == null) //Log.ConsoleLog("Tried to set data values on player that was not added to watch '" + UUID + "'");
		return;
		Integer pbb = ip.BlockPlaced.get(blockID);
		if (pbb == null) {
			pbb = new Integer(0);
		}

		pbb++;

		ip.BlockPlaced.put(blockID, pbb);
	}

	public synchronized void addKillTag(String UUID, KillTag kt) {
		_InternalPlayer ip = this._watchedPlayers.get(UUID);
		if (ip == null) //Log.ConsoleLog("Tried to set data values on player that was not added to watch '" + UUID + "'");
		return;
		ip.KillTags.add(kt);
	}

	public synchronized void addItemPickup(String UUID, Integer itemID, Integer amount) {
		_InternalPlayer ip = this._watchedPlayers.get(UUID);
		if (ip == null) //Log.ConsoleLog("Tried to set data values on player that was not added to watch '" + UUID + "'");
		return;
		Integer itemStore = ip.ItemPickup.get(itemID);
		if (itemStore == null) {
			itemStore = new Integer(0);
		}
		itemStore += amount;
		ip.ItemPickup.put(itemID, itemStore);
	}

	public synchronized void addItemDropped(String UUID, Integer itemID, Integer amount) {
		_InternalPlayer ip = this._watchedPlayers.get(UUID);
		if (ip == null) //Log.ConsoleLog("Tried to set data values on player that was not added to watch '" + UUID + "'");
		return;
		Integer itemStore = ip.ItemDropped.get(itemID);
		if (itemStore == null) {
			itemStore = new Integer(0);
		}
		itemStore += amount;
		ip.ItemDropped.put(itemID, itemStore);
	}

	public void addPlayerTimer(Long time, String UUID){
		_InternalPlayer ip= this._watchedPlayers.get(UUID);
		if (ip== null)
			return;
		ip.addStartTime(time);
	}


	@Override
	public synchronized void _processData() {
		List<_InternalPlayer> playersToRemove = new ArrayList<_InternalPlayer>();

		for (_InternalPlayer intP : this._watchedPlayers.values()) {
			// Do work, insert into DB
			Long currentTimeStamp = System.currentTimeMillis() / 1000;
			int timeSpentOnSinceLast = 0;
			if (intP.DestroyAndCleanup) {
				timeSpentOnSinceLast = currentTimeStamp.intValue() - intP.LastUpdateTime.intValue() - (currentTimeStamp.intValue() - intP.LogOutTime.intValue());
				if (intP.RenewMe) {
					timeSpentOnSinceLast += currentTimeStamp.intValue() - intP.RenewMeTime.intValue();
				}
			} else {
				timeSpentOnSinceLast = currentTimeStamp.intValue() - intP.LastUpdateTime.intValue();
			}

			timeSpentOnSinceLast += intP.TimeAlteration;

			// Database Calls

			DBSynchDataGetSet.incrementValue(DataStores.PLAYER, DBDataValues_Players.NUM_SECS_LOGGED,
					timeSpentOnSinceLast, DBDataValues_Players.UUID, intP.UUID);

			if (intP.Distance > 0) {
				DBSynchDataGetSet.incrementValue(DataStores.PLAYER, DBDataValues_Players.DISTANCE_TRAVELED,
						intP.Distance, DBDataValues_Players.UUID, intP.UUID);
			}

			if (intP.DistanceInMinecart > 0) {
				DBSynchDataGetSet.incrementValue(DataStores.PLAYER, DBDataValues_Players.DISTANCE_TRAVELED_IN_MINECART,
						intP.DistanceInMinecart, DBDataValues_Players.UUID, intP.UUID);
			}

			if (intP.DistanceOnPig > 0) {
				DBSynchDataGetSet.incrementValue(DataStores.PLAYER, DBDataValues_Players.DISTANCE_TRAVELED_ON_PIG,
						intP.DistanceOnPig, DBDataValues_Players.UUID, intP.UUID);
			}

			if (intP.DistanceInBoat > 0) {
				DBSynchDataGetSet.incrementValue(DataStores.PLAYER, DBDataValues_Players.DISTANCE_TRAVELED_IN_BOAT,
						intP.DistanceInBoat, DBDataValues_Players.UUID, intP.UUID);
			}

			for (Integer BlockID : intP.BlockDestroyed.keySet()) {
				Integer smashValue = intP.BlockDestroyed.get(BlockID);
				DBSynchDataGetSet.incrementBlockDestroy(intP.UUID, BlockID, smashValue);
			}

			for (Integer BlockID : intP.BlockPlaced.keySet()) {
				Integer putValue = intP.BlockPlaced.get(BlockID);
				DBSynchDataGetSet.incrementBlockPlaced(intP.UUID, BlockID, putValue);
			}

			for (KillTag kt : intP.KillTags) {
				DBSynchDataGetSet.newKill(kt);
			}

			for (Integer itemID : intP.ItemPickup.keySet()) {
				Integer pickedUp = intP.ItemPickup.get(itemID);
				DBSynchDataGetSet.incrementItemPickup(intP.UUID, itemID, pickedUp);
			}

			for (Integer itemID : intP.ItemDropped.keySet()) {
				Integer dropped = intP.ItemDropped.get(itemID);
				DBSynchDataGetSet.incrementItemDrop(intP.UUID, itemID, dropped);
			}

			ArrayList<Long> timesteps;
            synchronized(intP.timeSteps){
            	timesteps = new ArrayList<Long> (intP.timeSteps.size());
            	for(long timeStep: intP.timeSteps){
            		if(timeStep<lastTimeStep){
            			continue;
            		}
            		timesteps.addAll(intP.timeSteps);
            	}
            }
            
			for (Long timeStep: timesteps){
				Integer x = intP.timeXMapping.get(timeStep);
				Integer y = intP.timeYMapping.get(timeStep);
				Integer z = intP.timeZMapping.get(timeStep);
				if(x!=null && y!=null && z!=null){
					DBSynchDataGetSet.updateLocation(intP.UUID, timeStep, x, y,z);
				}
				lastTimeStep = timeStep;
			}


			// End of Database Calls

			intP.LastUpdateTime = currentTimeStamp;

			if (intP.DestroyAndCleanup) {
				if (intP.RenewMe) {
					intP.DestroyAndCleanup = false;
					intP.RenewMe = false;
				} else {
					playersToRemove.add(intP);
				}
			}

			intP._resetInternal(); // reset values
		}
		for (_InternalPlayer intP : playersToRemove) {
			this._watchedPlayers.remove(intP.UUID);
		}
	}

}
