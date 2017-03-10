package org;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
//This class is used for dealing with Course relation in DBMS 

public class Course implements Cloneable {

	private final Connection con;
	private String course_id;
	private String course_name;
	private String batch;
	private int no_Of_Students;
	ArrayList<OccupationData> roomData;
	private int unallocated_strength;
	boolean invigilanceEnsured;
	boolean processed = false;
	int allocatedStudents=0;

	public String getCourse_name() {
		return course_name;
	}

	public String getCourse_id() {
		return course_id;
	}

	public String getBatch() {
		return batch;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

//	public Course(String course_id, String course_name, String batch) throws ClassNotFoundException, SQLException// remove
//																		// it
//	{
//		con=DBConnection.getInstance().getConnectionSchema("public");
//		this.course_id = course_id;
//		this.course_name = course_name;
//		this.no_Of_Students = 0;
//		this.batch = batch;
//		this.roomData = new ArrayList<>();
//		this.unallocated_strength = this.no_Of_Students;
//		this.invigilanceEnsured = false;
//
//	}

	public Course(String course_id, String course_name,String batch, int no_Of_Students) throws ClassNotFoundException, SQLException
																		
	{
		con=DBConnection.getInstance().getConnectionSchema("public");
		this.course_id = course_id;
		this.course_name = course_name;
		this.no_Of_Students = no_Of_Students;
		this.batch=batch;
		this.roomData = new ArrayList<>();
		this.unallocated_strength = this.no_Of_Students;
		this.invigilanceEnsured = false;

	}

	public Course(Course other) throws ClassNotFoundException, SQLException {

		con=DBConnection.getInstance().getConnectionSchema("public");
		this.course_id = other.course_id;
		this.course_name = other.course_name;
		this.no_Of_Students = other.no_Of_Students;

		this.roomData = other.roomData;
		this.unallocated_strength = other.unallocated_strength;
		this.invigilanceEnsured = other.invigilanceEnsured;
		this.processed = other.processed;
	}
                                                                       
	public void addCourse(String course_id,String course_name,String batch,int no_of_students) throws DAOException
	{
		 try
		 {	 
			 String sql="Insert into Course VALUES("+course_id+","+course_name+","+batch+","+no_of_students+")";
			 Statement stmt=con.createStatement();
			 stmt.executeUpdate(sql);
		 }
		 catch(SQLException e)
		 {
			 throw new DAOException(e.getMessage());
		 }
		
	}
	
	public void deleteCourse(String course_id) throws DAOException
	{
		 try
		 {	 
			 String sql="Delete from Course where course_id="+course_id;
			 Statement stmt=con.createStatement();
			 ResultSet rs=stmt.executeQuery(sql);
			
			 
		 }
		 catch(SQLException e)
		 {
			 throw new DAOException(e.getMessage());
		 }
		
	}
	
	public void updateCourse(String course_id,String course_name,String batch,int no_of_students) throws DAOException
	{
		 try
		 {	 
			 String sql="UPDATE course SET course_name="+course_name+",batch="+batch+",no_of_students="+no_of_students+" WHERE course_id="+course_id;
			 Statement stmt=con.createStatement();
			 ResultSet rs=stmt.executeQuery(sql);
			
			 
		 }
		 catch(SQLException e)
		 {
			 throw new DAOException(e.getMessage());
		 }
		
	}
	
	public int getUnallocatedStrength() {
		return unallocated_strength;
	}

	public void setUnallocatedStrength(int num) {
		unallocated_strength = unallocated_strength - num;
	}

	public int getTotalStudents() {
		return no_Of_Students;
	}

	public String toString() {
		return course_id + "  " + course_name+" ["+allocatedStudents+"]";
	}
}
