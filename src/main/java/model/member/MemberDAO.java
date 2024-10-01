package model.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.JDBCUtil;

public class MemberDAO {
	//아이디로 찾기 FIXME 관리자 권한 추가(char값 'T','F')
	private final String SEARCH_ID = "SELECT MEMBER_ID,MEMBER_PASSWORD,MEMBER_NAME,MEMBER_PHONE,MEMBER_PROFILE,MEMBER_REGISTRATION_DATE,MEMBER_CURRENT_POINT,MEMBER_TOTAL_POINT,MEMBER_CREW_NUM,MEMBER_CREW_JOIN_DATE,MEMBER_LOCATION,MEMBER_ROLE\r\n"
			+ "FROM MEMBER\r\n"
			+ "WHERE MEMBER_ID = ?";

	//아이디 비밀번호로 찾기
	private final String SEARCH_ID_PASSWORD="SELECT MEMBER_ID,MEMBER_PASSWORD,MEMBER_NAME,MEMBER_PHONE,MEMBER_PROFILE,MEMBER_REGISTRATION_DATE,MEMBER_CURRENT_POINT,MEMBER_TOTAL_POINT,MEMBER_CREW_NUM,MEMBER_CREW_JOIN_DATE,MEMBER_LOCATION,MEMBER_ROLE\r\n"
			+ "FROM MEMBER\r\n"
			+ "WHERE MEMBER_ID = ? AND MEMBER_PASSWORD=?";

	//크루에 속한 회원목록 조회
	private final String SEARCH_CREW="SELECT MEMBER_ID,MEMBER_PASSWORD,MEMBER_NAME,MEMBER_PHONE,MEMBER_PROFILE,MEMBER_REGISTRATION_DATE,MEMBER_CURRENT_POINT,MEMBER_TOTAL_POINT,MEMBER_CREW_NUM,MEMBER_CREW_JOIN_DATE,MEMBER_LOCATION,MEMBER_ROLE\r\n"
			+ "FROM MEMBER\r\n"
			+ "WHERE MEMBER_CREW_NUM = ?";

	//랭킹높은순으로 정렬 관리자 FIXME 관리자 권한이 아닌사람들만
	private final String SEARCH_RANK="SELECT MEMBER_ID,MEMBER_PASSWORD,MEMBER_NAME,MEMBER_PHONE,MEMBER_PROFILE,MEMBER_REGISTRATION_DATE,MEMBER_CURRENT_POINT,MEMBER_TOTAL_POINT,MEMBER_CREW_NUM,MEMBER_CREW_JOIN_DATE,MEMBER_LOCATION,MEMBER_ROLE\r\n"
			+ "FROM MEMBER\r\n"
			+ "WHERE MEMBER_ROLE='F'\r\n"
			+ "ORDER BY MEMBER_TOTAL_POINT DESC";

	//회원가입
	private final String INSERT="INSERT INTO MEMBER(MEMBER_ID,MEMBER_NAME,MEMBER_PASSWORD,MEMBER_PHONE,MEMBER_LOCATION) \r\n"
			+ "VALUES(?,?,?,?,?)";

	//회원탈퇴
	private final String DELETE="DELETE FROM MEMBER WHERE MEMBER_ID = ?";

	//회원정보 업데이트 MEMBER_PASSWORD, MEMBER_PROFILE, MEMBER_PHONE, MEMBER_LOCATION, MEMBER_ID
	private final String UPDATE_ALL="UPDATE MEMBER\r\n"
			+ "SET\r\n"
			+ "	MEMBER_PASSWORD = ?,\r\n"
			+ "	MEMBER_PROFILE = ?,\r\n"
			+ "	MEMBER_PHONE = ?,\r\n"
			+ "	MEMBER_LOCATION = ?\r\n"
			+ "WHERE MEMBER_ID = ?";

	//회원정보 업데이트 (profile X) MEMBER_PASSWORD, MEMBER_PHONE, MEMBER_LOCATION, MEMBER_ID
	private final String UPDATE_WITHOUT_PROFILE="UPDATE MEMBER\r\n"
			+ "SET\r\n"
			+ "	MEMBER_PASSWORD = ?,\r\n"
			+ "	MEMBER_PHONE = ?,\r\n"
			+ "	MEMBER_LOCATION = ?\r\n"
			+ "WHERE MEMBER_ID = ?";

