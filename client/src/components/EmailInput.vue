<template>
  <div class="field is-horizontal">
    <div class="field-label is-normal" v-if="withLabel">
      <label class="label">
        Email
        <span v-if="required" class="required">*</span>
      </label>
    </div>
    <div class="field-body">
      <div class="field is-half">
        <div class="control">
          <p class="control has-icons-left">
            <input
              :class="{'is-danger': showError || $v.email.$error}"
              class="input"
              type="text"
              placeholder="me@example.com"
              v-model="$v.email.$model"
              v-on:change="emitData"
              :readonly="readonly"
            />
            <span class="icon is-small is-left">
              <em class="fas fa-envelope"></em>
            </span>
          </p>

          <div id="email-error" v-if="$v.email.$error">
            <div class="help is-danger" v-if="!$v.email.requiredIf">
              Email is
              required
            </div>
            <div class="help is-danger" v-if="!$v.email.email">
              Email is not in valid
              format
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import { email, requiredIf } from "vuelidate/lib/validators";

  export default {
    name: "InputEmail",
    /* Props:
             - required: True if the input field should throw an error when it is empty
             - default: Default value of the input field
             - withLabel: True if the field text should be present on the left side of the input field
             - readOnly: True if input field should not be editable
             - showError: True if alerts should be shown, turning the box red. False otherwise
             - showMandatory: True if the text field should show if it's mandatory (*)
          */
    props: [
      "required",
      "default",
      "withLabel",
      "readonly",
      "showMandatory",
      "showError"
    ],
    data: function() {
      return {
        email: this.default,
        invalidEmail: false
      };
    },
    validations: {
      email: {
        requiredIf: requiredIf(function() {
          return this.$props.required;
        }),
        email: email
      }
    },
    methods: {
      emitData: function() {
        this.$emit("emailInput", this.email);
        this.$emit("emailInvalid", this.$v.email.$error);
      }
    }
  };
</script>

<style scoped>
</style>
