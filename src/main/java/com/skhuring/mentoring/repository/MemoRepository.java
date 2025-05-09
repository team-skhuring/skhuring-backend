package com.skhuring.mentoring.repository;

import com.skhuring.mentoring.domain.Memo;
import com.skhuring.mentoring.dto.MemoResDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    Optional<Memo> findByChatRoomId(Long chatRoomId);

    List<Memo> findByUserId(Long userId);
}
