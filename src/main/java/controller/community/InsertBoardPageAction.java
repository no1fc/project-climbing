package controller.community;

import controller.common.Action;
import controller.common.ActionForward;
import controller.function.LoginCheck;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InsertBoardPageAction implements Action{

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {

		ActionForward forward = new ActionForward();
		String path = "editing.jsp";//글작성페이지
		boolean flagRedirect = false;//네비게이션 바 때문에 로그인정보 필요 

		//로그인 정보가 있는지 확인해주고
		String login[] = LoginCheck.Success(request, response);
		String member_id = login[0];
		
		System.out.println("InsertBoard 로그인 정보 로그 : "+member_id);
		//만약 로그인 정보가 없다면
		if(member_id == null) {
			//로그인 페이지로 넘어간다
			path = "LOGINPAGEACTION.do";
		}
		
		//로그인이 되어 있다면
		//글 작성 페이지로 넘어간다
		forward.setPath(path);
		forward.setRedirect(flagRedirect);

		return forward;

	}
}
