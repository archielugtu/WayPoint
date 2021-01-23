/**
 * Defines the functions to generate error messages
 * when using Vuelidate
 */

// Text to be shown for each errors
const errorLabels = {
  required: "This field is required",
  email: "Email is not in valid format",
  maxLength: "Field input is over the character limit",
  minDate: "Date is invalid",
  minLength: "Field does not meet minimum length requirement"
}

export default {
  getErrorLabel(validation) {
    if (!validation.$error) {
      return;
    }
    return Object.keys(validation)
      .filter(key => !key.startsWith("$"))
      .reduce((prev, curr) => {
        if (!validation[curr]) {
          prev = errorLabels[curr];
        }

        return prev;
      }, "");
  },

  status(validation) {
    return {
      "is-danger": validation.$error
    };
  }
}
