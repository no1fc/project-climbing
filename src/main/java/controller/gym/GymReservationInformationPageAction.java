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

public class GymReservationInformationPageAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		String path = "reservation.jsp"; // view에서 알려줄 예정
		String error_path = "info.jsp";
		boolean flag_Redirect = false; // 값을 전달해야하게 때문에 forward 방식으로 전달해야한다.
		ActionForward forward = new ActionForward();

		//로그인 정보가 있는지 확인해주고
		String login[] = LoginCheck.Success(request, response);
		//사용자 아이디
		String member_id = login[0];
		//가입 크루 번호
		//String crew_check = login[1];

		//------------------------------------------------------------
		//해당 기능에서 공통으로 사용할 변수 and 객체
		//View에서 전달해주는 (암벽장 번호 / 예약일 / 사용한 포인트 / 암벽장 가격)변수
		int view_gym_num = Integer.parseInt(request.getParameter("VIEW_RESERVATION_GYM_NUM"));
		String view_gym_reservation_date = request.getParameter("VIEW_RESERVATION_DATE");
		System.err.println("36 예약날짜 ="+view_gym_reservation_date);
		System.err.println("사용포인트 넘어왔니"+request.getParameter("VIEW_USE_POINT"));
		int view_reservation_use_point=0;
		if(request.getParameter("VIEW_USE_POINT")!=null) {
			view_reservation_use_point = Integer.parseInt(request.getParameter("VIEW_USE_POINT"));
		}
		int view_gym_price = Integer.parseInt(request.getParameter("VIEW_RESERVATION_PRICE"));
		//최대 사용 포인트
		int max_Point = 5000;
		
		//예약금액 변수
		int reservation_price = 0;

		//예약 가능 인원
		int reservation_cnt = 0;

		//사용자 아이디
		String member_name = null;

		//View에서 이동할 페이지 변수
		String view_path = "GymInformationPage.do?VIEW_GYM_NUM="+view_gym_num;
		
		//View에서 띄울 알림창
//		request.setAttribute("msg", "예약 되었습니다~!");

		//Reservation DTO DAO 객체
		ReservationDTO reservation_SelectOne = new ReservationDTO();
		ReservationDAO reservationDAO = new ReservationDAO();

		//Gym DTO DAO 객체
		GymDTO gymDTO = new GymDTO();
		GymDAO gymDAO = new GymDAO();		

		//Member DTO DAO 객체
		MemberDTO memberDTO = new MemberDTO();
		MemberDAO memberDAO = new MemberDAO();

		//------------------------------------------------------------
		
		if(member_id != null) {
			System.out.println("member_id 있음");
			//------------------------------------------------------------
			//사용 포인트 변경하는 로직 시작
			//사용자가 최대 포인트 보다 많이 입력했다면 
			if(view_reservation_use_point > max_Point) {
				//사용자 포인트를 강제로 5000으로 고정합니다.
				view_reservation_use_point = max_Point;
			}
			//(사용자 아이디)을 MemberDTO에 추가합니다.
			memberDTO.setModel_member_id(member_id);
			memberDTO.setModel_member_condition("MEMBER_SEARCH_ID");
			//TODO 사용자의 현재 포인트를 SelectOne으로 요청하고
			MemberDTO member_point = memberDAO.selectOne(memberDTO);
			//TODO 해당 사용자의 현재 포인트 - 사용 포인트를 use_Point 변수에 추가
			int use_Point = member_point.getModel_member_current_point() - view_reservation_use_point;
			//use_Point 값이
			//음수 == error_message : 현재 포인트가 부족하여 예약에 실패하였습니다. (현재 포인트 : XX)
			//path				   : 암벽장 페이지	
			if(use_Point < 0) {
				request.setAttribute("msg", "포인트가 부족합니다. 포인트를 확인해주세요. \n (현재 포인트 : "+member_point.getModel_member_current_point()+")");
				request.setAttribute("path", view_path);	
				forward.setPath(error_path);
				forward.setRedirect(flag_Redirect);
				return forward;
			}
			//양수 : 예약금액 = 암벽장금액 - 사용 포인트
			else {
				reservation_price = view_gym_price - view_reservation_use_point;			
			}
			//사용 포인트 변경하는 로직 종료
			//------------------------------------------------------------
			//예약 가능 인원 수 구하는 로직 시작
			//암벽장 번호를 Gym DTO 에 입력하여 암벽장 정보를 요청합니다.
			gymDTO.setModel_gym_num(view_gym_num);
			gymDTO.setModel_gym_condition("GYM_ONE");
			//해당 암벽장 정보의 예약 최대 인원을 요청합니다.
			int reservation_total_cnt = gymDAO.selectOne(gymDTO).getModel_gym_reservation_cnt();

			//암벽장 번호와 예약 날짜를 Reservation DTO 에 추가해줍니다.
			reservation_SelectOne.setModel_reservation_gym_num(view_gym_num);
			reservation_SelectOne.setModel_reservation_date(view_gym_reservation_date);
			reservation_SelectOne.setModel_reservation_condition("RESERVATION_ONE_COUNT");//TODO 컨디션 추가해야함
			//model 에 selectOne 을 요청하여 현재 예약한 인원을 요청합니다.
			int reservation_current_cnt = reservationDAO.selectOne(reservation_SelectOne).getModel_reservation_total();
			//예약 인원이 resrvation_cnt = resrvation_total_cnt - resrvation_current_cnt
			reservation_cnt = reservation_total_cnt - reservation_current_cnt;
			//만약 0보다 작다면
			if(reservation_cnt <= 0) {
				//error_message : 예약이 불가능한 날짜입니다.
				request.setAttribute("msg", "예약인원이 불가능한 날짜입니다.");
				//path : 암벽장 페이지
				request.setAttribute("path", view_path); //TODO 암벽장 페이지 작성해야함
				forward.setPath(error_path);
				forward.setRedirect(flag_Redirect);		
				return forward;
			}

			//예약 가능 인원 수 구하는 로직 종료
			//------------------------------------------------------------
			//사용자 이름 구하는 로직 시작
			//사용자 아이디를 Member DTO에 추가합니다.
			memberDTO.setModel_member_condition("MEMBER_SEARCH_ID");
			memberDTO.setModel_member_id(member_id);
			//model 에 selectOne으로 사용자 이름을 요청합니다.
			member_name = memberDAO.selectOne(memberDTO).getModel_member_name();

			//사용자 이름 구하는 로직 종료
			//------------------------------------------------------------
		}
		else {
			System.out.println("member_id 없음");
			//error_message : 예약이 불가능한 날짜입니다.
			request.setAttribute("msg", "로그인 후 사용 가능합니다.");
			//path : 암벽장 페이지
			request.setAttribute("path", "LOGINPAGEACTION.do"); //TODO 암벽장 페이지 작성해야함
			forward.setPath(error_path);
			forward.setRedirect(flag_Redirect);		
			return forward;
		}

		request.setAttribute("model_gym_num", view_gym_num);
		request.setAttribute("MEMBER_NAME", member_name);
		request.setAttribute("reservation_date", view_gym_reservation_date);
		request.setAttribute("reservation_cnt", reservation_cnt);
		request.setAttribute("reservation_price", reservation_price);
		request.setAttribute("use_point", view_reservation_use_point);

		forward.setPath(path);
		forward.setRedirect(flag_Redirect);		
		return forward;
	}

}
