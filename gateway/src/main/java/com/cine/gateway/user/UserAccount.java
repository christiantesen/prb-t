package com.cine.gateway.user;

public class UserAccount {
    private final long id;
    private final String email;
    private final String fullName;
    private final String passwordHash;

    public UserAccount(long id, String email, String fullName, String passwordHash) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.passwordHash = passwordHash;
    }

    public long getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getPasswordHash() { return passwordHash; }
}
