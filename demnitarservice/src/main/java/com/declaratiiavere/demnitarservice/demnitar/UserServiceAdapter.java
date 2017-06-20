package com.declaratiiavere.demnitarservice.demnitar;

import com.declaratiiavere.iam.user.SearchUserCriteria;
import com.declaratiiavere.iam.user.UserInfo;
import com.declaratiiavere.iam.user.UserInfoListResponse;
import com.declaratiiavere.restclient.RestClient;
import com.declaratiiavere.restclient.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;

import javax.validation.ValidationException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Adapter for user service.
 *
 * @author Razvan Dani
 */
@Component
public class UserServiceAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceAdapter.class);
    public static final String SERVICE_NAME = "iam";

    @Autowired
    private RestClient restClient;

    @Autowired
    private LoadBalancerClient loadBalancer;

    /**
     * Call User Service to get user by id
     * @param userId user id
     * @return UserInfo object
     */
    public UserInfo getUserById(Integer userId) throws RestException {
        Map<Integer, UserInfo> usersByIds = getUsersByIds(Collections.singletonList(userId));

        if (usersByIds == null || usersByIds.get(userId) == null) {
            throw new ValidationException("Cannot find user with id: " + userId);
        }

        return usersByIds.get(userId);
    }

    /**
     * Call User Service to get multiple users by ids in one call
     * @param userIds user ids
     * @return found users mapped by id
     */
    public Map<Integer, UserInfo> getUsersByIds(Collection<Integer> userIds) throws RestException {
        SearchUserCriteria searchUserCriteria = new SearchUserCriteria();
        searchUserCriteria.setUserIdList(new ArrayList<>(userIds));

        return findUsersBySearchCriteria(searchUserCriteria, UserInfo::getUserId);
    }

    private ServiceInstance chooseService() {
        ServiceInstance serviceInstance = loadBalancer.choose(SERVICE_NAME);
        if (serviceInstance == null) {
            LOGGER.error("Service {} is Unavailable!", SERVICE_NAME);
            throw new ValidationException(SERVICE_NAME);
        }
        return serviceInstance;
    }

    public Map<Integer, UserInfo> getUserInfoByIdMap(Set<Integer> userIdSet) throws RestException {
        SearchUserCriteria searchUserCriteria = new SearchUserCriteria();
        searchUserCriteria.setUserIdList(new ArrayList<>(userIdSet));

        return findUsersBySearchCriteria(searchUserCriteria, UserInfo::getUserId);
    }

    private <T> Map<T, UserInfo> findUsersBySearchCriteria(SearchUserCriteria searchUserCriteria,
                                                           Function<UserInfo, T> groupByFunction)
            throws RestException {
        UserInfoListResponse userInfoListResponse = restClient.post(chooseService().getUri() + "/iam/find",
                searchUserCriteria, UserInfoListResponse.class, new HashMap<>());

        if (userInfoListResponse.getUserInfoList() != null) {
            return userInfoListResponse.getUserInfoList().stream()
                    .collect(Collectors.toMap(groupByFunction, Function.identity()));
        }
        return null;
    }
}
