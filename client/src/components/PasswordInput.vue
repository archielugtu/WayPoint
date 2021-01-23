<template>
  <div class="field is-horizontal">
    <div class="field-label is-normal">
      <label class="label">
        Password
        <span v-if="required" class="required">*</span>
      </label>
    </div>
    <div class="field-body">
      <div class="field">
        <div class="control">
          <p class="control has-icons-left">
            <input
              :class="{'input': true, 'is-danger': showError || $v.password.$error}"
              type="password"
              placeholder="Password"
              v-model="$v.password.$model"
              v-on:change="emitData"
            />
            <span class="icon is-small is-left">
              <em class="fas fa-key"></em>
            </span>
          </p>

          <div id="password-error" v-if="$v.password.$error">
            <div class="help is-danger" v-if="!$v.password.required">Password is required</div>
            <div
              class="help is-danger"
              v-if="!$v.password.minLength"
            >Password must be of length 8 or more</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<script>
  import { requiredIf, minLength } from "vuelidate/lib/validators";

  export default {
    name: "InputPassword",
    /* Props:
       - required: True if the input field should throw an error when it is empty
       - default: Default value of the input field
       - showError: True if alerts should be shown, turning the box red. False otherwise
       - showMandatory: True if the text field should show if it's mandatory (*)
    */
    props: ["required", "default", "showMandatory", "showError"],
    data: function() {
      return {
        password: this.default,
        passwordInvalid: false,
        fieldText: this.showMandatory ? "Password*" : "Password"
      };
    },
    validations: {
      password: {
        requiredIf: requiredIf(function() {
          return this.$props.required;
        }),
        minLength: minLength(8)
      }
    },
    methods: {
      emitData: function() {
        this.$emit("passwordInput", this.password);
        this.$emit("passwordInvalid", this.$v.password.$error);
      }
    }
  };
</script>


<style scoped>
</style>

