package model.battle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.JDBCUtil;

public class BattleDAO {

	//(페이지네이션)활성화 되있는 크루전 전체 출력 내림차순 model_battle_min_num, model_battle_max_num
	private final String ALL_ACTIVE = "SELECT \r\n"
			+ "    BATTLE_NUM,\r\n"
			+ "    BATTLE_GYM_NAME,\r\n"
			+ "    BATTLE_REGISTRATION_DATE,\r\n"
			+ "    GYM_LOCATION,\r\n"
			+ "    BATTLE_GAME_DATE,\r\n"
			+ "    GYM_PROFILE\r\n"
			+ "FROM (\r\n"
			+ "    SELECT \r\n"
			+ "        BATTLE_NUM,\r\n"
			+ "        GYM_NAME AS BATTLE_GYM_NAME,\r\n"
			+ "        BATTLE_REGISTRATION_DATE,\r\n"
			+ "        GYM_LOCATION,\r\n"
			+ "        BATTLE_GAME_DATE,\r\n"
			+ "        GYM_PROFILE,\r\n"
			+ "        ROW_NUMBER() OVER (ORDER BY BATTLE_NUM DESC) AS RN\r\n"
			+ "    FROM (\r\n"
			+ "        SELECT DISTINCT\r\n"
			+ "            BATTLE_NUM,\r\n"
			+ "            GYM_NAME,\r\n"
			+ "            BATTLE_REGISTRATION_DATE,\r\n"
			+ "            GYM_LOCATION,\r\n"
			+ "            BATTLE_GAME_DATE,\r\n"
			+ "            GYM_PROFILE\r\n"
			+ "        FROM \r\n"
			+ "            BATTLE B\r\n"
			+ "        JOIN \r\n"
			+ "            GYM G\r\n"
			+ "        ON \r\n"
			+ "            B.BATTLE_GYM_NUM = G.GYM_NUM\r\n"
			+ "        JOIN\r\n"
			+ "            BATTLE_RECORD BR\r\n"
			+ "        ON\r\n"
			+ "            B.BATTLE_NUM = BR.BATTLE_RECORD_BATTLE_NUM\r\n"
			+ "        WHERE\r\n"
			+ "            BR.BATTLE_RECORD_MVP_ID IS NULL\r\n"
			+ "    )\r\n"
			+ ")\r\n"
			+ "WHERE RN BETWEEN ? AND ?";

	//특정 사용자가 참여한 크루전 찾기 BATTLE_RECORD_CREW_NUM
	private final String SEARCH_MEMBER_BATTLE = "SELECT\r\n"
			+ "			B.BATTLE_NUM,\r\n"
			+ "			G.GYM_NAME,\r\n"
			+ "			B.BATTLE_GAME_DATE,\r\n"
			+ "			G.GYM_LOCATION,\r\n"
			+ "			G.GYM_PROFILE\r\n"
			+ "			FROM\r\n"
			+ "			BATTLE B\r\n"
			+ "			JOIN\r\n"
			+ "			GYM G\r\n"
			+ "			ON\r\n"
			+ "			B.BATTLE_GYM_NUM = G.GYM_NUM\r\n"
			+ "			JOIN\r\n"
			+ "			BATTLE_RECORD BR\r\n"
			+ "			ON\r\n"
			+ "			BR.BATTLE_RECORD_BATTLE_NUM = B.BATTLE_NUM\r\n"
			+ "			WHERE\r\n"
			+ "			BR.BATTLE_RECORD_CREW_NUM = ? AND\r\n"
			+ "			B.BATTLE_GAME_DATE > (SELECT SYSDATE FROM DUAL)";
	
	//활성화 되있는 크루전 총 개수
	private final String ONE_COUNT_ACTIVE = "SELECT COUNT(DISTINCT B.BATTLE_NUM) AS BATTLE_TOTAL\r\n"
			+ "FROM\r\n"
			+ "	BATTLE B\r\n"
			+ "JOIN\r\n"
			+ "	BATTLE_RECORD BR\r\n"
			+ "ON\r\n"
			+ "	B.BATTLE_NUM = BR.BATTLE_RECORD_BATTLE_NUM\r\n"
			+ "WHERE\r\n"
			+ "	BR.BATTLE_RECORD_MVP_ID IS NULL";

