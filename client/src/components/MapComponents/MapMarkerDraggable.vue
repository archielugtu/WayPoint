<template>
  <div />
</template>

<script>
  export default {
    name: "MapMarkerDraggable",
    props: ["draggableMarkerPosition"],

    data: function() {
      return {
        map: null,
        marker: null,
        latitude: null,
        longitude: null
      };
    },

    watch: {
      draggableMarkerPosition: function() {
        if (this.map) { // Only update if map has loaded in. Initial positioning is otherwise handled in mounted
          if (this.draggableMarkerPosition) {
            this.marker.setMap(this.map);
            this.marker.setPosition(this.draggableMarkerPosition);
            if (this.draggableMarkerPosition.recenter) {
              this.map.setCenter(this.draggableMarkerPosition);
            }
          } else {
            // Hide marker
            this.marker.setMap(null);
          }
        }
      }
    },

    mounted() {
      this.$parent.getMap(map => {
        this.map = map;
        let initialMap
        let initialPosition
        if (this.draggableMarkerPosition) {
          initialMap = this.map
          initialPosition = this.draggableMarkerPosition
          if (this.draggableMarkerPosition.recenter) {
            this.map.setCenter(this.draggableMarkerPosition);
          }
        }

        this.marker = new window.google.maps.Marker({
          position: initialPosition,
          map: initialMap,
          draggable: true,
          animation: window.google.maps.Animation.DROP
        });

        this.marker.addListener("mouseup", () => {
          this.marker.setMap(this.map);
          this.$emit("draggableMarkerMoved", {
            lat: this.marker.getPosition().lat(),
            lng: this.marker.getPosition().lng()
          });
        });

        this.map.addListener("click", event => {
          this.marker.setMap(this.map);
          this.marker.setPosition(event.latLng);
          this.$emit("draggableMarkerMoved", {
            lat: this.marker.getPosition().lat(),
            lng: this.marker.getPosition().lng()
          });
        });
      });
    },

    beforeDestroy() {
      // Clean up marker from map
      this.marker.setMap(null);
      window.google.maps.event.clearInstanceListeners(this.marker);
    },

    render() {
      return null;
    }
  };
</script>

<style scoped>
</style>