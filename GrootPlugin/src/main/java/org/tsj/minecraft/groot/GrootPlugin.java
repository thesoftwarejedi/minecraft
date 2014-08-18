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
 * Oh this goofy useless minigame that has wasted my time.
 * It's not even fun, really.
 * 
 * However, it serves as a great example for building an arena
 * in future plugins
 * 
 * on creation it generates an arena 50 blocks above, plays a game, 
 * and then breaks down the arena and returns the players to their 
 * start point.  This gives the illusion of warping to a new land.
 * 
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
		        try {
					_g = new GrootGame(getServer(), this, (Player)sender, target, Integer.parseInt(args[1]), 
							Integer.parseInt(args[2]), 
							Integer.parseInt(args[3]));
					_g.Start();
					getServer().broadcastMessage("A battle has begun between " + sender.getName() + 
													" and " + target.getName());
		        } catch (Exception ex) {
		        	sender.sendMessage("Error: " + ex.getMessage());
		        }
		        return true;
	    	}
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}    	
        //returning false means that we didn't handle this command
    	return false; 
    }
	
}
