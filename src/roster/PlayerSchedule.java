package roster;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import dates.Schedule;

public class PlayerSchedule {
	static List<List<String>> days = Schedule.getSchedule();
	static HashMap<String, Set<String>> teamRoster = null;
	
	public static void getPlayerDates(String name){
		teamRoster = (HashMap<String, Set<String>>) Roster.getTeamRoster();
		String team = null;
		for(String x: teamRoster.keySet()){
			Set<String> roster = teamRoster.get(x);
			
			if(roster.contains(name)){
				team = x;
				break;
			}
		}
		
		if(team == null) //Player not found
			return;
		
		for(int i = 0; i < days.size(); i++){
			List<String> teamsPlaying = days.get(i);
			for(int j = 0; j < teamsPlaying.size(); j++){
				if(teamsPlaying.get(j).equals(team)){
					switch(days.size() - i){
					case 7:
						System.out.print("Monday ");
						break;
					case 6:
						System.out.print("Tuesday ");
						break;
					case 5:
						System.out.print("Wednesday ");
						break;
					case 4:
						System.out.print("Thursday ");
						break;
					case 3:
						System.out.print("Friday ");
						break;
					case 2:
						System.out.print("Saturday ");
						break;
					case 1:
						System.out.print("Sunday ");
						break;
					}
					
					break;
				}
			}
		}
		
		System.out.println();
	}
}
