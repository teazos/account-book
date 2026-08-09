package com.example.accountbook.user.dto;

public class UserView {
    private Long id;
    private String email;
    private String nickname;

    public UserView() {}
    public UserView(Long id, String email, String nickname) { this.id = id; this.email = email; this.nickname = nickname; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}
