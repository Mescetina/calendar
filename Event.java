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
	Boolean repeatable;
	LocalDateTime repeatUntil;
	RepeatType repeatFrequency;
	UUID originalEvent;
	ArrayList<UUID> repeatingEvents;

	Event(String title, Calendar calendar, ArrayList<UUID> viewers, LocalDateTime startTime, LocalDateTime endTime,
			Boolean repeatable, LocalDateTime repeatUntil, RepeatType frequency, UUID originalEvent) {
		this.eventID = UUID.randomUUID();
		this.title = title;
		this.calendar = calendar;
		this.viewers = viewers;
		this.startTime = startTime;
		this.endTime = endTime;
		this.repeatable = repeatable;
		this.repeatUntil = repeatUntil;
		this.repeatFrequency = frequency;
		this.originalEvent = originalEvent;
		this.repeatEvent();
	}

	void setEventTitle(String title) {
		this.title = title;
		if (this.repeatable) {
			for (int i = 0; i < this.repeatingEvents.size(); ++i) {
				Event event = this.calendar.getEvent(this.repeatingEvents.get(i));
				event.setEventTitle(title);
			}
		}
	}

	void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
		if (this.repeatable) {
			// make a deep copy of repeatingEvents
			ArrayList<UUID> events = new ArrayList<UUID>();
			for(int i = 0; i < this.repeatingEvents.size(); ++i) {
				events.add(this.repeatingEvents.get(i));
			}

			for (int i = 0; i < events.size(); ++i) {
				this.calendar.removeEvent(events.get(i));
			}
			this.repeatEvent();
		}
	}

	void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
		if (this.repeatable) {
			// make a deep copy of repeatingEvents
			ArrayList<UUID> events = new ArrayList<UUID>();
			for(int i = 0; i < this.repeatingEvents.size(); ++i) {
				events.add(this.repeatingEvents.get(i));
			}

			for (int i = 0; i < events.size(); ++i) {
				this.calendar.removeEvent(events.get(i));
			}
			this.repeatEvent();
		}
	}

	void setRepeatable(Boolean repeatable, LocalDateTime repeatUntil, RepeatType frequency) {
		if (this.repeatable) {
			// make a deep copy of repeatingEvents
			ArrayList<UUID> events = new ArrayList<UUID>();
			for(int i = 0; i < this.repeatingEvents.size(); ++i) {
				events.add(this.repeatingEvents.get(i));
			}

			for (int i = 0; i < events.size(); ++i) {
				this.calendar.removeEvent(events.get(i));
			}
		}

		this.repeatable = repeatable;
		this.repeatUntil = repeatUntil;
		this.repeatFrequency = frequency;

		if (this.repeatable) {
			this.repeatEvent();
		} else {
			this.repeatingEvents = null;
		}
	}

	void updateTimeByTimeZone(int timeDiff) {
		this.startTime = this.startTime.plusHours(timeDiff);
		this.endTime = this.endTime.plusHours(timeDiff);
		if (this.repeatable) {
			for (int i = 0; i < this.repeatingEvents.size(); ++i) {
				Event event = this.calendar.getEvent(this.repeatingEvents.get(i));
				event.updateTimeByTimeZone(timeDiff);
			}
		}
	}

	void shareEventWith(UUID userID) {
		this.viewers.add(userID);
		if (this.repeatable) {
			for (int i = 0; i < this.repeatingEvents.size(); ++i) {
				Event event = this.calendar.getEvent(this.repeatingEvents.get(i));
				event.shareEventWith(userID);
			}
		}
	}

	void repeatEvent() {
		if (!this.repeatable) {
			this.repeatingEvents = null;
			return;
		}
		if (this.originalEvent != null) {
			throw new Error("Repeating events can't be repeated.");
		}
		if (!this.startTime.isBefore(this.repeatUntil)) {
			throw new Error("Event start time must be before end repeat time.");
		}

		this.repeatingEvents = new ArrayList<UUID>();
		LocalDateTime eventStartTime = LocalDateTime.from(this.startTime);
		LocalDateTime eventEndTime = LocalDateTime.from(this.endTime);

		while (!eventStartTime.isAfter(this.repeatUntil)) {
			if (this.repeatFrequency == RepeatType.DAILY) {
				eventStartTime = eventStartTime.plusDays(1);
				eventEndTime = eventEndTime.plusDays(1);
			} else if (this.repeatFrequency == RepeatType.WEEKLY) {
				eventStartTime = eventStartTime.plusWeeks(1);
				eventEndTime = eventEndTime.plusWeeks(1);
			} else if (this.repeatFrequency == RepeatType.MONTHLY) {
				eventStartTime = eventStartTime.plusMonths(1);
				eventEndTime = eventEndTime.plusMonths(1);
			} else if (this.repeatFrequency == RepeatType.QUARTERLY) {
				eventStartTime = eventStartTime.plusMonths(3);
				eventEndTime = eventEndTime.plusMonths(3);
			} else if (this.repeatFrequency == RepeatType.ANNUALLY) {
				eventStartTime = eventStartTime.plusYears(1);
				eventEndTime = eventEndTime.plusYears(1);
			} else {
				throw new Error("The repeat frequency is undefined");
			}

			if (eventStartTime.isAfter(this.repeatUntil)) {
				break;
			}

			Event event = new Event(this.title, this.calendar, this.viewers,
					eventStartTime, eventEndTime, false, null, null, this.eventID);
			this.calendar.addEvent(event);
			this.repeatingEvents.add(event.eventID);
		}
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
		if (this.repeatable) {
			repeatString += "This event repeats every ";
			if (this.repeatFrequency == RepeatType.DAILY) {
				repeatString += "day ";
			} else if (this.repeatFrequency == RepeatType.WEEKLY) {
				repeatString += "week ";
			} else if (this.repeatFrequency == RepeatType.MONTHLY) {
				repeatString += "month ";
			} else if (this.repeatFrequency == RepeatType.QUARTERLY) {
				repeatString += "quarter ";
			} else {
				repeatString += "year ";
			}
			repeatString += "until " + this.repeatUntil + "\n";
		}
		return repeatString;
	}

}
