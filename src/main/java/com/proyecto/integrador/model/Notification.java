package com.proyecto.integrador.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notification {
	private int id;
	private String sender;
	private String message;
	private LocalDateTime timestamp;
	private boolean read;

	public Notification(int id, String sender, String message, LocalDateTime timestamp, boolean read) {
		this.id = id;
		this.sender = sender;
		this.message = message;
		this.timestamp = timestamp;
		this.read = read;
	}

	public int getId() { return id; }
	public String getSender() { return sender; }
	public String getMessage() { return message; }
	public LocalDateTime getTimestamp() { return timestamp; }
	public boolean isRead() { return read; }

	public void setRead(boolean r) { this.read = r; }

	@Override
	public String toString() {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		String ts = timestamp != null ? timestamp.format(fmt) : "";
		String preview = message != null && message.length() > 80 ? message.substring(0,80) + "..." : message;
		return String.format("[%s] %s - %s", ts, sender, preview);
	}
}
