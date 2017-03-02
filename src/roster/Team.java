package roster;

import java.util.HashSet;
import java.util.Set;

public class Team extends Roster{
	private static Team obj = new Team();
	
	private Team(){}
	
	public static Team getInstance(){
		return obj;
	}
	
	public Set<String> getTeam(String team){
		Set<String> result = teamRoster.get(team);
		
		if(result == null)
			return new HashSet<String>();
		
		return result;
	}
}
