package controller.gym;

import controller.common.Action;
import controller.common.ActionForward;
import controller.function.LoginCheck;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.battle.BattleDAO;
import model.battle.BattleDTO;
import model.battle_record.Battle_recordDAO;
import model.battle_record.Battle_recordDTO;
import model.crew.CrewDAO;
import model.crew.CrewDTO;

public class CrewBattleApplicationAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		String path = "info.jsp"; // view에서 알려줄 예정 alert 창 띄우기 위한 JavaScript 페이지
		boolean flag_Redirect = false; // 값을 전달해야하게 때문에 forward 방식으로 전달해야한다.
		ActionForward forward = new ActionForward();

		//로그인 정보가 있는지 확인해주고
		String login[] = LoginCheck.Success(request, response);
		//사용자 아이디
		String member_id = login[0];
		System.out.println("CrewBattleApplicationAction.java member_id 로그 : " + member_id);
		//가입 크루 번호
		int crew_check = 0;
		if(login[1] != null) {
			crew_check = Integer.parseInt(login[1]);			
		}
		
		System.out.println("CrewBattleApplicationAction.java crew_check 로그 : " + crew_check);
		
		//------------------------------------------------------------
		//해당 페이지에서 공통으로 사용할 변수 and 객체
		//View에서 전달해주는 (크루전 번호 / 크루전 게임일 / 암벽장 번호)변수
		int view_battle_num=0;
		System.out.println("CrewBattleApplicationAction.java view_battle_num 로그 : " + request.getParameter("VIEW_CREW_BATTLE_NUM"));
		if(request.getParameter("VIEW_CREW_BATTLE_NUM")!=null) {
			System.out.println("CrewBattleApplicationAction.java view_battle_num 로그 1 : " + view_battle_num);
			view_battle_num=Integer.parseInt(request.getParameter("VIEW_CREW_BATTLE_NUM"));
		}
		System.out.println("CrewBattleApplicationAction.java view_battle_num 로그 2 : " + view_battle_num);
		String view_battle_game_date = request.getParameter("VIEW_CREW_BATTLE_DATE");
		System.out.println("CrewBattleApplicationAction.java view_battle_game_date 로그 : " + view_battle_game_date);
		int view_gym_num = Integer.parseInt(request.getParameter("VIEW_CREW_BATTLE_GYM_NUM"));
		System.out.println("CrewBattleApplicationAction.java view_gym_num 로그 : " + view_gym_num);
		String error_message = "잘못된 요청";
		String error_page = "GymInformationPage.do?VIEW_GYM_NUM=" + view_gym_num;

		//Crew DTO DAO 객체
		CrewDTO crewDTO = new CrewDTO();
		CrewDAO crewDAO = new CrewDAO();

		//Battle DTO DAO 객체
		BattleDTO battleDTO = new BattleDTO();
		BattleDAO battleDAO = new BattleDAO();

		//Battle_record DTO DAO 객체
		Battle_recordDTO battle_recordDTO = new Battle_recordDTO();
		Battle_recordDAO battle_recordDAO = new Battle_recordDAO();
		//------------------------------------------------------------

		if(member_id != null) {
			//------------------------------------------------------------
			//암벽장에 크루전을 신청하는 사용자가 크루장인지 확인 로직 시작
			//(사용자 아이디 / 크루 번호) 를 Crew DTO에 추가합니다.
			crewDTO.setModel_crew_num(crew_check);
			crewDTO.setModel_crew_condition("CREW_ONE");
			//Crew selectOne으로 해당 사용자가 크루장인지 확인합니다.
			CrewDTO crew_leader = crewDAO.selectOne(crewDTO);		
			boolean flag_crew_leader = false;
			if(crew_leader!=null) {
				if(crew_leader.getModel_crew_leader().equals(member_id)){
						//해당 크루의 크루장이라면 true
						flag_crew_leader = true;
				}
			}
			System.out.println("CrewBattleApplicationAction.java flag_crew_leader 로그 : " + flag_crew_leader);
			
			//false 가 나오면 오류를 반환해줍니다.
			if(!flag_crew_leader) {
				System.out.println("CrewBattleApplicationAction.java flag_crew_leader false 로그");
				//flag_crew_leader = false : error_message 크루전은 크루장만 개최하실 수 있습니다.
				request.setAttribute("msg", "크루전은 크루장만 개최하실 수 있습니다.");
				request.setAttribute("path", error_page);
				forward.setPath(path);
				forward.setRedirect(flag_Redirect);		
				return forward;
			}
			
			//암벽장에 크루전을 신청하는 사용자가 크루장인지 확인 로직 종료
			//------------------------------------------------------------
			//크루전 개최 되어있는지 확인하기 위한 로직 시작
			//(크루전 번호) 을 Battle DTO에 추가합니다.
			battleDTO.setModel_battle_num(view_battle_num);
			battleDTO.setModel_battle_game_date(view_battle_game_date);
			battleDTO.setModel_battle_condition("ONE_SEARCH_BATTLE");//TODO 컨디션 추가해야함
			//Battle selectOne으로 해당 크루전이 개최되어 있는지 확인합니다.
			BattleDTO battle_data = battleDAO.selectOne(battleDTO);
			boolean flag = false;
			//개최되어 있다면 게임일을 확인해줍니다.
			if(battle_data != null){
				System.out.println("CrewBattleApplicationAction.java battle_data false 로그");
				flag = true;
				//만약 게임일이 없다면 : 게임일을 추가하기위해 Battle update 를 진행.
				if(battle_data.getModel_battle_game_date() == null) {
					flag = battleDAO.update(battleDTO);	
				}
			}			
			else if(battle_data == null) {
				//개최되어 있지 않은 크루전 번호라면
				//error_message : 크루전 개최에 실패하였습니다. (사유 : 없는 크루전)
				request.setAttribute("msg", "크루전 개최에 실패하였습니다. (사유 : 없는 크루전)");
				request.setAttribute("path", error_page);
				System.out.println("CrewBattleApplicationAction.java battle_data 로그 : "+error_page);
				forward.setPath(path);
				forward.setRedirect(flag_Redirect);
				return forward;
			}
			
			if(!flag) {
				//false: error_message 크루전 개최에 실패하였습니다. (사유 : 게임일 등록 실패)
				request.setAttribute("msg", "크루전 개최에 실패하였습니다. (사유 : 게임일 등록 실패)");
				request.setAttribute("path", error_page);
				forward.setPath(path);
				forward.setRedirect(flag_Redirect);		
				return forward;
			}
			//크루전 개최 되어있는지 확인하기 위한 로직 종료
			//------------------------------------------------------------
			//크루전 등록 로직 시작
			//(크루전 번호 / 크루 번호) 를 Battle_record DTO에 추가합니다.
			battle_recordDTO.setModel_battle_record_crew_num(crew_check);
			battle_recordDTO.setModel_battle_record_condition("BATTLE_RECORD_ONE_BATTLE_RECORD");
			
			//크루전 등록 여부 확인을 위해 selectOne 해서 비교한다.
			Battle_recordDTO battle_record_data=battle_recordDAO.selectOne(battle_recordDTO);
			if(battle_record_data!=null) {
				//false: error_message 크루전을 이미 등록했습니다. (사유 : 크루전 등록 중복)
				request.setAttribute("msg", "크루전을 이미 등록했습니다. (사유 : 크루전 등록 중복)");
				request.setAttribute("path", error_page);
				forward.setPath(path);
				forward.setRedirect(flag_Redirect);		
				return forward;
			}
			//model 의 Battle_record 에 Insert 해줍니다.
			boolean flag_battle_record = battle_recordDAO.insert(battle_recordDTO);
			//True : error_message 크루전 등록에 성공하였습니다.
			if(flag_battle_record) {
				error_message = "크루전 등록에 성공하였습니다.";
			}
			//false: error_message 크루전 등록에 실패하였습니다.
			else {
				error_message = "크루전 등록에 실패하였습니다.";
				
			}
			//크루전 등록 로직 종료
			//------------------------------------------------------------
		}
		else {
			error_message = "로그인 후 이용 가능합니다.";
			System.out.println("로그인 여부 로그 메시지 : " + error_message);
		}
		System.out.println("CrewBattleApplicationAction.java 최종 error_message 로그 : " + error_message);
		request.setAttribute("msg", error_message);
		System.out.println("CrewBattleApplicationAction.java 최종 error_page 로그 : " + error_page);
		request.setAttribute("path", error_page);
		forward.setPath(path);
		forward.setRedirect(flag_Redirect);		
		return forward;

	}

}
