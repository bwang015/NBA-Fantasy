package minheap;

import java.util.Comparator;
import java.util.PriorityQueue;

import database.Data;
import database.Statline;

public class MyTeam {
	private static MyTeam instance = new MyTeam();
	Data data = Data.getInstance();
	
	PriorityQueue<PercentLine> queue = new PriorityQueue<PercentLine>(new Comparator<PercentLine>(){

		@Override
		public int compare(PercentLine o1, PercentLine o2) {
			if(o1.getValue() > o2.getValue())
				return 1;
			else if(o1.getValue() < o2.getValue())
				return -1;
			else
				return 0;
		}
		
	});
	
	public static MyTeam getInstance(){
		return instance;
	}
	private MyTeam(){}
	
	public void add(String name, Statline s) {
		Statline historic = data.getPlayerStats(name);
		queue.add(new PercentLine(name, percentage(s.getOverallValue(), historic.getOverallValue()), s, historic.getMinutes()));
	}
	
	private double percentage(double current, double historic) {
		double percent = (current - historic) / Math.abs(historic) * 100;
		return percent;
	}
	
	public PercentLine[] getList() {
		PercentLine[] tuple = new PercentLine[queue.size()];
		int i = 0;
		while(!queue.isEmpty()){
			tuple[i++] = queue.poll();
		}
		
		return tuple;
	}
}
