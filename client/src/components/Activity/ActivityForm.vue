<template>
  <div>
    <b-field
      id="activity-name"
      :type="status($v.activityName)"
      :message="getErrorLabel($v.activityName)"
    >
      <template slot="label">
        Activity Name
        <span class="required">*</span>
      </template>
      <b-input icon="mountain" placeholder="Name your activity!" v-model="$v.activityName.$model"></b-input>
    </b-field>

    <InputActivityTypes
      id="activity-types"
      v-model="activityTypes"
      :withLabel="true"
      :required="true"
      placeholder="Activity type(s) for this event"
      :type="status($v.activityTypes)"
      :message="getErrorLabel($v.activityTypes)"
    />

    <div class="field">
      <div class="label">
        <label id="time-range-picker" class="label">Time Range</label>
      </div>
      <div class="control">
        <div class="field">
          <b-switch
            id="time-range-switch"
            v-model="timeRange"
            true-value="Duration"
            false-value="Continuous"
            @input="resetDate"
          >{{ timeRange }}</b-switch>
        </div>
      </div>
    </div>

    <div v-if="timeRange === 'Duration'" id="date-range" class="field">
      <div class="columns">
        <div class="column">
          <b-field
            id="date-start-label"
            :type="status($v.startDate)"
            :message="getErrorLabel($v.startDate)"
            label="Start date"
          >
            <b-datepicker
              editable
              id="date-start"
              :date-formatter="dateFormatter"
              :date-parser="dateParser"
              v-model="$v.startDate.$model"
              placeholder="DD/MM/YYYY"
              icon="calendar-alt"
              :min-date="minDate"
              @input="updateStartTime(false)"
            ></b-datepicker>
          </b-field>
        </div>
        <div class="column">
          <b-field
            id="date-end-label"
            :type="status($v.endDate)"
            :message="getErrorLabel($v.endDate)"
            label="End date"
          >
            <b-datepicker
              editable
              id="date-end"
              :date-formatter="dateFormatter"
              :date-parser="dateParser"
              v-model="$v.endDate.$model"
              placeholder="DD/MM/YYYY"
              icon="calendar-alt"
              :min-date="startDate"
            ></b-datepicker>
          </b-field>
        </div>
      </div>
    </div>

    <div id="time-range" v-if="!isContinuous">
      <b-checkbox id="checkbox" v-model="enableStartEndTime">Set start/end time</b-checkbox>
      <div v-if="enableStartEndTime" class="columns">
        <div class="column">
          <b-field
            :message="!isEndTimeValid ? 'Time range must be at least 1 hour apart' : ''"
            :type="!isEndTimeValid ? 'is-danger' : ''"
            label="Start time"
          >
            <b-timepicker
              id="time-start"
              v-model="startDate"
              editable
              placeholder="e.g. 15:00"
              icon="clock"
            ></b-timepicker>
          </b-field>
        </div>
        <div class="column">
          <b-field
            :message="!isEndTimeValid ? 'Time range must be at least 1 hour apart' : ''"
            :type="!isEndTimeValid ? 'is-danger' : ''"
            label="End time"
          >
            <b-timepicker
              id="time-end"
              v-model="endDate"
              editable
              placeholder="e.g. 18:00"
              icon="clock"
            ></b-timepicker>
          </b-field>
        </div>
      </div>
    </div>

    <div id="description" class="field">
      <label class="label">Description</label>
      <div class="control">
        <b-input v-model="description" maxlength="600" type="textarea"></b-input>
      </div>
    </div>

    <div id="hashtags" class="field">
      <label class="label">Hashtags</label>
      <HashtagInput id="hashtag-input" v-model="hashtags" />
    </div>

    <b-field v-if="!this.activityId">
      <template slot="label">Activity Visibility</template>

      <b-dropdown v-model="visibility" aria-role="list">
        <button class="button" type="button" slot="trigger">
          <template v-if="visibility === 'public'">
            <b-icon icon="globe"></b-icon>
          </template>
          <template v-else-if="visibility === 'restricted'">
            <b-icon icon="users"></b-icon>
          </template>
          <template v-else>
            <b-icon icon="lock"></b-icon>
          </template>
          <span>{{ capitalize(this.visibility) }}</span>
          <b-icon icon="caret-down"></b-icon>
        </button>

        <b-dropdown-item value="public" aria-role="listitem">
          <div class="media">
            <b-icon class="media-left" icon="globe"></b-icon>
            <div class="media-content">
              <h3>Public</h3>
              <small>Everyone can see</small>
            </div>
          </div>
        </b-dropdown-item>

        <b-dropdown-item value="restricted" aria-role="listitem">
          <div class="media">
            <b-icon class="media-left" icon="users"></b-icon>
            <div class="media-content">
              <h3>Restricted</h3>
              <small>Only specific people can see</small>
            </div>
          </div>
        </b-dropdown-item>

        <b-dropdown-item value="private" aria-role="listitem">
          <div class="media">
            <b-icon class="media-left" icon="lock"></b-icon>
            <div class="media-content">
              <h3>Private</h3>
              <small>Only I can see</small>
            </div>
          </div>
        </b-dropdown-item>
      </b-dropdown>
    </b-field>
  </div>
