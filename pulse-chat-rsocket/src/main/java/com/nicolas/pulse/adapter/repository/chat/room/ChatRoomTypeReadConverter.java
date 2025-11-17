package com.nicolas.pulse.adapter.repository.chat.room;

import com.nicolas.pulse.entity.enumerate.ChatRoomType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public class ChatRoomTypeReadConverter implements Converter<String, ChatRoomType> {

    @Override
    public ChatRoomType convert(String source) {
        return ChatRoomType.valueOf(source);
    }
}
