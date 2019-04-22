package com.sp.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.sp.member.Member;
import com.sp.member.MemberService;
import com.sp.member.SessionInfo;

public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	
	@Autowired
	private MemberService memberService;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		
		// 로그인 아이디 : authentication.getName();
		
		// 로그인 날짜 변경
		memberService.updateLastLogin(authentication.getName());
		
		// 로그인 정보 저장
		Member dto = memberService.readMember(authentication.getName());
		
		SessionInfo info = new SessionInfo();
		info.setUserId(dto.getUserId());
		info.setUserName(dto.getUserName());
		
		HttpSession session = request.getSession();
		session.setAttribute("member", info);
		
		super.onAuthenticationSuccess(request, response, authentication);
	}

}
