package org;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GenerateTTEndSem {

	// this method gets unallocated students after entire allocation
	// public static Map<Course,Integer>
	// printUnallocatedStudents(TimeTableEndSem TT)
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
	private static ArrayList<Course> failedCourses;
	static ArrayList<Course> buffer_copy;
	static ArrayList<Course> buffer;
	public static ArrayList<Course> getFailedCourses() {
		return failedCourses;
	}

	public static void main(String[] args)
			throws CloneNotSupportedException, SQLException, ClassNotFoundException, DAOException, IOException, CustomException {
		// GeneralDAO.deleteAllCourses();
		// ReadFromExcel.read_excel();
		// storing entire time table in 1 object of TimeTable class.
		if(GeneralDAO.getRooms().size()<2){
			TransactionStatus.setStatusMessage("There must be atleast 2 rooms !");
			throw new CustomException("");
		}
		TimeTableEndSem TT = new TimeTableEndSem();
		Set<Integer> set = new HashSet<>();// stores batch numbers of
											// timeinterval2 of odd slots
		// Running main algorithm for all the slots.It creates timetable of all
		// the slots separately.
		for (int h = 0; h < TT.getSlot().length; h++)//
		{
			System.out.println("**************************SLOT NO: " + (h + 1) + "****************************");
			// System.out.println("Room No Course ID Course Name");

			// if((h+1)%2==0)
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

			if (h == 0) {

				HashSet<Integer> set2 = new HashSet<>();
				for (Course course : GeneralDAO.getCourses(2)) {
					set2.add(Integer.parseInt(course.getBatch()));
				}
				for (Course course : slot.getCourses()) {
					if (set2.contains(Integer.parseInt(course.getBatch()))) {
						course.setSlot1priority(1);
					}
					System.out.println(course + " and priority: " + course.getSlot1priority());
				}

				// System.out.println("set2"+set2);
				// for(Course course:slot.getSlot2courses())
				// System.out.println(course);
			} else if (h != 0) {
				for (Course course : slot.getCourses()) {
					if (set.contains(Integer.parseInt(course.getBatch()))) {
						course.setFlag_clash(1);
						// System.out.println("setting flag_clash=1:::"+course);
					}
				}
			}
			set.removeAll(set);
			int k = 1;
			
			buffer=new ArrayList<>();
			while (slot.slotProcessed())// if all the courses are processed,
										// then loop breaks.
			{
				
				Course tempCourse = slot.chosingCourse();// refer slot class for
															// details
				buffer.add(new Course(tempCourse));//copy of new course so that initial state is always fresh even if 
				//source ArrayList in slot class is updated(when course is processed=true).
				Collections.sort(buffer, new CourseComparatorByCapacity());//reassigning priorities in descending order.
				System.out.println("Buffer:");
				for(Course course:buffer)
					System.out.println(course);
				//System.out.println("Address of:"+tempCourse.getCourse_name()+""+tempCourse);
				TimeInterval[] array2=null;
				
				buffer_copy=new ArrayList<>();
//				for(Course course:buffer)
//				{
//					Course course_copy=new Course(course);
//					buffer_copy.add(course_copy);
//				}
				Utility1 utility=null;
				
				
				if(true)//Except slot 1,all the slots are getting processed through buffer concept.
				{
					for(Course course:buffer)
					{
					
					utility=TT.dynamicAllot(array2, course, slot, TT, 1,0);
					k=utility.k;//useless in this case.
					array2=utility.array;//restoring array and slot after processing in above function.
					slot=utility.slot;
					//System.out.println("********************");
					buffer_copy.add(utility.course);// Storing all the courses of this buffer. This buffer_copy contains
					//Courses when all the courses from buffer have been processed and this needs to be updated to chosingCourse()
					//so that it choses next prior course.
//					array2[0].print();
//					array2[1].print();
					}
				}
				
				//For first slot, no buffer concept and if a course could not be processed or it lead to alter the existing ti 2
				//structure, undo it and reallocate it in normal case. below is normal case as for slot1. Check TimeTable class
				//for CourseIntact. 
				//NORMAL ALLOTMENT OPTION
				if(!TT.courseIntact(array, array2)) 
				{
					//System.out.println(tempCourse+"finally going to ti2");
//					if(!TT.courseIntact(array, array2))
//						k=1;//always gives k=0 if slot is not 1. Basically, I have ruled out alternating k from slot >1
					
				Utility1 utility2=TT.dynamicAllot(array, tempCourse, slot, TT, 1,1);// normal case.
				k=utility2.k;
				array=utility2.array;
				slot=utility2.slot;
				//pass on course
				//System.out.println("----------"+utility2.course+" "+utility2.course.getProcessed());
					
				//updating course in the source ArrayList of Slot class which chosingCourse() uses.
				for(int n=0;n<slot.getCourses().size();n++)
					{
						Course course2=slot.getCourses().get(n);
							if(course2.getCourse_id().equals(utility2.course.getCourse_id()))
							{
								slot.getCourses().set(n, utility2.course);
								
							}
						
					}
				}
				else
				{//if courseIntact is true, it means our buffer is successfully processed. Now, replace the existing 
					//timeinterval with new one with updated data.
					array=array2;
					
					//Further, update all the courses from buffer with modified state in buffer_copy to source arraylist
					//of slot class.
					for(int n=0;n<slot.getCourses().size();n++)
					{
						Course course2=slot.getCourses().get(n);
						for(Course course3:buffer_copy)
							if(course2.getCourse_id().equals(course3.getCourse_id()))
							{
								slot.getCourses().set(n, course3);
								
							}
						
					}
				}
				slot.updateProcessCount(); //One iteration of while loop processes one course 
				//fully irrespective of the fact that it has been failed, allocated in one go or in different buffer.
				
			}
			array[0].print();
			array[1].print();
			TT.getStore().put(h + 1, new StoreTT(h + 1, array[0], array[1]));
			//System.out.println("It's really painful************");
//			Set<Course> set1 = new HashSet<>();
//			for (ArrayList<OccupationData> od : array[0].getMap().values()) {
//				for (int hh = 0; hh < od.size(); hh++) {
//					set1.add(od.get(hh).getCourse());
//				}
//			}
			//System.out.println("set1" + set1 + "for k=" + k);
			// System.out.println(set);
			if (!array[0].getMap().isEmpty())// if time2 has some courses
												// allocated
			{System.out.println("Printingggg"+array[0].getMap().size());
				for (ArrayList<OccupationData> od : array[0].getMap().values())// stores
																				// all
																				// the
																				// batches
																				// of
																				// time2
																				// in
																				// set
				{
					System.out.println("Printing"+od.size());
				 	for (int hh = 0; hh < od.size(); hh++) {
						set.add(Integer.parseInt(od.get(hh).getCourse().getBatch()));
					}
				}
			}
			// System.out.println(set);
		}
		// It prints data in excel sheet and exports a .xlsx file
		// PrintExcel excel=new PrintExcel();
		PrintExcelEndSem excel = new PrintExcelEndSem();

		System.out.println("Following courses Failed");
		failedCourses = TT.getFailed();
		excel.createExcelSheet(TT);
		// Map<Course,Integer> map=GenerateTT.printUnallocatedStudents(TT);
	}
}
