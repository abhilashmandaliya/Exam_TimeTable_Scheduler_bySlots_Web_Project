package org;

public class Temp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String hashed = BCrypt.hashpw("admin123", BCrypt.gensalt());
		System.out.println(hashed);
	}

}
