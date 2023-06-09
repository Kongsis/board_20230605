package com.example.board.controller;

import com.example.board.dto.BoardDTO;
import com.example.board.dto.CommentDTO;
import com.example.board.service.BoardService;
import com.example.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    private final CommentService commentService;

    @GetMapping("/save")
    public String saveForm() {
        return "boardPages/boardSave";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO) throws IOException {
        boardService.save(boardDTO);
        return "redirect:/board/";
    }

    @GetMapping("/")
    public String findAll(Model model) {
        List<BoardDTO> boardDTOList = boardService.findAll();
        model.addAttribute("boardList", boardDTOList);
        return "boardPages/boardList";
    }

    // /board?page=1
    @GetMapping
    public String paging(@PageableDefault(page = 1) Pageable pageable,
                         @RequestParam(value="type", required = false, defaultValue = "") String type,
                         @RequestParam(value = "q", required = false, defaultValue = "") String q,
                         Model model) {
        System.out.println("page = " + pageable.getPageNumber());
        Page<BoardDTO> boardDTOS = boardService.paging(pageable, type, q);
//        model.addAttribute("boardList", boardDTOS);
        if (boardDTOS.getTotalElements() == 0) {
            model.addAttribute("boardList", null);
        } else {
            model.addAttribute("boardList", boardDTOS);
        }
        // 시작페이지(startPage), 마지막페이지(endPage)값 계산
        // 하단에 보여줄 페이지 갯수 3개
        int blockLimit = 3;
        int startPage = (((int) (Math.ceil((double) pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;
        int endPage = ((startPage + blockLimit - 1) < boardDTOS.getTotalPages()) ? startPage + blockLimit - 1 : boardDTOS.getTotalPages();
//        if(boardDTOS.getTotalPages() > (startPage + blockLimit - 1)) {
//            endPage = startPage + blockLimit - 1;
//        } else {
//            endPage = boardDTOS.getTotalPages();
//        }
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("type", type);
        model.addAttribute("q", q);
        return "boardPages/boardPaging";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, @RequestParam("page") int page,
                           @RequestParam("type") String type,
                           @RequestParam("q") String q,
                           Model model) {
        boardService.updateHits(id);
        model.addAttribute("page", page);
        model.addAttribute("type", type);
        model.addAttribute("q", q);
//        BoardDTO boardDTO = null;
        try {
            BoardDTO boardDTO = boardService.findById(id);
            model.addAttribute("board", boardDTO);
            List<CommentDTO> commentDTOList = commentService.findAll(id);
            if(commentDTOList.size() > 0) {
                model.addAttribute("commentList", commentDTOList);
            } else {
                model.addAttribute("commentList", null);
            }
            return "boardPages/boardDetail";
        } catch(NoSuchElementException e) {
            return "boardPages/boardNotFound";
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        boardService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board", boardDTO);
        return "boardPages/boardUpdate";
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody BoardDTO boardDTO) {
        boardService.update(boardDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
