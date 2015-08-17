package com.tsj.dadchantment;

import java.util.Map;
import java.util.Set;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.*;

import com.rit.sucy.CustomEnchantment;

public class GrumpyEnchantment extends CustomEnchantment implements Listener {
    
	public GrumpyEnchantment() {
		super("Grumpy", new Material[] { }, 10);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent evt) {
		Block block = evt.getBlock();
		Material mat = block.getType();
		//check to see if player hit ore - thats all we care about, so check quick and move on otherwise
		if (mat.equals(Material.IRON_ORE) || mat.equals(Material.GOLD_ORE)) {
			//get the enchantment of the item used to hit the block
			Map<Enchantment, Integer> enMap = evt.getPlayer().getItemInHand().getEnchantments();
			//these next three lines check to see if there's even an enchantment on the item
			if (enMap != null) {
				Set<Enchantment> keys = enMap.keySet();
				if (keys != null && !keys.isEmpty()) {
					//ok, there's an enchantment on the tool, loop through enchantments and see if its this one
					for (Enchantment e : keys) {
						if (e.getName().equals(this.enchantName)) {
							//BINGO
							InstantSmite(evt.getBlock(), evt.getPlayer());
							//prevent the block from doing what it normally would
							evt.setCancelled(true); 
							break; //no sense checking other enchantments
						}
					}
				}
			}
		}
	}

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
