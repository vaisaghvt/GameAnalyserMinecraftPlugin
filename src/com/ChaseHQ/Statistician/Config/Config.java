package com.ChaseHQ.Statistician.Config;

import org.bukkit.configuration.file.FileConfiguration;

import com.ChaseHQ.Statistician.StatisticianPlugin;

// *NOTE Config class must not be used before the plugin's onEnable is called.
public class Config {
	private static final int _dbVersion = 12;

	private static Config instance = null;

	private FileConfiguration config;

	private boolean verboseErrors;
	private String dbAddress;
	private int dbPort;
	private String dbName;
	private String dbUsername;
	private String dbPassword;
	private int dbUpdateTime;

	private int gameStartTime;

	private int timerUpdateTime;

	private int gameEndTime;

	private int numberOfPrisons;

	public static Config getConfig() {
		if (Config.instance == null) {
			Config.instance = new Config();
		}

		return Config.instance;
	}

	/**
	 * @return The current version
	 */
	public static String getStatisticianVersion() {
		return StatisticianPlugin.getInstance().getDescription().getVersion();
	}

	/**
	 * @return The log prefix
	 */
	public static String getLogPrefix() {
		return "[" + StatisticianPlugin.getInstance().getDescription().getName() + "]";
	}

	/**
	 * @return The database version
	 */
	public static int getDBVersion() {
		return Config._dbVersion;
	}

	public Config() {
		if (Config.instance != null) return;

		StatisticianPlugin plugin = StatisticianPlugin.getInstance();

		this.config = plugin.getConfig();
		this.config.options().copyDefaults(true);

		this.verboseErrors = this.config.getBoolean("verbose_errors");
		this.dbAddress = this.config.getString("database_address");
		this.dbPort = this.config.getInt("database_port");
		this.dbName = this.config.getString("database_name");
		this.dbUsername = this.config.getString("database_username");
		this.dbPassword = this.config.getString("database_password");
		this.dbUpdateTime = this.config.getInt("database_update_time");
		this.gameStartTime = this.config.getInt("game_start_time");
		this.timerUpdateTime = this.config.getInt("timer_update_time");
		this.gameEndTime = this.config.getInt("game_end_time");
		this.numberOfPrisons = this.config.getInt("number_of_prisons");


		plugin.saveConfig();
	}

	/**
	 * @return if should output verbose errors
	 */
	public boolean isVerboseErrors() {
		return this.verboseErrors;
	}

	/**
	 * @return The Database address
	 */
	public String getDBAddress() {
		return this.dbAddress;
	}

	/**
	 * @return The Database port
	 */
	public int getDBPort() {
		return this.dbPort;
	}

	/**
	 * @return The Database update time, in milliseconds.
	 */
	public int getDBUpdateTime() {
		return this.dbUpdateTime * 1000;
	}

	/**
	 * @return The Database table name
	 */
	public String getDBName() {
		return this.dbName;
	}

	/**
	 * @return The Database username
	 */
	public String getDBUsername() {
		return this.dbUsername;
	}

	/**
	 * @return The Database password
	 */
	public String getDBPassword() {
		return this.dbPassword;
	}

	public int getGameStartTime() {
		return gameStartTime;
	}

	public int getTimerUpdateTime() {
		return timerUpdateTime;
	}

	public int getGameEndTime() {
		return gameEndTime;
	}

	public int getNumberOfPrisons() {
		return numberOfPrisons;
	}
}
