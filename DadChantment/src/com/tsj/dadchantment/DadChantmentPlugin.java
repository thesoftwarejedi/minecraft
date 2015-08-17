package com.tsj.dadchantment;

import com.rit.sucy.*;

public class DadChantmentPlugin extends EnchantPlugin {

	@Override
    public void registerEnchantments() {
		//register grumpy (my only enchantment, for testing)
		GrumpyEnchantment enchantment = new GrumpyEnchantment();
        EnchantmentAPI.registerCustomEnchantment(enchantment);
        getServer().getPluginManager().registerEvents(enchantment, this);
    }
	
}
