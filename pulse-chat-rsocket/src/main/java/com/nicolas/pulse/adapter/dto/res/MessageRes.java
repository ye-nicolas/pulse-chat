package com.nicolas.pulse.adapter.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ProblemDetail;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageRes<T> {
    @Builder.Default
    private int status = 200;
    private T data;
    private ProblemDetail problemDetail;
}
