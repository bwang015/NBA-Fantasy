package minheap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Generics<T> {
	protected T field_goal;
	protected T free_throw;
	protected T threes;
	protected T points;
	protected T rebounds;
	protected T assists;
	protected T steals;
	protected T blocks;
	protected T turnovers;
	protected String teamName;
	Map<String, T> map;
	
	public Generics(T fgp, T ftp, T tre, T pts, T reb, T ast, T stl, T blk, T to){
		field_goal = fgp;
		free_throw = ftp;
		threes = tre;
		points = pts;
		rebounds = reb;
		assists = ast;
		steals = stl;
		blocks = blk;
		turnovers = to;
		
		map = new HashMap<String, T>();
		map.put("FIELD GOAL %", field_goal);
		map.put("FREE THROW %", free_throw);
		map.put("THREES MADE", threes);
		map.put("POINTS", points);
		map.put("REBOUNDS", rebounds);
		map.put("ASSISTS", assists);
		map.put("STEALS", steals);
		map.put("BLOCKS", blocks);
		map.put("TURNOVERS", turnovers);
	}
	
	public String getName(){
		return teamName;
	}
	
	public Set<String> getKeys(){
		return map.keySet();
	}
	
	public T get(String key){
		return map.get(key);
	}
}
