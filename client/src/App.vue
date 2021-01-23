<template>
  <div id="app">
    <Navbar />
    <keep-alive include="AdminDashboard">
      <router-view :key="$route.fullPath" />
    </keep-alive>
  </div>
</template>

<script>
  import Navbar from "./components/Navbar.vue";
  import { mapActions, mapState } from "vuex";


  // app Vue instance
  const app = {
    name: "app",
    components: {
      Navbar
    },
    methods: {
      ...mapActions(['checkAuth']),
      ...mapActions("appUser", ["fetchUserData"])
    },
    computed: {
      ...mapState({ userId: "userId", isGlobalAdmin: "isGlobalAdmin"}),
    },
    async mounted() {
      await this.checkAuth()
      if (!this.isGlobalAdmin) {
        await this.fetchUserData(this.userId)
      }
    },
  };

  export default app;
</script>

<style>
</style>
