package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.parser.ParseException;

import database.ExportExcel;
import database.Statline;
import league.LeagueStats;
import minheap.MinHeap;
import minheap.MyTeam;
import minheap.PercentLine;
import roster.Player;
import roster.PlayerSchedule;

public class Main {
	private final static String OVERALL = "overall_value";
	private final static String POINTS = "points_value";
	private final static String REBOUNDS = "rebound_value";
	private final static String ASSISTS = "assist_value";
	private final static String THREES = "three_value";
	private final static String STEALS = "steal_value";
	private final static String BLOCKS = "block_value";

	public static void main(String[] args) throws IOException, ParseException {
		LoadInfo info = new LoadInfo();
		String customDate = info.getCustomDate();
		String customDay = info.getCustomDay();
		String myTeam = info.getMyTeam();
		String otherTeam = info.getOtherTeam();
		
		Player fantasy = new Player(customDate, customDay); // 10/25, Sa
		
		String[] teams = info.getTeams();
		
		LeagueStats league = new LeagueStats(myTeam, otherTeam);
		
		String[] drop = info.getDrop();
		
		String[] add = info.getAdd();
		
		Statline myStat = info.getStatline(myTeam);
		fantasy.addStatline(myTeam, myStat);
		
		fantasy.addStatline("replace", myStat.clone());
		
		Statline otherStat = info.getStatline(otherTeam);
		fantasy.addStatline(otherTeam, otherStat);
		
		info.addEntries(fantasy, teams);
		
		System.out.println("Injury Report");
		displayInjuries();
		
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
		
		league.displayWeekly();
		league.displayOverall();

		//Display Hot Players in the last week
		ExportExcel.export("Week.xls", 0, fantasy);
		
		PercentLine[] topStats = MinHeap.getInstance().getOverall(OVERALL);
		System.out.println("Overall Top Value");
		displayHeap(topStats, OVERALL);
		
		topStats = MinHeap.getInstance().getOverall(POINTS);
		System.out.println("Overall Top Points");
		displayHeap(topStats, POINTS);
		
		topStats = MinHeap.getInstance().getOverall(REBOUNDS);
		System.out.println("Overall Top Rebounds");
		displayHeap(topStats, REBOUNDS);
		
		topStats = MinHeap.getInstance().getOverall(ASSISTS);
		System.out.println("Overall Top Assists");
		displayHeap(topStats, ASSISTS);
		
		topStats = MinHeap.getInstance().getOverall(THREES);
		System.out.println("Overall Top Threes");
		displayHeap(topStats, THREES);
		
		topStats = MinHeap.getInstance().getOverall(STEALS);
		System.out.println("Overall Top Steals");
		displayHeap(topStats, STEALS);
		
		topStats = MinHeap.getInstance().getOverall(BLOCKS);
		System.out.println("Overall Top Blocks");
		displayHeap(topStats, BLOCKS);
		
		topStats = MyTeam.getInstance().getList();
		System.out.println("My Team's Performance over the last week");
		displayHeap(topStats, OVERALL);
	}

	private static void displayInjuries() {
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
				Double minutes = round(s.getMinutes(), 2);
				System.out.println("\t" + player + " || " + minutes + " minutes || " + s.getInjuryCause());
			}

		}
		
		System.out.println();
	}

	private static void displayHeap(PercentLine[] overall, String category) {
		if(overall == null){
			System.out.println("No Players Currently \n");
			return;
		}
		
		for(int i = overall.length - 1; i >= 0; i--){
			
			if(overall[i] == null)
				continue;
			
			Statline s = overall[i].getStats();
			double percent = round(overall[i].getValue(), 2);
			double extraValue = 0;
			double minutes = 0;
			double historic_minutes = 0;
			switch (category) {
			case OVERALL:
				extraValue = round(s.getOverallValue(), 2);
				break;
			case POINTS:
				extraValue = round(s.getPoints(), 2);
				break;
			case REBOUNDS:
				extraValue = round(s.getRebounds(), 2);
				break;
			case ASSISTS:
				extraValue = round(s.getAssists(), 2);
				break;
			case THREES:
				extraValue = round(s.getThrees(), 2);
				break;
			case STEALS:
				extraValue = round(s.getSteals(), 2);
				break;
			case BLOCKS:
				extraValue = round(s.getBlocks(), 2);
				break;
			}
			
			minutes = round(s.getMinutes(), 2);
			historic_minutes = round(overall[i].getHistoricMinutes(), 2);
			
			System.out.print(overall[i].getName() + " " + percent + "% || ");
			System.out.print(extraValue + " || " + minutes + " current minutes || ");
			System.out.print(historic_minutes + " season minutes || ");
			System.out.print(s.getTeam() + " || ");
			PlayerSchedule.getPlayerDates(overall[i].getName());
		}
		//Formatting
		System.out.println();
	}
	
	private static Double round(Double value, int places) {
		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}
}
