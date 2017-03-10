package org;



public class Room{

	int room_no;
	private int leftStrength; //number of students sitting on left side
	private int rightStrength;//number of students sitting on right side
	private int capacity;
	private int timeSlot;
	private boolean checkBig;
	 boolean invigilanceRequired;
	private int rightCapacity;
	private int leftCapacity;
	public Room(int room_no,int capacity)
	{
		System.out.println("Inside default constructor of Room");
		this.room_no=room_no;
		this.capacity=capacity;
		rightStrength=0;
		leftStrength=0;
		timeSlot=0;
		checkBig=true;
		invigilanceRequired=true;
		rightCapacity=capacity;
		leftCapacity=capacity;
		System.out.println("Room: "+room_no+"capacity: "+capacity);
	}
	
	public Room(Room other)
	{
		System.out.println("Inside copy constructor of Room");
		this.room_no=other.room_no;
		this.capacity=other.capacity;
		this.rightStrength=other.rightStrength;
		this.leftStrength=other.leftStrength;
		this.timeSlot=other.timeSlot;
		this.checkBig=other.checkBig;
		this.invigilanceRequired=other.invigilanceRequired;
		this.rightCapacity=other.rightCapacity;
		this.leftCapacity=other.leftCapacity;
	}
	/*@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}*/
	public int getRightCapacity()
	{
		return capacity-rightStrength;
	}
	public int getLeftCapacity()
	{
		return capacity-leftStrength;
	}
	public boolean getCheckBig()
	{
		return checkBig;
	}
	public void setCheckBig(boolean value)
	{
		this.checkBig=value;
	}
	public void setLeftStrength(int num)
	{
		leftStrength=leftStrength+num;
	}
	
	public void setRightStrength(int num)
	{
		rightStrength=rightStrength+num;
	}

	public int getLeftStrength()
	{
		return leftStrength;
	}
	
	public int getRightStrength()
	{
		return rightStrength;
	}
	
	public int getCapacity()
	{
		return capacity;
	}
	
	public boolean getInvigilanceRequired()
	{
		return invigilanceRequired;
	}
}
