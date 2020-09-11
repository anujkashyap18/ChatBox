package com.vt.chatbox.Model;

public class ChatData {

	String message;
	String sender, reciever;
	String time;
	String type;
	String image;


	public ChatData(String message, String sender, String reciever, String time, String type, String image) {
		this.message = message;
		this.sender = sender;
		this.reciever = reciever;
		this.time = time;
		this.type = type;
		this.image = image;
	}

	public String getImage() {
		return image;
	}

	public String getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

	public String getSender() {
		return sender;
	}

	public String getReciever() {
		return reciever;
	}

	public String getTime() {
		return time;
	}


}
