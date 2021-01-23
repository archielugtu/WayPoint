<template>
  <section class="hero">
    <div class="hero-body">
      <div class="container">
        <div class="columns">
          <div class="column is-5 is-offset-3">
            <div class="field is-horizontal">
              <div class="field-label is-normal"></div>
              <div class="field-body">
                <h1 class="title">Login</h1>
              </div>
            </div>

            <div class="field is-horizontal">
              <div class="field-label is-normal"></div>
              <div class="field-body">
                <h3 class="subtitle">Welcome back!</h3>
              </div>
            </div>

            <b-field
              id="email"
              :type="status($v.email)"
              :message="getErrorLabel($v.email)"
              horizontal
            >
              <template slot="label">
                Email
                <span class="required">*</span>
              </template>
              <b-input
                icon="envelope-open"
                placeholder="Your email"
                type="email"
                v-model="$v.email.$model"
                @keyup.enter.native = "submit()"
              ></b-input>
            </b-field>

            <b-field
              id="password"
              :type="status($v.password)"
              :message="getErrorLabel($v.password)"
              @keyup.enter.native = "submit()"
              horizontal
            >
              <template slot="label">
                Password
                <span class="required">*</span>
              </template>
              <b-input
                icon="key"
                placeholder="Your password"
                type="password"
                v-model="$v.password.$model"
              ></b-input>
            </b-field>

            <!-- Error message when logging in -->
            <div v-if="showErrMsg" class="field is-horizontal">
              <div class="field-label">
                <!-- Left empty for spacing -->
              </div>
              <div class="field-body">
                <div class="field">
                  <div class="notification is-danger">{{ err.msg }}</div>
                </div>
              </div>
            </div>

            <div class="field is-horizontal">
              <div class="field-label">
                <!-- Left empty for spacing -->
              </div>
              <div class="field-body">
                <div class="field">
                  <div class="control">
                    <button
                      id="submit"
                      @click="submit"
                      :class="{'is-loading': buttonLoadingAnimation}"
                      class="button is-primary"
                    >Submit</button>
                  </div>
                </div>
              </div>
            </div>

            <div class="field is-horizontal">
              <div class="field-label">
                <!-- Left empty for spacing -->
              </div>
              <div class="field-body">
                <div class="field">
                  <div class="control">
                    <label class="label">
                      Not a member?
                      <router-link class="active" to="/signup">Sign up!</router-link>
                    </label>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>


<script>
  import api from "@/Api";
  import { getPasswordHash } from "@/utils.js";
  import { mapActions } from "vuex";
  import axios from "axios";
  import validationHelper from "@/utils/validations/validations";
  import { required, email } from "vuelidate/lib/validators";

  export default {
    name: "Login",
    data: function() {
      return {
        email: "",
        password: "",

        showErrMsg: false,
        buttonLoadingAnimation: false,
        err: {}
      };
    },
    validations: {
      email: { required, email },
      password: { required }
    },
    methods: {
      ...mapActions([
        "setLoggedIn",
        "setUserId",
        "setIsAdmin",
        "setIsGlobalAdmin"
      ]),
      status: validationHelper.status,
      getErrorLabel: validationHelper.getErrorLabel,
      login: async function() {
        this.buttonLoadingAnimation = true;

        let source = axios.CancelToken.source();
        const timeoutID = setTimeout(() => {
          source.cancel();
          this.showNoConnectionErr();
        }, 10000);
        let cancelToken = { cancelToken: source.token };
        const payload = {
          email: this.email,
          password: getPasswordHash(this.password)
        };
        try {
          let response = await api.login(payload, cancelToken);
          this.buttonLoadingAnimation = false;
          if (response.data.status === "SUCCESS") {
            this.setLoggedIn(true);
            this.setIsGlobalAdmin(response.data.isGlobalAdmin);
            this.setIsAdmin(response.data.isAdmin);
            this.setUserId(response.data.userId);
            this.$router.push({
              name: response.data.urlRedirect,
              params: { userId: response.data.userId }
            });
          }
        } catch (err) {
          if (!err.response) {
            this.showNoConnectionErr();
            return;
          }
          // We want to check login success against strings as
          // http codes can't enough info for the range of login responses
          // e.g. account locked, banned, ip banned, deactivated
          if (err.response.data.status === "WRONG PASSWORD") {
            this.showInvalidEmailOrPassword();
          } else if (err.response.data.status === "USER NOT FOUND") {
            this.showInvalidEmail();
          }
        } finally {
          clearTimeout(timeoutID);
        }
      },
      showInvalidEmailOrPassword: function() {
        this.err.msg = "Invalid email or password";
        this.showErrMsg = true;
        this.buttonLoadingAnimation = false;
      },
      showInvalidEmail: function() {
        this.err.msg = "Email is not registered with our service";
        this.showErrMsg = true;
        this.buttonLoadingAnimation = false;
      },
      showEmptyFieldsErr: function() {
        this.err.msg = "Email or password cannot be empty";
        this.showErrMsg = true;
        this.buttonLoadingAnimation = false;
      },
      showNoConnectionErr: function() {
        this.err.msg = "Failed to contact the server, please try again later";
        this.showErrMsg = true;
        this.buttonLoadingAnimation = false;
      },
      submit: function() {
        this.showErrMsg = false;
        if (this.$v.$invalid) {
          this.$v.$touch();
          this.showEmptyFieldsErr();
          return;
        }

        // Send POST request to backend server with login credentials
        this.login();
      }
    }
  };
</script>


<style scoped>
</style>
