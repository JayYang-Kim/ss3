package com.sp.member;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("member.memberController")
public class MemberController {
	@Autowired
	private MemberService service;
	@Autowired
	private BCryptPasswordEncoder bcryptEncoder;
	
	@RequestMapping(value="/member/member", method=RequestMethod.GET)
	public String memberForm(Model model) {
		model.addAttribute("mode", "created");
		return ".member.member";
	}

	@RequestMapping(value="/member/member", method=RequestMethod.POST)
	public String memberSubmit(Member dto,
			Model model) {
		
		// 패스워드 암호화
		String pwd = bcryptEncoder.encode(dto.getUserPwd());
		dto.setUserPwd(pwd);
		
		int result=service.insertMember(dto);
		if(result==1) {
			StringBuffer sb=new StringBuffer();
			sb.append(dto.getUserName()+ "님의 회원 가입이 정상적으로 처리되었습니다.<br>");
			sb.append("메인화면으로 이동하여 로그인 하시기 바랍니다.<br>");
			
			model.addAttribute("message", sb.toString());
			model.addAttribute("title", "회원 가입");
			
			return ".member.complete";
		} 
		
		model.addAttribute("mode", "created");
		model.addAttribute("message", "아이디 중복으로 회원가입이 실패했습니다.");
			
		return ".member.member";
	}
	
	@RequestMapping(value="/member/login", method=RequestMethod.GET)
	public String loginForm(String login_error, Model model) {
		boolean bLoginError = login_error != null;
		
		if(bLoginError) {
			model.addAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
		}
		
		return ".member.login";
	}
	
	// 접근 권한이 없는 경우
	@RequestMapping(value="/member/noAuthorized")
	public String noAuthorized() {
		return ".member.noAuthorized";
	}
	
	// 세션이 만료된 경우
	@RequestMapping(value="/member/expired")
	public String expired() {
		return ".member.expired";
	}
	
	@RequestMapping(value="/member/pwd", method=RequestMethod.GET)
	public String pwdForm(
			String dropout,
			Model model) {
		
		if(dropout==null) {
			model.addAttribute("mode", "update");
		} else {
			model.addAttribute("mode", "dropout");
		}
		
		return ".member.pwd";
	}
	
	@RequestMapping(value="/member/pwd", method=RequestMethod.POST)
	public String pwdSubmit(
			@RequestParam String userPwd,
			@RequestParam String mode,
			Model model,
			HttpSession session
	     ) {
		
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		
		Member dto=service.readMember(info.getUserId());
		if(dto==null) {
			session.invalidate();
			return "redirect:/";
		}
		
		boolean bPwd = bcryptEncoder.matches(userPwd, dto.getUserPwd());
		
		if(! bPwd) {
			if(mode.equals("update")) {
				model.addAttribute("mode", "update");
			} else {
				model.addAttribute("mode", "dropout");
			}
			model.addAttribute("message", "패스워드가 일치하지 않습니다.");
			return ".member.pwd";
		}
		
		if(mode.equals("dropout")){
			// 게시판 테이블등 자료 삭제
			
			// 회원탈퇴 처리
			/*
			Map<String, Object> map = new HashMap<>();
			map.put("memberIdx", info.getMemberIdx());
			map.put("userId", info.getUserId());
			*/

			// 세션 정보 삭제
			session.removeAttribute("member");
			session.invalidate();

			StringBuffer sb=new StringBuffer();
			sb.append(dto.getUserName()+ "님의 회원 탈퇴 처리가 정상적으로 처리되었습니다.<br>");
			sb.append("메인화면으로 이동 하시기 바랍니다.<br>");
			
			model.addAttribute("title", "회원 탈퇴");
			model.addAttribute("message", sb.toString());
			
			return ".member.complete";
		}

		// 회원정보수정폼
		model.addAttribute("dto", dto);
		model.addAttribute("mode", "update");
		return ".member.member";
	}

	@RequestMapping(value="/member/update",
			method=RequestMethod.POST)
	public String updateSubmit(
			Member dto,
			Model model) {
		
		// 패스워드 암호화
		String pwd = bcryptEncoder.encode(dto.getUserPwd());
		dto.setUserPwd(pwd);
		
		service.updateMember(dto);
		
		StringBuffer sb=new StringBuffer();
		sb.append(dto.getUserName()+ "님의 회원정보가 정상적으로 변경되었습니다.<br>");
		sb.append("메인화면으로 이동 하시기 바랍니다.<br>");
		
		model.addAttribute("title", "회원 정보 수정");
		model.addAttribute("message", sb.toString());
		return ".member.complete";
	}

	@RequestMapping(value="/member/userIdCheck", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> idCheck(
			@RequestParam String userId
			) throws Exception {
		
		String p="true";
		Member dto=service.loginMember(userId);
		if(dto!=null)
			p="false";
		
		Map<String, Object> model=new HashMap<>();
		model.put("passed", p);
		return model;
	}
}
