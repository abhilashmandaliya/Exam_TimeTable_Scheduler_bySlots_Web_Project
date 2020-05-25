package edu.daiict.other;

import java.util.Comparator;

public class CourseComparatorByCapacity implements Comparator<Course> {

	@Override
	public int compare(Course r1, Course r2) {
		return r1.compareTo(r2);

	}
}