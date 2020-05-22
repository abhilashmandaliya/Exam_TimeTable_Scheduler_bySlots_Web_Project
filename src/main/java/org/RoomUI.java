package org;

public class RoomUI {
	public Room room;
	public boolean is_active;
	public int priority;

	public RoomUI(Room room, boolean is_active, int priority) {
		this.room = room;
		this.is_active = is_active;
		this.priority = priority;
	}
}
