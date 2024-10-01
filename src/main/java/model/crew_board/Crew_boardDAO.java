package model.crew_board;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.JDBCUtil;

public class Crew_boardDAO {
	//(페이지 네이션) 특정 크루 글 출력 model_crew_board_min_num, model_crew_board_max_num, CREW_BOARD_WRITER_ID
	private final String ALL_CREW_BOARD = "SELECT\r\n"
			+ "	CB.RN,\r\n"
			+ "	CB.CREW_BOARD_NUM,\r\n"
			+ "	CB.CREW_BOARD_WRITER_ID,\r\n"
			+ "    CB.CREW_BOARD_TITLE,\r\n"
			+ "    CB.CREW_BOARD_CONTENT,\r\n"
			+ "    CB.CREW_BOARD_CNT,\r\n"
			+ "    M.MEMBER_PROFILE\r\n"
			+ "FROM (\r\n"
			+ "    SELECT \r\n"
			+ "        CREW_BOARD_NUM, \r\n"
			+ "        CREW_BOARD_WRITER_ID,\r\n"
			+ "        CREW_BOARD_TITLE, \r\n"
			+ "        CREW_BOARD_CONTENT, \r\n"
			+ "        CREW_BOARD_CNT, \r\n"
			+ "        ROW_NUMBER() OVER (ORDER BY CREW_BOARD_NUM DESC) AS RN\r\n"
			+ "    FROM \r\n"
			+ "        CREW_BOARD\r\n"
			+ ") CB\r\n"
			+ "JOIN\r\n"
			+ "	MEMBER M\r\n"
			+ "ON\r\n"
			+ "	M.MEMBER_ID = CB.CREW_BOARD_WRITER_ID\r\n"
			+ "JOIN\r\n"
			+ "	CREW C\r\n"
			+ "ON\r\n"
			+ "	M.MEMBER_CREW_NUM = C.CREW_NUM\r\n"
			+ "WHERE RN BETWEEN ? AND ? \r\n"
			+ "AND C.CREW_NUM = (\r\n"
			+ "	SELECT \r\n"
			+ "		MM.MEMBER_CREW_NUM\r\n"
			+ "    FROM \r\n"
			+ "    	MEMBER MM\r\n"
			+ "    WHERE \r\n"
			+ "    	MM.MEMBER_ID = ?\r\n"
			+ "	)";

	//해당 크루 커뮤니티 전체글 총 개수 CREW_BOARD_WRITER_ID
	private final String ONE_COUNT = "SELECT COUNT(*) AS CREW_BOARD_TOTAL \r\n"
			+ "FROM \r\n"
			+ "	CREW_BOARD CB\r\n"
			+ "JOIN\r\n"
			+ "	MEMBER M\r\n"
			+ "ON\r\n"
			+ "	M.MEMBER_ID = CB.CREW_BOARD_WRITER_ID\r\n"
			+ "JOIN\r\n"
			+ "	CREW C\r\n"
			+ "ON\r\n"
			+ "	M.MEMBER_CREW_NUM = C.CREW_NUM\r\n"
			+ "WHERE C.CREW_NUM = (\r\n"
			+ "	SELECT \r\n"
			+ "		MM.MEMBER_CREW_NUM\r\n"
			+ "    FROM \r\n"
			+ "    	MEMBER MM\r\n"
			+ "    WHERE \r\n"
			+ "    	MM.MEMBER_ID = ?\r\n"
			+ "	)";
	
	//크루 게시판 글 작성 CREW_BOARD_WRITER_ID, CREW_BOARD_CONTENT, CREW_BOARD_TITLE
	private final String INSERT = "INSERT INTO CREW_BOARD(CREW_BOARD_NUM,CREW_BOARD_WRITER_ID,CREW_BOARD_CONTENT,CREW_BOARD_TITLE)\r\n"
			+ "VALUES ((SELECT NVL(MAX(CREW_BOARD_NUM),0)+1 FROM CREW_BOARD),?,?,?)";
	
	//크루 게시판 글 삭제 CREW_NUM
	private final String DELETE = "DELETE FROM CREW_BOARD WHERE CREW_BOARD_NUM = ?";
	
	//PK로 검색 CREW_BOARD_NUM
	private final String ONE = "SELECT CREW_BOARD_NUM,CREW_BOARD_WRITER_ID,CREW_BOARD_CONTENT,CREW_BOARD_CNT FROM CREW_BOARD WHERE CREW_BOARD_NUM = ?";
	
