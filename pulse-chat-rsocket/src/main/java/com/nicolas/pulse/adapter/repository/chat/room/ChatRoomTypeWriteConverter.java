package com.nicolas.pulse.adapter.repository.chat.room;

import com.nicolas.pulse.entity.enumerate.ChatRoomType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public class ChatRoomTypeWriteConverter implements Converter<ChatRoomType, String> {

    @Override
    public String convert(ChatRoomType source) {
        return source.name();
    }
}
