package model.reservation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.JDBCUtil;

public class ReservationDAO {
	//예약 등록 RESERVATION_GYM_NUM, RESERVATION_DATE, RESERVATION_MEMBER_ID, RESERVATION_PRICE
	private final String INSERT = "INSERT INTO RESERVATION(RESERVATION_NUM,RESERVATION_GYM_NUM,RESERVATION_DATE,RESERVATION_MEMBER_ID,RESERVATION_PRICE)\r\n"
			+ "VALUES ((SELECT NVL(MAX(RESERVATION_NUM),0)+1 FROM RESERVATION),?,?,?,?)";
	
	//PK로 예약 정보 찾기 RESERVATION_NUM
	private final String ONE = "SELECT\r\n"
			+ "	RESERVATION_NUM,\r\n"
			+ "	RESERVATION_DATE,\r\n"
			+ "	RESERVATION_GYM_NUM,\r\n"
			+ "	RESERVATION_MEMBER_ID,\r\n"
			+ "	RESERVATION_PRICE\r\n"
			+ "FROM\r\n"
			+ "	RESERVATION\r\n"
			+ "WHERE\r\n"
			+ "	RESERVATION_NUM = ?";
	
	//해당 암벽장 예약 가능 개수 RESERVATION_GYM_NUM
	private final String ONE_COUNT = "SELECT COUNT(*) AS RESERVATION_TOTAL\r\n"
			+ "FROM\r\n"
			+ "(\r\n"
			+ "	SELECT G.GYM_RESERVATION_CNT AS RESERVATION_TOTAL \r\n"
			+ "	FROM \r\n"
			+ "		RESERVATION R\r\n"
			+ "	JOIN\r\n"
			+ "		GYM G\r\n"
			+ "	ON\r\n"
			+ "		R.RESERVATION_GYM_NUM = G.GYM_NUM\r\n"
			+ "	WHERE\r\n"
			+ "		RESERVATION_GYM_NUM = ? AND RESERVATION_DATE = ?\r\n"
			+ ")";
	
	//예약 취소 RESERVATION_NUM
	private final String DELETE = "DELETE FROM RESERVATION WHERE RESERVATION_NUM = ?";
	
	//사용자 아이디로 예약한 내역 전부 출력 RESERVATION_MEMBER_ID
	private final String ALL = "SELECT\r\n"
			+ "	R.RESERVATION_NUM,\r\n"
			+ "	R.RESERVATION_DATE,\r\n"
			+ "	R.RESERVATION_GYM_NUM,\r\n"
			+ "	R.RESERVATION_MEMBER_ID,\r\n"
			+ "	R.RESERVATION_PRICE,\r\n"
			+ "	G.GYM_NAME\r\n"
			+ "FROM\r\n"
			+ "	RESERVATION R\r\n"
			+ "JOIN\r\n"
			+ "	MEMBER M\r\n"
			+ "ON \r\n"
			+ "	R.RESERVATION_MEMBER_ID = M.MEMBER_ID\r\n"
			+ "JOIN \r\n"
			+ "    GYM G\r\n"
			+ "ON \r\n"
			+ "    R.RESERVATION_GYM_NUM = G.GYM_NUM\r\n"
			+ "WHERE\r\n"
			+ "	R.RESERVATION_MEMBER_ID = ?";
	
	//사용자 아이디로 해당 암벽장에 예약한 날짜 중복 확인 RESERVATION_MEMBER_ID, RESERVATION_GYM_NUM, RESERVATION_DATE
	private final String ONE_RESERVATION = "SELECT\r\n"
			+ "	R.RESERVATION_NUM,\r\n"
			+ "	R.RESERVATION_DATE,\r\n"
			+ "	R.RESERVATION_GYM_NUM,\r\n"
			+ "	R.RESERVATION_MEMBER_ID,\r\n"
			+ "	R.RESERVATION_PRICE\r\n"
			+ "FROM\r\n"
			+ "	RESERVATION R\r\n"
			+ "JOIN\r\n"
			+ "	MEMBER M\r\n"
			+ "ON \r\n"
			+ "	R.RESERVATION_MEMBER_ID = M.MEMBER_ID\r\n"
			+ "WHERE\r\n"
			+ "	R.RESERVATION_MEMBER_ID = ? AND R.RESERVATION_GYM_NUM = ? AND R.RESERVATION_DATE = ?";

