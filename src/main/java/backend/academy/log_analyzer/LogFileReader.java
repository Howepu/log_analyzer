package backend.academy.log_analyzer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LogFileReader {

    public List<String> loadLogs(List<String> paths) throws Exception {
        List<String> allLines = new ArrayList<>();
        for (String path : paths) {
            if (path.startsWith("http://") || path.startsWith("https://")) {
                // Чтение логов через URL (HTTP)
                URL url = new URL(path);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Обрабатываем строку (например, можно сразу добавлять в список)
                        allLines.add(line);
                    }
                }
            } else {
                // Локальное чтение логов
                Path filePath = Paths.get(path);
                try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Обрабатываем строку (например, можно сразу добавлять в список)
                        allLines.add(line);
                    }
                }
            }
        }
        return allLines;
    }
}
