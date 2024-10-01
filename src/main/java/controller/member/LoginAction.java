package controller.member;

import controller.common.Action;
import controller.common.ActionForward;
import controller.function.LoginCheck;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.member.MemberDAO;
import model.member.MemberDTO;

public class LoginAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {

		//기본으로 넘어가야하는 페이지 와 redirect 여부를 설정
		ActionForward forward = new ActionForward();
		String path = "MAINPAGEACTION.do";
		boolean flagRedirect = false;

		//로그인 정보가 있는지 확인해주고
		String login[] = LoginCheck.Success(request, response);
		//사용자 아이디
		String member_id = login[0];

		//만약 로그인 정보가 없다면
		if(member_id == null) {
			MemberDAO dao = new MemberDAO();
			MemberDTO data = new MemberDTO();
			//사용자의 아이디와 비밀번호를 model 로 전달하여 확인하고
			System.out.println(request.getParameter("VIEW_EMAIL") + " " + request.getParameter("VIEW_PASSWORD"));
			data.setModel_member_id(request.getParameter("VIEW_EMAIL"));
			data.setModel_member_password(request.getParameter("VIEW_PASSWORD"));
			data.setModel_member_condition("MEMBER_SEARCH_ID_PASSWORD");
			data = dao.selectOne(data);

			//만약 data 가 null 을 반환하면
			if(data == null) {
				System.err.println("(LoginAction.java) data null 로그");
				//로그인을 실패한 것 이기 때문에 login.jsp 페이지로 반환합니다.
				path = "LOGINPAGEACTION.do";
				//로그인 여부를 전달하여 사용자가 로그인 여부를 확인할 수 있도록 만들어 줍니다.
				request.setAttribute("LOGIN_CHECK", false);
			}
			//null 이 아니라면
			else {
				System.err.println("(LoginAction.java) data.getMember_id 로그 : " + data.getModel_member_id());
				System.err.println("(LoginAction.java) data.getModel_member_crew_num 로그 : " + data.getModel_member_crew_num());
				//session 에 값을 저장해둡니다.
				HttpSession session = request.getSession();
				//사용자 ID Session에 저장
				session.setAttribute("MEMBER_ID", data.getModel_member_id());

				//크루가 없다면 0
				session.setAttribute("CREW_CHECK", data.getModel_member_crew_num());				

				//자동로그인을 등록했다면 cookie 에 로그인 정보를 저장해줍니다.	
				String auto = (String)request.getParameter("VIEW_AUTO_LOGIN");
				System.out.println(auto);
				if(auto != null){
					Cookie member_id_cookie = new Cookie("MEMBER_ID", data.getModel_member_id());
					//Cookie 값은 haxCode로 저장되기 때문에 문자열로 변환하여 저장해야한다.
					//크루가 없으면 0을 반환한다.
					Cookie member_crew_cookie = new Cookie("CREW_CHECK", data.getModel_member_crew_num()+"");

					System.out.println("(LoginAction.java) 사용자 크루 쿠키 값 저장 로그 : " + member_crew_cookie.getValue());
					System.out.println("(LoginAction.java) 사용자 크루 쿠키 명 저장 로그 : " + member_crew_cookie.getName());

					// 쿠키 유효 시간 설정 (7일)
					member_id_cookie.setMaxAge(60 * 60 * 24 * 7); // 7일 동안 유지
					member_crew_cookie.setMaxAge(60 * 60 * 24 * 7);
					
					//쿠키를 추가헤줍니다.
					response.addCookie(member_id_cookie);
					response.addCookie(member_crew_cookie);
					System.out.println("(LoginAction.java) 사용자 아이디 쿠키 값 저장 로그 : " + member_id_cookie.getValue());
					System.out.println("(LoginAction.java) 사용자 아이디 쿠키 명 저장 로그 : " + member_id_cookie.getName());
				}
				//로그인 여부를 전달하여 사용자가 로그인 여부를 확인할 수 있도록 만들어 줍니다.
				//request.setAttribute("LOGIN_CHECK", true);
			}
		}

		//최종적으로 보낼 내용을 입력하여 값을 반환해줍니다.
		forward.setPath(path);
		forward.setRedirect(flagRedirect);
		return  forward;
	}
}
