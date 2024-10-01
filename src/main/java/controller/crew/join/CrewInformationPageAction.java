
package controller.crew.join;

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

public class CrewInformationPageAction implements Action{

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = new ActionForward();
		String path = "crewInformation.jsp"; //  페이지로 이동
		boolean flagRedirect = false; // 포워드 방식 사용 여부 설정 (false = forward 방식)
		/*
		 * 크루 상세보기 페이지
		 * 뷰에게 크루 번호를 받아옵니다
		 * crewDTO와 DAO를 생성해서 받아온 크루번호를 DTO에 set해서 
		 * crew selectOne 크루의 정보을 불러옵니다.
		 * memverDTO와 DAO를 생성해서 선택한 크루의 인원을
		 * selectOne해줍니다.
		 * model_battle_record_datas라는
		 * ArrayList를 생성해서 승리목록을 뷰에게 전달합니다.
		 * crewDTO(크루의 정보)또한 뷰에게 전달합니다.
		 */
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
			int view_crew_num = 0;
			
			if(request.getParameter("view_crew_num") !=null) {//null체크
				System.out.println("49 CrewInformationPageAction view_crew_num = "+request.getParameter("view_crew_num"));
			}//FIXME
			view_crew_num =	Integer.parseInt(request.getParameter("view_crew_num"));
			
			CrewDTO crewDTO = new CrewDTO();
			CrewDAO crewDAO = new CrewDAO();
			crewDTO.setModel_crew_num(view_crew_num);
			crewDTO.setModel_crew_condition("CREW_ONE_COUNT_CURRENT_MEMBER_SIZE");//선택 크루 인원 컨디션
			crewDTO = crewDAO.selectOne(crewDTO);
			System.out.println("57 CrewInformationPageAction crewDTO = "+crewDTO);
			String filename = "";

			if (crewDTO == null) {//혹시모를 에러잡기 위해
				filename = "default.jpg"; // 디폴트(기본) 이미지

			} else {
				filename = crewDTO.getModel_crew_profile(); // 사용자의 프로필을 받아옴

			}

			request.setAttribute("model_crew_profile", request.getServletContext().getContextPath() + "/crew_img_folder/" + filename);

			Battle_recordDTO battle_recordDTO = new Battle_recordDTO();
			Battle_recordDAO battle_recordDAO = new Battle_recordDAO();
			battle_recordDTO.setModel_battle_record_crew_num(view_crew_num);
			battle_recordDTO.setModel_battle_record_condition("BATTLE_RECORD_ALL_WINNER");//승리목록 컨디션
			ArrayList<Battle_recordDTO> model_battle_record_datas = battle_recordDAO.selectAll(battle_recordDTO);

			request.setAttribute("CREW", crewDTO);
			System.out.println("CREW로그 = "+ crewDTO);
			
			request.setAttribute("model_battle_record_datas", model_battle_record_datas);
			
		}
		forward.setPath(path);
		forward.setRedirect(flagRedirect);       
		return forward;
	}
}
