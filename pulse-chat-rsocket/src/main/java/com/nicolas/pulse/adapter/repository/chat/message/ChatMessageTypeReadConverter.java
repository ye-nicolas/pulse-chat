package com.nicolas.pulse.adapter.repository.chat.message;

import com.nicolas.pulse.entity.enumerate.ChatMessageType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ReadingConverter
public class ChatMessageTypeReadConverter implements Converter<String, ChatMessageType> {

    @Override
    public ChatMessageType convert(@Nullable String source) {
        if (StringUtils.hasText(source)) {
            return ChatMessageType.valueOf(source);
        }
        return null;
    }
}
