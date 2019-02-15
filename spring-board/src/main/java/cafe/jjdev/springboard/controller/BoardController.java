package cafe.jjdev.springboard.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import cafe.jjdev.springboard.service.BoardService;
import cafe.jjdev.springboard.vo.Board;

/*
 * @file BoardController.java
 * @brief board controller
 * @author ksmart30 HSY
 */

@Controller
public class BoardController {
	
	/* DI: 외부에서 BoardService 객체 생성 후 주입 */
	@Autowired
	private BoardService boardService;

	// 리스트 요청
	/* @param  @RequestParam(value="currentPage", required=false, defaultValue="1") int currentPage
	 * 		   get방식으로 currenPage를 받지 못 할 경우  기본값을 1로 셋팅후 선언한 int타입의 currentPage 매개변수에 복사한다.
	 * 		   Model model
	 * @brief  boardService객체의 selectBoardList메서드 호출
	 * 		   "http://localhost/boardList" 주소분기(get방식)
	 * 		   template폴더에 있는 boardList.html forward
	 * 		       같은표현: @RequestMapping(value="/boardList", method = RequestMethod.GET)
	 * @return String(view이름)
	 */
    @GetMapping("/boardList")
	public String boardList(Model model,
                            @RequestParam(value="currentPage", 
                            			  required=false, 
                            			  defaultValue="1") int currentPage) {
    	Map<String, Object> returnMap = boardService.selectBoardList(currentPage);
    	//Map객체주소로 보내는 경우(model.addAttribute("map", returnMap);
    	
    	//returnMap(Map타입 객체)에 담겨있는 값 -> model(Model타입 객체)에 복사 ->  view전달
    	model.addAttribute("list", returnMap.get("list"));
    	model.addAttribute("currentPage",returnMap.get("currentPage"));
    	model.addAttribute("lastPage",returnMap.get("lastPage"));
    	model.addAttribute("startPageNum",returnMap.get("startPageNum"));
    	model.addAttribute("lastPageNum", returnMap.get("lastPageNum"));
        return "boardList";
    }
    
    // 입력페이지 요청
    /* @brief  "http://localhost/boardAdd" 주소분기(get방식)
	 * 		   template폴더에 있는 boardAdd.html forward
	 * 		       같은표현: @RequestMapping(value="/boardAdd", method = RequestMethod.GET)
	 * @return String(view이름)
	 */
    @GetMapping("/boardAdd")
    public String boardAdd() {
        
        return "boardAdd";
    }
    
    // 입력(액션) 요청
    /* @param  Board board(커맨드객체)
     * 		       커맨드 객체: post방식으로 값을 받을 때  vo의 필드명과 input태그의 name과 같으면 vo객체에 자동 set
     * @brief  boardService객체의 addBoard메서드 호출
	 * 		   "http://localhost/boardList" 주소분기(post방식)
	 * 		   template폴더에 있는 boardList.html redirect
	 * 		       같은표현:@RequestMapping(value="/boardAdd", method = RequestMethod.POST)
	 * @return String(view이름)
	 */
    @PostMapping("/boardAdd")
    public String boardAdd(Board board) {
    	boardService.addBoard(board);
    	return "redirect:/boardList"; // 글입력후 "/boardList"로 리다이렉트(재요청)
    }
    
    
    //  4. 삭제
    /* @param  Board board(커맨드객체)
     * 		       커맨드 객체: post방식으로 값을 받을 때  vo의 필드명과 input태그의 name과 같으면 vo객체에 자동 set
     * @brief  boardService객체의 addBoard메서드 호출
	 * 		   "http://localhost/boardList" 주소분기(get방식)
	 * 		   boardPw가 있을 시 삭제 처리 후  "http://localhost/boardList"주소분기
	 * 		   template폴더에 있는 boardList.html redirect
	 * 		   boardPw가 없을 시  "http://localhost/boardRemove"주소분기
	 * 		   template폴더에 있는 boardList.html forward
	 * 		       같은표현:@RequestMapping(value="/boardRemove", method = RequestMethod.POST)
	 * @return String(view이름)
	 */
 	@GetMapping("/boardRemove")
 	public String boardRemove(Board board) {
 		String dispather = "boardRemove";
 		if(board.getBoardPw() != null) {
 			boardService.removeBoard(board);
 			dispather = "redirect:/boardList";
 		}
 		return dispather;
 	}
 	
 	// 5. 수정 폼
 	/* @param  @RequestParam(value="boardNo") int boardNo, Model model
     * 		   
     * @brief  boardService객체의 getBoard메서드 호출
	 * 		   "http://localhost/boardModify" 주소분기(get방식)
	 * 		   template폴더에 있는 boardModify.html forward
	 * @return String(view이름)
	 */
 	@GetMapping("/boardModify")
	public String boardModify(@RequestParam(value="boardNo") int boardNo , Model model) {
		Board modifyBoard = boardService.getBoard(boardNo);
		//modifyBoard(Board타입 객체)에 담겨있는 값 -> model(Model타입 객체)에 복사 ->  view전달
		model.addAttribute("board", modifyBoard);
		return "boardModify";
	}
 	
 	// 6. 수정 액션
 	/* @param  Board board(커맨드객체)
     * 		       커맨드 객체: post방식으로 값을 받을 때  vo의 필드명과 input태그의 name과 같으면 vo객체에 자동 set
     * @brief  boardService객체의 modifyBoard메서드 호출
	 * 		   "http://localhost/boardList" 주소분기(post방식)
	 * 		   template폴더에 있는 boardList.html redirect
	 * @return String(view이름)
	 */
 	@PostMapping("/boardModify")
	public String boardModify(Board board) {
 		boardService.modifyBoard(board);
		return "redirect:/boardList";
	}
}
