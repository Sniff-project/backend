package com.sniff.mapper;

import com.sniff.user.model.entity.User;
import com.sniff.user.model.request.UserSignUp;
import com.sniff.user.model.response.UserFullProfile;
import com.sniff.user.model.response.UserProfile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserSignUp userSignup);
    UserProfile toUserProfile(User user);
    UserFullProfile toUserFullProfile(User user);
}
