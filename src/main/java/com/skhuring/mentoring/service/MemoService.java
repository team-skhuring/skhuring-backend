package com.skhuring.mentoring.service;

import com.skhuring.mentoring.domain.Memo;
import com.skhuring.mentoring.domain.User;
import com.skhuring.mentoring.dto.MemoReqDto;
import com.skhuring.mentoring.dto.MemoResDto;
import com.skhuring.mentoring.repository.MemoRepository;
import com.skhuring.mentoring.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class MemoService {
    private final MemoRepository memoRepository;
    private final UserRepository userRepository;

    /* 채팅방 ID 로 메모 조회 */
    public MemoResDto getMemoByChatRoomId(Long chatRoomId) {
        return memoRepository.findByChatRoomId(chatRoomId)
                .map(memo -> new MemoResDto(memo.getId(), memo.getTitle(), memo.getContent()))
                .orElse(null);
    }

    /* 유저 ID 로 메모 조회 */
    public List<MemoResDto> getMemoByUserId(Long userId) {
        return memoRepository.findByUserId(userId).stream()
                .map(memo -> new MemoResDto(memo.getId(), memo.getTitle(), memo.getContent()))
                .collect(Collectors.toList());
    }

    /* 입장한 채팅방 ID 에 따른 메모 생성 */
    public void createMemo(MemoReqDto dto) {
        String socialId = dto.getSocialId();
        User user = userRepository.findBySocialId(String.valueOf(socialId))
                .orElseThrow(() -> new IllegalArgumentException("작성자 정보를 찾을 수 없습니다."));

        Memo memo = Memo.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .chatRoomId(dto.getChatRoomId())
                .user(user)
                .build();

        memoRepository.save(memo);
        log.info("Memo created: {}", memo);
    }


    /* 메모 수정 */
    public void updateMemo(MemoReqDto dto) {
        Memo memo = memoRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("메모를 찾을 수 없습니다."));

        memo.update(dto.getTitle(), dto.getContent());
    }

    /* 메모 삭제 */
    public void deleteMemo(Long memoId) {
        memoRepository.deleteById(memoId);
    }
}
