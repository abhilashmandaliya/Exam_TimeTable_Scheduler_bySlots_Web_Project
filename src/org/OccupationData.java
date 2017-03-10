package org;



public class OccupationData {

	private int fromRoll;
	 int toRoll;
	private int roomNo;
	private int totalNumber;
	
	public OccupationData(int fromRoll,int toRoll,int roomNo)
	{
		this.fromRoll=fromRoll;
		this.toRoll=toRoll;
		this.roomNo=roomNo;
		this.totalNumber=(toRoll-fromRoll+1);
	}
	
}
