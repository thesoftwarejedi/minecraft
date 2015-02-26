package org.tsj.minecraft.exampleplugin;

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
public final class ExamplePlugin extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
        // TODO Insert logic to be performed when the plugin is enabled
		getLogger().info("onEnable has been invoked!");
	}
	

    @Override
    public void onDisable() {
        // TODO Insert logic to be performed when the plugin is disabled
		getLogger().info("onDisable has been invoked!");
    }
    
    //we override "oncommand" to handle player typed commands.
    //this is the most common entry point for a plugin
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {   
    	//the variable "args" is an array holding the words after the command name
    	
    	//check to make sure that there are a certain number of arguments?
    	//this example would give the sender a message if they typed more
    	//that one word after the command
    	if (args.length > 1) {
            sender.sendMessage("Too many arguments!");
            return false;
         } 
    	
    	//example of sending the player a message of what they typed
    	sender.sendMessage("You sent to the plugin: " + args[0]);
    	
    	//this is the simplest way of handling a "/" command
    	if (cmd.getName().equalsIgnoreCase("example")) { // If the player typed /example then do the following...
    		// doSomething
    		return true; //return true if we handled this command
    	} 
    	
    	//this handles a command and checks that the person issuing it is a player
    	//which means that this would get an error if entered from the server console
    	//note that you could make this behave the opposite way as well...
    	if (cmd.getName().equalsIgnoreCase("example2")) { //If the player typed /example2 then do the following...
    		if (sender instanceof Player) {
    	           Player player = (Player) sender;
    	           // do something using player
    	        } else {
    	           sender.sendMessage("You must be a player!");
    	           return false;
    	        }
    	}

    	if (cmd.getName().equalsIgnoreCase("example3")) { //If the player typed /example3 then do the following...
	    	//this command checks to see if the word after the command is the name of an online player
	    	Player target = getServer().getPlayer(args[0]); 
	        if (target == null) {
	           sender.sendMessage(args[0] + " is not online!");
	           return false;
	        }
	        //this is just an example of writing out the last type of entity that hurt the player given
	        sender.sendMessage("Player " + args[0] + " was last hurt by a " + 
	        						target.getLastDamageCause().getEntityType().toString());
    	}
    	
        //returning false means that we didn't handle this command
    	return false; 
    }
    
    //another entry point is for listeners of events.  A full list of events can
    //be found in the wiki: http://wiki.bukkit.org/Event_API_Reference   Check all packages under
    //the "org.bukkit.event" package.  Here are some simple examples:
    
    //straight from the tutorial, this will make stone constantly appear above the player!
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Get the player's location.
        Location loc = event.getPlayer().getLocation();
        // Sets loc to five above where it used to be. Note that this doesn't change the player's position.
        loc.setY(loc.getY() + 5);
        // Gets the block at the new location.
        Block b = loc.getBlock();
        // Sets the block to type id 1 (stone).
        
        //NOTE: UNCOMMENT THIS TO MAKE THIS ACTIVELY WORK, COMMENTED OUT TO PREVENT CONFUSION
        //b.setType(Material.STONE);
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
    	getServer().broadcastMessage("OH NOES! " + event.getEntity().getDisplayName() + " has died!");
    }
    
    //some crazy events are available...
    public void onSheepGrowWoolEvent(SheepRegrowWoolEvent event) {
    	getServer().broadcastMessage("A sheep grew wool!?");
    }
	
}