	//크루가입 (크루가입시 가입날짜입력때문에 분리)
	private final String UPDATE_CREW = "UPDATE MEMBER\r\n" //FIXME 관리자 권한이 아닌 사람들만
			+ "SET\r\n"
			+ "MEMBER_CREW_NUM = ?,\r\n"
			+ "MEMBER_CREW_JOIN_DATE = SYSDATE\r\n"
			+ "WHERE MEMBER_ID = ? AND MEMBER_ROLE='F'";

	//관리자 권한 변경 ROLE, ID
	private final String UPDATE_ADMIN ="UPDATE MEMBER SET MEMBER_ROLE = '?' WHERE MEMBER_ID = '?'";

	//관리자가 아닌 신규회원 출력 (기간 7일)
	private final String ALL_NEW ="SELECT MEMBER_ID,MEMBER_PASSWORD,MEMBER_NAME,MEMBER_PHONE,MEMBER_PROFILE,MEMBER_REGISTRATION_DATE,MEMBER_CURRENT_POINT,MEMBER_TOTAL_POINT,MEMBER_CREW_NUM,MEMBER_CREW_JOIN_DATE,MEMBER_LOCATION,MEMBER_ROLE\r\n"
			+ "FROM MEMBER\r\n"
			+ "WHERE MEMBER_REGISTRATION_DATE >= SYSDATE - INTERVAL '7' DAY AND MEMBER_ROLE='F'";
	
	//크루 랭킹 상위 10개 조회
	private final String ALL_TOP10_CREW_RANK = "SELECT\r\n"
			+ "    ROWNUM AS RANKING,\r\n"
			+ "    SUB.CREW_PROFILE,\r\n"
			+ "    SUB.CREW_NAME,\r\n"
			+ "    MEMBER_CREW_RANK\r\n"
			+ "FROM (\r\n"
			+ "    SELECT\r\n"
			+ "        C.CREW_PROFILE,\r\n"
			+ "        C.CREW_NAME,\r\n"
			+ "        SUM(M.MEMBER_TOTAL_POINT) AS MEMBER_CREW_RANK\r\n"
			+ "    FROM\r\n"
			+ "        MEMBER M\r\n"
			+ "    JOIN\r\n"
			+ "        CREW C \r\n"
			+ "    ON \r\n"
			+ "        M.MEMBER_CREW_NUM = C.CREW_NUM\r\n"
			+ "    GROUP BY\r\n"
			+ "        C.CREW_PROFILE,\r\n"
			+ "        C.CREW_NAME\r\n"
			+ "    ORDER BY\r\n"
			+ "        MEMBER_CREW_RANK DESC\r\n"
			+ ") SUB\r\n"
			+ "WHERE ROWNUM <= 10";
	
	//상위 개인 랭킹 10개
	private final String ALL_TOP10_RANK = "SELECT\r\n"
			+ "    MEMBER_NAME,\r\n"
			+ "    MEMBER_PROFILE\r\n"
			+ "FROM (\r\n"
			+ "    SELECT\r\n"
			+ "        MEMBER_NAME,\r\n"
			+ "        MEMBER_PROFILE\r\n"
			+ "    FROM\r\n"
			+ "        MEMBER\r\n"
			+ "    ORDER BY\r\n"
			+ "        MEMBER_TOTAL_POINT DESC\r\n"
			+ ")\r\n"
			+ "WHERE\r\n"
			+ "    ROWNUM <= 10";
	
	//특정 크루에 속한 사용자 이름 전부 조회 CREW_NUM
	private final String ALL_SEARCH_CREW_MEMBER_NAME = "SELECT MEMBER_NAME FROM MEMBER M JOIN CREW C ON C.CREW_NUM = M.MEMBER_CREW_NUM WHERE CREW_NUM = ?";
	
