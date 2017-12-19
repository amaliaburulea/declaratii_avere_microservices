import React from 'react';
import { NavLink } from 'react-router-dom';

export const NavigationItem = (props) => {
  return (
    <li className={classes.NavigationItem}>
      <NavLink exact to={ props.link }>
        { props.children }
      </NavLink>
    </li>
  );
};
