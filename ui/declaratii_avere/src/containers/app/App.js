import React, { Component } from 'react';
import { Route } from 'react-router'
import logo from '../../assets/logo.svg';

import FontIcon from 'material-ui/FontIcon';
import IconButton from 'material-ui/IconButton';

import { Toolbar } from '../../components';
import { Login } from "../login";
import './App.css';

class App extends Component {
  render() {
    return (
      <div className="App">
        <Toolbar/>
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h1 className="App-title">Welcome to React</h1>
        </header>
        <p className="App-intro">
          To get started, edit <code>src/App.js</code> and save to reload.
        </p>

        <Route path="/login" component={Login}/>

        <IconButton>
          <FontIcon className="material-icons">event</FontIcon>
        </IconButton>
      </div>
    );
  }
}

export default App;
