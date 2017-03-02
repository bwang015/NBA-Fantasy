package minheap;

import java.util.HashMap;

import database.Data;
import database.Statline;

public class MinHeap {
	private static MinHeap instance = new MinHeap();
	HashMap<String, Heap> topStats = new HashMap<String, Heap>();
	Data data = Data.getInstance();
	
	private final String OVERALL = "overall_value";
	private final String POINTS = "points_value";
	private final String REBOUNDS = "rebound_value";
	private final String ASSISTS = "assist_value";
	private final String THREES = "three_value";
	private final String STEALS = "steal_value";
	private final String BLOCKS = "block_value";
	
	private MinHeap(){}
	
	public static MinHeap getInstance(){
		return instance;
	}

	public void add(String name, Statline s) {
		Statline historic = data.getPlayerStats(name);
		if(historic == null || s.getMinutes() < 25 || s.isInjured())
			return;
		
		if(s.getOverallValue() > 0){
			//Overall
			if(topStats.containsKey(OVERALL)){
				Heap tmp = topStats.get(OVERALL);
				tmp.add(name, percentage(s.getOverallValue(), historic.getOverallValue()), s, historic.getMinutes());
			}else{
				Heap tmp = new Heap(20);
				tmp.add(name, percentage(s.getOverallValue(), historic.getOverallValue()), s, historic.getMinutes());
				topStats.put(OVERALL, tmp);
			}
		}
		
		if(s.getPoints() > 15){
			//Points
			if(topStats.containsKey(POINTS)){
				Heap tmp = topStats.get(POINTS);
				tmp.add(name, percentage(s.getPoints(), historic.getPoints()), s, historic.getMinutes());
			}else{
				Heap tmp = new Heap( 20);
				tmp.add(name, percentage(s.getPoints(), historic.getPoints()), s, historic.getMinutes());
				topStats.put(POINTS, tmp);
			}
		}
		
		if(s.getRebounds() > 10){
			// Rebounds
			if (topStats.containsKey(REBOUNDS)) {
				Heap tmp = topStats.get(REBOUNDS);
				tmp.add(name, percentage(s.getRebounds(), historic.getRebounds()), s, historic.getMinutes());
			} else {
				Heap tmp = new Heap(20);
				tmp.add(name, percentage(s.getRebounds(), historic.getRebounds()), s, historic.getMinutes());
				topStats.put(REBOUNDS, tmp);
			}	
		}	
		
		if(s.getAssists() > 8){
			// Assists
			if (topStats.containsKey(ASSISTS)) {
				Heap tmp = topStats.get(ASSISTS);
				tmp.add(name, percentage(s.getAssists(), historic.getAssists()), s, historic.getMinutes());
			} else {
				Heap tmp = new Heap(20);
				tmp.add(name, percentage(s.getAssists(), historic.getAssists()), s, historic.getMinutes());
				topStats.put(ASSISTS, tmp);
			}	
		}
		
		if(s.getThrees() > 3){
			// Threes
			if (topStats.containsKey(THREES)) {
				Heap tmp = topStats.get(THREES);
				tmp.add(name, percentage(s.getThrees(), historic.getThrees()), s, historic.getMinutes());
			} else {
				Heap tmp = new Heap(20);
				tmp.add(name, percentage(s.getThrees(), historic.getThrees()), s, historic.getMinutes());
				topStats.put(THREES, tmp);
			}	
		}
		
		if(s.getSteals() > 2){
			// Steals
			if (topStats.containsKey(STEALS)) {
				Heap tmp = topStats.get(STEALS);
				tmp.add(name, percentage(s.getSteals(), historic.getSteals()), s, historic.getMinutes());
			} else {
				Heap tmp = new Heap(20);
				tmp.add(name, percentage(s.getSteals(), historic.getSteals()), s, historic.getMinutes());
				topStats.put(STEALS, tmp);
			}	
		}
		
		if(s.getBlocks() > 2){
			// Blocks
			if (topStats.containsKey(BLOCKS)) {
				Heap tmp = topStats.get(BLOCKS);
				tmp.add(name, percentage(s.getBlocks(), historic.getBlocks()), s, historic.getMinutes());
			} else {
				Heap tmp = new Heap(20);
				tmp.add(name, percentage(s.getBlocks(), historic.getBlocks()), s, historic.getMinutes());
				topStats.put(BLOCKS, tmp);
			}	
		}
	}

	private double percentage(double current, double historic) {
		if(historic == 0)
			return 0;
		double percent = (current - historic) / Math.abs(historic) * 100;
		return percent;
	}

	public PercentLine[] getOverall(String category) {
		if(!topStats.containsKey(category)){
			return null;
		}
		Heap tmp = topStats.get(category);
		PercentLine t[] = tmp.getList();
		return t;
	}
}