package com.progress.tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordChangeDto {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "New password must be at least 6 characters")
    private String newPassword;

    @NotBlank(message = "Please confirm new password")
    private String confirmPassword;

    public PasswordChangeDto() {
    }

    public PasswordChangeDto(String currentPassword, String newPassword, String confirmPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String currentPassword;
        private String newPassword;
        private String confirmPassword;

        public Builder currentPassword(String currentPassword) { this.currentPassword = currentPassword; return this; }
        public Builder newPassword(String newPassword) { this.newPassword = newPassword; return this; }
        public Builder confirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; return this; }

        public PasswordChangeDto build() {
            return new PasswordChangeDto(currentPassword, newPassword, confirmPassword);
        }
    }
}
