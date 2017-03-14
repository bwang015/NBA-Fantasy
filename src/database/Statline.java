package database;

import java.text.DecimalFormat;

import org.apache.poi.hssf.usermodel.HSSFCell;

public class Statline extends Calculation{
	private static DecimalFormat df2 = new DecimalFormat(".##");
	private boolean injury = true;
	private String injuryCause;
	private String team;
	double m;
	private double pts;
	private double threes;
	private double reb;
	private double ast;
	private double stl;
	private double blk;
	private double fgp;
	private double fga;
	private double ftp;
	private double fta;
	private double to;
	private double games;
	private double overallValue;
	private double overallPoints;
	private double overallThrees;
	private double overallRebounds;
	private double overallAssists;
	private double overallSteals;
	private double overallBlocks;
	private double overallFG;
	private double overallFT;
	private double overallTO;
	
	/*
	 * 0 - Inj
	 * 1 - Team
	 * 2 - Minutes
	 * 3 - Points
	 * 4 - 3s
	 * 5 - Reb
	 * 6 - Ast
	 * 7 - Stl
	 * 8 - blk
	 * 9 - FG%
	 * 10 - FGA
	 * 11 - FT%
	 * 12 - FTA
	 * 13 - TO
	 * 14 - Games
	 * 15 - Overall Value
	 * 16 - Overall Point
	 * 17 - Overall 3
	 * 18 - Overall Rebounds
	 * 19 - Overall Assists
	 * 20 - Overall Steals
	 * 21 - Overall Blocks
	 * 22 - Overall FG%
	 * 23 - Overall FT%
	 * 24 - Overall Turnovers
	 */
	
	public Statline(HSSFCell[] cell, double weight) {
		String injuried = cell[0].toString();
		this.injuryCause = injuried;
		if(injuried.isEmpty()){
			this.injury = false;
		}else{
			int index = injuried.indexOf(" ");
			if(index == -1){
				this.injury = false;
			}else{
				injuried = injuried.substring(0, index);
				this.injury = injuries.contains(injuried);
			}
		}
		
		this.team = cell[1].toString();
		this.m = cell[2].getNumericCellValue();
		this.pts = cell[3].getNumericCellValue();
		this.threes = cell[4].getNumericCellValue();
		this.reb = cell[5].getNumericCellValue();
		this.ast = cell[6].getNumericCellValue();
		this.stl = cell[7].getNumericCellValue();
		this.blk = cell[8].getNumericCellValue();
		this.fga = cell[10].getNumericCellValue();
		this.fgp = cell[9].getNumericCellValue();
		this.fta = cell[12].getNumericCellValue();
		this.ftp = cell[11].getNumericCellValue();
		this.to = cell[13].getNumericCellValue();
		this.games = cell[14].getNumericCellValue() / weight;
		
		if(weight == 1){
			this.overallValue = cell[15].getNumericCellValue();
			this.overallPoints = cell[16].getNumericCellValue();
			this.overallThrees = cell[17].getNumericCellValue();
			this.overallRebounds = cell[18].getNumericCellValue();
			this.overallAssists = cell[19].getNumericCellValue();
			this.overallSteals = cell[20].getNumericCellValue();
			this.overallBlocks = cell[21].getNumericCellValue();
			this.overallFG = cell[22].getNumericCellValue();
			this.overallFT = cell[23].getNumericCellValue();
			this.overallTO = cell[24].getNumericCellValue();
		}
	}
	
	public Statline(double fGA, double fGP, double fTA, double fTP, double threes, double pts, double reb,
			double ast, double stl, double blk, double to) {
		this.fga = fGA;
		this.fgp = fGP;
		this.fta = fTA;
		this.ftp = fTP;
		this.threes = threes;
		this.pts = pts;
		this.reb = reb;
		this.ast = ast;
		this.stl = stl;
		this.blk = blk;
		this.to = to;
	}
	
	public Statline(){}
	
	public Statline clone(){
		return new Statline(fga, fgp, fta, ftp, threes, pts, reb, ast, stl, blk, to);		
	}
	
	/*
	 * Combining statlines through your roster. 
	 */
	public void combineStatline(Statline s) {
		setFGP(s);
		setFTP(s);
		setThrees(s);
		setPoints(s);
		setRebounds(s);
		setAssists(s);
		setSteals(s);
		setBlocks(s);
		setTurnovers(s);
	}
	
	/*
	 * Combining statlines throughout a player's career
	 */
	public void combineAverageStatline(Statline s) {
		double totalGames = this.games + s.getGames();
		setAverageFGP(s);
		setAverageFTP(s);
		setThrees(s, totalGames);
		setPoints(s, totalGames);
		setRebounds(s, totalGames);
		setAssists(s, totalGames);
		setSteals(s, totalGames);
		setBlocks(s, totalGames);
		setTurnovers(s, totalGames);
		setGames(totalGames);
	}
	
