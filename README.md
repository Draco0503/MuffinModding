# MuffinModding
By Draco0503
## Description
Its a simple mod, developed on [FabricMC](https://github.com/FabricMC) `version 1.19`, that starts a Discord bot who works as a `ChatBridge` with the chat from the Minecraft server and the Discord channel set by the `/discord setBot <botToken> <channelId>` command, described later.

The data its stored in a file named: `discordConf.json`

Adding the new Minecraft server commands:

* **/discord setBot** `<botToken>` `<channelId>`: Sets up the bot identified by its Token and sends the messages to the channel given.
* **/discord setBot start**: Starts the bot if the server has the necessary data (`token` and `channelId`).
* **/discord setBot stop**: Stops the bot if its already working.

This mod also includes customizable Minecraft chat messages, see `mixin/discord/ChatMixin.java`. It does not work on Advancement and Non-mod Commands.

Please notify me of any errors or bugs.