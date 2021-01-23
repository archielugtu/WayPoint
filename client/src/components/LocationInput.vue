<template>
  <div>
    <b-field
      label="Address"
      :horizontal="horizontalLabel"
      id="address"
      :type="{'is-warning': isNoLocationFound }"
      
    >
      <b-autocomplete
        :data="dropDownValues"
        autocomplete="chrome-off"
        placeholder="Address"
        icon-pack="fas"
        icon="fa fa-home"
        field="address"
        :loading="isFetching"
        v-model="address"
        @typing="searchAddressWithTimeout"
        @input="emitData()"
        @select="selectAutofillOption"
        
      >
        <template slot-scope="props">{{props.option.description}}</template>
      </b-autocomplete>
    </b-field>

    <b-field
      label="Suburb"
      :horizontal="horizontalLabel"
      id="suburb"
      :type="{'is-warning': isNoLocationFound}"
    >
      <b-input
        disabled
        :data="dropDownValues"
        placeholder="Suburb"
        icon-pack="fas"
        icon="fa fa-street-view"
        field="suburb"
        :loading="isFetching"
        :readonly = true
        v-model="suburb"
        @input="emitData()"
      >
      </b-input>
    </b-field>

    <b-field
      label="City"
      :horizontal="horizontalLabel"
      id="city"
      :type="{'is-warning': isNoLocationFound}"
    >
      <b-autocomplete
        autocomplete="chrome-off"
        :data="dropDownValues"
        placeholder="City"
        icon-pack="fas"
        icon="fa fa-city"
        field="city"
        :loading="isFetching"
        v-model="city"
        @typing="searchCityWithTimeout"
        @input="emitData()"
        @select="selectAutofillOption"
      >
        <template slot-scope="props">{{props.option.description}}</template>
      </b-autocomplete>
    </b-field>

    <b-field
      label="State"
      :horizontal="horizontalLabel"
      id="state"
      :type="{'is-warning': isNoLocationFound}"
    >
      <b-input
        disabled
        placeholder="State"
        icon-pack="fas"
        icon="fas fa-flag"
        :loading="isFetching"
        v-model="state"
        :readonly = true
        @input="emitData()"
      ></b-input>
    </b-field>

    <b-field
      label="Country"
      :horizontal="horizontalLabel"
      id="country"
      :type="{'is-warning':isNoLocationFound}"
    >
      <b-input
        disabled
        placeholder="Country"
        icon-pack="fas"
        icon="fa fa-globe"
        :readonly = true
        :loading="isFetching"
        v-model="country"
        @input="emitData()"
      ></b-input>
    </b-field>

    <b-field v-if="warningMessage" label :horizontal="horizontalLabel" id="error">
      <p class="help is-danger">{{warningMessage}}</p>
    </b-field>
  </div>
</template>

<script>
import googleMaps from "@/utils/geolocation/googleMaps-api";

