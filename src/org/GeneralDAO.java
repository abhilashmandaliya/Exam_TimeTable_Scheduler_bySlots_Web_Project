package org;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

// This is a general class to deal with database where objects are not required to manipulate database.
public class GeneralDAO {

	// fetch rooms from database
	 public static ArrayList<Room> getRooms() throws DAOException, ClassNotFoundException, SQLException 
	 {
		 Connection con=DBConnection.getInstance().getConnectionSchema("public");
		 ArrayList<Room> rooms=new ArrayList<>();
			 try
			 {	 		 
				 Statement stmt=con.createStatement();
				 ResultSet rs=stmt.executeQuery("Select * from Room");
				 		
				 while(rs.next())
				 {				 
					 int room_no=rs.getInt("room_no");
					 int room_capacity=rs.getInt("room_capacity");
					 rooms.add(new Room(room_no,room_capacity));
				 }
				 
			 }
			 catch(SQLException e)
			 {
				 throw new DAOException(e.getMessage());
			 }
			 return rooms;
	}
	//add new room to database
	public static void addRoom(int room_no,int room_capacity) throws DAOException, ClassNotFoundException
	{
		 try
		 {	 
			 Connection con=DBConnection.getInstance().getConnectionSchema("public");
			 String sql="Insert into Room VALUES("+room_no+","+room_capacity+")";
			 Statement stmt=con.createStatement();
			 ResultSet rs=stmt.executeQuery(sql);
			
			 
		 }
		 catch(SQLException e)
		 {
			 throw new DAOException(e.getMessage());
		 }
		
	}
	
	//delete a room from database
	public static void deleteRoom(int room_no) throws DAOException, ClassNotFoundException
	{
		 try
		 {	 
			 Connection con=DBConnection.getInstance().getConnectionSchema("public");
			 String sql="Delete from Room where room_no="+room_no;
			 Statement stmt=con.createStatement();
			 ResultSet rs=stmt.executeQuery(sql);
			
			 
		 }
		 catch(SQLException e)
		 {
			 throw new DAOException(e.getMessage());
		 }
		
	}
	
	//update capacity of the room
	public static void updateRoom(int room_no,int capacity) throws DAOException, ClassNotFoundException
	{
		 try
		 {	 
			 Connection con=DBConnection.getInstance().getConnectionSchema("public");
			 String sql="UPDATE Room SET room_capacity="+capacity+" WHERE room_no="+room_no;
			 Statement stmt=con.createStatement();
			 ResultSet rs=stmt.executeQuery(sql);
			
			 
		 }
		 catch(SQLException e)
		 {
			 throw new DAOException(e.getMessage());
		 }
		
	}
	//fetch courses from database
	 public static ArrayList<Course> getCourses() throws DAOException, ClassNotFoundException, SQLException 
	 {
		 Connection con=DBConnection.getInstance().getConnectionSchema("public");
		 ArrayList<Course> courses=new ArrayList<>();
			 try
			 {	 		 
				 Statement stmt=con.createStatement();
				 ResultSet rs=stmt.executeQuery("Select * from Course");
				 		
				 while(rs.next())
				 {				 
					 courses.add(new Course(rs.getString(1),rs.getString(2),rs.getString(3),rs.getInt(4)));
				 }
				 
			 }
			 catch(SQLException e)
			 {
				 throw new DAOException(e.getMessage());
			 }
			 return courses;
	}
	 
	//add a new course to database
	public static void addCourse(String course_id,String course_name,String batch,int no_of_students) throws DAOException, ClassNotFoundException
	{
		 try
		 {	 
			 Connection con=DBConnection.getInstance().getConnectionSchema("public");
			 String sql="Insert into Course VALUES("+course_id+","+course_name+","+batch+","+no_of_students+")";
			 Statement stmt=con.createStatement();
			 stmt.executeUpdate(sql);
		 }
		 catch(SQLException e)
		 {
			 throw new DAOException(e.getMessage());
		 }
		
	}
	
	//delete a course from database
	public static void deleteCourse(String course_id) throws DAOException, ClassNotFoundException
	{
		 try
		 {	 
			 Connection con=DBConnection.getInstance().getConnectionSchema("public");
			 String sql="Delete from Course where course_id="+course_id;
			 Statement stmt=con.createStatement();
			 ResultSet rs=stmt.executeQuery(sql);
			
			 
		 }
		 catch(SQLException e)
		 {
			 throw new DAOException(e.getMessage());
		 }
		
	}
	
	//modify a course
	public static void updateCourse(String course_id,String course_name,String batch,int no_of_students) throws DAOException, ClassNotFoundException
	{
		 try
		 {	 
			 Connection con=DBConnection.getInstance().getConnectionSchema("public");
			 String sql="UPDATE course SET course_name="+course_name+",batch="+batch+",no_of_students="+no_of_students+" WHERE course_id="+course_id;
			 Statement stmt=con.createStatement();
			 ResultSet rs=stmt.executeQuery(sql);
			
			 
		 }
		 catch(SQLException e)
		 {
			 throw new DAOException(e.getMessage());
		 }
		
	}
	
}
