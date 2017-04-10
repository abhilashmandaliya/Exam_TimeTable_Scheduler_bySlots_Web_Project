package org;

public class Room{

	private int room_no;
	private int leftStrength; //number of students sitting on left side
	private int rightStrength;//number of students sitting on right side
	private int capacity;//total room capacity
	private boolean checkBigCapacity;// if it's true, then check for that side of the bench where more number 
	//of students are sitting
	private boolean invigilanceRequired;// if true, no fixed faculty assigned yet, still requires invigilation
	//even if some students are present in the room
	private int rightCapacity;//number of people sitting on right side
	private int leftCapacity;
	
	public Room(int room_no,int capacity)
	{
		this.room_no=room_no;
		this.capacity=capacity;
		this.rightStrength=0;
		this.leftStrength=0;
		this.checkBigCapacity=false;
		this.invigilanceRequired=true;
		this.rightCapacity=capacity;
		this.leftCapacity=capacity;
	}
	
	//copy constructor of the room
	public Room(Room other)
	{
		this.room_no=other.getRoom_no();
		this.capacity=other.getCapacity();
		this.rightStrength=other.getRightStrength();
		this.leftStrength=other.getLeftStrength();
		this.checkBigCapacity=other.getCheckBigCapacity();
		this.invigilanceRequired=other.getInvigilanceRequired();
		this.rightCapacity=other.getRightCapacityGET();
		this.leftCapacity=other.getLeftCapacityGET();
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	public int getLeftCapacityGET()
	{
		return leftCapacity;
	}
	public int getRightCapacityGET()
	{
		return rightCapacity;
	}
	public int getLeftCapacity() {
		leftCapacity=capacity-leftStrength;
		return leftCapacity;
	}
	
	public int getRightCapacity() {
		rightCapacity=capacity-rightStrength;
		return rightCapacity;
	}
	
	public int getLeftStrength() {
		return leftStrength;
	}
	
	public int getRightStrength() {
		return rightStrength;
	}
	
	public int getRoom_no() {
		return room_no;
	}
	
	public boolean getInvigilanceRequired()
	{
		return invigilanceRequired;
	}
	
	// by default,every room has no invigilation at start. So,invigilationRequired=true for all. But it may create
	//undesirable effects if just this is checked. So, checking that at least one student is already assigned in that 
	//room is required before processing invigilation.
	//Undesirable effect: While traversing the rooms, if algorithm finds any empty room, it goes inside as room's 
	// invigilation is required by default. But I may require invigilation in next interval for some room. So, in any case, 
	//fresh whole allocation is given to CASE 2 only. invigilation just checks rooms with at least 1 student 
	public boolean checkInvigilanceRequired(int flag_clash,Slot slot,TimeInterval ti)
	{
		int flag_big=0;
		if(flag_clash==1)
		{   
			for(Course course:slot.getCourses())
			{
				if(TimeTableEndSem.ifCourseIsBig(course,ti )==true)
				{
					flag_big=1;
				}
			}
			if(invigilanceRequired==true && flag_big==1)
				return true;
				else return false;
		}
		if(invigilanceRequired==true && (rightStrength>0 || leftStrength>0))
		return true;
		else return false;
	}
	
	
	
	public boolean getCheckBigCapacity()
	{
		return checkBigCapacity;
	}
	
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public void setCheckBigCapacity(boolean checkBigCapacity) {
		this.checkBigCapacity = checkBigCapacity;
	}
	
	public void setInvigilanceRequired(boolean invigilanceRequired) {
		this.invigilanceRequired = invigilanceRequired;
	}
	
	public void setLeftCapacity(int leftCapacity) {
		this.leftCapacity = leftCapacity;
	}
	
	public void setRightCapacity(int rightCapacity) {
		this.rightCapacity = rightCapacity;
	}
	
	public void setLeftStrength(int num)
	{
		leftStrength=leftStrength+num;
	}
	
	public void setRightStrength(int num)
	{
		rightStrength=rightStrength+num;
	}
	public String toString()
	{
		return "";
	}
	
	//sorting descending order
	public int compareTo(Room other)
	{
		if(this.getCapacity()<other.getCapacity())
			return 1;
		else if(this.getCapacity()>other.getCapacity())
			return -1;
		else return 0;					
	}
}