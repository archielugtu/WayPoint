<template>
  <div>
    <!-- head count text -->

    <b-field v-if="isCreator">
      <template slot="label">Activity Visibility</template>

      <b-dropdown v-model="visibility" aria-role="list">
        <button class="button" type="button" slot="trigger">
          <template v-if="visibility === 'public'">
            <b-icon icon="globe"></b-icon>
          </template>
          <template v-else-if="visibility === 'restricted'">
            <b-icon icon="users"></b-icon>
          </template>
          <template v-else>
            <b-icon icon="lock"></b-icon>
          </template>
          <span>{{ capitalize(this.visibility) }}</span>
          <b-icon icon="caret-down"></b-icon>
        </button>

        <b-dropdown-item value="public" aria-role="listitem">
          <div class="media">
            <b-icon class="media-left" icon="globe"></b-icon>
            <div class="media-content">
              <h3>Public</h3>
              <small>Everyone can see</small>
            </div>
          </div>
        </b-dropdown-item>

        <b-dropdown-item value="restricted" aria-role="listitem">
          <div class="media">
            <b-icon class="media-left" icon="users"></b-icon>
            <div class="media-content">
              <h3>Restricted</h3>
              <small>Only specific people can see</small>
            </div>
          </div>
        </b-dropdown-item>

        <b-dropdown-item value="private" aria-role="listitem">
          <div class="media">
            <b-icon class="media-left" icon="lock"></b-icon>
            <div class="media-content">
              <h3>Private</h3>
              <small>Only I can see</small>
            </div>
          </div>
        </b-dropdown-item>
      </b-dropdown>
    </b-field>
    <div v-if="this.visibility.toLowerCase() != 'private'">
      <div class="has-text-centered">{{ headcountText }}</div>
      <hr />
      <EmailSharing v-if="isCreator" :emails="users" @firstUpdate="onRoleUpdate()" />
    </div>
    <div class="box">
      <div v-if="this.visibility.toLowerCase() == 'private'">
        <p class="has-text-centered">Private activities cannot include other users</p>
      </div>
      <div v-else>
        <b-table :loading="loading" :data="users" paginated :total="totalUsers" :per-page="perPage">
          <template slot-scope="props">
            <b-table-column
              field="full_name"
              width="200"
              label="Full name"
            >{{ props.row.full_name }}</b-table-column>
            <b-table-column field="email" width="200" label="Email Address">{{ props.row.email }}</b-table-column>
            <b-table-column field="role" width="200" label="Existing Role">{{ props.row.role }}</b-table-column>
            <b-table-column label="New Role">
              <b-field>
                <b-radio-button
                  @click.native="onRoleUpdate()"
                  v-model="props.row.newRole"
                  native-value="none"
                  type="is-danger"
                >Remove</b-radio-button>
                <b-radio-button
                  @click.native="onRoleUpdate()"
                  v-model="props.row.newRole"
                  native-value="follower"
                  type="is-success"
                >Follower</b-radio-button>
                <b-radio-button
                  @click.native="onRoleUpdate()"
                  v-model="props.row.newRole"
                  native-value="participant"
                >Participant</b-radio-button>
                <b-radio-button
                  @click.native="onRoleUpdate()"
                  v-model="props.row.newRole"
                  native-value="organiser"
                >Organiser</b-radio-button>
              </b-field>
            </b-table-column>
          </template>
        </b-table>
      </div>
    </div>
  </div>
</template>

<script>
/**
 * Handles input for sharing activity
 * To retrieve the added users, listen to event 'input'
 */
  import api from "@/Api";
  import EmailSharing from "@/components/Activity/EmailSharing";
  export default {
    name: "Sharing",
    props: ["activityId", "payload", "parentPage", "isCreator"],
    components: {
      EmailSharing,
    },
    data: function () {
      return {
        users: [],
        // pagination variables
        perPage: 15,
        page: 0,
        totalUsers: 0,
        loading: false,

        organisers: -1,
        participants: -1,
        followers: -1,
        visibility: "public",
        previousVisibility: "public",

        isUpdated: false,
      };
    },
    computed: {
      headcountText() {
        return (
          `This activity has ${this.organisers} organisers, ` +
          `${this.participants} participants and ${this.followers} followers.`
        );
      },
    },
    mounted() {
      this.loadHeadcount();
      this.loadRelatedUsers();
    },
    watch: {
      parentPage(val) {
        // Only emit if changing off this page
        if (val !== 1) {
          this.emitData();
        }
      },
      visibility() {
        if (this.visibility !== this.previousVisibility &&
            (this.previousVisibility === "public" || this.visibility === "private")) {
          this.confirmVisibility();
        } else {
          this.previousVisibility = this.visibility;
          this.onRoleUpdate();
        }
      },
    },
    methods: {
      confirmVisibility() {
        this.$buefy.dialog.confirm({
          title: "Restricting Activity Visibility",
          message:
            "Are you sure you want to change the visibility of this activity?",
          confirmText: "Change Visibility",
          type: "is-danger",
          hasIcon: true,
          onConfirm: () => {
            this.previousVisibility = this.visibility;
            this.onRoleUpdate();
          },
          onCancel: () => {
            this.visibility = this.previousVisibility;
          },
        });
      },
      capitalize(s) {
        return s.charAt(0).toUpperCase() + s.slice(1);
      },
      onRoleUpdate() {
        if (!this.isUpdated) {
          this.isUpdated = true;
          this.$emit("firstUpdate", true);
        }
      },
      emitData() {
        let formattedUsers = [];
        this.users.forEach((u) => {
          const data = {
            email: u.email,
            role: u.newRole.toLowerCase(),
          };
          formattedUsers.push(data);
        });
        let payload = {
          visibility: this.visibility.toLowerCase(),
          accessors:
            this.visibility.toLowerCase() === "private" ? [] : formattedUsers,
        };
        this.$emit("updateUsers", payload);
      },
      async loadHeadcount() {
        try {
          let result = await api.fetchActivityHeadcounts(this.activityId);
          this.organisers = result.data.total_organisers;
          this.participants = result.data.total_participants;
          this.followers = result.data.total_followers;
          this.previousVisibility = result.data.visibility.toLowerCase();
          this.visibility = result.data.visibility.toLowerCase();
        } catch (err) {
          this.$buefy.toast.open({
            duration: 2000,
            message: "Unable to retrieve user counts for this activity",
            type: "is-warning",
          });
        }
      },
      async loadRelatedUsers() {
        let currPage = 0;
        let result;
        try {
          this.loading = true;
          do {
            result = await api.fetchUsersRelatedToActivity(this.activityId, {
              page: currPage++,
              size: this.perPage,
            });
            result.data.content.forEach((i) => {
              i.full_name = i.first_name + " " + i.last_name;
              i.newRole = i.role.toLowerCase();
              this.users.push(i);
            });
            this.totalUsers += result.data.totalElements;
          } while (result.data.numberOfElements); // keep doing this until we find no more users
        } catch (err) {
          this.$buefy.toast.open({
            duration: 2000,
            message: "Unable to retrieve users involved in this activity",
            type: "is-warning",
          });
        } finally {
          this.loading = false;
        }
      },
    },
  };
</script>

<style scoped>
</style>
