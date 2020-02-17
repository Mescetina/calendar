package calendar;

import java.util.ArrayList;
import java.util.UUID;

class Calendar {

	UUID calendarID;
	String name;
	UUID owner;
	ArrayList<Event> eventList;
	Boolean isPublic;
	Boolean isVisible;

	Calendar(String name, UUID owner) {
		this.calendarID = UUID.randomUUID();
		this.name = name;
		this.owner = owner;
		this.isPublic = false;
		this.isVisible = true;
		this.eventList = new ArrayList<Event>();
	}

	void setCalendarName(String name) {
		this.name = name;
	}

	void setAccessibility(Boolean isPublic) {
		this.isPublic = isPublic;
	}

	void setVisibility(Boolean isVisible) {
		this.isVisible = isVisible;
	}

	void addEvent(Event event) {
		this.eventList.add(event);
	}

	void removeEvent(UUID eventID) {
		Event removedEvent = this.getEvent(eventID);
		this.eventList.remove(removedEvent);
		if (removedEvent.originalEvent != null) {
			this.getEvent(removedEvent.originalEvent).repeatingEvents.remove(removedEvent.eventID);
		}
		if (removedEvent.repeatConfig.repeatable) {
			for (int i = 0; i < removedEvent.repeatingEvents.size(); ++i) {
				Event event = this.getEvent(removedEvent.repeatingEvents.get(i));
				this.eventList.remove(event);
			}
		}
	}

	Event getEvent(UUID eventID) {
		for (int i = 0; i < this.eventList.size(); ++i) {
			if (this.eventList.get(i).eventID.equals(eventID)) {
				return this.eventList.get(i);
			}
		}
		throw new Error("The event is not found");
	}

	public String toString() {
		return (this.isPublic ? "Public" : "Private") + " calendar" + "\n"
				+ "Calendar ID: " + this.calendarID + "\n"
				+ "Calendar Name: " + this.name + "\n"
				+ "Calendar Creator: " + CalendarManager.getCalendarManager().getUser(owner).username + "\n"
				+ "Calendar Visibility: " + (this.isVisible ? "visible" : "hidden");
	}

}
