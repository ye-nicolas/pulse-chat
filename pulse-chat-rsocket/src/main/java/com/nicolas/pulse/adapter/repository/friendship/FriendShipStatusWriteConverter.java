package com.nicolas.pulse.adapter.repository.friendship;

import com.nicolas.pulse.entity.enumerate.FriendShipStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public class FriendShipStatusWriteConverter implements Converter<FriendShipStatus, String> {

    @Override
    public String convert(FriendShipStatus source) {
        return source.name();
    }
}
