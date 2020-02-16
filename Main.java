package calendar;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;

public class Main {

	public static Scanner scanner;
	public static CalendarManager calendarManager;

	public static void main(String[] args) {
		int timezone = OffsetDateTime.now().getOffset().getTotalSeconds();
		calendarManager = CalendarManager.getCalendarManager(timezone / 60 / 60);

		scanner = new Scanner(System.in);
		int status = 1;
		while (status > 0) {
			welcome();
			login();
			status = operate();			
		}
		scanner.close();
	}

	static void welcome() {
		System.out.println("*-*-*-*-*-*-*-* Welcome to Calendars *-*-*-*-*-*-*-*\n");
	}

	static void login() {
		System.out.print("Please login with your username to continue: ");
		String username = scanner.nextLine();
		calendarManager.login(username);
		System.out.println();
		System.out.println("Welcome, " + username + "!\n");
	}

	static int operate() {
		while (true) {
			System.out.println("*-*-*-* Menu " + "(" + calendarManager.getTheme() + ")" + " *-*-*-*\n\n"
					+ "[1] View calendars\n"
					+ "[2] Add a calendar\n"
					+ "[3] Delete a calendar\n"
					+ "[4] Update a calendar\n"
					+ "[5] View events\n"
					+ "[6] Add an event\n"
					+ "[7] Delete an event\n"
					+ "[8] Update an event\n"
					+ "[9] Share an event\n"
					+ "[10] Search events\n"
					+ "[11] Change theme (light / dark)\n"
					+ "[12] View time zone\n"
					+ "[13] Change time zone\n"
					+ "[14] View timers\n"
					+ "[15] Add a timer\n"
					+ "[16] Delete a timer\n"
					+ "[17] Logout\n"
					+ "[18] Exit");
			System.out.print("Please select an option to continue (enter the number): ");
			int operation = Integer.parseInt(scanner.nextLine());
			System.out.println();
			if (operation == 17) {
				System.out.println("Logging out...\n");
				return 1;
			} else if (operation == 18) {
				System.out.println("System exiting...");
				return 0;
			} else {
				parseOperation(operation);
			}
		}
	}

	static void parseOperation(int operation) {
		if (operation == 1) {
			printCalendars(true);
		} else if (operation == 2) {
			addCalendar();
		} else if (operation == 3) {
			deleteCalendar();
		} else if (operation == 4) {
			updateCalendar();
		} else if (operation == 5) {
			printEvents(true);
		} else if (operation == 6) {
			addEvent();
		} else if (operation == 7) {
			deleteEvent();
		} else if (operation == 8) {
			updateEvent();
		} else if (operation == 9) {
			shareEvent();
		} else if (operation == 10) {
			searchEvents();
		} else if (operation == 11) {
			changeTheme();
		} else if (operation == 12) {
			viewTimeZone();
		} else if (operation == 13) {
			changeTimeZone();
		} else if (operation == 14) {
			printTimers();
		} else if (operation == 15) {
			addTimer();
		} else if (operation == 16) {
			deleteTimer();
		} else {
			System.out.println("Operation is invalid");
		}
	}

	static void addCalendar() {
		System.out.print("Calendar name: ");
		String calendarName = scanner.nextLine();
		Calendar calendar = new Calendar(calendarName, calendarManager.currentUser);
		calendarManager.addCalendar(calendar);
		System.out.println("Calendar is added!\n");
	}

	static void deleteCalendar() {
		if (calendarManager.getCalendars(calendarManager.currentUser, false).size() <= 0) {
			System.out.println("There is no calendar to delete!\n");
			return;
		}

		printCalendars(false);
		// very user unfriendly, will improve later
		System.out.print("Please select a calendar to delete (enter calendar ID): ");
		UUID calendarID = UUID.fromString(scanner.nextLine());
		calendarManager.removeCalendar(calendarID);
		System.out.println("Calendar is deleted!\n");
	}

