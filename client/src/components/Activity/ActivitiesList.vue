<template>
  <section class="hero">
    <b-field grouped>
      <p v-if="!newSearch" class="control">
        <b-button
          @click="setupNewSearch()"
          class="search-button"
          expanded
          id="new-search"
          type="is-primary"
        >New search</b-button>
      </p>
      <p class="control">
        <b-button
          @click="goToCreateActivity"
          class="is-primary is-outlined search-button"
          style="float: right"
        >Create Activity</b-button>
      </p>
    </b-field>
    <div class="field-indent" v-if="newSearch">
      <b-field expanded id="search-btn" label="Name:">
        <b-input icon="search" maxlength="200" v-model="activityName"></b-input>
      </b-field>

      <b-button
        :icon-right="filtersExpanded ? 'chevron-up' : 'chevron-down'"
        @click="filtersExpanded = !filtersExpanded"
        size="is-small"
      >More Filters</b-button>

      <div class="box" v-if="filtersExpanded">
        <div class="columns">
          <div class="column is-narrow" style="width: 8rem; text-align: right">
            <p>Hashtags:</p>
          </div>

          <div class="column">
            <HashtagInput
              :allowNew="true"
              :hasCounter="false"
              id="hashtag-input"
              v-model="hashtags"
            />
          </div>

          <div class="column is-narrow">
            <b-field>
              <b-select placeholder="Method" size="is-small" v-model="searchMethod">
                <option value="ALL">All of</option>
                <option value="ANY">Any of</option>
              </b-select>
            </b-field>
          </div>
        </div>

        <div class="columns">
          <div class="column is-narrow" style="width: 8rem; text-align: right">
            <p>Activity Types:</p>
          </div>

          <div class="column">
            <InputActivityTypes
              :placeholder="''"
              :required="false"
              :showMandatory="false"
              :withLabel="false"
              id="activity-types"
              v-model="activityTypes"
            />
          </div>

          <div class="column is-narrow">
            <b-field>
              <b-select placeholder="Method" size="is-small" v-model="typeMethod">
                <option value="ALL">All of</option>
                <option value="ANY">Any of</option>
              </b-select>
            </b-field>
          </div>
        </div>

        <div class="columns">
          <div class="column is-narrow" style="margin-left: 1rem; padding-right: 0">
            <b-checkbox v-model="viewportRestricted"></b-checkbox>
          </div>
          <div class="column">
            <p>Only display activities within the map's current viewing area</p>
          </div>
        </div>

        <div style="margin-bottom: 8px">
          <strong>Date Range</strong>
        </div>
        <div class="block">
          <b-radio native-value="Any Time" v-model="dateRange">Any Time</b-radio>
          <b-radio native-value="Today" v-model="dateRange">Today</b-radio>
          <b-radio native-value="Week" v-model="dateRange">This Week</b-radio>
          <b-radio native-value="Month" v-model="dateRange">This Month</b-radio>
          <b-radio native-value="Custom" v-model="dateRange">Custom</b-radio>
        </div>

        <div style="margin-bottom: 8px">
          <strong>Sort by</strong>
        </div>
        <b-select placeholder="Sort by" v-model="sort">
          <option value="date">Start Date</option>
          <option value="name">Name</option>
        </b-select>

        <strong v-if="dateRange === 'Custom'">Between:</strong>
        <div class="columns" v-if="dateRange === 'Custom'">
          <div class="column">
            <div>
              <p>Start Date</p>
            </div>
            <b-field :message="getErrorLabel(startDate)" id="date-start-label">
              <b-datepicker
                :date-formatter="dateFormatter"
                :date-parser="dateParser"
                editable
                icon="calendar-alt"
                id="date-start"
                placeholder="DD/MM/YYYY"
                v-model="startDate"
              ></b-datepicker>
            </b-field>
          </div>
          <div class="column">
            <div>
              <p>End Date</p>
            </div>
            <b-field id="date-end-label" :message="getErrorLabel(endDate)">
              <b-datepicker
                editable
                id="date-end"
                :date-formatter="dateFormatter"
                :date-parser="dateParser"
                v-model="endDate"
                placeholder="DD/MM/YYYY"
                icon="calendar-alt"
              ></b-datepicker>
            </b-field>
          </div>
        </div>
      </div>

      <b-button
        @click="() => {search(generateSearchParams()); hasSearched = true, newSearch = false, currentlySearching = true;}"
        icon-left="search"
        id="find-btn"
        size="is-small"
        type="is-primary"
      >Find activities!</b-button>
      <hr />
    </div>

    <div class="submit-button" v-if="searchResults.length">
      <p class="heading hint-text">Hint: double click an activity to learn more!</p>
    </div>

    <div
      v-if="this.currentlySearching && (this.hashtags.length > 0 || this.activityTypes.length > 0 || this.viewportRestricted || dateRange !== 'Any Time' || this.activityName)"
    >
      <p class="heading leader-filter-head">Active filters:</p>
      <div v-if="this.hashtags.length > 0">
        <p class="heading filter-head">Hashtags:</p>
        <div class="result-indent">
          <hashtag-display :hashtags="this.hashtags" />
        </div>
      </div>

      <div v-if="this.activityTypes.length > 0">
        <p class="heading filter-head">Activities:</p>
        <div class="result-indent">
          <b-taglist id="activity-display">
            <b-tag type="is-dark" v-for="types in this.activityTypes" :key="types">{{ types }}</b-tag>
          </b-taglist>
        </div>
      </div>
      <div v-if="this.activityName">
        <p class="heading filter-head">Name:</p>
        <div class="result-indent">{{activityName}}</div>
      </div>

      <div v-if="this.viewportRestricted">
        <p class="heading filter-head">Activity results are restricted to map viewing area</p>
      </div>

      <div v-if="dateRange == 'Week' || dateRange == 'Year' || dateRange == 'Month'">
        <p class="heading filter-head">Search results limited to this {{ this.dateRange }}</p>
      </div>
      <div v-else-if="dateRange == 'Today'">
        <p class="heading filter-head">Search results limited to today</p>
      </div>

      <div v-if="dateRange == 'Custom'">
        <p
          class="heading filter-head"
        >Search results limited betweeen {{ this.startDate.toLocaleDateString("en-US") }} and {{ this.endDate.toLocaleDateString("en-US") }}</p>
      </div>
    </div>

    <div :class="resultsFormatting" @scroll="handleScroll" id="results">
      <div
        :class="resultsColumnsFormatting"
        :key="activity.id"
        @mouseleave="resetCenterMapTimeout()"
        @mouseover="centerMapOnActivityAfterTimeout(activity)"
        v-for="(activity, index) in searchResults"
      >
        <div id="first-item" v-if="index === 0"></div>
        <div @dblclick="goToActivity(activity.id)">
          <ActivitySearchCard :activityId="activity.id" class="activity-search-card" />
        </div>
      </div>
    </div>

    <div v-if="newSearch" />
    <div class="has-text-centered" v-else-if="!searchResults.length">
      <span class="icon has-text-primary">
        <b-icon icon="fas fa-times"></b-icon>
      </span>
      No activities found
    </div>
  </section>