	//활성화 되있는 크루전 총 개수
	private final String ONE_SEARCH_BATTLE = "SELECT\r\n"
			+ "					BATTLE_NUM\r\n"
			+ "					BATTLE_GYM_NUM,\r\n"
			+ "					BATTLE_GAME_DATE\r\n"
			+ "					FROM\r\n"
			+ "						BATTLE\r\n"
			+ "					WHERE\r\n"
			+ "					BATTLE_NUM = ? and\r\n"
			+ "					(BATTLE_GAME_DATE > (SELECT SYSDATE FROM DUAL) OR\r\n"
			+ "					BATTLE_GAME_DATE IS NULL)";
	
	//해당 암벽장에서 실행된 크루전 전부 출력 BATTLE_GYM_NUM
	private final String ALL_GYM_BATTLE = "SELECT\r\n"
			+ "	BATTLE_NUM,\r\n"
			+ "	BATTLE_GYM_NUM,\r\n"
			+ "	BATTLE_GAME_DATE\r\n"
			+ "FROM\r\n"
			+ "	BATTLE B\r\n"
			+ "JOIN\r\n"
			+ "	GYM G\r\n"
			+ "ON\r\n"
			+ "	B.BATTLE_GYM_NUM = G.GYM_NUM\r\n"
			+ "WHERE\r\n"
			+ "	B.BATTLE_GYM_NUM = ?";
	
	//크루전 추가 BATTLE_GYM_NUM, BATTLE_GAME_DATE
	private final String INSERT = "INSERT INTO BATTLE(BATTLE_NUM,BATTLE_GYM_NUM,BATTLE_GAME_DATE)\r\n"
			+ "VALUES ((SELECT NVL(MAX(BATTLE_NUM),0)+1 FROM BATTLE),?,?)";
	
	//크루전 최신 4개만 출력
	private final String ALL_TOP4 = "SELECT \r\n"
			+ "    B.BATTLE_NUM,\r\n"
			+ "    B.BATTLE_GYM_NUM,\r\n"
			+ "    B.BATTLE_REGISTRATION_DATE,\r\n"
			+ "    B.BATTLE_GAME_DATE\r\n"
			+ "FROM (\r\n"
			+ "    SELECT \r\n"
			+ "        B.BATTLE_NUM,\r\n"
			+ "        B.BATTLE_GYM_NUM,\r\n"
			+ "        B.BATTLE_REGISTRATION_DATE,\r\n"
			+ "        B.BATTLE_GAME_DATE\r\n"
			+ "    FROM \r\n"
			+ "        BATTLE B\r\n"
			+ "    ORDER BY \r\n"
			+ "        BATTLE_NUM DESC\r\n"
			+ ") B\r\n"
			+ "WHERE ROWNUM <= 4";
	
	//게임날짜 업데이트 BATTLE_GAME_DATE, BATTLE_NUM
	private final String UPDATE = "UPDATE BATTLE SET BATTLE_GAME_DATE = ? WHERE BATTLE_NUM = ?";
	public boolean insert(BattleDTO battleDTO) {
		System.out.println("battle.BattleDAO.insert 시작");
		Connection conn=JDBCUtil.connect();
		PreparedStatement pstmt=null;
		try {
			pstmt=conn.prepareStatement(INSERT);
			pstmt.setInt(1, battleDTO.getModel_battle_gym_num());
			pstmt.setString(2, battleDTO.getModel_battle_game_date());
			int rs = pstmt.executeUpdate();
			if(rs<=0) {
				System.err.println("battle.BattleDAO.insert 실패");
				return false;
			}

		} catch (SQLException e) {
			System.out.println("battle.BattleDAO.insert SQL문 실패");
			return false;
		}finally {
			JDBCUtil.disconnect(pstmt,conn);
		}
		System.out.println("battle.BattleDAO.insert 성공");
		return true;
	}
	public boolean update(BattleDTO battleDTO) {
		System.out.println("battle.BattleDAO.update 시작");
		Connection conn=JDBCUtil.connect();
		PreparedStatement pstmt=null;
		try {
			//게임날짜 업데이트 BATTLE_GAME_DATE, BATTLE_NUM
			pstmt=conn.prepareStatement(UPDATE);
			pstmt.setString(1, battleDTO.getModel_battle_game_date());
			pstmt.setInt(2, battleDTO.getModel_battle_num());
			int rs = pstmt.executeUpdate();
			if(rs<=0) {
				System.err.println("battle.BattleDAO.update 실패");
				return false;
			}

		} catch (SQLException e) {
			System.err.println("battle.BattleDAO.update SQL문 실패");
			return false;
		}finally {
			JDBCUtil.disconnect(pstmt,conn);
		}
		System.out.println("battle.BattleDAO.update 성공");
		return true;
	}
	private boolean delete(BattleDTO battleDTO) {
		System.err.println("battle.BattleDAO.delete 시작");
		Connection conn=JDBCUtil.connect();
		PreparedStatement pstmt=null;
		try {
			pstmt=conn.prepareStatement("");
			int rs = pstmt.executeUpdate();
			if(rs<=0) {
				System.err.println("battle.BattleDAO.delete 실패");
				return false;
			}

		} catch (SQLException e) {
			System.err.println("battle.BattleDAO.delete SQL문 실패");
			return false;
		}finally {
			JDBCUtil.disconnect(pstmt,conn);
		}
		System.out.println("battle.BattleDAO.delete 성공");
		return true;
	}

