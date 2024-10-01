package controller.gym;

import java.util.ArrayList;

import controller.common.Action;
import controller.common.ActionForward;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.gym.GymDAO;
import model.gym.GymDTO;

public class GymMainPageAction implements Action {

   @Override
   public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
      String path = "gymMain.jsp"; // view에서 알려줄 예정
      boolean flag_Redirect = false; // 값을 전달해야하게 때문에 forward 방식으로 전달해야한다.

      //---------------------------------------------------------------------------
      //해당 페이지에서 공통으로 사용할 변수 and 객체
      //View에서 전달해주는 (페이지 번호)변수
      String view_gym_page_num = request.getParameter("page");

      GymDAO gymDAO = new GymDAO();
      GymDTO gymDTO_conut = new GymDTO();
      GymDTO gymDTO_selectALL = new GymDTO();

      //---------------------------------------------------------------------------
      //페이지 네이션을 위해 암벽장 전체 개수를 요청 selectOne
      //페이지 네이션을 위한 페이지 개수를 구하는 로직을 구현
      int page_num = 1; // page_num 초기 변수 지정
      if(view_gym_page_num != null) {
         page_num = Integer.parseInt(view_gym_page_num);         
      }
      int gym_size = 5; // 한 페이지에 표시할 게시글 수 설정
      int min_gym = 1; // 최소 게시글 수 초기화
      int max_gym = 1; // 최대 게시글 수 초기화

      // 페이지 번호에 따라 최소 및 최대 게시글 수 설정
      if(page_num <= 1) {
         // 페이지 번호가 1 이하일 경우
         min_gym = 1; // 최소 게시글 번호를 1로 설정
         max_gym = min_gym * gym_size; // 최대 게시글 번호 계산
      }
      else {
         // 페이지 번호가 2 이상일 경우
         min_gym = ((page_num - 1) * gym_size) + 1; // 최소 게시글 번호 계산
         max_gym = page_num * gym_size; // 최대 게시글 번호 계산
      }
      //페이지네이션 값과 condition 값을 DTO에 추가하여 (6개출력)
      gymDTO_conut.setModel_gym_max_num(max_gym);
      gymDTO_conut.setModel_gym_min_num(min_gym);
      gymDTO_conut.setModel_gym_condition("GYM_ONE_COUNT"); //컨디션 추가해야함

      //암벽장 총 개수를 요청 selectOne
      GymDTO model_gym_total = gymDAO.selectOne(gymDTO_conut);
      
      System.out.println("min_gym"+min_gym);
      System.out.println("max_gym"+max_gym);
      // 페이지네이션 에 표시할 min_gym ~ max_gym까지 를 설정
      gymDTO_selectALL.setModel_gym_min_num(min_gym);
      gymDTO_selectALL.setModel_gym_max_num(max_gym);
      //암벽장 리스트를 model에 요청 selectAll
      //암벽장 테이블에서 받을 값(암벽장 번호 / 암벽장 이름 / 암벽장 주소)
      ArrayList<GymDTO> model_gym_datas = gymDAO.selectAll(gymDTO_selectALL);
      System.out.println(model_gym_datas);
      //------------------------------------------------------------
      //지도 API를 사용하기 위해 json 형식으로 보내는 로직
      String json = "[";
      //model_gym_datas 비어 있지 않다면
      if(!model_gym_datas.isEmpty()) {
         for (GymDTO data_gym : model_gym_datas) {
            // datas(크롤링한 암벽장 데이터)만큼 암벽장 이름과
            json += "{\"title\":\"" + data_gym.getModel_gym_name() + "\",";
            // 암벽장 장소를 json 형식으로 쓰여져있는 String 값으로 추가해서
            json += "\"address\":\"" + data_gym.getModel_gym_location() + "\"},";
         }
         // 끝에있는 쉼표 제거한 뒤 저장
         json = json.substring(0, json.lastIndexOf(",")); 
      }
      json+="]";
      System.out.println(json);
      //------------------------------------------------------------

      //암벽장 리스트를 View로 전달
      request.setAttribute("model_gym_datas", model_gym_datas);

      //암벽장 전체 개수를 View로 전달
      request.setAttribute("model_gym_total", model_gym_total.getModel_gym_total());

      //암벽장 페이지 페이지 번호를 전달.
      request.setAttribute("page_num", page_num);
      
      //json 형식 데이터 전송
      request.setAttribute("datas", json);

      ActionForward forward = new ActionForward();
      forward.setPath(path);
      forward.setRedirect(flag_Redirect);      
      return forward;
   }

}
