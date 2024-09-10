package org.gobeshona.api.security.services;

public interface SmsService {
    boolean sendSms(String email, String newPassword);
}
