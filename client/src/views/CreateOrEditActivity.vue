<template>
  <div class="activities">
    <section class="hero">
      <div class="hero-body">
        <div class="container">
          <div class="columns is-centered">
            <div class="column is-four-fifths">
              <div class="has-text-centered title">
                {{ this.$route.name === "createActivity" ? "Create" : "Edit"}} an
                <span
                  class="title-icon icon is-large"
                >
                  <em class="fas fa-hiking"></em>
                </span>
                ctivity
              </div>
              <b-steps
                v-model="page"
                :animated="true"
                :has-navigation="true"
                :rounded="true"
                label-position="bottom"
                mobile-mode="compact"
              >
                <b-step-item :clickable="true" label="General" icon="list">
                  <div class="box">
                    <ActivityForm
                      :activityId="activityId"
                      :userId="userId"
                      :triggerValidation="triggerValidation"
                      @isValid="isFormValid = $event"
                      @payload="activityPayload = $event"
                      @visibilityValidation="isVisibilityValid = $event"
                      @isCreator="isCreator = $event"
                    />
                  </div>
                </b-step-item>

                <b-step-item :clickable="isFormValid" label="Location" icon="map-signs">
                  <div class="box">
                    <ActivityLocation
                      :locationNotNext="locationNotNext"
                      :activityId="activityId"
                      @location="activityLocation = $event"
                      @isValid="isLocationValid = $event"
                    />
                  </div>
                </b-step-item>

                <b-step-item :clickable="isLocationValid && isFormValid" label="Users" icon="users" v-if="activityId">
                  <div class="box">
                    <Sharing
                      :activityId="activityId"
                      :parentPage="page"
                      :isCreator="isCreator"
                      @updateUsers="usersPayload = $event"
                      @firstUpdate="isUserRolesModified = $event"
                    />
                  </div>
                </b-step-item>

                <b-step-item :clickable="isLocationValid && isFormValid" label="Results" icon="clipboard-list">
                  <PossibleResult
                    v-if="outcomeViewType === 'edit'"
                    :triggerResultValidation="triggerResultValidation"
                    :viewType="outcomeViewType"
                    :activityId="activityId"
                    @qCheck="isQuestionsValid = $event"
                    @checkValid="areQuestionsValid = $event"
                    v-model="outcomes"
                  />
                  <div v-else class="content is-unselectable info-text has-text-grey has-text-centered">
                    <p>
                      <b-icon icon="ban" size="is-large"></b-icon>
                    </p>
                    <p>Editing outcome results is disabled for organisers</p>
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
                        @click.prevent="onNextBtnClick(next)"
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
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import ActivityForm from "@/components/Activity/ActivityForm";
import Sharing from "@/components/Activity/Sharing";
import PossibleResult from "@/components/ResultComponents/PossibleResult";
import ActivityLocation from "../components/Activity/ActivityLocation";
import { mapActions, mapState } from "vuex";

export default {
  name: "CreateOrEditActivity",
  props: ["userId", "activityId"],

  data() {
    return {
      isCreator: false,
      outcomes: null,
      activityPayload: {},
      activityLocation: {},
      usersPayload: {},
      isFormValid: false,
      isLocationValid: false,
      locationNotNext: true, // keeps track of state if the next button is clicked when you're on location-step component
      isUserRolesModified: false,
      buttonLoadingAnimation: false,
      triggerValidation: false,
      triggerResultValidation: false,
      areQuestionsValid: [],
      isQuestionsValid: true,
      isVisibilityValid: true,
      page: 0,
    };
  },
  computed: {
    ...mapState("activities", ["activities"]),
    outcomeViewType() {
      if (!this.isCreator && this.activityId) {
        return "readOnly"
      } else {
        return "edit"
      }
    },
    cancelIcon() {
      if (this.page === 0) {
        return "times";
      }
      return "arrow-left";
    },
    submitIcon() {
      if (this.page === 0 || this.page === 1 || this.page === 2) {
        return "arrow-right";
      }
      return "check";
    },
  },
  components: {
    ActivityForm,
    PossibleResult,
    ActivityLocation,
    Sharing,
  },
  methods: {
    ...mapActions("activities", [
      "createActivity",
      "editActivity",
      "editActivityVisibility",
    ]),
    onNextBtnClick(next) {
      if (!this.isQuestionsValid) {
        this.$buefy.toast.open({
          duration: 3000,
          message: "Found duplicate questions!",
          type: "is-warning",
        });
        return;
      }
      if (!this.isFormValid) {
        // switch back to activity form page
        this.page = 0;
        this.triggerValidation = !this.triggerValidation;
        this.$buefy.toast.open({
          duration: 3000,
          message: "Please fill all the required fields!",
          type: "is-danger",
        });
        return;
      }

      // if location does not exist, and you're on the location-step component
      if (!this.isLocationValid && (this.page === 1)) {
        // if the next button is not clicked, then set locationNotNext to true (indicating it's been clicked)
        this.page = 1; // switch back to Activity Location page
        this.triggerValidation = !this.triggerValidation;
        this.$buefy.toast.open({
          duration: 3000,
          message: "Please enter a location for your activity!",
          type: "is-danger",
        });
        return;
      }

      this.nextPage(next);
    },
    nextPage(next) {
      if (next.disabled) {
        // we are on the result page
        this.submit();
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
      this.$router.go(-1);
    },
    /**
     * Submits data to the backend, API calls are handled by Vuex action
     * @see activities.js
     */
    async submit() {
      if (this.areQuestionsValid.includes(false)) {
        this.triggerResultValidation = true;
        this.$buefy.toast.open({
          duration: 3000,
          message: "Please fill out the form correctly",
          type: "is-warning",
        });
        return;
      }
      this.triggerResultValidation = false;
      this.buttonLoadingAnimation = true;
      let newActivityId;
      this.activityPayload.specifications = this.outcomes || [];
      this.activityPayload.location = this.activityLocation
      try {
        if (this.activityId) {
          await this.editActivity({
            userId: this.userId,
            activityId: this.activityId,
            payload: this.activityPayload,
          });
          if (this.isUserRolesModified) {
            await this.editActivityVisibility({
              userId: this.userId,
              activityId: this.activityId,
              payload: this.usersPayload,
            });
          }
        } else {
          newActivityId = await this.createActivity({
            userId: this.userId,
            activity: this.activityPayload,
          });
        }
        this.$router.push({
          name: "ViewActivity",
          params: {
            activityId: this.activityId ? this.activityId : newActivityId,
          },
        });
      } catch (err) {
        const errors = err.response.data.errors;
        let msg = err;
        if (errors && errors.length) {
          msg = errors[0].defaultMessage;
        }
        this.$buefy.toast.open({
          duration: 3000,
          message: msg,
          type: "is-danger",
        });
      } finally {
        this.buttonLoadingAnimation = false;
      }
    },
  },
};
</script>

<style scoped>
.title-icon {
  margin-left: -0.7rem;
  margin-right: -1.2rem;
}

.info-text {
  margin-top: 3rem;
  margin-bottom: 3rem;
}
</style>
