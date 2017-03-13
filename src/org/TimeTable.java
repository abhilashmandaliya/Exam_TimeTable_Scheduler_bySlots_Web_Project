package org;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;

public class TimeTable {
	
	private final Connection con;
	private Slot slot[];
	
	public Slot[] getSlot() {
		return slot;
	}
	public TimeTable() throws ClassNotFoundException, SQLException, DAOException
	{
		con=DBConnection.getInstance().getConnectionSchema("public");
		getSlotDetails();
	}
	
	//extracting slots with all the courses from database
	public void getSlotDetails() throws SQLException, ClassNotFoundException, DAOException
	{
		Statement stmt=con.createStatement();
		//counting total number of slots.
		String sql="Select COUNT( DISTINCT slot_no) from Slot";
		ResultSet rs=stmt.executeQuery(sql);
		int count=0;
		while(rs.next())
		{
			count=rs.getInt(1);
		}
		slot=new Slot[count];
		//extracting all the courses slot wise
		for(int i=0;i<slot.length;i++)
		{
			//constructing a Slot object in each iteration
			slot[i]=new Slot(i+1);
			//storing courses to slot object
			slot[i].refreshCourses();
		}
	}	
	
	//just for CASE 1 and CASE 2 
	public void assignOnLeftRight(Course course,Room room,int num_of_students,Slot slot,TimeInterval ti,String side)
	{
		course.setProcessed(true); // course is successfully processed.
		room.setInvigilanceRequired(false);// no, now invigilance is not required for this "room".
		course.setUnallocatedStrength(num_of_students);//set to 0 in CASE 1 and CASE 2//Reduce unallocated strength
		//as some(all in CASE1 and CASE2) students have already been allocated in a room.
		slot.updateProcessCount();// increase process count as a course has been processed successfully.
		ti.assignCourse(room.getRoom_no(), course,num_of_students);//refer TimeInterval class. Storing number 
		//of students of a course in a room
		//System.out.println("Assigning"+course+" in "+room+" for "+num_of_students+"in slot"+slot+" in ti"+ti+"on side: "+side);
		if(side.equals("right"))//allocate on right side
		{room.setRightStrength(num_of_students);}
			
		else if(side.equals("left"))//allocate on left side
			{room.setLeftStrength(num_of_students);}
	}
	
	public int rightGo(Room room,Course course,TimeInterval ti,Slot slot) // more students on right side,so fewer right seats left
	{
		
		if (room.getRightCapacity() < course.getUnallocated_strength()) 
		{
			int deduct=room.getRightCapacity();
			room.setRightStrength(deduct);
			course.setUnallocatedStrength(deduct);
			ti.assignCourse(room.getRoom_no(), course, deduct);
		//	System.out.println("Splitted: Assigning"+course+" in "+room+" for "+deduct+"in slot"+slot+" in ti"+ti+"on side: right");
			return 0;
		} else {
			// finish course.
			
			assignOnLeftRight(course,room,course.getUnallocated_strength(),slot,ti,"right");	
			return 1;			
		}
	} 
	
	public int leftGo(Room room,Course course,TimeInterval ti,Slot slot) // more students on right side,so fewer right seats left
	{
		
		if (room.getLeftCapacity() < course.getUnallocated_strength()) 
		{
			int deduct=room.getLeftCapacity();
			room.setLeftStrength(deduct);
			course.setUnallocatedStrength(deduct);
			ti.assignCourse(room.getRoom_no(), course, deduct);
			//System.out.println(" Splitted Assigning"+course+" in "+room+" for "+deduct+"in slot"+slot+" in ti"+ti+"on side: left");
			return 0;
		} else {
			// finish course.
			
			assignOnLeftRight(course,room,course.getUnallocated_strength(),slot,ti,"left");	
			return 1;			
		}
	} 
	
	//Algortihm cases:
	//CASE 1: Traverse through all the rooms. In a room, if there is at least 1 student and room still requires 
	//		  invigilation,then go inside the room.(It may happen that it's possible to allocate all the students
	//		  in this room but we will check for other rooms if they require invigilation. Anyway, this room is 
	//        saved for CASE 2 allotment.)
    //		  Above scenario may be possible in the case if splitting of students happened because of big batch size.
	//CASE 2: Allocate all the students in one room. invigilation doesn't matter. 	      
	
