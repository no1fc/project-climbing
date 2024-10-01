package controller.member;

import controller.common.Action;
import controller.common.ActionForward;
import controller.function.LoginCheck;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginPageAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		//기본으로 넘어가야하는 페이지 와 redirect 여부를 설정
		ActionForward forward = new ActionForward();
		String path = "login.jsp";
		boolean flagRedirect = true;
		
		//로그인 정보가 있는지 확인해주고
		String login[] = LoginCheck.Success(request, response);
		//사용자 아이디
		String member_id = login[0];
		
		System.out.println("LoginPageAction login 로그 : "+login);
		System.out.println("LoginPageAction path 로그 : "+path);
		//만약 로그인 정보가 있다면
		if(member_id != null) {
			//main 페이지로 전달해줍니다.
			path = "MAINPAGEACTION.do";
		}
		
		forward.setPath(path);
		forward.setRedirect(flagRedirect);
		return forward;
	}

}