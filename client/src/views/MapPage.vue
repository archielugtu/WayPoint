
<template>
  <div class="columns" id="container">
    <div
      v-bind:class="{
        'box column equalHeight is-3': searchShowing && mapShowing,
        'box column equalHeight': !mapShowing,
        '': !searchShowing && mapShowing
      }"
      style="height: 91vh"
    >
      <b-button
        v-if="searchShowing && !mapShowing"
        style="float: right; margin-right: 0.6%"
        class="is-small"
        @click="toggleMapShowing()"
      >
        <b-icon icon="angle-double-right" v-if="mapShowing"></b-icon>
        <b-icon icon="angle-double-left" v-else></b-icon>
      </b-button>
      <keep-alive>
        <ActivitiesList
          v-if="searchShowing || !mapShowing"
          :load="load"
          :bounds="viewportBounds"
          :isFullscreen="!mapShowing"
          @updateMarkers="updateMarkers($event)"
          @centerOnLocation="centerMapOnLocation($event)"
        />
      </keep-alive>
    </div>

    <div
      v-bind:class="{
        'column equalHeight is-9': mapShowing && searchShowing,
        'column equalHeight': mapShowing && !searchShowing,
        '': !mapShowing
      }"
      style="height: 90vh"
    >
      <div v-if="mapShowing">
        <div class="level" style="margin-bottom: 0">
          <div id="map-buttons" class="buttons" style="padding-left: 0.6%">
            <b-button
              style="float: right"
              class="is-small"
              @click="toggleSearchShowing()"
              :disabled="!mapShowing"
            >
              <b-icon icon="angle-double-left" v-if="searchShowing"></b-icon>
              <b-icon icon="angle-double-right" v-else></b-icon>
            </b-button>

            <b-button v-if="searchShowing" class="is-small" @click="toggleMapShowing()">
              <b-icon icon="angle-double-right" v-if="mapShowing"></b-icon>
              <b-icon icon="angle-double-left" v-else></b-icon>
            </b-button>
          </div>

          <!-- Map Legend -->
          <div class="level" style="padding-right: 1rem">
            <div style="padding-right: 1rem">
              <strong>Legend:</strong>
            </div>
            <div class="level-item" style="padding-right: 1rem">
              <figure class="image is-24x24">
                <img src="@/assets/user-pin.png" alt="User marker image" />
              </figure>
              <p>You</p>
            </div>
            <div class="level-item" style="padding-right: 1rem">
              <figure class="image is-24x24">
                <img src="@/assets/single-activity-pin.png" alt="Activity marker image" />
              </figure>
              <p>Activity</p>
            </div>
            <div class="level-item">
              <figure class="image is-24x24">
                <img src="@/assets/multiple-activity-pin.png" alt="Multiple Activity marker image" />
              </figure>
              <p>Multiple Activities</p>
            </div>
          </div>
        </div>

        <MapPane
          ref="mapPane"
          v-bind:markers.sync="markers"
          v-bind:height="85"
          @refreshMapLocation="viewportChanged($event)"
          @click="markerClicked($event)"
          @selectActivity="selectActivity($event)"
        />
      </div>
    </div>
  </div>
</template>

<script>
  import api from "@/Api";
  import dateHelper from "@/utils/dates/dates";
  import ActivitiesList from "@/components/Activity/ActivitiesList";
  import MapPane from "@/components/MapComponents/MapPane";
  import { mapState } from "vuex";

  export default {
    name: "MapPage",
    components: {
      ActivitiesList,
      MapPane
    },

    props: {
      // Hashtags name we are searching for
      load: { type: Boolean }
    },

    data: function() {
      return {
        // Example {"id":1612,"latitude":60.92103520000001,"longitude":30.3019144,"name":"__vue_devtool_undefined__","imageType":"user"}
        markers: [],
        participantsPlaceIds: [],
        selectedId: -1,
        activityName: "",
        location: "",
        description: "",
        continuous: false,
        startDate: null,
        endDate: null,
        hashtags: [],
        activityTypes: [],
        mapShowing: true,
        searchShowing: true,
        userPlaceId: null,
        viewportBounds: null,
        mapCenter: null,
        userMarker: null,
        hasRecentered: false
      };
    },

    mounted() {
      if (!this.isGlobalAdmin) {
        this.createUserMarker();
      }
    },

    computed: {
      ...mapState(["userId", "isGlobalAdmin"])
    },

    methods: {
      dateFormatterToEnglish: dateHelper.dateFormatterToEnglish,
      viewportChanged(bounds) {
        this.viewportBounds = bounds;
      },

      selectActivity(activity) {
        this.$router.push({
          name: "ViewActivity",
          params: {
            activityId: activity.id
          }
        });
      },

      centerMapOnLocation(location) {
        this.$refs.mapPane.center(location);
        this.mapCenter = location;
      },

      toggleSearchShowing() {
        this.searchShowing = !this.searchShowing;
      },

      toggleMapShowing() {
        this.mapShowing = !this.mapShowing;
        if (this.mapShowing) {
          this.createUserMarker();
        }
      },

    async createUserMarker() {
      let user = null;
      await api
        .getProfileById(Number(this.userId))
        .then(result => (user = result.data));
      if (user.location?.latitude && user.location?.longitude) {
        this.userMarker = {
          latitude: user.location.latitude,
          longitude: user.location.longitude,
          imageType: "user"
        };
      }
    },

      markerClicked(activityId) {
        this.selectedId = activityId;
        this.getActivity(activityId);
      },

      updateMarkers(newMarkers) {
        if (this.userMarker) {
          this.userMarker.recenter = !this.hasRecentered
          newMarkers.push(this.userMarker);
          this.hasRecentered = true
        }
        this.markers = newMarkers;
      }
    }
  };
</script>

<style scoped>
  .equalHeight {
    margin-bottom: 0;
  }

  #container {
    margin-top: 1vh;
  }

  #map-buttons {
    padding-bottom: 0;
    margin-bottom: 0;
  }
</style>
