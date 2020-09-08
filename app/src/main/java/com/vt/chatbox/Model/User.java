package com.vt.chatbox.Model;

public class User {
    private String id;
    private String userName;
    private String password;
    private String email;
    private String image;
	private String token;
	
	public User( String id, String userName, String password, String email, String image, String token ) {
		this.id = id;
		this.userName = userName;
		this.password = password;
		this.email = email;
		this.image = image;
		this.token = token;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken( String token ) {
		this.token = token;
	}
	
	public String getImage() {
		return image;
	}
	
	public void setImage( String image ) {
		this.image = image;
	}
	
	public String getId() {
		return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName( String userName ) {
        this.userName = userName;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword( String password ) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    
}
