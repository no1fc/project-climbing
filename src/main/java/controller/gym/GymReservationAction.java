package controller.gym;

import controller.common.Action;
import controller.common.ActionForward;
import controller.function.LoginCheck;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.gym.GymDAO;
import model.gym.GymDTO;
import model.member.MemberDAO;
import model.member.MemberDTO;
import model.reservation.ReservationDAO;
import model.reservation.ReservationDTO;

public class GymReservationAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		String path = "info.jsp"; // view에서 알려줄 예정 alert 창 띄우기 위한 JavaScript 페이지
		boolean flag_Redirect = false; // 값을 전달해야하게 때문에 forward 방식으로 전달해야한다.		
		ActionForward forward = new ActionForward();
		
        //로그인 정보가 있는지 확인해주고
        String login[] = LoginCheck.Success(request, response);
        //사용자 아이디
        String member_id = login[0];
        
		//------------------------------------------------------------
		//해당 기능에서 공통으로 사용할 변수 and 객체
		//View에서 전달해주는 (암벽장 번호 / 예약일 / 사용한 포인트 / 암벽장 가격)변수
		int view_reservation_gym_num = Integer.parseInt(request.getParameter("VIEW_RESERVATION_GYM_NUM"));
		String view_reservation_date = request.getParameter("VIEW_RESERVATION_DATE");
		int view_reservation_use_point=0;
		if(request.getParameter("VIEW_USE_POINT")!="0") {
			view_reservation_use_point = Integer.parseInt(request.getParameter("VIEW_USE_POINT"));
		}
		int view_reservation_price = Integer.parseInt(request.getParameter("VIEW_RESERVATION_PRICE"));
		//예약금액 변수
		int reservation_price = 0;
		
		//View에서 이동할 페이지 변수
		String view_path = "GymInformationPage.do?VIEW_GYM_NUM="+view_reservation_gym_num;
		
		//Reservation DTO DAO 객체
		ReservationDTO reservation_SelectOne = new ReservationDTO();
		ReservationDTO reservation_Update = new ReservationDTO();
		ReservationDAO reservationDAO = new ReservationDAO();
		
		//Member DTO DAO 객체
		MemberDTO memberDTO = new MemberDTO();
		MemberDAO memberDAO = new MemberDAO();
		
		//------------------------------------------------------------
		//사용자가 해당 암벽장에 예약한 정보가 있는지 확인하기 위한 로직 시작
		//(예약일 / 암벽장번호 / login) 정보를 ReservationDTO에 추가합니다.
		reservation_SelectOne.setModel_reservation_date(view_reservation_date);
		reservation_SelectOne.setModel_reservation_gym_num(view_reservation_gym_num);
		reservation_SelectOne.setModel_reservation_member_id(member_id);
		reservation_SelectOne.setModel_reservation_condition("RESERVATION_ONE_SEARCH");
		//Reservation selectOne 을 요청
		ReservationDTO reservation_Check = reservationDAO.selectOne(reservation_SelectOne);
		//요청 값이 null 이 아니라면 해당 날짜에 이미 예약되어있는 사용자 이므로
		//not null == error_message : 해당 날짜에는 이미 예약되어있습니다. (예약 번호 : Reservation PK 값)
		//path			 			: 암벽장 페이지
		if(reservation_Check != null) {
			request.setAttribute("msg", "해당 날짜에는 이미 예약되어있습니다. (예약 번호 : "+reservation_Check.getModel_reservation_num()+")");
			request.setAttribute("path", view_path);
			forward.setPath(path);
			forward.setRedirect(flag_Redirect);
			return forward;
		}
		
		//사용자가 해당 암벽장에 예약한 정보가 있는지 확인하기 위한 로직 종료		
		//------------------------------------------------------------
		//예약 정보가 정상문제 없는지 확인하는 로직 시작
		//암벽장 번호를 Gym DTO 에 추가해줍니다.
		GymDTO gymDTO = new GymDTO();
		GymDAO gymDAO = new GymDAO();
		gymDTO.setModel_gym_condition("GYM_ONE"); // TODO 컨디션 추가해야함
		gymDTO.setModel_gym_num(view_reservation_gym_num);
		
		//model 에 selectOne으로 암벽장 가격을 가져옵니다.
		String price = gymDAO.selectOne(gymDTO).getModel_gym_price();
		int gym_price = Integer.parseInt(price);
		
		//사용자가 최대 Point 보다 많이 입력했다면 최대 포인트로 고정합니다.
		int max_Point = 5000;
		if(view_reservation_use_point > max_Point) {
			//사용자 포인트를 강제로 5000으로 고정합니다.
			view_reservation_use_point = max_Point;
		}
		
		//(사용자 아이디)을 MemberDTO에 추가합니다.
		memberDTO.setModel_member_id(member_id);
		memberDTO.setModel_member_condition("MEMBER_SEARCH_ID");
		
		//사용자의 현재 포인트를 SelectOne으로 요청하고
		MemberDTO member_point = memberDAO.selectOne(memberDTO);
		
		//해당 사용자의 현재 포인트 - 사용 포인트를 use_Point 변수에 추가
		int use_Point = member_point.getModel_member_current_point() - view_reservation_use_point;
		
		//사용자 남은 포인트 로그
		System.out.println("사용자 남은 포인트 : "+use_Point);
		
		//use_Point 값이
		//음수 == error_message : 현재 포인트가 부족하여 예약에 실패하였습니다. (현재 포인트 : XX)
		//path				   : 암벽장 페이지	
		if(use_Point < 0) {
			request.setAttribute("msg", "포인트가 부족합니다. 포인트를 확인해주세요. \n (현재 포인트 : "+member_point.getModel_member_current_point()+")");
			request.setAttribute("path", view_path);	
			forward.setPath(path);
			forward.setRedirect(flag_Redirect);
			return forward;
		}
		
		//사용자 포인트에 문제가 없다면
		//DTO 에 남은 사용자 포인트를 추가하고
		memberDTO.setModel_member_current_point(use_Point);
		memberDTO.setModel_member_condition("MEMBER_UPDATE_CURRENT_POINT");
		//member update 로 사용자 포인트를 변경합니다.
		boolean flag_point_update = memberDAO.update(memberDTO);
		if(!flag_point_update) {
			//문제가 발생했다는 문구를 띄워 줍니다.
			request.setAttribute("msg", "예약 진행중 오류가 발생하였습니다. (사유 : 사용자 포인트 변경오류)");
			request.setAttribute("path", view_path);	
			forward.setPath(path);
			forward.setRedirect(flag_Redirect);
			return forward;
		}
		
		//예약금액 = 암벽장금액 - 사용 포인트
		reservation_price = gym_price - view_reservation_use_point;			
		
		//만약 받아온 금액과 다시 계산된 금액이 다르다면 
		if(view_reservation_price != reservation_price) {
			System.out.println("(GymReservationAction.java) Controller에서 계산된 예약금 로그 : "+view_reservation_price);
			System.out.println("(GymReservationAction.java) View에서 보내준 예약금 로그 : "+reservation_price);
			//문제가 발생했다는 문구를 띄워 줍니다.
			request.setAttribute("msg", "예약 불가 (사유 : 금액이 변경됨)");
			request.setAttribute("path", view_path);	
			forward.setPath(path);
			forward.setRedirect(flag_Redirect);
			return forward;
		}
		
		//예약 정보가 정상문제 없는지 확인하는 로직 종료
		//------------------------------------------------------------
		//예약 정보 저장 하기 위한 로직 시작
		//(암벽장 번호 / 예약일 / 예약금액)을 ReservationDTO에 추가합니다.
		System.out.println("(GymReservationAction.java) 사용자 예약 암벽장 사람 로그 : "+member_id);
		reservation_Update.setModel_reservation_member_id(member_id);
		System.out.println("(GymReservationAction.java) 사용자 예약 암벽장 번호 로그 : "+view_reservation_gym_num);
		reservation_Update.setModel_reservation_gym_num(view_reservation_gym_num);
		System.out.println("(GymReservationAction.java) 사용자 예약 암벽장 날짜 로그 : "+view_reservation_date);
		reservation_Update.setModel_reservation_date(view_reservation_date);
		System.out.println("(GymReservationAction.java) 사용자 예약 암벽장 가격 로그 : "+reservation_price);
		reservation_Update.setModel_reservation_price(reservation_price);
		
		//model 에 Reservation 테이블에 Insert 해줍니다.
		boolean flag = reservationDAO.insert(reservation_Update);
		//저장 여부에 따른 값 전달
		//True == error_message  : 예약에 성공하였습니다.
		if(flag) {
			request.setAttribute("msg", "예약에 성공하였습니다.");
		}
		//false == error_message : 예약에 실패하였습니다.
		//path					 : 암벽장 페이지
		else {
			request.setAttribute("msg", "예약에 실패하였습니다.");
		}
		request.setAttribute("path", view_path);
		//예약 정보 저장 하기 위한 로직 종료
		//------------------------------------------------------------

		forward.setPath(path);
		forward.setRedirect(flag_Redirect);		
		return forward;
	}

}
