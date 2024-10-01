package controller.crew.join;

import java.util.ArrayList;

import controller.common.Action;
import controller.common.ActionForward;
import controller.function.LoginCheck;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.crew.CrewDAO;
import model.crew.CrewDTO;



public class CrewListPageAction implements Action{

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = new ActionForward();
		String path = "crewList.jsp"; // 마이페이지로
		boolean flagRedirect = false; // 포워드 방식

		/*
		 * 페이지네이션을 하기 위해 page_num을 view에게 받아옵니다.
		 * 페이지네이션 계산 후 
		 * 크루DAO와 DTO를 생성해서
		 * 크루 전체목록을 selectAll 컨디션 주고
		 * 크루 총 개수 selectOne 컨디션 (페이지네이션)
		 * model_crew_datas라는 ArrayList를 생성해서
		 * 뷰에게 전달
		 * model_crew_page_total과 page_num 또한 뷰에게 전달
		 * 
		 */
		String login[] = LoginCheck.Success(request, response);
		String member_id = login[0];
		//사용자 아이디
		if (member_id == null) {
			// 로그인 페이지로 전달
			path = "LOGINACTION.do";
			flagRedirect = true;
		} 
		else {
			int pageNum = 1; // 페이지 번호 초기화
			// 페이지네이션 부분
			if(request.getParameter("page") != null) {
				pageNum = Integer.parseInt(request.getParameter("page")); // 페이지 번호가 있을 경우 변환하여 저장
			}
			int boardSize = 5; // 한 페이지에 표시할 게시글 수 설정
			int minBoard = 1; // 최소 게시글 수 초기화
			int maxBoard = 1; // 최대 게시글 수 초기화

			// 페이지 번호에 따라 최소 및 최대 게시글 수 설정
			if(pageNum <= 1) {
				// 페이지 번호가 1 이하일 경우
				minBoard = 1; // 최소 게시글 번호를 1로 설정
				maxBoard = minBoard * boardSize; // 최대 게시글 번호 계산
			}
			else {
				// 페이지 번호가 2 이상일 경우
				minBoard = ((pageNum - 1) * boardSize) + 1; // 최소 게시글 번호 계산
				maxBoard = pageNum * boardSize; // 최대 게시글 번호 계산
			}


			int listNum = 0; // 게시글 총 개수를 저장할 변수 초기화
			CrewDAO crewDAO = new CrewDAO();
			CrewDTO crewDTO = new CrewDTO();

			crewDTO.setModel_crew_min_num(minBoard);
			crewDTO.setModel_crew_max_num(maxBoard);

			crewDTO.setModel_crew_condition("CREW_ALL");//크루 전체 목록 컨디션
			ArrayList<CrewDTO> model_crew_datas = crewDAO.selectAll(crewDTO);
			if(!model_crew_datas.isEmpty()) {
				System.out.println("CrewListPageAction 72"+model_crew_datas.get(0).getModel_crew_num());
			}
			
			CrewDTO crewCount = new CrewDTO();
			crewCount.setModel_crew_condition("CREW_ONE_COUNT");//크루 총개수 컨디션
			crewCount = crewDAO.selectOne(crewCount);
			listNum = crewCount.getModel_crew_total();
			System.out.println("CrewListPageAction 78 "+listNum);
			
			request.setAttribute("model_crew_datas", model_crew_datas);
//			System.out.println("CrewListPageAction 83 "+model_crew_datas.get(0));
//			System.out.println(model_crew_datas.get(1));
//			System.out.println(model_crew_datas.get(2));
//			System.out.println(model_crew_datas.get(3));
			request.setAttribute("model_crew_page_total", listNum);
			request.setAttribute("currentPage", pageNum); // 현재 페이지 번호
		}

		forward.setPath(path); // 이동할 페이지 설정
		forward.setRedirect(flagRedirect); // 페이지 이동 방식 설정 (포워드)

		return forward;


	}

}
