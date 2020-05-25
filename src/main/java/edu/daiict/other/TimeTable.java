package edu.daiict.other;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class TimeTable {

	protected final Connection con;
	protected Slot slot[];// stores all the slots
	protected Map<Integer, StoreTT> store;// storing timetable (Slot
										// No,TimeIntervals-t1,t2)
	protected ArrayList<Course> failed;
	Map<Integer, Utility2> room_capacity;
	public ArrayList<Course> getFailed() {
		return failed;
	}

	public void setFailed(Course course) {
		failed.add(course);
	}

	public Slot[] getSlot() {
		return slot;
	}

	public Map<Integer, StoreTT> getStore() {
		return store;
	}

	public TimeTable() throws ClassNotFoundException, SQLException, DAOException {
		failed = new ArrayList<>();
		con = DBConnection.getInstance().getConnectionSchema("public");
		getSlotDetails();
		store = new HashMap<>();
	}

	// extracting slots with all the courses from database
	public void getSlotDetails() throws SQLException, ClassNotFoundException, DAOException {
		Statement stmt = con.createStatement();
		// counting total number of slots.
		String sql = "Select COUNT( DISTINCT slot_no) from Slot";
		ResultSet rs = stmt.executeQuery(sql);
		int count = 0;
		while (rs.next()) {
			count = rs.getInt(1);
		}
		slot = new Slot[count];

		// extracting all the courses slot wise
		for (int i = 0; i < slot.length; i++) {
			// constructing a Slot object in each iteration
			slot[i] = new Slot(i + 1);
			// storing courses to slot object
			slot[i].refreshCourses();

		}
	}

	// just for CASE 1 and CASE 2
	public void assignOnLeftRight(Course course, Room room, int num_of_students, Slot slot, TimeInterval ti,
			String side) {
		course.setProcessed(true); // course is successfully processed.

		course.setUnallocatedStrength(num_of_students);// set to 0 in CASE 1 and
														// CASE 2//Reduce
														// unallocated strength
		// as some(all in CASE1 and CASE2) students have already been allocated
		// in a room.
		//slot.updateProcessCount();// increase process count as a course has been
									// processed successfully.
		ti.assignCourse(room.getRoom_no(), course, num_of_students, side, room);// refer
																				// TimeInterval
																				// class.
																				// Storing
																				// number
		// of students of a course in a room
		// System.out.println("Assigning"+course+" in "+room+" for
		// "+num_of_students+"in slot"+slot+" in ti"+ti+"on side: "+side);
		if (side.equals("R"))// allocate on right side
		{
			room.setRightStrength(num_of_students);
		}

		else if (side.equals("L"))// allocate on left side
		{
			room.setLeftStrength(num_of_students);
		}
	}

	public int rightGo(Room room, Course course, TimeInterval ti, Slot slot) // more
																				// students
																				// on
																				// right
																				// side,so
																				// fewer
																				// right
																				// seats
																				// left
	{

		if (room.getRightCapacity() < course.getUnallocated_strength()) {
			int deduct = room.getRightCapacity();
			room.setRightStrength(deduct);
			course.setUnallocatedStrength(deduct);
			ti.assignCourse(room.getRoom_no(), course, deduct, "R", room);
			// System.out.println("Splitted: Assigning"+course+" in "+room+" for
			// "+deduct+"in slot"+slot+" in ti"+ti+"on side: right");
			return 0;
		} else {
			// finish course.

			assignOnLeftRight(course, room, course.getUnallocated_strength(), slot, ti, "R");
			return 1;
		}
	}

	public int leftGo(Room room, Course course, TimeInterval ti, Slot slot) // more
																			// students
																			// on
																			// right
																			// side,so
																			// fewer
																			// right
																			// seats
																			// left
	{

		if (room.getLeftCapacity() < course.getUnallocated_strength()) {
			int deduct = room.getLeftCapacity();
			room.setLeftStrength(deduct);
			course.setUnallocatedStrength(deduct);
			ti.assignCourse(room.getRoom_no(), course, deduct, "L", room);
			// System.out.println(" Splitted Assigning"+course+" in "+room+" for
			// "+deduct+"in slot"+slot+" in ti"+ti+"on side: left");
			return 0;
		} else {
			// finish course.

			assignOnLeftRight(course, room, course.getUnallocated_strength(), slot, ti, "L");
			return 1;
		}
	}

	// Algortihm cases:
	// CASE 1: Traverse through all the rooms. In a room, if there is at least 1
	// student and room still requires
	// invigilation,then go inside the room.(It may happen that it's possible to
	// allocate all the students
	// in this room but we will check for other rooms if they require
	// invigilation. Anyway, this room is
	// saved for CASE 2 allotment.)
	// Above scenario may be possible in the case if splitting of students
	// happened because of big batch size.
	// CASE 2: Allocate all the students in one room. invigilation doesn't
	// matter.

	// refer allocateFullChunk function for below description
	// this function consists CASE 1 and CASE 2
	// It will traverse through all the rooms to check for invigilation. But
	// side by side,it also checks for CASE 2
	// condition. in case, CASE 2 condition is true, it saves those values to be
	// used later. If CASE 1 is successful,
	// then it returns the function,but in case if CASE 1 fails, and
	// custom_flag==1(means CASE 2 is successful),then
	// it assigns according to case2. At last,if both case fails, it returns 0.

	public Map<Integer, String> findRoomsFromCourse(Course check_course, TimeInterval ti) {
		Map<Integer, String> room_list = new HashMap<>();
		for (Integer room_no : ti.getMap().keySet()) {
			// OcupationDatass for the room.Room has following occupations
			ArrayList<OccupationData> od_al = ti.getMap().get(room_no);
			// search all the courses in this room and figure out if it has
			// check_course
			for (OccupationData od : od_al) {
				if (od.getCourse().getCourse_id().equals(check_course.getCourse_id())) {
					// yes room is found and has our required course.
					String side = od.getSide();
					room_list.put(room_no, side);
				}
			}
		}
		return room_list;
	}

	public int calculateRoomCapacityOfSide(int room_no, TimeInterval ti, Course check_course) {
	//	Map<Integer, Integer> room_capacity = new HashMap<>();
		
			int sum = 0;
			ArrayList<OccupationData> od_al = ti.getMap().get(room_no);// accessed
																		// all
																		// the
																		// allocations
																		// from
																		// room

			for (OccupationData od : od_al) {
				if (od.getCourse().getCourse_id().equals(check_course.getCourse_id())) {
					sum = sum + od.getAllocatedStudents();
				}
			}
			
		
		return sum;

	}

	public Map<Integer, Integer> total_room_capacity() throws ClassNotFoundException, DAOException, SQLException {
		Map<Integer, Integer> total_capacity = new HashMap<>();
		ArrayList<Room> rooms = GeneralDAO.getRooms();
		for (Room room : rooms) {
			total_capacity.put(room.getRoom_no(), room.getCapacity());
		}
		return total_capacity;
	}

	public int totalExistingStrengthOfRoom(TimeInterval ti,Utility2 ob)
	{
		int sum=0;
		for(int room:ti.getMap().keySet())
		{
			int room_no=ob.room_no;
			String side=ob.side;
			if(room==room_no)
			{
				ArrayList<OccupationData> al=ti.getMap().get(room);
				for(OccupationData od:al) {
					if (side.equals(od.getSide())) {
						sum = sum + od.getAllocatedStudents();
					}
				}
			}
		}
		return sum;
	}
	// Room to which shifting needs to be done
	public int toRoom(Map<Integer, Utility2> room_capacity, Course course,TimeInterval ti)
			throws ClassNotFoundException, DAOException, SQLException {
		Map<Integer, Integer> total_capacity = total_room_capacity();
		for (Integer room_no : room_capacity.keySet()) {
			if (course.getUnallocated_strength() + totalExistingStrengthOfRoom(ti,room_capacity.get(room_no)) <= total_capacity.get(room_no)) {
				return room_no;
			}
		}
		return 0;
	}

	// Room from which shifting needs to be done
	// Further, accomodate present course to this room after freeing up some
	// space.
	public int from_room(Map<Integer, String> room_list, TimeInterval ti, Course check_course,
			int toRoom,Map<Integer, Utility2> room_capacity, Course course) {
		room_list.remove(toRoom);
		for (int room_no : room_list.keySet()) {
			for (OccupationData od : ti.getMap().get(room_no)) {
				//System.out.println(room_no+" "+room_capacity.get(room_no).capacity+" "+course.getUnallocated_strength());
				if (od.getCourse().getCourse_id().equals(check_course.getCourse_id())
						&& od.getRoom().getInvigilanceRequired() == true
						&& room_capacity.get(room_no).capacity >= course.getUnallocated_strength()) {
					return room_no;
				}
			}
		}
		return 0;
	}

	public Room shiftFromTo(int fromRoom, int toRoom, TimeInterval ti, Course check_course, int shift,String side_from,String side_to) {
		Room from_room_return = null;
		// deducting from FromRoom
		for (OccupationData od : ti.getMap().get(fromRoom)) {
			if (od.getCourse().getCourse_id().equals(check_course.getCourse_id())) {
				from_room_return = od.getRoom();
				od.setAllocatedStudents(od.getAllocatedStudents() - shift);
				//System.out.println(from_room_return.getLeftStrength()+""+shift);
				if (side_from.equals("L")) {
					from_room_return.setLeftStrength(-shift);
				} else if (side_from.equals("R")) {
					from_room_return.setRightStrength(-shift);
				}
				//System.out.println(from_room_return.getLeftStrength());
				break;
			}
		}

		// adding to toRoom
		for (OccupationData od : ti.getMap().get(toRoom)) {
			if (od.getCourse().getCourse_id().equals(check_course.getCourse_id())) {
				od.setAllocatedStudents(od.getAllocatedStudents() + shift);
				Room to_room_return = od.getRoom();
				if (side_to.equals("L")) {
					to_room_return.setLeftStrength(shift);
				} else if (side_to.equals("R")) {
					to_room_return.setRightStrength(shift);
				}
				//System.out.println(to_room_return.getLeftStrength());
			}
		}
		return from_room_return;
	}

	// this function shifts some portion of broken courses to other rooms and
	// make space to accomodate other course
	// to ensure invigilation
	public int tryToAdjust(TimeInterval ti, Course course, Slot slot)
			throws ClassNotFoundException, DAOException, SQLException {
		
		int flag_successful = 0;
		String side = null;// figure out the concerned side
		// iterate over courses
		for (Course check_course : slot.getCourses()) {
			if (check_course.getBroken() == true) {
				// search the occupation data and store all the rooms
				Map<Integer, String> room_list = findRoomsFromCourse(check_course, ti);
				System.out.println();
				room_capacity=new HashMap<>();
				for (Integer room_no : room_list.keySet())
				{
								
				 room_capacity.put(room_no, new Utility2(room_list.get(room_no),room_no,calculateRoomCapacityOfSide(room_no, ti, check_course)));
				 
				}
								
				int toRoom = toRoom(room_capacity, course,ti);
				if (toRoom == 0) {
					continue;// this course is useless,proceed to check other
								// course.
				}
				
				int fromRoom = from_room(room_list, ti, check_course, toRoom,room_capacity, course);
				// Now,toRoom is not 0,so we have to check further for from_room
				if (fromRoom == 0) {
					continue;
				}
				
				int shift = course.getUnallocated_strength();
				String side_from=room_capacity.get(fromRoom).side;
				String side_to=room_capacity.get(toRoom).side;
				Room from_room = shiftFromTo(fromRoom, toRoom, ti, check_course, shift,side_from,side_to);
				assignOnLeftRight(course, from_room, shift, slot, ti, side_from);
				from_room.setInvigilanceRequired(false);
				flag_successful=1;
				break;
			}
		}
		return flag_successful;
	}

	public int allocateFullChunk(TimeInterval array[], Course course, Slot slot)
			throws ClassNotFoundException, DAOException, SQLException {

		int flag = 0;
		int custom_flag = 0;
		Room save_room = null;
		TimeInterval save_ti = null;
		String save_side = null;
		int finalChunkStudents = course.getUnallocated_strength();// It will
																	// have full
																	// strength
																	// for CASE1
																	// and CASE2
		// CASE 1:
		//System.out.println("Course:"+course.getCourse_name()+" and strength:"+finalChunkStudents);
		for (int k = array.length - 1; k >= 0; k--) 
		{
			flag = 0;
			if (custom_flag == 1 && k == 0 && slot.getSlot_no() != 1)// K==0
																		// means
																		// it
																		// has
																		// already
																		// checked
																		// for
																		// k==1(TimeInterval2)
			// and it has just reached timeinterval1 first iteration. BUT
			// custom_flag==1 means it already has
			// a best case for entire allocation(although pushing this in
			// already invigilance secured room).
			// So,push it in that timeInterval2 even if invigilance is overdozed
			// and timeinterval1 still
			// requires invigilance as consecutive exams are
			// prohibited(priority).
			{    //System.out.println("Course:"+course.getCourse_name()+" Entered CASE 1");
				if (tryToAdjust(save_ti, course, slot) == 1) {
					return 1;
				}
				//	System.out.println("Course:"+course.getCourse_name()+" NOT RETURNED and ti:"+save_ti.getTime_interval());
				flag = 1;// course has been processed. jump to next course and
				// dont run below cases.
				// System.out.println("Room No: "+save_room.getRoom_no()+" is
				// "+save_room.getInvigilanceRequired());
				//System.out.println("Save room:"+save_room);
				assignOnLeftRight(course, save_room, finalChunkStudents, slot, save_ti, save_side);
				save_room.setInvigilanceRequired(false);// no, now invigilance
														// is not required for
														// this "room".
				return flag;
			}
			// custom_flag=0;
			for (int i = 0; i < array[k].getRooms().size(); i++) // entering a
																	// particular
																	// room to
																	// check
																	// invigilation
			{
				Room proposedRoom = array[k].getRooms().get(i);// storing
																// concerned
																// room for
																// handy
																// computation
				int left = proposedRoom.getLeftStrength();
				int right = proposedRoom.getRightStrength();
				// a new course will visit this function.So, this is certain
				// that below will have
				// all the students from that course.
				// System.out.println("k: "+k+"i: "+i+"
				// "+proposedRoom.getRoom_no()+"required:
				// "+proposedRoom.getInvigilanceRequired()+""+course);
				//System.out.println(proposedRoom.getRoom_no()+" "+proposedRoom.getInvigilanceRequired()+"Right: "+proposedRoom.getRightCapacity()+"Left: "+proposedRoom.getLeftCapacity());
				if (proposedRoom.checkInvigilanceRequired(course.getFlag_clash(), slot, array[k])) // refer
																									// Room
																									// class
																									// for
																									// this
																									// function
				{	//System.out.println("Entered room for invigilance:"+proposedRoom.getRoom_no());
					if (left > right) // left side has more strength
					{
						if (finalChunkStudents <= proposedRoom.getLeftCapacity())// left
																					// has
																					// more
																					// strength,so
																					// try
																					// to
						// allocate on left side first as right side can be
						// saved for future use if big course comes.
						{
							
							flag = 1;// course has been processed. jump to next
										// course and dont run below cases.
							assignOnLeftRight(course, proposedRoom, finalChunkStudents, slot, array[k], "L");
							proposedRoom.setInvigilanceRequired(false);// no,
																		// now
																		// invigilance
																		// is
																		// not
																		// required
																		// for
																		// this
																		// "room".
							return flag;
						} else if (finalChunkStudents <= proposedRoom.getRightCapacity()) // left
																							// couldn't
																							// allocate.
						// Maybe it was too small. Try doing on right side.
						{
							
							flag = 1;// course has been processed. jump to next
										// course and dont run below cases.
							assignOnLeftRight(course, proposedRoom, finalChunkStudents, slot, array[k], "R");
							proposedRoom.setInvigilanceRequired(false);// no,
																		// now
																		// invigilance
																		// is
																		// not
																		// required
																		// for
																		// this
																		// "room".
							return flag;
						}
					} else // handles "left==right" and left < right
					{
						if (finalChunkStudents <= proposedRoom.getRightCapacity()) {
							flag = 1;// course has been processed. jump to next
										// course and dont run below cases.
							
							assignOnLeftRight(course, proposedRoom, finalChunkStudents, slot, array[k], "R");
							proposedRoom.setInvigilanceRequired(false);// no,
																		// now
																		// invigilance
																		// is
																		// not
																		// required
																		// for
																		// this
																		// "room".
							return flag;
						} else if (finalChunkStudents <= proposedRoom.getLeftCapacity()) {
							flag = 1;// course has been processed. jump to next
										// course and dont run below cases.
							assignOnLeftRight(course, proposedRoom, finalChunkStudents, slot, array[k], "L");
							
							proposedRoom.setInvigilanceRequired(false);// no,
																		// now
																		// invigilance
																		// is
																		// not
																		// required
																		// for
																		// this
																		// "room".
							return flag;
						}
					}
				}
				if (custom_flag == 0 || proposedRoom.getInvigilanceRequired()==false)// it will be 0 if CASE2 is not yet
										// successful.
				{
					if (left > right) {
						
						if (finalChunkStudents <= proposedRoom.getLeftCapacity()) {
							save_room = proposedRoom;
							save_side = "L";
							save_ti = array[k];
							custom_flag = 1;// woww, CASE2 gets satisfied. Save
											// the variables in case CASE1 fails
											// for next
							// iterations. No more checking for this
							// custom_flag==0 statement as we just require first
							// best case
							
						} else if (finalChunkStudents <= proposedRoom.getRightCapacity()) {
							save_room = proposedRoom;
							save_side = "R";
							save_ti = array[k];
							custom_flag = 1;
							
						}
					} else {
						
						if (finalChunkStudents <= proposedRoom.getRightCapacity()) {
							save_room = proposedRoom;
							save_side = "R";
							save_ti = array[k];
							custom_flag = 1;
							
						} else if (finalChunkStudents <= proposedRoom.getLeftCapacity()) {
							save_room = proposedRoom;
							save_side = "L";
							save_ti = array[k];
							custom_flag = 1;
						//	System.out.println("Course:"+course.getCourse_name()+" entered good"+proposedRoom.getRoom_no()+"Left capacity:"+proposedRoom.getLeftCapacity());
						}
					}
					//System.out.println("custom flag:"+custom_flag+"for"+proposedRoom);
				}

			}
		}
		if (custom_flag == 1)// CASE 1 failed but CASE 2 was successful
		{
			flag = 1;// course has been processed. jump to next course and dont
						// run below cases.
			assignOnLeftRight(course, save_room, finalChunkStudents, slot, save_ti, save_side);
			save_room.setInvigilanceRequired(false);// no, now invigilance is
													// not required for this
													// "room".
		//	System.out.println("working custom flag");
			return flag;
		}
		return flag;// both case failed ,returning 0;
	}

	// this function checks if a course has too many students and in this case
	// time interval should be changed because
	// I dont want big courses to alternate to allow smaller courses to fit in
	// to ensure invigilation
	public static boolean ifCourseIsBig(Course course, TimeInterval ti) {
		if (course.getNo_Of_Students() > (1.65) * ti.totalCapacityOfRooms()) {
			return true;
		} else {
			return false;
		}
	}
	
	public Utility1 dynamicAllot(TimeInterval[] array,Course tempCourse_original,Slot slot,TimeTable TT,int k,int flag_failed,String type) throws ClassNotFoundException, DAOException, SQLException
	{
		if(array==null)
		{
			TimeInterval time1 = new TimeInterval(1);
			TimeInterval time2 = new TimeInterval(2);
			TimeInterval[] array1 = { time1, time2 };
			array=array1;
			
		}
		int flag_small=0;//for identifying small course;
		int flagContinue = 0;
		int flagContinue2 = 0;
		int flag=0;
		
		Course tempCourse=new Course(tempCourse_original);//making a new copy as the course given in function is coming from buffer
		//and I need to keep it fresh.
		
		//System.out.println("Course chosen: " + tempCourse + "processed is:" + tempCourse.getProcessed());
		// MAIN ALGORITHM:
		// There are 3 cases. Each course visits all the 3 cases. If it
		// gets allocated in CASE 1, it breaks
		// the while loop and gives chance to next course. If it doesn't
		// get allocated in CASE 1,then it tries for
		// CASE2, If not in even CASE2, then CASE3 will finally allocate
		// all the students.
		// (Exceptional case: it just stores number of unallocated
		// students in unallocated_strength if all the cases fail)
		// and gives chance to next course.

		// CASE 1: checks for invigilation and CASE 2: tries to allocate
		// all students in 1 class.
		if (TT.allocateFullChunk(array, tempCourse, slot) == 1) {
			//System.out.println("Allocated full chunk for" + tempCourse);
			
			return new Utility1(array,k,tempCourse,slot,0);
		}
		
		// CASE 3: small and big cases
		
		// if a course has come here,it means that it's broken in
		// different chunks
		tempCourse.setBroken(true);
		//course_backup.setBroken(true);
		//System.out.println(tempCourse+""+tempCourse.getBroken()+"Working");
	//	System.out.println("intitial k:"+k);
		if (TimeTable.ifCourseIsBig(tempCourse, array[0])) // assuming
															// that
															// total
															// capacity
															// of
															// rooms is
															// same
															// for time2
		{
			// k = p % 2; // k=0,1//just alternating,p->course
			// sequences(0,1,2,3,..)
			//System.out.println("Big COURSE:::"+tempCourse.getCourse_name());
			// flip k
			if (k == 0) {
				k = 1;
			} else if (k == 1) {
				k = 0;
			}
		} else {
			k = 1;// this is causing 4-5 times printing in excel if
			// given wrong value.
			flag_small = 1;
		}
		//	System.out.println("k = "+k+" for "+tempCourse.getCourse_name());
		// always start from k=1 for flag_clash==1

		if (tempCourse.getFlag_clash() == 1) {
			k = 1;
		}
		//	System.out.println(tempCourse+"final k:"+k);
		for (; k >= 0; k--) {
			flag = 0;

			for (int j = 0; j < (array[k].getRooms().size() + 1); j++)// checking
			// for
			// number
			// of
																		// cases
																		// =
																		// no
																		// of
																		// rooms
			{
				//tempCourse=new Course(tempCourse);
				//System.out.println("j:"+j+" -- "+tempCourse+"--Total:"+array[k].getRooms().size());
				//System.out.println("DANGER:"+tempCourse.getUnallocated_strength());
				// saving state of array[0],time2 and slot. In case,
				// entire
				// course is not allocated in one time interval,then it
				// undo all the operations.
				TimeInterval save1 = new TimeInterval(array[0]);
				TimeInterval save2 = new TimeInterval(array[1]);
				Slot save3 = new Slot(slot);
				// ArrayList<Room> save5=new ArrayList<>();
				// for(Room room:array[k].getRooms())
				// {
				// save5.add(new Room(room));
				// }
				array[k].smallBigPattern(j);// Refer TimeInterval class
				
				int save4 = 0;
				for (int i = 0; i < array[k].getRooms().size(); i++) // entering
																		// room
																		// for
																		// array[0]/time2
				{ // System.out.println("i:"+i+"for course"+tempCourse);
					
					save4 = i;
					Room proposedRoom = array[k].getRooms().get(i);
					//System.out.println("for j:"+j+"Room:"+proposedRoom);
					int left = proposedRoom.getLeftStrength();
					int right = proposedRoom.getRightStrength();
					boolean leftGo = false;
					boolean rightGo = false;
					boolean tempCheckBigCapacity = array[k].getRooms().get(i).getCheckBigCapacity();// to
																									// determine
																									// what
																									// to
																									// check-small/big
					// for this room,if tempCheckBig is true, so it will
					// check for that side which has bigger capacity
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

					if (rightGo) // more students on right side,so fewer
									// right seats left
					{
					//	System.out.println("Rightgo: j"+j);
						if (TT.rightGo(proposedRoom, tempCourse, array[k], slot) == 1) {
							flag = 1;
							break;
						}
					} else if (leftGo) {
						if (TT.leftGo(proposedRoom, tempCourse, array[k], slot) == 1) {
							flag = 1;
							break;
						}
					}
				}
				//System.out.println("j:"+j+"flag: "+flag);
				//System.out.println("Pattern check: ");
				//array[1].print();

				// till here course has been allocated in all the rooms
				// in
				// time interval 1
				flagContinue = 0;
				flagContinue2 = 0;
				//System.out.println("visiting hhhhhhhhhhh"+tempCourse.getUnallocated_strength());
				if (k == 1 && tempCourse.getUnallocated_strength() > 0) {// course
																			// still
																			// has
																			// some
																			// unallocated
																			// students
																			// in
																			// above
					// case, so try other case and undo above operation
					// array[0].printRooms();
					//System.out.println("before undo");
					//System.out.println(tempCourse.getUnallocated_strength());
					array[0] = save1;
					flagContinue = 1;
					flagContinue2 = 1;
					array[1] = save2;
					slot = save3;
//					System.out.println("After undo");
//					array[1].print();
					//see below comment
					if(flag_failed==1)
					{
						tempCourse=new Course(tempCourse_original);
					}
					else{
						
					
					for(int n=0;n<GenerateTT.buffer.size();n++)
					{
						Course course2=GenerateTT.buffer.get(n);
							if(course2.getCourse_id().equals(tempCourse.getCourse_id()))
							{
								tempCourse=new Course(course2);
								break;
							}
						
					}
					}
					//replacing this address in original buffer_copy also of GenerateTT
					//reflectChangesToBuffer(tempCourse,array);
					//System.out.println("flag1: " + tempCourse);
					// array[0].printRooms();
					// array[0].setRooms(save5);

					// send to k0(check same pattern for next time
					// interval)
					//System.out.println("after undo");
					//System.out.println(tempCourse.getUnallocated_strength());

				}

				//System.out.println(tempCourse+"k:"+k);
				// above pattern could not fit in both the time
				// intervals,so change the pattern and undo codes
				if (k == 0 && (save4 == array[k].getRooms().size() - 1)
						&& (tempCourse.getUnallocated_strength() > 0)) {
					// undo code;
					array[0] = save1;
					array[1] = save2;
					slot = save3;
					flagContinue2 = 1;
					if(flag_failed==1)
					{
						tempCourse=new Course(tempCourse_original);
					}
					else{
						
					
					//undo course by replacing it with fresh copy from buffer
					for(int n=0;n<GenerateTT.buffer.size();n++)
					{
						Course course2=GenerateTT.buffer.get(n);
							if(course2.getCourse_id().equals(tempCourse.getCourse_id()))
							{
								tempCourse=new Course(course2);
								break;
							}
						
					}
					}
					//replacing this address in original buffer_copy also of GenerateTT
					//reflectChangesToBuffer(tempCourse,array);
					//System.out.println("flag2: " + tempCourse);
					// send to next pattern.
				}
				
				// below is for normal stopping after all the (j,i)
				// combinations are finished. Now, it is still
				// unallocated, so this is failed
				// adding it to failed list.
				
				//flag_failed means it's coming from normal allotment option from GenrateTT and this normal option can
				//set a course to failed. As if buffer is allowed, it may set several courses as failed just in testing.
				//System.out.println(tempCourse+"flag_failed: "+flag_failed);
				System.out.println("flag failed:"+j);
				if (type.equals("normal")&& k == 0 && flag_failed==1 && (save4 == array[k].getRooms().size() - 1)
						&& (tempCourse.getUnallocated_strength() > 0) && (j == array[k].getRooms().size())) {
					// undo code;
					//System.out.println("breaking for" + tempCourse);
					System.out.println("Failinggggggg"+tempCourse.getCourse_name());
					TT.setFailed(tempCourse);
					
					tempCourse.setProcessed(true);
					//if this course is failed,it's removed from buffer also as it's no more required to check and it's
					//interfering in normal processing. Buffer always keeps initial states. So, this failing course might
					//be getting priority always.
					for(int n=0;n<GenerateTT.buffer.size();n++)
					{
						Course course2=GenerateTT.buffer.get(n);
							if(course2.getCourse_id().equals(tempCourse.getCourse_id()))
							{
								
								GenerateTT.buffer.remove(n);
								break;
							}
						
					}
					//slot.updateProcessCount();
				//	System.out.println(tempCourse.getProcessed());
					flag = 1;
					flagContinue2 = 0;
					flagContinue = 0;
					break;
					// send to next pattern.
				}

				if (k == 0 && flag_failed == 0 && (save4 == array[k].getRooms().size() - 1)
						&& (tempCourse.getUnallocated_strength() > 0) && (j == array[k].getRooms().size())) {
					flag_failed = 1;
					break;
					// send to next pattern.
				}
				// tempCourse = slot.chosingCourse();
				if (flagContinue2 == 1) {
					continue;
				}
				if (flag == 1 || flagContinue == 1) {
					break;
				}
			}
			if (flagContinue == 1) {
				continue;
			}
			if (flag == 1) {
				break;
			}
		}

		//p++;
		if (flag_small == 1) {
			k = 0;
		}

		return new Utility1(array, k, tempCourse, slot, flag_failed);
	}

	//this function compares 2 timeinterval array. It checks that all the allocated courses of old one in ti2 is in new 
	//one. If yes, then it means that all the existing courses are intact in new array and returns true.
	public boolean courseIntact(TimeInterval[] old_array,TimeInterval[] new_array)
	{
		HashSet<String> old_courses=new HashSet<>();
		HashSet<String> new_courses=new HashSet<>();
		//extracting old courses from TimeInterval2
		for(ArrayList<OccupationData> al:old_array[1].getMap().values())
		{ 
			for(OccupationData od:al)
			{
				old_courses.add(od.getCourse().getCourse_id());
			}
		}
		//extracting new courses from TimeInterval2
		for (ArrayList<OccupationData> al : new_array[1].getMap().values()) {
			for (OccupationData od : al) {
				new_courses.add(od.getCourse().getCourse_id());
			}
		}
		if (old_courses.isEmpty() && new_courses.isEmpty()) {
			return false;
		}
		for (String courseid : old_courses) {
			int flagg = 0;
			for (String new_course_id : new_courses) {
				if (courseid.equals(new_course_id)) {
					flagg = 1;
				}
			}
			if (flagg == 0) {
				return false;
			}
		}
		return true;
	}

}
