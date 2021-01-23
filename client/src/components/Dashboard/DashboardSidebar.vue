<template>
  <div class="sidebar">
    <img id="dashboard-sidebar-img" class="filtered" :src="require(`@/assets/dashboard.svg`)" alt="dashboard image" />
    <p class="heading has-text-centered">Actions</p>
    <hr />
    <div class="buttons">
      <b-button
        icon-left="users"
        tag="router-link"
        :to="{name: 'dashboardSearch'}"
        :type="isHighlight('dashboardSearch')"
        expanded
      >Users</b-button>
      <b-button
        tag="router-link"
        :to="{name: 'dashboardActivities'}"
        :type="isHighlight('dashboardActivities')"
        icon-left="hiking"
        expanded
      >Activities</b-button>
    </div>
  </div>
</template>


<script>
  export default {
    name: "DashboardSidebar",
    methods: {
      isHighlight(pageName) {
        return this.$route.name === pageName ? "is-dark" : "is-white";
      }
    }
  };
</script>

<style scoped>
  .sidebar {
    padding: 1rem;
  }

  #dashboard-sidebar-img {
    height: 120px;
    display: block;
    margin: auto;
    margin-bottom: 2rem;
  }

  .filtered {
    -webkit-filter: grayscale(50%);
    filter: grayscale(50%);
  }
</style>
