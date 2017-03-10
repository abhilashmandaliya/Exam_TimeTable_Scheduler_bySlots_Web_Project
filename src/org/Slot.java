package org;

import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class Slot implements Cloneable {
	private final Connection con;

	ArrayList<Course> courses;
	private int slot_no;

	public static void main(String[] args) throws Exception {
		Slot s = new Slot(1);
	}

	public ArrayList<Course> getC() {
		return courses;
	}

	public ArrayList<Course> getCourses() throws DAOException, ClassNotFoundException {
		courses = new ArrayList<>();
		Course course = null;
		try {

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("Select C.course_id,"
					+ "course_name,batch,no_of_students From course C,slot S where C.course_id=S.course_id "
					+ "AND S.slot_no=" + slot_no);
			while (rs.next()) {

				String course_id = rs.getString("course_id");
				String course_name = rs.getString("course_name");
				int no_of_students = rs.getInt("no_of_students");

				String batch = rs.getString("batch");

				courses.add(new Course(course_id, course_name, batch, no_of_students));
			}

		} catch (SQLException e) {
			throw new DAOException(e.getMessage());
		}

		return courses;

	}

	public Slot(int num) throws ClassNotFoundException, SQLException, DAOException {
		this.slot_no = num;
		System.out.println("constructiotr");
		con = DBConnection.getInstance().getConnectionSchema("public");
		getCourses();
		// slot1
		// courses.add(new Course("EL114","Digital Logic Design",311));
		// courses.add(new Course("IT478","Internet of Things",60));
		// courses.add(new Course("EL426","Digital System Architecture",42));
		// courses.add(new Course("IT483","Computational Electromagnetics",1));
		// courses.add(new Course("IT543","Advanced Logic for Computer
		// Science",15));
		// courses.add(new Course("CT513","Detection and Estimation",6));
		// courses.add(new Course("EL213","Analog Circuits",302));
		// courses.add(new Course("IT618","Enterprize Computing",112));
		// slot2
		// courses.add(new Course("SC209","Environmental Studies",297));
		// courses.add(new Course("SC465","Analysis of MultiDisciplinary
		// problems",14));
		// courses.add(new Course("EL213","Digital Image processing",14));
		// courses.add(new Course("PC725","Introduction to narratology",12));
		// slot3
		// courses.add(new Course("IT543","Discrete Mathematics",310));
		// courses.add(new Course("CT513","Software Engineering",277));
		// courses.add(new Course("SC215","Probability and Statistics",296));
		// courses.add(new Course("IT664","Remote Sensing and GIS",4));
		// slot4
		// courses.add(new Course("IT543","Introduction to Computational
		// physics",56));
		// courses.add(new Course("CT513","Models of computation",136));
		// courses.add(new Course("EL213","Introduction to Cryptography",112));
		// courses.add(new Course("IT618","Natural computing",38));
		// courses.add(new Course("IT543","Analog CMOS IC Design",13));
		// courses.add(new Course("CT513","Advanced Digital Communication",7));
		// courses.add(new Course("EL213","System approach to sustainable
		// development",4));

	}

	public Connection getCon() {
		return con;
	}

	public void addCourse(String course_id) throws DAOException {
		try {
			String sql = "Insert into Slot (slot_no,course_id) VALUES(" + this.slot_no + ",'" + course_id + "')";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);

		} catch (SQLException e) {
			throw new DAOException(e.getMessage());
		}

	}

	public void deleteCourse(String course_id) throws DAOException {
		try {
			System.out.println("cid : " + course_id);
			String sql = "Delete from slot where slot_no=" + this.slot_no + " and course_id='" + course_id + "'";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// throw new DAOException(e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		Object cloned = super.clone();

		return cloned;
	}

	public Slot(Slot other) throws ClassNotFoundException, SQLException {
		con = DBConnection.getInstance().getConnectionSchema("public");
		this.slot_no = other.slot_no;
		this.processCount = other.processCount;
		for (Course course : other.courses) {
			this.courses.add(new Course(course));
		}
	}

	public Course chosingCourse() {
		Course course = null;
		int max = 0;
		for (int i = 0; i < courses.size(); i++) {
			if (courses.get(i).getUnallocatedStrength() > 0 && courses.get(i).processed == false) {
				if (max < courses.get(i).getTotalStudents()) {
					max = courses.get(i).getTotalStudents();
					course = courses.get(i);
				}
			}
		}
		return course;
	}

	int processCount = 0;

	public void updateProcessCount() {
		processCount++;
	}

	public boolean slotProcessed() {
		if ((processCount == courses.size()))
			return false;
		else
			return true;

	}
}
