package org.tsj.minecraft.groot;

import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.util.*;
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
	Location _p1ResetLoc;
	Location _p2ResetLoc;
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
	LinkedList<Block> _arenaBlocks = new LinkedList<Block>();
	int _arenaBumper;
	
	public GrootGame(Server s, GrootPlugin plugin, Player p1, Player p2, int turnSeconds, int winDistance, int arenaBumper) throws Exception {
		if (turnSeconds < 3 || turnSeconds > 30) {
			throw new Exception("turn seconds must be between 3 and 30");
		}
		if (winDistance < 3 || winDistance > 10) {
			throw new Exception("win distance must be between 3 and 10");
		}
		if (arenaBumper < 2 || arenaBumper > 6) {
			throw new Exception("arena bumper must be between 3 and 10");
		}
		
		_server = s;
		_plugin = plugin;
		_p1 = p1;
		_p2 = p2;
		_w = _p1.getWorld();
		_turnSeconds = turnSeconds;
		_winDistance = winDistance;
		_arenaBumper = arenaBumper;
	}

	public void Start() {
		//set the start point
		Location startLoc = _p1.getLocation().clone().add(0, 50, 0);
		
		createArena(startLoc);		
		
		//place players
		_p1ResetLoc = _p1.getLocation().clone();
		_p1.teleport(startLoc.clone().add(_winDistance-1, 0, 0));
		_p2ResetLoc = _p2.getLocation().clone();
		_p2.teleport(startLoc.clone().add((_winDistance-1)*-1, 0, 0));
		
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
	
	private void createArena(Location startLoc) {
		Block brushBlock = null;
		
		//starting point
		brushBlock = _w.getBlockAt(startLoc.clone());
		brushBlock.setType(Material.OBSIDIAN);	
		_arenaBlocks.add(brushBlock);
		
		//winning points
		_winP1 = _w.getBlockAt(startLoc.clone().add(_winDistance, 0, 0));
		_winP1.setType(Material.WOOL);
		_winP1.setData(purpleData);
		_arenaBlocks.add(_winP1);
		
		_winP2 = _w.getBlockAt(startLoc.clone().add(_winDistance*-1, 0, 0));
		_winP2.setType(Material.WOOL);
		_winP2.setData(greenData);
		_arenaBlocks.add(_winP2);
		
		//build the arena around it all
		Material arenaMaterial = Material.STONE;
		Block refBlock = null;	
		
		//back wall
		refBlock = startLoc.clone().add(_winDistance+_arenaBumper, -_arenaBumper, -_arenaBumper).getBlock();
		createBackplate(arenaMaterial, refBlock);		
		refBlock = startLoc.clone().add(-1*(_winDistance+_arenaBumper), -_arenaBumper, -_arenaBumper).getBlock();
		createBackplate(arenaMaterial, refBlock);
		
		//tube walls
		refBlock = startLoc.clone().add(-1*(_winDistance+_arenaBumper), -_arenaBumper, -_arenaBumper).getBlock();
		createSideWalls(arenaMaterial, refBlock);
		refBlock = startLoc.clone().add(-1*(_winDistance+_arenaBumper), -_arenaBumper, _arenaBumper).getBlock();
		createSideWalls(arenaMaterial, refBlock);
		
		//top and bottom
		refBlock = startLoc.clone().add(-1*(_winDistance+_arenaBumper), -_arenaBumper, -_arenaBumper).getBlock();
		createRoofAndFloor(arenaMaterial, refBlock);
		refBlock = startLoc.clone().add(-1*(_winDistance+_arenaBumper), _arenaBumper, -_arenaBumper).getBlock();
		createRoofAndFloor(arenaMaterial, refBlock);
	}

	private void createRoofAndFloor(Material arenaMaterial, Block refBlock) {
		Block brushBlock;
		for (int i = 0; i < (_winDistance+_arenaBumper)*2; i++) {
			for (int j = 0; j < _arenaBumper*2; j++) {
				brushBlock = refBlock.getRelative(i, 0, j);
				brushBlock.setType(arenaMaterial);
				_arenaBlocks.add(brushBlock);
			}
		}
	}

	private void createSideWalls(Material arenaMaterial, Block refBlock) {
		Block brushBlock;
		for (int i = 0; i < (_winDistance+_arenaBumper)*2; i++) {
			for (int j = 0; j < _arenaBumper*2; j++) {
				brushBlock = refBlock.getRelative(i, j, 0);
				brushBlock.setType(arenaMaterial);
				_arenaBlocks.add(brushBlock);
			}
		}
	}

	private void createBackplate(Material arenaMaterial, Block refBlock) {
		Block brushBlock;
		for (int i = 0; i < _arenaBumper*2; i++) {
			for (int j = 0; j < _arenaBumper*2; j++) {
				brushBlock = refBlock.getRelative(0, i, j);
				brushBlock.setType(arenaMaterial);
				_arenaBlocks.add(brushBlock);
			}
		}
	}
	
	public void Stop() {
		_gameOver = true;
		
		//clear out the arena
		for (Block block : _arenaBlocks) {
			block.setType(Material.AIR);
		}
		
		//put the players back
		_p1.teleport(_p1ResetLoc.clone());
		if (!_p1.equals(_p2)) {
			_p2.teleport(_p2ResetLoc.clone());
		}
		
		//todo unregister events!
	}
	
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent evt) {
    	if (_gameOver) return;
    	Player p = evt.getPlayer();
    	if (p != _p1 && p != _p2) return;
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
    	if ((blockAgainst.getType() != Material.OBSIDIAN) && 
    			(blockAgainst.getType() != Material.WOOL ||
	    		 blockAgainst.getData() != canPlaceAgainstColor)) {
    		p.sendMessage("You can only place your wool on obsidian or your opponents wool until the game is over");
    		evt.setCancelled(true);
    		return;
    	}
    	
    	//the block was allowed
    	_arenaBlocks.add(blockPlaced);
    	
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
    	String oppMessage = "You must place your wool on obsidian or your opponents wool within " + _turnSeconds + " seconds";
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
