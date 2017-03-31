package org;

public class StoreTT {

	private int slot;
	private TimeInterval t1;
	private TimeInterval t2;
	
	public StoreTT(int slot,TimeInterval t1,TimeInterval t2)
	{
		this.slot=slot;
		this.t1=t1;
		this.t2=t2;
	}
	
	public int getSlot() {
		return slot;
	}
	public TimeInterval getT1() {
		return t1;
	}
	public TimeInterval getT2() {
		return t2;
	}
}
