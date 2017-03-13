package org;

import java.sql.SQLException;

//This class tracks how many students of a particular course are allocated a particular room. 
//For eg, Enterprize Computing[64]: 64 students of this course are allocated a particular room.

public class OccupationData {

	private int allocatedStudents;
	private Course course;
	
	public int getAllocatedStudents() {
		return allocatedStudents;
	}
	public Course getCourse() {
		return course;
	}
	public OccupationData(Course course,int allocatedStudents)
	{
		this.course=course;
		this.allocatedStudents=allocatedStudents;
	}
	
	//copy constructor
	public OccupationData(OccupationData other) throws ClassNotFoundException, SQLException
	{
		this.allocatedStudents=other.getAllocatedStudents();
		this.course=new Course(other.getCourse());//calling copy constructor of Course
	}
	
	public String toString()
	{
		return course+"["+allocatedStudents+"]";
	}
}
