import React, { Component } from 'react';
import { connect } from 'react-redux';

class DemnitarListComponent extends Component {
  render() {
    return (
      <h1>List</h1>
    );
  }
}

export const DemnitarList = connect()(DemnitarListComponent);
