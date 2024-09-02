package org.gobeshona.api.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class JwtResponseWeb {
  private String token;
  private String type = "Bearer";
  private Long id;
  private String username;
  @Getter @Setter
  private String password;
  private String email;
  private List<String> roles;

  public JwtResponseWeb(String accessToken, Long id, String username, String email, List<String> roles) {
    this.token = accessToken;
    this.id = id;
    this.username = username;
    this.email = email;
    this.roles = roles;
  }
  public JwtResponseWeb(String accessToken, Long id, String username, String password, String email, List<String> roles) {
    this.token = accessToken;
    this.id = id;
    this.username = username;
    this.password = password;
    this.email = email;
    this.roles = roles;
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
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
}
