package backend.academy.log_analyzer;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@SuppressWarnings("checkstyle:MultipleStringLiterals")
@AllArgsConstructor
public class LogReport {

    private final LogAnalyzer analyzer;
    private final String outputFormat;

    public String generateReport(String[] logFiles, LocalDateTime startDate, LocalDateTime endDate) {
        StringBuilder report = new StringBuilder();

        String fileNames = Arrays.stream(logFiles)
            .map(this::getFileName)
            .collect(Collectors.joining(", "));

        // Общая информация
        if ("adoc".equalsIgnoreCase(outputFormat)) {
            report.append("= Общая информация\n\n");
            report.append("| Метрика                  | Значение\n");
            report.append("|--------------------------|------------------------------\n");
            report.append("| Файл(-ы)                 | `").append(fileNames).append("`\n"); // Используем fileNames
            report.append("| Начальная дата           | ").append(startDate != null ? startDate.toLocalDate() : "-")
                .append("\n");
            report.append("| Конечная дата            | ").append(endDate != null ? endDate.toLocalDate() : "-")
                .append("\n");
            report.append("| Количество запросов      | ").append(analyzer.getTotalRequests()).append("\n");
            report.append("| Средний размер ответа    | ").append(Math.round(analyzer.getAverageResponseSize()))
                .append(" b\n");
            report.append("| 95p размера ответа       | ").append(Math.round(analyzer.get95thPercentileResponseSize()))
                .append(" b\n\n");
        } else { // Markdown
            report.append("#### Общая информация\n\n");
            report.append("|        Метрика           |     Значение                |\n");
            report.append("|:-------------------------|----------------------------:|\n");
            report.append("|       Файл(-ы)           | `").append(fileNames).append("` |\n"); // Используем fileNames
            report.append("|    Начальная дата        | ").append(startDate != null ? startDate.toLocalDate() : "-")
                .append(" |\n");
            report.append("|     Конечная дата        | ").append(endDate != null ? endDate.toLocalDate() : "-")
                .append(" |\n");
            report.append("|  Количество запросов     | ").append(analyzer.getTotalRequests()).append(" |\n");
            report.append("| Средний размер ответа    | ").append(Math.round(analyzer.getAverageResponseSize()))
                .append(" b |\n");
            report.append("|  95p размера ответа      | ").append(analyzer.get95thPercentileResponseSize())
                .append(" b |\n\n");
        }

        // Запрашиваемые ресурсы
        if ("adoc".equalsIgnoreCase(outputFormat)) {
            report.append("== Запрашиваемые ресурсы\n\n");
            report.append("| Ресурс                                     | Количество\n");
            report.append("|--------------------------------------------|-----------\n");
        } else {
            report.append("#### Запрашиваемые ресурсы\n\n");
            report.append("|                Ресурс                       | Количество |\n");
            report.append("|:-------------------------------------------:|-----------:|\n");
        }
        analyzer.getResourceCounts().entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .forEach(entry -> report.append("| ").append(entry.getKey())
                .append(" | ").append(entry.getValue()).append(" |\n"));

        // Коды ответа
        if ("adoc".equalsIgnoreCase(outputFormat)) {
            report.append("\n== Коды ответа\n\n");
            report.append("| Код   | Имя                        | Количество\n");
            report.append("|-------|----------------------------|-----------\n");
        } else {
            report.append("\n#### Коды ответа\n\n");
            report.append("| Код |          Имя                 | Количество |\n");
            report.append("|:---:|:----------------------------:|-----------:|\n");
        }
        analyzer.getResponseCodeCounts().entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .forEach(entry -> report.append("| ").append(entry.getKey()).append(" | ")
                .append(getResponseCodeName(String.valueOf(entry.getKey()))).append(" | ")
                .append(entry.getValue()).append(" |\n"));

        return report.toString();
    }

    private String getFileName(String filePath) {
        // Проверяем, является ли путь URL
        if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
            // Извлекаем имя файла из URL
            return filePath.substring(filePath.lastIndexOf("/") + 1);
        }
        // Для локального файла используем Paths.get()
        return Paths.get(filePath).getFileName().toString();
    }

    private String getResponseCodeName(String code) {
        return switch (code.charAt(0)) {
            case '2' -> CodeNames.OK.codeName();
            case '3' -> CodeNames.NOT_MODIFIED.codeName();
            case '4' -> CodeNames.NOT_FOUND.codeName();
            case '5' -> CodeNames.SERVER_ERROR.codeName();
            default -> CodeNames.UNKNOWN_ERROR.codeName();
        };
    }
}
