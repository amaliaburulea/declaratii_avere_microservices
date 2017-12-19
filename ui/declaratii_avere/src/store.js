import { createStore, applyMiddleware, compose } from 'redux';
import { routerMiddleware } from 'react-router-redux'
import createHistory from 'history/createBrowserHistory';
import { rootReducer } from './reducers';

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
];

const enhancers = [
  applyMiddleware(...middlewares),
];

export const store = createStore(
  rootReducer,
  composeEnhancers(...enhancers)
);
