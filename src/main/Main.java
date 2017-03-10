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
		ExportExcel.start();

		Player fantasy = new Player("", ""); // 10/25, Sa
		
		String[] teams = {
				"myTeam",
				"Jchow's Team",
				"Joe's Team",
				"Yujie's Team",
				"Jason's Team",
				"Andy's Team",
				"Child's Play",
				"Kimba's WhiteWalkers",
				"MollyWopers",
				"Warriors Reunion"
		};
		
		String myTeam = teams[0];
		String otherTeam = teams[1];
		
		LeagueStats league = new LeagueStats(myTeam, otherTeam);
		
		String[] drop = {};
		
		String[] add = {};
		
		Statline myStat = new Statline(216, .417, 79, .797, 23, 266, 91, 66, 12, 13, 22);
		fantasy.addStatline(myTeam, myStat);
		
		fantasy.addStatline("replace", myStat.clone());
		
		Statline otherStat = new Statline(223, .498, 55, .745, 22, 285, 110, 73, 18, 11, 28);
		fantasy.addStatline(otherTeam, otherStat);
		
		addEntries(fantasy, teams);
		
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

	private static void addEntries(Player fantasy, String[] teams) {
		fantasy.addEntry(teams[0], "Courtney Lee");
		fantasy.addEntry(teams[0], "Jameer Nelson");
		fantasy.addEntry(teams[0], "Pau Gasol");
		fantasy.addEntry(teams[0], "Gordon Hayward");
		fantasy.addEntry(teams[0], "Nikola Jokic");
		fantasy.addEntry(teams[0], "Myles Turner");
		fantasy.addEntry(teams[0], "Julius Randle");
		fantasy.addEntry(teams[0], "Seth Curry");
		fantasy.addEntry(teams[0], "Lou Williams");
		fantasy.addEntry(teams[0], "Jeremy Lin");
		fantasy.addEntry(teams[0], "Marcus Smart");
		fantasy.addEntry(teams[0], "CJ McCollum");
		fantasy.addEntry(teams[0], "Giannis Antetokounmpo");
		
		fantasy.addEntry(teams[1], "JJ Redick");
		fantasy.addEntry(teams[1], "Dario Saric");
		fantasy.addEntry(teams[1], "Wilson Chandler");
		fantasy.addEntry(teams[1], "LeBron James");
		fantasy.addEntry(teams[1], "Zach Randolph");
		fantasy.addEntry(teams[1], "Marcus Morris");
		fantasy.addEntry(teams[1], "Dirk Nowitzki");
		fantasy.addEntry(teams[1], "Reggie Jackson");
		fantasy.addEntry(teams[1], "Otto Porter");
		fantasy.addEntry(teams[1], "Elfrid Payton");
		fantasy.addEntry(teams[1], "Chris Paul");
		fantasy.addEntry(teams[1], "Danilo Gallinari");
		fantasy.addEntry(teams[1], "Kristaps Porzingis");
		
		fantasy.addEntry(teams[2], "James Harden");
		fantasy.addEntry(teams[2], "Victor Oladipo");
		fantasy.addEntry(teams[2], "Bradley Beal");
		fantasy.addEntry(teams[2], "Draymond Green");
		fantasy.addEntry(teams[2], "Derrick Favors");
		fantasy.addEntry(teams[2], "Markieff Morris");
		fantasy.addEntry(teams[2], "Brook Lopez");
		fantasy.addEntry(teams[2], "Ryan Anderson");
		fantasy.addEntry(teams[2], "Marcin Gortat");
		fantasy.addEntry(teams[2], "Marvin Williams");
		fantasy.addEntry(teams[2], "Gorgui Dieng");
		fantasy.addEntry(teams[2], "Dennis Schroder");
		fantasy.addEntry(teams[2], "Robert Covington");
		
		fantasy.addEntry(teams[3], "Eric Bledsoe");
		fantasy.addEntry(teams[3], "TJ Warren");
		fantasy.addEntry(teams[3], "Dion Waiters");
		fantasy.addEntry(teams[3], "Al-Farouq Aminu");
		fantasy.addEntry(teams[3], "Paul George");
		fantasy.addEntry(teams[3], "Tobias Harris");
		fantasy.addEntry(teams[3], "DeMarcus Cousins");
		fantasy.addEntry(teams[3], "Jusuf Nurkic");
		fantasy.addEntry(teams[3], "Paul Millsap");
		fantasy.addEntry(teams[3], "George Hill");
		fantasy.addEntry(teams[3], "Nicolas Batum");
		fantasy.addEntry(teams[3], "Clint Capela");
		fantasy.addEntry(teams[3], "Terrence Ross");
		
		fantasy.addEntry(teams[4], "Damian Lillard");
		fantasy.addEntry(teams[4], "Avery Bradley");
		fantasy.addEntry(teams[4], "Eric Gordon");
		fantasy.addEntry(teams[4], "Evan Fournier");
		fantasy.addEntry(teams[4], "Alan Williams");
		fantasy.addEntry(teams[4], "Tyreke Evans");
		fantasy.addEntry(teams[4], "DeMar DeRozan");
		fantasy.addEntry(teams[4], "Al Horford");
		fantasy.addEntry(teams[4], "Darren Collison");
		fantasy.addEntry(teams[4], "Isaiah Thomas");
		fantasy.addEntry(teams[4], "Devin Booker");
		fantasy.addEntry(teams[4], "Wesley Matthews");
		fantasy.addEntry(teams[4], "Hassan Whiteside");
		
		fantasy.addEntry(teams[5], "John Wall");
		fantasy.addEntry(teams[5], "Tim Hardaway Jr");
		fantasy.addEntry(teams[5], "Mike Conley");
		fantasy.addEntry(teams[5], "Trevor Ariza");
		fantasy.addEntry(teams[5], "Blake Griffin");
		fantasy.addEntry(teams[5], "Marc Gasol");
		fantasy.addEntry(teams[5], "Karl-Anthony Towns");
		fantasy.addEntry(teams[5], "Nerlens Noel");
		fantasy.addEntry(teams[5], "Guillermo Hernangomez");
		fantasy.addEntry(teams[5], "Enes Kanter");
		fantasy.addEntry(teams[5], "Goran Dragic");
		fantasy.addEntry(teams[5], "Jrue Holiday");
		fantasy.addEntry(teams[5], "Khris Middleton");
		
		fantasy.addEntry(teams[6], "Russell Westbrook");
		fantasy.addEntry(teams[6], "Kentavious Caldwell-Pope");
		fantasy.addEntry(teams[6], "Jeff Teague");
		fantasy.addEntry(teams[6], "Bojan Bogdanovic");
		fantasy.addEntry(teams[6], "Nikola Vucevic");
		fantasy.addEntry(teams[6], "Jae Crowder");
		fantasy.addEntry(teams[6], "Cody Zeller");
		fantasy.addEntry(teams[6], "Robin Lopez");
		fantasy.addEntry(teams[6], "Gary Harris");
		fantasy.addEntry(teams[6], "Jordan Clarkson");
		fantasy.addEntry(teams[6], "Rudy Gobert");
		fantasy.addEntry(teams[6], "James Johnson");
		fantasy.addEntry(teams[6], "Jimmy Butler");
		
		fantasy.addEntry(teams[7], "Ricky Rubio");
		fantasy.addEntry(teams[7], "Tyler Johnson");
		fantasy.addEntry(teams[7], "Kemba Walker");
		fantasy.addEntry(teams[7], "Kent Bazemore");
		fantasy.addEntry(teams[7], "Kevin Durant");
		fantasy.addEntry(teams[7], "Serge Ibaka");
		fantasy.addEntry(teams[7], "Steven Adams");
		fantasy.addEntry(teams[7], "Jonas Valanciunas");
		fantasy.addEntry(teams[7], "Jamal Crawford");
		fantasy.addEntry(teams[7], "Tony Allen");
		fantasy.addEntry(teams[7], "Kyrie Irving");
		fantasy.addEntry(teams[7], "Greg Monroe");
		fantasy.addEntry(teams[7], "Andre Drummond");
		
		fantasy.addEntry(teams[8], "Derrick Rose");
		fantasy.addEntry(teams[8], "Rodney Hood");
		fantasy.addEntry(teams[8], "Dwyane Wade");
		fantasy.addEntry(teams[8], "Carmelo Anthony");
		fantasy.addEntry(teams[8], "Dwight Howard");
		fantasy.addEntry(teams[8], "Kawhi Leonard");
		fantasy.addEntry(teams[8], "Anthony Davis");
		fantasy.addEntry(teams[8], "DeAndre Jordan");
		fantasy.addEntry(teams[8], "Joel Embiid");
		fantasy.addEntry(teams[8], "D'Angelo Russell");
		fantasy.addEntry(teams[8], "Emmanuel Mudiay");
		fantasy.addEntry(teams[8], "Patrick Beverley");
		fantasy.addEntry(teams[8], "Chandler Parsons");
		
		fantasy.addEntry(teams[9], "Stephen Curry");
		fantasy.addEntry(teams[9], "Tyson Chandler");
		fantasy.addEntry(teams[9], "Andre Iguodala");
		fantasy.addEntry(teams[9], "Harrison Barnes");
		fantasy.addEntry(teams[9], "Aaron Gordon");
		fantasy.addEntry(teams[9], "Andrew Wiggins");
		fantasy.addEntry(teams[9], "Dwight Powell");
		fantasy.addEntry(teams[9], "Mason Plumlee");
		fantasy.addEntry(teams[9], "Moe Harkless");
		fantasy.addEntry(teams[9], "Klay Thompson");
		fantasy.addEntry(teams[9], "Tristan Thompson");
		fantasy.addEntry(teams[9], "LaMarcus Aldridge");
		fantasy.addEntry(teams[9], "Nick Young");
	}
	
	private static Double round(Double value, int places) {
		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}
}
