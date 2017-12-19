import React, { Component } from 'react';
import { Grid, Row, Col } from 'react-flexbox-grid';

export class Login extends Component {
  render() {
    return (
      <Grid fluid>
        <Row center={'xs'}>
          <Col xs={6} md={3}>
            Hello, world!
          </Col>
        </Row>
      </Grid>
    );
  }
}
