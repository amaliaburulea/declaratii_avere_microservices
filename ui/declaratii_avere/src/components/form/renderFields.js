import React from 'react';
import TextField from 'material-ui/TextField';

export const renderTextField = ({
  input,
  label,
  meta: { touched, error },
  fullWidth,
  ...custom
}) => (
  <TextField
    hintText={label}
    floatingLabelText={label}
    fullWidth
    errorText={touched && error}
    {...input}
    {...custom}
  />
);
