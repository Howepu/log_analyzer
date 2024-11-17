package backend.academy.log_analyzer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LogFileReaderTest {

    // Путь к файлу
    private static final String LOG_FILE_PATH = "src/main/resources/logs/logs.log";
    // URL для загрузки логов NGINX
    private static final String URL = "https://raw.githubusercontent.com/elastic/examples/master/Common%20Data%20Formats/nginx_logs/nginx_logs";

    private LogFileReader logFileReader;

    @BeforeEach
    public void setUp() {
        logFileReader = new LogFileReader();
    }

    @Test
    public void testLoadLogsFromLocalFile() throws Exception {
        // Проверяем, что файл существует
        assertTrue(Files.exists(Paths.get(LOG_FILE_PATH)), "Файл логов не найден по указанному пути: " + LOG_FILE_PATH);

        List<String> lines = logFileReader.loadLogs(Collections.singletonList(LOG_FILE_PATH));

        // Проверяем, что содержимое загруженных логов не пустое
        assertFalse(lines.isEmpty(), "Файл логов не должен быть пустым");

        // Проверяем, что первая строка лога содержит ожидаемое значение (например, адрес IP, метод GET и статус)
        assertTrue(lines.get(0).matches(".*\\d+\\.\\d+\\.\\d+\\.\\d+ - - \\[.*\\] \"GET .*\" \\d{3} .*"),
            "Первая строка не соответствует формату лога NGINX");
    }

    @Test
    public void testLoadLogsFromUrl() throws Exception {
        List<String> lines = logFileReader.loadLogs(Collections.singletonList(URL));

        // Проверяем, что строка не пуста и содержит ожидаемое значение
        assertFalse(lines.isEmpty(), "Логи не должны быть пустыми из URL");

        // Проверяем, что первая строка содержит ожидаемый метод GET
        assertTrue(lines.get(0).contains("GET"), "Первая строка должна содержать метод GET");

        // Дополнительная проверка на наличие правильного формата
        assertTrue(lines.get(0).matches(".*\\d+\\.\\d+\\.\\d+\\.\\d+ - - \\[.*\\] \"GET .*\" \\d{3} .*"),
            "Первая строка не соответствует формату лога NGINX");
    }
}
