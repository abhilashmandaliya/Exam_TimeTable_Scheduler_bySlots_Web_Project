package org;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GenerateTT {
	
	//this method gets unallocated students after entire allocation
	public static Map<Course,Integer> printUnallocatedStudents(TimeTable TT)
	{
		Slot[]slot_data=TT.getSlot();
		
		Map<Course,Integer> unallocated_map=new HashMap<>();
		
		for(Slot s:slot_data)
		{
			ArrayList<Course> courses=s.getCourses();
			for(Course course:courses)
			{
				if(course.getUnallocated_strength()>0)
				{
					unallocated_map.put(course, course.getUnallocated_strength());
				}				
			}
		}
		return unallocated_map;
	}

	public static void main(String[] args) throws CloneNotSupportedException,SQLException,ClassNotFoundException,DAOException, IOException {
		GeneralDAO.deleteAllCourses();
		ReadFromExcel.read_excel();
		//storing entire time table in 1 object of TimeTable class.
		TimeTable TT=new TimeTable();
		Set<Integer> set=new HashSet<>();//stores batch numbers of timeinterval2 of odd  slots
		//Running main algorithm for all the slots.It creates timetable of all the slots separately.
		for(int h=0;h<TT.getSlot().length;h++)// 
		{ 
//			System.out.println("**************************SLOT NO: "+(h+1)+"****************************");
//			System.out.println("Room No     Course ID   Course Name");
			
			if((h+1)%2!=0)
				{
				set.removeAll(set);//clear set for reuse
				}
						
			TimeInterval time1 = new TimeInterval(1);
			TimeInterval time2 = new TimeInterval(2);
			int flag = 0;// a flag to use to forcibly exit loops or restarting loops.
			TimeInterval[] array = {time1,time2};
			int p = 0;
			Slot slot=TT.getSlot()[h];
			
			for(Course course:slot.getCourses())
			{
				if(set.contains(Integer.parseInt(course.getBatch())))
				{
					course.setFlag_clash(1);
				}
			}
			while (slot.slotProcessed())//if all the courses are processed, then loop breaks. 
			{
				
				int flagContinue = 0;
				int flagContinue2 = 0;
				Course tempCourse = slot.chosingCourse();//refer slot class for details
				//System.out.println("Course chosen: "+tempCourse);
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
				
				//if a course has come here,it means that it's broken in different chunks
				tempCourse.setBroken(true);
				int k;
				
				if (TimeTable.ifCourseIsBig(tempCourse, time1)) //assuming that total capacity of rooms is same for time2
				{
					k = p % 2; // k=0,1//just alternating,p->course sequences(0,1,2,3,..)
					
					//flip k
					if(k==0)
						k=1;
					else if(k==1)
						k=0;
				} 
				else
				{
					k = 1;
				}
				
				//always start from k=1 for flag_clash==1

				if(tempCourse.getFlag_clash()==1)
					k=1;
				for (; k >=0 ; k--) 
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
						if (k == 1 && tempCourse.getUnallocated_strength() > 0) {//course still has some unallocated students in above
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
							
							// send to k0(check same pattern for next time interval)
							
						}
	
						
						//above pattern could not fit in both the time intervals,so change the pattern and undo codes
						if (k == 0 && (save4 == array[k].getRooms().size() - 1) && (tempCourse.getUnallocated_strength() > 0)) {
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
//			time1.print();
//			time2.print();
			TT.getStore().put(h+1, new StoreTT(h+1,time1,time2));
			
			
				if(!time2.getMap().isEmpty())//if time2 has some courses allocated
				{					
		    		for(ArrayList<OccupationData> od:time2.getMap().values())//stores all the batches of time2 in set
		    		{
		    			for(int hh=0;hh<od.size();hh++)
		    			{
		    			set.add(Integer.parseInt(od.get(hh).getCourse().getBatch()));
		    			}
		    		}
				}
				
		}
		//It prints data in excel sheet and exports a .xlsx file
		PrintExcel excel=new PrintExcel();
		excel.createExcelSheet(TT);		
		Map<Course,Integer> map=GenerateTT.printUnallocatedStudents(TT);
	}
}
