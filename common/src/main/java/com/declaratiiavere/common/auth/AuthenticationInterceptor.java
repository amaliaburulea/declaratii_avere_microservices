package com.declaratiiavere.common.auth;

import com.declaratiiavere.iam.user.UserIdentity;
import com.declaratiiavere.iam.user.UserInfo;
import com.declaratiiavere.restclient.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Authentication filter.
 *
 * @author Razvan Dani
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Autowired
    private LoadBalancerClient loadBalancer;

    @Autowired
    private RestClient restClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse httpServletResponse, Object o) throws Exception {
        if (request.getHeader("Authorization") != null && request.getHeader("Authorization").startsWith("Basic")
                && !request.getRequestURI().contains("login")
                && !request.getRequestURI().contains("resetPassword")
                && !request.getRequestURI().contains("/institutie/find")
                && !request.getRequestURI().contains("/functie/find")
                ) {
            UserIdentity.setAuthorizationHeaderToThreadLocal(request.getHeader("Authorization"));
            ServiceInstance serviceInstance = loadBalancer.choose("iam");

            String base64Credentials = request.getHeader("Authorization").substring("Basic".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials),
                    Charset.forName("UTF-8"));
            final String[] usernameAndPassword = credentials.split(":", 2);
            String userName = usernameAndPassword[0];
            String password = usernameAndPassword[1];

            if (serviceInstance != null) {
                UserInfo userInfo = restClient.get(serviceInstance.getUri() + "/iam/" + userName + "/", UserInfo.class);
                UserIdentity.setUserToThreadLocal(userInfo);
            }
        } else {
            UserIdentity.setAuthorizationHeaderToThreadLocal(null);
            UserIdentity.setUserToThreadLocal(null);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
