<template>
  <section class="hero">
    <div class="hero-body">
      <div class="container">
        <div class="columns">
          <div class="column is-offset-1 is-10">
            <h1 class="title is-3">Edit Profile</h1>
            <!-- Tabs start -->
            <div class="tabs is-toggle is-toggle is-fullwidth">
              <ul>
                <li
                  v-for="(tab, index) in tabs"
                  :key="index"
                  :class="{'is-active': activeTab === index}"
                  @click="selectTab(index)"
                >
                  <a>
                    <span class="icon is-small">
                      <i :class="tab.icon" aria-hidden="true"></i>
                    </span>
                    <span>{{ tab.name }}</span>
                  </a>
                </li>
              </ul>
            </div>
            <!-- Tabs stop -->

            <keep-alive>
              <component
                @click="saveUserProfile"
                :userData="userData"
                :userId="userId"
                v-bind:is="currentTabComponent"
              ></component>
            </keep-alive>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
  import api from "@/Api";
  import dateHelper from "@/utils/dates/dates";
  import { ToastProgrammatic as Toast } from "buefy";

  export default {
    name: "EditProfile",
    components: {
      EditProfileGeneral: () => import("../components/UserProfileEdit/General"),
      EditProfileEmail: () => import("../components/UserProfileEdit/Email"),
      EditProfilePassports: () =>
        import("../components/UserProfileEdit/Passports"),
      EditProfileLocation: () => import("../components/UserProfileEdit/Location"),
      EditUserActivities: () =>
        import("../components/UserProfileEdit/ActivityTypes"),
      EditUserAccountManagement: () =>
        import("../components/UserProfileEdit/AccountManagement"),
      EditUserProfilePicture: () =>
        import("../components/ImageInput.vue")
    },
    props: ["userId"],
    data: function() {
      return {
        hasErrors: false,
        activeTab: 0,
        userData: {},
        eTagHeader: null,
        tabs: [
          {
            name: "General",
            icon: "far fa-user",
            componentName: "EditProfileGeneral"
          },
          {
              name: "Photo",
              icon: "fas fa-camera",
              componentName: "EditUserProfilePicture"
          },
          {
            name: "Passport",
            icon: "fas fa-passport",
            componentName: "EditProfilePassports"
          },
          {
            name: "Emails",
            icon: "far fa-envelope",
            componentName: "EditProfileEmail"
          },
          {
            name: "Location",
            icon: "fas fa-map",
            componentName: "EditProfileLocation"
          },
          {
            name: "Activity Types",
            icon: "fas fa-hiking",
            componentName: "EditUserActivities"
          },
          {
            name: "Account",
            icon: "fas fa-hdd",
            componentName: "EditUserAccountManagement"
          }
        ]
      };
    },
    computed: {
      currentTabComponent() {
        return this.tabs[this.activeTab].componentName;
      }
    },
    mounted() {
      this.init();
    },
    methods: {
      init() {
        this.getUserProfileData();
      },
      selectTab: function(tabIndex) {
        this.activeTab = tabIndex;
      },
      async getUserProfileData() {
        let response;
        try {
          response = await api.getProfileById(this.$route.params.userId);
          this.userData = response.data;
          this.userData.date_of_birth = new Date(this.userData.date_of_birth);
          // ETag header from backend that specifies the version of User
          // This will be sent back to backend whenenever we update the user's data
          this.eTagHeader = response.headers["etag"].slice(1, -1);

          response = await api.getUserActivitiesById(this.$route.params.userId);
          this.userData.activities = response.data;
        } catch (err) {
          Toast.open({
            message: "An error has occurred, unable to fetch user data",
            type: "is-danger"
          });
        }
      },
      dateFormatter: dateHelper.dateFormatterToYYYYMMDD,
      async saveUserProfile() {
        // Deep clone the userData to avoid butchering existing data
        let payload = JSON.parse(JSON.stringify(this.userData));
        payload.primary_email = payload.primary_email.address;
        payload.additional_email = payload.additional_email.map(e => e.address);
        payload.passports = payload.passports.map(p => p.name);
        payload.date_of_birth = this.dateFormatter(this.userData.date_of_birth);

        // Add userdata.location info from the Location tab
        if (this.userData.location.placeId !== null) {
          payload.location = this.userData.location;
        }

        try {
          let response = await api.updateUserProfile(
            this.$route.params.userId,
            payload,
            this.eTagHeader
          );
          // Update to new etag
          this.eTagHeader = response.headers["etag"];
          this.$buefy.toast.open({
            duration: 2000,
            message: "Profile updated!",
            type: "is-success"
          });
          await this.getUserProfileData();
        } catch (err) {
          if (err.response.status === 404) {
            alert("ERROR404: User Not Found");
          } else if (err.response.status === 400) {
            alert("ERROR400: Bad Request");
          } else if (err.response.status === 500) {
            alert("ERROR500: Internal Server Error");
          }
        }
      }
    }
  };
</script>

<style scoped>
</style>
