<template>
  <div class="users">
    <b-tooltip v-for="(user, index) in users" :key="user.id" :label="user.name">
      <b-icon v-if="index < maxItems" icon="user" size="is-small"></b-icon>
    </b-tooltip>
    <div v-if="totalUsers > maxItems">... </div>
  </div>
</template>

<script>
  export default {
    name: "SingleCounter",
    props: ["users", "totalUsers"],
    data: function() {
      return {
        maxItems: 50
      };
    },
    created() {},
    methods: {}
  };
</script>

<style scoped>
  .users {
    display: inline-block;
  }
</style>
