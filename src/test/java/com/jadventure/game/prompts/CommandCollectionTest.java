package com.jadventure.game.prompts;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.internal.bind.CollectionTypeAdapterFactory;
import com.jadventure.game.DeathException;
import com.jadventure.game.GameBeans;
import com.jadventure.game.entities.Player;
import com.jadventure.game.monsters.Troll;
import com.jadventure.game.navigation.Coordinate;
import com.jadventure.game.navigation.Direction;
import com.jadventure.game.navigation.Location;
import com.jadventure.game.navigation.LocationType;
import com.jadventure.game.repository.LocationRepository;



public class CommandCollectionTest {
	
	Player player;
	Location location;
	CommandCollection collection;
	PrintStream stdout;
	
	@Before
	public void setUp(){
		Coordinate coordinate = new Coordinate(1, 1, 0);
		String title = "At the edge of a forest";
		String description = "The are many big trees and some tick busses, looks difficult to go through.";
		LocationType locationType = LocationType.FOREST;
		location = new Location(coordinate, title, description, locationType);
		location.setDangerRating(5);
		
		player = Player.getInstance("recruit");
		player.setLevel(1);
		player.setLocation(location);
		
		collection = CommandCollection.getInstance();
		collection.initPlayer(player);
		
		stdout = System.out;
		
		File source = new File("json/original_data/locations.json");
        File dest = new File("json/locations.json");
        try {
            Files.copy(source.toPath(), dest.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}
	
	@After
	public void tearDown(){
		System.setOut(stdout);
	}
	
	@Test
	public void commandHelpTest(){	
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	    System.setOut(new PrintStream(outContent));
		collection.command_help();
		int n = countLines(outContent.toString());
		
		//13 help commands + 1 extra line
		assertTrue(n == 14);
	}
	
	@Test 
	public void commandSaveTest(){
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	    System.setOut(new PrintStream(outContent));  
		collection.command_save();	
		assertTrue(outContent.toString().contains("data was saved"));
	}
	
	@Test
	public void commandMonsterTest(){
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	    System.setOut(new PrintStream(outContent));
		
		collection.command_m();
		assertTrue(outContent.toString().contains("no monsters"));
		
		Troll troll = new Troll(player.getLevel());
		player.getLocation().addMonster(troll);
		collection.command_m();	
		assertTrue(outContent.toString().contains(troll.monsterType));
		
	}
	
	@Test
	public void commandGoTest() throws DeathException{
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	    System.setOut(new PrintStream(outContent));
	    
		player.setName("player1");
        LocationRepository locationRepo = GameBeans.getLocationRepository(player.getName());
        player.setLocation(locationRepo.getInitialLocation());
		
		collection.command_g("s");
		
		assertTrue(outContent.toString().contains("Stairs:"));
	}
	
	@Test
	public void commandInspect(){
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	    System.setOut(new PrintStream(outContent));
		collection.command_i("");
		assertTrue(outContent.toString().contains("Item doesn't exist"));
	}
	
	private static int countLines(String str){
		   String[] lines = str.split("\r\n|\r|\n");
		   return  lines.length;
		}
	
}
