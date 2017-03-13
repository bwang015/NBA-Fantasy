package main.util;

import org.json.simple.JSONObject;

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
}
