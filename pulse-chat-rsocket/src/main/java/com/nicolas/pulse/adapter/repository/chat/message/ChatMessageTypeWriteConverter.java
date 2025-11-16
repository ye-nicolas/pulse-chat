package com.nicolas.pulse.adapter.repository.chat.message;

import com.nicolas.pulse.entity.enumerate.ChatMessageType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public class ChatMessageTypeWriteConverter implements Converter<ChatMessageType, String> {

    @Override
    public String convert(ChatMessageType source) {
        return source.name();
    }
}
