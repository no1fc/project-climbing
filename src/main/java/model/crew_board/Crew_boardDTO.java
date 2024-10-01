package model.crew_board;

public class Crew_boardDTO {
	private int model_crew_board_num;            //크루 글 번호
	private String model_crew_board_writer_id;     //크루 글 작성자
	private String model_crew_board_content;    //크루 글 내용
	private String model_crew_board_title;      //크루 글 제목
	private int model_crew_board_cnt; 		//크루 글 조회수
	//DTO에만 존재하는 데이터 
	private String model_crew_board_member_profile;	//사용자 프로필
	private int model_crew_board_total;		      // 전체 커뮤니티 게시글 총 개수
	private int model_crew_board_max_num; 	    // 페이지네이션 데이터
	private int model_crew_board_min_num;	      // 페이지네이션 데이터
	private String model_crew_board_condition;  // 개발자 데이터


	public String getModel_crew_board_member_profile() {
		return model_crew_board_member_profile;
	}

	public void setModel_crew_board_member_profile(String model_crew_board_member_profile) {
		this.model_crew_board_member_profile = model_crew_board_member_profile;
	}

	public int getModel_crew_board_num() {
		return model_crew_board_num;
	}

	public void setModel_crew_board_num(int model_crew_board_num) {
		this.model_crew_board_num = model_crew_board_num;
	}

	public String getModel_crew_board_writer_id() {
		return model_crew_board_writer_id;
	}

	public void setModel_crew_board_writer_id(String model_crew_board_writer_id) {
		this.model_crew_board_writer_id = model_crew_board_writer_id;
	}
	public int getModel_crew_board_cnt() {
		return model_crew_board_cnt;
	}

	public void setModel_crew_board_cnt(int model_crew_board_cnt) {
		this.model_crew_board_cnt = model_crew_board_cnt;
	}

	public String getModel_crew_board_content() {
		return model_crew_board_content;
	}

	public void setModel_crew_board_content(String model_crew_board_content) {
		this.model_crew_board_content = model_crew_board_content;
	}

	public String getModel_crew_board_title() {
		return model_crew_board_title;
	}

	public void setModel_crew_board_title(String model_crew_board_title) {
		this.model_crew_board_title = model_crew_board_title;
	}

	public int getModel_crew_board_total() {
		return model_crew_board_total;
	}

	public void setModel_crew_board_total(int model_crew_board_total) {
		this.model_crew_board_total = model_crew_board_total;
	}

	public int getModel_crew_board_max_num() {
		return model_crew_board_max_num;
	}

	public void setModel_crew_board_max_num(int model_crew_board_max_num) {
		this.model_crew_board_max_num = model_crew_board_max_num;
	}

	public int getModel_crew_board_min_num() {
		return model_crew_board_min_num;
	}

	public void setModel_crew_board_min_num(int model_crew_board_min_num) {
		this.model_crew_board_min_num = model_crew_board_min_num;
	}

	public String getModel_crew_board_condition() {
		return model_crew_board_condition;
	}

	public void setModel_crew_board_condition(String model_crew_board_condition) {
		this.model_crew_board_condition = model_crew_board_condition;
	}

	@Override
	public String toString() {
		return "Crew_boardDTO [model_crew_board_num=" + model_crew_board_num + ", model_crew_board_writer_id="
				+ model_crew_board_writer_id + ", model_crew_board_content=" + model_crew_board_content
				+ ", model_crew_board_title=" + model_crew_board_title + ", model_crew_board_cnt="
				+ model_crew_board_cnt + ", model_crew_board_total=" + model_crew_board_total
				+ ", model_crew_board_max_num=" + model_crew_board_max_num + ", model_crew_board_min_num="
				+ model_crew_board_min_num + ", model_crew_board_condition=" + model_crew_board_condition + "]";
	}

	
}
