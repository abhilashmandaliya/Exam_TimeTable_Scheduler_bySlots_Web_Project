package org;

public class Course {

	private String course_id;
	private String course_name;
	private String batch;// B.Tech/M.Tech/M.Sc(IT)
	private int no_Of_Students;// number of students who enrolled for a particular course
	private int unallocated_strength;// number of students who are are still unallocated 
	//when algorithm is running
	private boolean processed;// all the students of this course are assigned a room and this course is 
	//successfully finished in allocation algorithm.

	public Course(String course_id, String course_name,String batch, int no_Of_Students)
	{
		this.course_id = course_id;
		this.course_name = course_name;
		this.no_Of_Students = no_Of_Students;
		this.batch=batch;
		this.unallocated_strength = this.no_Of_Students;
		this.processed = false;
	}

	//copy constructor
	public Course(Course other)
	{
		this.course_id = other.getCourse_id();
		this.course_name = other.getCourse_name();
		this.no_Of_Students = other.getNo_Of_Students();
		this.unallocated_strength = other.getUnallocated_strength();
		this.processed = other.getProcessed();
		this.batch=other.getBatch();
	}
                                                                       
	public String getBatch() {
		return batch;
	}
	
	public String getCourse_id() {
		return course_id;
	}
	
	public String getCourse_name() {
		return course_name;
	}
	
	public int getNo_Of_Students() {
		return no_Of_Students;
	}
	
	public int getUnallocated_strength() {
		return unallocated_strength;
	}
	
	public boolean getProcessed()
	{
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
	
	//"num" students are assigned a room for exam, so just (unallocated-num) students are still unallocated
	public void setUnallocatedStrength(int num) {
		unallocated_strength = unallocated_strength - num;
	}
	public String toString()
	{
		return "Course ID: "+this.course_id+" Course_name: "+this.course_name;
	}
	
}
