<template>
  <b-field :type="type" :message="message" :horizontal="horizontalLabel">
    <template v-if="withLabel" slot="label">
      Passport Countries
      <span v-if="required" class="required">*</span>
    </template>
    <b-taginput
      id="passport-tag-input"
      v-model="tags"
      :data="filteredTags"
      autocomplete
      icon="angle-double-right"
      field="name"
      :open-on-focus="openOnFocus"
      @input="emitData"
      @typing="getFilteredTags"
    ></b-taginput>
  </b-field>
</template>

<script>
  import axios from "axios";
  import { ToastProgrammatic as Toast } from "buefy";

  export default {
    name: "PassportTagInput",
    /* Props:
        "required" : Show the red asterisk
        "default" : Default value
        "withLabel" : Show the label Activity Types
        "horizontalLabel" : Make the label horizontal
        "type" : Colour of the input bar e.g. 'is-primary'
        "message" : Message to be shown below the input bar, useful for error messages
       */
    props: [
      "required",
      "default",
      "withLabel",
      "horizontalLabel",
      "type",
      "message"
    ],
    data() {
      return {
        allTags: [],
        filteredTags: [],
        isSelectOnly: false,
        tags: this.default,
        allowNew: false,
        openOnFocus: true
      };
    },
    mounted() {
      this.getCountryNames();
    },
    methods: {
      getFilteredTags(text) {
        this.filteredTags = this.allTags.filter(option => {
          return (
            option.name
              .toString()
              .toLowerCase()
              .indexOf(text.toLowerCase()) >= 0
          );
        });
      },
      getCountryNames() {
        axios
          .get("https://restcountries.eu/rest/v2/all")
          .then(response => {
            for (let country of response.data) {
              this.allTags.push({
                name: country.name,
                code: country.alpha3Code,
                flag: country.flag
              });
            }
          })
          .catch(() => {
            Toast.open({
              message: "Unable to fetch Passport countries",
              type: "is-danger"
            });
          });
      },
      emitData: function() {
        this.$emit("passportCountry", this.tags);
      }
    }
  };
</script>

<style scoped>
</style>
