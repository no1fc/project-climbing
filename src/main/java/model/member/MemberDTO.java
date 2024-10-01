package model.member;

import java.sql.Date;

public class MemberDTO {
	private String model_member_id;              //아이디
	private String model_member_name;            //이름
	private String model_member_password;        //비밀번호
	private String model_member_phone;           //휴대폰 번호
	private String model_member_profile;         // 프로필사진 url
	private int model_member_current_point;      //사용가능한 포인트
	private int model_member_total_point;        //누적포인트
	private int model_member_crew_num;           //크루 FK
	private String model_member_crew_join_date;    //크루 가입날짜
	//FIXME SQL DATE형식이라 String으로 변환하여 줄것
	private String model_member_location;        //사용자 현재지역
	private Date model_member_registration_date; //회원가입날짜
	private String model_member_role;            //관리자 권한
	
	//DTO에만 존재하는 데이터
	private String model_member_grade_profile; //사용자의 등급 사진
	private String model_member_grade_name; //사용자의 등급 이름
	private int model_member_crew_current_size; //사용자의 크루의 현재 인원수
	private String model_member_condition;       //개발자 데이터
	private String model_member_crew_name;		//사용자가 속한 크루 이름
	private String model_member_crew_profile;	//사용자가 속한 크루 이미지 url
	private String model_member_crew_leader;	//사용자가 속한 크루장 pk
	public String getModel_member_id() {
		return model_member_id;
	}
	public void setModel_member_id(String model_member_id) {
		this.model_member_id = model_member_id;
	}
	public String getModel_member_name() {
		return model_member_name;
	}
	public void setModel_member_name(String model_member_name) {
		this.model_member_name = model_member_name;
	}
	public String getModel_member_password() {
		return model_member_password;
	}
	public void setModel_member_password(String model_member_password) {
		this.model_member_password = model_member_password;
	}
	public String getModel_member_phone() {
		return model_member_phone;
	}
	public void setModel_member_phone(String model_member_phone) {
		this.model_member_phone = model_member_phone;
	}
	public String getModel_member_profile() {
		return model_member_profile;
	}
	public void setModel_member_profile(String model_member_profile) {
		this.model_member_profile = model_member_profile;
	}
	public int getModel_member_current_point() {
		return model_member_current_point;
	}
	public void setModel_member_current_point(int model_member_current_point) {
		this.model_member_current_point = model_member_current_point;
	}
	public int getModel_member_total_point() {
		return model_member_total_point;
	}
	public void setModel_member_total_point(int model_member_total_point) {
		this.model_member_total_point = model_member_total_point;
	}
	public int getModel_member_crew_num() {
		return model_member_crew_num;
	}
	public void setModel_member_crew_num(int model_member_crew_num) {
		this.model_member_crew_num = model_member_crew_num;
	}
	public String getModel_member_crew_join_date() {
		return model_member_crew_join_date;
	}
	public void setModel_member_crew_join_date(String model_member_crew_join_date) {
		this.model_member_crew_join_date = model_member_crew_join_date;
	}
	public String getModel_member_location() {
		return model_member_location;
	}
	public void setModel_member_location(String model_member_location) {
		this.model_member_location = model_member_location;
	}
	public Date getModel_member_registration_date() {
		return model_member_registration_date;
	}
	public void setModel_member_registration_date(Date model_member_registration_date) {
		this.model_member_registration_date = model_member_registration_date;
	}
	public String getModel_member_role() {
		return model_member_role;
	}
	public void setModel_member_role(String model_member_role) {
		this.model_member_role = model_member_role;
	}
	public String getModel_member_condition() {
		return model_member_condition;
	}
	public void setModel_member_condition(String model_member_condition) {
		this.model_member_condition = model_member_condition;
	}
	public String getModel_member_crew_name() {
		return model_member_crew_name;
	}
	public void setModel_member_crew_name(String model_member_crew_name) {
		this.model_member_crew_name = model_member_crew_name;
	}
	public String getModel_member_crew_profile() {
		return model_member_crew_profile;
	}
	public void setModel_member_crew_profile(String model_member_crew_profile) {
		this.model_member_crew_profile = model_member_crew_profile;
	}
	public String getModel_member_grade_profile() {
		return model_member_grade_profile;
	}
	public void setModel_member_grade_profile(String model_member_grade_profile) {
		this.model_member_grade_profile = model_member_grade_profile;
	}
	public String getModel_member_grade_name() {
		return model_member_grade_name;
	}
	public void setModel_member_grade_name(String model_member_grade_name) {
		this.model_member_grade_name = model_member_grade_name;
	}
	public int getModel_member_crew_current_size() {
		return model_member_crew_current_size;
	}
	public void setModel_member_crew_current_size(int model_member_crew_current_size) {
		this.model_member_crew_current_size = model_member_crew_current_size;
	}
	public String getModel_member_crew_leader() {
		return model_member_crew_leader;
	}
	public void setModel_member_crew_leader(String model_member_crew_leader) {
		this.model_member_crew_leader = model_member_crew_leader;
	}
	@Override
	public String toString() {
		return "MemberDTO [model_member_id=" + model_member_id + ", model_member_name=" + model_member_name
				+ ", model_member_password=" + model_member_password + ", model_member_phone=" + model_member_phone
				+ ", model_member_profile=" + model_member_profile + ", model_member_current_point="
				+ model_member_current_point + ", model_member_total_point=" + model_member_total_point
				+ ", model_member_crew_num=" + model_member_crew_num + ", model_member_crew_join_date="
				+ model_member_crew_join_date + ", model_member_location=" + model_member_location
				+ ", model_member_registration_date=" + model_member_registration_date + ", model_member_role="
				+ model_member_role + ", model_member_grade_profile=" + model_member_grade_profile
				+ ", model_member_grade_name=" + model_member_grade_name + ", model_member_crew_current_size="
				+ model_member_crew_current_size + ", model_member_condition=" + model_member_condition
				+ ", model_member_crew_name=" + model_member_crew_name + ", model_member_crew_profile="
				+ model_member_crew_profile + ", model_member_crew_leader=" + model_member_crew_leader + "]";
	}
	
	
	
}