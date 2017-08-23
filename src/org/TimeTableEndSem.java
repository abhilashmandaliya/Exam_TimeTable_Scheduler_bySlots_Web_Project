package org;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.sql.Connection;
import java.sql.ResultSet;

public class TimeTableEndSem extends TimeTable{

	
	public TimeTableEndSem() throws ClassNotFoundException, SQLException, DAOException {
		super();
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
			k = 0;
		for (; k <= 1; k++) {
			flag = 0;
			if (custom_flag == 1 && k == 1 && slot.getSlot_no() != 1)// K==1
																		// means
																		// it
																		// has
																		// already
																		// checked
																		// for
																		// k==0(TimeInterval1)
			// and it has just reached timeinterval2 first iteration. BUT
			// custom_flag==1 means it already has
			// a best case for entire allocation(although pushing this in
			// already invigilance secured room).
			// So,push it in that timeInterval1 even if invigilance is overdozed
			// and timeinterval2 still
			// requires invigilance as consecutive exams are
			// prohibited(priority).
			{
			//	System.out.println(course+"Trying to adjust");
				
				if (tryToAdjust(save_ti, course, slot) == 1)
					{
					return 1;
					}
				flag = 1;// course has been processed. jump to next course and
							// dont run below cases.
				// System.out.println("Room No: "+save_room.getRoom_no()+" is
				// "+save_room.getInvigilanceRequired());
				assignOnLeftRight(course, save_room, finalChunkStudents, slot, save_ti, save_side);
				save_room.setInvigilanceRequired(false);// no, now invigilance
														// is not required for
														// this "room".
				//System.out.println(course+"could not adjust");
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
					System.out.println(course+" is trying to allocate in room "+proposedRoom);
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
					System.out.println(course+" is finally trying to allocate in room "+proposedRoom);
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
							System.out.println("Working");
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

		public Utility1 dynamicAllot(TimeInterval[] array,Course tempCourse,Slot slot,TimeTable TT,int k,int flag_failed) throws ClassNotFoundException, DAOException, SQLException
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
			
			tempCourse=new Course(tempCourse);//making a new copy as the course given in function is coming from buffer
			//and I need to keep it fresh.
			
			System.out.println("Course chosen: " + tempCourse + "processed is:" + tempCourse.getProcessed());
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
				System.out.println("Allocated full chunk for" + tempCourse);
				array[0].print();
				array[1].print();
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
				if (k == 0)
					k = 1;
				else if (k == 1)
					k = 0;
			} else {
				k = 0;// this is causing 4-5 times printing in excel if
						// given wrong value.
				flag_small=1;
			}
		//	System.out.println("k = "+k+" for "+tempCourse.getCourse_name());
			// always start from k=1 for flag_clash==1

			if (tempCourse.getFlag_clash() == 1)
				k = 0;
		//	System.out.println(tempCourse+"final k:"+k);
			for (; k <= 1; k++) {
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
					
					System.out.println(tempCourse+"visiting hhhhhhhhhhh"+tempCourse.getUnallocated_strength());
					array[0].print();
					array[1].print();
					if (k == 0 && tempCourse.getUnallocated_strength() > 0) {// course
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
//						System.out.println("After undo");
//						array[1].print();
						//see below comment
						for(int n=0;n<GenerateTTEndSem.buffer.size();n++)
						{
							Course course2=GenerateTTEndSem.buffer.get(n);
								if(course2.getCourse_id().equals(tempCourse.getCourse_id()))
								{
									tempCourse=new Course(course2);
									break;
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
					if (k == 1 && (save4 == array[k].getRooms().size() - 1)
							&& (tempCourse.getUnallocated_strength() > 0)) {
						// undo code;
						array[0] = save1;
						array[1] = save2;
						slot = save3;
						flagContinue2 = 1;
						//undo course by replacing it with fresh copy from buffer
						for(int n=0;n<GenerateTTEndSem.buffer.size();n++)
						{
							Course course2=GenerateTTEndSem.buffer.get(n);
								if(course2.getCourse_id().equals(tempCourse.getCourse_id()))
								{
									tempCourse=new Course(course2);
									break;
								}
							
						}
						//replacing this address in original buffer_copy also of GenerateTT
						//reflectChangesToBuffer(tempCourse,array);
						//System.out.println("flag2: " + tempCourse);
						// send to next pattern.
					}
					if (k == 1 && (save4 == array[k].getRooms().size() - 1)
							&& (tempCourse.getUnallocated_strength() > 0) && tempCourse.getFlag_clash() == 1) {
						array[0] = save1;
						array[1] = save2;
						slot = save3;
						//undo course by replacing it with fresh copy from buffer
						for(int n=0;n<GenerateTTEndSem.buffer.size();n++)
						{
							Course course2=GenerateTTEndSem.buffer.get(n);
								if(course2.getCourse_id().equals(tempCourse.getCourse_id()))
								{
									tempCourse=new Course(course2);
									break;
								}
							
						}
						k = 0;
						break;
					}
					// check above comment. This ensures that it finally
					// terminates after k=0;
					if (k == 0 && flag_failed==1 && (save4 == array[k].getRooms().size() - 1)
							&& (tempCourse.getUnallocated_strength() > 0) && tempCourse.getFlag_clash() == 1) {
						array[0] = save1;
						array[1] = save2;
						slot = save3;
						//undo course by replacing it with fresh copy from buffer
						for(int n=0;n<GenerateTTEndSem.buffer.size();n++)
						{
							Course course2=GenerateTTEndSem.buffer.get(n);
								if(course2.getCourse_id().equals(tempCourse.getCourse_id()))
								{
									tempCourse=new Course(course2);
									break;
								}
							
						}
						TT.setFailed(tempCourse);
						tempCourse.setProcessed(true);
						//slot.updateProcessCount();
						for(int n=0;n<GenerateTTEndSem.buffer.size();n++)
						{
							Course course2=GenerateTTEndSem.buffer.get(n);
								if(course2.getCourse_id().equals(tempCourse.getCourse_id()))
								{
									GenerateTTEndSem.buffer.remove(n);
									break;
								}
							
						}
						flag = 1;
						flagContinue2 = 0;
						flagContinue = 0;
						break;
					}
					// below is for normal stopping after all the (j,i)
					// combinations are finished. Now, it is still
					// unallocated, so this is failed
					// adding it to failed list.
					
					//flag_failed means it's coming from normal allotment option from GenrateTT and this normal option can
					//set a course to failed. As if buffer is allowed, it may set several courses as failed just in testing.
					//System.out.println(tempCourse+"flag_failed: "+flag_failed);
					if (k == 1 && flag_failed==1 && (save4 == array[k].getRooms().size() - 1)
							&& (tempCourse.getUnallocated_strength() > 0) && (j == array[k].getRooms().size())) {
						// undo code;
						//System.out.println("breaking for" + tempCourse);
						//System.out.println("Failinggggggg"+tempCourse);
						array[0] = save1;
						array[1] = save2;
						slot = save3;
						TT.setFailed(tempCourse);
						tempCourse.setProcessed(true);
						//if this course is failed,it's removed from buffer also as it's no more required to check and it's
						//interfering in normal processing. Buffer always keeps initial states. So, this failing course might
						//be getting priority always.
						for(int n=0;n<GenerateTTEndSem.buffer.size();n++)
						{
							Course course2=GenerateTTEndSem.buffer.get(n);
								if(course2.getCourse_id().equals(tempCourse.getCourse_id()))
								{
									GenerateTTEndSem.buffer.remove(n);
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
					if (k == 0 && flag_failed==0 && (save4 == array[k].getRooms().size() - 1)
							&& (tempCourse.getUnallocated_strength() > 0) && (j == array[k].getRooms().size())) {
						flag_failed=1;
						break;
						// send to next pattern.
					}
					// tempCourse = slot.chosingCourse();
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

			//p++;
			if(flag_small==1)
				k=1;
			
			return new Utility1(array,k,tempCourse,slot,flag_failed);
		}
		
		public boolean courseIntact(TimeInterval[] old_array,TimeInterval[] new_array)
		{
			HashSet<String> old_courses=new HashSet<>();
			HashSet<String> new_courses=new HashSet<>();
			//extracting old courses from TimeInterval2
			for(ArrayList<OccupationData> al:old_array[0].getMap().values())
			{ 
				for(OccupationData od:al)
				{
					old_courses.add(od.getCourse().getCourse_id());
				}
			}
			//extracting new courses from TimeInterval2
			for(ArrayList<OccupationData> al:new_array[0].getMap().values())
			{
				for(OccupationData od:al)
				{
					new_courses.add(od.getCourse().getCourse_id());
				}
			}
			if(old_courses.isEmpty() && new_courses.isEmpty())
				return false;
			for(String courseid:old_courses)
			{
				int flagg=0;
				for(String new_course_id:new_courses)
				{
					if(courseid.equals(new_course_id))
						flagg=1;
				}
				if(flagg==0)
					return false;
			}
			return true;
		}

}
