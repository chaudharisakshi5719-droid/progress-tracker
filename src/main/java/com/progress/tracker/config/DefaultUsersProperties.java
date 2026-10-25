package com.progress.tracker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "app.default-users")
public record DefaultUsersProperties(
        DefaultUser admin,
        DefaultUser user
) {

    public record DefaultUser(
            String username,
            String email,
            String password,
            String firstName,
            String lastName,
            boolean enabled
    ) {
    }
}