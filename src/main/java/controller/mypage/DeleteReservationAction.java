package controller.mypage;

import controller.common.Action;
import controller.common.ActionForward;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.reservation.ReservationDAO;
import model.reservation.ReservationDTO;

public class DeleteReservationAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		//model_reservation_num
		int model_reservation_num =Integer.parseInt(request.getParameter("model_reservation_num"));
		String msg = "예약 취소 완료";
		String path = "MYPAGEPAGEACTION.do";
		
		ReservationDAO reservationDAO = new ReservationDAO();
		ReservationDTO reservationDTO = new ReservationDTO();
		
		reservationDTO.setModel_reservation_num(model_reservation_num);
		boolean falg =  reservationDAO.delete(reservationDTO);
		
		if (!falg) {
			msg = "예약 취소 실패";
		}
		
		request.setAttribute("msg", msg);
		request.setAttribute("path", path);
			
		ActionForward forward = new ActionForward();
		forward.setPath("info.jsp");
		forward.setRedirect(false);
		return forward;
	}

}
