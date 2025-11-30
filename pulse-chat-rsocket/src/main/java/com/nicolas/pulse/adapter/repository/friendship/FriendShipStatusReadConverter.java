package com.nicolas.pulse.adapter.repository.friendship;

import com.nicolas.pulse.entity.enumerate.FriendShipStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public class FriendShipStatusReadConverter implements Converter<String, FriendShipStatus> {

    @Override
    public FriendShipStatus convert(String source) {
        return FriendShipStatus.valueOf(source);
    }
}
