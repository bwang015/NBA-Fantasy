package dates;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class Schedule {
	private static List<List<String>> days;
	static Map<String, Integer> numGames;
	
	public static List<List<String>> getSchedule(){
		return days;
	}

	public static void start(String date, String day) throws IOException {
		File file = new File("NBA-2016-17.xls");
		FileInputStream fis = new FileInputStream(file);
		HSSFWorkbook wb = new HSSFWorkbook(fis);
		HSSFSheet ws = wb.getSheet("vertical");
		
		int rowNum = ws.getLastRowNum() + 1;
		int colNum = ws.getRow(0).getLastCellNum();
		
		days = new ArrayList<List<String>>();
		numGames = new HashMap<String, Integer>();
		int start = 0;
		
		for(int i = 1; i < rowNum; i++){
			HSSFRow row = ws.getRow(i);
			String nbaDay = row.getCell(0).toString();
			
			if(nbaDay.substring(3, nbaDay.length()).equals(date)){
				start = i;
				break;
			}
		}
		
		String nbaDay = "random value";
		while(!nbaDay.substring(0, 2).equals("Su")){
			HSSFRow row = ws.getRow(start);
			List<String> games;
			nbaDay = row.getCell(0).toString();

			games = new ArrayList<String>();
			for (int j = 1; j < colNum - 1; j++) {
				String team = row.getCell(j).toString();
				if (!team.isEmpty()) {
					team = formatTeams(team);
					games.add(team);
					
					if(numGames.containsKey(team)){
						numGames.put(team, numGames.get(team) + 1);
					}else{
						numGames.put(team, 1);
					}
				}
			}
			days.add(games);

			start++;
		}
		
		wb.close();
	}
	
	private static String formatTeams(String team){
		team = team.replaceAll("@ ", "").toUpperCase();

		if (team.equals("NO"))
			team = "NOR";
		else if (team.equals("NY"))
			team = "NYK";
		else if (team.equals("SA"))
			team = "SAS";
		else if(team.equals("UTH"))
			team = "UTA";
		
		return team;
	}
	
}