	//this function consists CASE 1 and CASE 2
	// It will traverse through all the rooms to check for invigilation. But side by side,it also checks for CASE 2 
	//condition. in case, CASE 2 condition is true, it saves those values to be used later. If CASE 1 is successful,
	//then it returns the function,but in case if CASE 1 fails, and custom_flag==1(means CASE 2 is successful),then
	// it assigns according to case2. At last,if both case fails, it returns 0.
	public int allocateFullChunk(TimeInterval array[],Course course,Slot slot)
	{
		
		int flag=0;	
		int custom_flag=0;
		Room save_room=null;
		TimeInterval save_ti=null;
		String save_side=null;
		int finalChunkStudents = course.getUnallocated_strength();//It will have full strength for CASE1 and CASE2
		//CASE 1:
		for (int k = 0; k < array.length; k++) 
		{
			flag=0;
			for (int i = 0; i < array[k].getRooms().size(); i++) //entering a particular room to check invigilation
			{
				Room proposedRoom = array[k].getRooms().get(i);//storing concerned room for handy computation
				int left = proposedRoom.getLeftStrength();
				int right = proposedRoom.getRightStrength();
				//a new course will visit this function.So, this is certain that below will have
				//all the students from that course.
				
				if (proposedRoom.checkInvigilanceRequired()) //refer Room class for this function
				{	if (left > right) //left side has more strength
					{	
						if(finalChunkStudents <= proposedRoom.getLeftCapacity())//left has more strength,so try to
							//allocate on left side first as right side can be saved for future use if big course comes.
						{
							
							flag=1;//course has been processed. jump to next course and dont run below cases.
							assignOnLeftRight(course, proposedRoom,finalChunkStudents, slot, array[k], "left");
							return flag;	
						}
						else if (finalChunkStudents <= proposedRoom.getRightCapacity()) //left couldn't allocate.
							//Maybe it was too small. Try doing on right side.
						{
						    flag=1;//course has been processed. jump to next course and dont run below cases.
							assignOnLeftRight(course, proposedRoom,finalChunkStudents, slot, array[k], "right");
							return flag;
						}
					} 
					else //handles "left==right" and left < right
					{	if(finalChunkStudents <= proposedRoom.getRightCapacity())
						{
							flag=1;//course has been processed. jump to next course and dont run below cases.
							assignOnLeftRight(course, proposedRoom,finalChunkStudents, slot, array[k], "right");
							return flag;	
						}
						else if(finalChunkStudents <= proposedRoom.getLeftCapacity()) 
						{
							flag=1;//course has been processed. jump to next course and dont run below cases.
							assignOnLeftRight(course, proposedRoom,finalChunkStudents, slot, array[k], "left");
							return flag;
						}
					}
				}
				if(custom_flag==0)// it will be 0 if CASE2 is not yet successful.
				{
					if (left > right) 
					{	
						if (finalChunkStudents <= proposedRoom.getLeftCapacity()) 
						{
						    save_room=proposedRoom;
						    save_side="left";
						    save_ti=array[k];
						    custom_flag=1;// woww, CASE2 gets satisfied. Save the variables in case CASE1 fails for next
						    //iterations. No more checking for this custom_flag==0 statement as we just require first best case
						}
						else if (finalChunkStudents <= proposedRoom.getRightCapacity()) 
						{
						    save_room=proposedRoom;
						    save_side="right";
						    save_ti=array[k];
						    custom_flag=1;
						}
					} 
					else
					{	if (finalChunkStudents <= proposedRoom.getRightCapacity()) 
						{
						    save_room=proposedRoom;
						    save_side="right";
						    save_ti=array[k];
						    custom_flag=1;
						}
						else if (finalChunkStudents <= proposedRoom.getLeftCapacity()) 
						{
							 save_room=proposedRoom;
							 save_side="left";
							 save_ti=array[k];
							 custom_flag=1;
						}
					}
				}
				
			}
		}
		if(custom_flag==1)// CASE 1 failed but CASE 2 was successful
		{
			flag=1;//course has been processed. jump to next course and dont run below cases.
			assignOnLeftRight(course, save_room,finalChunkStudents, slot, save_ti, save_side);
			return flag;	
		}
		return flag;//both case failed ,returning 0;
	}
	
	//this function checks if a course has too many students and in this case time interval should be changed because
	//I dont want big courses to alternate to allow smaller courses to fit in to ensure invigilation
	public boolean ifCourseIsBig(Course course,TimeInterval ti)
	{
			if (course.getNo_Of_Students() > (0.80) * ti.totalCapacityOfRooms())
			return true;
			else return false;
	}
	
