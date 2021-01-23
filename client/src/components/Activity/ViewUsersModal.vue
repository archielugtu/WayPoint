<template>
  <div class="modal-card">
    <header class="modal-card-head">
      <p class="modal-card-title">Viewing {{ usersType }}</p>
    </header>
    <section class="modal-card-body">
      <b-table
        :data="data"
        :columns="columns"
        :loading="loading"
        :selected.sync="selected"
        hoverable
        paginated
        backend-pagination
        :total="totalUsers"
        :per-page="perPage"
        @page-change="onPageChange"
        @dblclick="viewUser"
        :current-page="page + 1"
        backend-sorting
      ></b-table>
    </section>
    <footer class="modal-card-foot">
      <b-button v-if="usersType === 'participants' && creator"
                class="button is-primary"
                @click="promoteToOrganiser(selected)"
      ><strong>Promote to Organiser Role</strong>
      </b-button>
      <b-button v-if="usersType === 'organisers' && creator"
                class="button is-danger"
                @click="demoteToParticipant(selected)"
      ><strong>Demote to Participant Role</strong>
      </b-button>
    </footer>
  </div>
</template>

<script>
  import api from "@/Api";
  import { ToastProgrammatic as Toast } from "buefy";
  export default {
    name: "ViewUsersModal",
    props: ["activityId", "creator", "usersType", "creatorId", "toggleRefresh"],
    watch: {
      usersType: function() {
        this.localUsersType = this.usersType;
        this.refreshTable();
      },
      toggleRefresh: function() {
        this.localUsersType = this.usersType;
        this.reset();
      }

    },
    components: {},
    data: function() {
      return {
        data: [],
        localUsersType: this.usersType,
        loading: false,
        page: 0,
        perPage: 12,
        totalUsers: 0,
        selected: null,
        columns: [
          {
            field: "num",
            label: "Number",
            width: "40",
            numeric: true,
            centered: true
          },
          {
            field: "name",
            label: "Full name"
          }
        ]
      };
    },
    mounted() {
      this.reset();
    },
    methods: {
      reset() {
        this.page = 0;
        this.refreshTable();
      },
      viewUser(rowData) {
        this.$router.push({name: "profile", params: {userId: rowData.userId}})
      },
      onPageChange: function(newPage) {
        this.page = newPage - 1;
        this.refreshTable();
      },
      refreshTable: async function() {
        let response;
        const params = {
          page: this.page,
          size: this.perPage
        };
        try {
          this.loading = true;
          if (this.localUsersType === "organisers") {
            response = await api.fetchActivityOrganisers(this.activityId, params);
            this.populateTable(response.data.organisers);
          } else if (this.localUsersType === "participants") {
            response = await api.fetchActivityParticipants(
              this.activityId,
              params
            );
            this.populateTable(response.data.participants);
          } else if (this.localUsersType === "followers") {
            response = await api.fetchActivityFollowers(this.activityId, params);
            this.populateTable(response.data.followers);
          }
          this.followers = response.data.followers;
          this.totalUsers = response.data.total_pages * this.perPage;
        } catch (err) {
          this.$buefy.toast.open({
            duration: 2000,
            message: "An error has occurred, unable to refresh table",
            type: "is-danger"
          });
        } finally {
          this.loading = false;
        }
      },
      populateTable: function(newData) {
        this.data = [];
        let num = this.page * this.perPage + 1;
        newData.forEach(d => {
          this.data.push({
            num: num,
            userId: d.userId,
            name: d.name
          });
          num++;
        });
      },
      promoteToOrganiser: async function (selected) {
        try {
          if (selected === null) return ;
          this.loading = true;
          await api.creatorEditUserRole(this.creatorId, this.activityId, selected.userId, {
              role: "organiser"
          });
          await this.refreshTable();
          this.$emit("promote", {name: selected.name, userId: selected.userId});

        } catch (err) {
          Toast.open({
            message: "Unable to promote to organiser",
            type: "is-danger"
          });
        } finally {
          this.loading = false;
        }
      },

      demoteToParticipant: async function (selected) {
        try {
          if (selected === null) return ;
          this.loading = true;
          await api.creatorEditUserRole(this.creatorId, this.activityId, selected.userId, {
            role: "participant"
          });
          await this.refreshTable();
          this.$emit("demote", {name: selected.name, userId: selected.userId});
        } catch (err) {
          Toast.open({
            message: "Unable to demote to participant",
            type: "is-danger"
          });
        } finally {
          this.loading = false;
        }
      }
    }
  };
</script>

<style scoped>
</style>
