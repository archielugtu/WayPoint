<template>
  <table class="table user-info" aria-describedby="User's basic info table">
    <tbody>
      <tr>
        <td>Birthdate:</td>
        <td class="has-text-right" id="birthdate">{{ prettyBirthDate }}</td>
      </tr>
      <tr>
        <td>Email:</td>
        <td class="has-text-right" id="primary-email">{{ primaryEmail }}</td>
      </tr>
      <tr>
        <td>Gender:</td>
        <td class="has-text-right" id="gender">{{ userInfo.gender }}</td>
      </tr>
      <tr>
        <td>Location:</td>
        <td class="has-text-right" id="location">{{ userLocation }}</td>
      </tr>
      <tr>
        <td>Bio:</td>
        <td class="has-text-right" id="bio">{{ prettyBio }}</td>
      </tr>
      <tr>
        <td>Passports:</td>
        <td class="has-text-right" id="passport-countries">
          <div v-if="userInfo.passports.length">
            <div v-for="country in userInfo.passports" :key="country.name">
              <div class="field is-grouped is-grouped-right">
                <article class="media" style="padding-bottom: 0">
                  <figure class="media-left"></figure>
                  <div class="media-content">{{ country.name }}</div>
                  <figure class="media-right" style="padding-left: 0; margin-left: 5px">
                    <p class="image is-32x32">
                      <img :src="country.flag" />
                    </p>
                  </figure>
                </article>
              </div>
            </div>
          </div>
          <div v-else>No passports set</div>
        </td>
      </tr>
    </tbody>
  </table>
</template>


<script>
  import { toPrettyDate } from "@/utils";
  import { mapState } from "vuex";
  import googleMaps from "@/utils/geolocation/googleMaps-api";

  export default {
    name: "UserProfileBasicInfo",
    props: ["userInfo"],
    data() {
      return {
        userLocation: "Loading..."
      };
    },
    watch: {
      "userInfo.location.placeId": {
        immediate: true,
        handler: function() {
          this.retrieveLocation();
        }
      }
    },
    methods: {
      retrieveLocation() {
        if (this.userInfo.location !== null && this.userInfo.location.placeId) {
          const placeId = this.userInfo.location.placeId;
          googleMaps.getInfoFromPlaceId(placeId, this.fillInputFields);
        } else {
          this.userLocation = "Not set";
        }
      },
      fillInputFields(out) {
        let userLocation;

        const addrComponent = googleMaps.readAddressComponent(out);
        let address = addrComponent.address;
        let suburb = addrComponent.suburb;
        let city = addrComponent.city;
        let state = addrComponent.state;
        let country = addrComponent.country;
        // don't change
        if (this.userId === this.$route.params.userId) {
          userLocation = [address, suburb, city, state, country]
            .filter(e => e)
            .join(", ");
        } else {
          userLocation = [city, state, country].filter(e => e).join(", ");
        }

        this.userLocation = userLocation;
      }
    },
    computed: {
      ...mapState(["userId"]),

      prettyBirthDate() {
        return toPrettyDate(new Date(this.$props.userInfo.date_of_birth));
      },
      prettyBio() {
        if (!this.userInfo.bio.length) {
          return "Not set";
        }
        return this.userInfo.bio;
      },
      primaryEmail() {
        return this.$props.userInfo.primary_email.address;
      }
    }
  };
</script>


<style scoped>
  .user-info {
    margin-left: auto;
    margin-right: auto;
    width: 100%;
  }
</style>
