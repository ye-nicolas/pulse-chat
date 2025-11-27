package com.nicolas.pulse.adapter.repository.roleprivilege;

import com.nicolas.pulse.entity.enumerate.Privilege;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public class PrivilegeReadConverter implements Converter<String, Privilege> {

    @Override
    public Privilege convert(String source) {
        return Privilege.valueOf(source);
    }
}
