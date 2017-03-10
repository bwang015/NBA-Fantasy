package minheap;

import league.Heap;
import league.Tuple;

public class HeapStats extends Generics<Heap> {

	public HeapStats() {
		super(new Heap(), new Heap(), new Heap(), new Heap(), new Heap(), new Heap(), new Heap(), new Heap(),
				new Heap());
	}

	public void add(String teamName, Tuple fgp, Tuple ftp, Tuple tre, Tuple pts, Tuple reb, Tuple ast, Tuple stl,
			Tuple blk, Tuple to) {
		this.teamName = teamName;
		this.field_goal.add(teamName, fgp);
		this.free_throw.add(teamName, ftp);
		this.threes.add(teamName, tre);
		this.points.add(teamName, pts);
		this.rebounds.add(teamName, reb);
		this.assists.add(teamName, ast);
		this.steals.add(teamName, stl);
		this.blocks.add(teamName, blk);
		this.turnovers.add(teamName, to);
	}
}
