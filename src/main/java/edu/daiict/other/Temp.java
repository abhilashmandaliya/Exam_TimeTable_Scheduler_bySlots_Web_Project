package edu.daiict.other;

public class Temp {
	
	void ma() {
		System.out.println(getClass().getClassLoader().getResourceAsStream(FileConfig.OUTPUT_FILES_PATH + ""));
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String hashed = BCrypt.hashpw("admin123", BCrypt.gensalt());
		System.out.println();
		new Temp().ma();
	}

}
