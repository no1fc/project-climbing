package model.gym;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.JDBCUtil;

public class GymDAO {
	//(페이지 네이션) 암벽장 전체출력
	private String ALL = "SELECT \r\n"
			+ "   GYM_NUM, \r\n"
			+ "   GYM_NAME, \r\n"
			+ "   GYM_PROFILE, \r\n"
			+ "   GYM_DESCRIPTION, \r\n"
			+ "   GYM_LOCATION, \r\n"
			+ "   GYM_RESERVATION_CNT, \r\n"
			+ "   GYM_PRICE, \r\n"
			+ "   BATTLE_NUM, \r\n"
			+ "   BATTLE_GAME_DATE\r\n"
			+ "FROM \r\n"
			+ "   (\r\n"
			+ "   SELECT \r\n"
			+ "	  	GYM_NUM, \r\n"
			+ "	  	GYM_NAME, \r\n"
			+ "	  	GYM_PROFILE, \r\n"
			+ "	  	GYM_DESCRIPTION, \r\n"
			+ "	  	GYM_LOCATION, \r\n"
			+ "	  	GYM_RESERVATION_CNT, \r\n"
			+ "	  	GYM_PRICE, \r\n"
			+ "	  	BATTLE_NUM, \r\n"
			+ "	  	BATTLE_GAME_DATE,\r\n"
			+ "   		ROW_NUMBER() OVER (ORDER BY GYM_NUM) AS RN\r\n"
			+ "	FROM \r\n"
			+ "   		(\r\n"
			+ "   		SELECT\r\n"
			+ "	      G.GYM_NUM,\r\n"
			+ "	      G.GYM_NAME,\r\n"
			+ "	      G.GYM_PROFILE,\r\n"
			+ "	      G.GYM_DESCRIPTION,\r\n"
			+ "	      G.GYM_LOCATION,\r\n"
			+ "	      G.GYM_RESERVATION_CNT,\r\n"
			+ "	      G.GYM_PRICE,\r\n"
			+ "	      B.BATTLE_NUM,\r\n"
			+ "	      B.BATTLE_GAME_DATE,\r\n"
			+ "	      ROW_NUMBER() OVER (PARTITION BY G.GYM_NAME ORDER BY G.GYM_NUM) AS RN_G,  -- GYM_NAME별로 순번 부여\r\n"
			+ "	      ROW_NUMBER() OVER (ORDER BY G.GYM_NUM) AS ROW_INDEX\r\n"
			+ "   		FROM\r\n"
			+ "      		GYM G\r\n"
			+ "   		LEFT JOIN\r\n"
			+ "      		BATTLE B\r\n"
			+ "   		ON\r\n"
			+ "      		G.GYM_NUM = B.BATTLE_GYM_NUM\r\n"
			+ "   		ORDER BY ROW_INDEX\r\n"
			+ "      )GYM_BATTLE_CTE\r\n"
			+ "	WHERE RN_G = 1\r\n"
			+ "  	 )GYM_BATTLE\r\n"
			+ "WHERE \r\n"
			+ "   RN BETWEEN ? AND ?";

	//암벽장 총 개수
	private final String ONE_COUNT = "SELECT COUNT(*) AS GYM_TOTAL FROM GYM";
	
	//암벽장 PK로 검색 GYM_NUM
	private final String ONE = "SELECT\r\n"
			+ "	G.GYM_NUM,\r\n"
			+ "	G.GYM_NAME,\r\n"
			+ "	G.GYM_PROFILE,\r\n"
			+ "	G.GYM_DESCRIPTION,\r\n"
			+ "	G.GYM_LOCATION,\r\n"
			+ "	G.GYM_RESERVATION_CNT,\r\n"
			+ "	G.GYM_PRICE,\r\n"
			+ "	B.BATTLE_NUM,\r\n"
			+ "	B.BATTLE_GAME_DATE\r\n"
			+ "FROM\r\n"
			+ "	GYM G\r\n"
			+ "LEFT JOIN\r\n"
			+ "	BATTLE B\r\n"
			+ "ON\r\n"
			+ "	G.GYM_NUM = B.BATTLE_GYM_NUM\r\n"
			+ "WHERE\r\n"
			+ "	G.GYM_NUM = ?";
	
	//예약가능 개수 업데이트 GYM_RESERVATION_CNT, GYM_NUM
	private final String UPDATE_RESERVATION_CNT = "UPDATE GYM SET GYM_RESERVATION_CNT = ? WHERE GYM_NUM = ?";
	
