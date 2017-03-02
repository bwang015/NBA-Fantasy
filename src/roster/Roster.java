package roster;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import database.Data;
import database.Statline;

public class Roster {
	protected static Map<String, Set<String>> teamRoster = new HashMap<String, Set<String>>();
	Data stats = Data.getInstance();

	public void addEntry(String team, String name) {
		Statline prev = stats.getPlayerStats(name);
		if(prev != null){
			String prevTeam = prev.getTeam();
			if(!team.equals(prevTeam)){
				Set<String> roster = teamRoster.get(prevTeam);
				roster.remove(name);
			}else{
				return;
			}
		}
		
		if (teamRoster.containsKey(team)) {
			Set<String> players = teamRoster.get(team);
			players.add(name);
		} else {
			Set<String> players = new HashSet<String>();
			players.add(name);
			teamRoster.put(team, players);
		}
	}
	
	public static Map<String, Set<String>> getTeamRoster(){
		return teamRoster;
	}
}
