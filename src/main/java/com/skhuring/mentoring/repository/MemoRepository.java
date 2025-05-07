package com.skhuring.mentoring.repository;

import com.skhuring.mentoring.domain.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoRepository extends JpaRepository<Memo, Long> {

}
