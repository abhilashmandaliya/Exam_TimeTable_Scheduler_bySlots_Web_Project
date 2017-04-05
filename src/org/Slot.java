package org;

import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

//this class stores slots say slot1,slot 2,..... Each slot has a set of courses.
//Processing a course means that assigning the students a room for the exam.

public class Slot {
	private final Connection con;
	private ArrayList<Course> courses;// stores all the courses of this slot
										// object
	private int slot_no;// slot number like slot 1,slot 2,...
	private int processCount;

	public Slot(int num) throws ClassNotFoundException, SQLException, DAOException {
		this.slot_no = num;
		this.courses = new ArrayList<>();
		this.con = DBConnection.getInstance().getConnectionSchema("public");
		this.processCount = 0;
	}

	// copy constructor
	public Slot(Slot other) throws ClassNotFoundException, SQLException {
		this.courses = new ArrayList<>();
		this.con = DBConnection.getInstance().getConnectionSchema("public");
		this.slot_no = other.getSlot_no();
		this.processCount = other.getProcessCount();
		for (Course course : other.getCourses()) {
			this.courses.add(new Course(course));// calling copy constructor of
													// Course
		}
	}

	public ArrayList<Course> getCourses() {
		return courses;
	}

	public int getSlot_no() {
		return slot_no;
	}

	public int getProcessCount() {
		return processCount;
	}

	// refreshing courses to this slot object from the database. If there's
	// insertion/deletion in database.
	// this function refreshes the slot object.
	public void refreshCourses() throws SQLException {
		this.courses = getCourseFromDB();
	}

	// This method extracts all the courses from the database corresponding to
	// this slot.
	public ArrayList<Course> getCourseFromDB() throws SQLException {
		ArrayList<Course> temp = new ArrayList<>();
		Statement stmt = con.createStatement();
		String sql = "Select S.course_id,C.course_name,C.batch,C.no_of_students,C.faculty " + "from Slot S,Course C "
				+ "where S.course_id=C.course_id AND " + "S.slot_no=" + this.slot_no;
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			temp.add(new Course(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4),rs.getString(5)));
		}
		return temp;
	}

	public Connection getCon() {
		return con;
	}

	// this method add course to database corresponding to this slot
	public void addCourseToDB(String course_id) throws DAOException {
		try {
			String sql = "Delete from slot where course_id='" + course_id + "'";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			sql = "Insert into Slot (slot_no,course_id) VALUES(" + this.slot_no + ",'" + course_id + "')";
			stmt.executeUpdate(sql);

		} catch (SQLException e) {
			throw new DAOException(e.getMessage());
		}

	}

	// this method deletes course from database corresponding to this slot
	public String deleteCourseFromDB(String course_id) throws DAOException {
		try {
			System.out.println("cid : " + course_id);
			String sql = "Delete from slot where slot_no=" + this.slot_no + " and course_id='" + course_id + "'";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			sql = "Select program from batch_program,course where course.batch = batch_program.batch and course.course_id='"
					+ course_id+"'";
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next())
				return rs.getString(1);			
		} catch (SQLException e) {
			// throw new DAOException(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	// this method finds course which is to be processed by algorithm.
	// First criteria is to chose the course having max number of students.
	// Second, it ensures that this course is still under processing and
	// unallocated strength>0 means
	// it still has some students to be assigned a room.
	public Course chosingCourse() {
		Course course = null;
		int max = 0;

		int flag_no_otherloop = 0;
		for (int i = 0; i < courses.size(); i++) {
			if (courses.get(i).getUnallocated_strength() > 0 && courses.get(i).getProcessed() == false
					&& courses.get(i).getFlag_clash() == 1) {
				if (max < courses.get(i).getNo_Of_Students()) {
					max = courses.get(i).getNo_Of_Students();
					course = courses.get(i);
					flag_no_otherloop = 1;
				}
			}
		}
		// return course;
		if (flag_no_otherloop == 0) {
			for (int i = 0; i < courses.size(); i++) {
				if (courses.get(i).getUnallocated_strength() > 0 && courses.get(i).getProcessed() == false) {
					if (max < courses.get(i).getNo_Of_Students()) {
						max = courses.get(i).getNo_Of_Students();
						course = courses.get(i);
					}
				}
			}
		}

		return course;
	}

	// public Course chosingCourse() {
	// Course course = null;
	// int min = 100000;
	// for (int i = 0; i < courses.size(); i++) {
	// if (courses.get(i).getUnallocated_strength() > 0 &&
	// courses.get(i).getProcessed() == false) {
	// if (min > courses.get(i).getNo_Of_Students()) {
	// min = courses.get(i).getNo_Of_Students();
	// course = courses.get(i);
	// }
	// }
	// }
	// return course;
	// }
	// a particular course from this slot has been successfully processed. It
	// means all the students from
	// this course are assigned a room.
	public void updateProcessCount() {
		processCount++;
	}

	// this method checks if a course is still under process or is it finished
	public boolean slotProcessed() {
		if ((processCount == courses.size()))
			return false;
		else
			return true;
	}

	public String toString() {
		return slot_no + "";
	}
}
