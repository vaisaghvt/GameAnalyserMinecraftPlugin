package com.ChaseHQ.Statistician.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ChaseHQ.Statistician.EventDataHandlers.EDHPlayer;

import java.util.Date;

public class PlayerListener implements Listener {
	private EDHPlayer edhPlayer;
//	private DateFormat dateFormat;
	
	public PlayerListener(EDHPlayer passedEDH) {
		this.edhPlayer = passedEDH;
//		dateFormat = new SimpleDateFormat("MMddHHmmss"); // MMdd so that the value does not exceed the MaxInt value
		
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		this.edhPlayer.PlayerJoin(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		this.edhPlayer.PlayerQuit(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		//get current date time with Date()
		
		long time = new Date().getTime();
		
		this.edhPlayer.PlayerUpdateLocation(event.getPlayer(),time);
	
		this.edhPlayer.PlayerMove(event.getPlayer(), event.getPlayer().getVehicle() != null ? event.getPlayer().getVehicle().getClass() : null);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		this.edhPlayer.PlayerPickedUpItem(event.getPlayer(), event.getItem().getItemStack().getTypeId(), event.getItem().getItemStack().getAmount());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		this.edhPlayer.PlayerDroppedItem(event.getPlayer(), event.getItemDrop().getItemStack().getTypeId(), event.getItemDrop().getItemStack().getAmount());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
//	public PlayerInteractEvent(Player who,
//            Action action,
//            ItemStack item,
//            Block clickedBlock,
//            BlockFace clickedFace)
		this.edhPlayer.PlayerInteractEvent(event.getPlayer(), event.getAction(), event.getClickedBlock());
		
	}
}
