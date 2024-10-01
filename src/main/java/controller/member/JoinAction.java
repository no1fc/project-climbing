package controller.member;

import controller.common.Action;
import controller.common.ActionForward;
import controller.function.LoginCheck;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.member.MemberDAO;
import model.member.MemberDTO;

public class JoinAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		//기본으로 넘어가야하는 페이지 와 redirect 여부를 설정
		ActionForward forward = new ActionForward();

		// 회원가입 성공/실패 여부를 알랏창으로 띄우기 위해 안내메시지 페이지로 기본경로를 설정
		String path = "info.jsp";
		// 기본 : 포워드 방식
		boolean flagRedirect = false;

		// 로그인 정보가 있는지 확인해주고
		String login[] = LoginCheck.Success(request, response);
		// 사용자 아이디
		String member_id = login[0];
		
		// 만약 로그인 정보가 있다면
		if(member_id != null) {
			// main 페이지로 전달해줍니다.
			path = "MAINPAGEACTION.do";
			// 리다이렉트 방식으로 보내줍니다.
			flagRedirect = true;
		}
		else { // 로그인 정보가 없다면
			// 사용자가 저장한 값을 DB(M)에 저장하기 위해 객체를 생성합니다.
			MemberDAO dao = new MemberDAO();
			MemberDTO data = new MemberDTO();
			
			data.setModel_member_id(request.getParameter("VIEW_EMAIL"));//사용자 아이디
			data.setModel_member_password(request.getParameter("VIEW_PASSWORD"));// 비밀번호
			data.setModel_member_name(request.getParameter("VIEW_NAME"));//이름
			data.setModel_member_phone(request.getParameter("VIEW_PHONENUMBER"));//전환번호
			data.setModel_member_location(request.getParameter("VIEW_LOCATION"));//지역
			
			//model 에 등록을 요청하고
			boolean flag = dao.insert(data);
			
			//만약 성공했다면 회원가입 성공했다는 내용을 전달해주고
			if(flag) {
				System.err.println("회원가입 성공 로그");
				request.setAttribute("msg", "회원가입 성공!");
				request.setAttribute("path", "MAINPAGEACTION.do");
			}
			//실패했다면 실패 내용을 전달해준다.
			else {
				System.err.println("회원가입 실패 로그");
				request.setAttribute("msg", "회원가입 실패..");
				request.setAttribute("path", "JOINPAGEACTION.do");
			}
		}
		
		forward.setPath(path);
		forward.setRedirect(flagRedirect);
		return forward;
	}

}
