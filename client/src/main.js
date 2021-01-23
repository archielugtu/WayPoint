import Vue from 'vue'
import Buefy from 'buefy'
import Vuelidate from 'vuelidate'
import VueRouter from 'vue-router'
import router from '@/router'
import store from '@/store/store'
import App from '@/App'

import 'buefy/dist/buefy.css'
import '@/../node_modules/bulma/css/bulma.css';
import params from '@/params.json';

Vue.prototype.$params = params.params;
Vue.prototype.$authenticated = false;

Vue.config.productionTip = false
Vue.use(VueRouter)
Vue.use(Vuelidate)
Vue.use(Buefy, {
  defaultIconPack: 'fas',
})

import VueLogger from 'vuejs-logger';

const options = {
  isEnabled: true,
  logLevel: 'debug',
  stringifyArguments: false,
  showLogLevel: true,
  showMethodName: false,
  separator: '|',
  showConsoleColors: true
};

Vue.use(VueLogger, options);

/* eslint-disable no-new */
new Vue({
  el: '#app',
  template: '<App/>',
  components: { App },
  router,
  store
});

