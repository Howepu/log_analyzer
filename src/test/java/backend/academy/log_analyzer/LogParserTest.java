package backend.academy.log_analyzer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class LogParserTest {

    @Test
    void parseLine_ValidLogLine_ReturnsLogRecord() {

        String logLine = "127.0.0.1 - - [10/Oct/2000:13:55:36 +0000] \"GET /index.html\" 200 2326 \"-\" \"Mozilla/4.08 [en] (Win98; I ;Nav)\"";


        String remoteAddr = "127.0.0.1";
        String method = "GET";
        String request = "/index.html";
        int responseCode = 200;
        int responseSize = 2326;
        String agent = "Mozilla/4.08 [en] (Win98; I ;Nav)";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        LocalDateTime timestamp = LocalDateTime.parse("10/Oct/2000:13:55:36 +0000", formatter);

        // Выполнение метода
        LogRecord logRecord = LogParser.parseLine(logLine);

        // Проверка результатов
        assertEquals(remoteAddr, logRecord.ip());
        assertEquals(timestamp, logRecord.timestamp());
        assertEquals(method, logRecord.method());
        assertEquals(request, logRecord.request());
        assertEquals(responseCode, logRecord.status());
        assertEquals(responseSize, logRecord.size());
        assertEquals(agent, logRecord.agent());
    }

    @Test
    void parseLine_InvalidLogLine_ThrowsIllegalArgumentException() {
        String invalidLogLine = "invalid log line format";

        Executable executable = () -> LogParser.parseLine(invalidLogLine);
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void parseLine_InvalidDate_ThrowsIllegalArgumentException() {
        // Подготовка строки лога с некорректной датой
        String invalidDateLogLine = "127.0.0.1 - - [32/Oct/2000:13:55:36 +0000] \"GET /index.html\" 200 2326 \"-\" \"Mozilla/4.08 [en] (Win98; I ;Nav)\"";

        // Выполнение метода и проверка выброса исключения
        Executable executable = () -> LogParser.parseLine(invalidDateLogLine);
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void parseLine_MissingFields_ThrowsIllegalArgumentException() {
        // Подготовка строки лога с отсутствующими полями
        String missingFieldsLogLine = "127.0.0.1 - - [10/Oct/2000:13:55:36 +0000] \"GET\" 200 2326";

        // Выполнение метода и проверка выброса исключения
        Executable executable = () -> LogParser.parseLine(missingFieldsLogLine);
        assertThrows(IllegalArgumentException.class, executable);
    }
}
