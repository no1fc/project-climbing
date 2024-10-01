package controller.asycnServlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.member.MemberDAO;
import model.member.MemberDTO;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet implementation class LoginAPI
 */
@WebServlet("/loginAPI")
public class LoginAPIAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginAPIAction() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String api_id = request.getParameter("email");
		
		MemberDAO memberDAO = new MemberDAO();
		MemberDTO memberDTO = new MemberDTO();
		
		HttpSession session = request.getSession();
		PrintWriter out = response.getWriter();
		
		//해당 사용자의 아이디가 있는지 확인해서
		memberDTO.setModel_member_condition("MEMBER_SEARCH_ID");
		memberDTO.setModel_member_id(api_id);
		memberDTO = memberDAO.selectOne(memberDTO);
		
		//만약 있으면 session 값에 저장해서 로그인 진행
		if(memberDTO != null) {
			System.out.println("LoginAPIAtion memberDTO.member_id 로그 회원 아이디 : "+memberDTO.getModel_member_id());
			session.setAttribute("MEMBER_ID", memberDTO.getModel_member_id());
			session.setAttribute("CREW_CHECK", memberDTO.getModel_member_crew_num());
			System.out.println(memberDTO.getModel_member_id());
			System.out.println(memberDTO.getModel_member_crew_num());
			out.print(true);
		}
		//만약 없으면 회원가입 페이지로 넘겨서 회원가입할 수 있도록 한다.
		else {
			System.out.println("LoginAPIATION memberDTO NULL 로그 VIEW 에서 넘겨준 회원 아이디 : "+api_id);
			out.print(api_id);
		}
	}

}
