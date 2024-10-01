package controller.mypage;

import java.io.File;

import controller.common.Action;
import controller.common.ActionForward;
import controller.function.LoginCheck;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.member.MemberDAO;
import model.member.MemberDTO;

public class DeleteMemberAction implements Action{

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		//기본으로 넘어가야하는 페이지 와 redirect 여부를 설정
		ActionForward forward = new ActionForward();
		
		String path = "info.jsp";
		boolean flagRedirect = false;
//		String path = "MYPAGEPAGEACTION.do";
//		boolean flagRedirect = true;

	      //로그인 정보가 있는지 확인해주고
	      String login[] = LoginCheck.Success(request, response);
	      //사용자 아이디
	      String member_id = login[0];
		
		//만약 로그인 정보가 없다면
		if(member_id == null) {
			//main 페이지로 전달해줍니다.
			path = "LOGINPAGEACTION.do";
		}
		else {
			MemberDTO data = new MemberDTO();
			MemberDAO memberDAO = new MemberDAO();
			//사용자 아이디를 DTO에 등록
			data.setModel_member_id(member_id);
			System.out.println("DeletememberAction.java 로그 : "+member_id);
			
			//탈퇴전 사용자의 프로필이미지를 가져오기 위해 아이디 하나 검색하는 컨디션을 추가합니다.
			data.setModel_member_condition("MEMBER_SEARCH_ID");
			//탈퇴전 사용자의 프로필이미지를 가져와 줍니다.
			data = memberDAO.selectOne(data);
			
			//delete 를 성공하지 못했다면 Mypage로 보냅니다.
			boolean flag = memberDAO.delete(data);
			if(flag) {//멤버 삭제에 성공했다면 logout 페이지로 넘어갑니다.
				
				//탈퇴전 사용자의 프로필이미지를 변수에 추가해줍니다.
				String member_profile = data.getModel_member_profile();
				//사용자의 프로필이미지가 default 이미지가 아니라면 실행합니다.
				if(!member_profile.substring(0,member_profile.lastIndexOf(".")).equals("default")) {
					//받아온 프로필이미지를 삭제하기 위해 서버 주소를 추가해줍니다.
					String user_img_path = request.getServletContext().getRealPath("/profile_img/") + member_profile;
					//파일 위치 확인용 로그
					System.out.println(user_img_path);
					//내 PC에 파일을 확인해줍니다.
					File file = new File(user_img_path);
					//파일이 있다면
					if(file.isFile()) {
						//삭제해줍니다.
						boolean flag_profile = file.delete();
						//삭제 확인용 로그
						System.out.println("프로필 삭제 여부 : "+flag_profile);
					}
				}
				
				System.err.println("회원탈퇴 성공 로그");
				request.setAttribute("msg", "회원 탈퇴 성공!");
				request.setAttribute("path", "LOGOUTPAGEACTION.do");
				//path = "LOGOUTPAGEACTION.do";
			}
			else {
				System.err.println("회원탈퇴 실패 로그");
				request.setAttribute("msg", "회원 탈퇴 실패...");
				request.setAttribute("path", "MYPAGEPAGEACTION.do");
			}
		}
		forward.setPath(path);
		forward.setRedirect(flagRedirect);
		return forward;
	}

}
