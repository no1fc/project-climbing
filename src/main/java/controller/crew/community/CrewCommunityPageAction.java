package controller.crew.community;

import java.util.ArrayList;

import controller.common.Action;
import controller.common.ActionForward;
import controller.function.LoginCheck;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.crew_board.Crew_boardDAO;
import model.crew_board.Crew_boardDTO;

public class CrewCommunityPageAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = new ActionForward();
		String path = "crewCommunity.jsp"; // 기본 페이지 경로
		boolean flagRedirect = false; // 포워드 방식 초기화

		// 로그인 체크
		String login[] = LoginCheck.Success(request, response);
		String member_id = login[0]; // 사용자 아이디

		if (member_id == null) {
			// 로그인하지 않은 경우 로그인 페이지로 리다이렉트
			path = "LOGINACTION.do";
			flagRedirect = true;
		} else {
			// 사용자 크루 정보
			int crew_num = Integer.parseInt(login[1]);

			if (crew_num <= 0) {
				// 크루가 없는 경우 크루 목록 페이지로 리다이렉트
				path = "CrewListPage.do";
				flagRedirect = true;
			} else {
				int pageNum = 1; // 페이지 번호 초기화
				if (request.getParameter("page") != null) {
					// 페이지 번호가 주어지면 변환하여 저장
					pageNum = Integer.parseInt(request.getParameter("page"));
					System.out.println("43 (CrewCommunityPageAction) pageNum = "+pageNum);
				}
				int boardSize = 10; // 한 페이지에 표시할 게시글 수 설정
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

				Crew_boardDTO crew_boardDTO = new Crew_boardDTO();
				Crew_boardDAO crew_boardDAO = new Crew_boardDAO();

				crew_boardDTO.setModel_crew_board_min_num(minBoard);
				crew_boardDTO.setModel_crew_board_max_num(maxBoard);
				crew_boardDTO.setModel_crew_board_writer_id(member_id);
				crew_boardDTO.setModel_crew_board_condition("CREW_BOARD_ALL_CREW_BOARD");
				
				//줄바꿈 처리
				String br_content = "";
				// 게시글 목록 가져오기
				ArrayList<Crew_boardDTO> model_crew_board_datas = crew_boardDAO.selectAll(crew_boardDTO);
				for(int i=0;i<model_crew_board_datas.size();i++) {
					System.out.println(model_crew_board_datas.get(i));
					br_content = model_crew_board_datas.get(i).getModel_crew_board_content();
					br_content = br_content.replace("\n", "<br>");
					System.err.println("줄바꿈 적용 내용 = "+br_content);
					model_crew_board_datas.get(i).setModel_crew_board_content(br_content);
				}
				// 프로필 이미지 URL 설정
				for (Crew_boardDTO data : model_crew_board_datas) {
					String filename = data.getModel_crew_board_member_profile();
					data.setModel_crew_board_member_profile(request.getServletContext().getContextPath() + "/profile_img/" + filename);
				}

				// 총 게시글 수 확인
				Crew_boardDTO crew_board_count = new Crew_boardDTO();
				crew_board_count.setModel_crew_board_writer_id(member_id);
				crew_board_count.setModel_crew_board_condition("CREW_BOARD_ONE_COUNT");
				crew_board_count = crew_boardDAO.selectOne(crew_board_count);
				int listNum = crew_board_count.getModel_crew_board_total();

				request.setAttribute("model_crew_board_datas", model_crew_board_datas);
				request.setAttribute("totalCount", listNum);
				request.setAttribute("currentPage", pageNum);
				System.out.println("totalCount = "+listNum);
				System.out.println("currentPage = "+pageNum);
			}
		}

		forward.setPath(path); // 페이지 설정
		forward.setRedirect(flagRedirect); // 페이지 이동 방식 설정
		return forward;
	}
}
