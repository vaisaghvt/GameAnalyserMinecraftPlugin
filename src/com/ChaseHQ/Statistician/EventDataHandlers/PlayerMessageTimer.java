package com.ChaseHQ.Statistician.EventDataHandlers;

import java.util.Date;
import java.util.TimerTask;

import org.bukkit.entity.Player;

import com.ChaseHQ.Statistician.StatisticianPlugin;
import com.ChaseHQ.Statistician.Config.Config;
import com.ChaseHQ.Statistician.Utils.StringHandler;

public class PlayerMessageTimer extends TimerTask {
	
	public static String currentAim ="";
	private Player player;
	private int localTime;
	final private int updateTime;
	final private int gameEndTime;
	private int countTimesForLast;
	private Object lastPrisonLevel;

	

	public PlayerMessageTimer(Player player, int updateTime) {
		this.player = player;
		this.localTime = 0;
		this.updateTime = updateTime; // This is in seconds
		this.gameEndTime = Config.getConfig().getGameEndTime()*60; //Convert minutes to seconds

		StatisticianPlugin.getInstance().getPlayerData().addPlayerTimer((long) ((new Date()).getTime()%1e12), player.getUniqueId().toString());
		if(Config.getConfig().getGameStartTime()!=0){
			String message = "You have "+ Config.getConfig().getGameStartTime() +" minutes of preparation time to learn how to play.";
			message += "w,a,s,d to move. Mouse to look. Double tap 'w' to run. Left ctrl to zoom in to view.  ";
			
			
			player.sendMessage(StringHandler.formatForChat(message, player));
		}
		countTimesForLast =0;
	}

	@Override
	public void run() {
		if (localTime < 60){
			String message="";
			int secondsLeft = (this.gameEndTime-this.localTime);
			String timeLeft =  (secondsLeft /60)+ (secondsLeft%60 !=0 ?" : " +(secondsLeft %60):"");
			
			switch(localTime){
			case 0:
				message= " The countdown has started!"+ " "+timeLeft +" minutes left. Pay attention to the messages here.";
				break;
			case 15:
				message= " Levers are NOT hidden. Look carefully though."+ " "+timeLeft +" minutes left";
				break;
			case 30:
				message= " Hint : the environment is complicated so you might want to leave doors open to know where you've visited"+ " "+timeLeft +" minutes left";
				break;
			case 45:
				message= " However, be warned, the freed prisoners can open or close doors. So this isn't always reliable."+ " "+timeLeft +" minutes left";
				break;
			default:
				message= " The countdown has started!";
				message+= " Hint : the environment is complicated so you might want to leave doors open to know where you've visited";
				message+= " However, be warned, the freed prisoners can open or close doors. So this isn't always reliable.";
				message+= " Levers are not hidden. However, they might not catch the eye at first. So look carefully.";
			}
			player.sendMessage(StringHandler.formatForChat(message, player));
			localTime+=updateTime;
		}
		else if(localTime == this.gameEndTime){
			player.sendMessage(StringHandler.formatForChat("Game over!! You are no longer being tracked. Thank you.", player));
			StatisticianPlugin.getInstance().getPlayerData().removePlayerToWatch(player.getUniqueId().toString());
			this.cancel();
		}else if(StatisticianPlugin.getInstance().getPlayerData().isGameComplete(player.getUniqueId().toString())){
			player.sendMessage(StringHandler.formatForChat("Terminating timer", player));
			StatisticianPlugin.getInstance().getPlayerData().removePlayerToWatch(player.getUniqueId().toString());
			this.cancel();
		}
		else {
			int prisonersFreed = StatisticianPlugin.getInstance().getPlayerData().getPlayerPrisonersFreed(player.getUniqueId().toString());
			int prisonsLeft = (Math.max(0, Config.getConfig().getNumberOfPrisons()- prisonersFreed));
			int secondsLeft = (this.gameEndTime-this.localTime);
			String timeLeft =  (secondsLeft /60)+ (secondsLeft%60 !=0 ?" : " +(secondsLeft %60):"");
			String message = null;
			if(prisonsLeft >1){
				
				
				String prisonMessage = prisonsLeft + " prisons ";
				message = "You have "+ prisonMessage +"left to open. You have "+ timeLeft +
						" minutes Left!";
			}else if(prisonsLeft ==1){
				if (countTimesForLast<5){
					message = "You have 1 prison left to open. You have "+ timeLeft +
						" minutes Left!";
				}
				else{
					if(lastPrisonLevel == null){
						lastPrisonLevel = StatisticianPlugin.getInstance().getPlayerData().getLastPrisonLevel(player.getUniqueId().toString());
					}
					message = "Hint: The last one is in "+ lastPrisonLevel +". Hurry!!"+ timeLeft+ " minutes left!";
				}
				countTimesForLast++;
			}else {
			
				message = "Get to the " +PlayerMessageTimer.currentAim+"! You have "+ timeLeft +
						" minutes Left!";
			}
			
			player.sendMessage(StringHandler.formatForChat(message, player));
			localTime+=updateTime;
		}

	}

}
