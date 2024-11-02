package backend.academy.log_analyzer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LogReportTest {

    private LogAnalyzer mockAnalyzer;
    private LogReport logReportAdoc;
    private LogReport logReportMarkdown;

    @BeforeEach
    void setUp() {
        mockAnalyzer = mock(LogAnalyzer.class);

        // Установка значений для методов LogAnalyzer с использованием mockito
        when(mockAnalyzer.getTotalRequests()).thenReturn(10);
        when(mockAnalyzer.getAverageResponseSize()).thenReturn(500.0);
        when(mockAnalyzer.getSuccessRate()).thenReturn(80.0);
        when(mockAnalyzer.getUniqueIpCounts()).thenReturn(5L);
        when(mockAnalyzer.get95thPercentileResponseSize()).thenReturn(900.0);

        // Подготовка данных о ресурсах и кодах ответа
        when(mockAnalyzer.getResourceCounts()).thenReturn(Map.of(
            "/downloads/product_1 HTTP/1.1", 4,
            "/downloads/product_2 HTTP/1.1", 2
        ));

        when(mockAnalyzer.getResponseCodeCounts()).thenReturn(Map.of(
            200, 5,
            404, 3,
            500, 2
        ));

        logReportAdoc = new LogReport(mockAnalyzer, "adoc");
        logReportMarkdown = new LogReport(mockAnalyzer, "markdown");
    }

    @Test
    void testGenerateReportAdocFormat() {
        String[] logFiles = {"test1.log", "test2.log"};
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 31, 23, 59);

        String report = logReportAdoc.generateReport(logFiles, startDate, endDate);

        assertTrue(report.contains("= Общая информация"), "Report should contain header for AsciiDoc format.");
        assertTrue(report.contains("| Файл(-ы)                 | `test1.log, test2.log`"), "Report should include file names.");
        assertTrue(report.contains("| Начальная дата           | 2023-01-01"), "Report should include start date.");
        assertTrue(report.contains("| Конечная дата            | 2023-12-31"), "Report should include end date.");
        assertTrue(report.contains("| Количество запросов      | 10"), "Report should include total requests.");
        assertTrue(report.contains("| Средний размер ответа    | 500 b"), "Report should include average response size.");
        assertTrue(report.contains("| Процент успешных запросов| 80 %"), "Report should include success rate.");
        assertTrue(report.contains("| Количество уникальных IP | 5"), "Report should include unique IP count.");
        assertTrue(report.contains("| 95p размера ответа       | 900 b"), "Report should include 95th percentile response size.");

        assertTrue(report.contains("== Запрашиваемые ресурсы"), "Report should include requested resources section.");
        assertTrue(report.contains("| /downloads/product_1 HTTP/1.1 | 4"), "Report should include resource count for /downloads/product_1.");

        assertTrue(report.contains("== Коды ответа"), "Report should include response codes section.");
        assertTrue(report.contains("| 200 | OK | 5"), "Report should include response code 200 with description and count.");
    }

    @Test
    void testGenerateReportMarkdownFormat() {
        String[] logFiles = {"test1.log", "test2.log"};
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 31, 23, 59);

        String report = logReportMarkdown.generateReport(logFiles, startDate, endDate);

        assertTrue(report.contains("#### Общая информация"), "Report should contain header for Markdown format.");
        assertTrue(report.contains("|       Файл(-ы)           | `test1.log, test2.log` |"), "Report should include file names.");
        assertTrue(report.contains("|    Начальная дата        | 2023-01-01 |"), "Report should include start date.");
        assertTrue(report.contains("|     Конечная дата        | 2023-12-31 |"), "Report should include end date.");
        assertTrue(report.contains("|  Количество запросов     | 10 |"), "Report should include total requests.");
        assertTrue(report.contains("| Средний размер ответа    | 500 b |"), "Report should include average response size.");
        assertTrue(report.contains("| Процент успешных запросов| 80 % |"), "Report should include success rate.");
        assertTrue(report.contains("| Количество уникальных IP | 5 |"), "Report should include unique IP count.");
        assertTrue(report.contains("|  95p размера ответа      | 900 b |"), "Report should include 95th percentile response size.");

        assertTrue(report.contains("#### Запрашиваемые ресурсы"), "Report should include requested resources section.");
        assertTrue(report.contains("| /downloads/product_1 HTTP/1.1 | 4 |"), "Report should include resource count for /downloads/product_1.");

        assertTrue(report.contains("#### Коды ответа"), "Report should include response codes section.");
        assertTrue(report.contains("| 200 | OK | 5 |"), "Report should include response code 200 with description and count.");
    }
}
