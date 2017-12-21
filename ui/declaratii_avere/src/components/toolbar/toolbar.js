import React from 'react';
import { NavLink } from 'react-router-dom';
import AppBar from 'material-ui/AppBar';

import FontIcon from 'material-ui/FontIcon';
import IconButton from 'material-ui/IconButton';

const Logged = (props) => (
  <IconButton>
    <FontIcon className="material-icons">event</FontIcon>
  </IconButton>
);

export const Toolbar = (props) => {
  const toolbarRightItem = props.isLoggedIn ?
    <Logged/> :
    (
      <div>
        <NavLink exact to="/login">
          Login
        </NavLink>

        <NavLink exact to="/home">
          Home
        </NavLink>
      </div>
    );

  return (
    <AppBar
      title="Title"
      showMenuIconButton={false}
      iconElementRight={toolbarRightItem}
    >
    </AppBar>
  );
};
//
// title="Title"
// iconElementLeft={<IconButton><NavigationClose /></IconButton>}
// iconElementRight={this.state.logged ? <Logged /> : <Login />}
