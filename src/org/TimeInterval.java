package org;



import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TimeInterval {

	int day_no;
	 int time_interval;
	 ArrayList<Course> course_to_room;
		Map<Integer,ArrayList<OccupationData>> map;
		
		ArrayList<Room> rooms;
		
	private final Connection con;
		
	public TimeInterval(int day_no,int time_interval) throws ClassNotFoundException, SQLException, DAOException
	{
		con=DBConnection.getInstance().getConnectionSchema("public"); 
		System.out.println("Inside default constructor of TimeInterval");
		this.day_no=day_no;
		this.time_interval=time_interval;
		course_to_room=new ArrayList<>();
		map=new HashMap<>();
		rooms=new ArrayList<>();
		getRooms();
//		rooms.add(new Room(110,98));
//		rooms.add(new Room(108,60));
//		rooms.add(new Room(106,65));
//		rooms.add(new Room(103,60));
//		rooms.add(new Room(104,28));
		//addRooms();
	}
	
	public TimeInterval(TimeInterval other) throws ClassNotFoundException, SQLException
	{
		con=DBConnection.getInstance().getConnectionSchema("public"); 
		System.out.println("Inside copy constructor of TimeInterval");
		course_to_room=new ArrayList<>();
		map=new HashMap<>();
		rooms=new ArrayList<>();
		this.day_no=other.day_no;
		this.time_interval=other.time_interval;
		/*for(Course course:other.course_to_room)
		{
			this.course_to_room.add((Course)course.clone());
		}
		*/
		for(int i=0;i<other.rooms.size();i++)
		{
			System.out.println("Inside troublesome forloop");
			Room tempRoom=new Room(other.rooms.get(i));
			this.rooms.add(tempRoom);
		}
		
		this.map=new HashMap<>();
		for(Integer key:other.map.keySet())
		{
			
			ArrayList<Course> temp=other.map.get(key);
			ArrayList<Course> newClone=new ArrayList<>();
			for(Course course:temp)
			{
				newClone.add(new Course(course));
			}
			
			
			this.map.put(key, newClone);
		}
	}
	
	
	 public ArrayList<Room> getRooms() throws DAOException {
		 
		
		 Room room=null;
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
	 
	/*
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		TimeInterval cloned=(TimeInterval)super.clone();
		
		return cloned;
	}*/
	
	
	
	public void addRoom(int room_no,int room_capacity) throws DAOException
	{
		 try
		 {	 
			 String sql="Insert into Room VALUES("+room_no+","+room_capacity+")";
			 Statement stmt=con.createStatement();
			 ResultSet rs=stmt.executeQuery(sql);
			
			 
		 }
		 catch(SQLException e)
		 {
			 throw new DAOException(e.getMessage());
		 }
		
	}
	
	public void deleteRoom(int room_no) throws DAOException
	{
		 try
		 {	 
			 String sql="Delete from Room where room_no="+room_no;
			 Statement stmt=con.createStatement();
			 ResultSet rs=stmt.executeQuery(sql);
			
			 
		 }
		 catch(SQLException e)
		 {
			 throw new DAOException(e.getMessage());
		 }
		
	}
	
	public void updateRoom(int room_no,int capacity) throws DAOException
	{
		 try
		 {	 
			 String sql="UPDATE Room SET room_capacity="+capacity+" WHERE room_no="+room_no;
			 Statement stmt=con.createStatement();
			 ResultSet rs=stmt.executeQuery(sql);
			
			 
		 }
		 catch(SQLException e)
		 {
			 throw new DAOException(e.getMessage());
		 }
		
	}
	
	
	public void addCourse(int room_no,Course course,int allocated)
	{
		System.out.println("Adding course:"+course+"room_no "+room_no+"in time interval "+this.time_interval);
		if(map.containsKey(room_no))
		{ 
		ArrayList<OccupationData> temp=map.get(room_no);
		temp.add(new OccupationData(course,allocated));
		map.put(room_no, temp);
		return;
		}
		ArrayList<Course> newList=new ArrayList<>();
		newList.add(course);
		map.put(room_no, newList);
	}
	public void print()
	{
		System.out.println("Printing, time interval "+this.time_interval+"Map size: "+map.size());
		
		for(Integer key:map.keySet())
		{
			System.out.println("Room No: "+key+" "+map.get(key));
		}
	}
	
	
	
}
