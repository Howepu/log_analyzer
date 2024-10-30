package backend.academy.log_analyzer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CodeNames {
    OK("OK"),
    NOT_MODIFIED("Not Modified"),
    NOT_FOUND("Not Found"),
    SERVER_ERROR("Server Error"),
    UNKNOWN_ERROR("Unknown Error");

    private final String codeName;
}