	public boolean insert(ReservationDTO reservationDTO) {
		System.out.println("reservation.reservationDAO.insert 시작");
		Connection conn=JDBCUtil.connect();
		PreparedStatement pstmt=null;
		try {
			//예약 등록 RESERVATION_GYM_NUM, RESERVATION_DATE, RESERVATION_MEMBER_ID, RESERVATION_PRICE
			pstmt=conn.prepareStatement(INSERT);
			pstmt.setInt(1, reservationDTO.getModel_reservation_gym_num());
			pstmt.setString(2, reservationDTO.getModel_reservation_date());
			pstmt.setString(3, reservationDTO.getModel_reservation_member_id());
			pstmt.setInt(4, reservationDTO.getModel_reservation_price());
			int rs = pstmt.executeUpdate();
			if(rs<=0) {
				System.err.println("reservation.reservationDAO.insert 실패");
				return false;
			}

		} catch (SQLException e) {
			System.out.println("reservation.reservationDAO.insert SQL문 실패");
			return false;
		}finally {
			JDBCUtil.disconnect(pstmt,conn);
		}
		System.out.println("reservation.reservationDAO.insert 성공");
		return true;
	}
	public boolean update(ReservationDTO reservationDTO) {
		System.out.println("reservation.reservationDAO.update 시작");
		Connection conn=JDBCUtil.connect();
		PreparedStatement pstmt=null;
		try {
			pstmt=conn.prepareStatement("");
			int rs = pstmt.executeUpdate();
			if(rs<=0) {
				System.err.println("reservation.reservationDAO.update 실패");
				return false;
			}

		} catch (SQLException e) {
			System.err.println("reservation.reservationDAO.update SQL문 실패");
			return false;
		}finally {
			JDBCUtil.disconnect(pstmt,conn);
		}
		System.out.println("reservation.reservationDAO.update 성공");
		return true;
	}
	public boolean delete(ReservationDTO reservationDTO) {
		System.err.println("reservation.reservationDAO.delete 시작");
		Connection conn=JDBCUtil.connect();
		PreparedStatement pstmt=null;
		try {
			//예약 취소 RESERVATION_NUM
			pstmt=conn.prepareStatement(DELETE);
			pstmt.setInt(1, reservationDTO.getModel_reservation_num());
			int rs = pstmt.executeUpdate();
			if(rs<=0) {
				System.err.println("reservation.reservationDAO.delete 실패");
				return false;
			}

		} catch (SQLException e) {
			System.err.println("reservation.reservationDAO.delete SQL문 실패");
			return false;
		}finally {
			JDBCUtil.disconnect(pstmt,conn);
		}
		System.out.println("reservation.reservationDAO.delete 성공");
		return true;
	}

