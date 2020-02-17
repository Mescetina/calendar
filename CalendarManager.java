package calendar;

import java.util.ArrayList;
import java.util.UUID;

class CalendarManager {

	UUID currentUser;
	CalendarTheme theme;
	int timezone;

	ArrayList<Calendar> calendarList;
	ArrayList<CountdownTimer> timerList;
	ArrayList<User> userList;

	private static CalendarManager calendarManager;

	private CalendarManager(int timezone) {
		this.calendarList = new ArrayList<Calendar>();
		this.timerList = new ArrayList<CountdownTimer>();
		this.userList = new ArrayList<User>();
		this.currentUser = null;
		this.theme = CalendarTheme.LIGHT;
		this.timezone = timezone;
	}

	public static CalendarManager getCalendarManager() {
		if (calendarManager == null) {
			throw new Error("You must create a calendar manager with an appropriate time zone first");
		}
		return calendarManager;
	}

	public static CalendarManager getCalendarManager(int timezone) {
		if (calendarManager == null) {
			calendarManager = new CalendarManager(timezone);
		}
		return calendarManager;
	}

	void setTheme(CalendarTheme theme) {
		this.theme = theme;
	}

	String getTheme() {
		return this.theme == CalendarTheme.LIGHT ? "Light Theme" : "Dark Theme";
	}

	void setTimeZone(int timezone) {
		int timeDiff = timezone - this.timezone;
		this.timezone = timezone;
		for (int i = 0; i < calendarList.size(); ++i) {
			ArrayList<Event> events = calendarList.get(i).eventList;
			for (int j = 0; j < events.size(); ++j) {
				Event event = events.get(j);
				event.updateTimeByTimeZone(timeDiff);
			}
		}
	}

	void login(String username) {
		for (int i = 0; i < userList.size(); ++i) {
			User user = userList.get(i);
			if (user.username.equals(username)) {
				this.currentUser = user.userID;
				return;
			}
		}
		User newUser = new User(username);
		this.addUser(newUser);
		this.currentUser = newUser.userID;
	}

	void addUser(User user) {
		userList.add(user);
	}

	User getUser(UUID userID) {
		for (int i = 0; i < this.userList.size(); ++i) {
			User user = this.userList.get(i);
			if (user.userID.equals(userID)) {
				return user;
			}
		}
		throw new Error("The user is not found");
	}

	void addCalendar(Calendar calendar) {
		this.calendarList.add(calendar);
	}

	void removeCalendar(UUID calendarID) {
		Calendar removedCalendar = this.getCalendar(calendarID);
		this.calendarList.remove(removedCalendar);
	}

	Calendar getCalendar(UUID calendarID) {
		for (int i = 0; i < this.calendarList.size(); ++i) {
			Calendar calendar = this.calendarList.get(i);
			if (calendar.calendarID.equals(calendarID)) {
				return calendar;
			}
		}
		throw new Error("The calendar is not found");
	}

	ArrayList<Calendar> getCalendars(UUID userID, Boolean includePublic) {
		ArrayList<Calendar> calendars = new ArrayList<Calendar>();
		for (int i = 0; i < this.calendarList.size(); ++i) {
			Calendar calendar = this.calendarList.get(i);
			if (calendar.isPublic && includePublic || calendar.owner.equals(userID)) {
				calendars.add(calendar);
			}
		}
		return calendars;
	}

	void addEvent(Event event, UUID calendarID) {
		Calendar calendar = this.getCalendar(calendarID);
		calendar.addEvent(event);
	}

	void removeEvent(UUID eventID) {
		Event removedEvent = this.getEvent(eventID);
		removedEvent.calendar.removeEvent(eventID);
	}

	Event getEvent(UUID eventID) {
		for (int i = 0; i < this.calendarList.size(); ++i) {
			ArrayList<Event> events = this.calendarList.get(i).eventList;
			for (int j = 0; j < events.size(); ++j) {
				Event event = events.get(j);
				if (event.eventID.equals(eventID)) {
					return event;
				}
			}
		}
		throw new Error("The event is not found");
	}

	ArrayList<Event> getEvents(UUID userID, Boolean includePublic) {
		ArrayList<Event> events = new ArrayList<Event>();
		for (int i = 0; i < this.calendarList.size(); ++i) {
			Calendar calendar = this.calendarList.get(i);
			if (calendar.isVisible) {
				for (int j = 0; j < calendar.eventList.size(); ++j) {
					Event event = calendar.eventList.get(j);
					if (includePublic && (calendar.isPublic || event.viewers.contains(userID))
							|| calendar.owner.equals(userID)) {
						events.add(event);
					}
				}
			}
		}
		return events;
	}

	ArrayList<Event> searchEvent(String query) {
		ArrayList<Event> results = new ArrayList<Event>();
		ArrayList<Event> events = this.getEvents(this.currentUser, true);
		for (int i = 0; i < events.size(); ++i) {
			Event event = events.get(i);
			if (event.title.contains(query)) {
				results.add(event);
			}
		}
		return results;
	}

	void addTimer(CountdownTimer timer) {
		this.timerList.add(timer);
	}

	void removeTimer(UUID timerID) {
		CountdownTimer removedTimer = this.getTimer(timerID);
		this.timerList.remove(removedTimer);
	}

	CountdownTimer getTimer(UUID timerID) {
		for (int i = 0; i < this.timerList.size(); ++i) {
			CountdownTimer timer = this.timerList.get(i);
			if (timer.timerID.equals(timerID)) {
				return timer;
			}
		}
		throw new Error("The timer is not found");
	}

	ArrayList<CountdownTimer> getTimers(UUID userID) {
		ArrayList<CountdownTimer> timers = new ArrayList<CountdownTimer>();
		for (int i = 0; i < this.timerList.size(); ++i) {
			CountdownTimer timer = this.timerList.get(i);
			if (timer.owner.equals(userID)) {
				timers.add(timer);
			}
		}
		return timers;
	}

}
