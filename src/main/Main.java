package main;

import java.io.IOException;

import org.json.simple.parser.ParseException;

import database.Statline;
import league.LeagueStats;
import roster.Player;

public class Main {
	public static void main(String[] args) throws IOException, ParseException {
		LoadInfo info = new LoadInfo();
		String customDate = info.getCustomDate();
		String customDay = info.getCustomDay();
		String myTeam = info.getMyTeam();
		String otherTeam = info.getOtherTeam();
		
		Player fantasy = new Player(customDate, customDay);
		
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
		
		info.displayInjuries();
		
		info.compare(add, drop, fantasy, myTeam, otherTeam);
		
		league.displayWeekly();
		league.displayOverall();
		
		info.displayHotPlayers(fantasy);
	}
}
