package com.example.board;

import com.example.board.entity.BoardEntity;
import com.example.board.repository.BoardRepository;
import com.example.board.service.BoardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

@SpringBootTest
public class BoardTest {
    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    @Test
    @Transactional
    @DisplayName("참조관계 확인")
    public void test1() {
        BoardEntity boardEntity = boardRepository.findById(1L).get();
        // boardEntity로 첨부된 파일의 이름 조회하기(부모Entity에서 자식Entity를 조회하는 경우 Transactional 필요)
        System.out.println("첨부파일이름" + boardEntity.getBoardFileEntityList().get(0).getStoredFileName());
    }
}