</template>

<script>
  import { required, requiredIf, minValue } from "vuelidate/lib/validators";
  import { mapState } from "vuex";
  import api from "@/Api";
  import InputActivityTypes from "@/components/ActivityTypesInput";
  import casingHelper from "@/utils/casing/toCamelCase";
  import validationHelper from "@/utils/validations/validations";
  import dateHelper from "@/utils/dates/dates";
  import HashtagInput from "@/components/HashtagInput.vue";

  export default {
    name: "ActivityForm",
    props: ["activityId", "triggerValidation"],
    watch: {
      payload: function() {
        this.$emit("payload", this.payload);
      },
      isFormValid: function() {
        this.$emit("isValid", this.isFormValid);
      },
      triggerValidation: function() {
        this.$v.$touch();
      },
      isVisibilityValid: function() {
        this.$emit("visibilityValidation", this.isVisibilityValid);
      }
    },

    data: function() {
      let now = new Date(); // current day and time
      now.setHours(now.getHours() + 1);

      return {
        // Activity info data, these will be sent to the backend
        activityTypes: [],
        activityName: "",
        timeRange: "Continuous",
        startDate: now,
        endDate: now,
        location: "",
        description: "",
        isSwitchedCustom: "Continuous",
        hashtags: [],
        // visibility is not edited on this page but the update request requires the field.
        visibility: "public",
        originalVisibility: "",
        invalidEmail: false,
        emails: [],
        creatorId: -1,
        // No loading animation unless required
        buttonLoadingAnimation: false,
        // Specifying the minimum date users able to input
        minDate: new Date(),
        // Checkbox that enables time selection
        enableStartEndTime: false,
        isComponentModalActive: false,
        // Error messages for different validation types
      };
    },

    async created() {
      this.updateStartTime(true);
      try {
        await this.loadActivity();
      } catch (err) {
        this.$router.push({ name: "activityError" });
      }
    },

    components: {
      HashtagInput,
      InputActivityTypes
    },

    computed: {
      ...mapState("activities", ["activities"]),
      ...mapState(["userId"]),

      // Returns if user selected continuous time range
      isContinuous() {
        return this.timeRange === "Continuous";
      },
      // Checks if the end time is at least 1 hour after the start time
      // because #common-sense
      isEndTimeValid() {
        let start = new Date(this.startDate);
        const oneHour = 59 * 60 * 1000; // 59 minutes
        start.setTime(start.getTime() + oneHour);
        const end = new Date(this.endDate);
        return end >= start;
      },
      // If everything in the form is valid, this will return True. False otherwise
      isFormValid() {
        // Checks if there is any invalid forms, if so do not proceed
        if (this.$v.$invalid) {
          return false;
        }
        // Checks if start and end time is valid if the user set it
        return !(this.enableStartEndTime && !this.isEndTimeValid);
      },
      payload() {
        let params = {
          activity_type: this.activityTypes,
          activity_name: this.activityName.trim(),
          description: this.description.trim(),
          continuous: this.timeRange === "Continuous",
          start_time: this.generateTimeStr(this.startDate),
          end_time: this.generateTimeStr(this.endDate),
          location: this.location,
          hashtags: this.hashtags,
          visibility: this.visibility.toLowerCase(),
          emails: this.emails
        };

        // Continuous does not need start and end time to be specified
        if (params.continuous) {
          params.start_time = "";
          params.end_time = "";
        }

        return params;
      },
      
    },

    // Form validations by Vuelidate
    // https://vuelidate.js.org
    validations() {
      return {
        activityName: { required },
        startDate: {
          required: requiredIf(function() {
            return !this.isContinuous;
          }),
          minDate: minValue(new Date())
        },
        endDate: {
          required: requiredIf(function() {
            return !this.isContinuous;
          }),
          minDate: minValue(this.startDate)
        },
        activityTypes: { required }
      };
    },

    methods: {
      capitalize(s) {
        return s.charAt(0).toUpperCase() + s.slice(1);
      },
      async loadActivity() {
        if (this.activityId) {
          let selectedActivity = this.activities.find(
            a => a.activityId === this.activityId
          );
          if (selectedActivity === undefined) {
            let result = await api.getActivityById(this.activityId);
            selectedActivity = this.toCamelCase(result.data);
          }
          this.activityTypes = selectedActivity.activityType;
          this.activityName = selectedActivity.activityName;
          this.timeRange = selectedActivity.continuous
            ? "Continuous"
            : "Duration";
          if (!selectedActivity.continuous) {
            this.enableStartEndTime = true;
            this.startDate = new Date(selectedActivity.startTime);
            this.endDate = new Date(selectedActivity.endTime);
          }
          this.location = selectedActivity.location;
          this.description = selectedActivity.description;
          this.hashtags = selectedActivity.hashtags;
          this.originalVisibility =
            selectedActivity.visibility.charAt(0).toUpperCase() +
            selectedActivity.visibility.slice(1);
          this.visibility =
            selectedActivity.visibility.charAt(0).toUpperCase() +
            selectedActivity.visibility.slice(1);
          this.creatorId = selectedActivity.creatorId;
          this.$emit("isCreator", this.creatorId == this.userId)
        }
      },

      toCamelCase: casingHelper.activityResponseToCamelCase,

      /**
       * Returns the error label depending on validation error type
       */
      getErrorLabel: validationHelper.getErrorLabel,

      /**
       * Creates an activity by sending a POST request to the backend
       */
      status: validationHelper.status,

      /**
       * Formats date object to DD/MM/YYYY
       */
      dateFormatter: dateHelper.dateFormatter,

      /**
       * Parses DD/MM/YYYY to date object
       */
      dateParser: dateHelper.dateParser,

      /**
       * Converts date obj to time string with timezone info
       */
      generateTimeStr: dateHelper.generateTimeStr,


      /**
       * Whenever we update the starting date, update
       * the ending date to match the starting date
       * only if the starting date is > than ending date
       */
      updateStartTime(force) {
        if (force || this.startDate > this.endDate) {
          this.endDate = new Date(this.startDate);
          this.endDate.setHours(this.endDate.getHours() + 1);
        }
      },

      /**
       * Resets the date whenever we set the time range back to Continuous
       */
      resetDate() {
        if (this.timeRange === "Continuous") {
          const now = new Date(); // current day and time
          now.setHours(now.getHours() + 1);

          this.startDate = new Date(now);
          this.updateStartTime(true);
        }
      },

      /**
       * Shows error popup on top of the page
       */
      showErrorPopup(msg) {
        this.$buefy.toast.open({
          duration: 3000,
          message: msg,
          type: "is-danger"
        });
      },

      cancelActivity() {
        this.$router.go(-1);
      },

      emailInput(emails) {
        this.emails = emails;
      },

      checkValidEmail: function(emails) {
        if (emails.length === 0) {
          this.$emit("input", []);
          return;
        }

        // Regex retrieved from EmailListValidator on the server side
        const emailRegex = RegExp("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\." +
            "[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");
        let latestEmail = emails.pop();
        // Check the last input email is of valid format
        if (emailRegex.test(latestEmail)) {
          this.invalidEmail = false;
          if (emails.indexOf(latestEmail) === -1) {
            emails.push(latestEmail);
          }
        } else {
          this.invalidEmail = true;
        }

        this.emails = emails;
        this.$emit("input", emails);
      },
    }
  };
</script>

<style scoped>
  #time-range {
    margin-top: 20px;
    margin-bottom: 10px;
  }

  #checkbox {
    margin-bottom: 0.5rem;
  }
</style>
