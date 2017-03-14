package org;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//This time interval class just signifies time intervals of 2 hours for insem and 3 hours for endsem. A slot will be
// provided with 2 time intervals before/after lunch for insem.

public class TimeInterval {

	private int time_interval;
	private Map<Integer,ArrayList<OccupationData>> map;//this map maps Room number to an 
	//object(Course Name+ No_of_students of this course in this room )
	private	ArrayList<Room> rooms;
			
	public TimeInterval(int time_interval) throws ClassNotFoundException, DAOException, SQLException
	{
		this.time_interval=time_interval;
		this.map=new HashMap<>();
		this.rooms=new ArrayList<>();
		associateRooms();
	}
	
	//copy constructor
	public TimeInterval(TimeInterval other) throws ClassNotFoundException, SQLException
	{
		this.map=new HashMap<>();
		this.rooms=new ArrayList<>();
		this.time_interval=other.time_interval;
		
		//deep cloning rooms
		for(int i=0;i<other.getRooms().size();i++)
		{
			//called copy constructor of Room
			Room tempRoom=new Room(other.getRooms().get(i));
			this.rooms.add(tempRoom);
		}
		
		//deep cloning map
		for(Integer key:other.map.keySet())
		{			
			ArrayList<OccupationData> temp=other.getMap().get(key);// I have to make copy of temp
			ArrayList<OccupationData> newClone=new ArrayList<>();
			for(OccupationData od:temp)
			{
				newClone.add(new OccupationData(od));//calling copy constructor of OccupationData
			}
						
			this.map.put(key, newClone);
		}
	}
	
	public ArrayList<Room> getRooms() {
		return rooms;
	}
	
	public Map<Integer, ArrayList<OccupationData>> getMap() {
		return map;
	}
	public int getTime_interval() {
		return time_interval;
	}
	// fetch rooms from database and associate rooms to this particular time interval
	 public void associateRooms() throws DAOException, ClassNotFoundException, SQLException 
	 {
		this.rooms=GeneralDAO.getRooms();
	 }
	 
	 //adding capacities of each room. It will help to figure out courses requiring a lot of rooms.
	 //I will compare a percentage of this capacity,say if 70% is enough to accomodate in a particular timeInterval
	 //If not,switch the interval. It helps to alternate courses requiring a lot of rooms and in better invigilation
	 //as big courses will be in alternate batches and small courses will get space with big courses and they will
	 //ensure invigilation.
	 public int totalCapacityOfRooms()
	 {
		 int capacity=0;
		 for(Room room:rooms)
		 {
			 capacity+=room.getCapacity();
		 }
		 return capacity;
	 }
	 //this function assigns a particular course+allocated_strength(no of students from this course to be seated in 
	 //this room) to a particular room in this time interval. 
	 //If this room already contains course details,then this just appends a new course+allocated_strength
	 public void assignCourse(int room_no,Course course,int allocated)
	 {			//System.out.println("Really assigning"+course);
			if(map.containsKey(room_no))
			{ 
			ArrayList<OccupationData> temp=map.get(room_no);
			temp.add(new OccupationData(course,allocated));
			map.put(room_no, temp);
			return;
			}
			ArrayList<OccupationData> newList=new ArrayList<>();
			newList.add(new OccupationData(course,allocated));
			map.put(room_no, newList);
	}
	 
	 //this function makes a pattern where it sets some intial rooms as 'big' and later rooms as 'small'.
	 //this is done to optimize allocation as allocation will be held for small side in the room as much as possible
	 //if a room number 101 is marked as small,then left/right side is searched whose capacity is small(more students)
	 //have been allocated at that side.
	 public void smallBigPattern(int j)
	 {
		 System.out.println();;
		 for (int i = 0; i < getRooms().size(); i++) // all the default sides to look for in class is big. Setting to small for some.
			{
			// System.out.println("Pattern code running for:"+(i+1));
				if(i<j)
				{	getRooms().get(i).setCheckBigCapacity(true);
				//System.out.print("B");
				}
				else
				{
				getRooms().get(i).setCheckBigCapacity(false);
				//System.out.print("S");
				
				}
			}
	 }
	//printing timetable of this timeinterval
	public void print()
	{
		//System.out.println("Printing, time interval "+this.time_interval+"Map size: "+map.size());
		
		for(Integer key:map.keySet())
		{
			System.out.println(key+" "+map.get(key));
		}
	}	
	public String toString()
	{
		return time_interval+"";
	}
	 public void setRooms(ArrayList<Room> rooms) {
		this.rooms = rooms;
	}
	 
	 public void printRooms()
	 {
		 for(Room room:this.rooms)
		 {
			 System.out.println(room.getRoom_no()+"leftStrength: "+room.getLeftStrength()+"rightStrength: "+room.getRightStrength());
		 }
	 }
}
