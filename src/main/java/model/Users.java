package model;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;

public class Users {
	@Id
	@NotEmpty
	private String email;
	@NotEmpty
	private String accessToken;

	public Users(String email, String accessToken)
	{
		this.email = email;
		this.accessToken = accessToken;
	}
	
	
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	

}
