package cafe.jjdev.springboard.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import cafe.jjdev.springboard.vo.Board;

/*
 * @file BoardMapper.java
 * @brief BoardMapper interface
 * @author ksmart30 HSY
 */

@Mapper
public interface BoardMapper {
	/* -modify view 처리-
	 * @param int boardNo
	 * @brief BoardMapper.xml(id)를 인터페이스 BoardMapper.java(메서드명)와 맵핑
	 * @return Board 객체 주소
	 */
	Board selectBoard(int boardNo);
	
	/* -list처리- 
	 * @param Map<String, Integer> map
	 * @brief BoardMapper.xml(id)를 인터페이스 BoardMapper.java(메서드명)와 맵핑
	 * @return List<Board> 객체 주소
	 */
	List<Board> selectBoardList(Map<String, Integer> map);
	
	/* - board table의 전체 행의 갯수 -
	 * @brief BoardMapper.xml(id)를 인터페이스 BoardMapper.java(메서드명)와 맵핑
	 * @return int
	 */
	int selectBoardCount();
	
	/* -insert처리-
	 * @param Board board
	 * @brief BoardMapper.xml(id)를 인터페이스 BoardMapper.java(메서드명)와 맵핑
	 * @return int
	 */
	int insertBoard(Board board);
	
	/* -delete처리-
	 * @param Board board
	 * @brief BoardMapper.xml(id)를 인터페이스 BoardMapper.java(메서드명)와 맵핑
	 * @return int
	 */
	int deleteBoard(Board board);
	
	/* -update처리-
	 * @param Board board
	 * @brief BoardMapper.xml(id)를 인터페이스 BoardMapper.java(메서드명)와 맵핑
	 * @return int
	 */
	int updateBoard(Board board);
}
