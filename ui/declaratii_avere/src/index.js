import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { ConnectedRouter } from 'react-router-redux'
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';

import injectTapEventPlugin from 'react-tap-event-plugin';
import registerServiceWorker from './registerServiceWorker';

import { store, history } from './store';
import './styles/global.css';

import App from './containers/app/App';


injectTapEventPlugin(); // Needed for material-ui onTouchTap http://stackoverflow.com/a/34015469/988941

const app = (
  <Provider store={store}>
    <MuiThemeProvider>
      <ConnectedRouter history={history}>
        <App/>
      </ConnectedRouter>
    </MuiThemeProvider>
  </Provider>
);

ReactDOM.render(app, document.getElementById('root'));
registerServiceWorker();
