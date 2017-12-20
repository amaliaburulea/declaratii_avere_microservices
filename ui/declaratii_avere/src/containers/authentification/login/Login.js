import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { connect } from 'react-redux';
import * as actions from '../actions';

import { Grid, Row, Col } from 'react-flexbox-grid';
import { Card, CardHeader } from 'material-ui/Card';

import { LoginForm } from './LoginForm';

class LoginComponent extends Component {
  handleSubmit = (values) => {
    const { username, password } = values;

    this.props.onLogin(username, password);
  };

  render() {
    return (
      <Grid fluid>
        <Row around={'xs'}>
          <Col xs={10} md={6}>
            <Card>
              <CardHeader title={'Login'}/>
              <div className="layout-padding">
                <LoginForm onSubmit={this.handleSubmit}/>
              </div>
            </Card>
          </Col>
        </Row>
      </Grid>
    );
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    onLogin: (username, password) => dispatch(actions.login(username, password)),
  };
};

export const Login = connect(null, mapDispatchToProps)(LoginComponent);

// Login.propTypes = {
//   isLoggedIn: PropTypes.bool.isRequired
// };
