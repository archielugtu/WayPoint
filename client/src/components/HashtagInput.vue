<template>
  <b-field expanded
    :type="{'is-danger': invalidTag}"
  >
    <b-taginput
      id="hashtag-input"
      :data="filteredTags"
      :value="value"
      autocomplete
      icon="hashtag"
      :open-on-focus="false"
      :allow-new="allowNew"
      :has-counter="hasCounter"
      maxtags="30"
      @input="cleanTag"
      @typing="getFilteredTags"
    ></b-taginput>
  </b-field>
</template>

<script>
  import api from "@/Api";

  export default {
    name: "HashtagInput",
    props: {
      value: {
        type: Array
      },
      hasCounter: {
        type: Boolean,
        default: true
      },
      allowNew: {
        type: Boolean,
        default: true
      }
    },
    data() {
      return {
        existingHashtags: [],
        filteredTags: [],
        invalidTag: false
      };
    },
    watch: {
      value(value) {
        this.value = value;
      }
    },
    created() {
      this.getTags();
    },
    methods: {
      getFilteredTags(text) {
        this.invalidTag = false;
        if (text.length < 3) {
          this.filteredTags = [];
          return;
        }
        this.filteredTags = this.existingHashtags.filter(option => {
          return option.indexOf(text.toLowerCase()) >= 0;
        });
      },
      getTags() {
        api.getAllHashtags().then(result => {
          this.existingHashtags = result.data;
        });
      },
      cleanTag: function(tag) {
        // if the current input is empty, emit empty list
        if (tag.length === 0) {
          this.$emit("input", []);
          return;
        }

        const regex = RegExp("^[a-zA-Z0-9_ ]{1,30}$");
        // get the latest hashtag input
        let latestHashtag = tag.pop();
        // if the latest hashtag contains only alphanumeric
        if (regex.test(latestHashtag)) {
          // replace all the spaces with underline
          // then push it back to tag list
          latestHashtag = latestHashtag.replace(/\s+/g, "_").toLowerCase();
          // remove tag instead if it already exists
          let existingIndex = tag.indexOf(latestHashtag);
          if (existingIndex == -1) {
            tag.push(latestHashtag);
          } else {
            tag.splice(existingIndex, 1);
          }

          this.invalidTag = false;
        } else {
          this.invalidTag = true;
        }

        // emit to parent
        this.$emit("input", tag);
      }
    }
  };
</script>

<style scoped>
</style>
