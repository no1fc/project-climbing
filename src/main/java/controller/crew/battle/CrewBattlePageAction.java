package controller.crew.battle;

import java.util.ArrayList;

import controller.common.Action;
import controller.common.ActionForward;
import controller.function.LoginCheck;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.battle.BattleDAO;
import model.battle.BattleDTO;

public class CrewBattlePageAction implements Action{

   @Override
   public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
      ActionForward forward = new ActionForward();
      String path = "crewBattleMain.jsp";//크루전 메인 페이지로 이동
      boolean flagRediect = false;//로그인정보나 크루정보
      /*
       * 페이지네이션을 하기위해 뷰에게서 page_num을 받아오고
       * 페이지네이션 계산 후
       * memberDTO와 DAO 생성
       * DTO에 member_id를 set해주고
       * 내 크루를 찾기위해 selectOne
       * 컨디션은 MEMBER_SEARCH_MY_CREW
       * 
       * battleDTO와 DAO 생성
       * DTO에 battle_crew_num을 set해주고
       * 내 크루 크루전을 찾기위해 selectOne
       * 컨디션은 VATTLE_SEARCH_MEMBER_BATTLE
       * 
       * battleDTO를 따로 하나 더 생성해서
       * 크루전 목록  selectAll
       * min_num과 max_num을 set
       * 컨디션은 BATTLE_ALL_ACTIVE
       * 
       * battleDTO 또 하나 더 생성해서
       * 크루전 총 개수 selectOne
       * 컨디션은 BATTLE_ONE_COUNT_ACTIVE
       */

      // 로그인 정보 보내주기 네비게이션 바 때문에
      String login[] = LoginCheck.Success(request, response);
      String member_id = login[0];
      //선택한 크루 정보
      int crew_num = 0;
      if(member_id!=null) {
         System.err.println("CrewBattlePageAction crew_num = "+login[1]);
         crew_num = Integer.parseInt(login[1]);
      }
      int pageNum = 1; // 페이지 번호 초기화
      // 페이지네이션 부분
      if(request.getParameter("page") != null) {
         pageNum = Integer.parseInt(request.getParameter("page")); // 페이지 번호가 있을 경우 변환하여 저장
      }
      int boardSize = 10; // 한 페이지에 표시할 게시글 수 설정
      int minBoard = 1; // 최소 게시글 수 초기화
      int maxBoard = 1; // 최대 게시글 수 초기화

      // 페이지 번호에 따라 최소 및 최대 게시글 수 설정
      if(pageNum <= 1) {
         // 페이지 번호가 1 이하일 경우
         minBoard = 1; // 최소 게시글 번호를 1로 설정
         maxBoard = minBoard * boardSize; // 최대 게시글 번호 계산
      }
      else {
         // 페이지 번호가 2 이상일 경우
         minBoard = ((pageNum - 1) * boardSize) + 1; // 최소 게시글 번호 계산
         maxBoard = pageNum * boardSize; // 최대 게시글 번호 계산
      }

      int listNum = 0; // 게시글 총 개수를 저장할 변수 초기화

      


      BattleDTO battleDTO = new BattleDTO();
      BattleDAO battleDAO = new BattleDAO();
      battleDTO.setModel_battle_crew_num(crew_num);//크루pk
      battleDTO.setModel_battle_condition("BATTLE_SEARCH_MEMBER_BATTLE");//내 크루전 컨디션
      battleDTO = battleDAO.selectOne(battleDTO);
      if(battleDTO != null) {
    	  battleDTO.setModel_battle_gym_profile("https://"+battleDTO.getModel_battle_gym_profile());
    	  System.out.println("이미지 : "+battleDTO.getModel_battle_gym_profile());    	  
      }

      BattleDTO battle_data = new BattleDTO();
      battle_data.setModel_battle_min_num(minBoard);
      battle_data.setModel_battle_max_num(maxBoard);
      battle_data.setModel_battle_condition("BATTLE_ALL_ACTIVE");//전체 크루전 목록 컨디션
      ArrayList<BattleDTO> model_battle_datas = battleDAO.selectAll(battle_data);
      
      if(model_battle_datas != null) {
    	  for(BattleDTO data : model_battle_datas) {
    		  System.out.println("이미지 : "+data.getModel_battle_gym_profile());
    		  data.setModel_battle_gym_profile("https://"+data.getModel_battle_gym_profile());
    	  }    	  
      }

      BattleDTO battle_count = new BattleDTO();
      battle_count.setModel_battle_condition("BATTLE_ONE_COUNT_ACTIVE");//크루전 총 개수 컨디션(페이지네이션)
      battle_count = battleDAO.selectOne(battle_count);
      listNum = battle_count.getModel_battle_total();
      
      
      request.setAttribute("model_my_battle", battleDTO);//내크루전(암벽장, 주소, 날짜, 번호)
      request.setAttribute("model_battle_total", listNum);//전체 게시글 총수 
      request.setAttribute("currentPage", pageNum);//현재페이지번호
      request.setAttribute("model_battle_datas", model_battle_datas);//크루전목록

      forward.setPath(path);
      forward.setRedirect(flagRediect);
      return forward;
   }

}
