<template>
  <div class="container activities">
    <div class="hero-body">
      <b-button
        @click="backToSearch"
        class="has-text-dark"
        icon-left="arrow-left"
        id="back-btn"
        outlined
        type="is-light"
        v-if="showBackButton"
      >Back to search
      </b-button>

      <div class="columns">
        <div class="column box is-8">
          <div class="img-container">
            <div>
              <figure class="image is-16by9">
                <img :src="heroImageUrl + '?rnd=' + cacheKey"/>
              </figure>
            </div>
            <div class="middle-left has-text-white title" id="activity-title">{{ activityName }}</div>
            <p class="has-text-white bottom-left location">{{ locationName }}</p>
          </div>
          <b-loading :active.sync="loading" :can-cancel="false" :is-full-page="false">
            <b-icon custom-class="fa-spin" icon="sync-alt" pack="fas" size="is-large"></b-icon>
          </b-loading>

          <nav class="activity-info info-text level">
            <div class="level-left">
              <div class="level-item">
                <div v-if="isShowingParticipatingButton">
                  <b-button
                    :icon-right="isParticipating ? 'check' : '' "
                    :loading="isParticipatingLoading"
                    :type="isParticipating ? 'is-success' : 'is-info'"
                    @click="toggleParticipating()"
                    class="button-spacer"
                  >
                    <strong>{{ this.isParticipating ? 'Participating' : 'Click to participate' }}</strong>
                  </b-button>
                </div>
                <div v-if="isShowingFollowButton">
                  <b-button
                    :icon-right="isFollowing ? 'check' : '' "
                    :loading="isFollowingLoading"
                    :type="isFollowing ? 'is-success' : 'is-primary'"
                    @click="toggleFollowing()"
                  >
                    <strong>{{ this.isFollowing ? 'Following' : 'Click to follow' }}</strong>
                  </b-button>
                  <br/>
                </div>
              </div>
              <div class="level-item">
                <div class="has-text-centered">{{ statusMessage }}</div>
              </div>
            </div>
            <div class="level-right">
              <div
                class="buttons"
                v-if="this.userActivityRelation === 'creator' || this.userActivityRelation === 'organiser' || this.isAdmin"
              >
                <b-button @click="editActivity" type="is-warning">Edit</b-button>
                <b-button
                  @click="deleteActivity"
                  type="is-light"
                  v-if="this.userActivityRelation === 'creator' || this.isAdmin"
                >Delete
                </b-button>
              </div>
            </div>
          </nav>

          <b-tabs class="activity-info block" position="is-centered">

            <b-tab-item id="tab-activities" label="Activities">
              <template slot="header">
                <b-icon icon="hiking"></b-icon>
                <span>Activity Info</span>
              </template>
              <template slot="default">
                <div class="columns">
                  <div class="column">

                    <div class="spacer time" v-if="description">
                      <p class="heading">
                          <span class="icon has-text-black">
                            <em class="fas fa-align-left"></em>
                          </span>
                        Description
                      <p>{{ description }}</p>
                    </div>

                    <div class="spacer time" v-if="!continuous">
                      <div v-if="startDate">
                        <p class="heading">
                          <span class="icon has-text-black">
                            <em class="far fa-calendar-alt"></em>
                          </span>
                          Date
                        </p>Start:
                        <strong>{{ startDate }}</strong>
                        End:
                        <strong>{{ endDate }}</strong>
                      </div>
                    </div>
                    <div class="spacer" v-else>
                      <p class="heading">
                        <span class="icon has-text-black">
                          <em class="far fa-calendar-alt"></em>
                        </span>
                        Date
                      </p>
                      <strong>It's always on!</strong>
                    </div>
                    <div class="spacer" v-if="hashtags.length">
                      <p class="heading">
                        <span class="icon has-text-black">
                          <em class="fab fa-slack-hash"></em>
                        </span>
                        Tags
                      </p>
                      <hashtag-display :hashtags="hashtags"/>
                    </div>
                    <div class="spacer" v-if="activityTypes.length">
                      <p class="heading">
                      <span class="icon has-text-black">
                        <i class="fas fa-hiking"></i>
                      </span>
                        Activity Types
                      </p>
                      <b-taglist id="activity-types">
                        <b-tag :key="types" type="is-info" v-for="types in activityTypes">{{ types }}</b-tag>
                      </b-taglist>
                    </div>
                    <div class="spacer">
                      <p class="heading">
                        <span class="icon has-text-black">
                          <em class="fab fa-slack-hash"></em>
                        </span>
                        Activity Visibility
                      </p>
                      <strong>{{displayActivityVisibility(visibility)}}</strong>
                    </div>
                  </div>

                </div>
              </template>
            </b-tab-item>

            <b-tab-item id="tab-location">
              <template slot="header">
                <b-icon icon="map"></b-icon>
                <span>Location</span>
              </template>
              <template slot="default">
                <div class="spacer time">
                  <p class="heading">
                    <span class="icon has-text-black">
                      <em class="fas fa-map"></em>
                    </span>
                    Location
                  <p>
                    <strong>{{ locationName }}</strong></p>
                </div>
                <div class="spacer time">
                  <p class="heading">
                    <span class="icon has-text-black">
                      <em class="fas fa-align-left"></em>
                    </span>
                    Detailed description:
                  </p>
                  <p>
                    <span v-if="locationDescription">
                      <em>{{locationDescription}}</em>
                    </span>
                    <span v-else>
                      <em>Not set</em>
                    </span>
                  </p>
                </div>
                <MapPane
                  v-bind:height="40"
                  v-bind:markers.sync="markers"
                  v-bind:view-mode="true"
                />
              </template>
            </b-tab-item>


            <b-tab-item id="tab-general">
              <template slot="header">
                <b-icon icon="camera-retro"></b-icon>
                <span>Photos</span>
              </template>
              <template slot="default">
                <ImagePreview :activityId="activityId" :userRole="userActivityRelation"/>
              </template>
            </b-tab-item>

            <b-tab-item id="tab-results">
              <div class="field is-grouped is-grouped-right">
                <b-button :icon-left="previouslyAnswered ? 'pencil-alt' : 'plus-square'" @click="openActivityOutcomeAnswer(userId)" class=" is-success"
                          v-if="isParticipating">
                  <strong v-if="!previouslyAnswered">Add results</strong>
                  <strong v-else>Edit your results</strong>
                </b-button>
              </div>
              <template slot="header">
                <b-icon icon="align-left"></b-icon>
                <span>Results</span>
              </template>
              <template slot="default">
                <div class="activity-info activity-participant-results">
                  <div class="has-text-centered" v-if="!respondents.length">
                    <div class="container">
                      <img alt="No image found"
                           src="@/assets/backpack_hiker.png"
                           style="width: 20%; height: 20%"/>
                      <div class="is-6">
                        No recorded results from participants so far
                        <em class="far fa-sad-tear"></em>
                      </div>
                    </div>
                  </div>
                  <div
                    :key="index"
                    class="columns"
                    v-for="(row, index) of Array(numRespondentsRow).keys()"
                  >
                    <div
                      :key="colIndex"
                      class="column"
                      v-for="(col, colIndex) of Array(respondentsPerRow).keys()"
                    >
                      <ActivityRespondents
                        :activityId="activityId"
                        :user="respondents[row * respondentsPerRow + col]"
                        v-if="respondents[row * respondentsPerRow + col]"
                      />
                    </div>
                  </div>
                </div>
                <div v-if="this.creatorId === this.userId || this.isAdmin">
                  <hr/>
                  <div v-if="outcomes.length !== 0">
                    <div class="label">Activity Outcomes</div>
                  </div>
                  <PossibleResult
                    :activityId="activityId"
                    :userViewingAnswer="false"
                    :viewType="'read'"
                    @qCheck="isQuestionsValid = $event"
                    v-model="outcomes"
                  />
                </div>
              </template>
            </b-tab-item>
          </b-tabs>
        </div>

        <div class="column">
          <ActivityUsersCounter
            :activityId="activityId"
            :creatorId="creatorId"
            :refreshUserCount="refreshUserCount"
            :userId="this.userId"
            ref="activityUserCounter"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import api from "@/Api";
  import dateHelper from "@/utils/dates/dates";
  import PossibleResult from "../components/ResultComponents/PossibleResult";
  import {mapActions, mapState} from "vuex";
  import HashtagDisplay from "../components/HashtagDisplay";
  import ActivityUsersCounter from "../components/Activity/ActivityUsersCounter";
  import ActivityRespondents from "../components/Activity/ActivityRespondents";
  import ImagePreview from "../components/Activity/ImagePreview";
  import googleMaps from "@/utils/geolocation/googleMaps-api";
  import MapPane from "@/components/MapComponents/MapPane";

  export default {
    name: "ViewActivity",
    components: {
      MapPane,
      ImagePreview,
      HashtagDisplay,
      PossibleResult,
      ActivityUsersCounter,
      ActivityRespondents
    },
    props: ["activityId", "showBackButton"],
    data() {
      return {
        outcomes: [],
        activityName: "",
        locationName: "Unknown location",
        isFollowingLoading: true,
        isParticipatingLoading: true,
        userActivityRelation: "loading", // Box will stay hidden until is loaded
        location: "",
        description: "",
        locationDescription: "",
        continuous: false,
        startDate: null,
        endDate: null,
        hashtags: [],
        activityTypes: [],
        creatorId: null,
        loading: false,
        visibility: "public",
        refreshUserCount: false,
        previouslyAnswered: false,
        markers: [],
        respondents: [],
        respondentsPerRow: 3,
        numRespondentsRow: 0,

        // Random number that will be updated periodically,
        // used for updating hero image automatically
        cacheKey: new Date().getTime(),
        interval: null,
        marker: [],
        locationPlaceId: -1
      };
    },

    async created() {
      this.loading = true;
      this.interval = setInterval(() => {
        this.cacheKey = new Date();
        clearInterval(this.interval);
      }, 1 * 1000);
      try {

        let result = await api.getActivityUserRelation(this.activityId);
        this.userActivityRelation = result.data.role;
        this.isFollowingLoading = false;
        this.isParticipatingLoading = false;


        result = await api.getActivityById(this.activityId);


        this.activityName = result.data.activity_name;
        this.location = result.data.location;
        this.description = result.data.description;
        this.continuous = result.data.continuous;
        this.startDate = this.dateFormatterToEnglish(
          new Date(result.data.start_time)
        );
        this.visibility = result.data.visibility;
        this.endDate = this.dateFormatterToEnglish(
          new Date(result.data.end_time)
        );
        this.hashtags = result.data.hashtags.sort();
        this.activityTypes = result.data.activity_type;
        this.creatorId = result.data.creator_id;
        this.visibility = result.data.visibility;
        this.locationDescription = result.data.location.description;
        this.locationPlaceId = result.data.location.placeId;

        this.markers.push({
          id: result.data.activityId,
          latitude: result.data.location.latitude,
          longitude: result.data.location.longitude,
          name: result.data.activity_name,
          imageType: "activity",
          recenter: true

        });


        if (this.locationPlaceId == null) {
          this.locationName = "Unknown location"
        } else {
          googleMaps.getInfoFromPlaceId(
            result.data.location.placeId,
            this.fillLocationName
          );
        }



      } catch (err) {
        if ("response" in err && err.response.status === 403) {
          this.$router.push({name: "activityError"});
        }
      } finally {
        this.loading = false;
      }

    },
    computed: {
      ...mapState([
        "userId",
        "isAdmin",
        "previousHashtagSearch",
        "isGlobalAdmin"
      ]),

      heroImageUrl() {
        let url = "";
        url += process.env.VUE_APP_SERVER_ADD;
        url += "/activities/" + this.activityId + "/photos/primary";
        return url;
      },

      isFollowing() {
        return this.userActivityRelation === "follower";
      },

      isParticipating() {
        return this.userActivityRelation === "participant";
      },

      isShowingFollowButton() {
        return ["none", "follower"].includes(this.userActivityRelation);
      },

      isShowingParticipatingButton() {
        return ["none", "participant"].includes(this.userActivityRelation);
      },

      statusMessage() {
        switch (this.userActivityRelation) {
          case "none":
            return "";
          case "follower":
            return "You are signed up to receive any updates to this activity";
          case "participant":
            return "You are participating in this activity";
          case "organiser":
            return "You are an organiser for this activity";
          case "creator":
            return "You are the creator of this activity";
          default:
            return "";
        }
      }
    },
    async mounted() {
      this.checkIfPreviouslyAnswered();
      this.getRespondents();
    },
    methods: {
      ...mapActions("activities", ["removeActivity"]),
      dateFormatterToEnglish: dateHelper.dateFormatterToEnglish,


      fillLocationName: function (out) {
        this.locationName = out.formatted_address;
      },
      editActivity() {
        this.$emit("navigate");
        this.$router.push({
          name: "editActivity",
          params: {
            userId: Number(this.userId),
            activityId: Number(this.activityId)
          }
        });
      },

      toggleFollowing: async function () {
        if (this.isFollowingLoading) return;
        this.isFollowingLoading = true;
        if (this.isFollowing) {
          await api.unfollowActivity(Number(this.userId), this.activityId);
          this.userActivityRelation = "none";
        } else {
          await api.followActivity(Number(this.userId), this.activityId);
          this.userActivityRelation = "follower";
        }
        this.isFollowingLoading = false;
        this.refreshUserCount = !this.refreshUserCount;
      },

      toggleParticipating: async function () {
        if (this.isParticipatingLoading) return;
        this.isParticipatingLoading = true;
        if (this.isParticipating) {
          await api.deleteActivityParticipation(
            Number(this.userId),
            this.activityId
          );
          this.userActivityRelation = "none";
        } else {
          await api.participateActivity(Number(this.userId), this.activityId);
          this.userActivityRelation = "participant";
        }
        this.isParticipatingLoading = false;
        this.refreshUserCount = !this.refreshUserCount;
      },
      backToSearch() {
        this.$emit("navigate");
        this.$router.push({
          name: "SearchActivities",
          query: {load: true}
        });
      },

      async deleteSingleActivity() {
        await this.removeActivity({
          userId: Number(this.userId),
          activityId: Number(this.activityId)
        });
        this.$router.push({
          name: "SearchActivities"
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
          onConfirm: () => {
            this.deleteSingleActivity();
          }
        });
      },

      async openActivityOutcomeAnswer(userId) {
        let response = await api.getActivitySpecificationById(this.activityId);
        if (response.data.length) {
          this.$emit("navigate");
          this.$router.push({
            name: "activityOutcomeAnswer",
            params: {userId: userId, activityId: this.activityId}
          });
        } else {
          this.$buefy.toast.open({
            message: "Sorry there are no questions set for this activity",
            type: "is-info",
            duration: 3000
          });
        }
      },
      displayActivityVisibility(visibility) {
        switch (visibility) {
          case "public":
            return "Everyone can see!";
          case "restricted":
            return "Only specified people can see!";
          case "private":
            return "Only you can see!";
        }
      },

      async checkIfPreviouslyAnswered() {
        let response = await api.getUserOutcomeAnswers(
          this.userId,
          this.activityId
        );
        const answersFromBackend = response.data.answers;
        this.previouslyAnswered = answersFromBackend.length !== 0;
      },

      async getRespondents() {
        let results = await api.getRespondentsByActivityId(this.activityId);
        results.data.forEach(item => this.respondents.push(item));
        this.numRespondentsRow = Math.ceil(
          results.data.length / this.respondentsPerRow
        );
      }
    }
  };
</script>

<style scoped>
  #back-btn {
    margin-bottom: 1rem;
  }

  .time {
    margin-top: 0.5rem;
  }

  .spacer {
    margin-bottom: 1.5rem;
  }

  #activity-title {
    padding-top: 4rem;
  }

  .img-container {
    position: relative;
    text-align: center;
  }

  .img-container img {
    filter: brightness(80%);
  }

  .img-container .middle-left {
    position: absolute;
    bottom: 2rem;
    left: 2rem;
  }

  .img-container .bottom-left {
    position: absolute;
    bottom: 1rem;
    left: 2rem;
  }

  .title {
    text-shadow: 2px 0 0;
    font-size: 3rem;
  }

  .location {
    font-size: 1.1rem;
  }

  .activity-info {
    margin-left: 0.8rem;
    margin-right: 0.8rem;
  }

  nav.info-text {
    margin-top: 1rem;
  }

  .button-spacer {
    margin-right: 0.5rem;
  }
</style>
