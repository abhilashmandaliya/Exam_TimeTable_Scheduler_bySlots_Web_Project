package edu.daiict.other;

public class Utility1 {

	//This is a utility class where it is just giving back certain objects after processing.
	TimeInterval[] array;
	int k;
	Course course;
	Slot slot;
	int flag_failed;
	
	public Utility1(TimeInterval[] array,int k,Course course,Slot slot,int flag_failed)
	{
		this.array=array;
		this.k=k;
		this.course=course;
		this.slot=slot;
		this.flag_failed=flag_failed;
	}
}
