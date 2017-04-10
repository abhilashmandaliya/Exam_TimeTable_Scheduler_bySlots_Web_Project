package org;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GenerateTT {

	private static ArrayList<Course> failedCourses;

	public static ArrayList<Course> getFailedCourses() {
		return failedCourses;
	}
	// this method gets unallocated students after entire allocation
	// public static Map<Course,Integer> printUnallocatedStudents(TimeTable TT)
	// {
	// Slot[]slot_data=TT.getSlot();
	//
	// Map<Course,Integer> unallocated_map=new HashMap<>();
	//
	// for(Slot s:slot_data)
	// {
	// ArrayList<Course> courses=s.getCourses();
	// for(Course course:courses)
	// {
	// if(course.getUnallocated_strength()>0)
	// {
	// unallocated_map.put(course, course.getUnallocated_strength());
	// }
	// }
	// }
	// return unallocated_map;
	// }

	public static void main(String[] args)
			throws CloneNotSupportedException, SQLException, ClassNotFoundException, DAOException, IOException, CustomException {
		// GeneralDAO.deleteAllCourses();
		// ReadFromExcel.read_excel();
		// ReadFromExcel.read_excel(1);
		// ReadFromExcel.read_excel(2);
		// ReadFromExcel.read_excel(3);
		// ReadFromExcel.read_excel(4);
		// ReadFromExcel.read_excel(5);
		// ReadFromExcel.read_excel(6);
		// ReadFromExcel.read_excel(7);
		// ReadFromExcel.read_excel(8);

		// storing entire time table in 1 object of TimeTable class.
		if(GeneralDAO.getRooms().size()<2){
			TransactionStatus.setStatusMessage("There must be atleast 2 rooms !");
			throw new CustomException("");
		}
		TimeTable TT = new TimeTable();
		Set<Integer> set = new HashSet<>();// stores batch numbers of
											// timeinterval2 of odd slots
		// Running main algorithm for all the slots.It creates timetable of all
		// the slots separately.
		for (int h = 0; h < TT.getSlot().length; h++)//
		{
			System.out.println("**************************SLOT NO:" + (h + 1) + "****************************");
			// System.out.println("Room No Course ID Course Name");
			//
			// if((h+1)%2!=0)
			// {
			// set.removeAll(set);//clear set for reuse
			// }
			//

			TimeInterval time1 = new TimeInterval(1);
			TimeInterval time2 = new TimeInterval(2);
			int flag = 0;// a flag to use to forcibly exit loops or restarting
							// loops.
			TimeInterval[] array = { time1, time2 };
			int p = 0;
			Slot slot = TT.getSlot()[h];
			// slot.getSlot2courses().clear();
			// if(h==0)
			// {
			//
			// HashSet<String> set2=new HashSet<>();
			// for(Course course:GeneralDAO.getCourses(2))
			// {
			// set2.add(course.getBatch());
			// }
			// for(Course course:slot.getCourses())
			// {
			// if(set2.contains(course.getBatch()))
			// {
			// slot.setSlot2courses(course);
			// }
			// }
			// }
			// if (h == 0) {
			//
			// HashSet<Integer> set2 = new HashSet<>();
			// for (Course course : GeneralDAO.getCourses(2)) {
			// set2.add(Integer.parseInt(course.getBatch()));
			// }
			// for (Course course : slot.getCourses()) {
			// if (set2.contains(Integer.parseInt(course.getBatch()))) {
			// course.setSlot1priority(1);
			// }
			// System.out.println(course + " and priority: " +
			// course.getSlot1priority());
			// }
			// } else
			if (h != 0) {
				for (Course course : slot.getCourses()) {
					if (set.contains(Integer.parseInt(course.getBatch()))) {
						course.setFlag_clash(1);
						// System.out.println("setting flag_clash=1:::"+course);
					}
				}
			}
			set.removeAll(set);
			int k = 0;
			while (slot.slotProcessed())// if all the courses are processed,
										// then loop breaks.
			{

				int flagContinue = 0;
				int flagContinue2 = 0;
				Course tempCourse = slot.chosingCourse();// refer slot class for
															// details
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
					continue;
				}
				// CASE 3: small and big cases
				
				// if a course has come here,it means that it's broken in
				// different chunks
				tempCourse.setBroken(true);

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

					// flip k
					if (k == 0)
						k = 1;
					else if (k == 1)
						k = 0;
				} else {
					k = 1;// this is causing 4-5 times printing in excel if
							// given wrong value.
				}

				// always start from k=1 for flag_clash==1

				if (tempCourse.getFlag_clash() == 1)
					k = 1;
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

						// till here course has been allocated in all the rooms
						// in
						// time interval 1
						flagContinue = 0;
						flagContinue2 = 0;
						System.out.println("visiting");
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
							array[0] = save1;
							flagContinue = 1;
							flagContinue2 = 1;
							array[1] = save2;
							slot = save3;
							tempCourse = slot.chosingCourse();
							System.out.println("flag1: " + tempCourse);
							// array[0].printRooms();
							// array[0].setRooms(save5);

							// send to k0(check same pattern for next time
							// interval)

						}

						// above pattern could not fit in both the time
						// intervals,so change the pattern and undo codes
						if (k == 0 && (save4 == array[k].getRooms().size() - 1)
								&& (tempCourse.getUnallocated_strength() > 0)) {
							// undo code;
							array[0] = save1;
							array[1] = save2;
							slot = save3;
							flagContinue2 = 1;
							tempCourse = slot.chosingCourse();
							System.out.println("flag2: " + tempCourse);
							// send to next pattern.
						}

						// below is for normal stopping after all the (j,i)
						// combinations are finished. Now, it is still
						// unallocated, so this is failed
						// adding it to failed list.
						if (k == 0 && (save4 == array[k].getRooms().size() - 1)
								&& (tempCourse.getUnallocated_strength() > 0) && (j == array[k].getRooms().size())) {
							// undo code;
							System.out.println("breaking for" + tempCourse);

							TT.setFailed(tempCourse);
							tempCourse.setProcessed(true);
							slot.updateProcessCount();
							System.out.println(tempCourse.getProcessed());
							flag = 1;
							flagContinue2 = 0;
							flagContinue = 0;
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

				p++;
			}
			array[0].print();
			array[1].print();
			TT.getStore().put(h + 1, new StoreTT(h + 1, array[0], array[1]));

			if (!array[1].getMap().isEmpty())// if time2 has some courses
												// allocated
			{
				for (ArrayList<OccupationData> od : array[1].getMap().values())// stores
																				// all
																				// the
																				// batches
																				// of
																				// time2
																				// in
																				// set
				{
					for (int hh = 0; hh < od.size(); hh++) {
						set.add(Integer.parseInt(od.get(hh).getCourse().getBatch()));
					}
				}
			}
			// System.out.println(set);

		}
		// It prints data in excel sheet and exports a .xlsx file
		// PrintExcel excel=new PrintExcel();
		PrintExcel excel = new PrintExcel();
		 System.out.println("Following courses Failed");
		 for (Course course : TT.getFailed())
		 System.out.println(course);
		failedCourses = TT.getFailed();
		excel.createExcelSheet(TT);

		// Map<Course,Integer> map=GenerateTT.printUnallocatedStudents(TT);

	}
}