	//암벽장 등록 GYM_NAME, GYM_PROFILE, GYM_DESCRIPTION, GYM_LOCATION
	private final String INSERT = "INSERT GYM(GYM_NUM,GYM_NAME,GYM_PROFILE,GYM_DESCRIPTION,GYM_LOCATION)\r\n"
			+ "VALUES ((SELECT NVL(MAX(GYM_NUM),0)+1 FROM GYM),?,?,?,?)";
	public boolean insert(GymDTO gymDTO) {
		System.out.println("gym.GymDAO.insert 시작");
		Connection conn=JDBCUtil.connect();
		PreparedStatement pstmt=null;
		try {
			//암벽장 등록 GYM_NAME, GYM_PROFILE, GYM_DESCRIPTION, GYM_LOCATION
			pstmt=conn.prepareStatement(INSERT);
			pstmt.setString(1, gymDTO.getModel_gym_name());
			pstmt.setString(2, gymDTO.getModel_gym_profile());
			pstmt.setString(3, gymDTO.getModel_gym_description());
			pstmt.setString(4, gymDTO.getModel_gym_location());
			int rs = pstmt.executeUpdate();
			if(rs<=0) {
				System.err.println("gym.GymDAO.insert 실패");
				return false;
			}

		} catch (SQLException e) {
			System.out.println("gym.GymDAO.insert SQL문 실패");
			return false;
		}finally {
			JDBCUtil.disconnect(pstmt,conn);
		}
		System.out.println("gym.GymDAO.insert 성공");
		return true;
	}
	public boolean update(GymDTO gymDTO) {
		System.out.println("gym.GymDAO.update 시작");
		Connection conn=JDBCUtil.connect();
		PreparedStatement pstmt=null;
		try {
			//예약가능 개수 업데이트 GYM_RESERVATION_CNT, GYM_NUM
			pstmt=conn.prepareStatement(UPDATE_RESERVATION_CNT);
			pstmt.setInt(1, gymDTO.getModel_gym_reservation_cnt());
			pstmt.setInt(2, gymDTO.getModel_gym_num());
			int rs = pstmt.executeUpdate();
			if(rs<=0) {
				System.err.println("gym.GymDAO.update 실패");
				return false;
			}

		} catch (SQLException e) {
			System.err.println("gym.GymDAO.update SQL문 실패");
			return false;
		}finally {
			JDBCUtil.disconnect(pstmt,conn);
		}
		System.out.println("gym.GymDAO.update 성공");
		return true;
	}
	public boolean delete(GymDTO gymDTO) {
		System.err.println("gym.GymDAO.delete 시작");
		Connection conn=JDBCUtil.connect();
		PreparedStatement pstmt=null;
		try {
			pstmt=conn.prepareStatement("");
			int rs = pstmt.executeUpdate();
			if(rs<=0) {
				System.err.println("gym.GymDAO.delete 실패");
				return false;
			}

		} catch (SQLException e) {
			System.err.println("gym.GymDAO.delete SQL문 실패");
			return false;
		}finally {
			JDBCUtil.disconnect(pstmt,conn);
		}
		System.out.println("gym.GymDAO.delete 성공");
		return true;
	}

