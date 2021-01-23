<template>
  <b-taglist>
    <b-tag
      class="hashtags"
      v-for="tag in hashtags"
      @click.native="searchWithTag(tag)"
      type="is-warning"
      style="cursor: pointer"
      :key="tag"
    >#{{ tag }}</b-tag>
  </b-taglist>
</template>

<script>
  import { mapActions } from "vuex";
  export default {
    name: "HashtagDisplay",
    /* Props:
       -hashtags: the list of hashtags to be displayed.
     */
    props: ["hashtags"],

    methods: {
      ...mapActions(["setPrevActivitySearch"]),
      searchWithTag(tag) {
        this.setPrevActivitySearch({hashtag: tag});
        this.$router.push({
          name: "SearchActivities",
          query: { load: true }
        });
      }
    }
  };
</script>

<style scoped>
</style>
