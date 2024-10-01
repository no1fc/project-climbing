package controller.community;


import java.util.HashMap;
import java.util.Map;

import controller.common.Action;
import controller.common.ActionForward;
import controller.function.LoginCheck;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.board.BoardDAO;
import model.board.BoardDTO;

public class BoardUpdatePageAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		//기본으로 넘어가야하는 페이지 와 redirect 여부를 설정
		ActionForward forward = new ActionForward();
		String path = "updateEditing.jsp";
		boolean flagRedirect = false;

	      //로그인 정보가 있는지 확인해주고
	      String login[] = LoginCheck.Success(request, response);
	      //사용자 아이디
	      String member_id = login[0];
	      
		//만약 로그인 정보가 없다면
		if(member_id == null) {
			//LoginPageAction 페이지로 전달해줍니다.
			path = "LOGINPAGEACTION.do";
		}
		else {
			BoardDAO boardDAO = new BoardDAO();
			BoardDTO data = new BoardDTO();
			//사용자가 선택한 글번호를 받아서
			data.setModel_board_num(Integer.parseInt(request.getParameter("model_board_num")));
			data.setModel_board_writer_id(member_id);
			data.setModel_board_condition("BOARD_ONE_WRITER_ID");
			//model 에 전달하여 글 내용을 받아오고
			data = boardDAO.selectOne(data);
			
			//만약 데이터가 null 이라면 mypage.do 로 전달
			if(data == null) {
				path="info.jsp";
				request.setAttribute("path", "MYPAGEPAGEACTION.do");
				request.setAttribute("msg", "없는 게시글입니다.");
			}
			else {
				//해당 글 내용을 view 로 전달해줍니다.
				request.setAttribute("BOARD_NUM", data.getModel_board_num());
				request.setAttribute("BOARD_TITLE", data.getModel_board_title());
				System.out.println("지역 로그 1 : "+data.getModel_board_location());
				System.out.println("지역 로그 2 : "+location(data.getModel_board_location()));
				request.setAttribute("BOARD_LOCATION", location(data.getModel_board_location()));
				
				String content = data.getModel_board_content();
				request.setAttribute("BOARD_CONTENT", content);
				
				
				HttpSession session = request.getSession();
				try {
					//글 내용에서 img 태그가 있다면 해당 이미지 폴더의 번호만 가져오는 로직
					content = content.substring(content.lastIndexOf("img")+3).split("/")[2];
					System.out.println("BoardUpdatePageAction.java content 로그 : "+content);
					session.setAttribute("UPDATE_FOLDER_NUM", Integer.parseInt(content));
				} catch (Exception e) {
					session.setAttribute("UPDATE_FOLDER_NUM", 0);
				}
				
			}

			
		}
		
		forward.setPath(path);
		forward.setRedirect(flagRedirect);
		return forward;
	}
	
	public String location(String model_location) {
		Map<String, String> location_Map = new HashMap<String, String>();
		
		location_Map.put("서울특별시", "SEOUL");
		location_Map.put("경기도", "GYEONGGI");
		location_Map.put("인천광역시", "INCHEON");       
		location_Map.put("세종특별자치도", "SEJONG");
		location_Map.put("부산광역시", "BUSAN");
		location_Map.put("대구광역시", "DAEGU");
		location_Map.put("대전광역시", "DAEJEON");
		location_Map.put("광주광역시", "GWANGJU");
		location_Map.put("울산광역시", "ULSAN");
		location_Map.put("충청남도", "CHUNGCHEONGNAMDO");
		location_Map.put("충청북도", "CHUNGCHEONGBUKDO");
		location_Map.put("전라남도", "JEONLANAMDO");
		location_Map.put("전라북도", "JEONLABUKDO");
		location_Map.put("경상남도", "GYEONGSANGNAMDO");
		location_Map.put("경상북도", "GYEONGSANGBUKDO");
		location_Map.put("강원도", "GANGWONDO");
		
		return location_Map.getOrDefault(model_location, "SEOUL");
	}
	
}
