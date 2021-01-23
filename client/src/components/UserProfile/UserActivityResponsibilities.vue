<template>
  <div>
    <nav class="level">
      <div class="level-left">
        <b-switch
          true-value="Duration"
          false-value="Continuous"
          v-model="filterBy"
          type="is-dark"
        >{{ filterBy }}</b-switch>
      </div>

      <div class="level-right" v-if="Number(this.$route.params.userId) === Number(this.userId)">
        <b-button
          id="create-activity-btn"
          tag="router-link"
          :to="{name: 'createActivity', params: {userId: $route.params.userId}}"
          type="level-item is-primary"
        >Create Activity</b-button>
      </div>
    </nav>

    <p class="heading">Hint: double click an activity to learn more!</p>

    <div :key="activity.activityId" v-for="activity in displayedActivities">
      <SingleActivityCard :activityId="activity.activityId" />
    </div>
  </div>
</template>

<script>
  import { mapState, mapGetters } from "vuex";
  import SingleActivityCard from "@/components/Activity/SingleActivityCard.vue";

  export default {
    name: "UserActivityResponsibilities",
    props: ["userInfo", "activityRole"],
    components: {
      SingleActivityCard
    },
    data: function() {
      return {
        activitiesDropdownToggled: false,
        hasDuplicateActivity: false,
        filterBy: "Continuous",
        numActivitiesPerRow: 2,
      };
    },
    errorCaptured () {
      this.error = true
    },
    render (h) {

      return this.error ? h('p', 'Something went wrong.') : this.slots.default[0]
    },
    computed: {
      // Use state data stored in activities.js Vuex store
      ...mapState(["userId", "currentUserProfileViewed"]),
      ...mapGetters("activities", ["getActivityByRoleAndTimeRange"]),


      displayedActivities() {
        if (this.filterBy === "Continuous") {
          return this.getActivityByRoleAndTimeRange(this.activityRole, true)
        } else {
          return this.getActivityByRoleAndTimeRange(this.activityRole, false)
        }
      }
    },
    methods: {
      createActivity() {
        this.$router.push({ name: "createActivity" });
      }
    }
  };
</script>

<style scoped>
</style>
