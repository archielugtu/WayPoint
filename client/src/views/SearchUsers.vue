<template>
  <div id="column-wrapper">
    <div class="columns">
      <div class="column is-one-quarter">
        <div class="box">
          <strong>Search Filters</strong>
          <hr/>
          <b-field custom-class="is-small" label="First Name">
            <b-input
              @keyup.native.enter="runSearch()"
              id="firstname"
              type="search"
              v-model="searchFirstName"
            ></b-input>
          </b-field>

          <b-field custom-class="is-small" label="Last Name">
            <b-input
              @keyup.native.enter="runSearch()"
              id="lastname"
              type="search"
              v-model="searchLastName"
            ></b-input>
          </b-field>

          <b-field custom-class="is-small" label="Email">
            <b-input
              @keyup.native.enter="runSearch()"
              id="email"
              type="search"
              v-model="searchEmail"
            ></b-input>
          </b-field>

          <b-field custom-class="is-small" label="Activity type">
            <div class="field">
              <div class="level-left">
                <b-dropdown aria-role="list" id="search-method" v-model="searchMethod">
                  <button class="button" slot="trigger" type="button">
                    <span>{{ searchMethod }}</span>
                    <b-icon icon="caret-down"></b-icon>
                  </button>

                  <b-dropdown-item key="1" value="All of" aria-role="listitem">
                    <span>All of</span>
                  </b-dropdown-item>
                  <b-dropdown-item key="2" value="Any of" aria-role="listitem">
                    <span>Any of</span>
                  </b-dropdown-item>
                </b-dropdown>

                <b-tooltip
                  id="info"
                  label="Changes how the search will find users. For example, selecting 'All of' with Skiing and
                       Hiking selected will find people who like both Skiing and Hiking. Selecting 'Any of' will find
                       people who like Skiing or Hiking, but not necessarily both."
                  multilined
                  position="is-right"
                  type="is-light"
                >
                  <b-icon icon="question-circle"></b-icon>
                </b-tooltip>
              </div>
            </div>
          </b-field>

          <InputActivityTypes
            :horizontalLabel="false"
            :required="false"
            :showMandatory="false"
            :withLabel="false"
            id="activity-types"
            v-bind:placeholder="'Select activity types to search for'"
            v-model="selectedOptions"
          />

          <div class="level-right">
            <button @click="runSearch()" class="button is-primary" id="search-btn">Search</button>
          </div>
        </div>
      </div>

      <div class="column is-three-quarters">
        <div class="box">
          <strong style="color: #b5b5b5" v-if="!foundUsers">No users found</strong>

          <div v-else>
            <b-table
              hoverable
              :data="users"
              :loading="loading"
              :selected.sync="selected"
              @dblclick="viewOrEditProfile(selected.userId, true)"
            >
              <template slot-scope="props">
                <b-table-column class="hoverable" centered field="isAdmin" label="Admin" v-if="fromAdminDashboard">
                  <b-icon icon="check" type="is-success" v-if="props.row.isAdmin"/>
                  <b-icon icon="times" v-else/>
                </b-table-column>

                <b-table-column class="hoverable" field="firstName" label="First Name">{{ props.row.firstname }}</b-table-column>

                <b-table-column class="hoverable" field="middleName" label="Middle Name">{{ props.row.middlename }}</b-table-column>

                <b-table-column class="hoverable" field="lastName" label="Last Name">{{ props.row.lastname }}</b-table-column>

                <b-table-column class="hoverable" field="primaryEmail" label="Email">{{ props.row.primary_email }}</b-table-column>

                <b-table-column class="hoverable" field="activities" label="Activities">
                  {{
                  props.row.activities.length > 3 ?
                  props.row.activities.slice(0, 3).join(", ") + "..." :
                  props.row.activities.slice(0, 3).join(", ")
                  }}
                </b-table-column>

                <b-table-column class="hoverable" v-if="fromAdminDashboard">
                  <b-dropdown position="is-bottom-left">
                    <a class="navbar-item" role="button" slot="trigger">
                      <b-icon icon="ellipsis-h"></b-icon>
                    </a>

                    <b-dropdown-item>
                      <b-button
                        @click="viewOrEditProfile(props.row.userId, false)"
                        class="option-item edit-btn button is-success"
                        expanded
                        icon-left="edit"
                      >Edit
                      </b-button>
                    </b-dropdown-item>

                    <b-dropdown-item>
                      <b-button
                        @click="confirmChangeAdminStatus(false, props.row.firstname, props.row.lastname,
                                                     props.row.userId)"
                        class="option-item button is-danger"
                        expanded
                        icon-left="times"
                        v-if="props.row.isAdmin"
                      >Revoke Admin
                      </b-button>

                      <b-button
                        @click="confirmChangeAdminStatus(true, props.row.firstname, props.row.lastname,
                                                     props.row.userId)"
                        class="option-item button is-success"
                        expanded
                        icon-left="check"
                        v-else
                      >Grant Admin
                      </b-button>
                    </b-dropdown-item>

                    <b-dropdown-item>
                      <b-button
                        @click="confirmDelete(props.row.userId, props.row.firstname, props.row.lastname)"
                        class="option-item delete-btn button is-danger"
                        expanded
                        icon-left="trash-alt"
                      >Delete
                      </b-button>
                    </b-dropdown-item>
                  </b-dropdown>
                </b-table-column>
              </template>
            </b-table>

            <div class="level" id="pagination">
              <div class="level-item">
                <button :disabled="currentPage <= 0" @click="prevPage()" class="button is-small">
                  <b-icon icon="angle-left"></b-icon>
                </button>
                <p id="pagination-txt-left">{{ currentPage > 0 ? "1 ... " : " " }}</p>
                <p id="pagination-txt-centre">{{ currentPage + 1 }}</p>
                <p
                  id="pagination-txt-right"
                >{{ maxPages > currentPage + 1 ? " ... " + maxPages : " " }}</p>
                <button
                  :disabled="currentPage + 1 >= maxPages"
                  @click="nextPage()"
                  class="button is-small"
                >
                  <b-icon icon="angle-right"></b-icon>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import api from "../Api";
  import {mapActions, mapState} from "vuex";
  import {ToastProgrammatic as Toast} from "buefy";
  import InputActivityTypes from "@/components/ActivityTypesInput";

  const separator = ",";

  export default {
    name: "SearchUsers",
    components: {
      InputActivityTypes
    },
    props: ["fromAdminDashboard", "fromSearchResults"],

    data: function () {
      return {
        users: [],
        foundUsers: false,
        searchFirstName: "",
        searchLastName: "",
        searchEmail: "",
        selectedOptions: [],
        searchMethod: "All of",

        currentFirstNameSearch: "",
        currentLastNameSearch: "",
        currentEmailSearch: "",
        currentMethodSearch: "All of",
        currentActivityTypeSearch: [],

        currentPage: 0,
        maxPages: 0,
        isScrollable: true,
        maxHeight: 200,
        loading: false,
        newSearch: false,
        selected: null
      };
    },

    created() {
      // Retrieve list of activity types from the backend if not populated yet
      // List is stored in Vuex store activities.js
      if (!this.activityTypes.length) {
        this.fetchActivityTypes();
      }

      // Retrieve previous search data from sessionStorage
      let from = sessionStorage.getItem("from");
      if (from != null && (from === "profile" || from === "profileEdit")) {
        this.currentFirstNameSearch =
          sessionStorage.getItem("prevSearchFirstName") || "";
        this.currentLastNameSearch = sessionStorage.getItem("prevSearchLastName") || "";
        this.currentEmailSearch = sessionStorage.getItem("prevSearchEmail") || "";
        let optionsStr = sessionStorage.getItem("prevSelectedOptions");
        if (optionsStr != null && optionsStr.length > 0)
          this.currentActivityTypeSearch = optionsStr.split(separator);
        this.currentMethodSearch = sessionStorage.getItem("prevSearchMethod") || "All of";
        this.currentPage = parseInt(sessionStorage.getItem("page"));
        if (isNaN(this.currentPage)) { this.currentPage = 0; }
      }

      // If we are coming from search results, run the search to populate the table
      if (this.fromSearchResults) {
        this.refreshTable();
      }
    },

    computed: {
      // Vuex store variable containing list of activity types
      ...mapState("activities", ["activityTypes"])
    },

    methods: {
      // Vuex store action to fetch activity types
      ...mapActions("activities", ["fetchActivityTypes"]),

      /**
       * Searches user with specified criterion
       * Searches that are surrounded with quotes will search for exact searches
       *
       * @param firstName the user's first name
       * @param lastName the user's last name
       * @param email the user's email
       * @param selectedOptions array of activity types
       * @param searchMethod "AND" or "OR"
       * @param newSearch Boolean for whether this search is new or a repeat of the last
       */
      async searchUsers(
        firstName,
        lastName,
        email,
        selectedOptions,
        searchMethod,
        newSearch
      ) {
        if (newSearch) this.currentPage = 0;
        const params = {
          firstname: firstName,
          lastname: lastName,
          email: email,
          page: this.currentPage,
          activities: selectedOptions.join("%20"),
          method: searchMethod === "All of" ? "and" : "or"
        };
        this.users = [];
        this.loading = true;
        let usersExtraInfo;
        try {
          const response = await api.searchProfiles(params);
          const results = response.data.results;
          const userEmails = results.map(r => r.primary_email);

          if (response.data.totalElements > 0) {
            usersExtraInfo = await api.getUserAdminStatusFromEmail({
              emails: userEmails.join(";")
            });
            usersExtraInfo = usersExtraInfo.data;
          }

          for (let i = 0; i < response.data.totalElements; i++) {
            let row = results[i];

            let userInfo = usersExtraInfo.find(
              u => u.primary_email === results[i].primary_email
            );
            row.isAdmin = userInfo["is_admin"];
            row.userId = userInfo["user_id"];
            this.users.push(row);
          }
          this.loading = false;
          this.foundUsers = response.data.totalElements > 0;
          this.maxPages = response.data.totalPages;
        } catch (err) {
          this.users = [];
          this.foundUsers = false;
          this.currentPage = 0;
          this.maxPages = 0;
          this.loading = false;
          Toast.open({
            message: "An error occurred while searching",
            type: "is-danger"
          });
        }
      },

      // Runs the search
      runSearch: function () {
        this.newSearch = true;
        this.currentFirstNameSearch = this.searchFirstName;
        this.currentLastNameSearch = this.searchLastName;
        this.currentEmailSearch = this.searchEmail;
        this.currentActivityTypeSearch = this.selectedOptions;
        this.currentMethodSearch = this.searchMethod;

        this.refreshTable();
        this.newSearch = false;
      },

      // Go to previous page results
      prevPage: function () {
        if (this.currentPage > 0) this.currentPage--;
        this.refreshTable();
      },

      // Go to next page results
      nextPage: function () {
        if (this.currentPage + 1 < this.maxPages) this.currentPage++;
        this.refreshTable();
      },

      // Refreshes the current page's results
      refreshTable: function () {
        this.searchFirstName = this.currentFirstNameSearch ;
        this.searchLastName = this.currentLastNameSearch ;
        this.searchEmail = this.currentEmailSearch ;
        this.selectedOptions = this.currentActivityTypeSearch ;
        this.searchMethod = this.currentMethodSearch ;

        this.searchUsers(
          this.currentFirstNameSearch,
          this.currentLastNameSearch,
          this.currentEmailSearch,
          this.selectedOptions,
          this.searchMethod,
          this.newSearch
        );
      },

      // Saves the current search and redirect the user to a search results' profile page
      viewOrEditProfile: function (userId, viewingProfile) {
        sessionStorage.setItem("prevSearchFirstName", this.currentFirstNameSearch);
        sessionStorage.setItem("prevSearchLastName", this.currentLastNameSearch);
        sessionStorage.setItem("prevSearchEmail", this.currentEmailSearch);
        sessionStorage.setItem(
          "prevSelectedOptions",
          this.selectedOptions.join(separator)
        );
        sessionStorage.setItem("prevSearchMethod", this.searchMethod);
        sessionStorage.setItem("page", this.currentPage);

        let routerName = "profileEdit";
        if (viewingProfile) routerName = "profile";
        this.$router.push({name: routerName, params: {userId: userId}});
      },

      // Asks admin user for confirmation before giving admin privilege to another user
      confirmChangeAdminStatus: function (
        givingPrivileges,
        firstName,
        lastName,
        userId
      ) {
        let text = "revoke the admin privileges of";
        if (givingPrivileges) text = "grant admin privileges to";
        this.$buefy.dialog.confirm({
          title: `${givingPrivileges ? "Granting" : "Revoking"} Admin Privileges`,
          message: `Are you sure you want to ${text} ${firstName} ${lastName}?`,
          type: "is-warning",
          onConfirm: () => this.changeAdminStatus(givingPrivileges, userId)
        });
      },

      // Grant/revoke admin privileges
      changeAdminStatus: function (givingPrivileges, userId) {
        let payload = {role: "user"};
        let action = ["revoked admin privileges.", "revoke admin privileges."];
        if (givingPrivileges) {
          payload.role = "admin";
          action = ["granted admin privileges.", "grant admin privileges."];
        }
        api
          .setAdminPrivileges(userId, payload)
          .then(() => {
            Toast.open({
              message: "Successfully " + action[0],
              type: "is-success"
            });
            this.refreshTable();
          })
          .catch(() => {
            Toast.open({
              message: "An error has occurred, unable to " + action[1],
              type: "is-danger"
            });
          });
      },

      // Asks for admin user confirmation before deleting a user
      confirmDelete: function (userId, firstName, lastName) {
        this.$buefy.dialog.confirm({
          title: `Delete ${firstName} ${lastName}'s Account`,
          message:
            `Are you sure you want to <b>delete</b> ${firstName}'s account?<br>` +
            `This cannot be undone!`,
          confirmText: "Delete",
          cancelText: "Cancel",
          type: "is-danger",
          hasIcon: true,
          onConfirm: () => this.deleteUser(userId)
        });
      },

      // Deletes a user associated with userId
      deleteUser: function (userId) {
        api
          .deleteUserProfile(userId)
          .then(() => {
            Toast.open({
              message: "Successfully deleted user.",
              type: "is-success"
            });
            this.updateTable();
          })
          .catch(() => {
            Toast.open({
              message: "An error has occurred, unable to delete target user.",
              type: "is-danger"
            });
          });
      },

      /**
       * Updates the results table, called when deleting a user
       * if the table only have a single result,
       * redirect to the previous page's results
       * else, updates the result of the table
       */
      updateTable: function () {
        if (this.users.length === 1) {
          if (this.currentPage === 0) {
            this.users = [];
            this.foundUsers = false;
            this.maxPages = 0;
            sessionStorage.setItem("page", "0");
          } else {
            this.prevPage();
          }
        } else {
          this.refreshTable();
        }
      }
    }
  };
</script>

<style scoped>
  #column-wrapper {
    margin: 3%;
  }

  #pagination {
    margin-top: 15px;
  }

  #pagination-txt-left {
    margin-left: 14px;
  }

  #pagination-txt-centre {
    margin-left: 4px;
    margin-right: 4px;
    font-weight: bold;
  }

  #pagination-txt-right {
    margin-right: 14px;
  }

  .option-item {
    padding-right: 1rem;
  }

  #info {
    padding-left: 0.5rem;
    color: #b5b5b5;
  }

  .hoverable {
    cursor: pointer
  }
</style>
