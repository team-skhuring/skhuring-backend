package com.skhuring.mentoring.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessTokenDto {
    private String access_token;
    private String expires_in;
    private String scope;
    private String id_token;
}
