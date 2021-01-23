<template>
  <div class="box">
    <article class="media">
      <figure class="media-left">
        <p class="image is-64x64">
          <img class="is-rounded" alt="post image placeholder" :src="profileImageURL">
        </p>
      </figure>
      <div class="media-content">
        <div class="content">
          <p>
            <span style="color: #555555" class="title is-6">{{ new Date(post.timestamp) }}</span> <em class="fas fa-pencil-alt"></em>
            <br>
            <span>
              {{ post.editor }} has
              {{ post.action }} the activity:
            </span>
            <a style="color: #000000" @click="goToActivity(post.activity_id)">
              <strong>{{ post.activity_name }}</strong>
            </a>
          </p>
          <hr/>
        </div>
        <nav class="level is-mobile">
          <div class="level-left"></div>
        </nav>
      </div>
      <div class="media-right"></div>
    </article>
  </div>
</template>

<script>
  export default {
    name: "Post",
    props: ["post"],
    computed: {
      profileImageURL: function() {
        return (
          process.env.VUE_APP_SERVER_ADD +
          `/profiles/${this.post.editor_id}/photos/primary?rnd=` + this.cacheKey
        );
      },
    },
    methods: {
      goToActivity: function (activityId) {
        this.$router.push({
          name: "ViewActivity",
          params: { activityId: activityId, showBackButton: false }
        });
      }
    }
  };
</script>

<style scoped>
  hr {
    border-top: 4px double #8c8b8b;
    text-align: center;
  }

  hr:after {
    content: '\002665';
    display: inline-block;
    position: relative;
    top: -15px;
    background: #f0f0f0;
    color: #8c8b8b;
    font-size: 18px;
  }

  .media {
    padding-bottom: 0;
  }
  .media-content {
    padding-bottom: 0;
  }
</style>
