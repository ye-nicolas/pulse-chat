package com.nicolas.pulse.adapter.repository.chat.room.member;

import com.nicolas.pulse.entity.enumerate.ChatRoomMemberRole;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public class ChatRoomMemberRoleWriteConverter implements Converter<ChatRoomMemberRole, String> {

    @Override
    public String convert(ChatRoomMemberRole source) {
        return source.name();
    }
}
