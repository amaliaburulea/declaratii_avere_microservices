import React, { Component } from 'react';
import { Field, reduxForm } from 'redux-form';
import RaisedButton from 'material-ui/RaisedButton';
import PropTypes from 'prop-types';

import { renderTextField } from 'components';

const validate = values => {
  const errors = {};
  const requiredFields = [
    'username',
    'password',
  ];
  requiredFields.forEach(field => {
    if (!values[field]) {
      errors[field] = 'Required'
    }
  });
  // if (
  //   values.email &&
  //   !/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i.test(values.email)
  // ) {
  //   errors.email = 'Invalid email address'
  // }
  return errors
};

class Form extends Component {
  render() {
    const { handleSubmit } = this.props;

    return (
      <form onSubmit={ handleSubmit } noValidate autoComplete="new-password">
        <div>
          <Field
            name="username"
            component={renderTextField}
            label="Username"
          />
        </div>

        <div>
          <Field
            name="password"
            component={renderTextField}
            label="Password"
            type="password"
          />
        </div>

        <div>
          <RaisedButton label="Login" primary type="submit"/>
        </div>
      </form>
    );
  }
}

Form.propTypes = {
  handleSubmit: PropTypes.func.isRequired,
};

export const LoginForm = reduxForm({
  form: 'login-form',
  validate,
})(Form);
