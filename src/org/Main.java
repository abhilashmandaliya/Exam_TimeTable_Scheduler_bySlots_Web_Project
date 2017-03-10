package org;



import java.util.ArrayList;
import java.io.Serializable;
import java.sql.SQLException;


public class Main implements Serializable {
	public static void main(String[] args) throws CloneNotSupportedException,SQLException,ClassNotFoundException,DAOException {
		System.out.println("Time Table");
		Slot slot = new Slot(1);
		TimeInterval time1 = new TimeInterval(1, 1);
		TimeInterval time2 = new TimeInterval(1, 2);
		int flag = 0;

		TimeInterval[] array = new TimeInterval[2];
		array[0] = time1;
		array[1] = time2;
		int totalCapacityOfRooms = 0;
		for (int i = 0; i < array[0].rooms.size(); i++) {
			totalCapacityOfRooms = totalCapacityOfRooms + array[0].rooms.get(i).getCapacity();
		}
		int p = 0;
		while (slot.slotProcessed()) {
			System.out.println("********************process count:" + slot.processCount);
			int flagContinue = 0;
			int flagContinue2 = 0;
			Course tempCourse = slot.chosingCourse();// giving course from the
														// slot having max
														// students>0
			System.out.println("Started fitting Course:" + tempCourse);
			// special case for invigilation
			// int strength=tempCourse.getUnallocatedStrength();

			for (int i = 0; i < 5; i++) {
				System.out.println(
						" T1 right capacity of room" + (i + 1) + "--" + array[0].rooms.get(i).getRightCapacity());
				System.out.println(
						" T1 left capacity of room" + (i + 1) + "--" + array[0].rooms.get(i).getLeftCapacity());
				System.out.println(
						" T2 right capacity of room" + (i + 1) + "--" + array[1].rooms.get(i).getRightCapacity());
				System.out.println(
						" T2 left capacity of room" + (i + 1) + "--" + array[1].rooms.get(i).getLeftCapacity());

			}
			for (int k = 0; k < 2; k++) {
				if (k == 1)
					System.out.println("invigilation k=1");
				System.out.println("testing 2");
				// TimeInterval array[k]=array[k];//time interval among 1 and 2
				flag = 0;
				for (int i = 0; i < array[k].rooms.size(); i++) {
					if (array[k].rooms.get(i).getInvigilanceRequired() && (array[k].rooms.get(i).getLeftStrength()>0 || array[k].rooms.get(i).getRightStrength()>0)) // if
																		// invigilance
																		// required,it
																		// goes
																		// inside
					// but doesn't ensure that it will give because invigilance
					// means one full block in
					// one class. It will try several combinations and if it is
					// not possible to accomodate
					// one full chunk in one full class,it means,it needs to be
					// splitted itself.
					{
						System.out.println("Entering Invigilation in time Interval " + array[k].time_interval);
						Room proposedRoom = array[k].rooms.get(i);
						int left = proposedRoom.getLeftStrength();
						int right = proposedRoom.getRightStrength();
						int capacity = proposedRoom.getCapacity();
						int finalChunkStudents = tempCourse.getUnallocatedStrength();
						if (left > right) {

							if (finalChunkStudents < proposedRoom.getRightCapacity()) {
								tempCourse.processed = true;
								tempCourse.invigilanceEnsured = true;
								proposedRoom.invigilanceRequired = false;
								proposedRoom.setRightStrength(finalChunkStudents);
								tempCourse.setUnallocatedStrength(finalChunkStudents);// setting
																						// to
																						// 0.

								// tempCourse.roomData.add(new
								// OccupationData(1,tempCourse.getUnallocatedStrength(),proposedRoom.room_no));
								slot.updateProcessCount();
								flag = 1;
							//	tempCourse.allocatedStudents=
								array[k].addCourse(proposedRoom.room_no, tempCourse);
								System.out.println("Allocated Right in invigilation");
								break;
							}
						} else {

							if (finalChunkStudents < proposedRoom.getLeftCapacity()) {
								tempCourse.processed = true;
								tempCourse.invigilanceEnsured = true;
								proposedRoom.invigilanceRequired = false;
								proposedRoom.setLeftStrength(finalChunkStudents);
								tempCourse.setUnallocatedStrength(finalChunkStudents);// setting
																						// to
																						// 0.

								// tempCourse.roomData.add(new
								// OccupationData(1,tempCourse.getUnallocatedStrength(),proposedRoom.room_no));
								slot.updateProcessCount();
								flag = 1;
								array[k].addCourse(proposedRoom.room_no, tempCourse);
								System.out.println("Allocated Left in invigilation");
								break;
							}
						}
					}

				}
				// array[k].print();
				if (flag == 1)
					break;
			}

			if (flag == 1)
				continue;

			// *****************************************************************************

			// special case for to check that entire course gets allocated in 1
			// room.
			for (int k = 0; k < 2; k++) {
				// TimeInterval array[k]=array[k];//time interval among 1 and 2
				flag = 0;
				if (k == 1)
					System.out.println("Entire k=1");
				for (int i = 0; i < array[k].rooms.size(); i++) {
					Room proposedRoom = array[k].rooms.get(i);
					int left = proposedRoom.getLeftStrength();
					int right = proposedRoom.getRightStrength();
					int capacity = proposedRoom.getCapacity();
					int finalChunkStudents = tempCourse.getUnallocatedStrength();

					if (left > right) {
						if (finalChunkStudents < proposedRoom.getRightCapacity()) {
							tempCourse.processed = true;
							tempCourse.invigilanceEnsured = true;
							proposedRoom.invigilanceRequired = false;
							proposedRoom.setRightStrength(finalChunkStudents);
							tempCourse.setUnallocatedStrength(finalChunkStudents);// setting
																					// to
																					// 0.
							// tempCourse.roomData.add(new
							// OccupationData(1,tempCourse.getUnallocatedStrength(),proposedRoom.room_no));
							slot.updateProcessCount();
							flag = 1;
							array[k].addCourse(proposedRoom.room_no, tempCourse);
							break;
						}
					} else {
						if (finalChunkStudents < proposedRoom.getLeftCapacity()) {
							tempCourse.processed = true;
							tempCourse.invigilanceEnsured = true;
							proposedRoom.invigilanceRequired = false;
							proposedRoom.setLeftStrength(finalChunkStudents);
							tempCourse.setUnallocatedStrength(finalChunkStudents);// setting
																					// to
																					// 0.

							// tempCourse.roomData.add(new
							// OccupationData(1,tempCourse.getUnallocatedStrength(),proposedRoom.room_no));
							slot.updateProcessCount();
							flag = 1;
							array[k].addCourse(proposedRoom.room_no, tempCourse);
							break;
						}
					}
				}
				if (flag == 1)
					break;
			}
			if (flag == 1)
				continue;
			// small and big cases

			int k;
			if (tempCourse.getTotalStudents() > (0.80) * totalCapacityOfRooms) {
				k = p % 2;
			} else
				k = 0;
			for (; k < 2; k++) {
				if (k == 1)
					System.out.println("k is oneeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
				System.out.println("small and big case entered");
				// TimeInterval array[k]=array[k];//time interval among 1 and 2
				flag = 0;

				for (int j = 0; j < array[k].rooms.size(); j++)// checking for
																// number of
																// cases = no of
																// rooms
				{
					// if(slot.chosingCourse().getUnallocatedStrength()==0)
					// break;
					System.out.println("testing 4");
					// System.out.println("Time interval before: from
					// save1"+save1.R4.);
					TimeInterval save1 = new TimeInterval(time1);
					System.out.println("55555555555555555" + save1.equals(time1));
					TimeInterval save2 = new TimeInterval(time2);
					Slot save3 = new Slot(slot);
					System.out.println("Old time 1,Room 5 capacity left " + save1.rooms.get(3).getRightCapacity());
					System.out.println("Old time 2,Room 5 capacity left " + save2.rooms.get(3).getRightCapacity());

					System.out.println("Old time 1,Room 5 capacity left " + time1.rooms.get(3).getRightCapacity());
					System.out.println("Old time 2,Room 5 capacity left " + time2.rooms.get(3).getRightCapacity());
					System.out.println("Old time 2,Room 5 right strength " + save1.rooms.get(3).getRightStrength());
					System.out.println(
							"Old time 2,Room 5 time 1 right strength " + time1.rooms.get(3).getRightStrength());
					for (int i = j; i < array[k].rooms.size(); i++) // all the
																	// default
																	// sides to
																	// look for
																	// in class
																	// is big.
																	// Setting
																	// to small
																	// for some.
					{
						array[k].rooms.get(i).setCheckBig(false);
						System.out.println("setting rooms to small");
					}

					// Course save3=tempCourse;
					int save4 = 0;
					for (int i = 0; i < array[k].rooms.size(); i++) {
						System.out.println("testing 6");
						save4 = i;
						Room proposedRoom = array[k].rooms.get(i);
						System.out.println("Room:" + proposedRoom.room_no);
						int left = proposedRoom.getLeftStrength();
						int right = proposedRoom.getRightStrength();
						int capacity = proposedRoom.getCapacity();
						boolean leftGo = false;
						boolean rightGo = false;
						boolean tempCheckBig = array[k].rooms.get(i).getCheckBig();// to
																					// determine
																					// what
																					// to
																					// check-small/big
						// System.out.println("left: "+left+"right:
						// "+right+"checkBig: "+tempCheckBig);
						if (tempCheckBig == true) {
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
						} else if (tempCheckBig == false) {
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
							System.out.println("RightGo working");
							// System.out.println("testing 7");
							int rightCapacityRemaining = capacity - right;
							if (rightCapacityRemaining < tempCourse.getUnallocatedStrength()) {
								System.out.println("initial right capacity:" + proposedRoom.getRightCapacity());
								proposedRoom.setRightStrength(rightCapacityRemaining);
								tempCourse.setUnallocatedStrength(rightCapacityRemaining);// reduced
																							// course
																							// students
																							// by
																							// temp
								System.out.println("Right Capacity Remaining: " + proposedRoom.getRightCapacity()
										+ "Unallocated Strength left in Course: "
										+ tempCourse.getUnallocatedStrength());

								// tempCourse.setUnallocatedStrength(strength);//setting
								// to 0.
								// int
								// from=tempCourse.roomData.get(tempCourse.roomData.size()-1).toRoll+1;
								// tempCourse.roomData.add(new
								// OccupationData(from,from+tempCourse.getUnallocatedStrength(),proposedRoom.room_no));
								// slot.updateProcessCount();
								array[k].addCourse(proposedRoom.room_no, tempCourse);
								// break;
							} else {
								// finish course.
								System.out.println("last Block to finish last chunk of students");
								int finalChunkStudents = tempCourse.getUnallocatedStrength();
								System.out.println("in Last block: unallocated strength:" + finalChunkStudents);
								tempCourse.setUnallocatedStrength(finalChunkStudents);// reduced
																						// course
																						// students
																						// by
																						// temp
								proposedRoom.setRightStrength(finalChunkStudents);
								System.out.println("Right Capacity Remaining: " + proposedRoom.getRightCapacity()
										+ "Unallocated Strength left in Course: "
										+ tempCourse.getUnallocatedStrength());
								tempCourse.processed = true;
								/*
								 * if(proposedRoom.invigilanceRequired!=false)//
								 * this course is not providing invigilance.So,
								 * //if no invigilance required beacause of some
								 * another course,then it's fine. But if this
								 * //course is expected to provide
								 * invigilance,then it's mirage. {
								 * proposedRoom.invigilanceRequired=true; }
								 */

								// int
								// from=tempCourse.roomData.get(tempCourse.roomData.size()-1).toRoll+1;
								// tempCourse.roomData.add(new
								// OccupationData(from,from+tempCourse.getUnallocatedStrength(),proposedRoom.room_no));
								slot.updateProcessCount();
								flag = 1;
								array[k].addCourse(proposedRoom.room_no, tempCourse);
								tempCourse.processed = true;
								break;
							}
						} else if (leftGo) {
							int leftCapacityRemaining = capacity - left;
							System.out.println("LeftGo working");
							// System.out.println("testing 8");
							if (leftCapacityRemaining < tempCourse.getUnallocatedStrength()) {
								// int temp=capacity-left;
								proposedRoom.setLeftStrength(leftCapacityRemaining);
								tempCourse.setUnallocatedStrength(leftCapacityRemaining);// reduced
																							// course
																							// students
																							// by
																							// temp
								System.out.println("Left Capacity Remaining: " + proposedRoom.getLeftCapacity()
										+ "Unallocated Strength left in Course: "
										+ tempCourse.getUnallocatedStrength());
								// tempCourse.invigilanceEnsured=false;
								/*
								 * if(proposedRoom.invigilanceRequired!=false)//
								 * this course is not providing invigilance.So,
								 * //if no invigilance required beacause of some
								 * another course,then it's fine. But if this
								 * //course is expected to provide
								 * invigilance,then it's mirage. {
								 * proposedRoom.invigilanceRequired=true; }
								 */

								// tempCourse.setUnallocatedStrength(strength);//setting
								// to 0.
								// int
								// from=tempCourse.roomData.get(tempCourse.roomData.size()-1).toRoll+1;
								// tempCourse.roomData.add(new
								// OccupationData(from,from+tempCourse.getUnallocatedStrength(),proposedRoom.room_no));
								// slot.updateProcessCount();
								array[k].addCourse(proposedRoom.room_no, tempCourse);
								// break;
							} else {
								// finish course.
								// finish course.
								System.out.println("LEFTGO: last Block to finish last chunk of students");
								int finalChunkStudents = tempCourse.getUnallocatedStrength();
								System.out.println("in Last block: unallocated strength:" + finalChunkStudents);
								tempCourse.setUnallocatedStrength(finalChunkStudents);// reduced
																						// course
																						// students
																						// by
																						// temp
								proposedRoom.setLeftStrength(finalChunkStudents);
								System.out.println("left Capacity Remaining: " + proposedRoom.getLeftCapacity()
										+ "Unallocated Strength left in Course: "
										+ tempCourse.getUnallocatedStrength());
								tempCourse.processed = true;
								/*
								 * if(proposedRoom.invigilanceRequired!=false)//
								 * this course is not providing invigilance.So,
								 * //if no invigilance required beacause of some
								 * another course,then it's fine. But if this
								 * //course is expected to provide
								 * invigilance,then it's mirage. {
								 * proposedRoom.invigilanceRequired=true; }
								 */

								// int
								// from=tempCourse.roomData.get(tempCourse.roomData.size()-1).toRoll+1;
								// tempCourse.roomData.add(new
								// OccupationData(from,from+tempCourse.getUnallocatedStrength(),proposedRoom.room_no));
								slot.updateProcessCount();
								flag = 1;
								array[k].addCourse(proposedRoom.room_no, tempCourse);
								tempCourse.processed = true;
								break;

							}
							time1.print();
							time2.print();
						} // leftGo

					}

					// till here course has been allocated in all the rooms in
					// time interval 1
					for (int i = 0; i < array[k].rooms.size(); i++) {
						array[k].rooms.get(i).setCheckBig(true);
					}
					flagContinue = 0;
					if (k == 0 && tempCourse.getUnallocatedStrength() > 0) {
						// undo code;
						System.out.println("Compare new " + time1.rooms.get(4).getLeftCapacity());
						System.out.println("Compare old " + save1.rooms.get(4).getLeftCapacity());
						time1 = save1;
						System.out.println("Running flagContinue");
						flagContinue = 1;
						time2 = save2;
						slot = save3;
						tempCourse = slot.chosingCourse();
						// flag=1;
						// send to k1
						System.out
								.println("time 1 unallocated strength:" + slot.chosingCourse().getUnallocatedStrength()
										+ "R5,time1: " + time1.rooms.get(4).getLeftCapacity());
					}

					flagContinue2 = 0;
					if (k == 1 && (save4 == array[k].rooms.size() - 1) && (tempCourse.getUnallocatedStrength() > 0)) {
						System.out.println("Running flagContinue2");
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
		array[1].print();

	}

}