	static void updateCalendar() {
		if (calendarManager.getCalendars(calendarManager.currentUser, false).size() <= 0) {
			System.out.println("There is no calendar in the system.\n");
			return;
		}

		printCalendars(false);

		// very user unfriendly, will improve later
		System.out.print("Please select a calendar to update (enter calendar ID): ");
		UUID calendarID = UUID.fromString(scanner.nextLine());
		Calendar calendar = calendarManager.getCalendar(calendarID);
		System.out.println();

		System.out.println("*-*-*-* Calendar Settings *-*-*-*\n\n"
				+ "[1] Change calendar name\n"
				+ "[2] Change calendar's accessibility (public / private)\n"
				+ "[3] Change calendar's visibility");
		System.out.print("Please select an option to continue (enter the number): ");
		int operation = Integer.parseInt(scanner.nextLine());
		System.out.println();

		if (operation == 1) {
			System.out.print("Enter the new name: ");
			String name = scanner.nextLine();
			calendar.setCalendarName(name);
			System.out.println("Calendar's name is changed to " + calendar.name);
		} else if (operation == 2) {
			calendar.setAccessibility(!calendar.isPublic);
			System.out.println("Calendar is now " + (calendar.isPublic ? "public" : "private"));
		} else if (operation == 3) {
			calendar.setVisibility(!calendar.isVisible);
			System.out.println("Calendar is now " + (calendar.isVisible ? "visible" : "hidden"));
		} else {
			System.out.println("Operation is invalid");
		}
		System.out.println();
	}

	static void printCalendars(Boolean includePublic) {
		ArrayList<Calendar> calendars = calendarManager.getCalendars(calendarManager.currentUser, includePublic);
		if (calendars.size() <= 0) {
			System.out.println("There is no calendar in the system.\n");
			return;
		}

		System.out.println("*-*-*-* Calendar List *-*-*-*\n");
		for (int i = 0; i < calendars.size(); ++i) {
			Calendar calendar = calendars.get(i);
			System.out.println(calendar + "\n");
		}
	}

	static void addEvent() {
		if (calendarManager.getCalendars(calendarManager.currentUser, false).size() <= 0) {
			System.out.println("There is no calendar in the system.\n");
			return;
		}

		System.out.print("Event title: ");
		String eventTitle = scanner.nextLine();
		System.out.println();

		System.out.print("Start time [i.e. 2017-12-03T10:15:30]: ");
		LocalDateTime startTime = LocalDateTime.parse(scanner.nextLine());
		System.out.println();

		System.out.print("End time [i.e. 2017-12-03T11:15:30]: ");
		LocalDateTime endTime = LocalDateTime.parse(scanner.nextLine());
		System.out.println();

		System.out.print("Is this event repeatable (Y/N): ");
		Boolean repeatable = scanner.nextLine().equals("Y") ? true : false;
		System.out.println();

		LocalDateTime repeatUntil = null;
		RepeatType frequency = null;

		if (repeatable) {
			System.out.print("Repeat until [i.e. 2018-12-03T10:15:30]: ");
			repeatUntil = LocalDateTime.parse(scanner.nextLine());
			System.out.println();

			System.out.println("Repeat frequency:\n\n"
					+ "[1] Daily\n"
					+ "[2] Weekly\n"
					+ "[3] Monthly\n"
					+ "[4] Quarterly\n"
					+ "[5] Annually\n");
			System.out.print("Please select a repeat frequency (enter the number): ");
			int freq = Integer.parseInt(scanner.nextLine());
			if (freq == 1) {
				frequency = RepeatType.DAILY;
			} else if (freq == 2) {
				frequency = RepeatType.WEEKLY;
			} else if (freq == 3) {
				frequency = RepeatType.MONTHLY;
			} else if (freq == 4) {
				frequency = RepeatType.QUARTERLY;
			} else {
				frequency = RepeatType.ANNUALLY;
			}
			System.out.println();
		}

		printCalendars(false);
		System.out.print("Add to which calendar (enter calendar ID): ");
		UUID calendarID = UUID.fromString(scanner.nextLine());
		Calendar calendar = calendarManager.getCalendar(calendarID);
		System.out.println();

		System.out.print("Do you want to share this event (Y/N): ");
		String ans = scanner.nextLine();
		System.out.println();

		ArrayList<UUID> viewers = new ArrayList<UUID>();
		viewers.add(calendarManager.currentUser);
		if (ans.equals("Y") && calendarManager.userList.size() <= 1) {
			printUsers();
		} else if (ans.equals("Y")) {
			printUsers();
			while (true) {
				System.out.print("Share event with which user (enter user ID or N when you're done): ");
				String viewer = scanner.nextLine();
				if (viewer.equals("N")) {
					break;
				}
				viewers.add(UUID.fromString(viewer));
			}
			System.out.println();
		}

		Event event = new Event(eventTitle, calendar, viewers, startTime, endTime,
				repeatable, repeatUntil, frequency, null);
		calendar.addEvent(event);

		System.out.println("Event is added!\n");
	}

