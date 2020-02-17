package calendar;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

class Event implements Comparable<Event> {

	UUID eventID;
	String title;
	Calendar calendar;
	ArrayList<UUID> viewers;
	LocalDateTime startTime;
	LocalDateTime endTime;
	RepeatConfig repeatConfig;
	UUID originalEvent;
	ArrayList<UUID> repeatingEvents;

	Event(String title, UUID calendarID, ArrayList<UUID> viewers, LocalDateTime startTime,
			LocalDateTime endTime, RepeatConfig repeatConfig, UUID originalEvent) {
		this.eventID = UUID.randomUUID();
		this.title = title;
		this.calendar = CalendarManager.getCalendarManager().getCalendar(calendarID);
		this.viewers = viewers;
		this.startTime = startTime;
		this.endTime = endTime;
		this.repeatConfig = repeatConfig;
		this.originalEvent = originalEvent;
		this.repeatEvent();
	}

	void setEventTitle(String title) {
		this.title = title;
		if (this.repeatConfig.repeatable) {
			for (int i = 0; i < this.repeatingEvents.size(); ++i) {
				Event event = this.calendar.getEvent(this.repeatingEvents.get(i));
				event.setEventTitle(title);
			}
		}
	}

	void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
		if (this.repeatConfig.repeatable) {
			this.removeRepeatingEvents();
			this.repeatEvent();
		}
	}

	void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
		if (this.repeatConfig.repeatable) {
			this.removeRepeatingEvents();
			this.repeatEvent();
		}
	}

	void setRepeatable(RepeatConfig repeatConfig) {
		if (this.repeatConfig.repeatable) {
			this.removeRepeatingEvents();
		}

		this.repeatConfig = repeatConfig;

		if (this.repeatConfig.repeatable) {
			this.repeatEvent();
		} else {
			this.repeatingEvents = null;
		}
	}

	void updateTimeByTimeZone(int timeDiff) {
		this.startTime = this.startTime.plusHours(timeDiff);
		this.endTime = this.endTime.plusHours(timeDiff);
		if (this.repeatConfig.repeatable) {
			for (int i = 0; i < this.repeatingEvents.size(); ++i) {
				Event event = this.calendar.getEvent(this.repeatingEvents.get(i));
				event.updateTimeByTimeZone(timeDiff);
			}
		}
	}

	void shareEventWith(UUID userID) {
		if (!this.viewers.contains(userID)) {
			this.viewers.add(userID);
		}
		if (this.repeatConfig.repeatable) {
			for (int i = 0; i < this.repeatingEvents.size(); ++i) {
				Event event = this.calendar.getEvent(this.repeatingEvents.get(i));
				event.shareEventWith(userID);
			}
		}
	}

	private void repeatEvent() {
		if (!this.repeatConfig.repeatable) {
			this.repeatingEvents = null;
			return;
		}
		if (this.originalEvent != null) {
			throw new Error("Repeating events can't be repeated.");
		}
		if (!this.startTime.isBefore(this.repeatConfig.repeatUntil)) {
			throw new Error("Event start time must be before end repeat time.");
		}

		this.repeatingEvents = new ArrayList<UUID>();
		LocalDateTime eventStartTime = LocalDateTime.from(this.startTime);
		LocalDateTime eventEndTime = LocalDateTime.from(this.endTime);

		while (!eventStartTime.isAfter(this.repeatConfig.repeatUntil)) {
			if (this.repeatConfig.repeatFrequency == RepeatType.DAILY) {
				eventStartTime = eventStartTime.plusDays(1);
				eventEndTime = eventEndTime.plusDays(1);
			} else if (this.repeatConfig.repeatFrequency == RepeatType.WEEKLY) {
				eventStartTime = eventStartTime.plusWeeks(1);
				eventEndTime = eventEndTime.plusWeeks(1);
			} else if (this.repeatConfig.repeatFrequency == RepeatType.MONTHLY) {
				eventStartTime = eventStartTime.plusMonths(1);
				eventEndTime = eventEndTime.plusMonths(1);
			} else if (this.repeatConfig.repeatFrequency == RepeatType.QUARTERLY) {
				eventStartTime = eventStartTime.plusMonths(3);
				eventEndTime = eventEndTime.plusMonths(3);
			} else if (this.repeatConfig.repeatFrequency == RepeatType.ANNUALLY) {
				eventStartTime = eventStartTime.plusYears(1);
				eventEndTime = eventEndTime.plusYears(1);
			} else {
				throw new Error("The repeat frequency is undefined");
			}

			if (eventStartTime.isAfter(this.repeatConfig.repeatUntil)) {
				break;
			}

			RepeatConfig repeatConfig = new RepeatConfig(false, null, null);

			Event event = new Event(this.title, this.calendar.calendarID, this.copyViewers(),
					eventStartTime, eventEndTime, repeatConfig, this.eventID);
			this.calendar.addEvent(event);
			this.repeatingEvents.add(event.eventID);
		}
	}

	private void removeRepeatingEvents() {
		// make a deep copy of repeatingEvents
		ArrayList<UUID> events = new ArrayList<UUID>();
		for(int i = 0; i < this.repeatingEvents.size(); ++i) {
			events.add(this.repeatingEvents.get(i));
		}

		for (int i = 0; i < events.size(); ++i) {
			this.calendar.removeEvent(events.get(i));
		}
	}

	private ArrayList<UUID> copyViewers() {
		ArrayList<UUID> viewers = new ArrayList<UUID>();
		for(int i = 0; i < this.viewers.size(); ++i) {
			viewers.add(this.viewers.get(i));
		}
		return viewers;
	}

	@Override
	public int compareTo(Event event) {
		return this.startTime.compareTo(event.startTime);
	}
	public String toString() {
		return "Event ID: " + this.eventID + "\n"
				+ "Event Title: " + this.title + "\n"
				+ "Event Start Time: " + this.startTime + "\n"
				+ "Event End Time: " + this.endTime + "\n"
				+ "Calendar of the event: " + this.calendar.name + "\n"
				+ "Viewers of the event: " + this.viewerString() + "\n"
				+ this.repeatEventString();
	}

	private String viewerString() {
		String viewers = "";
		for (int i = 0; i < this.viewers.size(); ++i) {
			if (i > 0) {
				viewers += ", ";
			}
			UUID viewer = this.viewers.get(i);
			viewers += CalendarManager.getCalendarManager().getUser(viewer).username;
		}
		return viewers;
	}

	private String repeatEventString() {
		String repeatString = "";
		if (this.repeatConfig.repeatable) {
			repeatString += "This event repeats every ";
			if (this.repeatConfig.repeatFrequency == RepeatType.DAILY) {
				repeatString += "day ";
			} else if (this.repeatConfig.repeatFrequency == RepeatType.WEEKLY) {
				repeatString += "week ";
			} else if (this.repeatConfig.repeatFrequency == RepeatType.MONTHLY) {
				repeatString += "month ";
			} else if (this.repeatConfig.repeatFrequency == RepeatType.QUARTERLY) {
				repeatString += "quarter ";
			} else {
				repeatString += "year ";
			}
			repeatString += "until " + this.repeatConfig.repeatUntil + "\n";
		}
		return repeatString;
	}

}
