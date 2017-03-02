package minheap;

import database.Statline;

public class PercentLine {
	String name;
	Double value;
	Statline s;
	Double historic_minutes;
	
	public Double getValue(){
		return value;
	}

	public String getName(){
		return name;
	}
	
	public PercentLine(String name, Double value){
		this.name = name;
		this.value = value;
	}
	
	public PercentLine(String name, Double value, Statline s, Double historic_minutes){
		this.name = name;
		this.value = value;
		this.s = s;
		this.historic_minutes = historic_minutes;
	}
	
	public Statline getStats(){
		return s;
	}
	
	public Double getHistoricMinutes(){
		return historic_minutes;
	}
}
