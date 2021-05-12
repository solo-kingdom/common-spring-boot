package pub.wii.common.spring.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import pub.wii.common.spring.annotation.Auth;
import pub.wii.common.spring.annotation.NonAuth;
import pub.wii.common.spring.model.Token;
import pub.wii.common.spring.token.TokenManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Slf4j
@Component
@ConditionalOnProperty(value = "auth.enable")
public class AuthInterceptor implements HandlerInterceptor {

    TokenManager tokenManager;

    @Value("${auth.enable:false}")
    boolean enable;

    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (enable && handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();

            if (method.getAnnotation(Auth.class) != null ||
                    (method.getDeclaringClass().getAnnotation(Auth.class) != null &&
                            method.getAnnotation(NonAuth.class) == null)) {
                String tk = request.getHeader("authorization");
                Token token = tokenManager.check(tk);
                if (token == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return false;
                }
                request.setAttribute("auth.uid", token.getUid());
                request.setAttribute("auth.token", token.getToken());
            }
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
