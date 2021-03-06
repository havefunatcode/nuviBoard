package com.board.nuviBoard.config.Security;



import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

//Jwt가 유요한 토큰인지 판단하는 필터이다. UserPasswordAuthentication 필터 앞에 설정할 내용이다.
@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {
    //GenericFilterBean란 Filter의 확장 버전으로 Spring의 설정 정보 사용 가능!

    private JwtTokenProvider jwtTokenProvider;

    // Jwt Provier 주입
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // Request로 들어오는 Jwt Token의 유효성을 검증(jwtTokenProvider.validateToken)하는 filter를 filterChain에 등록합니다.
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        //여기서 뭘하냐=>필터에 구현할 로직을 작성?

        //HttpServletRequest-> 클라이언트로 부터 오는 요청 정보를 담는 객체이다.
        //SecurityContextHolder ->스프링 시큐리티가 인증한 내용을 담기 위해 사용

        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request); //resolve토큰은 헤더를 분리, 토큰만 가져온다.
        if (token != null && jwtTokenProvider.validateToken(token)) { //토큰이 있고 유효기간이 만료되지 않았다면
            Authentication auth = jwtTokenProvider.getAuthentication(token); //인증 정보를 가져온다.
//            log.info(auth.toString());
            SecurityContextHolder.getContext().setAuthentication(auth); //인증한 내용을 Context 홀더에 담아준다.
        }
        filterChain.doFilter(request, response); //요청, 응답에 대한 필터링 진행
    }
}