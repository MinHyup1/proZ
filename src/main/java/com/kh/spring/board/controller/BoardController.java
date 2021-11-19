package com.kh.spring.board.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.kh.spring.board.model.dto.Board;
import com.kh.spring.board.model.dto.Post;
import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.common.util.json.JsonMaker;
import com.kh.spring.member.model.dto.Member;
import com.kh.spring.workspace.model.dto.Workspace;

@Controller
@RequestMapping("board")
public class BoardController {
	
	@Autowired
	BoardService boardService;
	

	Logger logger = LoggerFactory.getLogger(this.getClass());

	
	@GetMapping("{wsIdx}")
	public String board(@PathVariable String wsIdx, Model model) {
		
	
		Workspace workspace = boardService.selectWorkSpaceByWsIdx(wsIdx);
		
		if(workspace==null) {
			return "/error/404";
		}
		
		
		model.addAttribute(workspace);
		
		List<Board> boardList = boardService.selectBoardByWsIdx(wsIdx);
		model.addAttribute("boardList" , boardList);
		
		List<Post> postList = boardService.selectPostListByWsIdx(wsIdx);
		model.addAttribute("postList" , postList);
		
		System.out.println(boardList);
		System.out.println("포스트 리스트: " + postList);
		return "/board/board-list";
	}
	
	
	@PostMapping("change/add-board")
	@ResponseBody
	public String addBoard(@RequestBody Board board) {
		
		System.out.println("인서트 전 board: "+board);
		
		boardService.insertBoard(board);
		
		System.out.println("인서트 이후 board: " +board);
		
		
		
		return JsonMaker.json(board);
				
		
	}
	
	@PostMapping("change/remove-board")
	@ResponseBody
	public String removeBoard(@RequestBody Board board) {
		
		
		System.out.println(board);
		boardService.deleteBoard(board);
		
		return "complete";
	}
	
	
	@PostMapping("change/sort")
	public String changeSort(@RequestBody Map<String, String> map) {
		
		System.out.println(map);
		boardService.updateSort(map);
		
		return "complete";
	}
	
	
	@GetMapping("post")
	public void postForm(@RequestParam(required = false) String bdidx ,
			Model model) {
		
		Board board = boardService.selectBoardByBdIdx(bdidx);
		model.addAttribute("wsIdx" , board.getWsIdx());
		
	} 
	
	
	@PostMapping("add-post")
	@ResponseBody
	public String addPost(@RequestBody Map<String, String> map,
			@SessionAttribute(required = false , value="authentication") Member member) {

		
		System.out.println(member);
		
		System.out.println(map);
		boardService.insertPost(map , member);
		

		return "complete";
		
	}
	
	@PostMapping("post/change-sort") 
	@ResponseBody
	public String changePostSort(@RequestBody Map<String, String> map) {
		
		
		System.out.println(map);
		boardService.updatePostSort(map);
		
		
		
		return "complete";
		
	}
	
	

}