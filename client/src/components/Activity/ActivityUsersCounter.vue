<template>
  <div>
      <div class="box">
        <b-field :label="'Organisers: ' + totalOrganisers"></b-field>
        <div class="block">
          <div v-for="(organiser, index) in organisers" :key="organiser.id">
            <div v-if="index < maxItems">
              <b-icon icon="user" size="is-small"></b-icon>
              {{ organiser.name }}
            </div>
          </div>
          <b-button
            size="is-small"
            @click="viewUsers('organisers')"
            class="more-btn"
            v-if="totalOrganisers !== 0"
          >View all</b-button>
        </div>
      </div>
      <div class="box">
        <b-field :label="'Participants: ' + totalParticipants"></b-field>
        <div class="block">
          <SingleCounter :users="participants" :totalUsers="totalParticipants" />
        </div>
        <b-button
          size="is-small"
          @click="viewUsers('participants')"
          class="more-btn"
          v-if="totalParticipants !== 0"
        >View all</b-button>
      </div>
      <div class="box">
        <b-field :label="'Followers: ' + totalFollowers"></b-field>
        <div class="block">
          <SingleCounter :users="followers" :totalUsers="totalFollowers" />
        </div>
        <b-button
          size="is-small"
          @click="viewUsers('followers')"
          class="more-btn"
          v-if="totalFollowers !== 0"
        >View all</b-button>
      </div>

    <b-modal
      :active.sync="isModalOpen"
      @close="toggle = !toggle"
      has-modal-card
      can-cancel
      trap-focus
      :destroy-on-hide="false"
      aria-modal
    >
      <ViewUsersModal
        :toggleRefresh="toggle"
        :activityId="activityId"
        :creator="userId === creatorId"
        :creatorId="creatorId"
        @promote="addUserToOrganiserList"
        @demote="addUserToParticipantList"
        :usersType="usersType">
      </ViewUsersModal>
    </b-modal>
  </div>
</template>

<script>
  import SingleCounter from "@/components/Activity/SingleCounter";
  import ViewUsersModal from "@/components/Activity/ViewUsersModal";
  import api from "@/Api";
  import { ToastProgrammatic as Toast } from "buefy";

  export default {
    name: "ActivityUsersCounter",
    props: ["activityId", "userId", "creatorId", "refreshUserCount"],
    components: {
      SingleCounter,
      ViewUsersModal
    },
    watch: {
      refreshUserCount: function() {
        this.fetchUserCount();
      }
    },
    data: function() {
      return {
        toggle: false,
        usersType: "organisers",
        isModalOpen: false,
        maxItems: 10,
        totalOrganisers: 0,
        organisers: [],
        totalParticipants: 0,
        participants: [],
        totalFollowers: 0,
        followers: [],
        visibility: "public"
      };
    },

    methods: {
      viewUsers(usersType) {
        this.usersType = usersType;
        this.isModalOpen = true;
      },

      addUserToOrganiserList: function (selected) {
        for (let i = 0; i < this.participants.length; i++) {
          if (this.participants[i].name === selected.name) {
            this.participants.splice(i, 1);
            this.totalParticipants--;
          }
        }
        this.organisers.push(selected);
        this.totalOrganisers++;
      },

      addUserToParticipantList: function (selected) {
        for (let i = 0; i < this.organisers.length; i++) {
          if (this.organisers[i].name === selected.name) {
            this.organisers.splice(i, 1);
            this.totalOrganisers--;
          }
        }
        this.participants.push(selected);
        this.totalParticipants++;
      },

      async fetchUserCount() {
        let response;
        const params = {
          page: 0,
          size: 40
        };
        try {
          response = await api.fetchActivityHeadcounts(this.activityId);
          this.totalFollowers = response.data.total_followers;
          this.totalParticipants = response.data.total_participants;
          this.totalOrganisers = response.data.total_organisers;

          response = await api.fetchActivityFollowers(this.activityId, params);
          this.followers = response.data.followers;

          response = await api.fetchActivityParticipants(this.activityId, params);
          this.participants = response.data.participants;

          response = await api.fetchActivityOrganisers(this.activityId, params);
          this.organisers = response.data.organisers;
        } catch (err) {
          Toast.open({
            message: "An error has occurred, unable to retrieve users involved",
            type: "is-danger"
          });
        }
      }
    },

    async created() {
      this.fetchUserCount();
    },
  };
</script>

<style scoped>
  .more-btn {
    margin-top: 0.5rem;
  }
</style>
