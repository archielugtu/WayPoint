<template>
  <div>
    <div class="column has-text-centered profile-display">
      <div class="img-container">
        <figure>
          <img class="banner-img" :src="bannerImageURL" />
        </figure>

        <figure class="profile-img image is-128x128">
          <img class="is-rounded" :src="profileImageURL" alt="banner image" />
        </figure>

        <div class="profile-text">
          <h1 v-if="clickable" class="has-text-white title">
            <a @click="goToProfile(userId)">{{ fullname }}</a>
          </h1>
          <h1 v-else id="fullname" class="has-text-white title">{{ fullname }}</h1>
        </div>
        <div class="nickname-text">
          <h2 v-if="nickname" id="nickname" class="has-text-white subtitle">"{{ nickname }}"</h2>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import { mapState } from "vuex";

  export default {
    name: "UserProfileBanner",
    props: ["fullname", "userId", "title", "nickname", "bio", "clickable"],
    data() {
      return {
        profileImg: require("@/assets/static/fabian.jpg"),
        cacheKey: new Date().getTime()
      };
    },

    computed: {
      // Use state data stored in activities.js Vuex store
      ...mapState("activities", ["continuousActivities", "durationActivities"]),
      ...mapState(["currentUserProfileViewed"]),
      profileImageURL: function() {
        return (
          process.env.VUE_APP_SERVER_ADD +
          `/profiles/${this.userId}/photos/primary?rnd=` + this.cacheKey
        );
      },

      bannerImageURL: function() {
        return (
          process.env.VUE_APP_SERVER_ADD +
          `/profiles/${this.userId}/photos/cover?rnd=` + this.cacheKey
        );
      }
    },

    created() {
      this.interval = setInterval(() => {
        this.cacheKey = new Date();
        clearInterval(this.interval);
      }, 1000);
    },

    methods: {
      goToProfile: function(userId) {
        this.$router.push({
          name: "profile",
          params: { userId: userId }
        });
      },
    }
  };
</script>

<style scoped>
  .profile-img {
    display: block;
    margin: auto;
  }

  .img-container {
    position: relative;
    text-align: center;
  }

  .img-container .profile-img {
    position: absolute;
    bottom: 30%;
    left: 50%;
    transform: translate(-50%, -50%);
  }

  .img-container .profile-text {
    position: absolute;
    bottom: 33%;
    left: 50%;
    transform: translate(-50%, -50%);
  }

  .img-container .nickname-text {
    position: absolute;

    bottom: 27%;
    left: 50%;
    transform: translate(-50%, -50%);
  }

  .banner-img {
    filter: brightness(60%);
  }

  a {
    color: #000000;
  }

  .profile-img img {
    border: 2px solid white;
    border-radius: 50%;
    -moz-border-radius: 50%;
    -webkit-border-radius: 50%;
  }

  .profile-display {
    padding-left:0;
    padding-right:0;
    padding-top:0;
  }
</style>

