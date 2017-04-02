package org;

import java.sql.SQLException;

//This class tracks how many students of a particular course are allocated a particular room. 
//For eg, Enterprize Computing[64]: 64 students of this course are allocated a particular room.

public class OccupationData {

	private int allocatedStudents;
	private Course course;
	private String side;
	
	public int getAllocatedStudents() {
		return allocatedStudents;
	}
	public Course getCourse() {
		return course;
	}
	public String getSide() {
		return side;
	}
	public OccupationData(Course course,int allocatedStudents,String side)
	{
		this.course=course;
		this.allocatedStudents=allocatedStudents;
		this.side=side;
	}
	
	//copy constructor
	public OccupationData(OccupationData other) throws ClassNotFoundException, SQLException
	{
		this.allocatedStudents=other.getAllocatedStudents();
		this.course=new Course(other.getCourse());//calling copy constructor of Course
		this.side=other.side;
	}
	public void setSide(String side) {
		this.side = side;
	}
	public String toString()
	{
		return course+"["+allocatedStudents+"]";
	}
	public void setAllocatedStudents(int allocatedStudents) {
		this.allocatedStudents = allocatedStudents;
	}
}
