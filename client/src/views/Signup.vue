<template>
  <section class="hero">
    <div class="hero-body">
      <div class="container">
        <h1 class="title has-text-centered">Sign Up</h1>

        <b-steps
          v-model="page"
          :animated="true"
          :has-navigation="false"
          mobile-mode="compact"
          size="is-small"
        >
          <b-step-item step="1" :clickable="isStepsClickable">
            <div class="columns is-centered">
              <div class="column is-three-fifths">
                <h3
                  class="has-text-centered subtitle"
                >We just need a few details to get you started!</h3>
                <b-field
                  horizontal
                  id="firstname"
                  :type="status($v.firstName)"
                  :message="getErrorLabel($v.firstName)"
                >
                  <template slot="label">
                    First name
                    <span class="required">*</span>
                  </template>
                  <b-input
                    placeholder="Your first name"
                    icon-pack="fas"
                    icon="user"
                    v-model="$v.firstName.$model"
                  ></b-input>
                </b-field>

                <b-field
                  :type="status($v.middleName)"
                  :message="getErrorLabel($v.middleName)"
                  id="middlename"
                  horizontal
                  label="Middle name"
                >
                  <b-input
                    placeholder="Your middle name"
                    icon-pack="fas"
                    icon="user"
                    v-model="$v.middleName.$model"
                  ></b-input>
                </b-field>

                <b-field
                  id="lastname"
                  :type="status($v.lastName)"
                  :message="getErrorLabel($v.lastName)"
                  horizontal
                >
                  <template slot="label">
                    Last name
                    <span class="required">*</span>
                  </template>
                  <b-input
                    placeholder="Your last name"
                    icon-pack="fas"
                    icon="user"
                    v-model="$v.lastName.$model"
                  ></b-input>
                </b-field>

                <b-field
                  id="nickname"
                  :message="getErrorLabel($v.nickname)"
                  :type="status($v.nickname)"
                  horizontal
                  label="Nickname"
                >
                  <b-input
                    placeholder="Your nickname"
                    icon-pack="fas"
                    icon="cat"
                    v-model="$v.nickname.$model"
                  ></b-input>
                </b-field>

                <b-field
                  :type="status($v.birthDate)"
                  :message="getErrorLabel($v.birthDate)"
                  id="date-of-birth"
                  label="Date of Birth"
                  horizontal
                >
                  <template slot="label">
                    Date of birth
                    <span class="required">*</span>
                  </template>
                  <b-datepicker
                    placeholder="Click to select your birth date"
                    :min-date="minDate"
                    :max-date="maxDate"
                    :focused-date="selectedDate"
                    :date-formatter="dateFormatter"
                    :years-range="yearsRange"
                    v-model="$v.birthDate.$model"
                  ></b-datepicker>
                </b-field>

                <b-field id="gender" horizontal>
                  <template slot="label">
                    Gender
                    <span class="required">*</span>
                  </template>
                  <div class="block">
                    <b-radio v-model="gender" name="gender" native-value="Male">Male</b-radio>
                    <b-radio v-model="gender" name="gender" native-value="Female">Female</b-radio>
                    <b-radio v-model="gender" name="gender" native-value="Non-binary">Non-binary</b-radio>
                  </div>
                </b-field>

                <b-field
                  id="email"
                  :type="status($v.email)"
                  :message="getErrorLabel($v.email)"
                  horizontal
                >
                  <template slot="label">
                    Primary Email
                    <span class="required">*</span>
                  </template>
                  <b-input
                    icon="envelope-open"
                    placeholder="Your email"
                    type="email"
                    v-model="$v.email.$model"
                  ></b-input>
                </b-field>

                <EmailSetup id="email-setup" :is-horizontal="true" :emails="additionalEmails" />

                <b-field
                  id="password"
                  :type="status($v.password)"
                  :message="getErrorLabel($v.password)"
                  horizontal
                >
                  <template slot="label">
                    Password
                    <span class="required">*</span>
                  </template>
                  <b-input
                    icon="key"
                    icon-pack="fas"
                    placeholder="Your password"
                    type="password"
                    v-model="$v.password.$model"
                  ></b-input>
                </b-field>

                <div class="field is-horizontal">
                  <div class="field-label">
                    <!-- Left empty for spacing -->
                  </div>
                  <div class="field-body">
                    <div class="field">
                      <div class="control">
                        <div class="is-size-7 help is-danger">* marked fields are mandatory</div>
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
                      <div class="control"></div>
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
                          Already a member?
                          <router-link class="active" to="/login">Log in!</router-link>
                        </label>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </b-step-item>

          <b-step-item step="2" :clickable="isStepsClickable && !$v.$invalid">
            <div class="columns is-centered">
              <div class="column is-three-fifths">
                <h3 class="has-text-centered subtitle">Tell us a little about yourself!</h3>
                <b-field
                  id="image-input"
                  :type="status($v.bio)"
                  :message="getErrorLabel($v.bio)"
                  label="Customise your profile image"
                ></b-field>
                    <image-input
                      @primaryImg="primaryImage = $event"
                      @coverImg="coverImage = $event"
                      :primaryImg="primaryImage"
                      :coverImg="coverImage"
                   ></image-input>

                <b-field
                  id="bio"
                  :type="status($v.bio)"
                  :message="getErrorLabel($v.bio)"
                  label="Bio"
                >
                  <b-input
                    value="A short description about yourself"
                    type="textarea"
                    v-model="$v.bio.$model"
                  ></b-input>
                </b-field>

                <InputActivityTypes
                  id="activity-types"
                  v-model="activityTypes"
                  :showMandatory="false"
                  :withLabel="true"
                  :required="false"
                />

                <b-field  label="Fitness Level">
                  <b-slider
                    id="fitness-slider"
                    size="is-medium"
                    :min="0"
                    :max="4"
                    class="fitness-level-slider"
                    v-model="fitnessLevel"
                  >
                    <template v-for="val in fitnessLevels">
                      <b-slider-tick :value="val.level" :key="val.level">{{ val.fitness }}</b-slider-tick>
                    </template>
                  </b-slider>
                </b-field>
              </div>
            </div>
          </b-step-item>

          <b-step-item step="3" :clickable="isStepsClickable && !$v.$invalid">
            <div class="columns is-centered">
              <div class="column is-three-fifths">
                <h3 class="has-text-centered subtitle">Let us help you find activities around you!</h3>
                <LocationMapInput
                  :required="true"
                  :locationData="{}"
                  :allowCoordinates="false"
                  :showDescription="false"
                  @isValid="isLocationValid = $event"
                  @locationInfo="setLocation($event)"
                />
                <br />
                <PassportTagInput
                  id="passport-countries"
                  :default="passports"
                  :showMandatory="false"
                  :withLabel="true"
                  :required="false"
                  @passportCountry="passports = $event"
                />
              </div>
            </div>
          </b-step-item>

          <template slot="navigation" slot-scope="{previous, next}">
            <div class="buttons is-centered">
              <b-button
                @click.prevent="onPrevBtnClick(previous)"
                class="button is-primary is-danger"
                :icon-left="cancelIcon"
                id="cancel-button"
                type="is-danger"
              >{{ previous.disabled ? "Cancel" : "Previous" }}</b-button>
              <p class="control">
                <b-button
                  :class="{'is-loading': buttonLoadingAnimation}"
                  @click.prevent="onNextBtnClick(next, previous)"
                  class="button is-primary"
                  :icon-right="submitIcon"
                  id="submit-button"
                  type="is-success"
                >{{ next.disabled ? "Submit" : "Next" }}</b-button>
              </p>
            </div>
          </template>
        </b-steps>
      </div>
    </div>
  </section>
