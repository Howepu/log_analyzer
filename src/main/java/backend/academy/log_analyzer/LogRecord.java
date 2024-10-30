package backend.academy.log_analyzer;

import java.time.LocalDateTime;

public record LogRecord(String ip, LocalDateTime timestamp, String request, int status, int size,
                        String agent, String method) {}
