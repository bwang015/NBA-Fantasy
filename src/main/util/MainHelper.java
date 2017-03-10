package main.util;

import org.json.simple.JSONObject;

import roster.Player;

public class MainHelper {

	public void loadRoster(String roster_data, Player fantasy, String[] teams) {
		Jsonparser parser = new Jsonparser(roster_data);
		for(int i = 0; i < teams.length; i++){
			JSONObject obj = parser.getObj(teams[i]);
			for(int j = 0; j < obj.size(); j++){
				int value = j + 1;
				String key = "player" + value;
				
				fantasy.addEntry(teams[i], (String) obj.get(key)); 
			}
		}
	}

}