	public boolean insert(Crew_boardDTO Crew_boardDTO) {//기능 미구현
		System.out.println("crew_board.crew_boardDAO.insert 시작");
		Connection conn=JDBCUtil.connect();
		PreparedStatement pstmt=null;
		try {
			//크루 게시판 글 작성 CREW_BOARD_WRITER_ID, CREW_BOARD_CONTENT, CREW_BOARD_TITLE
			pstmt=conn.prepareStatement(INSERT);
			pstmt.setString(1, Crew_boardDTO.getModel_crew_board_writer_id());
			pstmt.setString(2, Crew_boardDTO.getModel_crew_board_content());
			pstmt.setString(3, Crew_boardDTO.getModel_crew_board_title());
			int rs = pstmt.executeUpdate();
			if(rs<=0) {
				System.err.println("crew_board.crew_boardDAO.insert 실패");
				return false;
			}
		} catch (SQLException e) {
			System.out.println("crew_board.crew_boardDAO.insert SQL문 실패");
			return false;
		}finally {
			JDBCUtil.disconnect(pstmt,conn);
		}
		System.out.println("crew_board.crew_boardDAO.insert 성공");
		return true;
	}
	private boolean update(Crew_boardDTO Crew_boardDTO) {//기능 미구현
		System.out.println("crew_board.crew_boardDAO.update 시작");
		System.out.println("crew_board.crew_boardDAO.update 성공");
		return true;
	}
	public boolean delete(Crew_boardDTO Crew_boardDTO) {
		System.out.println("crew_board.crew_boardDAO.delete 시작");
		Connection conn=JDBCUtil.connect();
		PreparedStatement pstmt=null;
		try {
			pstmt=conn.prepareStatement(DELETE);
			pstmt.setInt(1, Crew_boardDTO.getModel_crew_board_num());
			int rs = pstmt.executeUpdate();
			if(rs<=0) {
				System.err.println("crew_board.crew_boardDAO.delete 실패");
				return false;
			}
		} catch (SQLException e) {
			System.err.println("crew_board.crew_boardDAO.delete SQL문 실패");
			return false;
		}finally {
			JDBCUtil.disconnect(pstmt,conn);
		}
		System.out.println("crew_board.crew_boardDAO.delete 성공");
		return true;
	}
	public Crew_boardDTO selectOne(Crew_boardDTO Crew_boardDTO) {
		System.out.println("crew_board.crew_boardDAO.selectOne 시작");
		Crew_boardDTO data = null;
		String sqlq = ""; //쿼리수행결과 구분용 데이터
		Connection conn=JDBCUtil.connect();
		PreparedStatement pstmt=null;
		try {
			//PK로 검색 CREW_BOARD_NUM
			if(Crew_boardDTO.getModel_crew_board_condition().equals("CREW_BOARD_ONE")) {
				pstmt=conn.prepareStatement(ONE);
				pstmt.setInt(1, Crew_boardDTO.getModel_crew_board_num());
				sqlq = "one";
			}
			//해당 크루 커뮤니티 전체글 총 개수 CREW_BOARD_WRITER_ID
			else if(Crew_boardDTO.getModel_crew_board_condition().equals("CREW_BOARD_ONE_COUNT")) {
				pstmt=conn.prepareStatement(ONE_COUNT);
				pstmt.setString(1, Crew_boardDTO.getModel_crew_board_writer_id());
				sqlq="count";
			}
			else {
				System.err.println("condition값 잘못됨");
				return null;
			}
			System.out.println("쿼리수행결과 구분용 데이터 = "+sqlq);
			ResultSet rs = pstmt.executeQuery();
			boolean flag = rs.next();
			if(flag && sqlq.equals("one")) {
				data = new Crew_boardDTO();
				data.setModel_crew_board_num(rs.getInt("CREW_BOARD_NUM"));
				data.setModel_crew_board_writer_id(rs.getString("CREW_BOARD_WRITER_ID"));
				data.setModel_crew_board_title(rs.getString("CREW_BOARD_TITLE"));
				data.setModel_crew_board_content(rs.getString("CREW_BOARD_CONTENT"));
				data.setModel_crew_board_cnt(rs.getInt("CREW_BOARD_CNT"));
			}
			else if(flag && sqlq.equals("count")) {
				System.out.println("crew_boardDAO.selectOne 맞는 데이터 존재함");
				data = new Crew_boardDTO();
				data.setModel_crew_board_total(rs.getInt("CREW_BOARD_TOTAL"));
			}
		} catch (SQLException e) {
			System.err.println("crew_board.crew_boardDAO.selectOne SQL문 실패");
		}
		JDBCUtil.disconnect(pstmt,conn);
		System.out.println("crew_board.crew_boardDAO.selectOne 성공");
		return data;
	}
	public ArrayList<Crew_boardDTO> selectAll(Crew_boardDTO Crew_boardDTO) {
		System.out.println("crew_board.crew_boardDAO.selectAll 시작");
		ArrayList<Crew_boardDTO> datas = new ArrayList<Crew_boardDTO>();
		Crew_boardDTO data = null;
		int rsCnt=1;//로그용
		Connection conn=JDBCUtil.connect();
		PreparedStatement pstmt=null;
		try {
			//(페이지 네이션) 특정 크루 글 출력 model_crew_board_min_num, model_crew_board_max_num, model_crew_board_writer_id
			if(Crew_boardDTO.getModel_crew_board_condition().equals("CREW_BOARD_ALL_CREW_BOARD")) {
				pstmt=conn.prepareStatement(ALL_CREW_BOARD);
				pstmt.setInt(1, Crew_boardDTO.getModel_crew_board_min_num());
				pstmt.setInt(2, Crew_boardDTO.getModel_crew_board_max_num());
				pstmt.setString(3, Crew_boardDTO.getModel_crew_board_writer_id());
			}
			else {
				System.err.println("condition값 잘못됨");
				return datas;
			}
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				System.out.println(rsCnt+"번행 출력중..");
				data = new Crew_boardDTO();
				data.setModel_crew_board_num(rs.getInt("CREW_BOARD_NUM"));
				data.setModel_crew_board_writer_id(rs.getString("CREW_BOARD_WRITER_ID"));
				data.setModel_crew_board_title(rs.getString("CREW_BOARD_TITLE"));
				data.setModel_crew_board_content(rs.getString("CREW_BOARD_CONTENT"));
				data.setModel_crew_board_cnt(rs.getInt("CREW_BOARD_CNT"));
				data.setModel_crew_board_member_profile(rs.getString("MEMBER_PROFILE"));
				datas.add(data);
				rsCnt++;
			}

		} catch (SQLException e) {
			System.err.println("crew_board.crew_boardDAO.selectAll SQL문 실패");
		}
		JDBCUtil.disconnect(pstmt,conn);
		System.out.println("crew_board.crew_boardDAO.selectAll 성공");
		return datas;
	}
}
