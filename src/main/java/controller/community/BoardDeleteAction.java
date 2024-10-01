package controller.community;

import controller.common.Action;
import controller.common.ActionForward;
import controller.function.LoginCheck;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.board.BoardDAO;
import model.board.BoardDTO;

public class BoardDeleteAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		//기본으로 넘어가야하는 페이지 와 redirect 여부를 설정
		ActionForward forward = new ActionForward();
		String path = "MYPAGEPAGEACTION.do";
		boolean flagRedirect = false;

	      //로그인 정보가 있는지 확인해주고
	      String login[] = LoginCheck.Success(request, response);
	      //사용자 아이디
	      String member_id = login[0];
		
		//만약 로그인 정보가 없다면
		if(member_id == null) {
			//LoginPageAction 페이지로 전달해줍니다.
			path = "LOGINPAGEACTION.do";
			//리다이렉트 방식으로 보내줍니다.
			flagRedirect = true;
		}
		else {
			BoardDAO boardDAO = new BoardDAO();
			BoardDTO data = new BoardDTO();
			//사용자가 선택한 글번호를 받아서
			System.out.println("글 번호 : "+request.getParameter("model_board_num"));
			data.setModel_board_num(Integer.parseInt(request.getParameter("model_board_num")));
			data.setModel_board_writer_id(member_id);
			//model 에 전달해 글을 삭제하고
			boolean flag = boardDAO.delete(data);
			
			if(!flag) {
				request.setAttribute("boardDelete", flag);
			}
			else {
				request.setAttribute("boardDelete", flag);
			}
		}
		
		forward.setPath(path);
		forward.setRedirect(flagRedirect);
		return forward;
	}

}
