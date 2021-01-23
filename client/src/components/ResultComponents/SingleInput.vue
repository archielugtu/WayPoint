<template>
  <div>
    <div class="spacer">
      <b-checkbox
        :disabled="readOnly"
        @input="$emit('input', [$event.toString()])"
        inline
        v-if="type === 'checkbox'"
        v-model="checkBoxInput"
      >
        {{question}}
        <span>({{formattedType}})</span>
      </b-checkbox>
      <p v-else>
        {{question}}
        <span>({{formattedType}})</span>
      </p>
    </div>
    <b-field>
      <!-- If date picker was chosen -->
      <b-datepicker
        :date-formatter="dateFormatter"
        :disabled="readOnly || !viewingSelfAnswer"
        :show-week-number="false"
        @input="getDateString($event)"
        icon="calendar-alt"
        placeholder="Click to select..."
        trap-focus
        v-if="type === 'date'"
        v-model="monthDay"
      />
      <!-- If time picker was chosen -->
      <b-timepicker
        :disabled="readOnly || !viewingSelfAnswer"
        @input="getHourMinuteString($event)"
        icon="clock"
        v-else-if="type === 'time'"
        v-model="hourMinute"
      />
      <!-- If decimal input was chosen -->
      <b-input
        :disabled="readOnly || !viewingSelfAnswer"
        @input="onValueInput"
        pattern="^-?\d+(\.\d+)?$"
        placeholder="Answer"
        required
        v-else-if="type === 'float'"
      />
      <!-- If decimal input was chosen -->
      <b-input
        :disabled="readOnly || !viewingSelfAnswer"
        @input="onValueInput"
        placeholder="Answer"
        required
        type="number"
        v-else-if="type === 'integer'"
        v-model="integerInput"
      />

      <!-- Else show simple input box -->
      <b-input
        :disabled="readOnly || !viewingSelfAnswer"
        @input="onValueInput"
        placeholder="Answer"
        v-else-if="type !== 'checkbox'"
        v-model="textInput"
      />
    </b-field>
  </div>
</template>

<script>
  import dateHelper from "@/utils/dates/dates";
  import { requiredIf } from "vuelidate/lib/validators";

  export default {
    name: "SingleInput",
    data: function () {
      return {
        onDatePickerInput: new Date(),
        onTimePickerInput: new Date(),
        integerInput: 0,
        hourMinute: new Date(),
        monthDay: new Date(),
        readOnly: true,
        textInput: "",
        checkBoxInput: false,
      };
    },
    // Form validations by Vuelidate
    // https://vuelidate.js.org
    validations() {
      return {
        onDatePickerInput: {
          required: requiredIf(function () {
            return this.viewType === 'answer'
          })
        },
        integerInput: {
          required: requiredIf(function () {
            return this.viewType === 'answer'
          })
        },
        textInput: {
          required: requiredIf(function () {
            return this.viewType === 'answer'
          })
        }
      }
    },
    props: ["question", "type", "viewType", "unit", "userAnswer", "viewingSelfAnswer"],
    watch: {
      userAnswer: {
        deep: true,
        handler() {
          if (this.userAnswer.length === 0) {
            return;
          }

          let answer = this.userAnswer[0].answer;

          if (this.type === "integer") {
            this.integerInput = Number(answer)

          } else if (this.type === "time") {
            let hourMin = answer[0].split(":");
            const today = new Date();
            today.setHours(hourMin[0]);
            today.setMinutes(hourMin[1]);
            this.hourMinute = today;

          } else if (this.type === "date") {
            const today = new Date();
            let yearMonthDate = answer[0].split("-");
            today.setFullYear(yearMonthDate[0]);
            today.setMonth(yearMonthDate[1] - 1);  // need to subtract 1 otherwise if month = 2 (Feb), it will set it to March
            today.setDate(yearMonthDate[2]);

            this.monthDay = today;

          } else if (this.type === "checkbox") {
            this.checkBoxInput = answer[0] === "true";
          } else if (this.type === "text") {
            this.textInput = answer[0];
          }
        }

      }
    },
    computed: {
      formattedType() {
        switch (this.type) {
          case "integer":
            return "whole number: " + this.unit;
          case "float":
            return "decimal number: " + this.unit;
          default:
            return this.type;
        }
      }
    },
    mounted() {
      if (this.viewType === "answer") {
        this.readOnly = false;

      }
    },
    methods: {
      /**
       * Formats date object to DD/MM/YYYY
       */
      dateFormatter: dateHelper.dateFormatter,
      dateToYYYYMMDD: dateHelper.dateFormatterToYYYYMMDD,
      hourMinuteFormatter: dateHelper.dateFormatterToHourMinuteString,
      getHourMinuteString(time) {
        const hourMinuteStr = this.hourMinuteFormatter(time);
        this.$emit("input", [hourMinuteStr]);
      },
      getDateString(time) {
        const dateStr = this.dateToYYYYMMDD(time);
        this.$emit("input", [dateStr]);
      },
      isValidNumber(number) {
        const reg = /^\d+$/;
        this.$emit("isValid", reg.test(number));
      },
      isValidDecimal(decimal) {
        const reg = /^-?\d+(\.\d+)?$/;
        this.$emit("isValid", reg.test(decimal));
      },
      onValueInput(value) {
        if (this.type === "float") {
          this.isValidDecimal(value);
        } else if (this.type === "integer") {
          this.isValidNumber(value);
        }
        this.$emit("input", [value]);
      }
    }
  };
</script>

<style scoped>
</style>
