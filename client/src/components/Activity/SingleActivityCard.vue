<template>
  <div class="activity-card card" style="cursor: pointer">
      <div class="img-container">
        <figure class="image is-16by9">
          <img class="activity-img" :src="heroImageUrl + '?rnd=' + cacheKey" @dblclick="clickActivity"/>
        </figure>
        <div id="activity-title" class="middle-left has-text-white title">{{ getTrimmedActivityName() }}</div>
        <p class="has-text-white middle-left">{{getTrimmedActivityLocation()}}</p>
        <p v-bind:class="{
            'has-text-white activity-description-high': !activity.continuous,
            'has-text-white activity-description-low': activity.continuous
        }">
          {{ getTrimmedActivityDescription() }}
        </p>

        <div class="time continuous has-text-white" v-if="!activity.continuous">
          <p class="has-text-white">
            Start:
            {{ startDate }}
          </p>
          <p>
            End:
            {{ endDate }}
          </p>
        </div>

        <div class="activityType">
          <b-taglist id="activity-types">
            <b-tag type="is-dark" v-for="types in activity.activityType" :key="types">{{ types }}</b-tag>
          </b-taglist>
        </div>

        <div class="hashTag">
          <hashtag-display :hashtags="activity.hashtags" />
        </div>
      </div>
  </div>
</template>


<script>
  import blobHelper from "@/utils/converter/blob";
  import { mapGetters, mapState, mapActions } from "vuex";
  import dateHelper from "@/utils/dates/dates";
  import HashtagDisplay from "../HashtagDisplay";
  import googleMaps from "@/utils/geolocation/googleMaps-api";

  export default {
    name: "SingleActivityCard",
    components: { HashtagDisplay },
    props: ["activityId"],

    data() {
      return {
        activity: {},
        activityLocationName: "",
        startDate: null,
        endDate: null,
        cacheKey: new Date().getTime(),
      };
    },

    mounted() {
      this.init()
    },

    methods: {
      ...mapActions("activities", ["removeActivity"]),
      base64toBlob: blobHelper.base64toBlob,
      dateFormatter: dateHelper.dateFormatterToEnglish,

      init: async function () {
        this.activity = await this.getActivityById(this.activityId);
        this.activity.hashtags.sort();
        this.startDate = this.dateFormatter(new Date(this.activity.startTime));
        this.endDate = this.dateFormatter(new Date(this.activity.endTime));
        if (this.activity.location && this.activity.location.placeId) {
          googleMaps.getInfoFromPlaceId(this.activity.location.placeId, this.setLocationName)
        } else {
          this.activityLocationName = "Unknown location"
        }
        if (this.activity !== null && this.activity.description.length >= 100) {
          this.activity.description = this.activity.description.slice(0, 100) + "..."
        }
      },
      getTrimmedActivityDescription() {
        if (this.activity !== null && "description" in this.activity &&this.activity.description.length >= 100) {
          this.activity.description = this.activity.description.slice(0, 100) + "..."
        }
        return this.activity.description
      },

      getTrimmedActivityName() {
        if (this.activity !== null && "activityName" in this.activity &&this.activity.activityName.length >= 35) {
          this.activity.activityName = this.activity.activityName.slice(0, 35) + "..."
        }
        return this.activity.activityName
      },

      getTrimmedActivityLocation() {
        if (this.activityLocationName !== "" && "Location" in this.activity && this.activityLocationName >= 35) {
          this.activityLocationName = this.activityLocationName.slice(0, 35) + "..."
        }
        return this.activityLocationName
      },

      setLocationName: function (out) {
        if (out) {
          this.activityLocationName = out.formatted_address;
        }
      },

      editActivity() {
        this.$router.push({
          name: "editActivity",
          params: {
            userId: this.$route.params.userId,
            activityId: this.activityId
          }
        });
      },

      deleteActivity() {
        this.$buefy.dialog.confirm({
          title: "Deleting activity",
          message:
            "Are you sure you want to <b>delete</b> this activity? This action cannot be undone.",
          confirmText: "Delete Activity",
          type: "is-danger",
          hasIcon: true,
          onConfirm: () =>
            this.removeActivity({
              userId: this.$route.params.userId,
              activityId: this.activityId
            })
        });
      },

      clickActivity() {
        this.$router.push({
          name: "ViewActivity",
          params: {
            activityId: this.activityId,
            showBackButton: false
          }
        })
      },
    },

    computed: {
      ...mapGetters("activities", ["getActivityById"]),
      ...mapState(["userId", "currentUserProfileViewed", "isGlobalAdmin"]),

      heroImageUrl() {
        let url = "";
        url += process.env.VUE_APP_SERVER_ADD;
        url += "/activities/" + this.activityId + "/photos/primary";
        return url;
      },
    }
  };
</script>

<style scoped>
  .time {
    margin-top: 0.5rem;
  }

  .activity-card {
    margin-bottom: 1.5rem;
  }

  #activity-types {
    margin-top: 0.6rem;
  }

  .img-container {
    position: relative;
    text-align: left;
  }

  .img-container img {
    filter: brightness(80%);
  }

  /* Centered text */
  .img-container .middle-left {
    position: absolute;
    bottom: 2rem;
    left: 2rem;
  }

  .img-container .middle-left {
    position: absolute;
    bottom: 11rem;
    left: 2rem;
  }

  .img-container .continuous {
    position: absolute;
    bottom: 5rem;
    left: 2rem;
  }

  .img-container .hashTag {
    position: absolute;
    bottom: 1rem;
    left: 2rem;
  }

  .img-container .activityType {
    position: absolute;
    bottom: 3rem;
    left: 2rem;
  }

  .img-container .bottom-left {
    position: absolute;
    bottom: 8rem;
    left: 2rem;
  }

  .img-container .activity-description-low {
    position: absolute;
    bottom: 8rem;
    left: 2rem;
  }

  .img-container .activity-description-high {
    position: absolute;
    bottom: 8.5rem;
    left: 2rem;
  }
</style>

