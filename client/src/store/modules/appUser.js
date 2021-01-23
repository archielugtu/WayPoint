import api from "@/Api";
import { ToastProgrammatic as Toast } from "buefy";

/** Converts user data from backend in snake_case to camelCase
 */
function toCamelCase(userData) {
  return {
    firstName: userData.firstname,
    lastName: userData.lastname,
    middleName: userData.middlename,
    nickname: userData.nickname,
    bio: userData.bio,
    gender: userData.gender,
    primaryEmail: userData.primary_email,
    additionalEmails: userData.additional_email,
    dateOfBirth: new Date(userData.date_of_birth),
    passports: userData.passports,
    activityTypes: userData.activities,
    fitness: userData.fitness,
    placeId: userData.placeId,
  };
}

const appUser = {
  namespaced: true,
  state: {
    userData: {
      firstName: "",
      lastName: "",
      middleName: "",
      nickname: "",
      bio: "",
      gender: "",
      primaryEmail: {},
      additionalEmails: [],
      dateOfBirth: null,
      passports: "",
      activityTypes: [],
      fitness: -1,
      placeId: ""
    },
  },

  getters: {
    getUserEmails: (state) => () => {
      let emails = [];
      state.userData.additionalEmails.forEach(e => emails.push(e.address));
      emails.push(state.userData.primaryEmail.address);
      return emails;
    }
  },

  mutations: {
    SET_USER_DATA(state, userData) {
      state.userData = toCamelCase(userData);
    },
  },

  actions: {
    async fetchUserData(context, userId) {
      if (userId <= 0) return;
      try {
        let result = await api.getProfileById(userId);
        context.commit("SET_USER_DATA", result.data);
      } catch (e) {
        Toast.open({
          message: "Unable to fetch user data",
          type: "is-danger"
        });
      }
    },
  },
};

export default appUser;
