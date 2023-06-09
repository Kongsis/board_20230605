package com.example.board.service;

import com.example.board.Util.UtilClass;
import com.example.board.dto.BoardDTO;
import com.example.board.entity.BoardEntity;
import com.example.board.entity.BoardFileEntity;
import com.example.board.repository.BoardFileRepository;
import com.example.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;

    public Long save(BoardDTO boardDTO) throws IOException {
//        BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
//        return boardRepository.save(boardEntity).getId();
//        if(boardDTO.getBoardFile().isEmpty()) {
        if(boardDTO.getBoardFile() == null || boardDTO.getBoardFile().get(0).isEmpty()) {
            // 파일 없음
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
            return boardRepository.save(boardEntity).getId();
        } else {
            // 파일 있음
            // 1. Board 테이블에 데이터를 먼저 저장
            BoardEntity boardEntity = BoardEntity.toSaveEntityWithFile(boardDTO);
            BoardEntity savedEntity = boardRepository.save(boardEntity);
            // 2. 파일이름 꺼내고, 저장용 이름 만들고 파일 로컬에 저장
//            String originalFileName = boardDTO.getBoardFile().getOriginalFilename();
//            String storedFileName = System.currentTimeMillis() + "_" + originalFileName;
//            String savePath = "D:\\springboot_img\\" + storedFileName;
//            boardDTO.getBoardFile().transferTo(new File(savePath));
            for (MultipartFile boardFile: boardDTO.getBoardFile()) {
                String originalFileName = boardFile.getOriginalFilename();
                String storedFileName = System.currentTimeMillis() + "_" + originalFileName;
                String savePath = "D:\\springboot_img\\" + storedFileName;
                boardFile.transferTo(new File(savePath));
                BoardFileEntity boardFileEntity =
                        BoardFileEntity.toSaveBoardFileEntity(savedEntity, originalFileName, storedFileName);
                boardFileRepository.save(boardFileEntity);
            }
            // 3. BoardFileEntity 로 변환 후 board_file_table 에 저장
            // 자식 데이터를 저장할 때 반드시 부모의 id가 아닌 부모의 Entity 객체가 전달돼야 함.
//            BoardFileEntity boardFileEntity =
//                    BoardFileEntity.toSaveBoardFileEntity(savedEntity, originalFileName, storedFileName);
//            boardFileRepository.save(boardFileEntity);
            return savedEntity.getId();
        }
    }

    @Transactional
    public List<BoardDTO> findAll() {
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<>();
//        for (BoardEntity boardEntity: boardEntityList) {
//            boardDTOList.add(BoardDTO.toDTO(boardEntity));
//        }
        boardEntityList.forEach(boardEntity -> {
            boardDTOList.add(BoardDTO.toDTO(boardEntity));
        });
        return boardDTOList;
    }
    @Transactional
    public void updateHits(Long id) {
        boardRepository.updateHits(id);
    }

    @Transactional
    public BoardDTO findById(Long id) {
        BoardEntity boardEntity = boardRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
        return BoardDTO.toDTO(boardEntity);
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    public void update(BoardDTO boardDTO) {
        BoardEntity boardEntity = BoardEntity.toUpdateEntity(boardDTO);
        boardRepository.save(boardEntity);
    }

    public Page<BoardDTO> paging(Pageable pageable) {
        // 사용자가 요청한 페이지보다 하나 작은값으로 요청해야 하기때문에 -1 을 해야함
        int page = pageable.getPageNumber() -1;
        int pageLimit = 5;
        Page<BoardEntity> boardEntities =
                boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC,"id")));
        Page<BoardDTO> boardDTOS = boardEntities.map(boardEntity -> BoardDTO.builder()
                                                    .id(boardEntity.getId())
                                                    .boardTitle(boardEntity.getBoardTitle())
                                                    .boardWriter(boardEntity.getBoardWriter())
                                                    .createdAt(UtilClass.dateFormat(boardEntity.getCreatedAt()))
                                                    .boardHits(boardEntity.getBoardHits())
                                                    .build());
        return boardDTOS;
    }
}
