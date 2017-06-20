package com.declaratiiavere.edge;

import com.declaratiiavere.iam.permission.PermissionValidationResponseInfo;
import com.declaratiiavere.iam.permission.RestRequestPermissionValidationInfo;
import com.declaratiiavere.restclient.RestClient;
import com.declaratiiavere.restclient.RestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Authentication filter in Edge.
 *
 * @author Razvam Dani
 */
public class EdgeAuthenticationFilter extends ZuulFilter {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(EdgeAuthenticationFilter.class);

    @Autowired
    private LoadBalancerClient loadBalancer;

    @Autowired
    private RestClient restClient;

    @Autowired
    private ObjectWriter objectWriter;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        Integer userRoleId = null;
        ServiceInstance serviceInstance = null;

        try {
            if (request.getHeader("Authorization") != null && request.getHeader("Authorization").startsWith("Basic")
                    && !request.getRequestURI().contains("login") && !request.getRequestURI().contains("resetPassword")
                    && !(request.getRequestURI().endsWith("/iam") && request.getMethod().equals("POST"))
                    && !request.getRequestURI().contains("/institutie/find")
                    && !request.getRequestURI().contains("/functie/find")
                    && !request.getRequestURI().contains("validateActivationTokenAndUnlockUser")) {
                User user;
                serviceInstance = loadBalancer.choose("iam");

                String base64Credentials = request.getHeader("Authorization").substring("Basic".length()).trim();
                String credentials = new String(Base64.getDecoder().decode(base64Credentials),
                        Charset.forName("UTF-8"));
                final String[] usernameAndPassword = credentials.split(":", 2);
                String userName = usernameAndPassword[0];
                String password = usernameAndPassword[1];

                if (serviceInstance != null) {
                    user = findUser(serviceInstance.getUri() + "/iam/checkUserAndPassword", userName,
                            password, request);
                } else {
                    return setFailedRequest(new ErrorMessageBody(503, "Service Unavailable", "Unable to perform authentication",
                            request.getServletPath()), 503);
                }

                if (user == null) {
                    return null;
                }

                userRoleId = user.getRoleId();
            }

            if (!request.getRequestURI().contains("login") && !request.getRequestURI().contains("resetPassword")
                    && !(request.getRequestURI().endsWith("/iam") && request.getMethod().equals("POST"))
                    && !request.getRequestURI().contains("validateActivationTokenAndUnlockUser")
                    && !request.getRequestURI().contains("/institutie/find")
                    && !request.getRequestURI().contains("/functie/find")
                    && (serviceInstance == null || !verifyAccess(request.getMethod(), userRoleId, request.getRequestURI(),
                    serviceInstance.getUri() + "/permission/checkPermissionForRequest"))) {
                return setFailedRequest(new ErrorMessageBody(
                        403, "Forbidden", "The user is not authorized to perform the operation",
                        request.getServletPath()), 403);
            }
        } catch (Exception e) {
            return setFailedRequest(new ErrorMessageBody(500, "Internal Server Error", e.getLocalizedMessage(),
                    request.getServletPath()), 500);
        }

        return null;
    }

    private User findUser(String path, String userName, String password, HttpServletRequest request) throws RestException, UnsupportedEncodingException {
        User user;

        if (password != null) {
            user = restClient.post(path, new User(userName, password), User.class);
        } else {
            user = restClient.get(path + "/" + userName + "/", User.class);
        }

        if (user == null || !user.getIsActive() || user.getIsLocked()) {
            setFailedRequest(new ErrorMessageBody(
                    401, "Unauthorized", "Bad credentials", request.getServletPath()), 401);
        } else {
            LOGGER.info(String.format("Successfully authenticated: %s", user.getUsername()));
        }

        return user;
    }

    private Object setFailedRequest(ErrorMessageBody errorMessageBody, int code) {
        String body = "";
        LOGGER.info(String.format("Authorization failed. Code: %d. Reason: %s", code, errorMessageBody.getMessage()));

        try {
            body = objectWriter.writeValueAsString(errorMessageBody);
        } catch (JsonProcessingException ignored) {
        }

        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setResponseStatusCode(code);

        if (ctx.getResponseBody() == null) {
            ctx.getResponse().setContentType("application/json;charset=UTF-8");
            ctx.setResponseBody(body);
            ctx.setSendZuulResponse(false);
            throw new RuntimeException("Code: " + code + ", " + body); //optional
        }
        return null;
    }

    private Boolean verifyAccess(String method, Integer userRoleId, String endPoint, String path) throws RestException {
        RestRequestPermissionValidationInfo restRequestPermissionValidationInfo = new RestRequestPermissionValidationInfo();
        restRequestPermissionValidationInfo.setRoleId(userRoleId);
        restRequestPermissionValidationInfo.setRestRequestPath(endPoint);
        restRequestPermissionValidationInfo.setRestRequestMethod(method);

        PermissionValidationResponseInfo permissionValidInfo = restClient.post(path, restRequestPermissionValidationInfo,
                PermissionValidationResponseInfo.class);

        return permissionValidInfo.getIsPermissionValid();
    }

}