	private void setGames(double totalGames) {
		this.games = totalGames;
	}

	private void setTurnovers(Statline s, double totalGames) {
		this.to = (this.to * this.games + s.getTurnovers() * s.getGames()) / totalGames;
	}

	private void setBlocks(Statline s, double totalGames) {
		this.blk = (this.blk * this.games + s.getBlocks() * s.getGames()) / totalGames;
	}

	private void setSteals(Statline s, double totalGames) {
		this.stl = (this.stl * this.games + s.getSteals() * s.getGames()) / totalGames;
	}

	private void setAssists(Statline s, double totalGames) {
		this.ast = (this.ast * this.games + s.getAssists() * s.getGames()) / totalGames;
 	}

	private void setRebounds(Statline s, double totalGames) {
		this.reb = (this.reb * this.games + s.getRebounds() * s.getGames()) / totalGames;
	}

	private void setPoints(Statline s, double totalGames) {
		this.pts = (this.pts * this.games + s.getPoints() * s.getGames()) / totalGames;
	}

	private void setThrees(Statline s, double totalGames) {
		this.threes = (this.threes * this.games + s.getThrees() * s.getGames()) / totalGames;
	}

	public double getPoints() {
		return pts;
	}
	
	public void setAverageFGP(Statline s) {
		double tmp_FGP = this.fgp;
		double s_FGP = s.getFGP();
		double tmp_FGA = this.fga;
		double s_FGA = s.getFGA();
		
		double tmp_FGM = tmp_FGA * tmp_FGP;
		double s_FGM = s_FGA * s_FGP;
		
		double result = (tmp_FGM * this.games + s_FGM * s.getGames()) / (tmp_FGA * this.games + s_FGA * s.getGames());
		double totalGames = this.games + s.getGames();
		setFGA((tmp_FGA*this.games + s_FGA*s.getGames()) / totalGames);
		this.fgp = result;
	}
	
	public void setFGP(Statline s) {
		double tmp_FGP = this.fgp;
		double s_FGP = s.getFGP();
		double tmp_FGA = this.fga;
		double s_FGA = s.getFGA();
		
		double tmp_FGM = tmp_FGA * tmp_FGP;
		double s_FGM = s_FGA * s_FGP;
		
		double result = (tmp_FGM + s_FGM) / (tmp_FGA + s_FGA);
		setFGA(tmp_FGA + s_FGA);
		this.fgp = result;
	}
	
	public double getGames() {
		return this.games;
	}

	public void setFGP(double FGP, double FGA) {
		double tmp_FGP = getFGP();
		double tmp_FGA = getFGA();
		
		double tmp_FGM = tmp_FGA * tmp_FGP;
		double s_FGM = FGA * FGP;
		
		double result = (tmp_FGM + s_FGM) / (tmp_FGA + FGA);
		
		setFGA(tmp_FGA + FGA);
		this.fgp = result;
	}

	private void setFGA(double result) {
		this.fga = result;
	}

	public double getFGA() {
		return this.fga;
	}

	public static void getFG(Statline a, Statline b) {
		System.out.println("My FG%: " + df2.format(a.getFGP()*100) + "% \t Other FG%: " + df2.format(b.getFGP()*100) + "%");
	}

	public double getFGP() {
		return this.fgp;
	}
	
	public static void getFT(Statline a, Statline b) {
		System.out.println("My FT%: " + df2.format(a.getFTP()*100) + "% \t Other FT%: " + df2.format(b.getFTP()*100) + "%");
	}
	
	public static void getFT(Statline a, Statline c, Statline b) {
		if(c ==  null)
			getFT(a, b);
		else
			System.out.println("My FT%: " + df2.format(a.getFTP()*100) + "% \t Change FT%: " + df2.format(c.getFTP()*100) + "%" + 
		"% \t Other FT%: " + df2.format(b.getFTP()*100) + "%");
	}

	public double getFTP() {
		return this.ftp;
	}

	public void setAverageFTP(Statline s) {
		double tmp_FTP = this.ftp;
		double s_FTP = s.getFTP();
		double tmp_FTA = this.fta;
		double s_FTA = s.getFTA();

		double tmp_FTM = tmp_FTA * tmp_FTP;
		double s_FTM = s_FTA * s_FTP;

		double result = (tmp_FTM * this.games + s_FTM * s.getGames()) / (tmp_FTA * this.games + s_FTA * s.getGames());
		
		setFTA((tmp_FTA*this.games + s_FTA*s.getGames())/(this.games + s.getGames()));
		this.ftp = result;		
	}
	
