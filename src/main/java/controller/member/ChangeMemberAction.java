package controller.member;

import controller.common.Action;
import controller.common.ActionForward;
import controller.function.LoginCheck;
import controller.function.ProfileUpload;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.member.MemberDAO;
import model.member.MemberDTO;

public class ChangeMemberAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		//기본으로 넘어가야하는 페이지 와 redirect 여부를 설정
		ActionForward forward = new ActionForward();
		String path = "MYPAGEPAGEACTION.do";
		boolean flagRedirect = true;

	      //로그인 정보가 있는지 확인해주고
	      String login[] = LoginCheck.Success(request, response);
	      //사용자 아이디
	      String member_id = login[0];

		//만약 로그인 정보가 있다면 사용자 정보를 변경하고
		if(member_id != null) {
			MemberDAO dao = new MemberDAO();
			MemberDTO data = new MemberDTO();

			//profile_img 에 파일을 저장하고
			
			data.setModel_member_id(member_id);//바꿀 사용자 아이디를 입력해줍니다.
			data.setModel_member_password(request.getParameter("VIEW_PASSWORD_CHECK"));// 비밀번호
			//data.setMember_name(request.getParameter("member_name"));//이름은 실명으로 변경하지 않습니다.
			data.setModel_member_phone(request.getParameter("VIEW_PHONE"));//전환번호
			data.setModel_member_location(request.getParameter("VIEW_LOCATION"));//지역
			
			//file 업로드 확인
			String filename = ProfileUpload.upload(request);
			//uploadfile이 null이 아니라면 DB의 profile 이미지를 변경합니다.
			if(!filename.isEmpty()){
				System.out.println("uploadfile not null 로그 : " + filename);
				data.setModel_member_profile(filename);//저장한 프로필 이미지로 변경합니다.
				data.setModel_member_condition("MEMBER_UPDATE_ALL");
			}
			else {
				System.out.println("uploadfile null 로그");
				data.setModel_member_condition("MEMBER_UPDATE_WITHOUT_PROFILE");
			}

			System.out.println(data);
			//프로필 이미지 저장 로그
			System.err.println(filename);
			boolean flag = dao.update(data);
			
			if(!flag) {
				request.setAttribute("CHANGE_CHECK", flag);
				path = "CHANGEMEMBERPAGEACTION.do";
			}
		}
		//로그인 정보가 없다면 로그인 페이지로 이동시킵니다.
		else {
			path = "LOGINPAGEACTION.do";
		}

		forward.setPath(path);
		forward.setRedirect(flagRedirect);
		return forward;
	}

}