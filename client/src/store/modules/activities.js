import Vue from "vue"
import api from "@/Api"
import {SnackbarProgrammatic as Snackbar} from 'buefy'
import casingHelper from "@/utils/casing/toCamelCase";


const activities = {
  namespaced: true,
  state: {
    activityTypes: [],
    activities: [],
    continuousActivities: [],
    durationActivities: []
  },
  getters: {
    /**
     * Retrieves activity details by its ID
     * @params activityId: activity's unique ID
     */
    getActivityById: (state) => (activityId) => {
      return state.activities.find(a => a.activityId === activityId)
    },

    getActivityByRoleAndTimeRange: (state) => (role, continuous) => {
      return state.activities.filter(a => a.role === role && a.continuous === continuous)
    }
  },
  mutations: {
    SET_ACTIVITIES(state, activitiesList) {
      state.activities = activitiesList.sort((a, b) => a.activityName.toLowerCase() > b.activityName.toLowerCase())
      state.continuousActivities = state.activities.filter(a => a.continuous)
      state.durationActivities = state.activities.filter(a => !a.continuous)
    },
    SET_ACTIVITY_TYPES(state, activityTypes) {
      state.activityTypes = activityTypes
    },
    REMOVE_ACTIVITY(state, activityId) {
      state.activities = state.activities.filter(e => e.activityId !== activityId)
      state.continuousActivities = state.activities.filter(a => a.continuous)
      state.durationActivities = state.activities.filter(a => !a.continuous)
    },
  },
  actions: {
    /**
     * Fetches list of available activity types from the backend
     * and saves it to the Vuex store
     */
    async fetchActivityTypes(context) {
      Vue.$log.debug("[Vuex] Fetching activity types from the backend")
      try {
        let response = await api.getActivityTypes()
        context.commit('SET_ACTIVITY_TYPES', response.data)
      } catch (err) {
        Vue.$log.warn("[Vuex] Unable to fetch activity types " + err)
      }
    },
    /**
     * Sets current logged in user's activities manually
     * (Not recommended unless you know what you are doing)
     * @params List of activities with their details (name, type, ...)
     */
    setActivities(context, activitiesList) {
      context.commit('SET_ACTIVITIES', activitiesList)
    },
    /**
     * Fetches a user's latest activities from the backend
     * and saves it to the Vuex store
     * @params userId: user's unique ID
     */
    async fetchUserActivities(context, userId) {
      Vue.$log.debug("[Vuex] Fetching activities for user ID " + userId)
      try {
        let response = await api.getUserActivities(userId)

        let activitiesList = response.data
        activitiesList = activitiesList.map(a => casingHelper.activityResponseToCamelCase(a))

        context.commit('SET_ACTIVITIES', activitiesList)
      } catch (err) {
        Vue.$log.warn("[Vuex] Unable to fetch user activities for user ID " + userId + 
          " with error " + err)
      }
    },

    /**
     * Sends an edit activity request to the backend
     * and fetches the latest activites from the backend
     * @params userId: user's unique ID
     * @params activityId: activity's unique ID to be edited
     * @params payload: updated activity's data (title, type, ...)
     */
    async editActivity(context, {userId, activityId, payload}) {
      Vue.$log.debug("[Vuex] Editing activity for user ID " + userId +
        ", activity ID " + activityId +
        ", payload " + payload)
      try {
        await api.editActivity(userId, activityId, payload)
        await context.dispatch("fetchUserActivities", userId)
      } catch (err) {
        Vue.$log.warn("[Vuex] Unable to edit activity for user ID " + userId +
          ", activity ID " + activityId +
          ", payload " + payload +
          " with error " + err)
      }
    },
    /**
     * Sends an edit visibility request to the backend
     * @params userId: user's unique ID
     * @params activityId: activity's unique ID to be edited
     * @params payload: data being updated for this activity
     */
    async editActivityVisibility(context, {userId, activityId, payload}) {
      Vue.$log.debug("[Vuex] Changing activity visibility for " + userId +
        ", activity ID " + activityId +
        ", payload " + payload)
      try {
        await api.putActivityVisibility(userId, activityId, payload)
      } catch (err) {
        Vue.$log.warn("[Vuex] Unable to edit visibility for user ID " + userId +
          ", activity ID " + activityId +
          ", payload " + payload +
          " with error " + err)
      }
    },
    /**
     * Sends a delete activity request to the backend
     * @params: userId: user's unique ID
     * @params: activityId: activity's unique ID to be deleted 
     */
    async removeActivity(context, {userId, activityId}) {
      Vue.$log.debug("[Vuex] Removing activity for user id " + userId + ", activity ID " + activityId)
      try {
        await api.deleteUserActivity(userId, activityId)
        context.commit('REMOVE_ACTIVITY', activityId)
        Snackbar.open('Activity removed!')
      } catch (err) {
        Vue.$log.warn("Unable to delete activity ID " + activityId +
          " with error " + err)
        throw err
      }
    },
    /**
     * Creates an activity by sending activity data to the backend. 
     * Also fetches and store latest user activites list to the store
     * @params userId: user's unique ID
     * @params activity: activity data payload (title, type, ...)
     */
    async createActivity(context, {userId, activity}) {
      Vue.$log.debug("[Vuex] Creating activity for user id " + userId)
      try {
        // create a new activity in the backend
        let response = await api.createActivity(userId, activity)
        await context.dispatch("fetchUserActivities", userId)
        return response.data
      } catch (err) {
        Vue.$log.warn("Unable to create for user ID " + userId + 
          " with payload " + activity +
          " with error " + err)
        throw err
      }
    },
  }
};

export default activities;
