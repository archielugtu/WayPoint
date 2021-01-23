<template>
  <div>
    <div class="hero-body">
      <div class="container">
        <div class="columns">
          <div class="column is-one-quarter">
            <div class="column">
              <div class="profile-banner">
                <UserProfileBanner
                  :fullname="userData.firstname + ' ' + userData.lastname"
                  :nickname="userData.nickname"
                  :bio="userData.bio"
                  :clickable="true"
                  title="Professional Hiker"
                />
              </div>
            </div>
          </div>
          <div class="column is-three-fifths">
            <div class="column">
              <div class="box">
                <div class="title is-4 has-text-centered">
                  Your Home Feed
                </div>
              </div>
            </div>
            <div class="column" v-if="activityFeed.length === 0">
              <div class="box">
                <section class="hero has-text-centered">
                  <div class="hero-body">
                    <div class="container">
                      <b-loading :is-full-page="false" :active.sync="loading" :can-cancel="false">
                        <b-icon pack="fas" icon="sync-alt" size="is-large" custom-class="fa-spin"></b-icon>
                      </b-loading>
                      <img style="width: 40%; height: 40%" src="@/assets/hiker.gif" alt="No image found"/><br>
                      <strong class="title is-4">No Activities Found</strong><br><br>
                      <div class="subtitle">Follow activities and get their latest updates</div>
                      <b-button class="button is-success is-rounded" @click="() => {this.$router.push({name: 'SearchActivities'})}">
                        <strong>Find Activities</strong>
                      </b-button>
                    </div>
                  </div>
                  <div class="hero-footer">
                  </div>
                </section>
              </div>
            </div>
            <div class="column" v-else v-for="(feed, index) in activityFeed" :key="index">
              <Post :post="feed" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import api from "@/Api";
  import UserProfileBanner from "../components/UserProfile/HomeBanner";
  import Post from "@/components/HomeFeed/Post";
  import {ToastProgrammatic as Toast} from "buefy";

  export default {
    name: "HomeFeed",
    props: ["userId"],
    components: {
      UserProfileBanner,
      Post
    },
    data: function() {
      return {
        userData: {
          firstname: "",
          lastname: "",
          bio: ""
        },
        activityFeed: [],
        currentPage: 0,
        loading: false,
      };
    },
    created() {
      this.fetchUser();
      this.fetchHistory();
      this.scroll();
    },
    destroyed() {
      window.onscroll = () => {};
    },
    methods: {
      scroll: function() {
        window.onscroll = () => {
          let bottomOfWindow =
            document.documentElement.scrollTop + window.innerHeight ===
            document.documentElement.offsetHeight;

          if (bottomOfWindow) {
            // we have reached the bottom of window
            // retrieve next page of data from backend
            this.currentPage++;
            this.fetchHistory();
          }
        };
      },
      fetchUser: async function() {
        let response;
        try {
          response = await api.getProfileById(this.userId);
          this.userData = response.data;
        } catch (err) {
          Toast.open({
            message: "An error has occurred, unable to fetch user data",
            type: "is-danger"
          });
        }
      },
      fetchHistory: async function() {
        let response;
        try {
          this.loading = true;
          const params = { page: this.currentPage };
          response = await api.getUserHomeFeed(this.userId, params);
          if (!response.data.activity_history.length) {
            this.currentPage--;
          }
          response.data.activity_history.forEach(e => this.activityFeed.push(e));
          this.loading = false;
        } catch (err) {
          Toast.open({
            message: "Unable to fetch history",
            type: "is-danger"
          });
        }
      },
      goToActivity: function () {
          this.$router.push({name: 'SearchActivities'})
      }
    }
  };
</script>

<style scoped>

</style>

