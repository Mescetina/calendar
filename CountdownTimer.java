package calendar;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

class CountdownTimer {

	UUID timerID;
	String name;
	LocalDateTime endTime;
	UUID owner;
	UUID event;

	CountdownTimer(String name, LocalDateTime endTime, UUID owner, UUID event) {
		this.timerID = UUID.randomUUID();
		this.name = name;
		this.endTime = endTime;
		this.owner = owner;
		this.event = event;
	}

	String calcRemainingTime() {
		LocalDateTime currentTime = LocalDateTime.now();

		if (currentTime.isAfter(this.endTime)) {
			return "0 years, 0 months, 0 days, 0 hours, 0 minutes, 0 seconds";
		}

		int years = (int) currentTime.until(this.endTime, ChronoUnit.YEARS);
		currentTime = currentTime.plusYears(years);

		int months = (int) currentTime.until(this.endTime, ChronoUnit.MONTHS);
		currentTime = currentTime.plusMonths(months);

		int days = (int) currentTime.until(this.endTime, ChronoUnit.DAYS);
		currentTime = currentTime.plusDays(days);

		int hours = (int) currentTime.until(this.endTime, ChronoUnit.HOURS);
		currentTime = currentTime.plusHours(hours);

		int minutes = (int) currentTime.until(this.endTime, ChronoUnit.MINUTES);
		currentTime = currentTime.plusMinutes(minutes);

		int seconds = (int) currentTime.until(this.endTime, ChronoUnit.SECONDS);

		return years + " years, " + months + " months, " + days + " days, " + hours
				+ " hours, " + minutes + " minutes, " + seconds + " seconds";
	}

	public String toString() {
		return "Timer ID: " + this.timerID + "\n"
				+ "Timer name: " + this.name + "\n"
				+ "Timer creator: " + CalendarManager.getUser(this.owner).username + "\n"
				+ "End Time: " + this.endTime + "\n"
				+ "Time remaining: " + calcRemainingTime();
	}

}
