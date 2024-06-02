package com.example.licenta.userboard.mappers;

import com.example.licenta.userboard.model.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    void updateUserProfile(@MappingTarget UserProfile profileToUpdate, UserProfile profileEntity);
}
