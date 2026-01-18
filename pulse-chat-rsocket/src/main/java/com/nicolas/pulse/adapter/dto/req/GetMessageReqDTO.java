package com.nicolas.pulse.adapter.dto.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetMessageReqDTO {
    @Size(min = 10, max = 100)
    private int size;
    @Size(min = 0)
    private int page;
}
