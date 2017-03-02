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

public class LeagueStats {
	JSONParser parser;
	Stack<Tuple> pts = new Stack<Tuple>();
	Stack<Tuple> rebs = new Stack<Tuple>();
	Stack<Tuple> to = new Stack<Tuple>();
	Stack<Tuple> blk = new Stack<Tuple>();
	Stack<Tuple> three_ptm = new Stack<Tuple>();
	Stack<Tuple> stl = new Stack<Tuple>();
	Stack<Tuple> ast = new Stack<Tuple>();
	Stack<Tuple> fgp = new Stack<Tuple>();
	Stack<Tuple> ftp = new Stack<Tuple>();
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
		Heap points = new Heap();
		Heap rebounds = new Heap();
		Heap turnovers = new Heap();
		Heap blocks = new Heap();
		Heap three = new Heap();
		Heap steals = new Heap();
		Heap assists = new Heap();
		Heap field_goal = new Heap();
		Heap free_throw = new Heap();

		for (String team : score.keySet()) {
			LeagueLine tmp = score.get(team);

			points.add(team, tmp.getPts());
			rebounds.add(team, tmp.getRebs());
			turnovers.add(team, tmp.getTo());
			blocks.add(team, tmp.getBlk());
			three.add(team, tmp.getThrees());
			steals.add(team, tmp.getStl());
			assists.add(team, tmp.getAst());

			int fga = tmp.getFGA();
			int fgm = tmp.getFGM();
			field_goal.add(team, new Tuple(team, percent(fgm, fga)));

			int fta = tmp.getFTA();
			int ftm = tmp.getFTM();
			free_throw.add(team, new Tuple(team, percent(ftm, fta)));
		}

