package com.declaratiiavere.restclient;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Rest client.
 *
 * @author Razvan Dani
 */
public class RestClient {
    private RestTemplate restTemplate = new RestTemplate();
    private static Method getAuthorizationHeaderMethod;

    static {
        try {
            Class userIdentityClass = Class.forName("com.declaratiiavere.iam.user");

            try {
                getAuthorizationHeaderMethod = userIdentityClass.getMethod("getAuthorizationHeader");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException ignore) {
            // if commons is not the classpath, we don't keep track of UserIdentity
        }
    }

    /**
     * Sends a POST request without http header.
     *
     * @param url           The Url
     * @param requestObject The request object
     * @param responseClass The class of the response
     * @return The response object
     */
    @SuppressWarnings("unused")
    public <T> T post(String url, Object requestObject, Class<T> responseClass) throws RestException {
        return post(url, requestObject, responseClass, null);
    }

    /**
     * Sends a POST request with http headers.
     *
     * @param url              The Url
     * @param requestObject    The request object
     * @param responseClass    The class of the response
     * @param requestHeaderMap The map of http request headers
     * @return The response object
     */
    public <T> T post(String url, Object requestObject, Class<T> responseClass,
                      Map<String, String> requestHeaderMap) throws RestException {
        HttpEntity<Object> httpEntity = getHttpEntity(requestObject, requestHeaderMap);

        return exchange(url, HttpMethod.POST, responseClass, httpEntity);
    }

    private <T> T exchange(String url, HttpMethod httpMethod, Class<T> responseClass, HttpEntity<Object> httpEntity, Object... uriVariables) throws RestException {
        ResponseEntity<T> responseEntity = restTemplate.exchange(url, httpMethod, httpEntity, responseClass, uriVariables);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new RestException(responseEntity.getStatusCode().value(), responseEntity.getBody());
        }


        return responseEntity.getBody();
    }

    /**
     * Sends a PUT request without http header.
     *
     * @param url           The Url
     * @param requestObject The request object
     * @param responseClass The class of the response
     * @return The response object
     */
    @SuppressWarnings("unused")
    public <T> T put(String url, Object requestObject, Class<T> responseClass) throws RestException {
        return put(url, requestObject, responseClass, null);
    }

    /**
     * Sends a PUT request with http headers.
     *
     * @param url              The Url
     * @param requestObject    The request object
     * @param responseClass    The class of the response
     * @param requestHeaderMap The map of http request headers
     * @return The response object
     */
    public <T> T put(String url, Object requestObject, Class<T> responseClass,
                     Map<String, String> requestHeaderMap) throws RestException {
        HttpEntity<Object> httpEntity = getHttpEntity(requestObject, requestHeaderMap);
        return exchange(url, HttpMethod.PUT, responseClass, httpEntity);
    }

    private HttpEntity<Object> getHttpEntity(Object requestObject, Map<String, String> requestHeaderMap) {
        String defaultAuthorizationHeader = null;

        if (getAuthorizationHeaderMethod != null) {
            try {
                defaultAuthorizationHeader = (String) getAuthorizationHeaderMethod.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (defaultAuthorizationHeader != null) {
            if (requestHeaderMap == null) {
                requestHeaderMap = new HashMap<>();
            }

            if (!requestHeaderMap.containsKey("Authorization")) {
                requestHeaderMap.put("Authorization", defaultAuthorizationHeader);
            }
        }

        HttpHeaders httpHeaders = new HttpHeaders();

        if (requestHeaderMap != null) {
            for (Map.Entry<String, String> requestHeaderEntrySet : requestHeaderMap.entrySet()) {
                httpHeaders.set(requestHeaderEntrySet.getKey(), requestHeaderEntrySet.getValue());
            }
        }

        return new HttpEntity<>(requestObject, httpHeaders);
    }

    /**
     * Sends a GET request without http header.
     *
     * @param url               The Url
     * @param responseClass     The class of the response
     * @param requestParameters Varargs for the request parameters
     * @return The response object
     */
    public <T> T get(String url, Class<T> responseClass, Object... requestParameters) throws RestException {
        return get(url, responseClass, null, requestParameters);
    }

    public <T> T get(String url, Class<T> responseClass, Map<String, String> requestHeaderMap, Object... requestParams) throws RestException {
        HttpEntity<Object> httpEntity = getHttpEntity(null, requestHeaderMap);

        return exchange(url, HttpMethod.GET, responseClass, httpEntity, requestParams);
    }
}
