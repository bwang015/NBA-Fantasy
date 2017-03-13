package roster;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import database.Data;
import database.Statline;
import dates.Schedule;
import minheap.Heap;
import minheap.PercentLine;

public class Player extends Roster{
	Map<String, Statline> compare = new HashMap<String, Statline>();
	Map<String, Set<String>> playerRoster = new HashMap<String, Set<String>>();
	Map<String, Set<String>> injuries = new HashMap<String, Set<String>>();
	Map<String, Set<String>> bench = new HashMap<String, Set<String>>();
	Map<String, Integer> numPlayers = new HashMap<String, Integer>();
	Data stats = Data.getInstance();
	List<List<String>> schedule; 
	
	public Player(String customDate, String customDay) throws IOException{
		if(!customDate.isEmpty() && !customDay.isEmpty()){
			Schedule.start(customDate, customDay);
		}else{
			SimpleDateFormat monthDay = new SimpleDateFormat("MM/dd");
			SimpleDateFormat Day = new SimpleDateFormat("E");
			Date date = new Date();
			Schedule.start(monthDay.format(date), Day.format(date));
		}
		schedule = Schedule.getSchedule();
	}
	
	
	public void addEntry(String team, String player){
		Set<String> set;
		if(playerRoster.containsKey(team)){
			set = playerRoster.get(team);
			set.add(player);
		}else{
			set = new HashSet<String>();
			set.add(player);
			playerRoster.put(team, set);
		}
	}
	
	public void addStatline(String team, Statline stat) {
		compare.put(team, stat);	
	}
	
	private void combineStatline(String team, Statline s) {
		if(compare.containsKey(team)){
			Statline tmp = compare.get(team);
			tmp.combineStatline(s);
		}else{
			compare.put(team, s.clone());
		}
	}

	public void compare(String myTeam, String change, String otherTeam){
		Team t = Team.getInstance();
		
		Set<String> myRoster = playerRoster.get(myTeam);
		Set<String> changedRoster = playerRoster.get(change);
		Set<String> otherRoster = playerRoster.get(otherTeam);
		
		numPlayers.put(myTeam, 0);
		numPlayers.put(change, 0);
		numPlayers.put(otherTeam, 0);
		
		int myNum = 0, replacementNum = 0, otherNum = 0;
		Heap myHeap = new Heap(10);
		Heap changeHeap = new Heap(10);
		Heap otherHeap = new Heap(10);
		for(int i = 0; i < schedule.size(); i++){
			List<String> day = schedule.get(i);
			for(String game: day){
				Set<String> teamRoster = t.getTeam(game);
				for(String playerName: teamRoster){
					if(myRoster.contains(playerName))
						myNum += setLineup(playerName, myTeam, myHeap);
					
					if(changedRoster != null && changedRoster.contains(playerName))
						replacementNum += setLineup(playerName, change, changeHeap);
					
					if(otherRoster.contains(playerName))
						otherNum += setLineup(playerName, otherTeam, otherHeap);
				}
			}
			
			Statline myStat = compare.get(myTeam);
			Statline changedStat = compare.get(change);
			Statline otherStat = compare.get(otherTeam);
			int dayNum = 7 - schedule.size() + i + 1;
			
			if(changedStat == null)
				System.out.println("Day " + dayNum + ": My Players: " + myNum + " Other Players: " + otherNum);
			else
				System.out.println("Day " + dayNum + ": My Players: " + myNum + " Changed Players: " + replacementNum + " Other Players: " + otherNum);
			
			
			numPlayers.put(myTeam, numPlayers.get(myTeam) + myNum);
			numPlayers.put(change, numPlayers.get(change) + replacementNum);
			numPlayers.put(otherTeam, numPlayers.get(otherTeam) + otherNum);
			myNum = 0; 
			otherNum = 0;
			replacementNum = 0;
			
			aggregateStats(myTeam, myHeap.getKeys());
			aggregateStats(change, changeHeap.getKeys());
			aggregateStats(otherTeam, otherHeap.getKeys());
			
			if(myStat == null){
				myStat = compare.get(myTeam);
			}
			if(otherStat == null){
				otherStat = compare.get(otherTeam);
			}
			
			Statline.getFG(myStat, changedStat, otherStat);
			Statline.getFT(myStat, changedStat, otherStat);
			Statline.getThrees(myStat, changedStat, otherStat);
			Statline.getPoints(myStat, changedStat, otherStat);
			Statline.getRebounds(myStat, changedStat, otherStat);
			Statline.getAssists(myStat, changedStat, otherStat);
			Statline.getSteals(myStat, changedStat, otherStat);
			Statline.getBlocks(myStat, changedStat, otherStat);
			Statline.getTurnovers(myStat, changedStat, otherStat);
			
			System.out.println();
			
			getAll(myTeam, change, otherTeam);
			clearInjuries(myTeam, change, otherTeam, myHeap, changeHeap, otherHeap);
			
			System.out.println();
		}
	}

