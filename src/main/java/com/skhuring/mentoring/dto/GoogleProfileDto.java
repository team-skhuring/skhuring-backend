package com.skhuring.mentoring.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleProfileDto {
    private String sub; // 사용자 고유 아이디 subjectId
    private String email;
    private String picture;
    private String name;
}
