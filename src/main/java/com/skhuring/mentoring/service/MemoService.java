package com.skhuring.mentoring.service;

import com.skhuring.mentoring.dto.MemoResDto;
import com.skhuring.mentoring.repository.MemoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class MemoService {
    private final MemoRepository memoRepository;

    /* 채팅방 ID 로 메모 조회 */
    public MemoResDto getMemoByChatRoomId(Long chatRoomId) {
        return memoRepository.findByChatRoomId(chatRoomId)
                .map(memo -> new MemoResDto(memo.getTitle(), memo.getContent()))
                .orElse(null);
    }

    /* 유저 ID 로 메모 조회 */
    public List<MemoResDto> getMemoByUserId(Long userId) {
        return memoRepository.findByUserId(userId).stream()
                .map(memo -> new MemoResDto(memo.getTitle(), memo.getContent()))
                .collect(Collectors.toList());
    }
}
