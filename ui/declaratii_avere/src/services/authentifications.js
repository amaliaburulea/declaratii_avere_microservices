import Axios from 'axios';

export const authService = Axios.create({
  baseURL: 'http://192.168.48.41:8090'
});
