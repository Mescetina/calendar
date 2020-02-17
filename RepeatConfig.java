package calendar;

import java.time.LocalDateTime;

class RepeatConfig {

	Boolean repeatable;
	LocalDateTime repeatUntil;
	RepeatType repeatFrequency;

	RepeatConfig(Boolean repeatable, LocalDateTime repeatUntil, RepeatType repeatFrequency) {
		this.repeatable = repeatable;
		this.repeatUntil = repeatUntil;
		this.repeatFrequency = repeatFrequency;
	}

}
