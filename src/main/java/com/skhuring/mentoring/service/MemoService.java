package com.skhuring.mentoring.service;

import com.skhuring.mentoring.repository.MemoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class MemoService {
    private MemoRepository memoRepository;

}
