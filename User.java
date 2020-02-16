package calendar;

import java.util.UUID;

class User {

	UUID userID;
	String username;

	User(String username) {
		this.userID = UUID.randomUUID();
		this.username = username;
	}

	public String toString() {
		return "User ID: " + this.userID + "\n"
				+ "Username: " + this.username;
	}

}
