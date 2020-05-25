package edu.daiict.other;

import java.util.Comparator;

public class RoomComparatorByCapacity implements Comparator<Room> {

	@Override
	public int compare(Room r1, Room r2) {
		return r1.compareTo(r2);

	}
}