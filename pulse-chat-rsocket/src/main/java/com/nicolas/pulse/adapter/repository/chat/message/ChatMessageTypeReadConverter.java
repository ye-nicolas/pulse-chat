package com.nicolas.pulse.adapter.repository.chat.message;

import com.nicolas.pulse.entity.enumerate.ChatMessageType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public class ChatMessageTypeReadConverter implements Converter<String, ChatMessageType> {

    @Override
    public ChatMessageType convert(String source) {
        return ChatMessageType.valueOf(source);
    }
}