	public BattleDTO selectOne(BattleDTO battleDTO){
		System.out.println("battle.BattleDAO.selectOne 시작");
		System.err.println("182 BattleDTO selectOne 컨디션 = "+battleDTO.getModel_battle_condition());
		BattleDTO data = null;
		String sqlq; // 쿼리수행결과 구분용 데이터
		Connection conn=JDBCUtil.connect();
		PreparedStatement pstmt=null;
		try {
			//특정 사용자가 참여한 크루전 찾기 BATTLE_RECORD_CREW_NUM
			if(battleDTO.getModel_battle_condition().equals("BATTLE_SEARCH_MEMBER_BATTLE")) {
				pstmt=conn.prepareStatement(SEARCH_MEMBER_BATTLE);
				pstmt.setInt(1, battleDTO.getModel_battle_crew_num());
				sqlq="one";
			}
			else if(battleDTO.getModel_battle_condition().equals("ONE_SEARCH_BATTLE")) {
				pstmt=conn.prepareStatement(ONE_SEARCH_BATTLE);
				pstmt.setInt(1, battleDTO.getModel_battle_num());
				sqlq="one";
			}
			//활성화 되있는 크루전 총 개수
			else if(battleDTO.getModel_battle_condition().equals("BATTLE_ONE_COUNT_ACTIVE")) {
				pstmt=conn.prepareStatement(ONE_COUNT_ACTIVE);
				sqlq="count";
			}
			else {
				System.err.println("condition 틀림");
				return null;
			}
			System.out.println("쿼리수행결과 구분용 데이터 = "+sqlq);
			ResultSet rs = pstmt.executeQuery();
			boolean flag = rs.next();
			if(flag && sqlq.equals("one")) {
				System.out.println("battle.BattleDAO.selectOne 검색 성공");
				data = new BattleDTO();
				try {
				    data.setModel_battle_num(rs.getInt("BATTLE_NUM"));
				} catch (SQLException e) {
				    data.setModel_battle_num(0);
				    System.err.println("BATTLE_NUM = 0");
				}
				try {
				    data.setModel_battle_gym_name(rs.getString("GYM_NAME"));
				} catch (SQLException e) {
				    data.setModel_battle_gym_name(null);
				    System.err.println("GYM_NAME = null");
				}
				try {
				    data.setModel_battle_gym_profile(rs.getString("GYM_PROFILE"));
				} catch (SQLException e) {
				    data.setModel_battle_gym_profile(null);
				    System.err.println("GYM_PROFILE = null");
				}
				try {
				    data.setModel_battle_gym_location(rs.getString("GYM_LOCATION"));
				} catch (SQLException e) {
				    data.setModel_battle_gym_location(null);
				    System.err.println("GYM_LOCATION = null");
				}
				try {
				    data.setModel_battle_registration_date(rs.getString("BATTLE_REGISTRATION_DATE"));
				} catch (SQLException e) {
				    data.setModel_battle_registration_date(null);
				    System.err.println("BATTLE_REGISTRATION_DATE = null");
				}
				try {
				    data.setModel_battle_game_date(rs.getString("BATTLE_GAME_DATE"));
				} catch (SQLException e) {
				    data.setModel_battle_game_date(null);
				    System.err.println("BATTLE_GAME_DATE = null");
				}
			}
			else if(flag && sqlq.equals("count")) {
				data = new BattleDTO();
				data.setModel_battle_total(rs.getInt("BATTLE_TOTAL"));
				System.err.println("count 결과 ="+data.getModel_battle_total());
			}
		} catch (SQLException e) {
			System.err.println("battle.BattleDAO.selectOne SQL문 실패");
			e.printStackTrace();
			return null;
		}finally {
			JDBCUtil.disconnect(pstmt,conn);
		}
		System.out.println("battle.BattleDAO.selectOne 성공");
		return data;
	}

