<template>
  <div>
    <div class="box">
      <div id="passports" class="title is-4">Passport Countries</div>

      <div v-for="(country, index) in userData.passports" :key="index" class="field has-addons">
        <figure class="image country-flag is-64x64">
          <img :src="country.flag" :alt="country.name"/>
        </figure>
        <div class="control">
          <input
            class="input country-name"
            type="text"
            v-model="userData.passports[index].name"
            readonly
          />
        </div>
        <div class="control">
          <button @click="removeCountry(index, country)" class="button is-danger">
            <span class="icon is-small">
              <em class="fas fa-times"></em>
            </span>
          </button>
        </div>
      </div>

      <div class="field">
        <div
          @click="toggleCountryDropdown()"
          class="dropdown"
          :class="{'is-active': countryDropdownToggled}"
        >
          <div class="dropdown-trigger">
            <button class="button" aria-haspopup="true" aria-controls="dropdown-menu">
              <span>{{ selectedCountry.name }}</span>
              <span class="icon is-small">
                <i class="fas fa-angle-down" aria-hidden="true"></i>
              </span>
            </button>
          </div>
          <div class="dropdown-menu" id="dropdown-menu" role="menu">
            <div class="dropdown-content">
              <a
                @click="selectedCountry = country"
                class="dropdown-item"
                v-for="country in availableCountries"
                :key="country.code"
              >{{ country.name }}</a>
            </div>
          </div>
        </div>
      </div>

      <div class="field">
        <div
          class="help is-danger"
          v-if="hasDuplicateCountry"
        >There is a duplicate passport country.</div>
        <div v-if="!isSaved" class="help is-danger">Changes not saved</div>
      </div>

      <div class="field">
        <a id="add-country-btn" class="button is-link" @click="addCountry(selectedCountry)">
          <strong>Add Country</strong>
        </a>
      </div>
    </div>

    <div class="field is-grouped is-grouped-right">
      <div class="control">
        <router-link
          :to="{name: 'profile', userId: $route.params.userId}"
          class="button is-primary"
        >Back to profile</router-link>
      </div>
      <div class="control">
        <a class="button is-success" @click="saveUserProfile">
          <strong>Save Passports</strong>
        </a>
      </div>
    </div>
  </div>
</template>


<script>
  import axios from "axios";
  import { ToastProgrammatic as Toast } from "buefy";

  export default {
    name: "UserProfilePassport",
    props: ["userData"],
    data: function() {
      return {
        availableCountries: [],
        original_content: [],
        selectedCountry: "",
        hasDuplicateCountry: false,
        countryDropdownToggled: false,
        isSaved: true
      };
    },
    mounted() {
      this.init();
    },
    methods: {
      init() {
        this.getCountryNames();
      },
      saveUserProfile() {
        this.original_content = JSON.parse(
          JSON.stringify(this.userData.passports)
        ); // DEEP COPY NOT SHALLOW .-.
        this.$emit("click");
        this.isSaved = this.arraysEqual(
          this.original_content,
          this.userData.passports
        );
        this.hasDuplicateCountry = false;
      },
      getCountryNames() {
        axios
          .get("https://restcountries.eu/rest/v2/all")
          .then(response => {
            for (let country of response.data) {
              this.availableCountries.push({
                name: country.name,
                code: country.alpha3Code,
                flag: country.flag
              });
            }
            this.original_content = JSON.parse(
              JSON.stringify(this.userData.passports)
            ); // DEEP COPY NOT SHALLOW
          })
          .catch(() => {
            Toast.open({
              message: "Unable to fetch countries",
              type: "is-danger"
            });
          });
      },
      addCountry(country) {
        if (!country) {
          return;
        }

        for (let i = 0, len = this.userData.passports.length; i < len; i++) {
          if (country.name === this.userData.passports[i].name) {
            this.hasDuplicateCountry = true;
            return;
          }
        }
        this.userData.passports.push(country);
        this.isSaved = this.arraysEqual(
          this.original_content,
          this.userData.passports
        );
        this.hasDuplicateCountry = false;
      },
      toggleCountryDropdown() {
        this.countryDropdownToggled = !this.countryDropdownToggled;
      },
      removeCountry(lineId, country) {
        let isInOriginal = false;
        for (let i = 0; i < this.original_content.length; i++) {
          if (this.original_content[i].name === country.name) {
            isInOriginal = true;
            break;
          }
        }

        if (this.isSaved || isInOriginal) {
          this.$buefy.dialog.confirm({
            title: `Passport Removal`,
            message:
              "<b>Removing</b> passport country " + this.userData.passports[lineId].name,
            confirmText: "Delete",
            cancelText: "Cancel",
            type: "is-danger",
            hasIcon: true,
            onConfirm: () => this.removeFromCountryList(lineId)
          });
        } else {
          this.removeFromCountryList(lineId);
        }
      },
      removeFromCountryList(lineId) {
        this.userData.passports.splice(lineId, 1);
        this.isSaved = this.arraysEqual(
          this.original_content,
          this.userData.passports
        );
        this.hasDuplicateCountry = false;
      },
      arraysEqual(a, b) {
        if (a === b) return true;
        if (a == null || b == null) return false;
        if (a.length !== b.length) return false;
        a.sort();
        b.sort();
        for (let i = 0; i < a.length; ++i) {
          if (a[i].code !== b[i].code) return false;
        }
        return true;
      }
    }
  };
</script>


<style scoped>
  .country-flag {
    margin-right: 0.5rem;
    height: 64px;
  }

  .country-name {
    width: 20rem;
  }

  .dropdown-content {
    max-height: 20em;
    overflow: auto;
  }
</style>

