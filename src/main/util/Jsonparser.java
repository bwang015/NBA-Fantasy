package main.util;

import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Jsonparser extends ObjectParser {
	private JSONParser parser;
	private String json;

	public Jsonparser(String roster_data) {
		parser = new JSONParser();
		this.json = roster_data;
	}

	public JSONObject getObj(String key) {
		JSONObject obj = null;
		try {
			JSONObject o = (JSONObject) parser.parse(new FileReader(json));
			obj = (JSONObject) o.get(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
}