	static void deleteEvent() {
		if (calendarManager.getEvents(calendarManager.currentUser, false).size() <= 0) {
			System.out.println("There is no event to delete!\n");
			return;
		}

		printEvents(false);
		System.out.print("Please select an event to delete (enter event ID): ");
		UUID eventID = UUID.fromString(scanner.nextLine());
		calendarManager.getEvent(eventID).calendar.removeEvent(eventID);
		System.out.println("Event is deleted!\n");
	}

	static void updateEvent() {
		if (calendarManager.getEvents(calendarManager.currentUser, false).size() <= 0) {
			System.out.println("There is no event in the system.\n");
			return;
		}

		printEvents(false);

		// very user unfriendly, will improve later
		System.out.print("Please select an event to update (enter event ID): ");
		UUID eventID = UUID.fromString(scanner.nextLine());
		Event event = calendarManager.getEvent(eventID);
		System.out.println();

		System.out.println("*-*-*-* Event Settings *-*-*-*\n\n"
				+ "[1] Change event title\n"
				+ "[2] Change event start time\n"
				+ "[3] Change event end time\n"
				+ "[4] Change settings related to repeating event\n"
				+ "    (change repeat end time, change repeat frequency)");
		System.out.print("Please select an option to continue (enter the number): ");
		int operation = Integer.parseInt(scanner.nextLine());
		System.out.println();

		if (operation == 1) {
			System.out.print("Enter the new title: ");
			String title = scanner.nextLine();
			event.setEventTitle(title);
			System.out.println("Event's name is changed to " + event.title);
		} else if (operation == 2) {
			System.out.print("Enter the new start time [i.e. 2019-12-01T14:00:00]: ");
			LocalDateTime startTime = LocalDateTime.parse(scanner.nextLine());
			event.setStartTime(startTime);
			System.out.println("Event's start time is changed to " + event.startTime);
		} else if (operation == 3) {
			System.out.print("Enter the new end time [i.e. 2019-12-01T15:00:00]: ");
			LocalDateTime endTime = LocalDateTime.parse(scanner.nextLine());
			event.setEndTime(endTime);
			System.out.println("Event's end time is changed to " + event.endTime);
		} else if (operation == 4) {
			System.out.print("Do you want to set the event as repeatable (Y/N): ");
			Boolean repeatable = scanner.nextLine().equals("Y") ? true : false;
			System.out.println();

			LocalDateTime repeatUntil = null;
			RepeatType frequency = null;

			if (repeatable) {
				System.out.print("Repeat until [i.e. 2020-12-01T14:00:00]: ");
				repeatUntil = LocalDateTime.parse(scanner.nextLine());
				System.out.println();

				System.out.println("Repeat frequency:\n\n"
						+ "[1] Daily\n"
						+ "[2] Weekly\n"
						+ "[3] Monthly\n"
						+ "[4] Quarterly\n"
						+ "[5] Annually\n");
				System.out.print("Please select a repeat frequency (enter the number): ");
				int freq = Integer.parseInt(scanner.nextLine());
				if (freq == 1) {
					frequency = RepeatType.DAILY;
				} else if (freq == 2) {
					frequency = RepeatType.WEEKLY;
				} else if (freq == 3) {
					frequency = RepeatType.MONTHLY;
				} else if (freq == 4) {
					frequency = RepeatType.QUARTERLY;
				} else {
					frequency = RepeatType.ANNUALLY;
				}
			}

			event.setRepeatable(repeatable, repeatUntil, frequency);
			System.out.println("Event's repeating settings have been changed!");
		} else {
			System.out.println("Operation is invalid");
		}
		System.out.println();
	}

