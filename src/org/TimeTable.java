package org;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.PropertyTemplate;
import org.apache.poi.ss.usermodel.BorderExtent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;

public class TimeTable {
	
	private final Connection con;
	private Slot slot[];
	private Map<Integer,StoreTT> store;
	public Slot[] getSlot() {
		return slot;
	}
	public Map<Integer, StoreTT> getStore() {
		return store;
	}
	
	public TimeTable() throws ClassNotFoundException, SQLException, DAOException
	{
		con=DBConnection.getInstance().getConnectionSchema("public");
		getSlotDetails();
		store=new HashMap<>();
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
	
	//this function merges region for time duration and "CEP Rooms tag"
	public static void merge(int i,int j,Sheet sheet1,ArrayList<Room> rooms)
	{
		sheet1.addMergedRegion(new CellRangeAddress(
	            i, //first row (0-based)
	            i+1, //last row  (0-based)
	            j+1, //first column (0-based)
	            j+3  //last column  (0-based)
	    ));
    	sheet1.addMergedRegion(new CellRangeAddress(
	            i, //first row (0-based)
	            i, //last row  (0-based)
	            j+4, //first column (0-based)
	            j+4+rooms.size()-1  //last column  (0-based)
	    ));
	}
	
	public static void printRooms(int i,int j,Sheet sheet1,ArrayList<Room> rooms)
	{
		Row row2=sheet1.getRow(i+1);
		if(row2==null)
		row2=sheet1.createRow(i+1);
	    for(int k=0;k<rooms.size();k++)
	    {
	    	Cell cell=row2.createCell(j+4+k);
	    	cell.setCellValue(rooms.get(k).getRoom_no());
	    	//cell.setCellStyle(style[1]);
	    }
	}
	//this function prints all the data in excel sheet for a particular SLOT and TIME INTERVAL
	public static void printInExcel(Map<Integer,String> batch_id_name,
			Map<Integer,Integer> line,Map<Integer,Integer> range,Sheet sheet1,int j,CellStyle[] style,Set<Course> set,TimeInterval t1,Workbook wb,int flag) throws ClassNotFoundException, DAOException, SQLException
	{
		int flag2=0;//to print labels just for first time.
		int border_first=0;
		int border_last=0;
		for(int batch_id=1;batch_id<=batch_id_name.size();batch_id++)
    	{
    		//check if such batch_id exists
    		if(!line.containsKey(batch_id))
    			continue;
    		//Merge Rows
    		
    		int start=line.get(batch_id);
    		int end=start+range.get(batch_id)-1;
    		int i=start-2;
    	//print default messages
    		
		    
		    ArrayList<Room> rooms=GeneralDAO.getRooms();
		    
		    Row row1=sheet1.getRow(i);
		    if(row1==null)
    		row1 = sheet1.createRow(i);
		    //if flag2==0,it means it's first iteration
		    if(flag2==0)
		    {   border_first=i;
	    		if(flag==1)
			    {
	    			//System.out.println("Working");
			    	TimeTable.merge(i, j, sheet1, rooms);
			    	row1.createCell(j+1).setCellValue(" 08:30 - 10:30 ");
			    	row1.createCell(j+4).setCellValue(" CEP Rooms ");
			    	TimeTable.printRooms(i, j, sheet1, rooms);
			    	flag2=1;
			    }
			    else if(flag==2)
			    {
			    	TimeTable.merge(i, j, sheet1, rooms);
			    	row1.createCell(j+1).setCellValue(" 11:00 - 13:00 ");
			    	row1.createCell(j+4).setCellValue(" CEP Rooms ");
			    	TimeTable.printRooms(i, j, sheet1, rooms);
			    	flag2=1;
			    }
			    else if(flag==3)
			    {	TimeTable.merge(i, j, sheet1, rooms);
			    	row1.createCell(j+1).setCellValue(" 14:00 - 16:00 ");
			    	row1.createCell(j+4).setCellValue(" CEP Rooms ");
			    	TimeTable.printRooms(i, j, sheet1, rooms);
			    	flag2=1;
			    }
			    else if(flag==4)
			    {
			    	TimeTable.merge(i, j, sheet1, rooms);
			    	row1.createCell(j+1).setCellValue(" 16:30 - 18:30 ");
			    	row1.createCell(j+4).setCellValue(" CEP Rooms ");
			    	TimeTable.printRooms(i, j, sheet1, rooms);
			    	flag2=1;
			    }
			    
		    }
		    
    		Row row=sheet1.getRow(start);
    		if(row==null)
    			row=sheet1.createRow(start);
    		//System.out.println("batch_id"+batch_id+"start: "+start+"end: "+end);
    		
    		if(flag==1)
    		{
	    		if((end-start)!=0)
	    		{	
	    		sheet1.addMergedRegion(new CellRangeAddress(
			            start, //first row (0-based)
			            end, //last row  (0-based)
			            j, //first column (0-based)
			            j  //last column  (0-based)
			    ));
	    		}
    		// Write Batch Name
    		
		    row.createCell(j).setCellValue(batch_id_name.get(batch_id));
		    row.getCell(j).setCellStyle(style[batch_id-1]);
    		}
		    //Start allocating courses
		    for(Course course:set)
		    {
		    	
		    	if(Integer.parseInt(course.getBatch())==batch_id)
		    	{
		    		 Row row_temp = sheet1.getRow(start);
		    		 if(row_temp==null)
		    		 {
		    			 row_temp=sheet1.createRow(start);
		    		 }
		    		    
					    row_temp.createCell(j+1).setCellValue(course.getCourse_id());
					    row_temp.getCell(j+1).setCellStyle(style[batch_id-1]);
					    row_temp.createCell(j+2).setCellValue(course.getCourse_name());
					    row_temp.getCell(j+2).setCellStyle(style[batch_id-1]);
					    
					    ArrayList<Room> temp_rooms=t1.getRooms();
					     //first column for timeinterval1
					    int first_col=j+4;
					    for(int p=0;p<temp_rooms.size();p++)
					    {
					    	
					    	System.out.println("Running"+p);
					    	
					    	if(!t1.getMap().containsKey(temp_rooms.get(p).getRoom_no()))
					    		continue;
					    	for(OccupationData od:t1.getMap().get(temp_rooms.get(p).getRoom_no()))
					    	{    		
					    		
					    		if(od.getCourse().getCourse_id()==course.getCourse_id())
					    		{
					    			row_temp.createCell(p+first_col).setCellValue(od.getAllocatedStudents());
					    			row_temp.getCell(p+first_col).setCellStyle(style[batch_id-1]);
					    		}
					    	}
					    	
					    }
					    start++;
		    	}
		    }
		    
		    border_last=end;//last iteration will set last end(although it sets this variable multiple times)
		    }
		 PropertyTemplate pt = new PropertyTemplate();
		  // #1) these borders will all be medium in default color
		  pt.drawBorders(new CellRangeAddress(border_first, border_last, j+1, j+8),
		          BorderStyle.MEDIUM,BorderExtent.OUTSIDE);
		  if(flag==1)
		  {
		  pt.drawBorders(new CellRangeAddress(border_first, border_last, j, j),
		          BorderStyle.MEDIUM,BorderExtent.OUTSIDE);	
		  sheet1.addMergedRegion(new CellRangeAddress(
          border_first, //first row (0-based)
          border_last, //last row  (0-based)
          j-1, //first column (0-based)
          j-1));  //last column  (0-based)
		  Row temp_row=sheet1.getRow(border_first);
		  if(temp_row==null)
			  temp_row=sheet1.createRow(border_first);
		  //temp_row.createCell(j-1,CellStyle.ALIGN_JUSTIFY, CellStyle.VERTICAL_JUSTIFY).setCellValue("Exam Day");
		  TimeTable.createCell(wb, temp_row, j-1, CellStyle.ALIGN_JUSTIFY, CellStyle.VERTICAL_CENTER);
		  pt.drawBorders(new CellRangeAddress(border_first, border_last, j-1, j-1),
		          BorderStyle.MEDIUM,BorderExtent.OUTSIDE);
		  }
		  pt.applyBorders(sheet1);
	}
	 private static void createCell(Workbook wb, Row row, int column, short halign, short valign) {
	        Cell cell = row.createCell(column);
	        cell.setCellValue(" Exam Day ");
	        CellStyle cellStyle = wb.createCellStyle();
	        cellStyle.setAlignment(halign);
	        cellStyle.setVerticalAlignment(valign);
	        cellStyle.setRotation((short)90);
	        cell.setCellStyle(cellStyle);
	    }
	public static void main(String[] args) throws CloneNotSupportedException,SQLException,ClassNotFoundException,DAOException, IOException {

		//storing entire time table in 1 object of TimeTable class.
		TimeTable TT=new TimeTable();
		
		//Running main algorithm for all the slots.It creates timetable of all the slots separately.
		for(int h=0;h<TT.getSlot().length;h++)// 
		{ 
			System.out.println("**************************SLOT NO: "+(h+1)+"****************************");
			System.out.println("Room No     Course ID   Course Name");
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
			TT.store.put(h+1, new StoreTT(h+1,time1,time2));
		}
		
		//mapping numeric code to Batches
		Map<Integer,String> batch_id_name=new HashMap<>();
		batch_id_name.put(1, "BTech-I");
		batch_id_name.put(2, "BTech-II");
		batch_id_name.put(3, "BTech-III");
		batch_id_name.put(4, "BTech-IV");
		batch_id_name.put(5, "MScIT-I");
		batch_id_name.put(6, "MScIT-II");
		batch_id_name.put(7, "MScICTARD-I");
		batch_id_name.put(8, "MScICTARD-II");
		batch_id_name.put(9, "MTech-I");
		batch_id_name.put(10, "MTech-II");
		batch_id_name.put(11, "MDes-I");
		batch_id_name.put(12, "MDes-II");
		
		//export to Excel Format
		//making workbook and sheet
				Workbook wb = new XSSFWorkbook();
				String safeName = WorkbookUtil.createSafeSheetName("Main Result"); // returns " O'Brien's sales   "
			    Sheet sheet1 = wb.createSheet(safeName);
			    
			    CellStyle[] style=new CellStyle[12];
			    //styles
			    //BTech Ist
			    style[0] = wb.createCellStyle();
			    style[0].setFillBackgroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			    style[0].setFillPattern(CellStyle.BIG_SPOTS);
			    
			    //B.Tech IInd
			    style[1] = wb.createCellStyle();
			    style[1].setFillBackgroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
			    style[1].setFillPattern(CellStyle.BIG_SPOTS);
			    
			    //B.Tech IIIrd
			    style[2] = wb.createCellStyle();
			    style[2].setFillBackgroundColor(IndexedColors.BLUE.getIndex());
			    style[2].setFillPattern(CellStyle.BIG_SPOTS);
			    
			    //B.Tech IVth
			    style[3] = wb.createCellStyle();
			    style[3].setFillBackgroundColor(IndexedColors.TAN.getIndex());
			    style[3].setFillPattern(CellStyle.BIG_SPOTS);
			    
			    //M.Sc(IT) Ist 
			    style[4] = wb.createCellStyle();
			    style[4].setFillBackgroundColor(IndexedColors.ORANGE.getIndex());
			    style[4].setFillPattern(CellStyle.BIG_SPOTS);
			    
			    //M.Sc(IT) IInd
			    style[5] = wb.createCellStyle();
			    style[5].setFillBackgroundColor(IndexedColors.LIGHT_GREEN.getIndex());
			    style[5].setFillPattern(CellStyle.BIG_SPOTS);
			    
			    //M.Sc(IT) ARD Ist
			    style[6] = wb.createCellStyle();
			    style[6].setFillBackgroundColor(IndexedColors.YELLOW.getIndex());
			    style[6].setFillPattern(CellStyle.BIG_SPOTS);
			    
			    //M.Sc(IT) ARD IInd
			    style[7] = wb.createCellStyle();
			    style[7].setFillBackgroundColor(IndexedColors.VIOLET.getIndex());
			    style[7].setFillPattern(CellStyle.BIG_SPOTS);
			    
			    //M.Tech Ist
			    style[8] = wb.createCellStyle();
			    style[8].setFillBackgroundColor(IndexedColors.ROSE.getIndex());
			    style[8].setFillPattern(CellStyle.BIG_SPOTS);
			    
			    //M.Tech IInd
			    style[9] = wb.createCellStyle();
			    style[9].setFillBackgroundColor(IndexedColors.LAVENDER.getIndex());
			    style[9].setFillPattern(CellStyle.BIG_SPOTS);
			    
			    //M.Des Ist
			    style[10] = wb.createCellStyle();
			    style[10].setFillBackgroundColor(IndexedColors.GREEN.getIndex());
			    style[10].setFillPattern(CellStyle.BIG_SPOTS);
			  
			    //M.Des IInd
			    style[11] = wb.createCellStyle();
			    style[11].setFillBackgroundColor(IndexedColors.RED.getIndex());
			    style[11].setFillPattern(CellStyle.BIG_SPOTS);
			    
			    //main work
			    CreationHelper createHelper = wb.getCreationHelper();
			    int i=0;
			    int j=0;
			    
			    //block 1 start
			    i=7;
			    j=2;
			    			    
			    ArrayList<Room> rooms=GeneralDAO.getRooms();
			   		    
			    
			    
			    int curr_row=i+2;
			     Map<Integer,StoreTT> temp_store=TT.getStore();
			     
			    //this loop runs in pair->1 run for (Slot1,Slot2),1 run for (Slot3,Slot4),....
			    for(int k=0;k<temp_store.size();k=k+2)
			    {
			    	Map<Integer,Integer> line=new HashMap<>();//batch_id and row number in Excel
			    	Map<Integer,Integer> range=new HashMap<>();//batch_id and range in Excel
			    	
			    	int slot1=k+1;
			    	int slot2=k+2;
			    	int slot2_flag=0;
			    	
			    	//**********checking for first Slot************
			    	TimeInterval t1=temp_store.get(slot1).getT1();
		    		TimeInterval t2=temp_store.get(slot1).getT2();
		    		
		    		//check T1
		    		Set<Course> set1=new HashSet<>();
		    		for(ArrayList<OccupationData> od:t1.getMap().values())
		    		{
		    			for(int hh=0;hh<od.size();hh++)
		    			{
//		    			if(Integer.parseInt(od.get(hh).getCourse().getBatch())==batch_id)
//		    			counter1++;
		    			set1.add(od.get(hh).getCourse());
		    			}
		    		}
		    		
		    		//check T2
		    		curr_row+=1;
		    		Set<Course> set2=new HashSet<>();
		    		for(ArrayList<OccupationData> od:t2.getMap().values())
		    		{
		    			for(int hh=0;hh<od.size();hh++)
		    			{
//		    			if(Integer.parseInt(od.get(hh).getCourse().getBatch())==batch_id)
//		    			counter1++;
		    			set2.add(od.get(hh).getCourse());
		    			}
		    		}
		    		//**********************checking for Slot2***************
		    		Set<Course> set1_2=new HashSet<>();
		    		Set<Course> set2_2=new HashSet<>();
		    		TimeInterval t1_2=null;
		    		TimeInterval t2_2=null;
		    		
		    		if(slot2<=temp_store.size())
			    	{
			    		slot2_flag=1;
			    	    t1_2=temp_store.get(slot2).getT1();
			    		t2_2=temp_store.get(slot2).getT2();
			    		//check T1
			    		curr_row+=1;
			    		for(ArrayList<OccupationData> od:t1_2.getMap().values())
			    		{
			    			for(int hh=0;hh<od.size();hh++)
			    			{
//			    			if(Integer.parseInt(od.get(hh).getCourse().getBatch())==batch_id)
//			    			counter1++;
			    			set1_2.add(od.get(hh).getCourse());
			    			}
			    		}
			    		
			    		//check T2
			    		curr_row+=1;
			    		for(ArrayList<OccupationData> od:t2_2.getMap().values())
			    		{
			    			for(int hh=0;hh<od.size();hh++)
			    			{
//			    			if(Integer.parseInt(od.get(hh).getCourse().getBatch())==batch_id)
//			    			counter1++;
			    			set2_2.add(od.get(hh).getCourse());
			    			}
			    		}
			    	}
		    		//calculating max number of rows required for each batch(Programme)
			    	for(int batch_id=1;batch_id<batch_id_name.size();batch_id++)
			    	{
			    		int max=0;
			    		int counter=0;
			    		//curr_row=curr_row+1;
			    		//check in first slot for occurrences of this batch_id. 
			    		
			    		//check in time-interval1.
			    		for(Course course:set1)
			    		{
			    			if(Integer.parseInt(course.getBatch())==batch_id)
			    				counter++;
			    		}
			    		max=Math.max(max, counter);
			    		counter=0;
			    		for(Course course:set2)
			    		{
			    			if(Integer.parseInt(course.getBatch())==batch_id)
			    				counter++;
			    		}
			    		max=Math.max(max, counter);
			    		counter=0;
			    		for(Course course:set1_2)
			    		{
			    			if(Integer.parseInt(course.getBatch())==batch_id)
			    				counter++;
			    		}
			    		max=Math.max(max, counter);
			    		counter=0;
			    		for(Course course:set2_2)
			    		{
			    			if(Integer.parseInt(course.getBatch())==batch_id)
			    				counter++;
			    		}
			    		max=Math.max(max, counter);
			    		if(max!=0)//to make sure,rows are updated only for those courses which exists in these slots
			    		{
			    			line.put(batch_id, curr_row);
			    			curr_row=curr_row+max;
			    			range.put(batch_id,max);
			    		} 		
			    		
			    	}
			    	// main allocation in excel######################################################
			    	
			    	//for time interval 1
			    	TimeTable.printInExcel(batch_id_name,line,range,sheet1,j,style,set1,t1,wb,1);			    	
			    	//for time interval 2
			    	
			    	TimeTable.printInExcel(batch_id_name,line,range,sheet1,j+9,style,set2,t2,wb,2);
			    	if(slot2_flag==1)
			    	{
			    	//for time interval 1_2
			    		TimeTable.printInExcel(batch_id_name,line,range,sheet1,j+18,style,set1_2,t1_2,wb,3);
			    	//for time interval 1_2
			    		TimeTable.printInExcel(batch_id_name,line,range,sheet1,j+27,style,set2_2,t2_2,wb,4);
			    	}
			    }
			    	
			   
			   
			    	
			    	
			    
			  
			    
			
			    
			    
			    
			    	    

			    //saving output to file
			    sheet1.getPrintSetup().setLandscape(true);
			    FileOutputStream fileOut = new FileOutputStream("workbook.xlsx");
			    wb.write(fileOut);
			    fileOut.close();
	}

}
