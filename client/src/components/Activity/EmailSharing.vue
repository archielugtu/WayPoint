<template>
  <div class="container">
    <b-field label="Share via Email" />
    <b-field :type="{ 'is-danger' : !isEmailValid || duplicateEmail || isUserEmail }">
      <b-input
        expanded
        placeholder="me@example.com"
        v-model="email"
        @input="validate"
        @keyup.native.enter="addEmail"
        type="search"
        icon="search"
      ></b-input>
      <p class="control">
        <button
          @click="addEmail"
          class="button is-primary"
        >{{isMultipleEmails ? "Add emails" :"Add email"}}</button>
      </p>
    </b-field>
    <b-field
      :message="message"
    ></b-field>
  </div>
</template>

<script>

import {mapGetters} from "vuex";
export default {
  name: "EmailSharing",
  props: ["emails"],
  components: {},
  data: function () {
    return {
      isEmailValid: true,
      duplicateEmail: false,
      isUserEmail: false,
      isMultipleEmails: false,
      email: ""
    };
  },
  created() {
  },
  computed: {
    ...mapGetters("appUser", ["getUserEmails"]),
    message() {
      if (!this.isEmailValid) {
        return "Input email is invalid";
      } else if (this.duplicateEmail) {
        return 'Duplicate email exists';
      } else if (this.isUserEmail) {
        return "You are the creator of this activity";
      } else {
        return 'Press [enter] to add an email.     Use commas to separate multiple emails';
      }
    }
  },
  methods: {
    addEmail: function () {
      if (this.email && this.isEmailValid && !this.duplicateEmail && !this.isUserEmail) {
        if (this.isMultipleEmails) {
          let splitEmails = this.email.split(",");
          splitEmails.forEach((e) => {
            this.emails.push({
              email: e.trim(),
              role: "None",
              newRole: "follower",
            });
          });
        } else {
          this.emails.push({
            email: this.email.trim(),
            role: "None",
            newRole: "follower",
          });
        }
        this.$emit("firstUpdate");
        this.email = "";
      }
    },
    remove(email) {
      const index = this.emails.map((e) => e.email).indexOf(email);
      if (index >= 0) {
        this.emails.splice(index, 1);
      }
    },
    validate() {
      this.checkDuplicateEmail();
      this.checkValidEmail();
      this.checkEmailNotCreators();
    },
    checkEmailNotCreators() {
      this.isUserEmail = false;
      let splitEmails = [];
      this.email.split(",").forEach((e) => splitEmails.push(e.trim()));
      splitEmails.forEach(e => {
        if (this.getUserEmails().includes(e)) {
          this.isUserEmail = true;
        }
      })
    },
    checkDuplicateEmail() {
      this.duplicateEmail = false;
      let splitEmails = [];
      this.email.split(",").forEach((e) => splitEmails.push(e.trim()));
      let allEmails = this.emails.map((e) => e.email);
      splitEmails.forEach((se) => {
        if (allEmails.indexOf(se.trim()) >= 0) {
          this.duplicateEmail = true;
        }
        allEmails.push(se.trim());
      });
    },
    checkValidEmail: function () {
      // Regex retrieved from EmailListValidator on the server side
      const singleEmailRegexString =
        "[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\." +
        "[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))";
      const commaSeparatedEmailRegexString = `^${singleEmailRegexString}(,\\s* ${singleEmailRegexString})*$`;
      const emailRegex = RegExp(commaSeparatedEmailRegexString);
      // Check the last input email is of valid format
      this.isEmailValid =
        !this.email.trim() || emailRegex.test(this.email.trim());
      this.isMultipleEmails = this.email.includes(",");
    },
  },
};
</script>

<style scoped>
.email {
  padding-top: 0.4rem;
}
</style>
