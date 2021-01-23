<template>
  <div class="info-window" style="cursor: pointer">
    <slot></slot>
  </div>
</template>

<script>
  export default {
    name: "MapInfoWindow",
    props: {
      latitude: { type: Number, required: true },
      longitude: { type: Number, required: true },
      toggleWindow: { type: Boolean, required: true },
      closeWindow: { type: Boolean, required: false }
    },

    watch: {
      toggleWindow: function() {
        this.localLatitude = this.latitude
        this.localLongitude = this.longitude
        this.showMapInfoWindow()
      },

      closeWindow: function() {
        if (this.closeWindow) {
          this.infoWindow.close()
        }
      }
    },

    data: function () {
      return {
        infoWindow: null,
        localLatitude: null,
        localLongitude: null
      }
    },

    methods: {
      showMapInfoWindow() {
        // Add info window to parent view's map
        this.$parent.getMap(map => {
          if (this.infoWindow != null) {
            this.infoWindow.setPosition({ lat: this.localLatitude, lng: this.localLongitude })
            this.infoWindow.setContent( this.$el )
          } else {
            this.infoWindow = new window.google.maps.InfoWindow({
              position: { lat: this.localLatitude, lng: this.localLongitude },
              content: this.$el,
              disableAutoPan: true  // Toggles the map auto centre on the added info window
            });
          }
          this.infoWindow.open(map);
        })
      }
    }
  }
</script>

<style scoped>
  div.info-window {
    max-height: 250px;
  }
</style>
