import Vue from "vue";
import Vuex from "vuex";
import api from "@/Api";
import activities from "./modules/activities";
import appUser from "./modules/appUser";
import router from "@/router";
import { ToastProgrammatic as Toast } from "buefy";

Vue.use(Vuex);

const store = new Vuex.Store({
  modules: {
    activities: activities,
    appUser: appUser,
  },
  state: {
    isLoggedIn: false,
    isGlobalAdmin: false,
    isAdmin: false,
    currentUserProfileViewed: "",
    userId: -1,
    fromUserSearchPage: false,
    fromActivitySearchPage: false,
    prevActivitySearch: {}
  },
  getters: {
    hasAdminPrivileges: (state) => {
      return state.isAdmin || state.isGlobalAdmin;
    },
  },
  mutations: {
    SET_LOGGED_IN(state, status) {
      state.isLoggedIn = status;
    },
    SET_USER_ID(state, userId) {
      state.userId = userId;
    },
    SET_IS_GLOBAL_ADMIN(state, isGlobalAdmin) {
      state.isGlobalAdmin = isGlobalAdmin;
    },
    SET_IS_ADMIN(state, isAdmin) {
      state.isAdmin = isAdmin;
    },
    SET_USER_PROFILE_VIEWED(state, user) {
      state.currentUserProfileViewed = user;
    },
    SET_FROM_USER_SEARCH(state, fromUserSearchPage) {
      state.fromUserSearchPage = fromUserSearchPage
    },
    SET_FROM_ACTIVITY_SEARCH(state, fromActivitySearchPage) {
      state.fromActivitySearchPage = fromActivitySearchPage
    },
    SET_PREV_ACTIVITY_SEARCH(state, value) {
      state.prevActivitySearch = value
    }
  },
  actions: {
    setLoggedIn(context, status) {
      context.commit("SET_LOGGED_IN", status);
    },
    setUserId(context, userId) {
      context.commit("SET_USER_ID", Number(userId));
    },
    setIsGlobalAdmin(context, isGlobalAdmin) {
      context.commit("SET_IS_GLOBAL_ADMIN", isGlobalAdmin);
    },
    setIsAdmin(context, isAdmin) {
      context.commit("SET_IS_ADMIN", isAdmin);
    },
    setUserProfileViewed(context, user) {
      context.commit("SET_USER_PROFILE_VIEWED", user);
    },
    setFromUserSearch(context, fromUserSearchPage) {
      context.commit("SET_FROM_USER_SEARCH", fromUserSearchPage);
    },
    setFromActivitySearch(context, fromActivitySearchPage) {
      context.commit("SET_FROM_ACTIVITY_SEARCH", fromActivitySearchPage);
    },
    setPrevActivitySearch(context, value) {
      context.commit("SET_PREV_ACTIVITY_SEARCH", value);
    },
    async checkAuth(context) {
      if (context.state.isLoggedIn) return;
      Vue.$log.info("Checking authorisation");
      try {
        let response = await api.getAuth();
        context.commit("SET_USER_ID", response.data.user_id);
        context.commit("SET_LOGGED_IN", true);
        context.commit("SET_IS_GLOBAL_ADMIN", response.data.is_global_admin);
        context.commit("SET_IS_ADMIN", response.data.is_admin);
      } catch (err) {
        sessionStorage.clear();
        Vue.$log.info(err);
      }
    },
    async logoutUser(context) {
      sessionStorage.clear();
      try {
        await api.logout();
        context.commit("SET_LOGGED_IN", false);
        context.commit("SET_USER_ID", -1);
        context.commit("SET_IS_GLOBAL_ADMIN", false);
        context.commit("SET_IS_ADMIN", false);
        await router.push({ name: "login" });
      } catch (err) {
        Toast.open({
          duration: 2000,
          message: "An error has occurred, unable to log out user",
          type: "is-danger"
        });
      }
    },
  },
});

export default store;
