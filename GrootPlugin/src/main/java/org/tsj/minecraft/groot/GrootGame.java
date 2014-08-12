package org.tsj.minecraft.groot;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

public class GrootGame implements Listener {
	
	Server _server;
	GrootPlugin _plugin;
	World _w;
	Player _p1;
	Player _p2;
	boolean isP1Turn = false;
	Wool purpleWool = new Wool(DyeColor.PURPLE);
	byte purpleData = DyeColor.PURPLE.getData();
	Wool greenWool = new Wool(DyeColor.GREEN);
	byte greenData = DyeColor.GREEN.getData();
	boolean _gameOver = false;
	int _turnSeconds;
	int _turnCounter = 0;
	Block _winP1;
	Block _winP2;
	int _winDistance;
	
	public GrootGame(Server s, GrootPlugin plugin, Player p1, Player p2, int turnSeconds, int winDistance) {
		_server = s;
		_plugin = plugin;
		_p1 = p1;
		_p2 = p2;
		_w = _p1.getWorld();
		_turnSeconds = turnSeconds;
		_winDistance = winDistance;
	}

	public void Start() {
		//set the start point
		_w.getBlockAt(_p1.getLocation().add(0, -1, 0)).setType(Material.BEDROCK);
		
		//set win points
		_winP1 = _w.getBlockAt(_p1.getLocation().add(_winDistance, 0, 0));
		_winP1.setType(Material.WOOL);
		_winP1.setData(purpleData);
		
		_winP2 = _w.getBlockAt(_p1.getLocation().add(_winDistance*-1, 0, 0));
		_winP2.setType(Material.WOOL);
		_winP2.setData(greenData);
		
		//p1 gets purple
		ItemStack is = purpleWool.toItemStack(64);
		_p1.setItemInHand(is);
		_p1.getInventory().addItem(is);
		_server.getLogger().info("player 1 is " + _p1.getName());

		//p2 gets green
		is = greenWool.toItemStack(64);
		_p2.setItemInHand(is);
		_p2.getInventory().addItem(is);
		_server.getLogger().info("player 2 is " + _p2.getName());

		//register for events
		_server.getPluginManager().registerEvents(this, _plugin);
	}
	
	public void Stop() {
		_gameOver = true;
		//todo unregister events!
	}
	
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent evt) {
    	if (_gameOver) return;
    	//prevent breaking blocks during the game
    	evt.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent evt) {
    	if (_gameOver) return;
    	Player p = evt.getPlayer();
    	if (p != _p1 && p != _p2) return;
    	if ((_p1 != _p2) && //during testing, p1 might be p2
    			((p == _p1 && !isP1Turn) ||
    			 (p == _p2 && isP1Turn))) {
    		evt.setCancelled(true);
    		p.sendMessage("It's not your turn");
    		return;
    	}
    	//if we get here, it was p1 or p2 and it was their turn.
    	
    	byte canPlaceColor = isP1Turn ? purpleData : greenData;
    	Block blockPlaced = evt.getBlockPlaced();
    	if (blockPlaced.getType() != Material.WOOL ||
    		blockPlaced.getData() != canPlaceColor) {
    		p.sendMessage("You can only place your color wool");
    		evt.setCancelled(true);
    		return;
    	}
    	
    	byte canPlaceAgainstColor = isP1Turn ? greenData : purpleData;
    	Block blockAgainst = evt.getBlockAgainst();    	
    	if ((blockAgainst.getType() != Material.BEDROCK) && 
    			(blockAgainst.getType() != Material.WOOL ||
	    		 blockAgainst.getData() != canPlaceAgainstColor)) {
    		p.sendMessage("You can only place your wool on bedrock or your opponents wool until the game is over");
    		evt.setCancelled(true);
    		return;
    	}
    	
    	//the block was allowed
    	
    	//check for a win...
    	{
	    	Player winner = null;
	    	Player loser = null;
	    	Location newBlockLocation = evt.getBlockPlaced().getLocation();
	    	if (newBlockLocation.distanceSquared(_winP1.getLocation()) == 1) {
	    		winner = _p1;
	    		loser = _p2;
	    	} else if (newBlockLocation.distanceSquared(_winP2.getLocation()) == 1) {
	    		winner = _p2;
	    		loser = _p1;
	    	}
	    	if (winner != null) {
		    	winner.sendMessage(ChatColor.GREEN + "Your target has been reached!  You win!");
	    		loser.sendMessage(ChatColor.RED + "Your opponents target has been reached!  You LOSE!");
	    		Stop();
	    		return;
	    	}
    	}
    	
    	//the game goes on!
		evt.setCancelled(false); //insure that the block is placed (as much we can with high priority here)
    	p.sendMessage("Block placed, your opponent has " + _turnSeconds + " seconds to go");
    	String oppMessage = "You must place your wool on bedrock or your opponents wool within " + _turnSeconds + " seconds";
    	if (isP1Turn) {
    		_p2.sendMessage(oppMessage);
    	} else {
    		_p1.sendMessage(oppMessage);
    	}
    	isP1Turn = !isP1Turn;
    	_turnCounter++;
    	final int currentTurn = _turnCounter;
    	final Player loser = isP1Turn ? _p1 : _p2;
    	final Player winner = isP1Turn ? _p2 : _p1;
    	//set a timer to see if the next player went
    	_server.getScheduler().scheduleSyncDelayedTask(_plugin, new Runnable() {
    		  public void run() {
    		      if (!_gameOver && _turnCounter == currentTurn) {
    		    	  //the loser didn't go
    		    	  Stop();
    		    	  loser.sendMessage(ChatColor.RED + "Times up! You lost!");
    		    	  winner.sendMessage(ChatColor.GREEN + "That dude's time is up! You won!");
    		      }
    		  }
    		}, _turnSeconds * 20);
    }
    
}
