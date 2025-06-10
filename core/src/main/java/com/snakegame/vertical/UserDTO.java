package com.snakegame.vertical;

public class UserDTO {
    private long id;
    private String username;
    private String password;
    private String email;
    private String role;

    public UserDTO() {}
    public UserDTO(UserDTO user){
        this.id = user.id;
        this.username = user.username;
        this.password = user.password;
        this.email = user.email;
        this.role = user.role;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() { return this.email; }
    public void setRole(String role) { this.role = role; }
}
