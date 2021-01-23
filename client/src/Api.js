import axios from "axios";
import { SERVER_URL } from "@/config.js";
import { ToastProgrammatic as Toast } from "buefy";

const instance = axios.create({
  baseURL: SERVER_URL,
  withCredentials: true,
  timeout: 5000,
});


// Tell axios to intercept incoming responses first and check for
// the status code before passing it on onwards.
instance.interceptors.response.use(
  function(response) {
    return response;
  },
  function(error) {
    if (error.response) {
      if (error.response.status === 403) {
        // Forbidden
        Toast.open({
          message: "You don't have permissions to do this action",
          type: "is-danger",
        });
      } else if (error.response.status === 404) {
        // Page not found
        Toast.open({
          message: "Page does not exist!",
          type: "is-danger",
        });
      } else if (error.response.status === 412) {
        // Precondition failed
        Toast.open({
          duration: 5000,
          message:
            "Looks like your data is outdated, please refresh the page before continuing",
          type: "is-warning",
        });
      }
    }
    return Promise.reject(error);
  }
);

export default {
  /**
   * Checks the AUTH status of a user by sending GET /auth to the backend
   */
  getAuth: () => instance.get("/auth"),

  /**
   * Sends a POST request to backend server when
   * registering a new user
   *
   * @param payload: Register form data
   * @returns Promise with response from backend server
   */
  signup: (payload) => instance.post("/profiles", payload),

  /**
   * Sends a POST request to backend server when
   * user is logging in
   *
   * @param payload     Login data (email and password)
   * @param cancelToken Cancel token to handle timeouts
   * @returns Promise with response from backend server
   */
  login: (payload, cancelToken) =>
    instance.post("/login", payload, cancelToken),

  /**
   * Sends a POST request to backend server to
   * log out user
   *
   * @returns Promise with response from backend server
   */
  logout: () => instance.post("/logout"),

  /**
   * Sends a GET request to backend server
   * to load user profile data
   *
   * Requires auth token to be present in localStorage
   *
   * @param userId: ID of user to be loaded
   * @returns Promise with response from backend server
   */
  getProfileById: (userId) => instance.get(`/profiles/${userId}`),

  /**
   * Sends a GET request to backend server
   * to load activities data
   *
   * Requires auth token to be present in localStorage
   *
   * @returns Promise with response from backend server
   */
  getActivityTypes: () => instance.get(`/activities/types`),

  /**
   * Sends a GET request to backend server
   * to load user profile data
   *
   * Requires auth token to be present in localStorage
   *
   * @param userId: ID of user to be loaded
   * @returns Promise with response from backend server
   */
  getUserActivitiesById: (userId) =>
    instance.get(`/profiles/${userId}/activity-types`),

  /**
   * Sends a GET request to backend server
   * to load activity results
   *
   * @param activityId: ID of activity to be loaded
   * @returns Promise with response from backend server
   */
  getActivitySpecificationById: (activityId) =>
    instance.get(`/activities/${activityId}/spec`),

  /**
   * Sends a PUT request to backend server
   * to change user's activities
   *
   * @param userId  The ID of the user whose activities are being changed
   * @param payload Payload of data containing user's activities
   * @returns {Promise<AxiosResponse<T>>}
   */
  changeUserActivities: (userId, payload) =>
    instance.put(`/profiles/${userId}/activity-types`, payload),

  /**
   * Sends a POST request to backend server
   * to change user password
   *
   * @param userId  The ID of the user whose password is being changed
   * @param payload Payload which contains old password, and the new password
   * @returns {Promise<AxiosResponse<T>>}
   */
  sendChangePassword: (userId, payload) =>
    instance.post(`/profiles/${userId}/password`, payload),

  /**
   * Sends a PUT request to backend server
   * to change user emails
   *
   * @param userId  The ID of the user whose emails are being updated
   * @param payload Payload containing primary email, and an array of secondary emails
   * @returns {Promise<AxiosResponse<T>>}
   */
  sendChangeEmails: (userId, payload) =>
    instance.put(`/profiles/${userId}/emails`, payload),

  /**
   * Sends a PUT request to backend server
   * to update user profile
   *
   * @param userId The ID of the user whose profile is being updated
   * @param payload Payload containing user data
   * @param eTagHeader Tag indicating user's "version" in the db
   * @returns {Promise<AxiosResponse<T>>}
   */
  updateUserProfile: (userId, payload, eTagHeader) =>
    instance.put(`/profiles/${userId}`, payload, {
      headers: {
        "If-Match": eTagHeader,
      },
    }),

  /**
   * Sends a GET request to find all available hashtags
   * Hashtags should be sorted by most commonly used
   *
   * @returns {Promise<AxiosResponse<T>>}
   */
  getAllHashtags: () => instance.get("/activities/hashtags"),

  /**
   * Sends a DELETE request to backend server
   * to delete the specified user
   *
   * @param userId The ID of the user who should be deleted
   */
  deleteUserProfile: (userId) => instance.delete(`/profiles/${userId}`),

  /**
   * Sends a POST request to backend server
   * to create an activity
   *
   * @param userId  The ID of the user creating the activity
   * @param payload Payload containing user data
   * @returns {Promise<AxiosResponse<T>>}
   */
  createActivity: (userId, payload) =>
    instance.post(`/profiles/${userId}/activities`, payload),

  /**
   * Sends a GET request to backend server
   * to get all the activities created by user
   *
   * @param userId The ID of the user to retrieve all their activities
   * @returns {Promise<AxiosResponse<T>>}
   */
  getActivitiesCreatedByUser: (userId) =>
    instance.get(`/profiles/${userId}/activities/created`),

  /**
   * Sends a GET request to backend server
   * to get all the activities related to a user (following/participating/organising/created)
   *
   * @param userId The ID of the user to retrieve all their activities
   * @returns {Promise<AxiosResponse<T>>}
   */
  getUserActivities: (userId) => instance.get(`/profiles/${userId}/activities`),

  /**
   * Sends a GET request to backend server
   * to get the activity associated with the provided ID
   *
   * @param activityId The ID of the activity to be retrieved
   * @returns {Promise<AxiosResponse<T>>}
   */
  getActivityById: (activityId) => instance.get(`/activities/${activityId}`),

  /**
   * Sends a DELETE request to backend server
   * to delete an activity
   *
   * @param userId     The ID of the user who created the activity
   * @param activityId The ID of the activity to delete
   * @returns {Promise<AxiosResponse<T>>}
   */
  deleteUserActivity: (userId, activityId) =>
    instance.delete(`/profiles/${userId}/activities/${activityId}`),

  /**
   * Sends a PUT request to backend server
   * to edit an activity
   *
   * @param userId     The ID of the user who created the activity
   * @param activityId The ID of the activity to be edited
   * @param payload    The (possibly) altered activity fields
   * @returns {Promise<AxiosResponse<T>>}
   */
  editActivity: (userId, activityId, payload) =>
    instance.put(`/profiles/${userId}/activities/${activityId}`, payload),

  /**
   * Sends a GET request to the backend server
   * to retrieve profiles matching the search parameters
   *
   * @param params A dictionary of parameters for the search,
   * containing first name, last name, email address, activity
   * types string (each activity separated by '%20'), the page
   * number to retrieve, and the search method ('and' or 'or').
   * @returns {Promise<AxiosResponse<T>>}
   */
  searchProfiles: (params) => instance.get(`/profiles`, {params}),

  /**
   *
   * Sends a GET request to the backend server
   * to retrieve user's ID and admin status with specified email address
   *
   * @param params List of email address separated by ';'
   * @returns {Promise<AxiosResponse<T>>}
   */
  getUserAdminStatusFromEmail: (params) =>
    instance.get(`/profiles/search_email`, {params}),

  /**
   * Sends a PUT request to the backend server
   * to set whether the user is an admin or not
   *
   * @param userId  The ID of the user to set the admin status of
   * @param payload The user's new admin status
   * @returns {Promise<AxiosResponse<T>>}
   */
  setAdminPrivileges: (userId, payload) =>
    instance.put(`/profiles/${userId}/role`, payload),

  /**
   * Sends a GET request to the backend server
   * to retrieve activities that match search params
   * 
   * @returns {Promise<AxiosResponse<T>>}
   */
  searchActivities: (params) => instance.get(`/activities`, {params}),

  /**
   * Sends a POST request to the backend server
   * to edit the user's role for the activity
   *
   * @param userId     The ID of the user to change the role of
   * @param activityId The ID of the activity which the user wants to update their role for
   * @param payload    The user's new role, as a string
   * @returns {Promise<AxiosResponse<T>>}
   */
  editUserRole: (userId, activityId, payload) =>
    instance.post(
      `/profiles/${userId}/subscriptions/activities/${activityId}`,
      payload
    ),

  /**
   * Sends a POST request to the backend server
   * to edit the participator's role for the activity
   * that the activity creator has specified
   *
   * @param userId     The ID of the user to change the role of
   * @param activityId The ID of the activity which the user wants to update their role for
   * @param payload    The user's new role, as a string
   * @returns {Promise<AxiosResponse<T>>}
   */
  creatorEditUserRole: (userId, activityId, participatorId, payload) =>
    instance.post(`/profiles/${userId}/subscriptions/activities/${activityId}/${participatorId}`, payload),

  /**
   * Sends a DELETE request to the backend server
   * to delete the user's role from the activity
   *
   * @param userId     The ID of the user to change the role of
   * @param activityId The ID of the activity which the user wants to delete their role for
   * @returns {Promise<AxiosResponse<T>>}
   */
  deleteUserRole: (userId, activityId) =>
    instance.delete(
      `/profiles/${userId}/subscriptions/activities/${activityId}`
    ),

  /**
   * Sends a GET request to the backend server
   * to retrieve the history of an activity that the user is following
   *
   * @param userId The ID of the user whom activity history should be retrieved for
   * @param params The page number of results to retrieve
   * @returns {Promise<AxiosResponse<any>>}
   */
  getUserHomeFeed: (userId, params) =>
    instance.get(`/profiles/${userId}/subscriptions/activities/history`, {
      params,
    }),

  /**
   * Sends a POST request to the backend server
   * to subscribe the user to the activity
   *
   * @param userId     The ID of the user to subscribe to an activity
   * @param activityId The ID of the activity the user wishes to subscribe to
   */
  followActivity: (userId, activityId) =>
    instance.post(
      `/profiles/${userId}/subscriptions/activities/${activityId}`,
      { role: "follower" }
    ),

  /**
   * Sends a DELETE request to the backend server
   * to unsubscribe the user from the activity
   *
   * @param userId     The ID of the user who wishes to be unsubscribed
   * @param activityId The ID of the activity the user wishes to be unsubscribed from
   */
  unfollowActivity: (userId, activityId) =>
    instance.delete(`/profiles/${userId}/subscriptions/activities/${activityId}`),

  /**
   * Sends a POST request to the backend server
   * to participate the user to the activity
   *
   * @param userId     The ID of the user who wishes to participate to an activity
   * @param activityId The ID of the activity the user wishes to participate to
   */
  participateActivity: (userId, activityId) =>
    instance.post(
      `/profiles/${userId}/subscriptions/activities/${activityId}`,
      { role: "participant" }
    ),

  /**
   * Sends a DELETE request to the backend server
   * to delete the participation of the user from the activity
   *
   * @param userId     The ID of the user who wishes to be not participating
   * @param activityId The ID of the activity the user wishes to be not participate from
   */
  deleteActivityParticipation: (userId, activityId) =>
    instance.delete(`/profiles/${userId}/subscriptions/activities/${activityId}`),

  /**
   * Sends a GET request to the backend server
   * to find out the relation a user has to an activity.
   * e.g. follower, creator, none
   *
   * @param activityId The ID of the activity the role should be retrieved for
   */
  getActivityUserRelation: (activityId) =>
    instance.get(`/activities/${activityId}/role`),

  /**
   * Sends a GET request to the backend server
   * to retrieve the followers of an activity
   *
   * @param activityId The ID of the activity to retrieve the followers of
   * @param params     The page number and size for results
   */
  fetchActivityFollowers: (activityId, params) =>
    instance.get(`/activities/${activityId}/followers`, {params}),

  /**
   * Sends a GET request to the backend server
   * to retrieve the organisers of an activity
   *
   * @param activityId The ID of the activity to retrieve the followers of
   * @param params     The page number and size for results
   */
  fetchActivityOrganisers: (activityId, params) =>
    instance.get(`/activities/${activityId}/organisers`, {params}),

  /**
   * Sends a GET request to the backend server
   * to retrieve the participants of an activity
   *
   * @param activityId The ID of the activity to retrieve the followers of
   * @param params     The page number and size for results
   */
  fetchActivityParticipants: (activityId, params) =>
    instance.get(`/activities/${activityId}/participants`, {params}),

  /**
   * Sends a GET request to the backend server
   * to retrieve number of users organising/participating/following an activity
   *
   * @param activityId The ID of the activity to retrieve the followers of
   */
  fetchActivityHeadcounts: (activityId) =>
    instance.get(`/activities/${activityId}/headcount`),

  /**
   * Sends a GET request to the backend server
   * to retrieve paginated users organising/participating/following an activity
   *
   * @param activityId The ID of the activity to retrieve the followers of
   * @param params     Object containing page (int) and size (int)
   */
  fetchUsersRelatedToActivity: (activityId, params) =>
    instance.get(`/activities/${activityId}/users`, {params}),

  /**
   * Sends a PUT request to change the visibility of an activity.
   * Also sends the list of users and their roles for after the change
   *
   * @param activityId The ID of the activity to change visibility for
   * @param userId     The ID of the user who created hte activity
   * @param payload    payload containing data on new visibliity and users associated
   */
  putActivityVisibility: (userId, activityId, payload) =>
    instance.put(`/profiles/${userId}/activities/${activityId}/visibility`, payload),

  /**
   * Sends a PUT request to the backend server
   * to submit the activity outcomes
   *
   * @param userId     The ID of the user to submit their answer
   * @param activityId The ID of the activity that the question is associated with
   * @param payload    The user's answers
   */
  putActivityOutcomes: (userId, activityId, payload) =>
    instance.put(`/activity/${activityId}/outcomes/${userId}/answers`, payload),

  /**
   * Sends a GET request to the backend server
   * to get the activity outcomes for a given user
   *
   * @param userId     The ID of the user to retrieve the activity outcomes of
   * @param activityId The ID of the activity to retrieve the outcomes from
   * @returns {Promise<AxiosResponse<any>>}
   */
  getUserOutcomeAnswers: (userId, activityId) =>
    instance.get(`/activity/${activityId}/outcomes/${userId}/answers`),

  /**
   * Sends a GET request to the backend server
   * to get the users who have answered the questons
   *
   * @returns {Promise<AxiosResponse<any>>}
   */
  getRespondentsByActivityId: (activityId) => instance.get(`/activities/${activityId}/outcomes/users`),

  /**
   * Sends a GET request to the backend server
   * to get whether an email is already in use or not
   *
   * @param params the email address to check the uniqueness of
   * @returns {Promise<AxiosResponse<any>>}
   */
  getEmailIsInUse: (params) => instance.get(`/profiles/emailIsInUse`, {params}),

  /**
   * Sends a GET request to the backend server
   * to fetch all the photos related to an activity
   *
   * @returns {Promise<AxiosResponse<any>>}
   */
  fetchActivityPhotos: (activityId) =>
    instance.get(`/activities/${activityId}/photos`),

  /**
   * Sends a GET request to the backend server
   * to fetch single photo binary related to activity
   *
   * @returns {Promise<AxiosResponse<any>>}
   */
  fetchActivityPhoto: (activityId, photoId) =>
    instance.get(`/activities/${activityId}/photos/${photoId}`),

  /**
   * Sends a POST request to the backend server
   * to upload a new photo related to the activity
   *
   * @returns {Promise<AxiosResponse<any>>}
   */
  uploadActivityPhoto: (activityId, photo, photoType) =>
    instance.post(`/activities/${activityId}/photos`, photo, {
      headers: { "Content-Type": photoType },
    }),

  /**
   * Sends a PUT request to the backend server
   * to set a new primary image for an activity specified by photoId
   *
   * @returns {Promise<AxiosResponse<any>>}
   */
  setActivityImageAsPrimary: (activityId, photoId) =>
    instance.put(`/activities/${activityId}/photos/${photoId}/primary`),

  /**
   * Sends a DELETE request to the backend server
   * to set a new primary image for an activity specified by photoId
   *
   * @returns {Promise<AxiosResponse<any>>}
   */
  deleteActivityImage: (activityId, photoId) =>
    instance.delete(`/activities/${activityId}/photos/${photoId}`),

  /**
   * Sends a PUT request to the backend server
   * to set the user's profile image
   *
   * @param userId    User's unique ID
   * @param photo     Photo to be uploaded
   * @param photoType Image type, e.g. "image/jpg"
   * @returns {Promise<AxiosResponse<any>>}
   */
  uploadUserHeroImage: (userId, photo, photoType) =>
      instance.put(`/profiles/${userId}/photos/primary`, photo, {
        headers: { "Content-Type": photoType },
      }),

  /**
   * Sends a GET request to the backend server
   * to retrieve the activity's primary photo
   *
   * @param activityId The ID of the activity to retrieve the primary photo of
   * @returns {Promise<AxiosResponse<any>>}
   */
  getActivityPrimaryPhoto: (activityId) =>
    instance.get(`/activities/${activityId}/photos/primary`),

  /**
   * Sends a PUT request to the backend server
   * to set the user's cover image
   *
   * @param userId    User's unique ID
   * @param photo     Photo to be uploaded
   * @param photoType Image type, e.g. "image/jpg"
   * @returns {Promise<AxiosResponse<any>>}
   */
  uploadUserCoverImage: (userId, photo, photoType) =>
      instance.put(`/profiles/${userId}/photos/cover`, photo, {
        headers: { "Content-Type": photoType },
      }),

  /**
   * Sends a DELETE request to the backend server
   * to delete the user's cover image
   *
   * @param userId User's unique ID
   * @returns {Promise<AxiosResponse<any>>}
   */
  deleteCoverImage: (userId) =>
    instance.delete(`/profiles/${userId}/photos/cover`),

  /**
   * Sends a DELETE request to the backend server
   * to delete the user's profile image
   *
   * @param userId User's unique ID
   * @returns {Promise<AxiosResponse<any>>}
   */
  deletePrimaryImage: (userId) =>
    instance.delete(`/profiles/${userId}/photos/primary`),

  /**
   * Sends a GET request to the backend server
   * to retrieve the user's profile image
   *
   * @param userId The ID of the user to retrieve the primary photo of
   * @returns {Promise<AxiosResponse<any>>}
   */
  getUserPrimaryPhoto: (userId) =>
    instance.get(`/profiles/${userId}/photos/primary`),

  /**
   * Sends a GET request to the backend server
   * to retrieve the user's cover image
   *
   * @param userId The ID of the user to retrieve the cover photo of
   * @returns {Promise<AxiosResponse<any>>}
   */
  getUserCoverPhoto: (userId) =>
    instance.get(`/profiles/${userId}/photos/cover`),
};
