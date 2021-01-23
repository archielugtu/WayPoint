<template>
  <div class="card">
    <div class="card-content">
      <div class="media">
        <div class="media-left">
          <figure class="image is-64x64">
            <img src="https://bulma.io/images/placeholders/96x96.png" class="is-rounded" alt="Placeholder image">
          </figure>
        </div>
        <div class="media-content has-text-centered">
          <span>
            <p class="title is-6 respondent-name">{{user.firstname + " " + user.lastname}} </p>
          </span>
          <span>
            <b-button
              class="button is-info is-info is-small is-fullwidth is-rounded"
              @click="openActivityOutcomeAnswer(user.id)"
            ><strong>View</strong>
            </b-button>
          </span>
        </div>
        <div class="media-right">

        </div>
      </div>
    </div>
  </div>
</template>

<script>
 import api from "@/Api"
  export default {
    name: "ActivityRespondents",
    props: ["user", "activityId"],
    methods: {
        async openActivityOutcomeAnswer(userId) {
            let response = await api.getActivitySpecificationById(this.activityId);
            if (response.data.length) {
                this.$router.push({
                    name: "activityOutcomeAnswer",
                    params: {userId: userId, activityId: this.activityId, name: this.user.firstname}
                })
            } else {
                this.$buefy.toast.open({
                    message: "Sorry there are no questions set for this activity",
                    type: "is-info",
                    duration: 3000
                });
            }
        }
    }
  }
</script>

<style scoped>
 .respondent-name {
   margin-bottom: 1em;
 }
</style>