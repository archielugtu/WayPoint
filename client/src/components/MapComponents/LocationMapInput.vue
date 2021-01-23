<template>
  <div>
    <MapPane
      id="map"
      @draggableMarkerMoved="setLocationFromMarkerPosition($event)"
      :isUsingDraggableMarker="true"
      :displayUser="isPlaceIdValid"
      :displayActivity="false"
      :markers="[]"
      :draggableMarkerPosition="draggableMarkerPosition"
      v-bind:height="50"
    />
    <b-field :type="validationStatus">
      <b-autocomplete
        placeholder="Address"
        icon="map-pin"
        autocomplete="chrome-off"
        v-model="locationDetail"
        :loading="this.isFetching || this.isValidating"
        :data="dropDownValues"
        @typing="searchLocationWithTimeout($event); validateWithTimeout($event); "
        @input="emitData()"
        @select="selectAutofillOption"
        @blur="isDropdownOpen = false"
        @keydown.native.enter="selectFirstDropdownOption"
      >
        <template slot-scope="props">{{props.option.description}}</template>
      </b-autocomplete>
    </b-field>
    <b-field v-if="allowCoordinates">
      <div class="level location">
        <div class="level-left">
          <b-switch v-model="isUsingCoordinates">
            Use Coordinates
            <b-tooltip
              type="is-light"
              label="Use Coordinates to specify locations that can't be shown with just an address"
            >
              <b-icon size="is-small" icon="question-circle"></b-icon>
            </b-tooltip>
          </b-switch>
        </div>
        <div class="level-right">
          <p
            class="pulled-right has-text-danger"
          >{{isMarkerPositionValid ? "" : "Could not find location for map marker"}}</p>
        </div>
      </div>
    </b-field>
    <b-field v-if="isUsingCoordinates">
      <div class="columns location">
        <div class="column ">
          <b-field label-position="on-border" label="Latitude">
            <b-input placeholder="Latitude" expanded v-model.number="latitude"></b-input>
          </b-field>
        </div>
        <div class="column ">
          <b-field label-position="on-border" label="Longitude">
            <b-input placeholder="Longitude" expanded v-model.number="longitude"></b-input>
            <p class="control">
            <b-button @click="setToLongLat" type="is-primary">Set</b-button>
            </p>
          </b-field>
        </div>
      </div>
    </b-field>
    <b-field v-if="showDescription" label="Description" >
      <b-input v-model="description" type="textarea" maxlength="100" />
    </b-field>
  </div>
</template>


