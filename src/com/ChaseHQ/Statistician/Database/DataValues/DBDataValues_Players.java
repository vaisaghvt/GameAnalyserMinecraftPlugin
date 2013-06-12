package com.ChaseHQ.Statistician.Database.DataValues;

public enum DBDataValues_Players implements IDataValues {
	UUID("uuid"),
	PLAYER_NAME("player_name"),
	ONLINE("online"),
	FIRSTEVER_LOGIN("firstever_login"),
	LAST_LOGIN("last_login"),
	THIS_LOGIN("this_login"),
	LAST_LOGOUT("last_logout"),
	NUM_LOGINS("num_logins"),
	NUM_SECS_LOGGED("num_secs_loggedon"),
	DISTANCE_TRAVELED("distance_traveled"),
	DISTANCE_TRAVELED_IN_MINECART("distance_traveled_in_minecart"),
	DISTANCE_TRAVELED_ON_PIG("distance_traveled_on_pig"),
	DISTANCE_TRAVELED_IN_BOAT("distance_traveled_in_boat");

	private final String columnName;

	private DBDataValues_Players(String ColName) {
		this.columnName = ColName;
	}

	@Override
	public String getColumnName() {
		return this.columnName;
	}

	@Override
	public DataStores belongsToStore() {
		return DataStores.PLAYER;
	}
}
