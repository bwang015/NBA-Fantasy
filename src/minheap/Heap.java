package minheap;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

import database.Statline;

public class Heap {
	private final int SIZE;
	PriorityQueue<PercentLine> queue;
	
	public Heap(int capacity){
		this.SIZE = capacity;
		queue = new PriorityQueue<PercentLine>(SIZE, new Comparator<PercentLine>(){

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
	}
	
	public String add(String name, Double value, Statline s, Double historic_minutes){
		queue.add(new PercentLine(name, value, s, historic_minutes));
		return guardOverflow();
	}

	public String add(String name, Double value){
		queue.add(new PercentLine(name, value, null, null));
		return guardOverflow();
	}
	
	public int size(){
		return queue.size();
	}
	
	private String guardOverflow() {
		if(queue.size() > SIZE){
			return queue.poll().getName();
		}
		
		return null;
	}
	
	public Iterator<PercentLine> getKeys(){
		return queue.iterator();
	}
	
	public void clear(){
		queue.clear();
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