<script>
  import MapPane from "@/components/MapComponents/MapPane";
  import googleMaps from "@/utils/geolocation/googleMaps-api";

  export default {
    name: "LocationMapInput",
    props: {
      required: { type: Boolean, required: true }, // If true, location will not be valid if empty
      locationData: { type: Object, required: true }, // Initial data for pin. Not modified internally.
      allowCoordinates: { type: Boolean, required: true }, // Allows users to specify a location through lat/lng coordinates
      showDescription: {
        type: Boolean,
        default: true
      },
    },

    components: {
      MapPane
    },

    data: function() {
      return {
        isPlaceIdValid: false,
        draggableMarkerPosition: null,

        locationDetail: "", // Formatted address field used in autocomplete
        placeId: null,
        longitude: null,
        latitude: null,
        description: "",
        isUsingCoordinates: false,

        dropDownValues: [],
        warningMessage: "Location is invalid",
        isFetching: false,
        isValidating: false,
        addressSearchTimer: null,
        selectedFromDropdown: false,
        selectedOption: null,
        isDropdownOpen: false
      };
    },

    computed: {
      validationStatus: function() {
        if (!this.isMarkerPositionValid) {
          return "is-danger";
        }
        if (
          this.isFetching ||
          this.isValidating ||
          this.locationDetail === "" ||
          this.isDropdownOpen
        ) {
          return "";
        }
        if (this.isPlaceIdValid) {
          return "is-success";
        }
        return "is-danger";
      },

      isMarkerPositionValid: function() {
        return (
          (!this.latitude && !this.longitude) ||
          this.isPlaceIdValid ||
          this.isFetching ||
          this.isUsingCoordinates
        );
      },

      isLocationValid: function() {
        if (
          !this.required &&
          this.placeId == null &&
          this.latitude == null &&
          this.longitude == null
        ) {
          return true;
        }
        return (
          this.isPlaceIdValid ||
          (this.isUsingCoordinates && this.latitude && this.longitude)
        );
      },

    },

    watch: {
      isLocationValid: function() {
        this.$emit("isValid", this.isLocationValid);
      },
      locationData: function() {
        this.setLocationData(this.locationData);
      },
      description: function() {
        this.emitData();
      }
    },

    mounted: function() {
      this.setLocationData(this.locationData)
    },

    methods: {
      setToLongLat: function() {
        this.setLocationFromMarkerPosition({
              lat: this.latitude,
              lng: this.longitude
            }
        );
      },

      // After a delay, searches for addresses. Removes any existing pins from the map
      searchLocationWithTimeout: function(address) {
        this.resetLocation();
        this.selectedFromDropdown = false;
        this.isPlaceIdValid = false;
        this.isFetching = true;

        // Sets a 500ms timer. Further input will reset this timer. Runs a search on timer completion
        if (this.addressSearchTimer) {
          clearTimeout(this.addressSearchTimer);
          this.addressSearchTimer = null;
        }
        this.addressSearchTimer = setTimeout(() => {
          this.runAddressSearch(address);
        }, 500);
      },

      // Sends a request to google API to find valid addresses. Options are seyt as dropdown values in the autcomplete box
      runAddressSearch: async function(address) {
        try {
          this.isFetching = true;
          this.isDropdownOpen = true;
          await googleMaps.searchByLocationName(address, this.setDropdownValues);
          this.isFetching = false;
        } catch (err) {
          this.isPlaceIdValid = false;
          this.isFetching = false;
        }
      },

      // Sets the values of the autocomplete box's dropdown options.
      setDropdownValues: function(out) {
        this.dropDownValues = out ? out : [];
      },

      selectFirstDropdownOption: function() {
        if (this.dropDownValues && this.dropDownValues.length) {
          this.isDropdownOpen = false;
          this.selectAutofillOption(this.dropDownValues[0]);
        }
      },

      // Fills in data when user selects an option from autocomplete dropdown
      selectAutofillOption: function(option) {
        if (option === null) {
          return;
        }
        this.selectedOption = option;
        googleMaps.getInfoFromPlaceId(
          option.place_id,
          this.setLocationFromPlaceId
        );
        this.isPlaceIdValid = true;
        this.dropDownValues = [];
      },

      // Callback for getInfoFromPlaceId. Used when selecting option from dropdown
      setLocationFromPlaceId: async function(out) {
        if (out === null) {
          this.isPlaceIdValid = false;
          this.selectedFromDropdown = false;
          return;
        }
        const addrComponent = await googleMaps.readAddressComponent(out);

        // Need to set location as formatted addresses to keep consistne for validation
        this.locationDetail = out.formatted_address;
        this.placeId = addrComponent.placeId;
        this.longitude = addrComponent.longitude;
        this.latitude = addrComponent.latitude;
        this.draggableMarkerPosition = {
          lat: this.latitude,
          lng: this.longitude,
          recenter: true
        };
        this.isPlaceIdValid = true;
        this.selectedFromDropdown = true;
        this.emitData();
      },

      // Sets the location when the map marker is used. If not using a coordinate system
      // The pin will snap to the nearest valid position
      setLocationFromMarkerPosition(location) {
        this.isFetching = true;
        this.latitude = location.lat;
        this.longitude = location.lng;

        if (this.isUsingCoordinates) {
          googleMaps.searchByLatLng(
            location.lat,
            location.lng,
            this.setLocationToExactCoordinates
          );
        } else {
          googleMaps.searchByLatLng(
            location.lat,
            location.lng,
            this.setLocationToNearestPlaceId
          );
        }
      },

      // Sets the location placeId and description to the exact location of the map marker - no snapping
      setLocationToExactCoordinates(response) {
        if (response && response.length) {
          const bestMatch = response[0];
          this.placeId = bestMatch.place_id;

          this.isPlaceIdValid = true;
          this.locationDetail = bestMatch.formatted_address;

          this.draggableMarkerPosition = {
            lat: this.latitude,
            lng: this.longitude,
            recenter: true
          };
        } else {
          this.placeId = null;
          this.isPlaceIdValid = false;
          this.locationDetail = "";
        }
        this.isFetching = false;
        this.emitData();
      },

      // Sets the location data when parent class changes it
      setLocationData(locationData) {
        this.placeId = locationData?.placeId;
        if (this.placeId) {
          this.isFetching = true;
          googleMaps.getInfoFromPlaceId(this.placeId, this.setAddressInfo);
        }
        this.isUsingCoordinates = locationData?.isUsingCoordinates || false;
        if (locationData?.latitude && locationData?.longitude) {
          this.latitude = locationData.latitude;
          this.longitude = locationData.longitude;
          this.draggableMarkerPosition = {
            recenter: true,
            lat: this.latitude,
            lng: this.longitude
          };
        }
      },

      // Sets the location placeId and coordinates to the best placeId selected by google for the specified pin location
      setLocationToNearestPlaceId(response) {
        if (response && response.length) {
          const bestMatch = response[0];
          this.placeId = bestMatch.place_id;
          this.locationDetail = bestMatch.formatted_address;
          this.latitude = bestMatch.geometry.location.lat();
          this.longitude = bestMatch.geometry.location.lng();
          this.isPlaceIdValid = true;
          // Snap the marker to the nearest location
          this.draggableMarkerPosition = {
            lat: this.latitude,
            lng: this.longitude
          };
        } else {
          this.isPlaceIdValid = false;
          this.locationDetail = "";
        }
        this.isFetching = false;
        this.emitData();
      },

      // Sends the data to parent
      emitData: function() {
        this.$emit("locationInfo", {
          placeId: this.placeId,
          longitude: this.longitude,
          latitude: this.latitude,
          description: this.description,
          isUsingCoordinates: this.isUsingCoordinates
        });
      },

      // Validates the address after a timeout
      validateWithTimeout: function() {
        this.isValidating = true;
        if (this.validateTimer) {
          clearTimeout(this.validateTimer);
          this.validateTimer = null;
        }
        this.validateTimer = setTimeout(() => {
          this.validateAndSetPlace();
        }, 1000);
      },

      // Validate the formatted address by checking for equality
      // against the formatted addresses in the dropdown options
      // these options should be up to date and the best possible matches

      validateAndSetPlace: function() {
        const match = this.dropDownValues.find(option => {
          return (
            option.description.toLowerCase() === this.locationDetail.toLowerCase()
          );
        });

        // Need to get coords/placeId if user enters in text themselves.
        if (match) {
          googleMaps.getInfoFromPlaceId(
            match.place_id,
            this.setLocationFromPlaceId
          );
          this.isPlaceIdValid = true;
        } else {
          this.isPlaceIdValid = false;
        }
        this.isValidating = false;
      },

      // Removes the map marker from the map
      resetLocation: function() {
        this.placeId = null;
        this.latitude = null;
        this.longitude = null;
        this.draggableMarkerPosition = null;
      },

      // Sets the address field from a google API request. Only done on itital load
      setAddressInfo: function(out) {
        this.isFetching = false;
        this.locationDetail = out.formatted_address;
        this.isPlaceIdValid = Boolean(out);
      }
    }
  };
</script>

<style scoped>
  .location {
    margin-bottom: 1rem;
  }

  #map {
    margin-bottom: 1rem;
  }

  #map {
    margin-bottom: 1rem;
  }
</style>