	public ArrayList<BattleDTO> selectAll(BattleDTO battleDTO){
		System.out.println("battle.BattleDAO.selectAll 시작");
		ArrayList<BattleDTO> datas = new ArrayList<BattleDTO>();
		int rsCnt=1;//로그용
		Connection conn = JDBCUtil.connect();
		PreparedStatement pstmt = null;
		try {
			//(페이지네이션)활성화 되있는 크루전 전체 출력 내림차순 model_battle_min_num, model_battle_max_num
			if(battleDTO.getModel_battle_condition().equals("BATTLE_ALL_ACTIVE")) {
				System.out.println("270 DAO.selectAll 컨디션 ="+battleDTO.getModel_battle_condition());
				pstmt=conn.prepareStatement(ALL_ACTIVE);
				pstmt.setInt(1, battleDTO.getModel_battle_min_num());
				pstmt.setInt(2, battleDTO.getModel_battle_max_num());
			}
			//해당 암벽장에서 실행된 크루전 전부 출력 BATTLE_GYM_NUM
			else if(battleDTO.getModel_battle_condition().equals("BATTLE_ALL_GYM_BATTLE")) {
				System.out.println("277 DAO.selectAll 컨디션 ="+battleDTO.getModel_battle_condition());
				pstmt=conn.prepareStatement(ALL_GYM_BATTLE);
				pstmt.setInt(1, battleDTO.getModel_battle_gym_num());
			}
			//최신 크루전 4개만 출력
			else if(battleDTO.getModel_battle_condition().equals("BATTLE_ALL_TOP4")) {
				System.out.println("283 DAO.selectAll 컨디션 ="+battleDTO.getModel_battle_condition());
				pstmt=conn.prepareStatement(ALL_TOP4);
			}
			else {
				System.err.println("condition 틀림");
				return datas;
			}
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				System.out.println(rsCnt+"번행 출력중...");
				BattleDTO data = new BattleDTO();
				try {
			        data.setModel_battle_num(rs.getInt("BATTLE_NUM"));
			    } catch (SQLException e) {
			        System.err.println("battle_num = null ");
			        data.setModel_battle_num(0);
			    }
			    try {
			        data.setModel_battle_gym_name(rs.getString("BATTLE_GYM_NAME"));
			    } catch (SQLException e) {
			        System.err.println("battle_gym_name = null ");
			        data.setModel_battle_gym_name(null);
			    }
			    try {
			        data.setModel_battle_gym_profile(rs.getString("GYM_PROFILE"));
			    } catch (SQLException e) {
			    	System.err.println("battle_gym_profile = null ");
			        data.setModel_battle_gym_profile(null);
			    }
			    try {
			        data.setModel_battle_gym_location(rs.getString("GYM_LOCATION"));
			    } catch (SQLException e) {
			    	System.err.println("gym_location = null ");
			        data.setModel_battle_gym_location(null);
			    }
			    try {
			        data.setModel_battle_registration_date(rs.getString("BATTLE_REGISTRATION_DATE"));
			    } catch (SQLException e) {
			    	System.err.println("battle_registration_date = null ");
			        data.setModel_battle_registration_date(null);
			    }
			    try {
			        data.setModel_battle_game_date(rs.getString("BATTLE_GAME_DATE"));
			    } catch (SQLException e) {
			    	System.err.println("battle_game_date = null ");
			        data.setModel_battle_game_date(null);
			    }
			    datas.add(data);
				rsCnt++;
			}
		}catch(SQLException e) {
			System.err.println("battle.BattleDAO.selectAll SQL문 실패");
			e.printStackTrace();
			return datas;
		}finally {
			JDBCUtil.disconnect(pstmt,conn);
		}
		System.out.println("battle.BattleDAO.selectAll 성공");
		return datas;

	}
}
