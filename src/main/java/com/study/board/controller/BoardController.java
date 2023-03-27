package com.study.board.controller;

import com.study.board.entity.Board;
import com.study.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/board/write") //localhost:8080/board/write
    public String boardWriteForm(){

        return "boardwrite";
    }

    @PostMapping("/board/writepro")
    public String boardWritePro(Board board, Model model, MultipartFile file) throws Exception{

        boardService.write(board,file);
        //제목이 없으면 글작성아 되지않도록 해야한다. (수정요망!)
        if(board.getTitle() != null && !board.getTitle().isEmpty()){
            model.addAttribute("message", "글 작성 '완료' 되었습니다.");
            model.addAttribute("searchUrl", "/board/list");
        }else {
            model.addAttribute("message", "글 작성 '실패', 제목을 입력해주세요.");
            model.addAttribute("searchUrl", "/board/write");
        }
        return "message";
    }

    @GetMapping("/board/list")                           /*page:default, size:한 페이지 게시글 수, sort:정렬 기준 컬럼, direction: 정렬순서*/
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
                            ,String searchKeyword) {

        Page<Board> list = null;

        if(searchKeyword == null){
            list = boardService.boardList(pageable);
        }else{
            list = boardService.boardSearchlist(searchKeyword, pageable);
        }

        int nowPage = list.getPageable().getPageNumber();
        int startPage = Math.max(nowPage - 5, 1);
        int endPage = Math.min(nowPage+5, list.getTotalPages()-1);

        model.addAttribute("list",list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage",endPage);

        return "boardlist";
    }

    @GetMapping("/board/view") // localhost:8080/board/view?id=1
    public String boardView(Model model, Integer id){

        model.addAttribute("board", boardService.boardView(id));
        return  "boardview";
    }

    @GetMapping("/board/delete")
    public String boardDelete(Integer id){

        boardService.boardDelete(id);

        return "redirect:/board/list";
    }

    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id, Model model){

        model.addAttribute("board", boardService.boardView(id));
        return "boardmodify";
    }

    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board, Model model, MultipartFile file) throws Exception{
        if(board.getTitle() != null && !board.getTitle().isEmpty()){
            Board boardTemp = boardService.boardView(id);
            boardTemp.setTitle(board.getTitle());
            boardTemp.setContent(board.getContent());
            boardService.write(boardTemp, file);
            model.addAttribute("message", "글 수정이 완료되었습니다.");
            model.addAttribute("searchUrl", "/board/list");
        } else {
            model.addAttribute("message", "글 작성 '실패', 제목을 입력해주세요.");
            model.addAttribute("searchUrl", "/board/list");
        }

        return "message";
    }
}
