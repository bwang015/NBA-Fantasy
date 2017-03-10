package league;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import minheap.HeapStats;
import minheap.StackStats;

public class LeagueStats {
	JSONParser parser;
	StackStats sstats = new StackStats();
	String myTeam;
	String otherTeam;

	public LeagueStats(String myTeam, String otherTeam) throws FileNotFoundException, IOException, ParseException {
		this.myTeam = myTeam;
		this.otherTeam = otherTeam;
		parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(new FileReader("League.json"));
		for (int i = 0; i < obj.size(); i++) {
			int week = i + 1;
			JSONArray arr = (JSONArray) obj.get("Week " + week);
			HashMap<String, LeagueLine> score = new HashMap<String, LeagueLine>();
			for (int j = 0; j < arr.size(); j++) {
				JSONObject o = (JSONObject) arr.get(j);
				String teamName = (String) o.get("team");
				long pts = (long) o.get("pts");
				long reb = (long) o.get("reb");
				long to = (long) o.get("to");
				long blk = (long) o.get("blk");
				long three = (long) o.get("3ptm");
				long stl = (long) o.get("stl");
				long ast = (long) o.get("ast");
				long fgm = (long) o.get("fgm");
				long fga = (long) o.get("fga");
				long ftm = (long) o.get("ftm");
				long fta = (long) o.get("fta");

				score.put(teamName, new LeagueLine(teamName, (int) pts, (int) reb, (int) to, (int) blk, (int) three,
						(int) stl, (int) ast, (int) ftm, (int) fta, (int) fgm, (int) fga));
			}
			// rank the scores here then delete
			sort(score);
			score.clear();
		}
	}

	private void sort(HashMap<String, LeagueLine> score) {
		// score has all stats for a specific team -> myTeam, pts:xxx, reb:xxx etc.
		HeapStats hstats = new HeapStats();

		for (String team : score.keySet()) {
			LeagueLine tmp = score.get(team);
			int fga = tmp.getFGA();
			int fgm = tmp.getFGM();
			int fta = tmp.getFTA();
			int ftm = tmp.getFTM();
			
			hstats.add(team, new Tuple(team, percent(fgm, fga)), new Tuple(team, percent(ftm, fta)), 
					tmp.getThrees(), tmp.getPts(), tmp.getRebs(), tmp.getAst(), tmp.getStl(), 
					tmp.getBlk(), tmp.getTo());
		}
		
		parseHeap(hstats, sstats);
	}

	private void parseHeap(HeapStats hstats, StackStats sstats) {
		for(String category : hstats.getKeys()){
			parseHeap(hstats.get(category), sstats.get(category), category);
		}
	}

	private int percent(int fgm, int fga) {
		int tmp = fgm * 100;
		int tmp2 = tmp / fga;
		return tmp2;
	}

	private void parseHeap(Heap stats, Stack<Tuple> data, String category) {
		Heap sort = organizeByStats(stats, category);
		HashMap<String, Integer> map = combineWithPreviousStats(data);
		organizeByPoints(sort, map, data);
	}

	private void organizeByPoints(Heap sort, HashMap<String, Integer> map, Stack<Tuple> data) {
		Heap combine = new Heap();
		while (!sort.isEmpty()) {
			Tuple tmp = sort.pop();
			if (map.containsKey(tmp.getName())) {
				int newRank = tmp.getValue() + map.get(tmp.getName());
				combine.add(tmp.getName(), new Tuple(tmp.getName(), newRank));
			} else {
				combine.add(tmp.getName(), tmp);
			}
		}
		while (!combine.isEmpty()) {
			data.push(combine.pop());
		}
	}

	private HashMap<String, Integer> combineWithPreviousStats(Stack<Tuple> data) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();

		while (!data.isEmpty()) {
			Tuple tmp = data.pop();
			if (map.containsKey(tmp.getName())) {
				int newRank = map.get(tmp.getName()) + tmp.getValue();
				map.put(tmp.getName(), newRank);
			} else {
				map.put(tmp.getName(), tmp.getValue());
			}
		}
		
		return map;
	}

	private Heap organizeByStats(Heap stats, String category) {
		int rank = 1;
		boolean reverse = false;
		if (category.equals("TURNOVERS")) {
			rank = 10;
			reverse = true;
		}
		Heap sort = new Heap(); // sort by week
		int prev = Integer.MAX_VALUE;
		Stack<Tuple> prev_value = new Stack<Tuple>();
		while (!stats.isEmpty()) {
			Tuple stat_value = stats.pop();
			if (prev == stat_value.getValue()) {
				ArrayList<Tuple> list = new ArrayList<Tuple>();
				ArrayList<Tuple> toList = new ArrayList<Tuple>();
				while (!prev_value.isEmpty() && prev_value.peek().getValue() == prev) {
					list.add(prev_value.pop());
					if(reverse){
						rank--;
						toList.add(sort.pop());
					}
					else{
						rank--;
					}
				}

				for (int i = 0; i < list.size(); i++) {
					prev_value.add(list.get(i));
				}
				
				for(int i = 0; i < toList.size(); i++){
					if(i == 0)
						rank += toList.size();
					sort.add(toList.get(i).getName(), new Tuple(toList.get(i).getName(), rank));
				}

				sort.add(stat_value.getName(), new Tuple(stat_value.getName(), rank));
				prev_value.add(stat_value);
				if(reverse)
					rank--;
				else
					rank = rank + 1 + list.size();
			} else {
				if (reverse)
					sort.add(stat_value.getName(), new Tuple(stat_value.getName(), rank--));
				else
					sort.add(stat_value.getName(), new Tuple(stat_value.getName(), rank++));
				prev_value.push(stat_value);
			}

			prev = stat_value.getValue();
		}
		
		return sort;
	}

	public void display() {
		HashMap<String, Integer> overall = new HashMap<String, Integer>();
		
		for(String category: sstats.getKeys()){
			int rank = 1;
			System.out.println(category);
			Stack<Tuple> stack = sstats.get(category);
			
			while(!stack.isEmpty()){
				Tuple tmp = stack.pop();
				if(overall.containsKey(tmp.getName())){
					int addition = overall.get(tmp.getName()) + tmp.getValue();
					overall.put(tmp.getName(), addition);
				}else{
					overall.put(tmp.getName(), tmp.getValue());
				}
				if(tmp.getName().equals(myTeam) || tmp.getName().equals(otherTeam))
					System.out.println("\tRank " + rank + ": " + tmp.getName() + " " + tmp.getValue());
				rank++;
			}
			
			System.out.println();
		}

		Heap overallValue = new Heap();

		for (String x : overall.keySet()) {
			overallValue.add(x, new Tuple(x, overall.get(x)));
		}

		Stack<Tuple> realoverall = new Stack<Tuple>();

		int rank = 1;
		System.out.println("OVERALL");
		while (!overallValue.isEmpty()) {
			realoverall.add(overallValue.pop());
		}

		while (!realoverall.isEmpty()) {
			Tuple tmp = realoverall.pop();
			// if (tmp.getName().equals(myTeam) || tmp.getName().equals(otherTeam))
				System.out.println("\tRank " + rank + ": " + tmp.getName() + " " + tmp.getValue());
			rank++;
		}

		System.out.println();
	}
}
