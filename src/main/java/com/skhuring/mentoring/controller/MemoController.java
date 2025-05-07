package com.skhuring.mentoring.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor // 생성자 자동 생성 Lombok 어노테이션
public class MemoController {
    private MemoService memoService;

}