	public static void main(String[] args) throws CloneNotSupportedException,SQLException,ClassNotFoundException,DAOException {

		//storing entire time table in 1 object of TimeTable class.
		TimeTable TT=new TimeTable();
		
		//Running main algorithm for all the slots.It creates timetable of all the slots separately.
		for(int h=0;h<TT.getSlot().length;h++)// 
		{ 
			TimeInterval time1 = new TimeInterval(1);
			TimeInterval time2 = new TimeInterval(2);
			int flag = 0;// a flag to use to forcibly exit loops or restarting loops.
			TimeInterval[] array = {time1,time2};
			int p = 0;
			Slot slot=TT.getSlot()[h];
			while (slot.slotProcessed())//if all the courses are processed, then loop breaks. 
			{
				int flagContinue = 0;
				int flagContinue2 = 0;
				Course tempCourse = slot.chosingCourse();//refer slot class for details
				//System.out.println(tempCourse);
				//MAIN ALGORITHM:
				// There are 3 cases. Each course visits all the 3 cases. If it gets allocated in CASE 1, it breaks
				//the while loop and gives chance to next course. If it doesn't get allocated in CASE 1,then it tries for
				//CASE2, If not in even CASE2, then CASE3 will finally allocate all the students.
				//(Exceptional case: it just stores number of unallocated students in unallocated_strength if all the cases fail)
				//and gives chance to next course.
				
				// CASE 1: checks for invigilation and CASE 2: tries to allocate all students in 1 class.
				if (TT.allocateFullChunk(array, tempCourse, slot) == 1)
				{
					
					continue;
				}
				
				// CASE 3: small and big cases
	
				int k;
				if (TT.ifCourseIsBig(tempCourse, time1)) //assuming that total capacity of rooms is same for time2
				{
					k = p % 2; // k=0,1
				} 
				else
				{
					k = 0;
				}
				
				for (; k < 2; k++) 
				{
					flag = 0;
	
					for (int j = 0; j < (array[k].getRooms().size()+1); j++)// checking for number of cases = no of rooms
					{
						
						//saving state of time1,time2 and slot. In case, entire course is not allocated in one time interval,then it
						//undo all the operations.
						TimeInterval save1 = new TimeInterval(time1);
						TimeInterval save2 = new TimeInterval(time2);
						Slot save3 = new Slot(slot);
//						ArrayList<Room> save5=new ArrayList<>();
//						for(Room room:array[k].getRooms())
//						{
//							save5.add(new Room(room));
//						}
						array[k].smallBigPattern(j);//Refer TimeInterval class
	
						int save4 = 0;
						for (int i = 0; i < array[k].getRooms().size(); i++) //entering room for time1/time2 
						{	save4 = i;
							Room proposedRoom = array[k].getRooms().get(i);
							int left = proposedRoom.getLeftStrength();
							int right = proposedRoom.getRightStrength();
							boolean leftGo = false;
							boolean rightGo = false;
							boolean tempCheckBigCapacity = array[k].getRooms().get(i).getCheckBigCapacity();// to determine what to check-small/big
							//for this room,if tempCheckBig is true, so it will check for that side which has bigger capacity
							if (tempCheckBigCapacity == true) {
								if (left >= right) {
									if (proposedRoom.getLeftCapacity() > 0) {
										leftGo = true;
									}
									rightGo = false;
								} else if (right > left) {
									leftGo = false;
									if (proposedRoom.getRightCapacity() > 0) {
										rightGo = true;
									}
								}
							} else if (tempCheckBigCapacity == false) {
								if (left >= right) {
									leftGo = false;
									if (proposedRoom.getRightCapacity() > 0) {
										rightGo = true;
									}
								} else if (right > left) {
									if (proposedRoom.getLeftCapacity() > 0) {
										leftGo = true;
									}
									rightGo = false;
								}
							}
	
							if (rightGo) // more students on right side,so fewer right seats left
							{
								
								if(TT.rightGo(proposedRoom, tempCourse, array[k], slot)==1)
								{
									flag=1;
									break;
								}								
							} 
							else if (leftGo) 
							{
								if(TT.leftGo(proposedRoom, tempCourse, array[k], slot)==1)
								{
									flag=1;
									break;
								}								
							}	
						}
	
						// till here course has been allocated in all the rooms in
						// time interval 1
						flagContinue = 0;
						flagContinue2 = 0;
						if (k == 0 && tempCourse.getUnallocated_strength() > 0) {//course still has some unallocated students in above
							//case, so try other case and undo above operation
						//	time1.printRooms();
							time1 = save1;
							flagContinue = 1;
							flagContinue2=1;
							time2 = save2;
							slot = save3;
							tempCourse = slot.chosingCourse();
							//time1.printRooms();
							//time1.setRooms(save5);
							
							// send to k1(check same pattern for next time interval)
							
						}
	
						
						//above pattern could not fit in both the time intervals,so change the pattern and undo codes
						if (k == 1 && (save4 == array[k].getRooms().size() - 1) && (tempCourse.getUnallocated_strength() > 0)) {
							// undo code;
							time1 = save1;
							time2 = save2;
							slot = save3;
							flagContinue2 = 1;
							tempCourse = slot.chosingCourse();
							// send to next pattern.
						}
						if (flagContinue2 == 1)
							continue;
						if (flag == 1 || flagContinue == 1)
							break;
					}
					if (flagContinue == 1)
						continue;
					if (flag == 1)
						break;
				}
	
				p++;
			}
			time1.print();
			time2.print();
		}

	}

}
