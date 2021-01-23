<template>
  <div>
    <div class="box">
      <div class="field">
        <div id="email" class="title is-4">Email</div>
        <p>You can have up to 5 emails</p>
      </div>

      <div v-for="(email, index) in emailAddress" :key="email.userEmailID" class="field has-addons">
        <div class="control is-expanded">
          <EmailInput
            v-bind:default="emailAddress[index].address"
            v-bind:readonly="index == 0"
            @emailInvalid="emailInvalid[index] = $event"
            @emailInput="emailAddress[index].address = $event"
          />
        </div>
        <div v-if="index != 0" class="control">
          <button @click="removeEmail(index)" class="button is-danger">
            <span class="icon is-small">
              <em class="fas fa-times"></em>
            </span>
          </button>
        </div>
        <div v-if="index != 0" class="control">
          <button @click="setEmailToPrimary(index)" class="button is-success">
            <span class="icon is-small">
              <em class="fas fa-check"></em>
            </span>
          </button>
        </div>
        <div v-if="index == 0" class="control">
          <button id="primary-email-lbl" class="button is-success">Primary Email</button>
        </div>
      </div>

      <div class="field">
        <div class="help is-danger" v-if="hasDuplicateEmails">There is a duplicate email address.</div>
      </div>

      <div v-if="emailAddress.length < 5" class="field">
        <a id="add-email-btn" class="button is-link" @click="addEmail">
          <strong>Add Another Email</strong>
        </a>
      </div>
    </div>
    <div class="field is-grouped is-grouped-right">
      <div class="control">
        <router-link
          :to="{name: 'profile', userId: $route.params.userId}"
          class="button is-primary"
        >Back to profile</router-link>
      </div>
      <div class="control">
        <div class="field">
          <a id="save-email-btn" class="button is-success" @click="saveUserEmails">
            <strong>Save Emails</strong>
          </a>
        </div>
      </div>
    </div>
  </div>
</template>


<script>
  import api from "@/Api";
  import EmailInput from "../EmailInput";

  export default {
    name: "EditProfileEmail",
    props: ["userData"],
    data: function() {
      return {
        emailAddress: [this.userData.primary_email].concat(
          this.userData.additional_email
        ),
        duplicateEmails: [],
        emailInvalid: [false, false, false, false, false]
      };
    },
    components: {
      EmailInput
    },
    computed: {
      hasDuplicateEmails: function() {
        let userEmails = this.emailAddress.map(e => e.address);
        let findDuplicates = arr =>
          arr.filter((item, index) => arr.indexOf(item) !== index);
        return findDuplicates(userEmails).length > 0;
      }
    },
    mounted() {
      this.init();
    },
    methods: {
      init() {},
      hasInvalidEmail() {
        for (let i = 0, len = this.emailInvalid.length; i < len; i++) {
          if (this.emailInvalid[i] === true) {
            return true;
          }
        }
        return false;
      },
      addEmail() {
        let emails = this.emailAddress;
        let blankEmails = emails.filter(e => !e.address);
        if (
          blankEmails.length ||
          this.hasDuplicateEmails ||
          this.hasInvalidEmail()
        ) {
          return;
        }

        let highestId = -1;
        this.emailAddress.forEach(e => {
          if (e.userEmailID > highestId) {
            highestId = e.userEmailID;
          }
        });

        if (this.emailAddress.length < 5) {
          this.emailAddress.push({
            userEmailID: highestId + 1,
            isPrimary: false,
            address: ""
          });
        }
      },
      removeEmail(lineId) {
        if ( this.emailAddress[lineId].address !== "" ) {
          this.$buefy.dialog.confirm({
            title: `Email Removal`,
            message:
              "You want to <b>delete</b> " + this.emailAddress[lineId].address + ", are you sure?",
            confirmText: "Delete",
            cancelText: "Cancel",
            type: "is-danger",
            hasIcon: true,
            onConfirm: () => {
            this.emailInvalid[lineId] = false;
            this.emailAddress.splice(lineId, 1);
          }
          });
        }
      },
      setEmailToPrimary(index) {


        if (
          this.emailAddress[index].address === "" ||
          this.hasDuplicateEmails ||
          this.hasInvalidEmail()
        ) {
          return;
        }

        this.$buefy.dialog.confirm({
          title: `Email Removal`,
          message:
            "You want to set " + this.emailAddress[index].address + " as your primary email, are you sure?",
          confirmText: "OK",
          cancelText: "Cancel",
          type: "is-success",
          hasIcon: true,
          onConfirm: () => {
            this.emailAddress.forEach((_, i, arr) => {
              arr[i].isPrimary = false;
            });
            this.emailAddress[index].isPrimary = true;

            let e = this.emailAddress[index];
            this.emailAddress.splice(index, 1);
            this.emailAddress.splice(0, 0, e);
            this.userData.primary_email = this.emailAddress.filter(
              i => i.isPrimary
            )[0];
            this.userData.additional_email = this.emailAddress.filter(
              i => !i.isPrimary
            );
          }
        });
      },
      filterInvalidEmails() {
        // Filter out empty emails
        this.emailAddress = this.emailAddress.filter(e => e.address !== "");

        // Filter out duplicate emails
        this.emailAddress = this.emailAddress.filter(
          (e, index, self) =>
            index === self.findIndex(t => t.address === e.address)
        );
      },
      async saveUserEmails() {
        this.filterInvalidEmails();
        let secondaryEmails = [];
        let primaryEmail = null;
        this.emailAddress.forEach(e => {
          if (e.isPrimary) {
            primaryEmail = e.address;
          } else {
            secondaryEmails.push(e.address);
          }
        });
        let payload = {
          primary_email: primaryEmail,
          additional_email: secondaryEmails
        };
        try {
          await api.sendChangeEmails(this.$route.params.userId, payload);
          this.$buefy.toast.open({
            duration: 2000,
            message: "Email updated!",
            type: "is-success"
          });
        } catch (err) {
          this.$buefy.toast.open({
            duration: 2000,
            message: err.response.data.message,
            type: "is-danger"
          })
        }
      }
    }
  };
</script>


<style scoped>
</style>

