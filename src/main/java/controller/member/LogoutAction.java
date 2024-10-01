package controller.member;

import controller.common.Action;
import controller.common.ActionForward;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LogoutAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		//request 내장객체를 사용하여 cookies 배열을 가져옵니다.
		Cookie[] cookies = request.getCookies();
		//쿠키가 없다면 for문을 실행하지 않습니다.
		if(cookies != null) {
			//cookie 배열 만큼 for문을 돌리고
			for (int i = 0; i < cookies.length; i++) {
				//쿠키 배열을 하나씩 확인하고
				Cookie cookie = cookies[i];
				//만약 MEMBER_ID라는 cookie가 있으면 해당 쿠키의
				if(cookie.getName().equals("MEMBER_ID")) {
					//기간을 0으로 하여
					cookie.setMaxAge(0);
					//쿠키를 저장해 삭제해줍니다.
					response.addCookie(cookie);
				}
				if(cookie.getName().equals("CREW_CHECK")) {
					//기간을 0으로 하여
					cookie.setMaxAge(0);
					//쿠키를 저장해 삭제해줍니다.
					response.addCookie(cookie);
				}
			}			
		}
		
		//request의 내장객체인 Session을 가져옵니다.
		HttpSession session = request.getSession();
		//로그아웃이므로 session에 MEMBER_ID 라는 이름을 삭제합니다.
		session.removeAttribute("MEMBER_ID");
		session.removeAttribute("CREW_CHECK");
		        
		ActionForward forward = new ActionForward();
		forward.setPath("MAINPAGEACTION.do");
		forward.setRedirect(true);
		return forward;
	}

}