</template>


<script>
import api from "@/Api";
import {
  required,
  email,
  maxLength,
  minLength
} from "vuelidate/lib/validators";
import { getPasswordHash } from "@/utils";
import validationHelper from "@/utils/validations/validations";
import dateHelper from "@/utils/dates/dates";
import InputActivityTypes from "@/components/ActivityTypesInput";
import PassportTagInput from "../components/PassportTagInput";
import fitnessLevels from "@/constants";
import { mapActions } from "vuex";
import EmailSetup from "../components/EmailSetup";
import LocationMapInput from "@/components/MapComponents/LocationMapInput";
import ImageInput from "../components/ImageInput";
import blobHelper from "@/utils/converter/blob.js";

export default {
  name: "Signup",
  components: {
    ImageInput,
    EmailSetup,
    PassportTagInput,
    InputActivityTypes,
    LocationMapInput
  },
  data: function() {
    const today = new Date();
    return {
      isStepsClickable: true,
      page: 0,
      firstName: "",
      lastName: "",
      middleName: "",
      nickname: "",
      fitnessLevel: 2,
      bio: "",
      email: "",
      additionalEmails: [],
      password: "",
      birthDate: null,
      gender: "Male",
      activityTypes: [],
      passports: [],
      fitnessLevels: fitnessLevels.fitnessLevels,
      isLocationValid: true,
      longitude: null,
      latitude: null,
      placeId: null,
      address: "",
      suburb: "",
      country: "",
      city: "",
      state: "",
      description: "",
      primaryImage: null,
      coverImage: null,
      draggableMarkerPosition: null,
      markerId: 1001,
      // State of various elements in the page
      buttonLoadingAnimation: false,
      yearsRange: [-100, 100],
      minDate: new Date(
        today.getFullYear() - 100,
        today.getMonth(),
        today.getDate()
      ),
      maxDate: new Date(
        today.getFullYear() - 10,
        today.getMonth(),
        today.getDate()
      ),
      selectedDate: new Date(
        today.getFullYear() - 10,
        today.getMonth(),
        today.getDate()
      )
    };
  },
  created() {
    this.init();
  },
  computed: {
    cancelIcon() {
      if (this.page === 0) {
        return "times";
      }
      return "arrow-left";
    },
    submitIcon() {
      if (this.page === 0 || this.page === 1) {
        return "arrow-right";
      }
      return "check";
    }
  },
  methods: {
    ...mapActions(["setLoggedIn", "setUserId"]),
    getErrorLabel: validationHelper.getErrorLabel,
    /**
     * Formats date object to DD/MM/YYYY
     */
    dateFormatter: dateHelper.dateFormatter,
    /**
     * Formats date object to YYYY-MM-DD
     */
    dateFormatterToYYYYMMDD: dateHelper.dateFormatterToYYYYMMDD,
    dataURItoBlob: blobHelper.dataURItoBlob,
    status: validationHelper.status,
    init: function() {
      if (this.$params.devMode) {
        this.firstName = this.$params.user.firstName;
        this.lastName = this.$params.user.lastName;
        this.middleName = this.$params.user.middleName;
        this.nickname = this.$params.user.nickname;
        this.bio = this.$params.user.bio;
        this.email = this.$params.user.email;
        this.password = this.$params.user.password;
        this.birthDate = new Date(this.$params.user.birthDate);
        this.activityTypes = this.$params.user.activityTypes;
      }
    },
    created() {
      this.init();
    },
    async uploadUserImages(userId) {
      if (this.primaryImage) {
        const primaryImageType = this.primaryImage.split(";")[0].split(":")[1];
        await api.uploadUserHeroImage(
          userId,
          this.dataURItoBlob(this.primaryImage),
          primaryImageType
        );
      }

      if (this.coverImage) {
        const coverImageType = this.coverImage.split(";")[0].split(":")[1];
        await api.uploadUserCoverImage(
          userId,
          this.dataURItoBlob(this.coverImage),
          coverImageType
        );
      }
    },

    submit: async function(prev) {
      if (this.$v.$invalid || !this.isLocationValid) {
        this.$v.$touch();
        this.showErrorPopup("Please fill all the mandatory fields!");
        return;
      }

      this.buttonLoadingAnimation = true;

      // Generate the payload to backend server
      const payload = {
        firstname: this.firstName,
        lastname: this.lastName,
        middlename: this.middleName,
        nickname: this.nickname,
        bio: this.bio,
        primary_email: this.email,
        additional_email: this.additionalEmails
          .map(e => e.address)
          .filter(e => e.length > 0),
        password: getPasswordHash(this.password),
        date_of_birth: this.dateFormatterToYYYYMMDD(this.birthDate),
        gender: this.gender,
        fitness: this.fitnessLevel,
        passports: this.passports.map(p => p.name),
        activities: this.activityTypes,
        location: {
          placeId: this.placeId,
          longitude: this.longitude,
          latitude: this.latitude,
          description: "",
          isCoordinate: false
        }
      };
      try {
        let response;
        // Submit a POST request to the backend server
        await api.signup(payload);
        this.emailInvalid = false;
        this.buttonLoadingAnimation = false;

        response = await api.login({
          email: payload.primary_email,
          password: payload.password
        });

        this.setLoggedIn(true);
        this.setUserId(response.data.userId);

        await this.uploadUserImages(response.data.userId);

        this.$router.push({
          name: "profile",
          params: { userId: response.data.userId }
        });
      } catch (err) {
        prev.action();
        this.buttonLoadingAnimation = false;
        this.showErrorPopup(err.response.data.errors[0]);
      }
    },
    setLocation(location) {
      this.placeId = location.placeId;
      this.latitude = location.latitude;
      this.longitude = location.longitude;
      this.description = location.description;
    },

    
    nextPage(next, prev) {
      if (next.disabled) {
        // we are on the result page
        this.submit(prev);
      } else {
        // we are on the activity form page
        next.action();
      }
    },
    onPrevBtnClick(prev) {
      if (prev.disabled) {
        this.cancelActivity();
      } else {
        prev.action();
      }
    },
    cancelActivity() {
      this.$router.push({ name: "home" });
    },
    onNextBtnClick(next, prev) {
      if (this.$v.$invalid) {
        this.$buefy.toast.open({
          duration: 3000,
          message: "Must fill in mandatory fields!",
          type: "is-danger"
        });
        return;
      }

      api
        .getEmailIsInUse({ email: this.email })
        .then(result => {
          if (result.data) {
            this.$buefy.toast.open({
              duration: 3000,
              message: "Email address is already in use",
              type: "is-danger"
            });
          } else {
            this.nextPage(next, prev);
          }
        })
        .catch(() => {
          this.$buefy.toast.open({
            message: "Unable to determine whether email is already in use",
            type: "is-danger"
          });
        });
    },
  },
  validations: {
    firstName: { required, maxLength: maxLength(50) },
    middleName: { maxLength: maxLength(50) },
    lastName: { required, maxLength: maxLength(50) },
    nickname: { maxLength: maxLength(50) },
    bio: { maxLength: maxLength(200) },
    birthDate: { required },
    email: { required, email },
    password: { required, minLength: minLength(8) }
  },
  watch: {
    email: function() {
      if (this.email === "") return;

      api
        .getEmailIsInUse({ email: this.email })
        .then(result => {
          this.isStepsClickable = !result.data;
        })
        .catch(() => {
          this.$buefy.toast.open({
            message: "Unable to determine whether email is already in use",
            type: "is-danger"
          });
        });
    }
  },
};
</script>

<style scoped>
.required {
  color: red;
}

.fitness-level-slider {
  margin-bottom: 2.7rem;
}

#email-setup {
  margin-bottom: 1rem;
}
</style>