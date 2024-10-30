package backend.academy.log_analyzer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("checkstyle:MagicNumber")
public class LogParser {
    private LogParser() {
        throw new AssertionError("Не удается создать экземпляр служебного класса");
    }

    public static LogRecord parseLine(String line) {
        String regex = "^(\\S+) - - \\[(.+?)\\] \"(.+?)\" (\\d{3}) (\\d+) \"([^\"]*)\" \"([^\"]*)\"$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            String remoteAddr = matcher.group(1);
            String dateString = matcher.group(2);
            String request = matcher.group(3);
            int responseCode = Integer.parseInt(matcher.group(4));
            int responseSize = Integer.parseInt(matcher.group(5));

            // Парсинг даты из строки лога
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

            LocalDateTime timestamp;
            try {
                timestamp = LocalDateTime.parse(dateString, formatter);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Ошибка при парсинге даты в строке лога: " + dateString);
            }

            return new LogRecord(remoteAddr, timestamp, request, responseCode, responseSize);
        } else {
            throw new IllegalArgumentException("Неверный формат строки лога");
        }
    }
}
