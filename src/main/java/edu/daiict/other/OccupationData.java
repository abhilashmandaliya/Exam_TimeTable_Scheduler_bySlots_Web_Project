package edu.daiict.other;

import java.sql.SQLException;

//This class tracks how many students of a particular course are allocated a particular room. 
//For eg, Enterprize Computing[64]: 64 students of this course are allocated a particular room.

public class OccupationData {

	private int allocatedStudents;
	private Course course;
	private String side;
	private Room room;
	
	public int getAllocatedStudents() {
		return allocatedStudents;
	}
	public Course getCourse() {
		return course;
	}
	public String getSide() {
		return side;
	}
	 public Room getRoom() {
		return room;
	}
	public OccupationData(Course course,int allocatedStudents,String side,Room room)
	{
		this.course=course;
		this.allocatedStudents=allocatedStudents;
		this.side=side;
		this.room=room;
	}
	
	//copy constructor
	public OccupationData(OccupationData other) throws ClassNotFoundException, SQLException {
		allocatedStudents = other.getAllocatedStudents();
		course = new Course(other.getCourse());//calling copy constructor of Course
		side = other.side;
		room = other.room;
	}

	public void setSide(String side) {
		this.side = side;
	}

	@Override
	public String toString() {
		return course + "[" + allocatedStudents + side + "]";
	}

	public void setAllocatedStudents(int allocatedStudents) {
		this.allocatedStudents = allocatedStudents;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
}
