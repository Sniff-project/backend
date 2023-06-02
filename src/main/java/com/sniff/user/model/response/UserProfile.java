package com.sniff.user.model.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class UserProfile {
    private Long id;

    private String avatar;

    private String firstname;

    private String lastname;

    private String region;

    private String city;
}
