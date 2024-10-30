package backend.academy.log_analyzer;

import java.time.LocalDate;


public record Arguments(String path, LocalDate from, LocalDate to, String outputFormat) {
}
