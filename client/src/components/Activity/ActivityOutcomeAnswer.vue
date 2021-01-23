<template>
  <section class="hero">
    <div class="hero-body">
      <b-loading :is-full-page="false" :active.sync="loading" :can-cancel="false">
        <b-icon pack="fas" icon="sync-alt" size="is-large" custom-class="fa-spin"></b-icon>
      </b-loading>
      <div class="container has-text-centered">
        <div class="title" v-if="myUserId === userId">
          Please enter your results
        </div>
        <div class="title" v-else>
          Viewing {{ firstName }}'s Results
        </div>
      </div>
      <div class="container">
        <PossibleResult
          :triggerResultValidation="triggerResultValidation"
          :viewType="'answer'"
          :viewingSelfAnswer="userId === myUserId"
          :userViewingAnswer="true"
          :userId="userId"
          :activityId="activityId"
          @qCheck="isQuestionsValid = $event"
          @checkValid="areQuestionsValid = $event"
          @loading="loading = $event"
          v-model="outcomes"
        />
      </div>
    </div>
  </section>
</template>

<script>
  import PossibleResult from "../ResultComponents/PossibleResult";
  import {mapState} from "vuex"
  import api from "@/Api"
  export default {
    name: "ActivityOutcomeAnswer.vue",
    components: { PossibleResult },
    props: ["activityId", "userId", "name"],
    data() {
      return {
        outcomes: null,
        triggerResultValidation: false,
        loading: true,
        firstName: ""
      };
    },
    computed: {
      ...mapState({ myUserId: "userId" }),
    },
    mounted() {
      this.getUserFirstname();
    },
    methods: {
      getUserFirstname: async function () {
        let result;
        result = await api.getProfileById(this.userId);
        this.firstName = result.data.firstname

      }
    }
  };
</script>

<style scoped>
  .container {
    width: 60%;
  }
</style>
