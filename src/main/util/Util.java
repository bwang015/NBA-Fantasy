package main.util;

import org.json.simple.JSONObject;

import database.Statline;
import minheap.MinHeap;
import minheap.PercentLine;
import roster.PlayerSchedule;

public class Util {
	public static String[] parseTransactions(String transaction) {
		String[] params = new String[3];
		params[0] = transaction.substring(0, transaction.indexOf(':')).trim();
		params[1] = transaction.substring(transaction.indexOf(':') + 1, transaction.indexOf(',')).trim();
		params[2] = transaction.substring(transaction.indexOf(',') + 1, transaction.length()).trim();
		return params;
	}

	public static String[] parseTeams(JSONObject obj) {
		String[] teams = new String[obj.size()];
		for(int i = 0; i < obj.size(); i++){
			int index = i + 1;
			teams[i] = (String) obj.get("team" + index);
		}
		return teams;
	}
	
	public static Double round(Double value, int places) {
		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	public static void displayHeap(PercentLine[] topStats, String category) {
		MinHeap min = MinHeap.getInstance();
		if(topStats == null){
			System.out.println("No Players Currently \n");
			return;
		}
		
		for(int i = topStats.length - 1; i >= 0; i--){
			
			if(topStats[i] == null)
				continue;
			
			Statline s = topStats[i].getStats();
			double percent = round(topStats[i].getValue(), 2);
			double extraValue = 0;
			double minutes = 0;
			double historic_minutes = 0;
			if(category.equals(min.OVERALL)){
				extraValue = round(s.getOverallValue(), 2);
			} else if (category.equals(min.POINTS)) {
				extraValue = round(s.getPoints(), 2);
			} else if (category.equals(min.REBOUNDS)){
				extraValue = round(s.getRebounds(), 2);
			} else if(category.equals(min.ASSISTS)) {
				extraValue = round(s.getAssists(), 2);
			} else if(category.equals(min.THREES)) {
				extraValue = round(s.getThrees(), 2);
			} else if(category.equals(min.STEALS)) {
				extraValue = round(s.getSteals(), 2);
			} else if(category.equals(min.BLOCKS)) {
				extraValue = round(s.getBlocks(), 2);
			}
			
			minutes = round(s.getMinutes(), 2);
			historic_minutes = round(topStats[i].getHistoricMinutes(), 2);
			
			System.out.print(topStats[i].getName() + " " + percent + "% || ");
			System.out.print(extraValue + " || " + minutes + " current minutes || ");
			System.out.print(historic_minutes + " season minutes || ");
			System.out.print(s.getTeam() + " || ");
			PlayerSchedule.getPlayerDates(topStats[i].getName());
		}
		//Formatting
		System.out.println();
	}
}