		parseHeap(points, pts);
		parseHeap(rebounds, rebs);
		parseHeap(turnovers, to, "turnovers"); // Dummy String used for
												// polymorphism
		parseHeap(blocks, blk);
		parseHeap(three, three_ptm);
		parseHeap(assists, ast);
		parseHeap(steals, stl);
		parseHeap(field_goal, fgp);
		parseHeap(free_throw, ftp);
	}

	private int percent(int fgm, int fga) {
		int tmp = fgm * 100;
		int tmp2 = tmp / fga;
		return tmp2;
	}

	private void parseHeap(Heap stats, Stack<Tuple> data) {
		int rank = 1;
		Heap sort = new Heap(); // sort by week
		int prev = Integer.MAX_VALUE;
		Stack<Tuple> prev_value = new Stack<Tuple>();
		while (!stats.isEmpty()) {
			Tuple stat_value = stats.pop();
			if(prev == stat_value.getValue()){
				ArrayList<Tuple> list = new ArrayList<Tuple>();
				while(!prev_value.isEmpty() && prev_value.peek().getValue() == prev){
					list.add(prev_value.pop());
					rank--;
				}
				
				for(int i = 0; i < list.size(); i++){
					prev_value.add(list.get(i));
				}
				
				sort.add(stat_value.getName(), new Tuple(stat_value.getName(), rank));
				prev_value.add(stat_value);
				rank = rank + 1 + list.size();
			}else{
				sort.add(stat_value.getName(), new Tuple(stat_value.getName(), rank++));
				prev_value.push(stat_value);
			}
			
			prev = stat_value.getValue();
		}

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

	private void parseHeap(Heap stats, Stack<Tuple> data, String msg) {
		int rank = 10;
		Heap sort = new Heap(); // sort by week
		while (!stats.isEmpty()) {
			Tuple tmp = stats.pop();
			sort.add(tmp.getName(), new Tuple(tmp.getName(), rank--));
		}

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
			data.add(combine.pop());
		}
	}

	public void display() {
		HashMap<String, Integer> overall = new HashMap<String, Integer>();

		int rank = 1;
		System.out.println("FG %");
		while (!fgp.isEmpty()) {
			Tuple tmp = fgp.pop();
			overall.put(tmp.getName(), tmp.getValue());
			 if (tmp.getName().equals(myTeam) || tmp.getName().equals(otherTeam))
				 System.out.println("\tRank " + rank + ": " + tmp.getName() + " " + tmp.getValue());
			rank++;
		}
		System.out.println();

		rank = 1;
		System.out.println("FT %");
		while (!ftp.isEmpty()) {
			Tuple tmp = ftp.pop();
			overall.put(tmp.getName(), tmp.getValue() + overall.get(tmp.getName()));
			 if (tmp.getName().equals(myTeam) || tmp.getName().equals(otherTeam))
				 System.out.println("\tRank " + rank + ": " + tmp.getName() + " " + tmp.getValue());
			rank++;
		}
		System.out.println();

		rank = 1;
		System.out.println("3PTM");
		while (!three_ptm.isEmpty()) {
			Tuple tmp = three_ptm.pop();
			overall.put(tmp.getName(), tmp.getValue() + overall.get(tmp.getName()));
			if (tmp.getName().equals(myTeam) || tmp.getName().equals(otherTeam))
				System.out.println("\tRank " + rank + ": " + tmp.getName() + " " + tmp.getValue());
			rank++;
		}

		System.out.println();

		rank = 1;
		System.out.println("POINTS");
		while (!pts.isEmpty()) {
			Tuple tmp = pts.pop();
			overall.put(tmp.getName(), tmp.getValue() + overall.get(tmp.getName()));
			if (tmp.getName().equals(myTeam) || tmp.getName().equals(otherTeam))
				System.out.println("\tRank " + rank + ": " + tmp.getName() + " " + tmp.getValue());
			rank++;
		}

		System.out.println();

		rank = 1;
		System.out.println("REBOUNDS");
		while (!rebs.isEmpty()) {
			Tuple tmp = rebs.pop();
			overall.put(tmp.getName(), tmp.getValue() + overall.get(tmp.getName()));
			if (tmp.getName().equals(myTeam) || tmp.getName().equals(otherTeam))
				System.out.println("\tRank " + rank + ": " + tmp.getName() + " " + tmp.getValue());
			rank++;
		}

		System.out.println();

		rank = 1;
		System.out.println("ASSISTS");
		while (!ast.isEmpty()) {
			Tuple tmp = ast.pop();
			overall.put(tmp.getName(), tmp.getValue() + overall.get(tmp.getName()));
			if (tmp.getName().equals(myTeam) || tmp.getName().equals(otherTeam))
				System.out.println("\tRank " + rank + ": " + tmp.getName() + " " + tmp.getValue());
			rank++;
		}

		System.out.println();

		rank = 1;
		System.out.println("STEALS");
		while (!stl.isEmpty()) {
			Tuple tmp = stl.pop();
			overall.put(tmp.getName(), tmp.getValue() + overall.get(tmp.getName()));
			if (tmp.getName().equals(myTeam) || tmp.getName().equals(otherTeam))
				System.out.println("\tRank " + rank + ": " + tmp.getName() + " " + tmp.getValue());
			rank++;
		}

		System.out.println();

		rank = 1;
		System.out.println("BLOCKS");
		while (!blk.isEmpty()) {
			Tuple tmp = blk.pop();
			overall.put(tmp.getName(), tmp.getValue() + overall.get(tmp.getName()));
			if (tmp.getName().equals(myTeam) || tmp.getName().equals(otherTeam))
				System.out.println("\tRank " + rank + ": " + tmp.getName() + " " + tmp.getValue());
			rank++;
		}

		System.out.println();

		rank = 1;
		System.out.println("TURNOVERS");
		while (!to.isEmpty()) {
			Tuple tmp = to.pop();
			overall.put(tmp.getName(), tmp.getValue() + overall.get(tmp.getName()));
			if (tmp.getName().equals(myTeam) || tmp.getName().equals(otherTeam))
				System.out.println("\tRank " + rank + ": " + tmp.getName() + " " + tmp.getValue());
			rank++;
		}

		System.out.println();

		Heap overallValue = new Heap();

		for (String x : overall.keySet()) {
			overallValue.add(x, new Tuple(x, overall.get(x)));
		}

		Stack<Tuple> realoverall = new Stack<Tuple>();

		rank = 1;
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
