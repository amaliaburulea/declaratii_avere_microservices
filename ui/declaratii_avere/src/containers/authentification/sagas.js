import {  put, takeLatest } from 'redux-saga/effects';
import * as actions from './constants';
import { LOCAL_STORAGE_KEYS } from 'consts';
import { onLoginSuccess } from './actions';
import { authService } from 'services';

function* login(action) {
  try {
    const { username, password } = action.payload;
    const { data } = yield authService.post('/iam/login', { username, password });

    yield localStorage.setItem(LOCAL_STORAGE_KEYS.USERNAME, username);
    yield localStorage.setItem(LOCAL_STORAGE_KEYS.PASSWORD, password);
    yield localStorage.setItem(LOCAL_STORAGE_KEYS.USER_INFO, data);

    yield put(onLoginSuccess(data));
  } catch (e) {
    yield put({type: "USER_FETCH_FAILED", message: e.message});
  }
}

export function* authentificationSaga() {
  yield takeLatest(actions.ON_LOGIN_INIT, login);
}
