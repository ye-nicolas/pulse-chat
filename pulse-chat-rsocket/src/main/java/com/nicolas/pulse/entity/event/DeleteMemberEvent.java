package com.nicolas.pulse.entity.event;

import java.util.Set;

public record DeleteMemberEvent(String roomId, Set<String> accountIdSet) {
}