	private void aggregateStats(String team, Iterator<PercentLine> keys) {
		while(keys.hasNext()){
			combineStatline(team, stats.getPlayerStats(keys.next().getName()));
		}
	}


	private int setLineup(String playerName, String myTeam, Heap teamHeap) {
		Statline s = stats.getPlayerStats(playerName);
		if(s == null){
			System.err.println("Could not find " + playerName);
			System.exit(0);
			
		}
		if(s.isInjured()){
			if(injuries.containsKey(myTeam)){
				Set<String> set = injuries.get(myTeam);
				set.add(playerName);
			}else{
				Set<String> set = new HashSet<String>();
				set.add(playerName);
				injuries.put(myTeam, set);
			}
		}
		
		if(!s.isInjured()){
			if(teamHeap.size() + 1 > 10){
				if(!bench.containsKey(myTeam)){
					Set<String> set = new HashSet<String>();
					set.add(teamHeap.add(playerName, s.getOverallValue()));
					bench.put(myTeam, set);
				}else{
					Set<String> set = bench.get(myTeam);
					set.add(teamHeap.add(playerName, s.getOverallValue()));
				}
				return 0;
			}else{
				teamHeap.add(playerName, s.getOverallValue());
				return 1;
			}
		}
		return 0;
	}


	public void addStatline(String team, double FGA, double FGP, double FTA, double FTP, double threes, 
			double pts, double reb, double ast, double stl, double blk, double to) {
		Statline curr = compare.get(team);
		curr.setFGP(FGA, FGP);
	}


	public void changeTeam(String team, String[] drop, String[] add) {
		Set<String> masterSet = playerRoster.get(team);
		Set<String> after = new HashSet<String>(masterSet);
		
		for(String x: drop){
			after.remove(x);
		}
		
		for(String x: add){
			after.add(x);
		}
		
		playerRoster.put("replace", after);
	}


	public void getAll(String myTeam, String change, String otherTeam) {
		Set<String> set = injuries.get(myTeam);
		Set<String> benchedPlayers = bench.get(myTeam);
		if(set != null && !set.isEmpty()){
			System.out.println("List of Injuried players for " + myTeam);
			for(String x: set){
				System.out.println(x);
			}
		}
		if(benchedPlayers != null && !benchedPlayers.isEmpty()){
			System.out.println("List of Benched players for " + myTeam);
			for(String x: benchedPlayers){
				System.out.println(x);
			}
		}
		
		set = injuries.get(change);
		benchedPlayers = bench.get(change);
		if(set != null && !set.isEmpty()){
			set = injuries.get(change);
			System.out.println("List of Injuried players for " + change);
			for(String x: set){
				System.out.println(x);
			}
		}
		if(benchedPlayers != null && !benchedPlayers.isEmpty()){
			System.out.println("List of Benched players for " + change);
			for(String x: benchedPlayers){
				System.out.println(x);
			}
		}
		
		set = injuries.get(otherTeam);
		benchedPlayers = bench.get(otherTeam);
		if(set != null && !set.isEmpty()){
			set = injuries.get(otherTeam);
			System.out.println("List of Injuried players for " + otherTeam);
			for(String x: set){
				System.out.println(x);
			}
		}
		if(benchedPlayers != null && !benchedPlayers.isEmpty()){
			System.out.println("List of Benched players for " + otherTeam);
			for(String x: benchedPlayers){
				System.out.println(x);
			}
		}
	}
	
	private void clearInjuries(String myTeam, String change, String otherTeam, Heap myHeap, Heap changeHeap, Heap otherHeap) {
		Set<String> set = injuries.get(myTeam);
		Set<String> benchedPlayers = bench.get(myTeam);
		
		if(set != null)
			set.clear();
		if(benchedPlayers != null)
			benchedPlayers.clear();
		
		set = injuries.get(change);
		benchedPlayers = bench.get(change);
		if(set != null)
			set.clear();
		if(benchedPlayers != null)
			benchedPlayers.clear();
		
		set = injuries.get(otherTeam);
		benchedPlayers = bench.get(otherTeam);
		if(set != null)
			set.clear();
		if(benchedPlayers != null)
			benchedPlayers.clear();
		
		myHeap.clear();
		changeHeap.clear();
		otherHeap.clear();
	}


	public void displayValues(String team) {
		System.out.println(team + " total players: " + numPlayers.get(team));
	}
	
	public Map<String, Set<String>> getPlayerRoster(){
		return playerRoster;
	}


	public Object getPlayerRoster(String teamName) {
		return playerRoster.get(teamName) != null ? playerRoster.get(teamName) : null;
	}


	public boolean dropPlayer(String team, String drop) {
		Set<String> tmp = playerRoster.get(team);
		return tmp.remove(drop);
	}


	public boolean addPlayer(String team, String add) {
		Set<String> tmp = playerRoster.get(team);
		return tmp.add(add);
	}
}
