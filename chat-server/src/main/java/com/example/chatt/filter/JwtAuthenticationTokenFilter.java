package com.example.chatt.filter;

import com.example.chatt.pojo.JwtUser;
import com.example.chatt.pojo.SuperUser;
import com.example.chatt.utils.JwtTokenUtil;
import com.example.chatt.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

//每个请求都会经过此过滤器一次
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;
    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取token
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            //放行
            filterChain.doFilter(request,response);
            return;
        }
        //解析token
        String username = jwtTokenUtil.getUsernameFromToken(token);
        //从redis中获取用户信息
        if (username.equals("admin")) {
             SuperUser user = redisCache.getCacheObject("login" + username);
            if (Objects.isNull(user)) {
                return;
            }
        } else {
            JwtUser jwtUser = redisCache.getCacheObject("login" + username);
            if (Objects.isNull(jwtUser)) {
                return;
            }
            //存入SecurityContextHolder
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(jwtUser.getUser().getUserId(),null,null);
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
        //放行
        filterChain.doFilter(request,response);
    }
}
