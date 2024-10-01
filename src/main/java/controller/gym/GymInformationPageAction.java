package controller.gym;

import java.util.ArrayList;

import controller.common.Action;
import controller.common.ActionForward;
import controller.function.LoginCheck;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.battle_record.Battle_recordDAO;
import model.battle_record.Battle_recordDTO;
import model.favorite.FavoriteDAO;
import model.favorite.FavoriteDTO;
import model.gym.GymDAO;
import model.gym.GymDTO;
import model.member.MemberDAO;
import model.member.MemberDTO;

public class GymInformationPageAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		String path = "gymInformation.jsp"; // view에서 알려줄 예정
		boolean flag_Redirect = false; // 값을 전달해야하게 때문에 forward 방식으로 전달해야한다.

		//로그인 정보가 있는지 확인해주고
		String login[] = LoginCheck.Success(request, response);
		//사용자 아이디
		String member_id = login[0];
		//가입 크루 번호
		//String crew_check = login[1];

		//---------------------------------------------------------------------------
		//해당 페이지에서 공통으로 사용할 변수 and 객체
		//View에서 전달해주는 (암벽장 번호)변수
		int gym_num = Integer.parseInt(request.getParameter("VIEW_GYM_NUM"));
		String model_favorite = "delete"; //찜목록 데이터 초기화 없다면 delete 있다면 insert		
		
		//암벽장 정보 변수 및 객체
		GymDTO gymDTO = new GymDTO();
		GymDTO gymDTO_selectOne = new GymDTO();
		GymDAO gymDAO = new GymDAO();
		int model_gym_num = 0;
		String model_gym_name="";
		String model_gym_profile="";
		String model_gym_description="";
		String model_gym_location="";
		int model_gym_price=0;
		
		//해당 암벽장의 승리 크루전 객체
		Battle_recordDTO battle_recordDTO = new Battle_recordDTO();
		Battle_recordDAO battle_recordDAO = new Battle_recordDAO();
		
		//크루전 정보 변수 및 객체
		int model_gym_battle_num;
		String model_gym_battle_game_date;
		//---------------------------------------------------------------------------
		//암벽장 정보 로직 시작
		//View에서 전달해준 암벽장 번호를 gym DTO에 저장하고
		System.out.println("암벽장 PK : "+ gym_num);
		gymDTO.setModel_gym_num(gym_num);
		
		gymDTO.setModel_gym_condition("GYM_ONE");//TODO 컨디션값 입력해야함

		//gym selectOne으로 Model에 암벽장정보를 요청합니다.
		//데이터 : 암벽장 번호 / 암벽장 이름 / 암벽장 사진 / 암벽장 설명 / 암벽장 주소 / 암벽장 가격
		GymDTO data = gymDAO.selectOne(gymDTO);
		model_gym_num = data.getModel_gym_num();
		model_gym_name = data.getModel_gym_name();
		model_gym_profile = "https://"+data.getModel_gym_profile();
		model_gym_description = data.getModel_gym_description();
		model_gym_location = data.getModel_gym_location();
		String price = data.getModel_gym_price();
		model_gym_price = Integer.parseInt(price);
		//암벽장 정보 로직 종료
		//---------------------------------------------------------------------------
		//해당 암벽장에서 승리한 크루 목록 로직 시작
		//View에서 전달해준 암벽장 번호를 battle_record DTO에 저장하고
		battle_recordDTO.setModel_battle_record_condition("BATTLE_RECORD_ALL_WINNER_PARTICIPANT_GYM");//TODO 컨디션 추가해야함 selectAll 필요함
		battle_recordDTO.setModel_battle_record_gym_num(gym_num);
		//battle_record selectAll으로 Model에 해당 암벽장에서 승리한 크루 목록을 요청하고
		//데이터 : 승리크루 이름 / 승리크루 사진 / 승리크루 경기날짜 / MVP 이름
		ArrayList<Battle_recordDTO> model_battle_record_datas = battle_recordDAO.selectAll(battle_recordDTO);

		for (Battle_recordDTO battle_record : model_battle_record_datas) {
			battle_record.setModel_battle_record_crew_profile(request.getServletContext().getContextPath() + "/crew_img_foloder/" + battle_record.getModel_battle_record_crew_profile());
		}

		//해당 암벽장에서 승리한 크루 목록 로직 종료
		//---------------------------------------------------------------------------
		//해당 암벽장에 등록되어 있는 크루전 정보 로직 시작
		// View에서 전달해준 암벽장 번호를 DTO에 저장하고
		gymDTO_selectOne.setModel_gym_condition("GYM_ONE"); //TODO 컨디션 추가해야함
		gymDTO_selectOne.setModel_gym_num(gym_num);
		// Battle selectOne으로 Model에 해당 암벽장에서 크루전 정보 요청
		//데이터 : 크루전 번호 / 크루전 날짜
		GymDTO gym_data = gymDAO.selectOne(gymDTO_selectOne);
		model_gym_battle_num = gym_data.getModel_gym_battle_num();
		model_gym_battle_game_date = gym_data.getModel_gym_battle_game_date();
		//해당 암벽장에 등록되어 있는 크루전 정보 로직 종료
		//---------------------------------------------------------------------------
		//로그인한 사용자라면
		if(member_id != null) {
			//사용자 포인트 요청 로직 시작
			//사용자 아이디를 Member DTO에 저장하고
			MemberDTO memberDTO = new MemberDTO();
			MemberDAO memberDAO = new MemberDAO();
			memberDTO.setModel_member_condition("MEMBER_SEARCH_ID");//TODO 컨디션 추가해야함
			memberDTO.setModel_member_id(member_id);
			//Member selectOne으로 Model에 해당 사용자의 사용가능 포인트요청
			MemberDTO member_data = memberDAO.selectOne(memberDTO);
			int current_point = 0 ;
			//데이터 : 사용가능 포인트
			if(member_data != null) {
				current_point = member_data.getModel_member_current_point();
				//View로 사용 가능 포인트 전달 model_gym_member_current_point
				request.setAttribute("model_gym_member_current_point", current_point);
			}

			//사용자 포인트 요청 로직 종료
			//---------------------------------------------------------------------------
			//좋아요 여부 로직 시작
			//View에서 전달해준 암벽장 번호와 사용자 아이디를 favorite DTO에 저장하고
			FavoriteDTO favoriteDTO = new FavoriteDTO();
			FavoriteDAO favoriteDAO = new FavoriteDAO();
			favoriteDTO.setModel_favorite_gym_num(gym_num);
			favoriteDTO.setModel_favorite_member_id(member_id);
			//Favorite selectOne으로 Model에 좋아요 여부를 요청합니다.
			FavoriteDTO favorite_null_check = favoriteDAO.selectOne(favoriteDTO);
			//만약 없으면 좋아요 안한 사용자.	
			if(favorite_null_check != null) {
				//만약 있으면 좋아요 한 사용자.
				model_favorite = "insert";
				System.out.println("찜목록 if 안 : "+model_favorite);
			}
			System.out.println("찜목록 if 밖 : " + model_favorite);
			//좋아요 여부 로직 종료
		}//if(member_id != null) { 종료
		//---------------------------------------------------------------------------	
		//View로 암벽장 승리 크루 전달 model_battle_record_datas
		request.setAttribute("model_battle_record_datas", model_battle_record_datas);
		//View로 암벽장 정보 전달 
		/*
		model_gym_num
		model_gym_name
		model_gym_profile
		model_gym_description
		model_gym_location
		model_gym_price
		 */
		request.setAttribute("model_gym_num", model_gym_num);
		request.setAttribute("model_gym_name", model_gym_name);
		request.setAttribute("model_gym_profile", model_gym_profile);
		request.setAttribute("model_gym_description", model_gym_description);
		request.setAttribute("model_gym_location", model_gym_location);
		request.setAttribute("model_gym_price", model_gym_price);

		//View로 암벽장 크루전 정보 전달
		/*
		model_battle_num
		model_battle_game_date
		 */
		request.setAttribute("model_battle_num", model_gym_battle_num);
		request.setAttribute("model_gym_battle_game_date", model_gym_battle_game_date);

		//View로 좋아요 여부 전달 model_favorite
		System.out.println("GIP 167 model_favorite = "+model_favorite);
		request.setAttribute("model_favorite", model_favorite);

		ActionForward forward = new ActionForward();
		forward.setPath(path);
		forward.setRedirect(flag_Redirect);		
		return forward;
	}
}
