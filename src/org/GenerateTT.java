package org;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GenerateTT {

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
			

			TimeInterval time1 = new TimeInterval(1);
			TimeInterval time2 = new TimeInterval(2);
			int flag = 0;// a flag to use to forcibly exit loops or restarting
							// loops.
			TimeInterval[] array = { time1, time2 };
			int p = 0;
			Slot slot = TT.getSlot()[h];
			
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
			
			buffer=new ArrayList<>();
			while (slot.slotProcessed())// if all the courses are processed,
										// then loop breaks. 
			{
				//Buffer Concept: Earlier, it was chosing a course and then allocating it. Once allocated, it was almost fixed except
				// tryToadjust function. Now, I am maintaining a buffer of courses with original states. If a course comes from normal 
				//chosingCourse function with preexisting constraints, it first goes
				//to buffer with its initial states. Then, this course is processesd.Similarly, when a next course comes, it first goes to buffer.
				//Buffer sorts in descending order. Now, whenever a new course comes, all the courses are allocated
				//afresh so as to allocate in a optimized way. This rules out small courses with high priorities blocking
				//out a lot of rooms and wasting a time interval when a big course comes. A big course comes but it didn't
				//have enoguh rooms earlier.Now, it has.
				
				Course tempCourse = slot.chosingCourse();// refer slot class for
				// details
								
				buffer.add(new Course(tempCourse));//copy of new course so that initial state is always fresh even if 
				//source ArrayList in slot class is updated(when course is processed=true).
				Collections.sort(buffer, new CourseComparatorByCapacity());//reassigning priorities in descending order.
				//System.out.println("Buffer:");
//				for(Course course:buffer)
//					System.out.println(course);
				//System.out.println("Address of:"+tempCourse.getCourse_name()+""+tempCourse);
				TimeInterval[] array2=null;
				
				buffer_copy=new ArrayList<>();
//				for(Course course:buffer)
//				{
//					Course course_copy=new Course(course);
//					buffer_copy.add(course_copy);
//				}
				Utility1 utility=null;
				
				if(h!=0)//Except slot 1,all the slots are getting processed through buffer concept.
				{
					for(Course course:buffer)
					{
					
					utility=TT.dynamicAllot(array2, course, slot, TT, 0,0);
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
				if(h==0 || !TT.courseIntact(array, array2)) 
				{
					//System.out.println(tempCourse+"finally going to ti2");
					if(h!=0 && !TT.courseIntact(array, array2))
						k=0;//always gives k=0 if slot is not 1. Basically, I have ruled out alternating k from slot >1
					
				Utility1 utility2=TT.dynamicAllot(array, tempCourse, slot, TT, k,1);// normal case.
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
//		 System.out.println("Following courses Failed");
//		 for (Course course : TT.getFailed())
//		 System.out.println(course);
		failedCourses = TT.getFailed();
		excel.createExcelSheet(TT);

		// Map<Course,Integer> map=GenerateTT.printUnallocatedStudents(TT);

	}
}