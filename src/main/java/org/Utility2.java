package org;

public class Utility2 {

	String side;
	int room_no;
	int capacity;
	public Utility2(String side, int room_no, int capacity) {
		super();
		this.side = side;
		this.room_no = room_no;
		this.capacity = capacity;
	}
	public String toString()
	{
		return "Room No:"+room_no+"Side:"+side+"Capacity:"+capacity;
	}
}
