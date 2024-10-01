package controller.common;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

//프로필 이미지를 저장하기 위해 @ 어노테이션을 설정한다.
@MultipartConfig(
		// 멀티파트 설정
		maxFileSize = 1024 * 50, // 한 파일을 저장한때 10KB
		maxRequestSize = 1024 * 50, // 여러 파일을 저장한때 50KB
		fileSizeThreshold = 1024 * 2, // 메모리에 저장할때 크기 2KB
		location = "/" // 파일이 저장되는 경로
		)
//view(Web)에서 보내는 Xxx.do 모든 요청을 받아옵니다.
@WebServlet("*.do")
public class FrontController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HandlerMapper mapping;
	public FrontController() {
		super();
		this.mapping = new HandlerMapper();
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doAction(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		doAction(request, response);
	}

	public void doAction(HttpServletRequest request, HttpServletResponse response) {
		//1. 서버 주소를 받아서 요청값 추출 및 확인
		String url = request.getRequestURI(); // 서버 전체 URL(주소)를 받아옵니다
		String cp = request.getContextPath(); // context Path 를 받아옵니다.
		String command = url.substring(cp.length()); // URL에서 context Path 길이 만큼 잘라와 요청을 확인합니다.
		//command 로그
		System.err.println("요청 : "+command);

		Action action = this.mapping.getAction(command); // 핸들러 맵핑에서 맞는 Action만 가져와서 사용해줍니다.
		ActionForward forward = null;
		try {
			forward = action.execute(request, response);
			System.out.println("ActionForward : "+forward.getPath());
		} catch (Exception e) {
			e.getStackTrace();
		}

		//forward 가 null 이 아니라면 요청값이 있는 것이기 때문에
		// Redirect(리다이렉트) / Forward(포워드) 인지 구분해줍니다.
		if(forward != null) {
			//만약 forward.isRedirect 가 true 일때 실행됩니다.
			if(forward.isRedirect()) {
				try {
					//forward 로 받아온 주소 값을 등록하여 페이지만 이동합니다.
					response.sendRedirect(forward.getPath());
					System.out.println("Redirect 요청 로그");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//만약 forward.isRedirect 가 false 일때 실행됩니다.
			else{
				//request 내장 메서드인 RequestDispatcher 에 주소를 입력해줍니다.
				RequestDispatcher dispatcher = request.getRequestDispatcher(forward.getPath());
				try {
					//데이터를 넘겨야하니 forward 방식으로 값을 같이 넘겨줍니다.
					dispatcher.forward(request, response);
					System.out.println("Forward 요청 로그");
				} catch (ServletException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//forward 가 null 이면 요청이 없이 들어온 것이기 때문에
		// 잘못된 요청으로 페이지를 넘겨야합니다.
		else{
			try {
				response.sendRedirect("InfoPage.do");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("forward null 로그");
		}
	}
}
