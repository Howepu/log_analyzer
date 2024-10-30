package backend.academy.log_analyzer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

public class LogParserTest {

    @Test
    public void testParseValidLogLine() {
        String logLine = "192.168.1.1 - - [30/Oct/2024:10:15:30 +0000] \"GET /index.html HTTP/1.1\" 200 512 \"-\" \"Mozilla/5.0\"";
        LogRecord record = LogParser.parseLine(logLine);

        assertEquals(LocalDateTime.of(2024, 10, 30, 10, 15, 30), record.timestamp());
        assertEquals("GET /index.html HTTP/1.1", record.request());
        assertEquals(200, record.status());
        assertEquals(512, record.size());
    }

    @Test
    public void testParseInvalidLogLineFormat() {
        String invalidLogLine = "192.168.1.1 - - [30/Oct/2024:10:15:30 +0000] \"GET /index.html HTTP/1.1\" 200";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            LogParser.parseLine(invalidLogLine);
        });

        assertEquals("Неверный формат строки лога", thrown.getMessage());
    }

    @Test
    public void testParseLogLineWithInvalidDate() {
        String logLineWithInvalidDate = "192.168.1.1 - - [30/Oct/2024:10:15:30] \"GET /index.html HTTP/1.1\" 200 512 \"-\" \"Mozilla/5.0\"";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            LogParser.parseLine(logLineWithInvalidDate);
        });

        assertTrue(thrown.getMessage().contains("Ошибка при парсинге даты в строке лога"));
    }

    @Test
    public void testParseLogLineWithMissingFields() {
        String logLineMissingFields = "192.168.1.1 - - [30/Oct/2024:10:15:30 +0000]";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            LogParser.parseLine(logLineMissingFields);
        });

        assertEquals("Неверный формат строки лога", thrown.getMessage());
    }

    @Test
    public void testParseLogLineWithInvalidResponseCode() {
        String logLineInvalidResponseCode = "192.168.1.1 - - [30/Oct/2024:10:15:30 +0000] \"GET /index.html HTTP/1.1\" abc 512 \"-\" \"Mozilla/5.0\"";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            LogParser.parseLine(logLineInvalidResponseCode);
        });

        assertTrue(thrown.getMessage().contains("Неверный формат строки лога"));
    }

    @Test
    public void testParseLogLineWithInvalidResponseSize() {
        String logLineInvalidResponseSize = "192.168.1.1 - - [30/Oct/2024:10:15:30 +0000] \"GET /index.html HTTP/1.1\" 200 xyz \"-\" \"Mozilla/5.0\"";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            LogParser.parseLine(logLineInvalidResponseSize);
        });

        assertTrue(thrown.getMessage().contains("Неверный формат строки лога"));
    }
}