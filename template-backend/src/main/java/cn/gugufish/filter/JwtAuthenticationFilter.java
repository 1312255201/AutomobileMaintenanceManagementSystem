package cn.gugufish.filter;

import cn.gugufish.utils.Const;
import cn.gugufish.utils.JwtUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 用于对请求头中Jwt令牌进行校验的工具，为当前请求添加用户验证信息
 * 并将用户的ID存放在请求对象属性中，方便后续使用
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    JwtUtils utils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        DecodedJWT jwt = utils.resolveJwt(authorization);
        if(jwt != null) {
            UserDetails user = utils.toUser(jwt);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.setAttribute(Const.ATTR_USER_ID, utils.toId(jwt));
            
            // Extract role (assuming single role)
            String role = user.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("user");
            if (role.startsWith("ROLE_")) role = role.substring(5); // Remove ROLE_ prefix if exists, usually Spring Security adds it? 
            // My previous code used roles like "admin", "repairman". Let's check how they are stored.
            // AccountServiceImpl: .roles(account.getRole()) -> roles are stored as "admin", "repairman" in DB.
            // Spring Security might wrap them. But here we are manually building UserDetails.
            // JwtUtils.toUser gets authorities from claims.
            // JwtUtils.createJwt puts authorities from user.getAuthorities().
            // AccountServiceImpl returns User.withUsername(...).roles(...)
            // Spring Security `roles()` method automatically adds "ROLE_" prefix.
            // So if DB has "admin", token has "ROLE_admin".
            // So I should strip "ROLE_".
            if (role.startsWith("ROLE_")) role = role.substring(5);
            request.setAttribute(Const.ATTR_USER_ROLE, role);
        }
        filterChain.doFilter(request, response);
    }
}
