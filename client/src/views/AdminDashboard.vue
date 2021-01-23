<template>
  <div class="columns">
    <div class="column is-2">
      <DashboardSidebar class="sidebar" />
    </div>

    <div class="column">
      <!-- caches the page to prevent recreating
      the component every time-->
      <keep-alive>
        <router-view :key="$route.path" />
      </keep-alive>
    </div>
  </div>
</template>



<script>
  export default {
    name: "AdminDashboard",
    components: {
      DashboardSidebar: () => import("@/components/Dashboard/DashboardSidebar")
    },
    data: function() {
      return {
        menu: ""
      };
    }
  };
</script>

<style scoped>
  .sidebar {
    margin-top: 5vh;
    border-right: 0.75px solid rgba(0, 0, 0, .1);
  }
</style>
