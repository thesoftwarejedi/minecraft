package org.tsj.minecraft.jedigames;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This simple example minecraft plugin was created as an entry point for my kids
 * to begin developing plugins.
 * 
 * This was mainly taken from http://wiki.bukkit.org/Plugin_Tutorial
 * @author @thesoftwarejedi
 */
public final class JediGamesPlugin extends JavaPlugin implements Listener {

	public static Boolean _s_snowballActive = false;
	public static Location _s_pos1 = null;
	public static Location _s_pos2 = null;

	@Override
	public void onEnable() {		
		//load from config
		_s_snowballActive = getConfig().getBoolean("jg-snowball_active");
		
		String s = getConfig().getString("jg-snowball_pos1");
		String[] ss = s.split(",");
		_s_pos1 = new Location(getServer().getWorlds().get(0), Integer.parseInt(ss[0]), Integer.parseInt(ss[1]), Integer.parseInt(ss[2]));
		
		s = getConfig().getString("jg-snowball_pos2");
		ss = s.split(",");
		_s_pos1 = new Location(getServer().getWorlds().get(0), Integer.parseInt(ss[0]), Integer.parseInt(ss[1]), Integer.parseInt(ss[2]));
				
		//register and move on
		getServer().getPluginManager().registerEvents(this,  this);
		getLogger().info("Jedi Games has been enabled");
	}
	

    @Override
    public void onDisable() {
        HandlerList.unregisterAll((Listener)this);
		getConfig().set("jg-snowball_active", _s_snowballActive);
		if (_s_pos1 != null) {
			getConfig().set("jg-snowball_pos1", _s_pos1.getBlockX() + "," + _s_pos1.getBlockY() + "," + _s_pos1.getBlockZ());
		}
		if (_s_pos2 != null) {
			getConfig().set("jg-snowball_pos2", _s_pos2.getBlockX() + "," + _s_pos2.getBlockY() + "," + _s_pos2.getBlockZ());
		}
        saveConfig();
		getLogger().info("Jedi Games has been disabled");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	
		if (cmd.getName().equalsIgnoreCase("jg-snowball")) { 
			if (args.length != 1) {
	            sender.sendMessage("Requires exactly one argument, \"on\", \"off\", \"set1\", \"set2\"");
	            return false;
			}
			if (args[0].equalsIgnoreCase("on")) {
				_s_snowballActive = true;
	            sender.sendMessage("Jedi snowball on");
			} else if (args[0].equalsIgnoreCase("off")) {
				_s_snowballActive = false;
	            sender.sendMessage("Jedi snowball off");
			} else if (args[0].equalsIgnoreCase("set1")) {
				if (!(sender instanceof Player)) return false;
				Block b = ((Player)sender).getTargetBlock(null, 25);
				_s_pos1 = b.getLocation();
	            sender.sendMessage("Jedi snowball set position 1");
			} else if (args[0].equalsIgnoreCase("set2")) {
				if (!(sender instanceof Player)) return false;
				Block b = ((Player)sender).getTargetBlock(null, 25);
				_s_pos2 = b.getLocation();
	            sender.sendMessage("Jedi snowball set position 2");
			}
            return true; //return true if we handled this command
		}
        //returning false means that we didn't handle this command
    	return false; 
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    	
    	if (!_s_snowballActive || _s_pos1 == null || _s_pos2 == null) return;
    	
    	if (event.getDamager() instanceof Snowball 
    			&& event.getCause() == DamageCause.PROJECTILE 
    			&& event.getEntity() instanceof Player) {
    		Player jailMe = (Player)event.getEntity();
    		if (isInBounds(jailMe.getLocation(), _s_pos1, _s_pos2)) {
    			Bukkit.dispatchCommand(getServer().getConsoleSender(), "togglejail " + jailMe.getName() + " snowball-jail 5s");
    			getServer().broadcastMessage(jailMe.getName() + " was hit by a snowball!");
    		}
    	}
    }

	private static boolean isInBounds(Location loc, Location pos1, Location pos2) {
		return isBetween(loc.getBlockX(), _s_pos1.getBlockX(), _s_pos2.getBlockX()) &&
				isBetween(loc.getBlockY(), _s_pos1.getBlockY(), _s_pos2.getBlockY()) &&
				isBetween(loc.getBlockZ(), _s_pos1.getBlockZ(), _s_pos2.getBlockZ());
	}
	
	private static boolean isBetween(int a, int b, int c) {
		//swap them if needed
		if (c < b) {
			int t = b;
			b = c;
			c = t;
		}
		return b <= a && a <= c; 
	}
}
	
