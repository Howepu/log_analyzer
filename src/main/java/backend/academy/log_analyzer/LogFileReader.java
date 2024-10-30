package backend.academy.log_analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LogFileReader {

    public List<String> loadLogs(String path) throws IOException {
        List<String> lines = new ArrayList<>();

        if (path.startsWith("https://") || path.startsWith("http://")) {
            // Чтение логов из URL
            URL url = new URL(path);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
        } else {
            // Чтение логов из файла
            Path filePath = Paths.get(path);
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                System.err.println("Ошибка при чтении файла " + filePath + ": " + e.getMessage());
            }
        }

        return lines;
    }
}
