<template>
  <div>
    <div>
      <div
        v-for="(email, index) in emailAddress"
        :key="email.userEmailID"
        class="field is-horizontal"
      >
        <div class="field-label is-normal">
          <label v-if="index === 0" id="email" class="label">Additional Emails</label>
        </div>
        <div class="field-body">
          <div class="field has-addons">
            <div class="control is-expanded">
              <EmailInput
                v-bind:default="emailAddress[index].address"
                @emailInvalid="emailInvalid[index] = $event"
                @emailInput="emailAddress[index].address = $event"
              />
            </div>

            <div class="control">
              <button @click="removeEmail(index)" class="button is-danger">
                <span class="icon is-small">
                  <em class="fas fa-times"></em>
                </span>
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="field is-horizontal">
        <div class="field-label is-normal"></div>
        <div class="field-body">
          <div class="help is-danger" v-if="hasDuplicateEmails">There is a duplicate email address.</div>
          <div v-if="emailAddress.length < 4" class="field">
            <a id="add-email-btn" class="button is-link" @click="addEmail">
              <strong>Add Another Email</strong>
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<script>
  import EmailInput from "./EmailInput";
  import { confirmationAlertBox } from "@/utils";

  export default {
    name: "EmailSetup",
    props: ["emails", "is-horizontal"],
    data: function() {
      return {
        emailAddress: this.emails,
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
        if (
          this.emailAddress[lineId].address === "" ||
          confirmationAlertBox(
            "You want to delete " + this.emailAddress[lineId].address + "?"
          )
        ) {
          this.emailInvalid[lineId] = false;
          this.emailAddress.splice(lineId, 1);
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

        let selectedEmail = this.emailAddress[index].address;
        if (
          confirmationAlertBox("Set " + selectedEmail + " as your primary email")
        ) {
          this.emailAddress.forEach((_, i, arr) => {
            arr[i].isPrimary = false;
          });
          this.emailAddress[index].isPrimary = true;

          let e = this.emailAddress[index];
          this.emailAddress.splice(index, 1);
          this.emailAddress.splice(0, 0, e);
          this.userData.primary_email = this.emailAddress.filter(
            u => u.isPrimary
          )[0];
          this.userData.additional_email = this.emailAddress.filter(
            u => !u.isPrimary
          );
        }
      },
      filterInvalidEmails() {
        // Filter out empty emails
        this.emailAddress = this.emailAddress.filter(e => e.address !== "");

        // Filter out duplicate emails
        this.emailAddress = this.emailAddress.filter(
          (e, index, self) =>
            index === self.findIndex(t => t.address === e.address)
        );
      }
    }
  };
</script>


<style scoped>
</style>

