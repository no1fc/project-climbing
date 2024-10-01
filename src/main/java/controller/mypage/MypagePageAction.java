package controller.mypage;

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
import model.reservation.ReservationDAO;
import model.reservation.ReservationDTO;

public class MypagePageAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		//기본으로 넘어가야하는 페이지 와 redirect 여부를 설정
		ActionForward forward = new ActionForward();
		String path = "myPage.jsp";
		boolean flagRedirect = false;

	      //로그인 정보가 있는지 확인해주고
	      String login[] = LoginCheck.Success(request, response);
	      //사용자 아이디
	      String member_id = login[0];
	      
		//만약 로그인 정보가 없다면
		if(member_id == null) {
			//main 페이지로 전달해줍니다.
			path = "LOGINPAGEACTION.do";
			//리다이렉트 방식으로 보내줍니다.
			flagRedirect = true;
		}
		else {
			//회원 정보 전달하기 위한 member 객체
			MemberDAO memberDAO = new MemberDAO();
			MemberDTO memberDTO = new MemberDTO();
			//신규 회원 정보 전달하기 위한 member 객체
			MemberDTO babymember = new MemberDTO();
			//내 게시글 정보 전달하기 위한 board 객체
			BoardDAO boardDAO = new BoardDAO();
			BoardDTO boardDTO = new BoardDTO();
			
			//mypage 
			System.out.println("MyPage 로그인 정보 로그 : "+member_id);
			//------------------------------------------------------------------
			//내 정보 불러오는 코드 시작
			//사용자 정보 이름, 전화번호, 아이디, 프로필 이미지, 권한 전달
			memberDTO.setModel_member_id(member_id);
			memberDTO.setModel_member_condition("MEMBER_SEARCH_ID");
			memberDTO = memberDAO.selectOne(memberDTO);
			String filename = "";
			//회원가입할때 무언가 문제가 발생하여 이미지가 설정되지 못했다면
			//기본 default 이미지로 설정해야한다.
			if(memberDTO == null) {
				filename = "default.jpg";
			}
			else {
				filename = memberDTO.getModel_member_profile();
			}
			
			//현재 웹 프로젝트의 경로를 반환받고 프로젝트 경로를 반환 받아옵니다.
			memberDTO.setModel_member_profile(request.getServletContext().getContextPath()+ "/profile_img/" + filename);
			//내 정보 불러오는 코드 종료
			//------------------------------------------------------------------
			
			//------------------------------------------------------------------
			//관리자 권한이 있다면 신규 등록한 아이디 출력 시작
			if(memberDTO.getModel_member_role().equals("T")) {
				//사용자 정보 이름, 전화번호, 아이디, 프로필 이미지, 권한 전달
				babymember.setModel_member_condition("MEMBER_ALL_NEW");
				ArrayList<MemberDTO> member_list = memberDAO.selectAll(babymember);
				request.setAttribute("MEMBER_LIST", member_list);
			}
			//관리자 권한이 있다면 신규 등록한 아이디 출력 종료
			//------------------------------------------------------------------
			
			//사용자가 입력한 글 목록 출력 후 전달 시작
			//boardDTO.setBoard_writer_id(login); 모델에 mypage에서 쓸 컨디션 추가 부탁해야함
			boardDTO.setModel_board_searchKeyword(member_id);
			//이후 구현 예정
			System.err.println("MyPagePageAction 로그 페이지 네이션 하드코딩 해둔 상태");
			boardDTO.setModel_board_min_num(0);//페이지 네이션 하드코딩했음
			boardDTO.setModel_board_max_num(300);//페이지 네이션 하드코딩했음
			//사용자가 입력한 글 목록 출력 후 전달 종료
			//------------------------------------------------------------------
			// 내 게시글 불러오는 코드 시작
			boardDTO.setModel_board_condition("BOARD_ALL_SEARCH_MATCH_ID");
			ArrayList<BoardDTO> boardList = boardDAO.selectAll(boardDTO);

			// 내 게시글 불러오는 코드 종료
			//------------------------------------------------------------------
			//내 예약정보 불러오는 코드 시작
			
			//model에 reservation 테이블 정보를 요청
			ReservationDTO reservationDTO = new ReservationDTO();
			ReservationDAO reservationDAO = new ReservationDAO();
			
			//로그인 정보를 전달하기 위해 DTO에 추가
			reservationDTO.setModel_reservation_member_id(member_id);
			
			//요청해서 받을 값 (예약 PK번호 / 예약 암벽장 PK 번호 / 예약 암벽장 이름 / 예약 가격 / 암벽장 예약 날짜)
			ArrayList<ReservationDTO> model_Reservation_Datas = reservationDAO.selectAll(reservationDTO);
			
			//내 예약정보 불러오는 코드 종료
			//------------------------------------------------------------------
			
			//사용자 정보를 MEMBERDATA에 담아서 View로 전달
			request.setAttribute("MEMBERDATA", memberDTO);			
			//내 게시글 정보를 BOARD에 담아서 View로 전달
			request.setAttribute("BOARD", boardList);
			//내 예약 정보를 model_reservation_datas 에 담아서 View로 전달
			request.setAttribute("model_reservation_datas", model_Reservation_Datas);
		}
		
		forward.setPath(path);
		forward.setRedirect(flagRedirect);
		return forward;
	}

}
