package com.cine.gateway.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<UserAccount> findByEmail(String email) {
        List<UserAccount> users = jdbcTemplate.query(
                "CALL sp_find_user_by_email(?)",
                (rs, row) -> new UserAccount(
                        rs.getLong("id"),
                        rs.getString("email"),
                        rs.getString("fullName"),
                        rs.getString("passwordHash")),
                email);
        return users.stream().findFirst();
    }
}
