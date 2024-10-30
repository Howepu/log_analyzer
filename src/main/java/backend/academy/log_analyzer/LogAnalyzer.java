package backend.academy.log_analyzer;

import java.io.IOException;
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

    public LogAnalyzer(String path) throws IOException {
        LogFileReader logFileReader = new LogFileReader();
        List<String> logLines = logFileReader.loadLogs(path);
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

    // Метод для фильтрации записей по диапазону дат
    public void filterByDateRange(LocalDateTime from, LocalDateTime to) {
        int beforeFilterCount = records.size();
        records.removeIf(logRecord -> {
            LocalDateTime timestamp = logRecord.timestamp();
            return (from != null && timestamp.isBefore(from)) || (to != null && timestamp.isAfter(to));
        });
        // Пересчитываем метрики после фильтрации
        resourceCount.clear();
        responseCodeCount.clear();
        analyzeLogs(); // Повторный анализ уже отфильтрованных записей
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
        if (index < 0) {
            index = 0;
            return index;
        }
        return sizes.get(Math.min(index, sizes.size() - 1));
    }

    public Map<String, Integer> getResourceCounts() {
        return resourceCount;
    }

    public Map<Integer, Integer> getResponseCodeCounts() {
        return responseCodeCount;
    }
}
