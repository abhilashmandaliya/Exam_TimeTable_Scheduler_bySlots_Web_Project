package org;

import java.util.Comparator;

public class CourseComparatorByCapacity implements Comparator<Course> {

	public int compare(Course r1,Course r2)
	{
		return r1.compareTo(r2);
		
	}
}