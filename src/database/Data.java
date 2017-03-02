package database;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;

public class Data {
	private static Data obj = new Data();
	private Map<String, Statline> playerStatline = new HashMap<String, Statline>();
	
	public static Data getInstance(){
		return obj;
	}

	public Statline addEntry(String name, HSSFCell[] cell, double weight) {
		if(playerStatline.containsKey(name)){
			Statline s = playerStatline.get(name);
			Statline curr = new Statline(cell, weight);
			curr.combineAverageStatline(s);
			playerStatline.put(name, curr);
			return new Statline(cell, weight);
		}else{
			Statline tmp = new Statline(cell, weight);
			playerStatline.put(name, tmp);
			return tmp;
		}
	}

	public Statline getPlayerStats(String name) {
		Statline s = playerStatline.get(name);
		return s;
	}

	public void setInjuries() {
		for(String player: playerStatline.keySet()){
			Statline s = playerStatline.get(player);
			s.setInjury(true);
		}
	}
}