	//특정 사용자가 속한 크루 찾기 MEMBER_ID
	private final String SEARCH_MY_CREW = "SELECT\r\n"
			+ "	M.MEMBER_CREW_NUM\r\n"
			+ "FROM\r\n"
			+ "	MEMBER M\r\n"
			+ "JOIN\r\n"
			+ "	CREW C\r\n"
			+ "ON\r\n"
			+ "	M.MEMBER_CREW_NUM = C.CREW_NUM\r\n"
			+ "WHERE\r\n"
			+ "	MEMBER_ID = ?";
	
	//크루 랭킹 전체 출력
	private final String ALL_CREW_RANK = "SELECT\r\n"
			+ "	C.CREW_NUM,\r\n"
			+ "    C.CREW_NAME,\r\n"
			+ "    C.CREW_LEADER,\r\n"
			+ "    C.CREW_MAX_MEMBER_SIZE,\r\n"
			+ "    COUNT(M.MEMBER_ID) AS CREW_CURRENT_SIZE,\r\n"
			+ "    SUM(M.MEMBER_TOTAL_POINT) AS MEMBER_TOTAL_POINT\r\n"
			+ "FROM\r\n"
			+ "    CREW C\r\n"
			+ "JOIN\r\n"
			+ "    MEMBER M \r\n"
			+ "ON \r\n"
			+ "    M.MEMBER_CREW_NUM = C.CREW_NUM\r\n"
			+ "GROUP BY\r\n"
			+ "	C.CREW_NUM,\r\n"
			+ "    C.CREW_NAME,\r\n"
			+ "    C.CREW_LEADER,\r\n"
			+ "    C.CREW_MAX_MEMBER_SIZE\r\n"
			+ "ORDER BY\r\n"
			+ "    MEMBER_TOTAL_POINT DESC";
	
