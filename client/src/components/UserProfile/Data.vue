<template>
  <b-tabs type="is-boxed" class="tabs-section" position="is-centered">
    <b-tab-item id="tab-general">
      <template slot="header">
        <b-icon icon="user"></b-icon>
        <span>General</span>
      </template>
      <template slot="default">
        <UserProfileBasicInfo :userInfo="userInfo" />
      </template>
    </b-tab-item>

    <b-tab-item  id="tab-activities" label="Activities">
      <template slot="header">
        <b-icon icon="hiking"></b-icon>
        <span>Activities</span>
      </template>
      <template slot="default">
        <UserActivities :userInfo="userInfo" />
      </template>
    </b-tab-item>

  </b-tabs>
</template>


<script>
  export default {
    name: "UserProfileData",
    props: ["userInfo"],
    components: {
      UserProfileBasicInfo: () => import("./BasicInfo.vue"),
      UserActivities: () => import("@/components/UserActivities")
    }
  };
</script>


<style scoped>
  .tabs-section{
    margin: 16px;
  }
</style>

