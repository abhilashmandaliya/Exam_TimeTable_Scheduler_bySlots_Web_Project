package org;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.ResultSet;

public class TimeTableEndSem {

	private final Connection con;
	private Slot slot[];// stores all the slots
	private Map<Integer, StoreTT> store;// storing timetable (Slot
										// No,TimeIntervals-t1,t2)
	private ArrayList<Course> failed;

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

	public TimeTableEndSem() throws ClassNotFoundException, SQLException, DAOException {
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
		slot.updateProcessCount();// increase process count as a course has been
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
				if (od.getCourse().getCourse_id() == check_course.getCourse_id()) {
					// yes room is found and has our required course.
					String side = od.getSide();
					room_list.put(room_no, side);
				}
			}
		}
		return room_list;
	}

	public Map<Integer, Integer> calculateRoomCapacityOfSide(Map<Integer, String> map, TimeInterval ti, String side) {
		Map<Integer, Integer> room_capacity = new HashMap<>();
		for (Integer room_no : map.keySet())// check capacity of room_no
		{
			int sum = 0;
			ArrayList<OccupationData> od_al = ti.getMap().get(room_no);// accessed
																		// all
																		// the
																		// allocations
																		// from
																		// room

			for (OccupationData od : od_al) {
				if (od.getSide().equals(side)) {
					sum = sum + od.getAllocatedStudents();
				}
			}
			room_capacity.put(room_no, sum);
		}
		return room_capacity;

	}

	public Map<Integer, Integer> total_room_capacity() throws ClassNotFoundException, DAOException, SQLException {
		Map<Integer, Integer> total_capacity = new HashMap<>();
		ArrayList<Room> rooms = GeneralDAO.getRooms();
		for (Room room : rooms) {
			total_capacity.put(room.getRoom_no(), room.getCapacity());
		}
		return total_capacity;
	}

	// Room to which shifting needs to be done
	public int toRoom(Map<Integer, Integer> room_capacity, Course course)
			throws ClassNotFoundException, DAOException, SQLException {
		Map<Integer, Integer> total_capacity = total_room_capacity();
		for (Integer room_no : room_capacity.keySet()) {
			if (course.getUnallocated_strength() + room_capacity.get(room_no) <= total_capacity.get(room_no)) {
				return room_no;
			}
		}
		return 0;
	}

	// Room from which shifting needs to be done
	// Further, accomodate present course to this room after freeing up some
	// space.
	public int from_room(Map<Integer, String> room_list, TimeInterval ti, Course check_course, int toRoom) {
		room_list.remove(toRoom);
		for (int room_no : room_list.keySet()) {
			for (OccupationData od : ti.getMap().get(room_no)) {
				if (od.getCourse().getCourse_id() == check_course.getCourse_id()
						&& od.getRoom().getInvigilanceRequired() == true)
					return room_no;
			}
		}
		return 0;
	}

	public Room shiftFromTo(int fromRoom, int toRoom, TimeInterval ti, Course check_course, int shift) {
		Room from_room_return = null;
		// deducting from FromRoom
		for (OccupationData od : ti.getMap().get(fromRoom)) {
			if (od.getCourse().getCourse_id() == check_course.getCourse_id()) {
				from_room_return = od.getRoom();
				od.setAllocatedStudents(od.getAllocatedStudents() - shift);
				break;
			}
		}

		// adding to toRoom
		for (OccupationData od : ti.getMap().get(toRoom)) {
			if (od.getCourse().getCourse_id() == check_course.getCourse_id()) {
				od.setAllocatedStudents(od.getAllocatedStudents() + shift);
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
				for (String side1 : room_list.values())
					side = side1;
				Map<Integer, Integer> room_capacity = calculateRoomCapacityOfSide(room_list, ti, side);
				int toRoom = toRoom(room_capacity, course);
				if (toRoom == 0) {
					continue;// this course is useless,proceed to check other
								// course.
				}
				int fromRoom = from_room(room_list, ti, check_course, toRoom);
				// Now,toRoom is not 0,so we have to check further for from_room
				if (fromRoom == 0) {
					continue;
				}
				int shift = course.getUnallocated_strength();
				Room from_room = shiftFromTo(fromRoom, toRoom, ti, check_course, shift);
				assignOnLeftRight(course, from_room, shift, slot, ti, side);
				flag_successful = 1;
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
		int k = 0;
		if (course.getFlag_clash() == 1)
			k = 1;
		for (; k <= 1; k++) {
			flag = 0;
			if (custom_flag == 1 && k == 1 && slot.getSlot_no() != 1)// K==0
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
			{
				if (tryToAdjust(save_ti, course, slot) == 1)
					return 1;
				flag = 1;// course has been processed. jump to next course and
							// dont run below cases.
				// System.out.println("Room No: "+save_room.getRoom_no()+" is
				// "+save_room.getInvigilanceRequired());
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
				if (proposedRoom.checkInvigilanceRequired(course.getFlag_clash(), slot, array[k])) // refer
																									// Room
																									// class
																									// for
																									// this
																									// function
				{
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
				if (custom_flag == 0)// it will be 0 if CASE2 is not yet
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
						}
					}
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
			return flag;
		}
		return flag;// both case failed ,returning 0;
	}

	// this function checks if a course has too many students and in this case
	// time interval should be changed because
	// I dont want big courses to alternate to allow smaller courses to fit in
	// to ensure invigilation
	public static boolean ifCourseIsBig(Course course, TimeInterval ti) {
		if (course.getNo_Of_Students() > (0.80) * ti.totalCapacityOfRooms())
			return true;
		else
			return false;
	}

}
