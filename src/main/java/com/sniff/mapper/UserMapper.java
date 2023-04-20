package com.sniff.mapper;

import com.sniff.user.model.entity.User;
import com.sniff.user.model.request.UserSignUp;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserSignUp userSignup);
}
