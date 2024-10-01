package controller.ranking;

import java.util.ArrayList;

import controller.common.Action;
import controller.common.ActionForward;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.grade.GradeDAO;
import model.grade.GradeDTO;
import model.member.MemberDAO;
import model.member.MemberDTO;

public class CrewRankingPageAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		String path = "crewRank.jsp"; // view에서 알려줄 예정
		boolean flag_Redirect = false; // 값을 전달해야하게 때문에 forward 방식으로 전달해야한다.

		//크루 랭킹을 model에 요청 point 순으로 selectAll 예정
		//랭킹 페이지가 두개 이기 때문에 model에 condition 값 전달
		MemberDAO memberDAO = new MemberDAO();
		MemberDTO memberDTO = new MemberDTO();
		//TODO 컨디션값 받아서 수정해야함
		memberDTO.setModel_member_condition("MEMBER_ALL_CREW_RANK");

		//요청 값 : 전체 point / 등급 이미지 / 크루 이름 / 크루장 / 크루원명
		ArrayList<MemberDTO> model_member_datas = memberDAO.selectAll(memberDTO);

		//등급 이미지는 서버에 저장해 두었기 때문에
		//model에서 받아온 등급 이미지 앞에 서버 주소를 추가해줍니다.
		//----------------------------------------------------------

		//트리거 사용안할시 아래 코드 사용
		GradeDTO gradeDTO = new GradeDTO();
		GradeDAO gradeDAO = new GradeDAO();

		//model에 요청해서 등급 정보를 전부 불러오고 현재 등급 9개
		ArrayList<GradeDTO> model_grade_datas = gradeDAO.selectAll(gradeDTO);

		//불러온 랭킹에 등급 사진을 추가하기 위해 for문을 사용
		for (MemberDTO data : model_member_datas) {
			int member_total_point = data.getModel_member_total_point();

			//member_total_point 가 0보다 크면 for문 실행
			if(0 <= member_total_point) {
				//해당 등급의 최소 점수와 최대 점수를 비교
				//등급개수 만큼 for문을 돌려서 확인
				for (int i = 0; i < model_grade_datas.size(); i++) {
					int grade_min = model_grade_datas.get(i).getModel_grade_min_point();
					int grade_max = model_grade_datas.get(i).getModel_grade_max_point();

					//해당 등급 최소 점수보다 사용자의 점수가 크거나 같고 //사용자의 점수가 해당 등급의 최대 점수보다 작거나 같으면
					if(grade_min <= member_total_point && member_total_point <= grade_max) {
						data.setModel_member_grade_profile(request.getServletContext().getContextPath()+"/grade_folder/" + model_grade_datas.get(i).getModel_grade_profile());
						data.setModel_member_grade_name(model_grade_datas.get(i).getModel_grade_name());
					}
					//만약 등급의 최대 점수보다 사용자 점수가 크다면
					else if(grade_max < member_total_point) {
						data.setModel_member_grade_profile(request.getServletContext().getContextPath()+"/grade_folder/" + model_grade_datas.get(i).getModel_grade_profile());
						data.setModel_member_grade_name(model_grade_datas.get(i).getModel_grade_name());
						break;
					}
					
				}//	for (int i = 0; i < model_grade_datas.size(); i++) { 이미지 비교 for문 종료
				
			}// if(0 < member_total_point) { 종료
		}//	for (MemberDTO data : model_member_datas) { 랭킹 등급이미지 등록 for문 종료



		//받아온 값을 model_member_datas 값에 저장하여 전달
		request.setAttribute("model_member_datas", model_member_datas);

		ActionForward forward = new ActionForward();
		forward.setPath(path);
		forward.setRedirect(flag_Redirect);		
		return forward;
	}

}
