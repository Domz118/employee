package com.hris.employee.dto.response;

import java.util.List;

public class JwtResponse {

	private String token;
	private String type = "Bearer";
	private Long id;
	private String username;
	private List<String> roles;
    private Boolean isAccountLocked;


    public JwtResponse(String accessToken, Long id, String username, List<String> roles,Boolean isAccountLocked) {
		this.token = accessToken;
		this.id = id;
		this.username = username;
		this.roles = roles;
        this.isAccountLocked = isAccountLocked;
	}

	public String getAccessToken() {
		return token;
	}

	public void setAccessToken(String accessToken) {
		this.token = accessToken;
	}

	public String getTokenType() {
		return type;
	}

	public void setTokenType(String tokenType) {
		this.type = tokenType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}


    public Boolean getAccountLocked() {
        return isAccountLocked;
    }

    public void setAccountLocked(Boolean accountLocked) {
        isAccountLocked = accountLocked;
    }
}
