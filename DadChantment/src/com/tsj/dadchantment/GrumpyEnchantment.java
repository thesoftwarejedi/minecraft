package com.tsj.dadchantment;

import java.util.HashSet;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import com.rit.sucy.CustomEnchantment;
import com.rit.sucy.EnchantmentAPI;
import com.rit.sucy.service.ENameParser;

//this example enchantment will automatically smite any gold or iron that is mined
//it's named grumpy because previously it just yelled "get off my lawn" to any player
//hit with it
public class GrumpyEnchantment extends CustomEnchantment implements Listener {
	
	//this is a set of players names who are currently holding an instant smite enchantment
	public static HashSet<String> _s_playersEquiped = new HashSet<String>();
    
	public GrumpyEnchantment() {
		super("Grumpy", new Material[] { }, 10);
	}
	
	//whenever the item ANY player is holding changes, this is called
	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent evt) {
		//the player changed inventory slots, get what they have now
		ItemStack item = evt.getPlayer().getInventory().getItemInHand();
		//remove them from the list of players holding instant smite (if they are there)
		_s_playersEquiped.remove(evt.getPlayer().getName());
		//if they're holding nothing, we can return
		if (item == null) return;
		ItemMeta meta = item.getItemMeta();
		//if the item doesn't have meta, we can return
        	if (meta == null) return;
	        //if the item's meta doesn't have lore (enchantment) we return
	        if (!meta.hasLore()) return;
	        //for each enchantment
	        for (String lore : meta.getLore()) {
	            String name = ENameParser.parseName(lore);
	            int level = ENameParser.parseLevel(lore);
	            if (name == null) continue;
	            if (level == 0) continue;
	            //if the enchantment is this enchantment, we add the player to our list of people holding the enchantment
	            if (EnchantmentAPI.isRegistered(name) && EnchantmentAPI.getEnchantment(name) == this) {
	                _s_playersEquiped.add(evt.getPlayer().getName());
	            }
	        }
	}
	
	//every time a block is broken, this is called
	@EventHandler
	public void onBlockBreak(BlockBreakEvent evt) {
		//get the player and check our list.  We want this method optimized for speed since it's being called
		//on the server every time anyone breaks a block.  Nothing is faster than checking a hashset for a string
		Player player = evt.getPlayer();
		if (_s_playersEquiped.contains(player.getName())) {
			Block block = evt.getBlock();
			Material mat = block.getType();
			//check to see if player hit ore - thats all we care about, so check quick and move on otherwise
			if (mat.equals(Material.IRON_ORE) || mat.equals(Material.GOLD_ORE)) {
				//BINGO - this is defined below
				InstantSmite(block, evt.getPlayer());
				//prevent the block from doing what it normally would
				evt.setCancelled(true); 
			}
		}
	}

	//this method does the smolting magic
	private void InstantSmite(Block block, Player player) {
		Material mat = block.getType();
		//give the player ingot instantly, gold or iron
		if (mat.equals(Material.GOLD_ORE)) {
			player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 1));
		} else if (mat.equals(Material.IRON_ORE)) {
			player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 1));
		}
		//clear the block from existence to prevent the drop of the ore
		block.setType(Material.AIR);
	}
	
}
