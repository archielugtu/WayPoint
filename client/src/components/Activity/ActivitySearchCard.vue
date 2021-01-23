<template>
  <div class="activity-card card" style="cursor: pointer">
    <div class="card-content">
      <div class="img-container">
        <figure class="image is-16by9">
          <img :src="heroImageUrl + '?rnd=' + cacheKey" />
        </figure>
        <div id="activity-title" class="middle-left has-text-white title">{{ activity.activity_name }}</div>
        <p class="has-text-white bottom-left location">{{addressDescription}}</p>
        <p class="has-text-white activity-description">{{ activity.description }}</p>
      </div>
    </div>
  </div>
</template>

<script>
  import {mapGetters} from "vuex";
  import api from "@/Api";
  import googleMaps from "@/utils/geolocation/googleMaps-api";

  export default {
    name: "ActivitySearchCard",
    props: ["activityId"],

    data() {
      return {
        activity: {},
        cacheKey: new Date().getTime(),
        activityLocation: {description: ""}
      };
    },

    async mounted() {
      let response = await api.getActivityById(this.activityId);
      if (response && response.data !== null) {
        this.activity = response.data;
        if (this?.activity?.location?.placeId){
          googleMaps.getInfoFromPlaceId(this.activity.location.placeId, this.setLocation)
        }    
        if (this.activity.description.length >= 100) {
          this.activity.description = this.activity.description.slice(0, 100) + "..."
        }
      }
    },

    computed: {
      ...mapGetters("activities", ["getActivityById"]),
      addressDescription() {
        return this.activityLocation?.formatted_address ?  this.activityLocation.formatted_address : "Location not specified"
      },
      heroImageUrl() {
        let url = "";
        url += process.env.VUE_APP_SERVER_ADD;
        url += "/activities/" + this.activityId + "/photos/primary";
        return url;
      },
    },

    methods: {
      setLocation(activityLocation) {
        this.activityLocation = activityLocation
      },
    }
  }
</script>

<style scoped>

  .img-container {
    position: relative;
    text-align: left;
  }

  .img-container img {
    filter: brightness(0.8);
  }

  .img-container .middle-left {
    position: absolute;
    bottom: 5rem;
    left: 1rem;
  }

  .img-container .activity-description {
    position: absolute;
    bottom: 1rem;
    left: 1rem;
  }

  .img-container .bottom-left {
    position: absolute;
    bottom: 4rem;
    left: 1rem;
  }
  .card-content {
    padding: 0.5rem 0.5rem 0.5rem 0.5rem;
  }

</style>
