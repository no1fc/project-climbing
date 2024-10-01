package controller.community;

import controller.common.Action;
import controller.common.ActionForward;
import controller.function.LoginCheck;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.reply.ReplyDAO;
import model.reply.ReplyDTO;

public class ReplyAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		// 댓글 달기
		int reply_board_num = Integer.parseInt(request.getParameter("board_id")); // 댓글 작성할 게시글 번호

		// 기본으로 넘어가야하는 페이지와 redirect 여부를 설정
		ActionForward forward = new ActionForward();
		String path = "info.jsp";
		// 글 하나 보는 페이지로 돌아오기 위해 해당 댓글의 글번호를 get 방식으로 전달
		boolean flagRedirect = false; // 리다이렉트 방식 사용

		String info_path = "BOARDONEPAGEACTION.do?model_board_num=" + reply_board_num;

		// 로그인 정보가 있는지 확인
		String login[] = LoginCheck.Success(request, response);
		String memeber_id = login[0];
		System.out.println("로그인 확인: " + memeber_id);

		// 만약 로그인 정보가 없다면
		if (memeber_id == null) {
			// 로그인 페이지로 전달
			request.setAttribute("msg", "댓글 작성은 로그인 후 사용 가능합니다.");
			request.setAttribute("path", "LOGINPAGEACTION.do");
		} 
		else {
			// 댓글 작성
			String reply_content = request.getParameter("reply_content");
			String reply_writer_id = memeber_id; // 세션에 있는 사용자의 아이디

			System.out.println("댓글 작성자 ID: " + reply_writer_id);
			System.out.println("댓글 내용: " + reply_content);
			System.out.println("댓글 작성할 게시글 번호: " + reply_board_num);

			ReplyDTO replyDTO = new ReplyDTO();
			ReplyDAO replyDAO = new ReplyDAO();
			replyDTO.setModel_reply_board_num(reply_board_num); // 해당 댓글의 글 번호
			replyDTO.setModel_reply_content(reply_content); // 댓글 내용
			replyDTO.setModel_reply_writer_id(reply_writer_id); // 댓글 작성자

			boolean insertResult = replyDAO.insert(replyDTO); // 댓글 삽입

			if(!insertResult) {
				request.setAttribute("msg", "댓글 작성을 성공하였습니다.");
			}
			else {
				request.setAttribute("msg", "댓글 작성을 성공하였습니다.");
			}
			request.setAttribute("path", info_path);

		}

		forward.setPath(path);
		forward.setRedirect(flagRedirect);
		return forward;
	}
}
