package com.botsystem;

import com.botsystem.console.commands.ConsoleCommand;
import com.botsystem.console.commands.ConsoleCommands;
import com.botsystem.core.BotSystem;
import com.botsystem.core.BotSystemModule;

import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;

/**
 * A class to hold disposable bot information
 * @author BlockBa5her
 *
 */
public class BotBottle {
	// main bot
	private BotSystem bot;
	// console commands class
	private ConsoleCommands commands;
	
	// BotSystem modules
	private BotSystemModule[] modules;
	// all console commands
	private ConsoleCommand[] consoleCommands;
	
	// is bot fully shutdown
	private boolean botShutdown = false;

	/**
	 * Package-private initializer to create bottle
	 * @param modules The modules for BotSystem
	 * @param consoleCommands The console commands to add
	 */
	BotBottle(BotSystemModule[] modules, ConsoleCommand[] consoleCommands) {
		Debug.trace("creating instance of new bot bottle");
		
		this.modules = modules;
		this.consoleCommands = consoleCommands;
		
		Debug.trace("new instance created");
	}
	
	/**
	 * Starts the instance of the bottle
	 */
	public void start() {
		try {
			Thread.sleep(1500); // ghost-time period
		} catch (InterruptedException e1) {}
		
		// creating bot
		bot = new BotSystem(Main.CONFIG.getString("token"));
		// adding modules
		bot.addModuleRange(modules);
		// login the bot
		bot.login();
		
		// add ready event to bot
		bot.addEvent(ReadyEvent.class, e -> {
			// creating console commands class
			Debug.trace("initializing console commands, and command modules");
			commands = new ConsoleCommands(bot, consoleCommands);
			// starting console commands
			commands.start();
			Debug.trace("console commands started");
		});
	}
	
	/**
	 * Disposes and stops all items in bottle
	 */
	public void dispose() {
		// add shutdown event to know when bot is fully shutdown
		bot.addEvent(ShutdownEvent.class, e -> {
			botShutdown = true;
		});
		
		// logout the bot
		bot.logout();
		
		// wait till bot is fully logged out
		while (!botShutdown) {
			Thread.yield();
		}
		
		// stop console commands
		commands.interrupt();
		
		// nullify everything
		bot = null;
		commands = null;
		consoleCommands = null;
		modules = null;
		
		Debug.trace("disposed current bot bottle");
	}
	
	/**
	 * Get's the bot instance
	 * @return The BotSystem bot
	 */
	public BotSystem getBot() {
		return bot;
	}

	/**
	 * Get's whether the bot is fully shutdown
	 * @return
	 */
	public boolean isBotShutdown() {
		return botShutdown;
	}
}
