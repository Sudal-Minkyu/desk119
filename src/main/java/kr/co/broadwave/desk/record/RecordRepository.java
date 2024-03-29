package kr.co.broadwave.desk.record;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author MinKyu
 * Date : 2019-09-06
 * Remark : 출동일지 레포지토리
 */
public interface RecordRepository extends JpaRepository<Record,Long>, QuerydslPredicateExecutor<Record> {
    Optional<Record> findByArNumber(String arNumber);

}




