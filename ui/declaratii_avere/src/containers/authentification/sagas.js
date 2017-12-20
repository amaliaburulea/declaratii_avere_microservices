import {  put, takeLatest } from 'redux-saga/effects';
import * as actions from './constants';
import { authService } from 'services';

function* login(action) {
  try {
    const { username, password } = action.payload;
    const user = yield authService.post('/iam/login', { username, password });
    debugger;
    yield put({type: "USER_FETCH_SUCCEEDED", user: user});
  } catch (e) {
    yield put({type: "USER_FETCH_FAILED", message: e.message});
  }
}

export function* authentificationSaga() {
  yield takeLatest(actions.ON_LOGIN_INIT, login);
}
