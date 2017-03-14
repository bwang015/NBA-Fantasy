package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import minheap.MinHeap;
import minheap.MyTeam;
import roster.Player;
import roster.Team;

public class ExportExcel {
	final static int EIGHT = 8;
	final static int FOUR = 4;
	final static int TWO = 2;
	final static int ONE = 1;
	static int LEAGUE_SIZE;
	public static HashMap<String, Statline> notable_injuries = new HashMap<String, Statline>();
	public static String TEAM_PERFORMANCE;
	
	static {
		Properties prop = new Properties();
		InputStream input = null;
		
		try {
			input = new FileInputStream("conf/config.properties");
			
			//load a properties file
			prop.load(input);
			
			TEAM_PERFORMANCE = prop.getProperty("team.status");
			LEAGUE_SIZE = Integer.parseInt(prop.getProperty("league.size"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	
	public static void start() throws IOException {
		export("NBA 2013-2014 Stats.xls", EIGHT, null);
		export("NBA 2014-2015 Stats.xls", EIGHT, null);
		export("NBA 2015-2016 Stats.xls", EIGHT, null);
		setInjuries();
		export("Export.xls", ONE, null);
	}

	private static void setInjuries() {
		Data stats = Data.getInstance();
		stats.setInjuries();
	}

	public static void export(String filename, int weight, Player myLeague) throws IOException {
		File stats = new File(filename);
		FileInputStream fis = new FileInputStream(stats);
		HSSFWorkbook wb = new HSSFWorkbook(fis);
		HSSFSheet ws = wb.getSheet("Sheet 1");
		
		int rowNum = ws.getLastRowNum() + 1;
		
		int[] list = {
			4, 5, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 7, 2, 20, 21, 22, 23, 24, 25, 26, 27, 28
		};

		for(int i = 1; i < rowNum; i++){
			HSSFRow row = ws.getRow(i);
			String name = row.getCell(3).toString();
			String teamName = row.getCell(5).toString();
			HSSFCell[] cell = new HSSFCell[list.length];
			int index = 0;
			for(int j: list){
				cell[index++] = row.getCell(j);
			}
			
			Statline s = new Statline(cell, 1);
			if(weight == 0){
				Map<String, Set<String>> leagueRoster = myLeague.getPlayerRoster();
				int not_on_roster = 0;
				for(String x: leagueRoster.keySet()){
					Set<String> individualRoster = leagueRoster.get(x);
					if(individualRoster.contains(name)){
						if(x.equals(TEAM_PERFORMANCE)){
							MyTeam.getInstance().add(name, s);
						}
						break;
					}
					not_on_roster++;
				}
				
				if(not_on_roster == LEAGUE_SIZE){
					//Player is not on any current myLeague roster
					MinHeap.getInstance().add(name, s);
				}

			}else{
				Team.getInstance().addEntry(teamName, name);
				Data.getInstance().addEntry(name, cell, weight);
			}
			
			if(weight == 1 && s.isInjured()){
				notable_injuries.put(name, s);
			}
		}
		
		wb.close();
	}
}
