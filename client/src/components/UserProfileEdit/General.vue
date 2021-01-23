<template>
  <div>
    <!-- start of box -->
    <div class="box">
      <div class="title is-4">General</div>
      <!-- First row containing firstname, lastname and middlename -->
      <div class="columns is-centered">
        <div class="column">
          <b-field
            :message="getErrorLabel($v.userData.firstname)"
            :type="status($v.userData.firstname)"
            id="firstname"
            label="First name"
          >
            <b-input
              icon="user"
              icon-pack="fas"
              placeholder="Your first name"
              v-model="$v.userData.firstname.$model"
            ></b-input>
          </b-field>
        </div>

        <div class="column">
          <b-field
            :message="getErrorLabel($v.userData.middlename)"
            :type="status($v.userData.middlename)"
            id="middlename"
            label="Middle name"
          >
            <b-input
              icon="user"
              icon-pack="fas"
              placeholder="Your middle name"
              v-model="$v.userData.middlename.$model"
            ></b-input>
          </b-field>
        </div>

        <div class="column">
          <b-field
            :message="getErrorLabel($v.userData.lastname)"
            :type="status($v.userData.lastname)"
            id="lastname"
            label="Last name"
          >
            <b-input
              icon="user"
              icon-pack="fas"
              placeholder="Your last name"
              v-model="$v.userData.lastname.$model"
            ></b-input>
          </b-field>
        </div>
      </div>
      <!-- First row end -->

      <div class="columns is-centered">
        <div class="column is-half">
          <b-field
            :message="getErrorLabel($v.userData.nickname)"
            :type="status($v.userData.nickname)"
            id="nickname"
            label="Nickname"
          >
            <b-input
              icon="user"
              icon-pack="fas"
              placeholder="Your nick name"
              v-model="$v.userData.nickname.$model"
            ></b-input>
          </b-field>
        </div>
        <div class="column">
          <b-field
            :message="getErrorLabel($v.userData.date_of_birth)"
            :type="status($v.userData.date_of_birth)"
            id="date-of-birth"
            label="Date of Birth"
          >
            <b-datepicker
              :date-formatter="dateFormatter"
              :focused-date="selectedDate"
              :max-date="maxDate"
              :min-date="minDate"
              placeholder="Click to select your birth date"
              v-model="$v.userData.date_of_birth.$model"
            ></b-datepicker>
          </b-field>
        </div>
      </div>

      <div class="columns is-centered">
        <div class="column">
          <!-- Gender -->

          <div class="field">
            <label class="label">Gender</label>
            <div class="control">
              <div class="field is-horizontal">
                <div class="field-body">
                  <div class="field is-narrow">
                    <div class="control">
                      <label class="radio">
                        <input name="member" type="radio" v-model="userData.gender" value="Male"/>
                        Male
                      </label>
                      <label class="radio">
                        <input name="member" type="radio" v-model="userData.gender" value="Female"/>
                        Female
                      </label>
                      <label class="radio">
                        <input
                          id="gender"
                          name="member"
                          type="radio"
                          v-model="userData.gender"
                          value="Non-binary"
                        />
                        Non-binary
                      </label>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <!-- Gender -->

        </div>
        <div class="column">
          <div class="field">

          <!-- Fitness Level -->
            <label class="label">Fitness Level</label>
            <div
              :class="{'is-active': fitnessDropdownToggled}"
              @click="toggleFitnessDropdown()"
              class="dropdown"
              id="fitness-dropdown"
            >
              <div class="dropdown-trigger">
                <button aria-controls="dropdown-menu" aria-haspopup="true" class="button">
                  <div v-if="this.fitnessLevels[userData.fitness] !== undefined">
                    <span>{{ this.fitnessLevels[userData.fitness].fitness }}</span>
                  </div>
                  <span class="icon is-small">
                    <i aria-hidden="true" class="fas fa-angle-down"></i>
                  </span>
                </button>
              </div>

              <div class="dropdown-menu" id="dropdown-menu" role="menu">
                <div class="dropdown-content">
                  <a
                    :key="fitnessInfo.level"
                    @click="saveFitnessLevel(fitnessInfo)"
                    class="dropdown-item"
                    v-for="fitnessInfo in fitnessLevels"
                  >{{ fitnessInfo.fitness }}</a>
                </div>
              </div>
            </div>
          </div>
          <!-- Fitness Level -->


        </div>

      </div>
      <b-field
        :message="getErrorLabel($v.userData.bio)"
        :type="status($v.userData.bio)"
        id="bio"
        label="Bio"
      >
        <b-input
          placeholder="A short description about yourself"
          type="textarea"
          v-model="$v.userData.bio.$model"
        ></b-input>
      </b-field>
    </div>

    <!-- end of box -->

    <div class="field is-grouped is-grouped-right">
      <div class="control">
        <router-link
          :to="{name: 'profile', userId: $route.params.userId}"
          class="button is-primary"
        >Back to profile
        </router-link>
      </div>
      <div class="control">
        <a @click="saveUserProfile" class="button is-success">
          <strong>
            Confirm
            Changes
          </strong>
        </a>
      </div>
    </div>
  </div>
</template>


<script>
  import {maxLength, required} from "vuelidate/lib/validators";
  import validationHelper from "@/utils/validations/validations";
  import dateHelper from "@/utils/dates/dates";
  import constants from "@/constants";

  export default {
    name: "EditProfileGeneral",
    props: ["userData"],

    data: function () {
      const today = new Date();
      return {
        fitnessLevels: constants.fitnessLevels,
        fitnessDropdownToggled: false,
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
    validations: {
      userData: {
        firstname: {
          required,
          maxLength: maxLength(50)
        },
        middlename: {
          maxLength: maxLength(50)
        },
        lastname: {
          required,
          maxLength: maxLength(50)
        },
        date_of_birth: {
          required
        },
        nickname: {
          maxLength: maxLength(50)
        },
        bio: {
          maxLength: maxLength(200)
        },
      }
    },
    methods: {
      getErrorLabel: validationHelper.getErrorLabel,
      status: validationHelper.status,
      dateFormatter: dateHelper.dateFormatter,
      toggleFitnessDropdown() {
        this.fitnessDropdownToggled = !this.fitnessDropdownToggled;
      },
      saveUserProfile() {
        if (this.$v.userData.$anyError || this.$v.userData.$invalid) {
          this.$buefy.toast.open({
            duration: 3000,
            message: "Please fill all the mandatory fields",
            type: "is-danger"
          });
          return;
        }
        this.$emit("click");
      },
      saveFitnessLevel(fitnessInfo) {
        this.userData.selectedFitness = fitnessInfo.fitness;
        this.userData.fitness = fitnessInfo.level;
      }
    }
  };
</script>


<style scoped>
  #bio {
    margin-top: 1rem;
  }
</style>
