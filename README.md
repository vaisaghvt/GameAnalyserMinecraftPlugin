My modification of the statistician plugin
==========================================

This is a plugin for minecraft forked from Statistician2

It does the following:

- Stores complete trajectory and writes to the SQL database specificied in the config file
- Sends messages from server to player based on his actions in the game
- Has a countdown timer and countdown timer based messages to the player
- Shuts down the server as appropriate.
- Gate opening times are also stored in a file locally on the machine with the game server.

All of this has specifically been created for http://www.vaisaghvt.com/minecraft-experiment/ and work only in single player mode. It's also been specifically created for that game. So parts of the code related to gate openings, determining which gate is opened and other aspects that are particular to the game I created will probably cause exceptions if used directly.