	public void setFTP(Statline s) {
		double tmp_FTP = this.ftp;
		double s_FTP = s.getFTP();
		double tmp_FTA = this.fta;
		double s_FTA = s.getFTA();

		double tmp_FTM = tmp_FTA * tmp_FTP;
		double s_FTM = s_FTA * s_FTP;

		double result = (tmp_FTM + s_FTM) / (tmp_FTA + s_FTA);
		
		setFTA(tmp_FTA + s_FTA);
		this.ftp = result;		
	}

	private void setFTA(double result) {
		this.fta = result;
	}

	public double getFTA() {
		return this.fta;
	}

	public static void getThrees(Statline currMyTeam, Statline currOtherTeam) {
		System.out.println("My 3 Pointers: " + df2.format(currMyTeam.getThrees()) + " \t Other 3 Pointers: " + df2.format(currOtherTeam.getThrees()));
	}
	
	public static void getThrees(Statline a, Statline c, Statline b) {
		if(c == null)
			getThrees(a, b);
		else
			System.out.println("My 3 Pointers: " + df2.format(a.getThrees()) +  " \t Change 3 Pointers: " 
					+ df2.format(c.getThrees()) + " \t Other 3 Pointers: " + df2.format(b.getThrees()));
	}
	
	public double getThrees() {
		return this.threes;
	}
	
	public void setThrees(Statline s) {
		this.threes = this.threes + s.getThrees();
	}

	public void setPoints(Statline s) {
		this.pts = this.pts + s.getPoints();
	}
	
	public static void getPoints(Statline a, Statline b){
		System.out.println("My Points: " + df2.format(a.getPoints()) + " \t Other Points: " + df2.format(b.getPoints()));
	}
	
	public static void getPoints(Statline a, Statline c, Statline b){
		if(c == null)
			getPoints(a, b);
		else
			System.out.println("My Points: " + df2.format(a.getPoints()) + " \t Other Points: " + df2.format(c.getPoints()) + " \t Other Points: " + df2.format(b.getPoints()));
	}

	public static void getRebounds(Statline a, Statline b) {
		System.out.println("My Rebounds: " + df2.format(a.getRebounds()) + " \t Other Rebounds: " + df2.format(b.getRebounds()));
	}
	
	public static void getRebounds(Statline a, Statline c, Statline b) {
		if(c == null)
			getRebounds(a, b);
		else
			System.out.println("My Rebounds: " + df2.format(a.getRebounds()) + " \t Other Rebounds: " + df2.format(c.getRebounds()) + " \t Other Rebounds: " + df2.format(b.getRebounds()));
	}

	public void setRebounds(Statline s) {
		this.reb = this.reb + s.getRebounds();
	}
	
	public double getRebounds(){
		return this.reb;
	}

	public static void getAssists(Statline a, Statline b) {
		System.out.println("My Assists: " + df2.format(a.getAssists()) + " \t Other Assists: " + df2.format(b.getAssists()));
	}
	
	public static void getAssists(Statline a, Statline c, Statline b) {
		if(c == null)
			getAssists(a, b);
		else
			System.out.println("My Assists: " + df2.format(a.getAssists()) + " \t Other Assists: " + df2.format(c.getAssists()) + " \t Other Assists: " + df2.format(b.getAssists()));
	}

	public double getAssists() {
		return this.ast;
	}

	public void setAssists(Statline s) {
		this.ast = this.ast + s.getAssists();
	}

	public static void getSteals(Statline a, Statline b) {
		System.out.println("My Steals: " + df2.format(a.getSteals()) + " \t Other Steals: " + df2.format(b.getSteals()));
	}
	
	public static void getSteals(Statline a, Statline c, Statline b) {
		if(c == null)
			getSteals(a, b);
		else
			System.out.println("My Steals: " + df2.format(a.getSteals()) + " \t Other Steals: " + df2.format(c.getSteals()) + " \t Other Steals: " + df2.format(b.getSteals()));
	}

	public double getSteals() {
		return this.stl;
	}

	public void setSteals(Statline s) {
		this.stl = this.stl + s.getSteals();
	}

	public static void getBlocks(Statline a, Statline b) {
		System.out.println("My Blocks: " + df2.format(a.getBlocks()) + " \t Other Blocks: " + df2.format(b.getBlocks()));
	}
	
	public static void getBlocks(Statline a, Statline c, Statline b) {
		if(c == null)
			getBlocks(a, b);
		else
			System.out.println("My Blocks: " + df2.format(a.getBlocks()) + " \t Other Blocks: " + df2.format(c.getBlocks()) + " \t Other Blocks: " + df2.format(b.getBlocks()));
	}

