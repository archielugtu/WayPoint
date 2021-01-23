<template>
  <div>
    <div class="box">
      <div class="field">
        <div class="title is-4">Activities</div>
        <p>Tell us what you enjoy doing!</p>
      </div>

      <InputActivityTypes
        id="activity-types"
        v-model="activities"
        :showMandatory="false"
        :withLabel="false"
        :required="false"
      />
      <div class="field is-grouped is-grouped-right">
        <div class="control">
          <router-link
            :to="{name: 'profile', userId: $route.params.userId}"
            class="button is-primary"
          >Back to profile</router-link>
        </div>
        <div class="control">
          <a class="button is-success" @click="saveUserActivities">
            <strong>Save Activities</strong>
          </a>
        </div>
      </div>
    </div>
  </div>
</template>


<script>
  import api from "@/Api";
  import InputActivityTypes from "@/components/ActivityTypesInput";
  import { ToastProgrammatic as Toast } from "buefy";

  export default {
    name: "ActivityTypes",
    props: ["userData"],
    data: function() {
      return {
        activities: this.userData.activities,
        selectedActivity: "",
        hasDuplicateActivity: false
      };
    },
    components: {
      InputActivityTypes
    },
    methods: {
      saveUserActivities() {
        let payload = {
          activities: this.activities
        };

        api
          .changeUserActivities(this.$route.params.userId, payload)
          .then(() => {
            this.$buefy.toast.open({
              message: "Activities saved",
              type: "is-success"
            });
          })
          .catch(() => {
            Toast.open({
              message: "Unable to save changes",
              type: "is-danger"
            });
          });
      }
    }
  };
</script>


<style scoped>

</style>
