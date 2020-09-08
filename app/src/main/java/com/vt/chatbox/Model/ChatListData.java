package com.vt.chatbox.Model;

public class ChatListData {

	String name;
	String imgurl;
	String time;
	String email;
	String tokens;

	public ChatListData(String name, String imgurl, String time, String tokens, String email) {
		this.name = name;
		this.imgurl = imgurl;
		this.time = time;
		this.tokens = tokens;
		this.email = email;

	}

	public String getTokens() {
		return tokens;
	}
	
	public String getName() {
		return name;
	}

	public String getImgurl() {
		return imgurl;
	}

	public String getTime() {
		return time;
	}

	public String getEmail() {
		return email;
	}
}
