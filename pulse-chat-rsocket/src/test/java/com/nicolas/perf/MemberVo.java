package com.nicolas.perf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberVo {
    private String accountId;
    private String name;
    @Builder.Default
    private Set<String> chatRoomIdSet = new HashSet<>();
}
