<template>
  <div class="user-profile">
    <div class="hero-body">
      <b-loading :is-full-page="false" :active.sync="loading" :can-cancel="false">
        <b-icon pack="fas" icon="sync-alt" size="is-large" custom-class="fa-spin"></b-icon>
      </b-loading>
      <div class="container">
        <div class="columns is-centered">
          <div class="box column is-three-fifths profile-right profile-box">
            <UserProfileBanner
                :userId="userId"
                :fullname="userData.firstname + ' ' + userData.lastname"
                :nickname="userData.nickname"
                :bio="userData.bio"
                :clickable="false"
                title="Professional Hiker"
            />
            <!-- v-if is used to prevent rendering before data is ready -->
            <UserProfileData v-if="userData.firstname" :userInfo="userData" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<script>
  import api from "@/Api";
  import { mapState, mapActions, mapGetters } from "vuex";
  import UserProfileBanner from "../components/UserProfile/Banner";
  import UserProfileData from "../components/UserProfile/Data";
  import { ToastProgrammatic as Toast } from "buefy";

  export default {
    name: "Profile",
    props: ["userId"],
    components: {
      UserProfileBanner,
      UserProfileData
    },
    data: function() {
      return {
        userData: {
          firstname: "",
          lastname: "",
          middlename: "",
          nickname: "",
          bio: "",
          primary_email: "",
          date_of_birth: "",
          gender: "",
          fitness: -1,
          passports: [],
          location: {
            placeId: null,
            description: "",
            latitude: null,
            longitude: null,
          },
          activities: {
            userActivities: ""
          }
        },
        loading: false
      };
    },
    created() {
      this.init();
    },
    beforeDestroy() {
      this.setUserProfileViewed("");
    },
    computed: {
      ...mapState({ viewingUserId: "userId" }),
      ...mapGetters(["hasAdminPrivileges"])
    },
    methods: {
      ...mapActions("appUser", ["fetchUserData"]),
      ...mapActions(["setUserProfileViewed"]),
      init: async function() {
        this.loading = true;
        await this.getUserProfile();
        this.loading = false;
        if (this.hasAdminPrivileges) {
          if (this.userId != this.viewingUserId) {
            this.setUserProfileViewed(this.userData.firstname);
          }
        }
      },
      async getUserProfile() {
        let response;
        try {
          response = await api.getProfileById(this.userId);
          this.userData = response.data;
          this.userData.date_of_birth = new Date(this.userData.date_of_birth);

          response = await api.getUserActivitiesById(this.userId);
          this.userData.activities = response.data;
        } catch (err) {
          Toast.open({
            message: "Unable to fetch user data",
            type: "is-danger"
          });
        }
      }
    }
  };
</script>

<style scoped>
  .profile-box{
    padding-left:0;
    padding-right:0;
    padding-top:0;
  }
</style>
