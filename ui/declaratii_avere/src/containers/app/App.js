import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Route } from 'react-router';

import { Toolbar } from 'components';
import { Login , DemnitarList} from "containers";
import classes from './App.css';

export class App extends Component {
  render() {
    return (
      <div>
        <Toolbar/>
        <header className={classes.AppHeader}>
          <h1 className={classes.AppTitle}>Welcome to React</h1>
        </header>

        <Route path="/login" component={Login}/>
        <Route path="/home" component={DemnitarList}/>
      </div>
    );
  }
}
