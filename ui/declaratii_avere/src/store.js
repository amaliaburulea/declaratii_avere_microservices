import { createStore, applyMiddleware, compose } from 'redux';
import { routerMiddleware } from 'react-router-redux';
import createHistory from 'history/createBrowserHistory';
import { rootReducer } from './reducers';

import createSagaMiddleware from 'redux-saga';
import { authentificationSaga } from 'containers';

/*ToDo This could co in a new file*/
const sagaMiddleware = createSagaMiddleware();
/* eslint-disable no-underscore-dangle */
/**
 * Enable redux devtools
 * */
const composeEnhancers =
  process.env.NODE_ENV !== 'production' && typeof window === 'object' && window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ ?
    window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__({ shouldHotReload: false }) :
    compose;
/* eslint-enable */

export const history = createHistory();

const middlewares = [
  routerMiddleware(history),
  sagaMiddleware,
];

const enhancers = [
  applyMiddleware(...middlewares),
];

export const store = createStore(
  rootReducer,
  composeEnhancers(...enhancers)
);

sagaMiddleware.run(authentificationSaga);