	static void shareEvent() {
		if (calendarManager.getEvents(calendarManager.currentUser, false).size() <= 0) {
			System.out.println("There is no event in the system!\n");
			return;
		}
		if (calendarManager.userList.size() <= 1) {
			printUsers();
			return;
		}

		printEvents(false);
		System.out.print("Please select an event to share with (enter event ID): ");
		UUID eventID = UUID.fromString(scanner.nextLine());
		Event event = calendarManager.getEvent(eventID);
		System.out.println();

		printUsers();
		while (true) {
			System.out.print("Share event with which user (enter user ID or N when you're done): ");
			String viewer = scanner.nextLine();
			if (viewer.equals("N")) {
				break;
			}
			event.shareEventWith(UUID.fromString(viewer));
		}
		System.out.println();
	}

	static void printEvents(Boolean includePublic) {
		ArrayList<Event> events = calendarManager.getEvents(calendarManager.currentUser, includePublic);
		Collections.sort(events);

		if (events.size() <= 0) {
			System.out.println("There is no event in the system!\n");
			return;
		}

		System.out.println("*-*-*-* Event View *-*-*-*\n\n"
				+ "[1] View events by day\n"
				+ "[2] View events by week\n"
				+ "[3] View events by month\n"
				+ "[4] View events by year\n");
		System.out.print("Please select an option to continue (enter the number): ");
		int operation = Integer.parseInt(scanner.nextLine());
		System.out.println("\n*-*-*-* Event List *-*-*-*\n");

		if (operation == 1) {
			LocalDateTime lastEventDate = null;
			for (int i = 0; i < events.size(); ++i) {
				Event event = events.get(i);
				if (lastEventDate == null || event.startTime.getYear() != lastEventDate.getYear()
						|| event.startTime.getDayOfYear() != lastEventDate.getDayOfYear()) {
					lastEventDate = event.startTime;
					System.out.println("Events on " + lastEventDate.toLocalDate() + ":\n");
				}
				System.out.println(event);
			}
		} else if (operation == 2) {
			LocalDateTime lastEventDate = null;
			TemporalField weekOfMonth = WeekFields.of(Locale.getDefault()).weekOfMonth();
			TemporalField weekOfYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
			for (int i = 0; i < events.size(); ++i) {
				Event event = events.get(i);
				if (lastEventDate == null || event.startTime.getYear() != lastEventDate.getYear()
						|| event.startTime.get(weekOfYear) != lastEventDate.get(weekOfYear)) {
					lastEventDate = event.startTime;
					System.out.println("Events on " + lastEventDate.getYear() + "-" + lastEventDate.getMonthValue()
						+ " week " + lastEventDate.get(weekOfMonth) + ":\n");
				}
				System.out.println(event);
			}
		} else if (operation == 3) {
			LocalDateTime lastEventDate = null;
			for (int i = 0; i < events.size(); ++i) {
				Event event = events.get(i);
				if (lastEventDate == null || event.startTime.getYear() != lastEventDate.getYear()
						|| event.startTime.getMonthValue() != lastEventDate.getMonthValue()) {
					lastEventDate = event.startTime;
					System.out.println("Events in " + lastEventDate.getYear() + "-" + lastEventDate.getMonthValue() + ":\n");
				}
				System.out.println(event);
			}
		} else if (operation == 4) {
			LocalDateTime lastEventDate = null;
			for (int i = 0; i < events.size(); ++i) {
				Event event = events.get(i);
				if (lastEventDate == null || event.startTime.getYear() != lastEventDate.getYear()) {
					lastEventDate = event.startTime;
					System.out.println("Events in " + lastEventDate.getYear() + ":\n");
				}
				System.out.println(event);
			}
		} else {
			System.out.println("Operation is invalid");
		}
	}