	public GymDTO selectOne(GymDTO gymDTO){
		System.out.println("gym.GymDAO.selectOne 시작");
		GymDTO data = null;
		String sqlq; //쿼리수행결과 구분용 데이터
		Connection conn=JDBCUtil.connect();
		PreparedStatement pstmt=null;
		try {
			//암벽장 PK로 검색 GYM_NUM
			if(gymDTO.getModel_gym_condition().equals("GYM_ONE")) {
				pstmt=conn.prepareStatement(ONE);
				pstmt.setInt(1, gymDTO.getModel_gym_num());
				sqlq = "one";
			}
			//암벽장 총 개수
			else if(gymDTO.getModel_gym_condition().equals("GYM_ONE_COUNT")) {
				pstmt=conn.prepareStatement(ONE_COUNT);
				sqlq = "count";
			}
			else {
				System.err.println("condition 틀림");
				return null;
			}
			System.out.println("쿼리수행결과 구분용 데이터 = "+sqlq);
			ResultSet rs = pstmt.executeQuery();
			boolean flag = rs.next();
			if(flag && sqlq.equals("one")) {
				System.out.println("gym.GymDAO.selectOne 검색 성공");
				data = new GymDTO();
				data.setModel_gym_num(rs.getInt("GYM_NUM"));
				data.setModel_gym_name(rs.getString("GYM_NAME"));
				data.setModel_gym_profile(rs.getString("GYM_PROFILE"));
				data.setModel_gym_description(rs.getString("GYM_DESCRIPTION"));
				data.setModel_gym_location(rs.getString("GYM_LOCATION"));
				data.setModel_gym_reservation_cnt(rs.getInt("GYM_RESERVATION_CNT"));
				data.setModel_gym_price(rs.getString("GYM_PRICE"));
				data.setModel_gym_battle_num(rs.getInt("BATTLE_NUM"));
				data.setModel_gym_battle_game_date(rs.getString("BATTLE_GAME_DATE"));
			}
			else if(flag && sqlq.equals("count")) {
				System.out.println("gym.GymDAO.selectOne 검색 성공");
				data = new GymDTO();
				data.setModel_gym_total(rs.getInt("GYM_TOTAL"));
			}
		} catch (SQLException e) {
			System.err.println("gym.GymDAO.selectOne SQL문 실패");
			return null;
		}finally {
			JDBCUtil.disconnect(pstmt,conn);
		}
		System.out.println("gym.GymDAO.selectOne 성공");
		return data;
	}

	public ArrayList<GymDTO> selectAll(GymDTO gymDTO){
		System.out.println("gym.GymDAO.selectAll 시작");
		ArrayList<GymDTO> datas = new ArrayList<GymDTO>();
		int rsCnt=1;//로그용
		Connection conn = JDBCUtil.connect();
		PreparedStatement pstmt = null;
		try {
			//(페이지 네이션) 암벽장 전체출력
			pstmt=conn.prepareStatement(ALL);
			pstmt.setInt(1, gymDTO.getModel_gym_min_num());
			pstmt.setInt(2, gymDTO.getModel_gym_max_num());
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				System.out.println(rsCnt+"번행 출력중...");
				GymDTO data = new GymDTO();
				try {
	                data.setModel_gym_num(rs.getInt("GYM_NUM"));
	            } catch (SQLException e) {
	                System.err.println("gym_num = null");
	                data.setModel_gym_num(0);
	            }
	            try {
	                data.setModel_gym_name(rs.getString("GYM_NAME"));
	            } catch (SQLException e) {
	                System.err.println("gym_name = null");
	                data.setModel_gym_name(null);
	            }
	            try {
	                data.setModel_gym_profile(rs.getString("GYM_PROFILE"));
	            } catch (SQLException e) {
	                System.err.println("gym_profile = null");
	                data.setModel_gym_profile(null);
	            }
	            try {
	                data.setModel_gym_description(rs.getString("GYM_DESCRIPTION"));
	            } catch (SQLException e) {
	                System.err.println("gym_description = null");
	                data.setModel_gym_description(null);
	            }
	            try {
	                data.setModel_gym_location(rs.getString("GYM_LOCATION"));
	            } catch (SQLException e) {
	                System.err.println("gym_location = null");
	                data.setModel_gym_location(null);
	            }
	            try {
	                data.setModel_gym_reservation_cnt(rs.getInt("GYM_RESERVATION_CNT"));
	            } catch (SQLException e) {
	                System.err.println("gym_reservation_cnt = null");
	                data.setModel_gym_reservation_cnt(0);
	            }
	            try {
	                data.setModel_gym_price(rs.getString("GYM_PRICE"));
	            } catch (SQLException e) {
	                System.err.println("gym_price = null");
	                data.setModel_gym_price(null);
	            }
	            try {
	                data.setModel_gym_battle_num(rs.getInt("BATTLE_NUM"));
	            } catch (SQLException e) {
	                System.err.println("gym_battle_num = null");
	                data.setModel_gym_battle_num(0);
	            }
	            try {
	                data.setModel_gym_battle_game_date(rs.getString("BATTLE_GAME_DATE"));
	            } catch (SQLException e) {
	                System.err.println("gym_battle_game_date = null");
	                data.setModel_gym_battle_game_date(null);
	            }
				datas.add(data);
				rsCnt++;
			}

		}catch(SQLException e) {
			System.err.println("gym.GymDAO.selectAll SQL문 실패");
			return datas;
		}finally {
			JDBCUtil.disconnect(pstmt,conn);
		}
		System.out.println("gym.GymDAO.selectAll 성공");
		return datas;

	}
}
