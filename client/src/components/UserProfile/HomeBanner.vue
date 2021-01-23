<template>
  <div>
    <div class="column has-text-centered profile-display">
        <figure class="profile-img image is-128x128">
          <img class="is-rounded" :src="profileImageURL" alt="banner image" />
        </figure>

        <div class="profile-text">
          <h1 class="has-text-white title">
            <a @click="goToProfile(userId)">{{ fullname }}</a>
          </h1>
        </div>
        <div class="nickname-text">
          <h2 v-if="nickname" id="nickname" class="subtitle">"{{ nickname }}"</h2>
        </div>
    </div>
  </div>
</template>

<script>
  import {mapState} from "vuex";

  export default {
    name: "HomeBanner",
    props: ["fullname", "title", "nickname", "bio", "clickable"],
    computed: {
      ...mapState(["userId"]),
      profileImageURL: function () {
        return (
          process.env.VUE_APP_SERVER_ADD +
          `/profiles/${this.userId}/photos/primary`
        );
      },
    },
    methods: {
      goToProfile: function (userId) {
        this.$router.push({
          name: "profile",
          params: {userId: userId}
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

  .img-container .profile-img {
    position: absolute;
    bottom: 30%;
    left: 50%;
    transform: translate(-50%, -50%);
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