	public void setBlocks(Statline s) {
		this.blk = this.blk + s.getBlocks();
	}

	public double getBlocks() {
		return this.blk;
	}

	public static void getTurnovers(Statline a, Statline b) {
		System.out.println("My Turnovers: " + df2.format(a.getTurnovers()) + " \t Other Turnovers: " + df2.format(b.getTurnovers()));
	}
	
	public static void getTurnovers(Statline a, Statline c, Statline b) {
		if(c == null)
			getTurnovers(a, b);
		else
			System.out.println("My Turnovers: " + df2.format(a.getTurnovers()) + " \t Other Turnovers: " + df2.format(c.getTurnovers()) + " \t Other Turnovers: " + df2.format(b.getTurnovers()));
	}

	public void setTurnovers(Statline s) {
		this.to = this.to + s.getTurnovers();
	}

	public double getTurnovers() {
		return this.to;
	}
	
	public boolean isInjured(){
		return this.injury;
	}

	public String getTeam() {
		return this.team;
	}

	public void setInjury(boolean b) {
		this.injury = true;
	}

	public static void getFG(Statline myStat, Statline changedStat, Statline otherStat) {
		if(changedStat == null){
			getFG(myStat, otherStat);
		}else{
			System.out.println("My FG%: " + df2.format(myStat.getFGP()*100) + "% \t Change FG%: " + df2.format(changedStat.getFGP()*100) + 
					"% \t Other FG%: " + df2.format(otherStat.getFGP()*100) + "%");
		}
	}

	public double getOverallValue() {
		return this.overallValue;
	}

	public void displayValues() {
		System.out.print("Overall Value: " + df2.format(this.overallValue) + " \t");
		System.out.print("Overall Points: " + df2.format(this.overallPoints) + " \t");
		System.out.print("Overall 3s: " + df2.format(this.overallThrees) + " \t");
		System.out.print("Overall Rebounds: " + df2.format(this.overallRebounds) + " \t");
		System.out.print("Overall Assists: " + df2.format(this.overallAssists) + " \t");
		System.out.print("Overall Steals: " + df2.format(this.overallSteals) + " \t");
		System.out.print("Overall Blocks: " + df2.format(this.overallBlocks) + " \t");
		System.out.print("Overall Field Goal: " + df2.format(this.overallFG) + " \t");
		System.out.print("Overall Free Throw: " + df2.format(this.overallFT) + " \t");
		System.out.print("Overall Turnovers: " + df2.format(this.overallTO) + " \t");
	}

	public void combineStatlineValue(Statline s) {
		setOverallValue(s);		
		setOverallPoints(s);
		setOverallThrees(s);
		setOverallRebounds(s);
		setOverallAssists(s);
		setOverallSteals(s);
		setOverallBlocks(s);
		setOverallFG(s);
		setOverallFT(s);
		setOverallTO(s);
	}
	
	private void setOverallTO(Statline s) {
		this.overallTO += s.getOverallTO();
	}

	private double getOverallTO() {
		return this.overallTO;
	}

	private void setOverallFT(Statline s) {
		this.overallFT += s.getOverallFT();
	}

	private double getOverallFT() {
		return this.overallFT;
	}

	private void setOverallFG(Statline s) {
		this.overallFG += s.getOverallFG();
	}

	private double getOverallFG() {
		return this.overallFG;
	}

	private void setOverallBlocks(Statline s) {
		this.overallBlocks += s.getOverallBlocks();
	}

	public double getOverallBlocks() {
		return this.overallBlocks;
	}

	private void setOverallSteals(Statline s) {
		this.overallSteals += s.getOverallSteals();
	}

	public double getOverallSteals() {
		return this.overallSteals;
	}

	private void setOverallAssists(Statline s) {
		this.overallAssists += s.getOverallAssists();
	}

	public double getOverallAssists() {
		return this.overallAssists;
	}

	private void setOverallRebounds(Statline s) {
		this.overallRebounds += s.getOverallRebounds();
	}

	public double getOverallRebounds() {
		return this.overallRebounds;
	}

	private void setOverallThrees(Statline s) {
		this.overallThrees += s.getOverallThrees();
	}

	public double getOverallThrees() {
		return this.overallThrees;
	}

	private void setOverallPoints(Statline s) {
		this.overallPoints += s.getOverallPoints();
	}

	public double getOverallPoints() {
		return this.overallPoints;
	}
	
	public double getMinutes() {
		return this.m;
	}

	private void setOverallValue(Statline s) {
		this.overallValue += s.getOverallValue();
	}

	public String getInjuryCause() {
		return injuryCause;
	}
}
