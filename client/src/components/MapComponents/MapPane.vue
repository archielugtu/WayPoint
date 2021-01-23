<template>
  <div id="map" :style="'height: ' + height + 'vh'">
    <map-marker
      :markers="filteredMarkers"
      :isViewMode="viewMode"
      @hoverMarker="markerHover($event)"
      @markerClicked="displayInfoWindow($event)"
    ></map-marker>
    <MapMarkerDraggable
      v-if="isUsingDraggableMarker"
      :draggableMarkerPosition="draggableMarkerPosition"
      @draggableMarkerMoved="draggableMarkerMoved($event)"
    />
    <map-info-window
      :toggle-window="toggleWindow"
      :latitude="selectedLatitude"
      :longitude="selectedLongitude"
    >
      <InfoCard
        type="Activities"
        :items="selectedActivities"
        @selectActivity="selectActivity($event)"
      />
    </map-info-window>
  </div>
</template>

<script>
  import { ToastProgrammatic as Toast } from "buefy";
  import MapMarker from "./MapMarker";
  import MapInfoWindow from "./MapInfoWindow";
  import InfoCard from "./InfoCard";
  import MapMarkerDraggable from "./MapMarkerDraggable";

  export default {
    name: "MapPane",
    props: {
      markers: { type: Array, required: true }, // List containing { id, latitude, longitude, name, imageType } for each marker
      draggableMarkerPosition: { type: Object, required: false }, // List containing { id, latitude, longitude, name, imageType } for each marker
      height: { type: Number, required: true }, // Desired height of map pane, in vh,
      viewMode: { type: Boolean, required: false },
      isUsingDraggableMarker: { type: Boolean, required: false },
    },
    components: {
      MapMarker,
      MapInfoWindow,
      InfoCard,
      MapMarkerDraggable,
    },

    watch: {
      markers: function () {
        if (this.viewMode) {
          this.filteredMarkers = this.markers;
          return;
        }

        const activityMarkersWithLocation = this.markers.filter((m) => {
          return m.latitude && m.longitude && m.imageType != "user";
        });

        // List of all markers. These markers can contain multiple activities
        let combinedMarkers = [];

        // Congregate nearby markers into eachother
        for (let newMarker of activityMarkersWithLocation) {
          let nearby = combinedMarkers.find((existingMarker) => {
            return this.isLocationWithinRange(
              {
                latitude: newMarker.latitude,
                longitude: newMarker.longitude,
              },
              {
                latitude: existingMarker.latitude,
                longitude: existingMarker.longitude,
              },
              this.markerGroupingRange
            );
          });
          if (nearby) {
            // Add activity to existing pin
            nearby.imageType = "activity_multiple";
            nearby.activities.push({
              name: newMarker.name,
              id: newMarker.id,
              description: newMarker.description,
            });
          } else {
            // Add activity to new pin
            combinedMarkers.push({
              imageType: "activity",
              latitude: newMarker.latitude,
              longitude: newMarker.longitude,
              activities: [
                {
                  name: newMarker.name,
                  id: newMarker.id,
                  description: newMarker.description,
                },
              ],
            });
          }
        }

        // Add in user's pin separately to prevent combining
        const userMarker = this.markers.find((m) => {
          return m.imageType === "user";
        });
        if (userMarker) {
          combinedMarkers.push(userMarker);
        }

        this.filteredMarkers = combinedMarkers;
      },
    },

    data: function () {
      return {
        filteredMarkers: [],
        filteredAndJoinedMarkers: [],
        map: null,
        selectedLatitude: 0,
        selectedLongitude: 0,
        toggleWindow: false,
        data: [],
        loadActivitiesTimer: null,
        maxMarkersPerPageHeight: 40, // Used to group markers based on the height of the viewport.
        markerGroupingRange: 0.3, // range for grouping markers. This value is updated on zoom change
        selectedActivities: [],
      };
    },

    methods: {
      selectActivity(item) {
        this.$emit("selectActivity", item);
      },

      displayInfoWindow(thing) {
        this.selectedLatitude = thing.latitude;
        this.selectedLongitude = thing.longitude;
        this.selectedActivities = thing.activities;
        this.toggleWindow = !this.toggleWindow;
      },

      // Checks if locations are too close. Uses a square rather than radius for performance reasons
      isLocationWithinRange(l1, l2, range) {
        return (
          Math.abs(l1.latitude - l2.latitude) < range &&
          Math.abs(l1.longitude - l2.longitude) < range
        );
      },

      draggableMarkerMoved(event) {
        this.$emit("draggableMarkerMoved", event);
      },

      getMap(callback) {
        let vm = this;
        function checkForMap() {
          if (vm.map) callback(vm.map);
          else setTimeout(checkForMap, 200);
        }
        checkForMap();
      },

      center: function (location) {
        this.map.panTo({ lat: location.latitude, lng: location.longitude });
      },

      zoomAdjust() {
        const bounds = this.map.getBounds();
        const northEast = bounds.getNorthEast();
        const southWest = bounds.getSouthWest();
        // Only check vertical axis to avoid issues with meridian
        const vSize = northEast.lat() - southWest.lat();
        this.markerGroupingRange = vSize / this.maxMarkersPerPageHeight;
      },
    },

    async mounted() {
      const self = this;
      this.filteredMarkers = this.markers;
      try {
        let mapEl = document.getElementById("map");
        if (!mapEl) return;
        this.map = new window.google.maps.Map(mapEl, {
          center: { lat: -40.9006, lng: 174.886 }, // Initial center point for the map
          zoom: 6,
          minZoom: 2,
          maxZoom: 18,
          zoomControl: true,
        });

        this.map.addListener("zoom_changed", () => {
          self.zoomAdjust();
        });

        this.map.addListener("bounds_changed", () => {
          if (this.loadActivitiesTimer) {
            clearTimeout(this.loadActivitiesTimer);
            this.loadActivitiesTimer = null;
          }
          // After 1000 ms, update the map bounds
          this.loadActivitiesTimer = setTimeout(() => {
            const coords = this.map.getBounds();
            const northEast = coords.getNorthEast();
            const southWest = coords.getSouthWest();

            this.$emit("refreshMapLocation", {
              sw_lat: southWest.lat(),
              sw_lng: southWest.lng(),
              ne_lat: northEast.lat(),
              ne_lng: northEast.lng(),
            });
          }, 1000);
        });
      } catch (err) {
        Toast.open({
          message: "Error loading map",
          type: "is-danger",
        });
      }
    },
  };
</script>

<style scoped>
</style>