</template>

<script>
import HashtagInput from "@/components/HashtagInput.vue";
import api from "@/Api";
import dateHelper from "@/utils/dates/dates";
import { mapActions, mapState } from "vuex";
import { ToastProgrammatic as Toast } from "buefy";
import ActivitySearchCard from "./ActivitySearchCard";
import InputActivityTypes from "../ActivityTypesInput";
import validationHelper from "@/utils/validations/validations";
import HashtagDisplay from "../HashtagDisplay";

export default {
  name: "ActivitiesList",
  props: {
    bounds: { type: Object },
    load: { type: Boolean },
    isFullscreen: { type: Boolean }
  },

  components: {
    InputActivityTypes,
    ActivitySearchCard,
    HashtagInput,
    HashtagDisplay
  },
  data: function() {
    let now = new Date(); // current day and time
    let anyTime = new Date();
    anyTime.setDate(anyTime.getDate() + 99999);

    return {
      hasSearched: false, //Until the user searches, they can browse by moving the viewport
      hashtags: [],
      activityTypes: [],
      activityName: "",
      newSearch: false,
      totalElements: 0,
      page: 1,
      perPage: 20,
      searchMethod: "ALL",
      searchResults: [],
      selected: null,
      typeMethod: "ANY",
      filtersExpanded: false,
      viewportRestricted: false,
      dateRange: "Any Time",
      startDate: now,
      endDate: anyTime,
      hasDoneInitialSearch: false,
      centerMapTimeout: null,
      currentlySearching: false,
      sort: "",

      // Keeps track of activity ids in the page
      activityIds: new Set()
    };
  },

  mounted() {
    if (this.load) {
      this.reloadSearchParams();
      this.page = 1;
      while (this.page <= this.prevActivitySearch.page) {
        this.search(this.generateSearchParams());
      }
    }
  },

  computed: {
    ...mapState(["userId", "prevActivitySearch"]),
    resultsFormatting() {
      return {
        columns: this.isFullscreen,
        "is-multiline": this.isFullscreen
      };
    },
    resultsColumnsFormatting() {
      return {
        column: this.isFullscreen,
        "is-4": this.isFullscreen
      };
    }
  },

  watch: {
    // Run initial search when bounds are loaded in
    bounds() {
      if (!this.hasDoneInitialSearch) {
        this.search(this.generateSearchParams());
        this.hasDoneInitialSearch = true;
      } else if (!this.hasSearched && !this.newSearch) {
        this.page = 1;
        this.search(this.generateSearchParams());
      }
    },
    dateRange() {
      switch (this.dateRange) {
        case "Any Time": {
          this.startDate = new Date();
          this.endDate = new Date();
          this.endDate.setDate(this.endDate.getDate() + 99999);
          break;
        }
        case "Today": {
          this.startDate = new Date();
          this.endDate = new Date();
          this.endDate.setDate(this.endDate.getDate() + 1);
          break;
        }
        case "Week": {
          this.startDate = new Date();
          this.endDate = new Date();
          this.endDate.setDate(this.endDate.getDate() + 7);
          break;
        }
        case "Month": {
          this.startDate = new Date();
          this.endDate = new Date();
          this.endDate.setDate(this.endDate.getDate() + 30);
          break;
        }
        // Custom dates use the data manually input from the users
        case "Custom": {
          break;
        }
      }
    }
  },

  methods: {
    ...mapActions(["setPrevActivitySearch"]),
    dateFormatter: dateHelper.dateFormatter,
    dateParser: dateHelper.dateParser,
    getErrorLabel: validationHelper.getErrorLabel,

    handleScroll: function(el) {
      // Check if they have reached the bottom of results
      if (
        el.srcElement.offsetHeight + el.srcElement.scrollTop >=
        el.srcElement.scrollHeight
      ) {
        this.page++;
        this.search(this.generateSearchParams());
      }
    },

    setupNewSearch() {
      this.currentlySearching = false;
      this.newSearch = true;
      this.page = 1;
      this.searchResults = [];
      this.activityIds = new Set();
      this.$emit("results", []);
    },

    centerMapOnActivityAfterTimeout(activity) {
      if (this.centerMapTimeout) {
        clearTimeout(this.centerMapTimeout);
      }
      this.centerMapTimeout = setTimeout(() => {
        this.centerMapOnActivity(activity);
      }, 500);
    },

    resetCenterMapTimeout() {
      clearTimeout(this.centerMapTimeout);
      this.centerMapTimeout = null;
    },

    centerMapOnActivity(activity) {
      this.$emit("centerOnLocation", activity.location);
    },

    goToActivity(activityId) {
      this.$router.push({
        name: "ViewActivity",
        params: { activityId: activityId, showBackButton: true }
      });
    },
    goToCreateActivity() {
      this.$router.push({
        name: "createActivity",
        params: { userId: this.userId }
      });
    },
    generateSearchParams() {
      const params = {
        name: this.activityName,
        hashtag: this.hashtags.join(";"),
        hashtagSearchType: this.searchMethod === "ALL" ? "and" : "or",

        viewportRestricted: this.hasSearched ? this.viewportRestricted : true,

        activityType: this.activityTypes.join(";"),
        activityTypeSearchType: this.typeMethod === "ALL" ? "and" : "or",

        date_start: dateHelper.dateFormatterToYYYYMMDD(this.startDate),
        date_end: dateHelper.dateFormatterToYYYYMMDD(this.endDate),

        sw_lat: this.bounds.sw_lat,
        sw_lng: this.bounds.sw_lng,
        ne_lat: this.bounds.ne_lat,
        ne_lng: this.bounds.ne_lng,

        page: this.page - 1, // backend page starts from 0
        size: this.perPage,

        sort: this.sort // "name" or "date"
      };

      this.setPrevActivitySearch(params);
      return params;
    },

    reloadSearchParams() {
      this.activityName = this.prevActivitySearch.name;
      if (this.prevActivitySearch.hashtag) {
        this.hashtags = this.prevActivitySearch.hashtag.split(";");
      }

      if (this.prevActivitySearch.activityType) {
        this.activityTypes = this.prevActivitySearch.activityType.split(";");
      }

      this.viewportRestricted = this.prevActivitySearch.viewportRestricted;

      this.typeMethod =
        this.prevActivitySearch.activityTypeSearchType === "and"
          ? "ALL"
          : "ANY";
      this.searchMethod =
        this.prevActivitySearch.hashtagSearchType === "and" ? "ALL" : "ANY";

      this.dateRange = "Custom";

      if (this.prevActivitySearch.date_start) {
        this.startDate = new Date(this.prevActivitySearch.date_start);
        this.endDate = new Date(this.prevActivitySearch.date_end);
      }

      this.bounds.sw_lat = this.prevActivitySearch.sw_lat;
      this.bounds.sw_lng = this.prevActivitySearch.sw_lng;
      this.bounds.ne_lat = this.prevActivitySearch.ne_lat;
      this.bounds.ne_lng = this.prevActivitySearch.ne_lng;

      this.perPage = this.prevActivitySearch.size;
    },

    async search(params) {
      try {
        let response = await api.searchActivities(params);
        if (this.page == 1) {
          this.searchResults = [];
          this.activityIds.clear();
        }
        for (const a of response.data.results) {
          if (!this.activityIds.has(a.id)) {
            this.searchResults.push(a);
            this.activityIds.add(a.id);
          }
        }
        const resultsWithMarkers = this.searchResults.filter(m => {
          return m?.location?.latitude && m?.location?.longitude;
        });

        this.$emit(
          "updateMarkers",
          resultsWithMarkers.map(a => {
            if (a.location === null) return null;
            return {
              id: a.id,
              latitude: a.location.latitude,
              longitude: a.location.longitude,
              name: a.activity_name,
              imageType: "activity",
              description: a.description
            };
          })
        );
      } catch (err) {
        Toast.open({
          message: "Unable to find activities",
          type: "is-danger"
        });
      }
    }
  }
};
</script>

<style scoped>
.search-button {
  margin-left: 0.5rem;
  margin-bottom: 0.4rem;
}

.activity-search-card {
  margin-bottom: 1rem;
}

.field-indent {
  margin-left: 0.5rem;
}

p.hint-text {
  margin-left: 0.5rem;
}

p.filter-head {
  margin-left: 0.75rem;
}

p.leader-filter-head {
  font-size: 0.9rem;
  margin-left: 0.5rem;
}

.result-indent {
  margin-left: 1.25rem;
  margin-bottom: 0.5rem;
}

#results {
  margin-top: 2rem;
  overflow-y: scroll;
  max-height: 79vh;
  margin-bottom: 1rem;
}
</style>
