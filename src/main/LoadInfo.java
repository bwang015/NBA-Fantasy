package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.json.simple.JSONObject;

import database.ExportExcel;
import database.Statline;
import main.util.Jsonparser;
import main.util.MainHelper;
import main.util.Util;
import roster.Player;

public class LoadInfo {
	private Properties prop;
	
	public LoadInfo() throws IOException{
		prop = new Properties();
		InputStream input = new FileInputStream("conf/config.properties");
		prop.load(input);
		ExportExcel.start();
	}

	public String getCustomDate() {
		return prop.getProperty("nba.date", "");
	}
	
	public String getCustomDay() {
		return prop.getProperty("nba.day", "");
	}
	
	public String getMyTeam() {
		return prop.getProperty("general.myteam");
	}
	
	public String getOtherTeam() {
		return prop.getProperty("general.otherteam");
	}
	
	public String[] getTeams() {
		String roster_data = prop.getProperty("data.teams");
		Jsonparser parser = new Jsonparser(roster_data);
		
		JSONObject obj = parser.getObj("Teams");
		String[] teams = new String[obj.size()];
		
		for(int i = 0; i < obj.size(); i++){
			int num = i+1;
			String key = "team" + num;
			teams[i] = (String) obj.get(key);
		}

		return teams;
	}

	public String[] getDrop() {
		String roster_data = prop.getProperty("data.drop");
		//To be implemented
		return roster_data == null ? new String[0] : null;
	}
	
	public String[] getAdd() {
		String roster_data = prop.getProperty("data.add");
		//To be implemented
		return roster_data == null ? new String[0] : null;
	}
	
	public Statline getStatline(String teamName) {
		String roster_data = prop.getProperty("data.statline");
		Jsonparser parser = new Jsonparser(roster_data);
		
		JSONObject obj = parser.getObj(teamName.equals("myTeam") ? teamName : "otherTeam");
		parser.parseStats(obj);
		
		Statline s = new Statline(parser.getFGA(), parser.getFGP(), parser.getFTA(), parser.getFTP(),
				parser.getTRE(), parser.getPTS(), parser.getREB(), parser.getAST(), parser.getSTL(),
				parser.getBLK(), parser.getTO());

		return s;
	}

	@SuppressWarnings("unchecked")
	public void addEntries(Player fantasy, String[] teams) {
		MainHelper roster = new MainHelper();
		String roster_data = prop.getProperty("data.teams");
		roster.loadRoster(roster_data, fantasy, teams);
		String file = prop.getProperty("data.transactions");
		
		List<String> transactions = new ArrayList<String>();
		List<String> write_actions = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			String line;
			while((line = reader.readLine()) != null){
				if(line.startsWith("#")){
					write_actions.add(line);
					continue;
				}
				transactions.add(line);
			}
			
			reader.close();
			
			if(transactions.isEmpty())
				return;
			
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			
			for(int i = 0; i < write_actions.size(); i++){
				writer.write(write_actions.get(i) + "\n");
			}
			
			writer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//cleared transactions.log and commit changes to json files.
		Jsonparser parser = new Jsonparser(roster_data);
		
		//get map of all teams
		HashMap<String, JSONObject> roster_map = new HashMap<String, JSONObject>();
		for(String x: teams){
			JSONObject obj = parser.getObj(x);
			roster_map.put(x, obj);
		}
		roster_map.put("Teams", parser.getObj("Teams"));
		
		for(int i = 0; i < transactions.size(); i++){
			if(roster.executeTransactions(transactions.get(i), fantasy)){
				String[] params = Util.parseTransactions(transactions.get(i));
				JSONObject obj = roster_map.get(params[0]);
				
				for(int j = 0; j < obj.size(); j++){
					int index = j + 1;
					String key = "player" + index;
					if(((String) obj.get(key)).equals(params[1])){
						obj.put(key, params[2]);
						break;
					}
				}
			}
		}
		
		//Write to file
		FileWriter json = null;
		try {
			json = new FileWriter(roster_data);
			StringBuilder str = new StringBuilder();
			str.append("{");
			for(String x: roster_map.keySet()){
				JSONObject obj = roster_map.get(x);
				str.append("\"" + x + "\": " + obj.toJSONString() + ",\n");
			}
			str.deleteCharAt(str.length() - 2);
			str.append("}");
			json.append(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			json.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
