package backend.academy.log_analyzer;

import java.time.LocalDate;
import java.util.List;

public record Arguments(List<String> paths, LocalDate from, LocalDate to, String outputFormat,
                        String filterField, String filterValue) {}

