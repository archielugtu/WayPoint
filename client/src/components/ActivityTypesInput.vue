<template>
  <b-field :type="type" :message="message" :horizontal="horizontalLabel">
    <template v-if="withLabel" slot="label">
      Activity Types
      <span v-if="required" class="required">*</span>
    </template>
    <b-taginput
      id="activity-tag-input"
      :value="newValue"
      :data="filteredTags"
      autocomplete
      :ellipsis="true"
      icon="angle-double-right"
      field="activity"
      :open-on-focus="openOnFocus"
      :placeholder="placeholder != null ? placeholder : defaultPlaceholder"
      @input="$emit('input', $event)"
      @typing="getFilteredTags"
    ></b-taginput>
  </b-field>
</template>

<script>
  import api from "@/Api";
  import { ToastProgrammatic as Toast } from "buefy";

  export default {
    name: "InputActivityTypes",
    /* Props:
      "required" : Show the red asterisk
      "value" : Default value
      "withLabel" : Show the label Activity Types
      "horizontalLabel" : Make the label horizontal
      "type" : Colour of the input bar e.g. 'is-primary'
      "message" : Message to be shown below the input bar, useful for error messages
     */
    props: [
      "required",
      "value",
      "withLabel",
      "horizontalLabel",
      "type",
      "message",
      "placeholder"
    ],
    data() {
      return {
        allTags: [],
        filteredTags: [],
        isSelectOnly: false,
        allowNew: false,
        openOnFocus: false,
        defaultPlaceholder: "Add activities that you are interested in"
      };
    },
    computed: {
      newValue() {
        return this.value
      }
    },
    mounted() {
      api
        .getActivityTypes()
        .then(response => {
          this.allTags = response.data;
        })
        .catch(() => {
          Toast.open({
            message: "Unable to fetch activity types",
            type: "is-danger"
          });
        });
    },
    methods: {
      getFilteredTags(text) {
        this.filteredTags = this.allTags.filter(option => {
          return (
            option
              .toString()
              .toLowerCase()
              .indexOf(text.toLowerCase()) >= 0
          );
        });
      },
    }
  };
</script>

<style scoped>
</style>
