package com.example.board.repository;

import com.example.board.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    // update board_table set board_hits=board_hits+1 where id = ?
    // jpql(java persistence query language): 필요한 쿼리문을 직접 적용하고자 할 때 사용
    @Modifying
    @Query(value = "update BoardEntity b set b.boardHits=b.boardHits+1 where b.id=:id")
//    @Query(value = "update board_table set board_hits=board_hits+1 where id=:id", nativeQuery = true)
    void updateHits(@Param("id") Long id);

    // 제목으로 검색
    // select * from board_table where board_title like '%q%'
    // Containing >> like 기능
    List<BoardEntity> findByBoardTitleContaining(String q);

    // 작성자로 검색
    // select * from board_table where board_writer like '%q%'
    List<BoardEntity> findByBoardWriterContaining(String q);
    // 작성자로 검색한 결과 페이징
    Page<BoardEntity> findByBoardWriterContaining(String q, Pageable pageable);
    // 제목으로 검색한 결과 페이징
    Page<BoardEntity> findByBoardTitleContaining(String q, Pageable pageable);

    // 제목으로 검색한 결과를 id 기준 내림차순정렬
    List<BoardEntity> findByBoardTitleContainingOrderByIdDesc (String q);

    // 작성자 또는 제목에 검색어가 포함된 결과 조회
    // select * from board_table where board_title like'%q%' or board_writer like '%q%'
    List<BoardEntity> findByBoardTitleContainingOrBoardWriterContainingOrderByIdDesc(String q1, String q2);
}
