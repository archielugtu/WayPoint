<template>
  <div>
    <LocationMapInput
      class="location-input"
      :locationData="userData.location"
      :allowCoordinates="false"
      :required="false"
      @isValid="isValidLocation = $event"
      @locationInfo="location = $event"
    />
    <div class="field is-grouped is-grouped-right">
      <div class="control">
        <router-link
          :to="{name: 'profile', userId: $route.params.userId}"
          class="button is-primary"
        >Back to profile</router-link>
      </div>
      <div class="control">
        <a class="button is-success" @click="saveUserLocation">
          <strong>
            Confirm
            Location
          </strong>
        </a>
      </div>
    </div>
  </div>
</template>

<script>
import LocationMapInput from "@/components/MapComponents/LocationMapInput";

export default {
  name: "Location",
  props: ["userData"],
  components: {
    LocationMapInput
  },
  data() {
    return {
      location: {},
      isValidLocation: true
    };
  },
  methods: {
    saveUserLocation() {
      if (!this.isValidLocation) {
        this.$buefy.toast.open({
          duration: 3000,
          message: "Address field is invalid",
          type: "is-danger"
        });
        return;
      }
      // Set the userdata.location to the location that they have inputted in the location fields in edit profile
      this.userData.location = this.location
      this.$emit("click", this.userData);
    }
  }
};
</script>

<style scoped>
.location-input {
  margin-bottom: 16px;
}

#map {
  height: 24.8em;
}

.required {
  margin-right: 2px;
}
</style>
