package backend.academy.log_analyzer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LogFileReaderTest {

    // Путь к файлу
    private static final String LOG_FILE_PATH = "C:\\Users\\gstit\\IdeaProjects\\backend_academy_2024_project_3-java-Howepu\\src\\test\\java\\backend\\academy\\log_analyzer\\logs_1";
    // URL для загрузки логов NGINX
    private static final String URL = "https://raw.githubusercontent.com/elastic/examples/master/Common%20Data%20Formats/nginx_logs/nginx_logs";

    private LogFileReader logFileReader;

    @BeforeEach
    public void setUp() {
        logFileReader = new LogFileReader();
    }

    @Test
    public void testLoadLogsFromLocalFile() throws IOException {
        // Проверяем, что файл существует
        assertTrue(Files.exists(Paths.get(LOG_FILE_PATH)), "Файл логов не найден по указанному пути: " + LOG_FILE_PATH);

        List<String> lines = logFileReader.loadLogs(LOG_FILE_PATH);

        // Проверяем, что содержимое загруженных логов соответствует ожиданиям
        assertFalse(lines.isEmpty(), "Файл логов не должен быть пустым");
        assertTrue(lines.get(0).contains("93.180.71.3 - - [17/May/2015:08:05:23 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\""));
    }

    @Test
    public void testLoadLogsFromUrl() throws IOException {
        List<String> lines = logFileReader.loadLogs(URL);

        // Проверяем, что строка не пуста и содержит ожидаемое значение
        assertFalse(lines.isEmpty(), "Логи не должны быть пустыми из URL");
        // Проверяем, что первая строка содержит ожидаемую информацию
        assertTrue(lines.get(0).contains("GET"), "Первая строка должна содержать метод GET");
    }
}
