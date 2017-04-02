package org;

import java.util.Comparator;

public class RoomComparatorByCapacity implements Comparator<Room> {

	public int compare(Room r1,Room r2)
	{
		return r1.compareTo(r2);
	}
}
