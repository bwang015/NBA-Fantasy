package league;

public class LeagueLine {
	Tuple pts;
	Tuple rebs;
	Tuple tos;
	Tuple blks;
	Tuple threes;
	Tuple stl;
	Tuple ast;
	int ftm;
	int fta;
	int fgm;
	int fga;
	
	public LeagueLine(String teamName, int points, int reb, int to, int blk, int three, int stl, int ast,
			int ftm, int fta, int fgm, int fga) {
		pts = new Tuple(teamName, points);
		rebs = new Tuple(teamName, reb);
		tos = new Tuple(teamName, to);
		blks = new Tuple(teamName, blk);
		threes = new Tuple(teamName, three);
		this.stl = new Tuple(teamName, stl);
		this.ast = new Tuple(teamName, ast);
		this.ftm = ftm;
		this.fta = fta;
		this.fgm = fgm;
		this.fga = fga;
	}
	
	public Tuple getPts() {
		return pts;
	}
	
	public Tuple getRebs() {
		return rebs;
	}
	
	public void setPts(int newPts) {
		pts.setValue(newPts);
	}

	public void setRebs(int newReb) {
		rebs.setValue(newReb);
	}

	public Tuple getTo() {
		return tos;
	}

	public void setTos(int newTO) {
		tos.setValue(newTO);
	}

	public Tuple getBlk() {
		return blks;
	}

	public void setBlk(int newBlk) {
		blks.setValue(newBlk);
	}

	public Tuple getThrees() {
		return threes;
	}

	public void setThrees(int newThree) {
		threes.setValue(newThree);
	}

	public Tuple getStl() {
		return stl;
	}

	public Tuple getAst() {
		return ast;
	}

	public void setStl(int newStl) {
		stl.setValue(newStl);
	}

	public void setAst(int newAst) {
		ast.setValue(newAst);
	}

	public int getFGM() {
		return fgm;
	}

	public int getFGA() {
		return fga;
	}

	public int getFTM() {
		return ftm;
	}

	public int getFTA() {
		return fta;
	}

	public void setFGM(int newFGM) {
		this.fgm = newFGM;
	}

	public void setFGA(int newFGA) {
		this.fga = newFGA;
	}

	public void setFTM(int newFTM) {
		this.ftm = newFTM;
	}

	public void setFTA(int newFTA) {
		this.fta = newFTA;
	}

}
