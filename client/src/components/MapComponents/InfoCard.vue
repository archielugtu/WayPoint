<template>
  <div >
    <div class="media activity-list" @click="emitActivity(item)" v-for="(item, index) in items" :key="index">
      <div class="media-left">
        <img class="square-cover" :src="heroImageUrl(item.id)" alt="Placeholder image" />
      </div>
      <div class="media-content">
        <p class="title is-6">{{ item.name }}</p>
        <p>{{trimmedDescription(item.description)}}</p>
      </div>
    </div>
  </div>
</template>

<script>
/**
 * Handles displaying inside info window when a marker is clicked
 */
export default {
  name: "InfoCard",
  props: {
    items: { type: Array, required: true },
    type: { type: String, required: true }
  },
  methods: {
    heroImageUrl(activityId) {
      let url = "";
      url += process.env.VUE_APP_SERVER_ADD;
      url += "/activities/" + activityId + "/photos/primary";
      return url;
    },
    trimmedDescription(description) {
      const maxLength = 65;
      if (!description) {
        return "No description";
      }
      if (description.length <= maxLength) {
        return description;
      } else {
        return description.substring(0, maxLength) + "...";
      }
    },
    emitActivity(item){
      this.$emit("selectActivity", item)
    }
  }
};
</script>

<style scoped>
.card-header-title {
  font-size: 1.5rem;
}

.square-cover {
  object-fit: cover;
  height: 96px;
}

.activity-list {
  width: 350px;
}

.title {
  margin-bottom: 4px;
}
</style>
