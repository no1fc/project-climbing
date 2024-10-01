package controller.crew.community;

import java.util.ArrayList;

import controller.common.Action;
import controller.common.ActionForward;
import controller.function.LoginCheck;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.battle_record.Battle_recordDAO;
import model.battle_record.Battle_recordDTO;
import model.crew.CrewDAO;
import model.crew.CrewDTO;
import model.member.MemberDAO;
import model.member.MemberDTO;

public class CrewPageAction implements Action{

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = new ActionForward();
		String path = "myCrewPage.jsp";//크루가 있다는 것을 가정
		boolean flagRedirect = false;

		//로그인 정보 확인하고
		//크루 정보를 확인한다.
		//크루 정보를 확인한다면,
		//만약 크루가 있다면 크루마이페이지로
		//만약 없다면 크루가입페이지(CrewJoinPage.do)로 간다.

		//로그인 정보가 있는지 확인해주고
		String login[] = LoginCheck.Success(request, response);
		//사용자 아이디
		String member_id = login[0];
		//사용자 아이디
		if (member_id == null) {
			// 로그인 페이지로 전달
			path = "LOGINACTION.do";
			flagRedirect = true;
		} 
		else {
			//사용자 크루 정보
			int crew_num = Integer.parseInt(login[1]);
			System.out.println("현재 크루"+login[1]);
			//크루가 없다면
			if(crew_num<=0) {
				path = "CrewListPage.do";
				flagRedirect = true;
			}
			//크루가 있다면
			else {

				//자신의 크루의 크루원들의 정보를 위해 member selectAll
				//자신의 크루의 승리목록의 정보를 위해 battle selectAll
				CrewDTO crewDTO = new CrewDTO();
				CrewDAO crewDAO = new CrewDAO();
				crewDTO.setModel_crew_num(crew_num);//세션에 저장되어있는 로그인된 사용자의 크루pk
				System.out.println("57 CrewPageAction crew_num "+crew_num);
				
				crewDTO.setModel_crew_condition("CREW_ONE");//내 크루정보 셀렉원 컨디션
				crewDTO = crewDAO.selectOne(crewDTO);
				String filename = "";
				if (crewDTO == null) {//혹시모를 에러잡기 위해
					filename = "default.jpg"; // 디폴트(기본) 이미지

				} else {
					filename = crewDTO.getModel_crew_profile(); // 사용자의 프로필을 받아옴

				}
				crewDTO.setModel_crew_profile(request.getServletContext().getContextPath() + "/crew_img_folder/" + filename); 

				MemberDTO memberDTO = new MemberDTO();
				MemberDAO memberDAO = new MemberDAO();
				memberDTO.setModel_member_crew_num(crew_num);
				memberDTO.setModel_member_condition("MEMBER_ALL_SEARCH_CREW_MEMBER_NAME");//크루원 셀렉올 컨디션

				ArrayList<MemberDTO> model_member_crew_datas = memberDAO.selectAll(memberDTO);
				System.out.println("77 model_member_crew_datas 사이즈 = "+model_member_crew_datas.size());

				Battle_recordDTO battle_recordDTO = new Battle_recordDTO();
				Battle_recordDAO battle_recordDAO = new Battle_recordDAO();
				battle_recordDTO.setModel_battle_record_condition("BATTLE_RECORD_ALL_WINNER");//내 크루 승리목록 컨디션
				battle_recordDTO.setModel_battle_record_crew_num(crew_num);
				ArrayList<Battle_recordDTO> model_battle_record_datas = battle_recordDAO.selectAll(battle_recordDTO);
				int cnt = 1;
				for(Battle_recordDTO data : model_battle_record_datas) {
					System.err.println(cnt+"번째 CrewPageAction 내 크루 승리목록 정보 시작");
					System.out.println("크루전 pk = "+data.getModel_battle_record_battle_num());
					System.out.println("크루전기록 pk = "+data.getModel_battle_record_num());
					System.out.println("크루 pk = "+data.getModel_battle_record_crew_num());
					System.out.println("크루 이름 = "+data.getModel_battle_record_crew_name());
					System.out.println("암벽장 장소 = "+data.getModel_battle_record_gym_location());
					System.out.println("mvp = "+data.getModel_battle_record_mvp_id());
					System.err.println("CrewPageAction 내 크루 승리목록 정보 끝");
					cnt++;
				}
				request.setAttribute("CREW", crewDTO);
				request.setAttribute("model_member_crew_datas", model_member_crew_datas);
				request.setAttribute("model_battle_record_datas", model_battle_record_datas);



			}

		}
		forward.setPath(path);
		forward.setRedirect(flagRedirect);
		return forward;
	}
}
