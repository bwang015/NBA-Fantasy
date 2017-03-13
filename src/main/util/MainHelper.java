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

	public boolean executeTransactions(String transaction, Player fantasy) {
		String[] params = Util.parseTransactions(transaction);
			
		boolean dropflag = false;
		boolean addFlag = false;
			
		if(fantasy.dropPlayer(params[0], params[1])) {
			System.out.println("Dropped " + params[1] + " Successfully");
			dropflag = true;
		}
		else{
			System.out.println("Error Dropping " + params[1]);
		}
		
		if(fantasy.addPlayer(params[0], params[2])){
			System.out.println("Added " + params[2] + " Successfully");
			addFlag = true;
		}
		else
			System.out.println("Error Adding " + params[2]);
		
		return addFlag && dropflag;
	}

}
