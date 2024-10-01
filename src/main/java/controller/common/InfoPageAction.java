package controller.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InfoPageAction implements Action{

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("msg", "페이지 오류");
		request.setAttribute("path", "MAINPAGEACTION.do");		
		ActionForward forward = new ActionForward();
		forward.setPath("info.jsp");
		forward.setRedirect(false);
		return forward;
	}

}
