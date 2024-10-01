package controller.community;

import java.util.ArrayList;

import controller.common.Action;
import controller.common.ActionForward;
import controller.function.LoginCheck;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.board.BoardDAO;
import model.board.BoardDTO;
import model.member.MemberDAO;
import model.member.MemberDTO;
import model.reply.ReplyDAO;
import model.reply.ReplyDTO;

public class BoardOnePageAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		// 글 하나 선택 페이지
		ActionForward forward = new ActionForward();
		String path = "post.jsp"; // 선택한 글 하나 보는 페이지
		boolean flagRedirect = false; // 포워드 방식

		// 글 pk 가져오기
		int board_num = Integer.parseInt(request.getParameter("model_board_num"));
		System.out.println("게시글 번호: " + board_num);

		// pk로 selectOne하기
		BoardDAO boardDAO = new BoardDAO();
		BoardDTO boardDTO = new BoardDTO();
		boardDTO.setModel_board_num(board_num); // pk
		boardDTO.setModel_board_condition("BOARD_ONE"); // 글 selectOne 컨디션
		boardDTO = boardDAO.selectOne(boardDTO); // pk로 글 selectOne

		System.out.println("게시글 정보 조회: " + boardDTO);

		// MemberDAO에서 프로필 정보를 가져옴
		MemberDAO memberDAO = new MemberDAO();
		MemberDTO memberDTO = new MemberDTO();
		memberDTO.setModel_member_id(boardDTO.getModel_board_writer_id()); // 세션에 있는 사용자의 아이디
		memberDTO.setModel_member_condition("MEMBER_SEARCH_ID"); // member selectOne 컨디션
		memberDTO = memberDAO.selectOne(memberDTO); // 프로필 사진을 보여주기 위해 member selectOne
		System.out.println("회원 정보 조회: " + memberDTO);

		String filename = memberDTO.getModel_member_profile();

		request.setAttribute("member_profile", request.getServletContext().getContextPath() + "/profile_img/" + filename);

		int board_cnt = boardDTO.getModel_board_cnt() + 1; // 조회수 증가
		boardDTO.setModel_board_cnt(board_cnt);

		BoardDTO data_cnt = new BoardDTO();
		data_cnt.setModel_board_cnt(board_cnt); // 글의 조회수
		data_cnt.setModel_board_num(board_num); // 글의 번호
		data_cnt.setModel_board_condition("BOARD_UPDATE_CNT"); // 글 조회수 업데이트 컨디션
		boardDAO.update(data_cnt); // 글 조회수 업데이트

		System.out.println("게시글 조회수 업데이트: " + board_cnt);

		ReplyDAO replyDAO = new ReplyDAO();
		ReplyDTO replyDTO = new ReplyDTO();
		replyDTO.setModel_reply_board_num(board_num); // boardDTO 안에 있는 것들만 보내는 것들로
		ArrayList<ReplyDTO> replyList = replyDAO.selectAll(replyDTO);

		//System.out.println("댓글 리스트 조회: " + replyList);

		request.setAttribute("BOARD", boardDTO); // 글 정보 넘겨주기
		request.setAttribute("REPLY", replyList); // 댓글 리스트 넘겨주기


		forward.setPath(path);
		forward.setRedirect(flagRedirect);
		return forward;
	}
}
