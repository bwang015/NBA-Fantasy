package minheap;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import database.Data;
import database.Statline;

public class MinHeap {
	private static MinHeap instance = new MinHeap();
	HashMap<String, Heap> topStats = new HashMap<String, Heap>();
	Data data = Data.getInstance();
	
	public final String OVERALL = "overall_value";
	public final String POINTS = "points_value";
	public final String REBOUNDS = "rebound_value";
	public final String ASSISTS = "assist_value";
	public final String THREES = "three_value";
	public final String STEALS = "steal_value";
	public final String BLOCKS = "block_value";
	private static int HEAP_SIZE;
	
	
	static {
		Properties prop = new Properties();
		InputStream input = null;
		
		try {
			input = new FileInputStream("conf/config.properties");
			
			//load a properties file
			prop.load(input);
			
			HEAP_SIZE = Integer.parseInt(prop.getProperty("league.heap.stats", "10"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private MinHeap(){}
	
	public static MinHeap getInstance(){
		return instance;
	}

	public void add(String name, Statline s) {
		Statline historic = data.getPlayerStats(name);
		if(historic == null || s.getMinutes() < 25 || s.isInjured())
			return;
		
		if(s.getOverallValue() > 0 && percentage(s.getOverallValue(), historic.getOverallValue()) > 0){
			//Overall
			if(topStats.containsKey(OVERALL)){
				Heap tmp = topStats.get(OVERALL);
				tmp.add(name, percentage(s.getOverallValue(), historic.getOverallValue()), s, historic.getMinutes());
			}else{
				Heap tmp = new Heap(HEAP_SIZE);
				tmp.add(name, percentage(s.getOverallValue(), historic.getOverallValue()), s, historic.getMinutes());
				topStats.put(OVERALL, tmp);
			}
		}
		
		if(s.getOverallPoints() > 0 && percentage(s.getPoints(), historic.getPoints()) > 0){
			//Points
			if(topStats.containsKey(POINTS)){
				Heap tmp = topStats.get(POINTS);
				tmp.add(name, percentage(s.getPoints(), historic.getPoints()), s, historic.getMinutes());
			}else{
				Heap tmp = new Heap(HEAP_SIZE);
				tmp.add(name, percentage(s.getPoints(), historic.getPoints()), s, historic.getMinutes());
				topStats.put(POINTS, tmp);
			}
		}
		
		if(s.getOverallRebounds() > 0 && percentage(s.getRebounds(), historic.getRebounds()) > 0){
			// Rebounds
			if (topStats.containsKey(REBOUNDS)) {
				Heap tmp = topStats.get(REBOUNDS);
				tmp.add(name, percentage(s.getRebounds(), historic.getRebounds()), s, historic.getMinutes());
			} else {
				Heap tmp = new Heap(HEAP_SIZE);
				tmp.add(name, percentage(s.getRebounds(), historic.getRebounds()), s, historic.getMinutes());
				topStats.put(REBOUNDS, tmp);
			}	
		}	
		
		if(s.getOverallAssists() > 0 && percentage(s.getAssists(), historic.getAssists()) > 0){
			// Assists
			if (topStats.containsKey(ASSISTS)) {
				Heap tmp = topStats.get(ASSISTS);
				tmp.add(name, percentage(s.getAssists(), historic.getAssists()), s, historic.getMinutes());
			} else {
				Heap tmp = new Heap(HEAP_SIZE);
				tmp.add(name, percentage(s.getAssists(), historic.getAssists()), s, historic.getMinutes());
				topStats.put(ASSISTS, tmp);
			}	
		}
		
		if(s.getOverallThrees() > 0 && percentage(s.getThrees(), historic.getThrees()) > 0){
			// Threes
			if (topStats.containsKey(THREES)) {
				Heap tmp = topStats.get(THREES);
				tmp.add(name, percentage(s.getThrees(), historic.getThrees()), s, historic.getMinutes());
			} else {
				Heap tmp = new Heap(HEAP_SIZE);
				tmp.add(name, percentage(s.getThrees(), historic.getThrees()), s, historic.getMinutes());
				topStats.put(THREES, tmp);
			}	
		}
		
		if(s.getOverallSteals() > 0 && percentage(s.getSteals(), historic.getSteals()) > 0){
			// Steals
			if (topStats.containsKey(STEALS)) {
				Heap tmp = topStats.get(STEALS);
				tmp.add(name, percentage(s.getSteals(), historic.getSteals()), s, historic.getMinutes());
			} else {
				Heap tmp = new Heap(HEAP_SIZE);
				tmp.add(name, percentage(s.getSteals(), historic.getSteals()), s, historic.getMinutes());
				topStats.put(STEALS, tmp);
			}	
		}
		
		if(s.getOverallBlocks() > 0 && percentage(s.getBlocks(), historic.getBlocks()) > 0){
			// Blocks
			if (topStats.containsKey(BLOCKS)) {
				Heap tmp = topStats.get(BLOCKS);
				tmp.add(name, percentage(s.getBlocks(), historic.getBlocks()), s, historic.getMinutes());
			} else {
				Heap tmp = new Heap(HEAP_SIZE);
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