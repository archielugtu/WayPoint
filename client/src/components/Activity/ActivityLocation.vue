<template>
  <div>
    <LocationMapInput
      :locationData="location"
      :allowCoordinates="true"
      :required="true"
      @isValid="emitIsLocationValid($event)"
      @locationInfo="emitLocation($event)"
    />
  </div>
</template>

<script>
  import LocationMapInput from "@/components/MapComponents/LocationMapInput";
  import api from "@/Api";

  export default {
    name: "ActivityLocation",
    props: ["locationNotNext", "activityId"],
    components: {
      LocationMapInput
    },

    data() {
      return {
        placeId: null,
        location: {}
      };
    },

    mounted: function() {
      if (this.activityId) {
        api
          .getActivityById(this.activityId)
          .then(response => {
            this.location = response.data.location || {};
          })
          .catch(() => {
            this.$buefy.toast.open({
              duration: 3000,
              message: "Failed to load location",
              type: "is-danger"
            });
          });
      }
    },

    methods: {
      emitLocation: function(location) {
        this.$emit("location", location);
      },
      emitIsLocationValid(isLocationValid) {
        this.$emit("isValid", isLocationValid);
      }
    }
  };
</script>

<style scoped>
#map {
  height: 24.8em;
}

.required {
  margin-right: 2px;
}
</style>