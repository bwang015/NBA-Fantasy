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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.json.simple.JSONObject;

import database.ExportExcel;
import database.Statline;
import main.util.Jsonparser;
import main.util.MainHelper;
import main.util.Util;
import minheap.MinHeap;
import minheap.MyTeam;
import minheap.PercentLine;
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
		} finally {
			System.out.println();
		}
	}

	public void displayInjuries() {
		System.out.println("Injury Report");
		HashMap<String, Set<String>> injuries = new HashMap<String, Set<String>>();
		for(String x: ExportExcel.notable_injuries.keySet()){
			Statline s = ExportExcel.notable_injuries.get(x);
			String team = s.getTeam();
			if(injuries.containsKey(team)){
				Set<String> tmp = injuries.get(team);
				tmp.add(x);
			}else{
				Set<String> tmp = new HashSet<String>();
				tmp.add(x);
				injuries.put(team, tmp);
			}
		}
		List<String> teams = new ArrayList<String>(injuries.keySet());
		Collections.sort(teams);
		for(String team: teams){
			Set<String> injuryRoster = injuries.get(team);
			System.out.println(team + ": ");
			for(String player: injuryRoster){
				Statline s = ExportExcel.notable_injuries.get(player);
				Double minutes = Util.round(s.getMinutes(), 2);
				System.out.println("\t" + player + " || " + minutes + " minutes || " + s.getInjuryCause());
			}

		}
		
		System.out.println();
	}

	public void compare(String[] add, String[] drop, Player fantasy, String myTeam, String otherTeam) {
		if(add.length != 0 && drop.length != 0){
			fantasy.changeTeam(myTeam, drop, add);
			fantasy.compare(myTeam, "replace", otherTeam);
			fantasy.displayValues(myTeam);
			fantasy.displayValues("replace");
			fantasy.displayValues(otherTeam);
		}
		else{
			fantasy.compare(myTeam, "", otherTeam);
			fantasy.displayValues(myTeam);
			fantasy.displayValues(otherTeam);
		}
		
		//Formatting
		System.out.println();	
	}

	public void displayHotPlayers(Player fantasy) throws IOException {
		//Display Hot Players in the last week
		ExportExcel.export("Week.xls", 0, fantasy);
		
		MinHeap min = MinHeap.getInstance();
		
		HashMap<String, Integer> outputPlayers = new HashMap<String, Integer>();
		
		PercentLine[] topStats = min.getOverall(min.OVERALL);
		System.out.println("Overall Top Value");
		Util.displayHeap(topStats, min.OVERALL, outputPlayers);
		
		topStats = MinHeap.getInstance().getOverall(min.POINTS);
		System.out.println("Overall Top Points");
		Util.displayHeap(topStats, min.POINTS, outputPlayers);
		
		topStats = MinHeap.getInstance().getOverall(min.REBOUNDS);
		System.out.println("Overall Top Rebounds");
		Util.displayHeap(topStats, min.REBOUNDS, outputPlayers);
		
		topStats = MinHeap.getInstance().getOverall(min.ASSISTS);
		System.out.println("Overall Top Assists");
		Util.displayHeap(topStats, min.ASSISTS, outputPlayers);
		
		topStats = MinHeap.getInstance().getOverall(min.THREES);
		System.out.println("Overall Top Threes");
		Util.displayHeap(topStats, min.THREES, outputPlayers);
		
		topStats = MinHeap.getInstance().getOverall(min.STEALS);
		System.out.println("Overall Top Steals");
		Util.displayHeap(topStats, min.STEALS, outputPlayers);
		
		topStats = MinHeap.getInstance().getOverall(min.BLOCKS);
		System.out.println("Overall Top Blocks");
		Util.displayHeap(topStats, min.BLOCKS, outputPlayers);
		
		System.out.println("Players +3 Categories");
		Util.display(outputPlayers);
		
		topStats = MyTeam.getInstance().getList();
		System.out.println(ExportExcel.TEAM_PERFORMANCE + "'s Performance over the last week");
		Util.displayHeap(topStats, min.OVERALL, outputPlayers);
	}
}
