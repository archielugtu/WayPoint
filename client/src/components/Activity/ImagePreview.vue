<template>
  <div>
    <div class="columns" v-if="userHasEditPermissions">
      <div class="column">
        <b-button
          expanded
          tag="router-link"
          :to="{name: 'ImageUpload', activityId: activityId}"
          type="is-primary"
        >Upload images</b-button>
      </div>
      <div v-if="deleteList.length" class="column">
        <b-button
          expanded
          v-if="['organiser', 'creator'].includes(userRole) && deleteList.length"
          @click="deleteImages"
          type="is-danger"
        >Delete images</b-button>
      </div>
    </div>

    <hr v-if="images.length && userHasEditPermissions"/>
    <div v-if="images.length">
      <p class="heading" v-if="userHasEditPermissions">
        To remove a picture, select it and click the delete button
      </p>
      <div v-for="(row, i) of Array(numOfRows).keys()" :key="i" class="columns">
        <div
          class="column"
          v-for="(col, j) in Array(numImagesPerRow).keys()"
          :key="i * numImagesPerRow + j"
        >
          <img
            @click="addToDeleteList(i * numImagesPerRow + j)"
            :class="{'to-be-deleted': deleteList.includes(i * numImagesPerRow + j)}"
            v-if="i * numImagesPerRow + j < images.length"
            :src="images[i * numImagesPerRow + j].image"
          />
        </div>
      </div>
    </div>
    <div v-else>
      <p
        v-if="userHasEditPermissions"
        class="has-text-centered"
      >Click the upload button above to start uploading!</p>
      <p v-else class="has-text-centered">No photos for this activity so far</p>
    </div>
  </div>
</template>

<script>
  import api from "@/Api";

  export default {
    name: "ImagePreview",
    props: ["activityId", "userRole"],

    data() {
      return {
        numImagesPerRow: 3,
        images: [],
        deleteList: [],
      };
    },

    computed: {
      numOfRows() {
        return Math.ceil(this.images.length / this.numImagesPerRow);
      },
      userHasEditPermissions() {
        return  ['organiser', 'creator'].includes(this.userRole)
      }
    },

    mounted() {
      this.loadImages();
    },

    methods: {
      addToDeleteList(imageIdx) {
        if (this.deleteList.includes(imageIdx)) {
          this.deleteList.splice(this.deleteList.indexOf(imageIdx), 1);
        } else {
          this.deleteList.push(imageIdx);
        }
      },

      async loadImages() {
        this.images = []
        let result = await api.fetchActivityPhotos(this.activityId);
        for (const i of result.data.photos) {
          let imageId = i.id;
          let image = await api.fetchActivityPhoto(this.activityId, imageId);
          let imageType = image.headers["content-type"];
          this.images.push({
            image: "data:" + imageType + ";base64," + image.data,
            id: imageId
          });
        }
      },

      async deleteImages() {
        for (const i of this.deleteList) {
          await api.deleteActivityImage(this.activityId, this.images[i].id)
        }
        this.deleteList = [];
        await this.loadImages()
      }
    }
  };
</script>

<style scoped>
  img {
    float: left;
    width: 100%;
    height: 100%;
    object-fit: cover;
    box-sizing: border-box;
  }

  .to-be-deleted {
    border: 3px solid red;
  }
</style>
