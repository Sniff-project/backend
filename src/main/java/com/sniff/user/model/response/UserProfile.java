package com.sniff.user.model.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserProfile {
    private String avatar;

    private String firstname;

    private String lastname;

    private String region;

    private String city;
}
