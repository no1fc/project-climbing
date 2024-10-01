package controller.community;

import controller.common.Action;
import controller.common.ActionForward;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BoardDoneAction implements Action {

//취소
	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = new ActionForward();

        // 글 작성 취소 시, 메인페이지로 리다이렉트
        forward.setRedirect(true);
        forward.setPath("MAINPAGEACTION.do");

        return forward;
        
        //중프때 메인에서 커뮤니티페이지로 바뀔 예정이라 만들었다요
	}
	
}
