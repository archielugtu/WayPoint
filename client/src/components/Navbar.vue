<template>
  <nav
    :class="routeName === 'home' ? 'has-shadow is-transparent' : generateNavbarStyle"
    class="navbar"
    role="navigation"
    aria-label="main navigation"
  >
    <div class="navbar-brand">
      <a id="logo" class="navbar-item" @click="goHome">
        <img
          src="https://cdn0.iconfinder.com/data/icons/geography-flat/58/048_-_Map_Pin-512.png"
          width="28"
          height="28"
          alt="Waypoint icon"
        />
        <span class="title is-2" style="color: forestgreen">Waypoint</span>
      </a>

      <a
        role="button"
        :class="{ 'is-active': isOpen }"
        class="navbar-burger burger"
        aria-label="menu"
        aria-expanded="false"
        data-target="navbarBasicExample"
        @click="toggleActiveNavbar"
      >
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
      </a>
    </div>

    <div id="navbarBasicExample" class="navbar-menu" v-bind:class="{ 'is-active': isOpen }">
      <div class="navbar-start" v-if="hasAdminPrivileges">
        <div class="navbar-item">
          <b-button
            tag="router-link"
            @click="isOpen = false"
            id="dashboard"
            type="is-dark"
            icon-left="user-secret"
            :to="{name: 'adminDashboard'}"
          >Admin Dashboard</b-button>
        </div>
      </div>

      <div class="navbar-end">
        <div class="navbar-item">
          <div class="buttons" v-if="!isLoggedIn">
            <b-button
              tag="router-link"
              @click="isOpen = false"
              id="signup"
              type="is-primary"
              icon-left="user-plus"
              to="/signup"
            >
              <strong>
                Sign up
              </strong>
            </b-button>
            <b-button
              tag="router-link"
              @click="isOpen = false"
              id="login"
              type="is-light"
              icon-left="sign-in-alt"
              to="/login"
            >Log in</b-button>
          </div>
          <div v-else>
            <div class="buttons">
              <b-button
                id="homeFeed"
                v-if="!isGlobalAdmin"
                tag="router-link"
                icon-left="fas fa-home"
                inverted
                :type="hasAdminPrivileges ? 'is-black' : 'is-primary'"
                :to="{name: 'homeFeed', params: {userId: profileBtnTargetUserId}}"
              >Home</b-button>

              <b-button
                tag="router-link"
                id="searchActivity"
                @click="isOpen = false"
                :type="hasAdminPrivileges ? 'is-black' : 'is-primary'"
                inverted
                icon-left="map"
                :to="{name: 'SearchActivities', params: { fromSearchResults: fromActivitySearchPage }}"
              >
                <span v-if="fromActivitySearchPage">
                  Back to search
                </span>
                <span v-else>
                  Search Activities
                </span>
              </b-button>

              <b-button
                tag="router-link"
                id="searchUser"
                @click="isOpen = false"
                :type="hasAdminPrivileges ? 'is-black' : 'is-primary'"
                inverted
                :icon-left="fromUserSearchPage ? 'chevron-left' : 'users' "
                :to="{name: 'SearchUsers', params: { fromSearchResults: fromUserSearchPage }}"
              >
                <span v-if="fromUserSearchPage">
                  Back to search</span>
                <span v-else>
                  Search Users</span>
              </b-button>

              <b-button
                v-if="profileBtnShowUp"
                tag="router-link"
                @click="isOpen = false"
                id="profile"
                :type="hasAdminPrivileges ? 'is-black' : 'is-primary'"
                icon-left="user"
                :inverted="routeName !== 'home'"
                :to="{name: profileBtnUrl, params: {userId: profileBtnTargetUserId}}"
              >{{ profileBtnText }}</b-button>
              <b-button
                @click="logoutUser"
                icon-left="sign-out-alt"
                id="logout"
                :type="hasAdminPrivileges ? 'is-black' : 'is-danger'"
              >Log out</b-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
  import { mapState, mapGetters, mapActions } from "vuex";

  export default {
    name: "Navbar",

    data: function() {
      return {
        isOpen: false,
        route: this.$router.currentRoute.path,
        routeName: this.$router.currentRoute.name
      };
    },

    watch: {
      $route: function() {
        this.route = this.$router.currentRoute.path;
        this.routeName = this.$router.currentRoute.name;
      }
    },

    computed: {
      ...mapState([
        "isLoggedIn",
        "isGlobalAdmin",
        "userId",
        "currentUserProfileViewed",
        "fromUserSearchPage",
        "fromActivitySearchPage"
      ]),

      ...mapGetters(["hasAdminPrivileges"]),

      profileBtnText() {
        let seeOwnTxt = "My Profile";
        let editOwnTxt = "Edit Profile";

        if (this.isViewingSomeoneElseProfile) {
          if (this.hasAdminPrivileges) {
            return `Edit ${this.currentUserProfileViewed}'s profile`;
          } else {
            return seeOwnTxt;
          }
        } else {
          if (this.routeName === "profile") return editOwnTxt;
          return seeOwnTxt;
        }
      },

      profileBtnUrl() {
        let url = "profileEdit";
        if (this.isViewingSomeoneElseProfile && !this.hasAdminPrivileges) {
          url = "profile";
        }
        return this.routeName !== "profile" ? "profile" : url;
      },

      generateNavbarStyle() {
        if (this.hasAdminPrivileges) {
          return "is-black";
        } else {
          return "is-transparent has-shadow";
        }
      },

      isViewingSomeoneElseProfile() {
        return (
          this.routeName === "profile" &&
          Number(this.$route.params.userId) !== Number(this.userId)
        );
      },

      /**
       * Returns which userId should the profile button link to
       */
      profileBtnTargetUserId() {
        if (this.hasAdminPrivileges && this.isViewingSomeoneElseProfile) {
          return this.$route.params.userId;
        }
        return this.userId;
      },

      /**
       * Profile/Edit Profile button should only show up when user is not global admin
       * or
       * When viewing other user's profile with admin privileges
       */
      profileBtnShowUp() {
        return (
          (this.hasAdminPrivileges &&
            this.routeName === "profile" &&
            this.userId !== this.$route.params.userId) ||
          !this.isGlobalAdmin
        );
      }
    },

    methods: {
      ...mapActions(["setLoggedIn", "logoutUser"]),
      toggleActiveNavbar: function() {
        this.isOpen = !this.isOpen;
      },
      goHome: function () {
          this.$router.push({
              name: "home"
          })
      }
    }
  };
</script>

<style scoped>

  #logo {
    margin-left: 5%;
  }

  .navbar-item {
    margin-right: 3%;
  }

</style>