	static void printUsers() {
		if (calendarManager.userList.size() <= 1) {
			System.out.println("There is no other user in the system.\n");
			return;
		}

		System.out.println("*-*-*-* User List *-*-*-*\n");
		for (int i = 0; i < calendarManager.userList.size(); ++i) {
			User user = calendarManager.userList.get(i);
			if (user.userID != calendarManager.currentUser) {
				System.out.println(user + "\n");				
			}
		}
	}

	static void changeTheme() {
		calendarManager.setTheme(calendarManager.theme == CalendarTheme.LIGHT ? CalendarTheme.DARK : CalendarTheme.LIGHT);
		System.out.println("Calendars is changed to " + calendarManager.getTheme() + "\n");
	}

	static void viewTimeZone() {
		System.out.println("The system's time zone is GMT" + (calendarManager.timezone >= 0 ? "+" : "")
				+ calendarManager.timezone + ".\n");
	}

	static void changeTimeZone() {
		System.out.print("Please enter the new time zone (integer number): ");
		int timezone = Integer.parseInt(scanner.nextLine());
		calendarManager.setTimeZone(timezone);
		System.out.println("Time zone is changed to GMT" + (timezone >= 0 ? "+" : "") + timezone + ".\n");
	}

	static void searchEvents() {
		System.out.print("Please enter the search query: ");
		String query = scanner.nextLine();

		ArrayList<Event> results = calendarManager.searchEvent(query);
		System.out.println("\n*-*-*-* Search Results *-*-*-*\n");

		for (int i = 0; i < results.size(); ++i) {
			System.out.println(results.get(i));
		}
	}

	static void addTimer() {
		System.out.print("Do you want to add timer for an event (Y/N): ");
		String ans = scanner.nextLine();
		System.out.println();

		if (ans.equals("Y")) {
			if (calendarManager.getEvents(calendarManager.currentUser, false).size() <= 0) {
				System.out.println("There is no event in the system!\n");
				return;
			}

			printEvents(false);
			System.out.print("Please select an event for the timer (enter event ID): ");
			UUID eventID = UUID.fromString(scanner.nextLine());
			Event event = calendarManager.getEvent(eventID);
			CountdownTimer timer = new CountdownTimer(event.title, event.endTime, calendarManager.currentUser, eventID);
			calendarManager.addTimer(timer);
		} else {
			System.out.print("Timer name: ");
			String name = scanner.nextLine();
			System.out.println();

			System.out.print("Timer end time [i.e. 2017-12-03T10:15:30]: ");
			LocalDateTime endTime = LocalDateTime.parse(scanner.nextLine());

			CountdownTimer timer = new CountdownTimer(name, endTime, calendarManager.currentUser, null);
			calendarManager.addTimer(timer);
		}
		System.out.println();
	}

	static void deleteTimer() {
		if (calendarManager.getTimers(calendarManager.currentUser).size() <= 0) {
			System.out.println("There is no timer to delete!\n");
			return;
		}

		printTimers();
		// very user unfriendly, will improve later
		System.out.print("Please select a timer to delete (enter timer ID): ");
		UUID timerID = UUID.fromString(scanner.nextLine());
		calendarManager.removeTimer(timerID);
		System.out.println("Timer is deleted!\n");
	}

	static void printTimers() {
		ArrayList<CountdownTimer> timers = calendarManager.getTimers(calendarManager.currentUser);
		if (timers.size() <= 0) {
			System.out.println("There is no timer in the system.\n");
			return;
		}

		System.out.println("*-*-*-* Timer List *-*-*-*\n");
		for (int i = 0; i < timers.size(); ++i) {
			CountdownTimer timer = timers.get(i);
			System.out.println(timer + "\n");
		}
	}

}