	//사용자 포인트 업데이트 MEMBER_CURRENT_POINT, MEMBER_ID
	private final String UPDATE_CURRENT_POINT = "UPDATE MEMBER SET MEMBER_CURRENT_POINT = ? WHERE MEMBER_ID = ?";
	public boolean insert(MemberDTO memberDTO) {
		System.out.println("member.MemberDAO.insert 시작");
		Connection conn=JDBCUtil.connect();
		PreparedStatement pstmt=null;
		try {
			//회원가입 MEMBER_ID, MEMBER_NAME, MEMBER_PASSWORD, MEMBER_PHONE, MEMBER_LOCATION
			pstmt=conn.prepareStatement(INSERT);
			pstmt.setString(1, memberDTO.getModel_member_id());
			pstmt.setString(2, memberDTO.getModel_member_name());
			pstmt.setString(3, memberDTO.getModel_member_password());
			pstmt.setString(4, memberDTO.getModel_member_phone());
			pstmt.setString(5, memberDTO.getModel_member_location());
			int rs = pstmt.executeUpdate();
			if(rs<=0) {
				System.err.println("member.MemberDAO.insert 실패");
				return false;
			}

		} catch (SQLException e) {
			System.out.println("member.MemberDAO.insert SQL문 실패");
			return false;
		}finally {
			JDBCUtil.disconnect(pstmt,conn);
		}
		System.out.println("member.MemberDAO.insert 성공");
		return true;
	}
	public boolean update(MemberDTO memberDTO) {
		System.out.println("member.MemberDAO.update 시작");
		Connection conn=JDBCUtil.connect();
		PreparedStatement pstmt=null;
		try {
			//회원정보 업데이트 MEMBER_PASSWORD, MEMBER_PROFILE, MEMBER_PHONE, MEMBER_LOCATION, MEMBER_ID
			if(memberDTO.getModel_member_condition().equals("MEMBER_UPDATE_ALL")) {
				pstmt=conn.prepareStatement(UPDATE_ALL);
				pstmt.setString(1, memberDTO.getModel_member_password());
				pstmt.setString(2, memberDTO.getModel_member_profile());
				pstmt.setString(3, memberDTO.getModel_member_phone());
				pstmt.setString(4, memberDTO.getModel_member_location());
				pstmt.setString(5, memberDTO.getModel_member_id());
			}
			//회원정보 업데이트 (profile X) MEMBER_PASSWORD, MEMBER_PHONE, MEMBER_LOCATION, MEMBER_ID
			else if(memberDTO.getModel_member_condition().equals("MEMBER_UPDATE_WITHOUT_PROFILE")) {
				pstmt=conn.prepareStatement(UPDATE_WITHOUT_PROFILE);
				pstmt.setString(1, memberDTO.getModel_member_password());
				pstmt.setString(2, memberDTO.getModel_member_phone());
				pstmt.setString(3, memberDTO.getModel_member_location());
				pstmt.setString(4, memberDTO.getModel_member_id());
			}
			//크루가입 MEMBER_CREW_NUM, MEMBER_ID
			else if(memberDTO.getModel_member_condition().equals("MEMBER_UPDATE_CREW")) {
				pstmt=conn.prepareStatement(UPDATE_CREW);
				pstmt.setInt(1, memberDTO.getModel_member_crew_num());
				pstmt.setString(2, memberDTO.getModel_member_id());
			}
			//관리자 권한 변경 MEMBER_ROLE, MEMBER_ID
			else if(memberDTO.getModel_member_condition().equals("MEMBER_UPDATE_ADMIN")) {
				pstmt=conn.prepareStatement(UPDATE_ADMIN);
				pstmt.setString(1, memberDTO.getModel_member_role());
				pstmt.setString(2, memberDTO.getModel_member_id());
			}
			//사용자 포인트 업데이트 MEMBER_CURRENT_POINT, MEMBER_ID
			else if(memberDTO.getModel_member_condition().equals("MEMBER_UPDATE_CURRENT_POINT")) {
				pstmt=conn.prepareStatement(UPDATE_CURRENT_POINT);
				pstmt.setInt(1, memberDTO.getModel_member_current_point());
				pstmt.setString(2, memberDTO.getModel_member_id());
			}
			else {
				System.err.println("condition 틀림");
				return false;
			}
			int rs = pstmt.executeUpdate();
			if(rs<=0) {
				System.err.println("member.MemberDAO.update 실패");
				return false;
			}
		} catch (SQLException e) {
			System.err.println("member.MemberDAO.update SQL문 실패");
			return false;
		}finally {
			JDBCUtil.disconnect(pstmt,conn);
		}
		System.out.println("member.MemberDAO.update 성공");
		return true;
	}
	public boolean delete(MemberDTO memberDTO) {
		System.out.println("member.MemberDAO.delete 시작");
		Connection conn=JDBCUtil.connect();
		PreparedStatement pstmt=null;
		try {
			//회원탈퇴 MEMBER_ID
			pstmt=conn.prepareStatement(DELETE);
			pstmt.setString(1, memberDTO.getModel_member_id());
			int rs = pstmt.executeUpdate();
			if(rs<=0) {
				System.err.println("member.MemberDAO.delete 실패");
				return false;
			}

		} catch (SQLException e) {
			System.err.println("member.MemberDAO.delete SQL문 실패");
			return false;
		}finally {
			JDBCUtil.disconnect(pstmt,conn);
		}
		System.out.println("member.MemberDAO.delete 성공");
		return true;
	}
	public MemberDTO selectOne(MemberDTO memberDTO) {
		System.out.println("member.MemberDAO.selectOne 시작");
		MemberDTO data = null;
		Connection conn=JDBCUtil.connect();
		PreparedStatement pstmt=null;
		try {
			//아이디로 검색 MEMBER_ID
			if(memberDTO.getModel_member_condition().equals("MEMBER_SEARCH_ID")) {
				pstmt=conn.prepareStatement(SEARCH_ID);
				pstmt.setString(1, memberDTO.getModel_member_id());
			}
			//아이디,비밀번호로 검색 MEMBER_ID, MEMBER_PASSWORD
			else if(memberDTO.getModel_member_condition().equals("MEMBER_SEARCH_ID_PASSWORD")) {
				pstmt=conn.prepareStatement(SEARCH_ID_PASSWORD);
				pstmt.setString(1, memberDTO.getModel_member_id());
				pstmt.setString(2, memberDTO.getModel_member_password());
			}
			//특정 사용자가 속한 크루 찾기 MEMBER_ID
			else if(memberDTO.getModel_member_condition().equals("MEMBER_SEARCH_MY_CREW")) {
				pstmt=conn.prepareStatement(SEARCH_MY_CREW);
				pstmt.setString(1, memberDTO.getModel_member_id());
			}
			else {
				System.err.println("condition 틀림");
				return null;
			}
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				System.out.println("memberDAO.selectOne 맞는 데이터 존재함");
				data = new MemberDTO();
				data.setModel_member_id(rs.getString("MEMBER_ID"));
				data.setModel_member_name(rs.getString("MEMBER_NAME"));
				data.setModel_member_password(rs.getString("MEMBER_PASSWORD"));
				data.setModel_member_phone(rs.getString("MEMBER_PHONE"));
				data.setModel_member_registration_date(rs.getDate("MEMBER_REGISTRATION_DATE"));
				data.setModel_member_profile(rs.getString("MEMBER_PROFILE"));
				data.setModel_member_current_point(rs.getInt("MEMBER_CURRENT_POINT"));
				data.setModel_member_total_point(rs.getInt("MEMBER_TOTAL_POINT"));
				data.setModel_member_crew_num(rs.getInt("MEMBER_CREW_NUM"));
				data.setModel_member_crew_join_date(rs.getString("MEMBER_CREW_JOIN_DATE"));
				data.setModel_member_location(rs.getString("MEMBER_LOCATION"));
				data.setModel_member_role(rs.getString("MEMBER_ROLE"));
				//FIXME role추가 String값, 'T','F'만 가능
			}
		} catch (SQLException e) {
			System.err.println("member.MemberDAO.selectOne SQL문 실패");
			return null;
		}finally {
			JDBCUtil.disconnect(pstmt,conn);
		}
		System.out.println("member.MemberDAO.selectOne 성공");
		return data;
	}
	public ArrayList<MemberDTO> selectAll(MemberDTO memberDTO) {
		System.out.println("member.MemberDAO.selectAll 시작");
		ArrayList<MemberDTO> datas = new ArrayList<MemberDTO>();
		MemberDTO data = null;
		int rsCnt=1;//로그용
		Connection conn=JDBCUtil.connect();
		PreparedStatement pstmt=null;
		try {
			//랭킹높은순으로 정렬
			if(memberDTO.getModel_member_condition().equals("MEMBER_SEARCH_RANK")) {
				pstmt=conn.prepareStatement(SEARCH_RANK);
			}
			//크루 랭킹 높은순으로 전체 출력
			else if(memberDTO.getModel_member_condition().equals("MEMBER_ALL_CREW_RANK")) {
				pstmt=conn.prepareStatement(ALL_CREW_RANK);
			}
			//크루에 속한 회원목록 조회 MEMBER_CREW_NUM
			else if(memberDTO.getModel_member_condition().equals("MEMBER_SEARCH_CREW")) {
				pstmt=conn.prepareStatement(SEARCH_CREW);
				pstmt.setInt(1, memberDTO.getModel_member_crew_num());
			}
			//관리자가 아닌 신규회원 출력 (기간 7일)
			else if(memberDTO.getModel_member_condition().equals("MEMBER_ALL_NEW")) {
				pstmt=conn.prepareStatement(ALL_NEW);
			}
			//크루 랭킹 상위 10개
			else if(memberDTO.getModel_member_condition().equals("MEMBER_ALL_TOP10_CREW_RANK")) {
				pstmt=conn.prepareStatement(ALL_TOP10_CREW_RANK);
			}
			//개인 랭킹 상위  10개
			else if(memberDTO.getModel_member_condition().equals("MEMBER_ALL_TOP10_RANK")) {
				pstmt=conn.prepareStatement(ALL_TOP10_RANK);
			}
			//특정 크루에 속한 사용자 이름 전부 조회 CREW_NUM
			else if(memberDTO.getModel_member_condition().equals("MEMBER_ALL_SEARCH_CREW_MEMBER_NAME")) {
				pstmt=conn.prepareStatement(ALL_SEARCH_CREW_MEMBER_NAME);
				pstmt.setInt(1, memberDTO.getModel_member_crew_num());
			}
			else {
				System.err.println("condition 틀림");
				return datas;
			}
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				System.out.println(rsCnt+"번행 출력중..");
				data = new MemberDTO();
				try {
	                data.setModel_member_id(rs.getString("MEMBER_ID"));
	            } catch (SQLException e) {
	            	System.err.println("member_id = null");
	                data.setModel_member_id(null);
	            }
	            try {
	                data.setModel_member_name(rs.getString("MEMBER_NAME"));
	            } catch (SQLException e) {
	            	System.err.println("member_name = null");
	                data.setModel_member_name(null);
	            }
	            try {
	                data.setModel_member_password(rs.getString("MEMBER_PASSWORD"));
	            } catch (SQLException e) {
	            	System.err.println("member_password = null");
	                data.setModel_member_password(null);
	            }
	            try {
	                data.setModel_member_phone(rs.getString("MEMBER_PHONE"));
	            } catch (SQLException e) {
	            	System.err.println("member_phone = null");
	                data.setModel_member_phone(null);
	            }
	            try {
	                data.setModel_member_registration_date(rs.getDate("MEMBER_REGISTRATION_DATE"));
	            } catch (SQLException e) {
	            	System.err.println("member_registration_date = null");
	                data.setModel_member_registration_date(null);
	            }
	            try {
	                data.setModel_member_profile(rs.getString("MEMBER_PROFILE"));
	            } catch (SQLException e) {
	            	System.err.println("member_profile = null");
	                data.setModel_member_profile(null);
	            }
	            try {
	                data.setModel_member_current_point(rs.getInt("MEMBER_CURRENT_POINT"));
	            } catch (SQLException e) {
	            	System.err.println("member_current_point = null");
	                data.setModel_member_current_point(0);
	            }
	            try {
	                data.setModel_member_total_point(rs.getInt("MEMBER_TOTAL_POINT"));
	            } catch (SQLException e) {
	            	System.err.println("member_total_point = null");
	                data.setModel_member_total_point(0);
	            }
	            try {
	                data.setModel_member_crew_num(rs.getInt("MEMBER_CREW_NUM"));
	            } catch (SQLException e) {
	            	System.err.println("member_crew_num = null");
	                data.setModel_member_crew_num(0);
	            }
	            try {
	                data.setModel_member_crew_join_date(rs.getString("MEMBER_CREW_JOIN_DATE"));
	            } catch (SQLException e) {
	            	System.err.println("member_crew_join_date = null");
	                data.setModel_member_crew_join_date(null);
	            }
	            try {
	                data.setModel_member_location(rs.getString("MEMBER_LOCATION"));
	            } catch (SQLException e) {
	            	System.err.println("member_location = null");
	                data.setModel_member_location(null);
	            }
	            try {
	                data.setModel_member_role(rs.getString("MEMBER_ROLE"));
	            } catch (SQLException e) {
	            	System.err.println("member_role = null");
	                data.setModel_member_role(null);
	            }
	            // DTO 데이터 처리
	            try {
	                data.setModel_member_crew_name(rs.getString("CREW_NAME"));
	            } catch (SQLException e) {
	            	System.err.println("member_crew_name = null");
	                data.setModel_member_crew_name(null);
	            }
	            try {
	                data.setModel_member_crew_profile(rs.getString("CREW_PROFILE"));
	            } catch (SQLException e) {
	            	System.err.println("member_crew_profile = null");
	                data.setModel_member_crew_profile(null);
	            }
	            try {
	                data.setModel_member_crew_current_size(rs.getInt("CREW_CURRENT_SIZE"));
	            } catch (SQLException e) {
	            	System.err.println("member_current_size = null");
	                data.setModel_member_crew_current_size(0);
	            }
	            try {
	                data.setModel_member_crew_leader(rs.getString("CREW_LEADER"));
	            } catch (SQLException e) {
	            	System.err.println("member_crew_leader = null");
	                data.setModel_member_crew_leader(null);
	            }
				datas.add(data);
				rsCnt++;
			}

		} catch (SQLException e) {
			System.err.println("member.MemberDAO.selectAll SQL문 실패");
			return datas;
		}finally {
			JDBCUtil.disconnect(pstmt,conn);
		}
		System.out.println("member.MemberDAO.selectAll 성공");
		return datas;
	}
}
