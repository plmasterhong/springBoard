package cafe.jjdev.springboard.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import cafe.jjdev.springboard.mapper.BoardMapper;
import cafe.jjdev.springboard.vo.Board;
import cafe.jjdev.springboard.vo.BoardRequest;
import cafe.jjdev.springboard.vo.Boardfile;

/*
 * @file BoardService.java
 * @brief Board service
 * @author ksmart30 HSY
 */

//@Service: 비즈니스로직
//@Transactional: 트랜젝션 (ACID)처리
@Service
@Transactional
public class BoardService {
	
	/* DI: 외부에서 BoardMapper 객체 생성 후 주입 */
	@Autowired
	private BoardMapper boardMapper;
	
	/* -modify view 처리-
	 * @param int boardNo
	 * @brief BoardMapper.xml를 인터페이스 BoardMapper.java와 맵핑 후 selectBoard()메서드 호출 
	 * @return boardMapper.selectBoard(boardNo)(Board 객체 주소)
	 */
	public Board getBoard(int boardNo) {
		return boardMapper.selectBoard(boardNo);
	}
	
	/* -list처리- 
	 * @param int currentPage
	 * @brief BoardMapper.xml를 인터페이스 BoardMapper.java와 맵핑 후 selectBoard()메서드 호출 
	 * @return boardMapper.selectBoard(boardNo)(Board 객체 주소)
	 */
	public Map<String, Object> selectBoardList(int currentPage){
		// view(list)에서 보여줄 행의 갯수
		final int ROW_PER_PAGE = 10;
		
		// view(list)에서 보여줄 첫번째 페이지번호 초기화 (view쪽의 반복문의 초깃값)
		int startPageNum = 1;
		
		// view(list)에서 보여줄 페이지번호의 갯수 초기화 (view쪽의 반복문의 반복횟수)
		int lastPageNum = ROW_PER_PAGE;
		
		// view(list)에서 6번 페이지부터 보여줄 첫번재 페이지번호 변동)
		if(currentPage > (ROW_PER_PAGE/2)) {
			// view(list)에서 보여줄 첫번째 페이지번호 (ex:현재페이지6: 2)
			startPageNum = currentPage - ((lastPageNum/2)-1);
			// view(list)에서 보열줄 마지막 페이지 번호 (ex:마지막페이지: 12)
			lastPageNum += (startPageNum-1);
		}
		
		// Map(키, 값) 생성 -> DB에 접근하여 리스트로 보여줄 시작점과 행의 갯수 
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		// Table(board)에 담겨진 행의 시작점은 0이므로 현재페이지가 첫번째일 경우 0으로 시작하는 알고리즘
		int startRow = (currentPage-1)*ROW_PER_PAGE;
		
		// map에 보여줄 행의 시작점과 보여줄 행의 갯수를 키와 함께 담는다.
		map.put("currentPage", startRow);
		map.put("rowPerPage", ROW_PER_PAGE);
		
		// Table(board)에 담겨진 전체 행의 갯수
		int boardCount = boardMapper.selectBoardCount();
		
		// view에 보여질 마지막 페이지의 수(전체행의 갯수/보여줄 행의 갯수-> 올림)
		int lastPage = (int)(Math.ceil(boardCount/ROW_PER_PAGE));
		
		// view에 현재 페이지가 마지막페이지보다 4작을 경우 view쪽의 반복문의 반복횟수를 조정
		if(currentPage >= (lastPage-4)) {
			lastPageNum = lastPage;
		}
		
		// view에 보여질 페이징 처리를 위해 값을 Map에 담아 리턴
		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("list", boardMapper.selectBoardList(map));
		returnMap.put("currentPage", currentPage);
		returnMap.put("lastPage", lastPage);
		returnMap.put("startPageNum", startPageNum);
		returnMap.put("lastPageNum", lastPageNum);
		
		return returnMap;
	}
	
	/* - board table의 전체 행의 갯수 -
	 * @brief BoardMapper.xml를 인터페이스 BoardMapper.java와 맵핑 후 selectBoardCount()메서드 호출 
	 * @return boardMapper.selectBoardCount()(int)
	 */
	public int getBoardCount() {
		return boardMapper.selectBoardCount();
	}
	
	/* -insert처리-
	 * @brief BoardMapper.xml를 인터페이스 BoardMapper.java와 맵핑 후 insertBoard()메서드 호출 
	 * 			입력처리(result) 성공:1 실패:0
	 * @return boardMapper.insertBoard(board)(int)
	 */
	public void addBoard(BoardRequest boardRequest, String path){
		/*
		 *  1. Board분리 : board, file, boardfile(파일 정보)
		 *  2. board -> Board Vo
		 *  3. file정보 ->Boardfile Vo
		 *  4. file -> path를 이용해 물리적 장치에 저장
		 */
		// 1
		Board board = new Board();
		board.setBoardTitle(boardRequest.getBoardTitle());
		board.setBoardPw(boardRequest.getBoardPw());
		board.setBoardUser(boardRequest.getBoardUser());
		board.setBoardContent(boardRequest.getBoardContent());
		
		// board.getBoardNo()
		boardMapper.insertBoard(board);
		
		// 2
		List<MultipartFile> files = boardRequest.getFiles();
		for(MultipartFile f : files) {
			// f -> boardfile
			Boardfile boardfile = new Boardfile();
			boardfile.setBoardNo(board.getBoardNo());
			boardfile.setFileSize(f.getSize());
			boardfile.setFileType(f.getContentType());
			
			String originalFilename = f.getOriginalFilename();
			int i = originalFilename.lastIndexOf(".");
			String ext = originalFilename.substring(i+1);
			
			String fileName = UUID.randomUUID().toString();
			boardfile.setFileExt(ext);
			boardfile.setFileName(fileName);
			boardMapper.insertBoardFile(boardfile);
			
			// 전체작업이 롤백되면 파일 삭제 작업은 직접!
	
			//3. 파일저장
			try {
				f.transferTo(new File(path+"/"+fileName+"."+ext));
			} catch(IllegalStateException e) {
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			}
			
		}
		
		//return boardMapper.insertBoard(board);
	}
	
	/* -delete처리-
	 * @brief BoardMapper.xml를 인터페이스 BoardMapper.java와 맵핑 후 deleteBoard()메서드 호출 
	 * 			삭제처리(result) 성공:1 실패:0
	 * @return boardMapper.insertBoard(board)(int)
	 */
	public int removeBoard(Board board) {
		return boardMapper.deleteBoard(board);
	}
	/* -update처리-
	 * @brief BoardMapper.xml를 인터페이스 BoardMapper.java와 맵핑 후 updateBoard()메서드 호출 
	 * 			수정처리(result) 성공:1 실패:0
	 * @return boardMapper.insertBoard(board)(int)
	 */
	public int modifyBoard(Board board) {
		return boardMapper.updateBoard(board);
	}
}
