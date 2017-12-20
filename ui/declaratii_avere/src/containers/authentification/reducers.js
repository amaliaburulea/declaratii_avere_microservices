import * as actions from './constants';

const initialState = {
  username: 'xxx',
  password: 'yyy'
};

export const authReducer = (state = initialState, action) => {
  switch (action.type) {
    case actions.ON_LOGIN_SUCCESS:
      return { ...state, username: action.payload.username, password: action.payload.password };
    default:
      return state;
  }
};