	public ReservationDTO selectOne(ReservationDTO reservationDTO){
		System.out.println("reservation.reservationDAO.selectOne 시작");
		ReservationDTO data = null;
		String sqlq; //쿼리수행결과 구분용 데이터
		Connection conn=JDBCUtil.connect();
		PreparedStatement pstmt=null;
		try {
			//PK로 예약 정보 찾기 RESERVATION_NUM
			if(reservationDTO.getModel_reservation_condition().equals("RESERVATION_ONE")) {
				pstmt=conn.prepareStatement(ONE);
				pstmt.setInt(1, reservationDTO.getModel_reservation_num());
				sqlq = "one";
			}
			//해당 암벽장 예약 가능 개수 RESERVATION_GYM_NUM
			else if(reservationDTO.getModel_reservation_condition().equals("RESERVATION_ONE_COUNT")) {
				pstmt=conn.prepareStatement(ONE_COUNT);
				pstmt.setInt(1, reservationDTO.getModel_reservation_gym_num());
				pstmt.setString(2, reservationDTO.getModel_reservation_date());
				System.err.println("167 log"+reservationDTO.getModel_reservation_date());
				sqlq = "count";
			}
			//사용자 아이디로 해당 암벽장에 예약한 날짜 중복 확인 RESERVATION_MEMBER_ID, RESERVATION_GYM_NUM, RESERVATION_DATE
			else if(reservationDTO.getModel_reservation_condition().equals("RESERVATION_ONE_SEARCH")) {
				pstmt=conn.prepareStatement(ONE_RESERVATION);
				pstmt.setString(1, reservationDTO.getModel_reservation_member_id());
				pstmt.setInt(2, reservationDTO.getModel_reservation_gym_num());
				pstmt.setString(3, reservationDTO.getModel_reservation_date());
				sqlq = "one";
			}
			else {
				System.err.println("condition 틀림");
				return null;
			}
			System.out.println("쿼리수행결과 구분용 데이터 = "+sqlq);
			ResultSet rs = pstmt.executeQuery();
			boolean flag = rs.next();
			if(flag && sqlq.equals("one")) {
				System.out.println("reservation.reservationDAO.selectOne 검색 성공");
				data = new ReservationDTO();
				data.setModel_reservation_num(rs.getInt("RESERVATION_NUM"));
				data.setModel_reservation_date(rs.getString("RESERVATION_DATE"));
				data.setModel_reservation_gym_num(rs.getInt("RESERVATION_GYM_NUM"));
				data.setModel_reservation_member_id(rs.getString("RESERVATION_MEMBER_ID"));
				data.setModel_reservation_price(rs.getInt("RESERVATION_PRICE"));
			}
			else if(flag && sqlq.equals("count")) {
				System.out.println("reservation.reservationDAO.selectOne 검색 성공");
				data = new ReservationDTO();
				data.setModel_reservation_total(rs.getInt("RESERVATION_TOTAL"));
			}
		} catch (SQLException e) {
			System.err.println("reservation.reservationDAO.selectOne SQL문 실패");
			System.err.println("reservation.reservationDAO.selectOne SQL문 실패: " + e.getMessage());
		    e.printStackTrace(); // 예외의 전체 스택 트레이스를 로그로 남길 수 있음
			return null;
		}finally {
			JDBCUtil.disconnect(pstmt,conn);
		}
		System.out.println("reservation.reservationDAO.selectOne 성공");
		return data;
	}

	public ArrayList<ReservationDTO> selectAll(ReservationDTO reservationDTO){
		System.out.println("reservation.reservationDAO.selectAll 시작");
		ArrayList<ReservationDTO> datas = new ArrayList<ReservationDTO>();
		int rsCnt=1;//로그용
		Connection conn = JDBCUtil.connect();
		PreparedStatement pstmt = null;
		try {
			//사용자 아이디로 예약한 내역 전부 출력 RESERVATION_MEMBER_ID
			pstmt=conn.prepareStatement(ALL);
			pstmt.setString(1, reservationDTO.getModel_reservation_member_id());
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				System.out.println(rsCnt+"번행 출력중...");
				ReservationDTO data = new ReservationDTO();
				data.setModel_reservation_num(rs.getInt("RESERVATION_NUM"));
				data.setModel_reservation_date(rs.getString("RESERVATION_DATE"));
				data.setModel_reservation_gym_num(rs.getInt("RESERVATION_GYM_NUM"));
				data.setModel_reservation_member_id(rs.getString("RESERVATION_MEMBER_ID"));
				data.setModel_reservation_price(rs.getInt("RESERVATION_PRICE"));
				data.setModel_reservation_gym_name(rs.getString("GYM_NAME"));
				datas.add(data);
				rsCnt++;
			}

		}catch(SQLException e) {
			System.err.println("reservation.reservationDAO.selectAll SQL문 실패");
			return datas;
		}finally {
			JDBCUtil.disconnect(pstmt,conn);
		}
		System.out.println("reservation.reservationDAO.selectAll 성공");
		return datas;

	}
}
