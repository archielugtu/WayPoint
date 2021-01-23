<template>
  <div />
</template>

<script>
  import SingleActivityMarker from "@/assets/single-activity-pin.png";
  import MultipleActivityMarker from "@/assets/multiple-activity-pin.png";
  import UserMarker from "@/assets/user-pin.png";

  export default {
    name: "MapMarker",
    props: {
      markers: { type: Array, required: true },
      isViewMode: { type: Boolean }
    },
    watch: {
      markers: {
        handler: function() {
          // remove all existing markers
          for (let i = 0; i < this.markerObjects.length; i++) {
            this.markerObjects[i].setMap(null);
          }
          this.markerObjects = [];

          //add new markers
          for (let i = 0; i < this.markers.length; i++) {
            this.createMarker(this.markers[i]);
          }
        }
      }
    },
    data: function() {
      return {
        markerObjects: [],
        mapObject: null,
        isAwaitingMap: false,
        markerToBeAdded: null,
      };
    },
    methods: {
      setMap(map) {
        this.mapObject = map;
        if (this.isAwaitingMap) {
          this.createMarker(this.markerToBeAdded);
        }
      },
      createMarker(marker) {
        if (!this.mapObject) {
          this.isAwaitingMap = true;
          this.markerToBeAdded = marker;
          return;
        }
        let url;
        let markerObject;
        let hoverText;
        if (marker.imageType === "activity") {
          url = SingleActivityMarker;
          if(marker.activities?.[0]){
            hoverText = marker.activities[0].name
          }else{
            hoverText = marker.name
          }

        } else if (marker.imageType === "activity_multiple") {
          url = MultipleActivityMarker;
          hoverText = marker.activities.length + " Activities"
        } else {
          url = UserMarker;
          hoverText = "Your Location"
        }
        if (marker.recenter) {
          this.mapObject.setCenter({
            lat: marker.latitude,
            lng: marker.longitude
          });
        }
        let image = {
          scaledSize: new window.google.maps.Size(32, 32),
          url: url
        };
        markerObject = new window.google.maps.Marker({
          position: { lat: marker.latitude, lng: marker.longitude },
          map: this.mapObject,
          icon: image
        });
        if (marker.imageType !== "user" && !this.isViewMode) {
          markerObject.addListener("click", () => {
            this.$emit("markerClicked", {
              latitude: marker.latitude,
              longitude: marker.longitude,
              activities: marker.activities
            });
          });
        }
        markerObject.info = new window.google.maps.InfoWindow({
          content:
            "<strong>" +
            hoverText +
            "</strong>"
        });
        markerObject.addListener("mouseover", () => {
          markerObject.info.open(this.mapObject, markerObject);
        });
        markerObject.addListener("mouseout", () => {
          markerObject.info.close();
        });

        this.markerObjects.push(markerObject);
      }
    },
    mounted() {
      this.$parent.getMap(this.setMap);
      // Add map marker to parent view's map
    },

    render() {
      return null;
    }
  };
</script>

<style scoped>
</style>