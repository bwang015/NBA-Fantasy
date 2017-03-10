package main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.json.simple.JSONObject;

import database.ExportExcel;
import database.Statline;
import main.util.Jsonparser;
import main.util.MainHelper;
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
		
		JSONObject obj = parser.getObj(teamName);
		parser.parseStats(obj, teamName);
		
		Statline s = new Statline(parser.getFGA(), parser.getFGP(), parser.getFTA(), parser.getFTP(),
				parser.getTRE(), parser.getPTS(), parser.getREB(), parser.getAST(), parser.getSTL(),
				parser.getBLK(), parser.getTO());

		return s;
	}

	public void addEntries(Player fantasy, String[] teams) {
		MainHelper roster = new MainHelper();
		String roster_data = prop.getProperty("data.teams");
		roster.loadRoster(roster_data, fantasy, teams);
		String transactions = prop.getProperty("data.transactions");
		
		if(transactions == null)
			return;
		
		//roster.executeTransactions();		
	}
}
