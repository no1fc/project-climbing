package controller.store;

import java.util.ArrayList;

import controller.common.Action;
import controller.common.ActionForward;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.product.ProductDTO;

public class StorePageAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		String path = "store.jsp"; // view에서 알려줄 예정 alert 창 띄우기 위한 JavaScript 페이지
		boolean flag_Redirect = false; // 값을 전달해야하게 때문에 forward 방식으로 전달해야한다.
		
		//------------------------------------------------------------
		//TODO 해당 페이지에서 공통으로 사용할 변수 and 객체
		//product_datas 상품이 담겨 있는 session 불러오기
//		HttpSession session = request.getSession();
//		ArrayList<ProductDTO> datas = (ArrayList<ProductDTO>)session.getAttribute("product_datas");
		ServletContext context = request.getServletContext();
		ArrayList<ProductDTO> datas = (ArrayList<ProductDTO>)context.getAttribute("product_datas");
		if(datas == null) {
			path = "info.jsp";
			request.setAttribute("path", "MAINPAGEACTION.do");
			request.setAttribute("msg", "현재 상품이 없습니다.");
		}
		else {
			

		ArrayList<ProductDTO> model_product_datas = new ArrayList<ProductDTO>();
		//view에서 보내준 page_num 확인
		String view_page_num = request.getParameter("page");
		//------------------------------------------------------------
		//session 에 담겨 있는 상품 목록을 출력해주는 로직 시작
		//TODO (페이지 번호 / 상품 총 개수) 를 구하는 로직을 작성
		int page_num = 1; // page_num 초기 변수 지정
		if(view_page_num != null) {
			page_num = Integer.parseInt(view_page_num);			
		}
        int gym_size = 12; // 한 페이지에 표시할 게시글 수 설정
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
        System.out.println("모든 상품 : "+datas);
    	System.out.println("최소 번호 : " + min_gym);
    	System.out.println("최대 번호 : " + max_gym);

    	System.out.println("상품 개수1 : "+ datas.size());
        int product_total = datas.size();
        System.out.println("상품 개수2 : "+product_total);
        
        if(datas.size() < gym_size) {
        	max_gym = datas.size();
            System.out.println("상품 개수 12 미만 : "+max_gym);
        }
        else if(datas.size() <= max_gym){
        	max_gym = datas.size();
        }
        
        for(int i = min_gym-1; i < max_gym; i++) {
        	System.out.println("상품 번호 : " + i);
        	model_product_datas.add(datas.get(i));
        }
        
        System.out.println("넘어갈 모든 상품 :"+model_product_datas);
		// 페이지 번호 : page_num
        request.setAttribute("page_num", page_num);
		// 상품의 총 개수 : product_total
        request.setAttribute("product_total", product_total);
        //상품 목록 전달
        request.setAttribute("model_product_datas", model_product_datas);
        
		//session 에 담겨 있는 상품 목록을 출력해주는 로직 종료
		//------------------------------------------------------------
		}
		
		ActionForward forward = new ActionForward();
		forward.setPath(path);
		forward.setRedirect(flag_Redirect);		
		return forward;
	}

}
