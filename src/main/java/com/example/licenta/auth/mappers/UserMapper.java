package com.example.licenta.auth.mappers;

import com.example.licenta.auth.model.User;
import com.example.licenta.auth.payload.response.RegularUserDetails;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    RegularUserDetails toApiModel(User user);

    List<RegularUserDetails> toApiModelList(List<User> users);
}
