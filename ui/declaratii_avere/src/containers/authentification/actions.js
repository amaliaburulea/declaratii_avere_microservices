import * as actions from './constants';

export const login = (username, password) => ({ type: actions.ON_LOGIN_INIT, payload: { username, password } });

export const onLoginSuccess = (response) => {
  debugger;

  return {
    type: actions.ON_LOGIN_SUCCESS,
    payload: response.data,
  };
};
