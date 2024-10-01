package controller.crew.join;

import controller.common.Action;
import controller.common.ActionForward;
import controller.function.LoginCheck;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.member.MemberDAO;
import model.member.MemberDTO;

public class CrewJoinAction implements Action {

@Override
public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
    ActionForward forward = new ActionForward();
    String path = "info.jsp"; // 크루 가입 안내 페이지
    boolean flagRedirect = false; // 포워드 방식 (false = forward)

    // 로그인 상태 체크
    String login[] = LoginCheck.Success(request, response);
    String member_id = login[0];

    // 로그인 되어 있지 않은 경우
    if (member_id == null) {
        path = "LOGINACTION.do"; // 로그인 페이지로 이동
        flagRedirect = true;
    } else {
        // 크루 번호 확인 및 유효성 검사
        try {
            int crew_num = Integer.parseInt(login[1]);

            // 이미 크루에 가입된 경우
            if (crew_num > 0) {
                request.setAttribute("msg", "이미 소속된 크루가 있습니다.");
                request.setAttribute("path", "CrewListPage.do");
            } else {
                // 크루 번호 파라미터 확인
                int view_crew_num = 0;
                if (request.getParameter("view_crew_num") != null) {
                    view_crew_num = Integer.parseInt(request.getParameter("view_crew_num"));
                } else {
                    request.setAttribute("msg", "잘못된 요청입니다.");
                    request.setAttribute("path", "CrewListPage.do");
                    forward.setPath(path);
                    forward.setRedirect(flagRedirect);
                    return forward; // 유효하지 않은 요청 시 바로 종료
                }

                // 크루 가입 처리
                MemberDTO memberDTO = new MemberDTO();
                MemberDAO memberDAO = new MemberDAO();
                memberDTO.setModel_member_id(member_id);
                memberDTO.setModel_member_crew_num(view_crew_num);
                memberDTO.setModel_member_condition("MEMBER_UPDATE_CREW");

                boolean flag = memberDAO.update(memberDTO);

                if (flag) {
                    // 업데이트 성공 시 세션 갱신
                    HttpSession session = request.getSession();
                    session.setAttribute("CREW_CHECK", view_crew_num);

                    request.setAttribute("msg", "해당 크루에 가입을 완료했습니다.");
                    request.setAttribute("path", "CrewPage.do");
                } else {
                    // 업데이트 실패 시
                    request.setAttribute("msg", "크루 가입에 실패했습니다.");
                    request.setAttribute("path", "CrewListPage.do");
                }
            }
        } catch (NumberFormatException e) {
            // 숫자 변환 실패 (유효하지 않은 파라미터)
            request.setAttribute("msg", "잘못된 요청입니다.");
            request.setAttribute("path", "CrewListPage.do");
        }
    }

    forward.setPath(path);
    forward.setRedirect(flagRedirect);
    return forward;
}
}
