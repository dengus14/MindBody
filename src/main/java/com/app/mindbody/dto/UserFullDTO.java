package com.app.mindbody.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserFullDTO {
    private UserAuthDTO auth;
    private UserProfileDTO profile;
}
