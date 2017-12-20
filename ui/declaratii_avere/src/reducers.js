import { combineReducers } from 'redux';
import { reducer as formReducer } from 'redux-form'
import { routerReducer } from 'react-router-redux';

import {
  authReducer,
} from 'containers';

export const rootReducer = combineReducers({
  auth: authReducer,
  router: routerReducer,
  form: formReducer,
});
