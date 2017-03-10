package main.util;

import org.json.simple.JSONObject;

public class ObjectParser {
	private Double fgp;
	private Double fga;
	private Double fta;
	private Double ftp;
	private Double pts;
	private Double three;
	private Double reb;
	private Double ast;
	private Double stl;
	private Double blk;
	private Double to;
	
	public void parseStats(JSONObject o, String teamName) {
		pts = Double.parseDouble(o.get("pts").toString());
		reb = Double.parseDouble(o.get("reb").toString());
		to = Double.parseDouble(o.get("to").toString());
		blk = Double.parseDouble(o.get("blk").toString());
		three = Double.parseDouble(o.get("3pm").toString());
		stl = Double.parseDouble(o.get("stl").toString());
		ast = Double.parseDouble(o.get("ast").toString());
		fgp = Double.parseDouble(o.get("fgp").toString());
		fga = Double.parseDouble(o.get("fga").toString());
		ftp = Double.parseDouble(o.get("ftp").toString());
		fta = Double.parseDouble(o.get("fta").toString());
	}
	
	public double getFGP(){
		return fgp;
	}
	
	public double getFGA() {
		return fga;
	}
	
	public double getFTA() {
		return fta;
	}
	
	public double getFTP() {
		return ftp;
	}
	
	public double getPTS() {
		return pts;
	}
	
	public double getTRE() {
		return three;
	}
	
	public double getAST() {
		return ast;
	}
	
	public double getREB() {
		return reb;
	}
	
	public double getBLK() {
		return blk;
	}
	
	public double getSTL() {
		return stl;
	}
	
	public double getTO() {
		return to;
	}
}
