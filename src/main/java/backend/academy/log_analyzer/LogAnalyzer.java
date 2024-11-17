package backend.academy.log_analyzer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("checkstyle:MagicNumber")
@Slf4j
public class LogAnalyzer {
    private final List<LogRecord> records = new ArrayList<>();
    private final Map<String, Integer> resourceCount = new HashMap<>();
    private final Map<Integer, Integer> responseCodeCount = new HashMap<>();

    public LogAnalyzer(List<String> paths) throws Exception {
        LogFileReader logFileReader = new LogFileReader();
        List<String> logLines = logFileReader.loadLogs(paths);
        parseLogLines(logLines);
        analyzeLogs();
    }

    private void parseLogLines(List<String> logLines) {
        for (String line : logLines) {
            try {
                LogRecord logRecord = LogParser.parseLine(line);
                records.add(logRecord);
            } catch (IllegalArgumentException e) {
                log.error(e.getMessage());
            }
        }
    }

    private void analyzeLogs() {
        for (LogRecord logRecord : records) {
            resourceCount.put(logRecord.request(), resourceCount.getOrDefault(logRecord.request(), 0) + 1);
            responseCodeCount.put(logRecord.status(), responseCodeCount.getOrDefault(logRecord.status(), 0) + 1);
        }
    }

    public void filterByDateRange(LocalDateTime from, LocalDateTime to) {
        records.removeIf(logRecord -> {
            LocalDateTime timestamp = logRecord.timestamp();
            return (from != null && timestamp.isBefore(from)) || (to != null && timestamp.isAfter(to));
        });
        resourceCount.clear();
        responseCodeCount.clear();
        analyzeLogs();
    }

    public void filterByField(String field, String value) {
        if (field == null || value == null) {
            log.warn("Фильтрация по полю пропущена: field={}, value={}", field, value);
            return;
        }

        records.removeIf(logRecord -> {
            String fieldValue = switch (field.toLowerCase()) {
                case "agent" -> logRecord.agent();
                case "method" -> logRecord.method();
                default -> null;
            };

            if (fieldValue == null) {
                log.warn("Поле {} отсутствует в записи {}", field, logRecord);
                return true; // Удаляем запись, если поле отсутствует
            }

            boolean matches = fieldValue.matches(value.replace("*", ".*"));
            return !matches; // Удаляем, если не совпадает
        });

        resourceCount.clear();
        responseCodeCount.clear();
        analyzeLogs();
    }


    public int getTotalRequests() {
        return records.size();
    }

    public double getAverageResponseSize() {
        return records.stream()
            .mapToInt(LogRecord::size)
            .average().orElse(0);
    }

    public double get95thPercentileResponseSize() {
        List<Integer> sizes = records.stream()
            .map(LogRecord::size)
            .sorted()
            .toList();
        int index = (int) Math.ceil(sizes.size() * 0.95) - 1;
        if (index <= 0) {
            return 0;
        }
        return sizes.get(Math.max(0, Math.min(index, sizes.size() - 1)));
    }

    public double getSuccessRate() {
        long totalRequests = records.size();
        long successfulRequests = records.stream()
            .filter(logRecord -> logRecord.status() >= 200 && logRecord.status() < 300)
            .count();
        return totalRequests == 0 ? 0 : (double) successfulRequests / totalRequests * 100;
    }

    public long getUniqueIpCounts() {
        long uniqueIpCount = records.stream()
            .map(LogRecord::ip)
            .distinct()
            .count();
        return uniqueIpCount;
    }

    public Map<String, Integer> getResourceCounts() {
        return resourceCount;
    }

    public Map<Integer, Integer> getResponseCodeCounts() {
        return responseCodeCount;
    }
}
