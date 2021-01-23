<template>
  <div>
    <b-steps type="is-info" :animated="true" :has-navigation="false" mobile-mode="compact">
      <b-step-item icon="chalkboard-teacher" label="Created" :clickable="isStepsClickable">
        <UserActivityResponsibilities
          class="activity-involvement"
          :user-info="userInfo"
          activityRole="creator"
        ></UserActivityResponsibilities>
      </b-step-item>

      <b-step-item icon="people-arrows" label="Organising" :clickable="isStepsClickable">
        <UserActivityResponsibilities
          class="activity-involvement"
          :user-info="userInfo"
          activityRole="organiser"
        ></UserActivityResponsibilities>
      </b-step-item>

      <b-step-item icon="walking" label="Participating" :clickable="isStepsClickable">
        <UserActivityResponsibilities
          class="activity-involvement"
          :user-info="userInfo"
          activityRole="participant"
        ></UserActivityResponsibilities>
      </b-step-item>

      <b-step-item icon="user-friends" label="Following" :clickable="isStepsClickable">
        <UserActivityResponsibilities
          class="activity-involvement"
          :user-info="userInfo"
          activityRole="follower"
        ></UserActivityResponsibilities>
      </b-step-item>
    </b-steps>
  </div>
</template>

<script>
  import { mapActions } from "vuex";
  import UserActivityResponsibilities from "./UserProfile/UserActivityResponsibilities";

  export default {
    name: "UserActivities",
    props: ["userInfo"],
    components: {
      UserActivityResponsibilities
    },
    mounted() {
      this.init();
    },
    methods: {
      // Use actions defined in activities.js Vuex store
      ...mapActions("activities", ["fetchUserActivities"]),
      init() {
        this.fetchUserActivities(this.$route.params.userId);
      }
    },
    data() {
      return {
        isStepsClickable: true
      };
    }
  };
</script>

<style scoped>
  .activity-involvement {
    margin-top: 2rem;
  }
</style>