export default {
  name: "LocationInput",
  /* Props:
   *   - defaultCountry: Default country value of the input field
   *   - defaultCity: Default city value of the input field
   *   - defaultState: Default state value of the input field
   *   - defaultAddress: Default address value of the input field
   *   - defaultSuburb: Default suburb value of the input field
   */
  props: ["defaultLocation", "horizontalLabel"],
  data: function() {
    return {
      placeId: null,
      address: "",
      suburb: "",
      city: "",
      state: "",
      country: "",
      latitude: null,
      longitude: null,
      isFetching: false,
      isLocationValid: false,
      dropDownValues: [],
      addressSearchTimer: null,
      citySearchTimer: null,
      googleThing: null,
    };
  },
  watch: {
    defaultLocation: function(placeId) {
      this.placeId = placeId;

    },
    defaultAddress: function(updated) {
      this.address = updated;
    },
    defaultSuburb: function(updated) {
      this.suburb = updated;
    },
  },
  mounted() {
    if (this.defaultLocation) {
      this.placeId = this.defaultLocation;
      googleMaps.getInfoFromPlaceId(this.placeId, this.fillInputFields)
    }

  },
  computed: {
    warningMessage: function() {
      if (this.isNoLocationFound) {
        return "Location not found";
      }
      return ""
    },
    isNoLocationFound: function() {
      return !this.isLocationValid && (this.address || this.city) && !this.isFetching && !this.dropDownValues.length
    },
  },
  methods: {

    resetNonAddressFields() {
      this.suburb = "";
      this.city = "";
      this.state = "";
      this.country = "";
      this.placeId = null;
      this.longitude = null;
      this.latitude = null;
      this.emitData()
    },
    resetNonCityFields() {
      this.address = "";
      this.suburb = "";
      this.state = "";
      this.country = "";
      this.placeId = null;
      this.longitude = null;
      this.latitude = null;
      this.emitData()
    },
    selectAutofillOption: function(option) {
      if (option === null) {
        return;
      }

      googleMaps.getInfoFromPlaceId(option.place_id, this.fillInputFields)
      this.isLocationValid = true;
      this.emitData();
    },

    emitData: function() {
      this.$emit("location", {
        placeId: this.placeId,
        address: this.address,
        suburb: this.suburb,
        city: this.city,
        state: this.state,
        country: this.country,
        latitude: this.latitude,
        longitude: this.longitude
      });
    },
    // Runs an address search if no input is given for 500ms after last input
    searchAddressWithTimeout: function(addressName) {
      this.resetNonAddressFields()
      if (addressName) {
        this.isLocationValid = false;
      } else {
        this.isLocationValid = true;
        return;
      }
      this.isFetching = true;
      if (this.addressSearchTimer) {
        clearTimeout(this.addressSearchTimer);
        this.addressSearchTimer = null;
      }
      this.addressSearchTimer = setTimeout(() => {
        this.runAddressSearch(addressName);
      }, 500);
    },
    // Runs a city search if no input is given for 500ms after last input
    searchCityWithTimeout: function(cityName) {

      this.resetNonCityFields()
      if (cityName) {
        this.isLocationValid = false;
      } else {
        this.isLocationValid = true;
        return;
      }
      this.isFetching = true;
      if (this.citySearchTimer) {
        clearTimeout(this.citySearchTimer);
        this.citySearchTimer = null;
      }
      this.citySearchTimer = setTimeout(() => {
        this.runCitySearch(cityName);
      }, 500);
    },
    setDropdownValues(out) {
      this.dropDownValues = out ? out : []
    },
    // parses the address components given by the selected location into correct format
    fillInputFields(out) {
      // If no location is found, then set isLocationValid to false
      if (out === null) {
        this.isLocationValid = false;
        return;
      }
      this.isLocationValid = true;

      const addrComponent = googleMaps.readAddressComponent(out);
      this.address = addrComponent.address
      this.latitude = addrComponent.latitude
      this.longitude = addrComponent.longitude
      this.suburb = addrComponent.suburb
      this.city = addrComponent.city
      this.state = addrComponent.state
      this.country = addrComponent.country
      this.placeId = addrComponent.placeId
      this.emitData()
    },
    async runCitySearch(cityName) {
      try {
        this.reset();
        this.isFetching = true;
        await googleMaps.searchByCity(cityName, this.setDropdownValues);
        this.isFetching = false;
      } catch (err) {
        this.isLocationValid = false;
        this.isFetching = false;
        this.reset();
      }
    },

    async runAddressSearch(addressName) {
      try {
        this.reset();
        this.isFetching = true;
        await googleMaps.searchByLocationName(addressName, this.setDropdownValues);
        this.isFetching = false;
      } catch (err) {
        this.isLocationValid = false;
        this.isFetching = false;
        this.reset();
      }
    },
    reset: function() {
      this.isFetching = false;
    }
  }
};
</script>

<style scoped>
</style>
