package com.example.sever.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(500, "Uncategorized error.", HttpStatus.INTERNAL_SERVER_ERROR),
    UPLOAD_FAILED(500, "Upload failed.", HttpStatus.INTERNAL_SERVER_ERROR),
    JSON_PROCESSING_ERROR(500, "Failed to process JSON data.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED(401, "Unauthenticated: Invalid or expired JWT token.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(403, "You don't have permission.", HttpStatus.FORBIDDEN),
    BAD_REQUEST(400, "Invalid request.", HttpStatus.BAD_REQUEST),

    INVALID_KEY(400, "Invalid key.", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(400, "Invalid token.", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_OR_PASSWORD(400, "Invalid email or password.", HttpStatus.BAD_REQUEST),
    INVALID_FULL_NAME(400, "Your first name must be at least {min} characters.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(400, "Your password must be at least {min} characters.", HttpStatus.BAD_REQUEST),
    INVALID_DOB(400, "Your age must be at least {min}.", HttpStatus.BAD_REQUEST),
    INVALID_PHONE(400, "Your phone must be at least {min}.", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_INVALID(400, "Refresh token invalid.", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_EXPIRED(400, "Refresh token expired.", HttpStatus.BAD_REQUEST),
    TOKEN_BLACKLISTED(400, "Tokens blacklisted.", HttpStatus.BAD_REQUEST),

    ROYALTYFEE_NOT_FOUND(404, " royaltyfee not found.", HttpStatus.NOT_FOUND),
    STATUS_NOT_FOUND(404, " status not found.", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(404, "User not found.", HttpStatus.NOT_FOUND),
    CHAT_NOT_FOUND(404, "Chat not found.", HttpStatus.NOT_FOUND),
    MESSAGE_NOT_FOUND(404, "Message not found.", HttpStatus.NOT_FOUND),
    API_ENDPOINT_NOT_FOUND(404, "API endpoint not found.", HttpStatus.NOT_FOUND),

    EMAIL_EXISTED(409, "Email already existed.", HttpStatus.CONFLICT),
    PHONE_EXISTED(409, "Phone already existed.", HttpStatus.CONFLICT),

    CANT_REMOVE_USER(403, "You can't remove another user.", HttpStatus.FORBIDDEN),
    ACCOUNT_HAS_BEEN_LOCKED(403, "User account is locked.", HttpStatus.FORBIDDEN),
    NOT_IN_GROUP(403, "You are not a member of this group.", HttpStatus.FORBIDDEN),
    NOT_RELATED_TO_CHAT(403, "You are not related to this chat.", HttpStatus.FORBIDDEN),
    CANT_DELETE_OTHER_MESSAGE(403, "You can't delete another user's message.", HttpStatus.FORBIDDEN),

    CANNOT_ADD_USER_TO_SINGLE_CHAT(400, "You can't add a user to a single chat.", HttpStatus.BAD_REQUEST),
    CANNOT_REMOVE_USER_FROM_SINGLE_CHAT(400, "You can't remove a user from a single chat.", HttpStatus.BAD_REQUEST);

    final int code;
    final String message;
    final HttpStatus status;

    ErrorCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
