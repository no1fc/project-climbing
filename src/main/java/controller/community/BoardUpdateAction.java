package controller.community;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import controller.common.Action;
import controller.common.ActionForward;
import controller.function.CKEditorDeleteFile;
import controller.function.LoginCheck;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.board.BoardDAO;
import model.board.BoardDTO;

public class BoardUpdateAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		// 기본으로 넘어가야하는 페이지와 redirect 여부를 설정
		ActionForward forward = new ActionForward();
		String path = "MYPAGEPAGEACTION.do"; // 마이페이지로
		boolean flagRedirect = false; // 포워드 방식

		// 로그인 정보가 있는지 확인
		String login[] = LoginCheck.Success(request, response);
		String member_id = login[0]; // 세션에 있는 사용자의 아이디
		System.out.println("로그인 확인: " + member_id);

		// 만약 로그인 정보가 없다면
		if (member_id == null) {
			// 로그인 페이지로 전달
			path = "LOGINPAGEACTION.do";

		} else {
			// 로그인이 되어있다면
			// 업데이트 가능
			String board_title = request.getParameter("VIEW_TITLE"); // 제목 받기
			String board_location = request.getParameter("VIEW_BOARD_LOCATION"); // 제목 받기
			String board_content = request.getParameter("VIEW_CONTENT"); // 내용 받기
			int board_num = Integer.parseInt(request.getParameter("VIEW_BOARD_NUM")); // 번호 받기

			System.out.println("게시글 제목: " + board_title);
			System.out.println("게시글 지역: " + board_location);
			System.out.println("게시글 작성자 ID: " + member_id);
			System.out.println("게시글 내용: " + board_content);
			System.out.println("게시글 번호: " + board_num);
			//------------------------------------------------------------------------
			//CKEditor 제거된 이미지 제거하는 로직
			//UTF-8 형식으로 보내주니 UTF-8 로 인코딩 해주고
			try {
				request.setCharacterEncoding("UTF-8");
			} catch (UnsupportedEncodingException e) {
				System.out.println("인코딩 실패");
			}		
			//세션을 불러와서
			HttpSession session = request.getSession();
			//세션에 저장되어 있는 폴더 개수를 가져옵니다. 삼항연산자로 만약 세션값이 null이 아니라면 정수형으로 변경하여 가져오도록합니다.
			int folder_session = (session.getAttribute("UPDATE_FOLDER_NUM") != null) ? (Integer)session.getAttribute("UPDATE_FOLDER_NUM"):0;

			//View에서 보내준 내용 확인합니다.
			System.out.println(board_content);
			//세션에 있는 값이 0보다 크다면 이미지 폴더가 생성된 것으로 if문 실행
			if(folder_session > 0) {
				//폴더를 명시해줍니다.
				String folder = "/board_img/"+member_id+"/"+folder_session;

				//명시된 폴더를 추가해주고 내 서버의 폴더주소를 불러옵니다.
				String uploadPath = request.getServletContext().getRealPath(folder);

				System.out.println("BoardInsertAction.java 확인용 로그 : "+uploadPath);

				//넘어온 이미지와 서버에 저장된 이미지를 체크하여
				//없는 이미지는 삭제해줍니다.
				CKEditorDeleteFile.imgDelete(board_content, uploadPath);
			}
			//------------------------------------------------------------------------

			BoardDTO boardDTO = new BoardDTO();
			BoardDAO boardDAO = new BoardDAO();

			boardDTO.setModel_board_num(board_num);
			boardDTO.setModel_board_content(board_content);
			boardDTO.setModel_board_location(Location(board_location));
			boardDTO.setModel_board_title(board_title);

			boardDTO.setModel_board_condition("BOARD_UPDATE_CONTENT_TITLE"); // 글 수정 컨디션
			boolean updateFlag = boardDAO.update(boardDTO); // 업데이트


			request.setAttribute("MEMBER_ID", login); // 로그인한 사용자 정보
		}

		forward.setPath(path);
		forward.setRedirect(flagRedirect);
		return forward;
	}
	/* 뷰에서 전달받은 지역 값을 실제 지역명으로 변환하는 함수
	 */
	public String Location(String view_Location) {
		Map<String, String> locationMap = new HashMap<String, String>();

		locationMap.put("SEOUL", "서울특별시");
		locationMap.put("GYEONGGI", "경기도");
		locationMap.put("INCHEON", "인천광역시");       
		locationMap.put("SEJONG", "세종특별자치도");
		locationMap.put("BUSAN", "부산광역시");
		locationMap.put("DAEGU", "대구광역시");
		locationMap.put("DAEJEON", "대전광역시");
		locationMap.put("GWANGJU", "광주광역시");
		locationMap.put("ULSAN", "울산광역시");
		locationMap.put("CHUNGCHEONGNAMDO", "충청남도");
		locationMap.put("CHUNGCHEONGBUKDO", "충청북도");
		locationMap.put("JEONLANAMDO", "전라남도");
		locationMap.put("JEONLABUKDO", "전라북도");
		locationMap.put("GYEONGSANGNAMDO", "경상남도");
		locationMap.put("GYEONGSANGBUKDO", "경상북도");
		locationMap.put("GANGWONDO", "강원도");
		locationMap.put("CHUNGNAM", "충청남도");

		return locationMap.getOrDefault(view_Location, "서울특별시"); // 기본값은 서울특별시
		//getOrDefault  Map 인터페이스에서 제공하는 메서드로, 
		//특정 키에 해당하는 값을 반환하되,
		//만약 그 키가 존재하지 않으면 기본값을 반환하는 역할을 합니다.
	}
}
