package com.github.fit_app.mapper;

import com.github.fit_app.dto.ActivityRequest;
import com.github.fit_app.entity.Activity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActivityMapper {
    @Mapping(source = "name", target = "name")
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "date")
    @Mapping(ignore = true, target = "user")
    Activity activityRequestToActivity(ActivityRequest source);
}
