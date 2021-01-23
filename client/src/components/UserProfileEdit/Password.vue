<template>
  <div>
    <div class="box">
      <div class="block">
        <div class="field">
          <div class="columns">
            <div class="column">
              <div class="field">
                <label class="label">Current password</label>
                <div class="control">
                  <p class="control has-icons-left">
                    <input
                      class="input"
                      id="old-password"
                      type="password"
                      placeholder="Old password"
                      v-model.lazy="userData.password"
                      @keyup.enter = "changePassword()"
                    />
                    <span class="icon is-small is-left">
                      <em class="fas fa-lock-open"></em>
                    </span>
                  </p>


                </div>
              </div>
            </div>

            <div class="column">
              <div class="field">
                <label class="label">New password</label>
                <div class="control">
                  <p class="control is-expanded has-icons-left">
                    <input
                      class="input"
                      id="new-password"
                      type="password"
                      placeholder="New password"
                      @keyup.enter = "changePassword()"
                      :class="{'is-danger': $v.userData.newPassword.$error}"
                      v-model.lazy="$v.userData.newPassword.$model"
                    />
                    <span class="icon is-small is-left">
                      <em class="fas fa-lock"></em>
                    </span>
                  </p>

                  <div v-if="($v.userData.newPassword.$error)">
                    <div
                      v-if="(!$v.userData.newPassword.required)"
                      class="help is-danger"
                    >Password is Required</div>
                    <div
                      v-if="(!$v.userData.newPassword.minLength)"
                      class="help is-danger"
                    >Password must be length of 8 or more</div>
                  </div>
                </div>
              </div>
            </div>

            <div class="column">
              <div class="control">
                <label class="label">Confirm new password</label>
                <p class="control is-expanded has-icons-left">
                  <input
                    class="input"
                    id="new-password-confirm"
                    type="password"
                    placeholder="New password"
                    :class="{'is-danger': ($v.userData.confirmNewPassword.$error) || (doesNotMatch)}"
                    v-model.lazy="$v.userData.confirmNewPassword.$model"
                    @keyup.enter = "changePassword()"
                  />
                  <span class="icon is-small is-left">
                    <em class="fas fa-lock"></em>
                  </span>
                </p>

                <div v-if="($v.userData.confirmNewPassword.$error)">
                  <div
                    v-if="(!$v.userData.confirmNewPassword.required)"
                    class="help is-danger"
                  >Password is Required.</div>
                  <div
                    v-if="(!$v.userData.confirmNewPassword.minLength)"
                    class="help is-danger"
                  >Must be length of 8 or more.</div>
                </div>
                <div v-if="doesNotMatch" class="help is-danger">Must match your new password.</div>
              </div>
            </div>
          </div>
        </div>


        <div class="field is-grouped is-grouped-right ">
          <div v-if="hasAdminPrivileges" class="control">
            <a id="reset-btn" class="button is-warning" @click="resetPasswordConfirmation">
              <strong>Reset Password</strong>
            </a>
          </div>
          <div class="control">
            <a id="change-pw-btn" class="button is-danger" @click="changePassword">
              <strong>Change Password</strong>
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import { required, minLength } from "vuelidate/lib/validators";
  import api from "@/Api";
  import { getPasswordHash } from "@/utils.js";
  import {mapGetters, mapState} from "vuex";

  export default {
    name: "Password",
    data: function() {
      return {
        userData: {
          password: "",
          newPassword: "",
          confirmNewPassword: ""
        },
        doesNotMatch: false
      };
    },

    created() {
      this.init();
    },
    validations: {
      userData: {
        newPassword: {
          required,
          minLength: minLength(8)
        },
        confirmNewPassword: {
          required,
          minLength: minLength(8)
        }
      }
    },
    computed: {
        ...mapState([
            "isLoggedIn",
            "isGlobalAdmin",
            "userId",
            "currentUserProfileViewed"
        ]),

        ...mapGetters(["hasAdminPrivileges"]),

    },
    methods: {
      init() {},
      changePassword: function() {
        if (this.$v.$invalid || this.$v.$anyError) {
          this.$v.$touch();
          this.$buefy.toast.open({
            duration: 3000,
            message: "Please fill all of the required fields",
            type: "is-danger"
          });
        } else if (
          this.userData.newPassword !== this.userData.confirmNewPassword
        ) {
          this.doesNotMatch = true;
        } else {
          this.doesNotMatch = false;
          const newPassword = getPasswordHash(this.userData.newPassword);
          const payload = {
            old_password: getPasswordHash(this.userData.password),
            new_password: newPassword,
            repeat_password: newPassword
          };

          api
            .sendChangePassword(this.$route.params.userId, payload)
            .then(response => {
              if (response.status === 200) {
                this.$buefy.toast.open({
                  duration: 3000,
                  message: "Password updated!",
                  type: "is-success"
                });
              }
            })
            .catch(err => {
              this.errorMessageResponse(err);
            });
        }
      },

      resetPasswordConfirmation() {
        this.$buefy.dialog.confirm({
          title: `Resetting user's password`,
          message:
            `Are you sure you want to <b>reset</b> this user's password?<br>` +
            `Default password: <b>default123</b>`,
          confirmText: "Reset",
          cancelText: "Cancel",
          type: "is-danger",
          hasIcon: true,
          onConfirm: () => this.resetPassword()
        });
      },

      resetPassword() {
        const payload = {
          old_password: "placeholder",
          new_password: getPasswordHash("default123"),
          repeat_password: getPasswordHash("default123")
        };

        api
          .sendChangePassword(this.$route.params.userId, payload)
          .then(response => {
            if (response.status === 200) {
              this.toastMessage('Password has been reset successfully!', 'is-success')
            }
          })
          .catch(err => {
            this.errorMessageResponse(err);
          });
      },

      errorMessageResponse(err) {
        if (err.response.status === 403) {
          this.toastMessage("You are forbidden to this action", "is-danger")
        } else if (err.response.status === 401) {
          this.toastMessage("You are unauthorized to this action", "is-danger")
        } else if (err.response.status === 400) {
          this.toastMessage("Incorrect password provided", "is-danger")
        } else {
          this.toastMessage("Whoops. Something went wrong on our end.", "is-danger")
        }
      },

      toastMessage(message, type) {
        this.$buefy.toast.open({
          duration: 3000,
          message: message,
          type: type
        });
      }
    }
  };
</script>

<style scoped>
</style>
