package org.tsj.minecraft.groot;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This simple example minecraft plugin was created as an entry point for my kids
 * to begin developing plugins.
 * 
 * This was mainly taken from http://wiki.bukkit.org/Plugin_Tutorial
 * @author @thesoftwarejedi
 */
public final class GrootPlugin extends JavaPlugin implements Listener {
    
    GrootGame _g;

	@Override
	public void onEnable() {
		getLogger().info("GrootPlugin enabled");
	}

	@Override
	public void onDisable() {
        if (_g != null) _g.Stop();
		getLogger().info("GrootPlugin disabled");
	}
    
    //we override "oncommand" to handle player typed commands.
    //this is the most common entry point for a plugin
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	try {
	    	if (cmd.getName().equalsIgnoreCase("groot")) { 
		    	Player target = getServer().getPlayer(args[0]); 
		        if (target == null) {
		           sender.sendMessage(args[0] + " is not online!");
		           return true;
		        }
				_g = new GrootGame(getServer(), this, (Player)sender, target, Integer.parseInt(args[1]), 
																				Integer.parseInt(args[2]));
				_g.Start();
				getServer().broadcastMessage("A battle has begun between " + sender.getName() + 
												" and " + target.getName());
		        return true;
	    	}
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}    	
        //returning false means that we didn't handle this command
    	return false; 
    }
	
}
