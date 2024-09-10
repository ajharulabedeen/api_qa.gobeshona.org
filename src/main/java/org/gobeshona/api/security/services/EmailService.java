package org.gobeshona.api.security.services;

public interface EmailService {
    boolean sendEmail(String email, String newPassword);
}
