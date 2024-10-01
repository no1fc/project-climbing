package controller.main;

import java.util.ArrayList;

import controller.common.Action;
import controller.common.ActionForward;
import controller.function.LoginCheck;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.battle.BattleDAO;
import model.battle.BattleDTO;
import model.board.BoardDAO;
import model.board.BoardDTO;
import model.member.MemberDAO;
import model.member.MemberDTO;

public class MainPageAction implements Action{

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = new ActionForward();
		String path = "main.jsp"; // 메인 페이지로 이동
        boolean flagRedirect = false; // 포워드 방식 사용 여부 설정 (false = forward 방식)

        // 로그인 정보 보내주기 네비게이션 바 때문에
        String login[] = LoginCheck.Success(request, response);
        System.out.println("log : 로그인 성공 " + login[0] );
        //크루전 정보 부분
        //배틀 DTO, DAO로 selectAll - 크루전 정보 보내주기 컨디션
        BattleDAO battleDAO = new BattleDAO();
        BattleDTO battleDTO = new BattleDTO();
        battleDTO.setModel_battle_condition("BATTLE_ALL_TOP4");//크루전 정보4개 컨디션        
        
        ArrayList<BattleDTO> model_battle_datas = battleDAO.selectAll(battleDTO);
        //암벽장 프로필 생각
       
        String filename = battleDTO.getModel_battle_gym_profile();
//        프로필foreach하고 set으로 주소값추가
        
        for(BattleDTO data : model_battle_datas) {
            String contextPath = request.getServletContext().getContextPath();
            String profilePath = contextPath + "/profile_img/" + data.getModel_battle_gym_profile();
            data.setModel_battle_gym_profile(profilePath);
        }
        request.setAttribute("model_battle_datas", model_battle_datas);
        
        //크루랭킹부분
        //크루프로필 생각				
        MemberDAO memberDAO = new MemberDAO();
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setModel_member_condition("MEMBER_ALL_TOP10_CREW_RANK");//크루랭킹 10개 컨디션
        
        ArrayList<MemberDTO> model_crew_rank_datas = memberDAO.selectAll(memberDTO);
        //model_crew_rank_datas에 담아서 보내주기
                
        //model_member_rank_datas에 담아서 보내기
        for(MemberDTO data : model_crew_rank_datas) {
        	String contextPath = request.getServletContext().getContextPath();
        	String profilePath = contextPath + "/crew_img_folder/" + data.getModel_member_crew_profile();
        	data.setModel_member_crew_profile(profilePath);
        }
        request.setAttribute("model_crew_rank_datas", model_crew_rank_datas);
        
        
        
        //개인 랭킹 부분
        MemberDTO datas = new MemberDTO();
        datas.setModel_member_condition("MEMBER_ALL_TOP10_RANK");//개인 랭킹 10개 컨디션
        
        ArrayList<MemberDTO> model_member_rank_datas = memberDAO.selectAll(datas);
        for (MemberDTO data : model_member_rank_datas) {
            String profileFileName = data.getModel_member_profile();
            String contextPath = request.getServletContext().getContextPath();
            String profilePath = contextPath + "/profile_img/" + profileFileName;
            data.setModel_member_profile(profilePath);
        }
        request.setAttribute("model_member_rank_datas", model_member_rank_datas);
                
        //board DTO, DAO로 selectAll - 게시판 6개 컨디션
        //글6개 부분
        BoardDAO boardDAO = new BoardDAO();
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setModel_board_condition("BOARD_ALL_ROWNUM");//글 6개 컨디션
                
        ArrayList<BoardDTO> model_board_datas = boardDAO.selectAll(boardDTO);
        request.setAttribute("model_board_datas", model_board_datas);

       //model_board_datas에 담아서 보내기
        
        forward.setPath(path);
        forward.setRedirect(flagRedirect);
        return forward;
	}

}
