package com.nicolas.pulse.adapter.repository.roleprivilege;

import com.nicolas.pulse.entity.enumerate.Privilege;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public class PrivilegeWriteConverter implements Converter<Privilege, String> {

    @Override
    public String convert(Privilege source) {
        return source.name();
    }
}
