package league;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Heap {
	PriorityQueue<Tuple> queue;
	HashMap<String, Tuple> map;
	
	public Heap() {
		map = new HashMap<String, Tuple>();
		queue = new PriorityQueue<Tuple>(new Comparator<Tuple>(){

			@Override
			public int compare(Tuple o1, Tuple o2) {
				if(o1.getValue() > o2.getValue())
					return 1;
				else if(o1.getValue() < o2.getValue())
					return -1;
				else{
					return 0;
				}
			}
			
		});
	}

	public void add(String name, Tuple value) {
		queue.add(value);
		map.put(name, value);
	}

	public int size() {
		return queue.size();
	}

	public Tuple pop() {
		return queue.poll();
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public boolean containsKey(String team) {
		return map.containsKey(team);
	}

	public Tuple get(String team) {
		return map.get(team);
	}

	public Tuple peek() {
		return queue.peek();
	}

	public Tuple tail(Tuple this_tuple) {
		queue.remove(this_tuple);
		Tuple t = map.get(this_tuple.getName());
		map.remove(this_tuple.getName());
		return t;
		
	}	
}
