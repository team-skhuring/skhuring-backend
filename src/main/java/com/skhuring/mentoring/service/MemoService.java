package com.skhuring.mentoring.service;

import com.skhuring.mentoring.domain.Memo;
import com.skhuring.mentoring.dto.MemoDto;
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
    public MemoDto getMemoByChatRoomId(Long chatRoomId) {
        return memoRepository.findByChatRoomId(chatRoomId)
                .map(memo -> new MemoDto(memo.getId(), memo.getTitle(), memo.getContent()))
                .orElse(null);
    }

    /* 유저 ID 로 메모 조회 */
    public List<MemoDto> getMemoByUserId(Long userId) {
        return memoRepository.findByUserId(userId).stream()
                .map(memo -> new MemoDto(memo.getId(), memo.getTitle(), memo.getContent()))
                .collect(Collectors.toList());
    }

    /* 메모 수정 */
    public void updateMemo(Long memoId, MemoDto dto) {
        Memo memo = memoRepository.findById(memoId).orElse(null);

        if (memo == null) {
            throw new IllegalArgumentException("메모를 찾을 수 없습니다.");
        }

        memo.update(dto.getTitle(), dto.getContent());
    }
}
