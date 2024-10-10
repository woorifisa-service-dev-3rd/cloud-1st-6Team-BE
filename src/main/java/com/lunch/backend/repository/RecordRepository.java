package com.lunch.backend.repository;

import com.lunch.backend.domain.Member;
import com.lunch.backend.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByMember(Member member);
}
