package league;

public class Tuple {
	private String teamName;
	private int value;
	
	public Tuple(String teamName, int value){
		this.teamName = teamName;
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}

	public String getName() {
		return teamName;
	}

	public void setValue(int newValue) {
		value = newValue;
	}
	
	@Override
	public String toString(){
		return "Team: " + teamName + " Value: " + value;
	}
	
	public boolean equals(Object o){
		if(!(o instanceof Tuple))
			return false;
		
		Tuple t = (Tuple) o;
		
		if(this.teamName.equals(t.getName()))
			return true;
		
		return false;
	}
}
