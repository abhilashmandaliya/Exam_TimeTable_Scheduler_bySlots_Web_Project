package org;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// This is a general class to deal with database where objects are not required to manipulate database.
public class GeneralDAO {

	// fetch rooms from database
	private static Connection con;

	public static void makeConnection() throws ClassNotFoundException, SQLException {
		con = DBConnection.getInstance().getConnectionSchema("public");
	}

	public static boolean validateUser(String uname, String password) throws ClassNotFoundException, SQLException {
		if (con == null)
			makeConnection();
		String sql = "SELECT PASSWORD FROM USERS WHERE UNAME='" + uname + "'";
		ResultSet rs = con.createStatement().executeQuery(sql);
		if (rs.next()) {
			if (BCrypt.checkpw(password, rs.getString(1)))
				return true;
		}
		return false;
	}

	public static boolean registerUser(String uname, String password) throws ClassNotFoundException, SQLException {
		int cnt = 0;
		if (con == null)
			makeConnection();
		// check whether user is already registered
		String sql = "SELECT UNAME FROM USERS WHERE UNAME='" + uname + "'";
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(sql);
		if (rs.next())
			return false;
		password = BCrypt.hashpw(password, BCrypt.gensalt());
		sql = "INSERT INTO USERS VALUES ( NEXTVAL('user_sequence'),'" + uname + "','" + password + "')";
		cnt = st.executeUpdate(sql);
		return cnt > 0;
	}

	public static ArrayList<Room> getRooms() throws DAOException, ClassNotFoundException, SQLException {
		if (con == null)
			makeConnection();
		ArrayList<Room> rooms = new ArrayList<>();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("Select * from Room order by room_no");

			while (rs.next()) {
				int room_no = rs.getInt("room_no");
				int room_capacity = rs.getInt("room_capacity");
				rooms.add(new Room(room_no, room_capacity));
			}

			Collections.sort(rooms, new RoomComparatorByCapacity());

		} catch (SQLException e) {
			throw new DAOException(e.getMessage());
		}
		return rooms;
	}

	// add new room to database
	public static void addRoom(int room_no, int room_capacity) throws DAOException, ClassNotFoundException {
		try {
			if (con == null)
				makeConnection();
			String sql = "Insert into Room VALUES(" + room_no + "," + room_capacity + ")";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage());
		}

	}

	// delete a room from database
	public static void deleteRoom(int room_no) throws DAOException, ClassNotFoundException {
		try {
			if (con == null)
				makeConnection();
			String sql = "Delete from Room where room_no=" + room_no;
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage());
		}

	}

	// update capacity of the room
	public static void updateRoom(int room_no, int capacity) throws DAOException, ClassNotFoundException {
		try {
			if (con == null)
				makeConnection();
			String sql = "UPDATE Room SET room_capacity=" + capacity + " WHERE room_no=" + room_no;
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage());
		}

	}

	// fetch courses from database
	public static ArrayList<Course> getCourses() throws DAOException, ClassNotFoundException, SQLException {
		if (con == null)
			makeConnection();
		ArrayList<Course> courses = new ArrayList<>();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("Select * from Course order by course_name");

			while (rs.next()) {
				courses.add(
						new Course(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getString(5)));
			}

		} catch (SQLException e) {
			throw new DAOException(e.getMessage());
		}
		return courses;
	}

	// fetch courses from database slot wise
	public static ArrayList<Course> getCourses(int slot_no) throws DAOException, ClassNotFoundException, SQLException {
		if (con == null)
			makeConnection();
		ArrayList<Course> courses = new ArrayList<>();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(
					"Select C.course_id,C.course_name,C.batch,C.no_of_students,C.faculty from Course C,Slot S where S.course_id=C.course_id AND S.slot_no="
							+ slot_no);

			while (rs.next()) {
				courses.add(
						new Course(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getString(5)));
			}

		} catch (SQLException e) {
			throw new DAOException(e.getMessage());
		}
		return courses;
	}

	// add a new course to database
	public static void addCourse(String course_id, String course_name, String batch, int no_of_students, String faculty)
			throws DAOException, ClassNotFoundException {
		try {
			if (con == null)
				makeConnection();
			String sql = "Insert into Course VALUES('" + course_id + "','" + course_name + "','" + batch + "',"
					+ no_of_students + ",'" + faculty + "')";

			Statement stmt = con.createStatement();

			stmt.execute(sql);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage());
		}

	}

	// delete a course from database
	public static void deleteCourse(String course_id) throws DAOException, ClassNotFoundException {
		try {
			if (con == null)
				makeConnection();
			String sql = "Delete from Course where course_id='" + course_id + "'";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage());
		}

	}

	// modify a course
	public static void updateCourse(String course_id, String course_name, String batch, int no_of_students,
			String faculty) throws DAOException, ClassNotFoundException {
		try {
			if (con == null)
				makeConnection();
			String sql = "UPDATE course SET course_name='" + course_name + "',batch='" + batch + "',no_of_students="
					+ no_of_students + ",faculty='" + faculty + "'WHERE course_id='" + course_id + "'";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage());
		}

	}

	// delete all the courses from database.it clears the database as slot table
	// also get deleted
	public static void deleteAllCourses() throws DAOException, ClassNotFoundException {
		try {
			if (con == null)
				makeConnection();
			String sql = "delete from course";
			Statement stmt = con.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage());
		}

	}

	public static void addSlotEntry(int slot_no, String course_id) throws DAOException, ClassNotFoundException {
		try {
			if (con == null)
				makeConnection();
			String sql = "Insert into slot(slot_no,course_id) VALUES(" + slot_no + ",'" + course_id + "')";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage());
		}

	}

	public static Connection getCon() {
		return con;
	}

	public static Map<Integer, String> getBatchProgram() throws DAOException, ClassNotFoundException, SQLException {
		if (con == null)
			makeConnection();
		Map<Integer, String> batch_program = new HashMap<>();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("Select * from batch_program");

			while (rs.next()) {
				int batch = Integer.parseInt(rs.getString("batch"));
				String program = rs.getString("program");
				batch_program.put(batch, program);
			}

		} catch (SQLException e) {
			throw new DAOException(e.getMessage());
		}
		return batch_program;
	}

	// get codes which map to programs
	public static Map<Integer, String> getBatch_Program() throws DAOException, ClassNotFoundException, SQLException {
		if (con == null)
			makeConnection();
		Map<Integer, String> batch_program = new HashMap<>();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("Select * from batch_program order by batch");

			while (rs.next()) {
				int batch = rs.getInt("batch");
				String program = rs.getString("program");
				batch_program.put(batch, program);
			}

		} catch (SQLException e) {
			throw new DAOException(e.getMessage());
		}
		return batch_program;
	}

	// add new batch program code to database
	public static void addBatch_Program(Integer batch, String program) throws DAOException, ClassNotFoundException {
		try {
			if (con == null)
				makeConnection();
			String sql = "Insert into batch_program VALUES(" + batch + ",'" + program + "')";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage());
		}

	}

	// delete a batch_program from database
	public static void deleteBatch_Program(Integer batch) throws DAOException, ClassNotFoundException {
		try {
			if (con == null)
				makeConnection();
			String sql = "Delete from batch_program where batch=" + batch;
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage());
		}

	}

	// update batch_program
	public static void updateBatch_Program(int batch, String program) throws DAOException, ClassNotFoundException {
		try {
			if (con == null)
				makeConnection();
			String sql = "UPDATE batch_program SET program='" + program + "' WHERE batch=" + batch;
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage());
		}

	}

}
