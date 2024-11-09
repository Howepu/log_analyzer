package backend.academy.log_analyzer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class LogAnalyzerTest {
    private LogAnalyzer logAnalyzer;
    private Path tempLogFile;

    @BeforeEach
    void setUp() throws Exception {
        // Создание временного файла с логами
        tempLogFile = Files.createTempFile("testLogs", ".log");

        // Запись фиктивных данных в временный файл в формате, который вы предоставили
        String logContent =
            "93.180.71.3 - - [17/May/2015:08:05:57 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"\n" +
                "93.180.71.3 - - [17/May/2015:08:06:00 +0000] \"GET /downloads/product_2 HTTP/1.1\" 200 1024 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"\n" +
                "93.180.71.4 - - [17/May/2015:08:06:05 +0000] \"POST /submit HTTP/1.1\" 201 250 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"\n" +
                "93.180.71.5 - - [17/May/2015:08:06:10 +0000] \"GET /downloads/product_11 HTTP/1.1\" 200 500 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"\n";

        Files.writeString(tempLogFile, logContent);

        // Инициализация LogAnalyzer с путём к временной записи
        logAnalyzer = new LogAnalyzer(Collections.singletonList(tempLogFile.toString()));
    }

    @AfterEach
    void tearDown() throws IOException {
        // Удаление временного файла после тестов
        Files.deleteIfExists(tempLogFile);
    }

    @Test
    void testTotalRequests() {
        assertEquals(4, logAnalyzer.getTotalRequests(), "Total requests should be 4.");
    }

    @Test
    void testAverageResponseSize() {
        assertEquals(444, Math.round(logAnalyzer.getAverageResponseSize()), "Average response size should be 443.75.");
    }

    @Test
    void test95thPercentileResponseSize() {
        assertEquals(1024, logAnalyzer.get95thPercentileResponseSize(), "95th percentile response size should be 500.");
    }

    @Test
    void testSuccessRate() {
        assertEquals(75.0, logAnalyzer.getSuccessRate(), "Success rate should be 75.0%.");
    }

    @Test
    void testUniqueIpCounts() {
        assertEquals(3, logAnalyzer.getUniqueIpCounts(), "Unique IP count should be 3.");
    }

    @Test
    void testFilterByDateRange() {
        LocalDateTime from = LocalDateTime.parse("2015-05-17T08:06:00");
        LocalDateTime to = LocalDateTime.parse("2015-05-17T08:06:10");
        logAnalyzer.filterByDateRange(from, to);

        assertEquals(3, logAnalyzer.getTotalRequests(), "Total requests after filtering should be 2.");
    }


    @Test
    void testResponseCodeCounts() {
        assertEquals(2, logAnalyzer.getResponseCodeCounts().get(200), "Response code count for 200 should be 2.");
        assertEquals(1, logAnalyzer.getResponseCodeCounts().get(201), "Response code count for 201 should be 1.");
        assertEquals(1, logAnalyzer.getResponseCodeCounts().get(304), "Response code count for 304 should be 1.");
    }

}
