import React, { Component } from 'react';
import { Route } from 'react-router'
import logo from '../../assets/logo.svg';

import { Toolbar } from 'components';
import { Login } from "containers";
import classes from './App.css';

export class App extends Component {
  render() {
    return (
      <div>
        <Toolbar/>
        <header className={classes.AppHeader}>
          <img src={logo} className={classes.AppLogo} alt="logo" />
          <h1 className={classes.AppTitle}>Welcome to React</h1>
        </header>
        <p className={classes.AppIntro}>
          To get started, edit <code>src/App.js</code> and save to reload.
        </p>

        <Route path="/login" component={Login}/>
      </div>
    );
  }
}
