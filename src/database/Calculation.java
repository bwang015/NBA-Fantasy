package database;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Calculation {
	/*
	 * 0 - Inj
	 * 1 - Team
	 * 2 - Minutes
	 * 3 - Points
	 * 4 - 3s
	 * 5 - Reb
	 * 6 - Ast
	 * 7 - Stl
	 * 8 - blk
	 * 9 - FG%
	 * 10 - FGA
	 * 11 - FT%
	 * 12 - FTA
	 * 13 - TO
	 * 14 - Games
	 */
	
	protected Set<String> injuries;
	Map<String, Integer> categories;
	
	public Calculation(){
		injuries = new HashSet<String>();
		injuries.add("Out");
		injuries.add("Doubtful");
		injuries.add("Injured");
		injuries.add("Questionable");
		injuries.add("Suspended");
		categories = new HashMap<String, Integer>();
		categories.put("Inj", 0);
		categories.put("Team", 1);
		categories.put("Minutes", 2);
		categories.put("Points", 3);
		categories.put("Games", 14);
		categories.put("FGP", 9);
		categories.put("FGA", 10);
		categories.put("FTP", 11);
		categories.put("FTA", 12);
		categories.put("Three", 4);
		categories.put("Rebounds", 5);
		categories.put("Assists", 6);
		categories.put("Steals", 7);
		categories.put("Blocks", 8);
		categories.put("Turnovers", 13);
	}
}
