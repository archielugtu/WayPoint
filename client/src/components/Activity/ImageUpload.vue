<template>
  <section class="section">
    <div class="container box">
      <b-loading :is-full-page="false" :active.sync="loading" :can-cancel="false">
        <b-icon pack="fas" icon="sync-alt" size="is-large" custom-class="fa-spin"></b-icon>
      </b-loading>
      <div>
        <p class="title">
          Upload some
          <em class="fas fa-camera-retro"></em> images for your activity
        </p>
        <p class="heading">Hot tip: Click on an image to set it as the activity's primary image!</p>
        <p class="heading">Maximum file size is 5MB</p>
        <hr />
      </div>
      <div>
        <CreateOrEditActivityPhoto v-model="photos" :refresh="isModalOpen" />
        <b-button
          v-if="photos.images.length"
          expanded
          @click="submit"
          id="upload-btn"
          size="is-medium"
          type="is-primary"
        >Post</b-button>
        <b-modal :active.sync="isModalOpen" has-modal-card can-cancel trap-focus aria-modal>
          <CropActivityModal
            v-bind="photos"
            :toggleRefresh="toggle"
            :photos="photos"
            @photos="photos=$event"
            @close="handleCloseModal"
          ></CropActivityModal>
        </b-modal>
      </div>
    </div>
  </section>
</template>

<script>
  import api from "@/Api";
  import blobHelper from "@/utils/converter/blob";
  import CreateOrEditActivityPhoto from "@/components/Activity/CreateOrEditActivityPhoto";
  import CropActivityModal from "@/components/CropActivityPhoto";

  export default {
    name: "ImageUpload",
    components: { CreateOrEditActivityPhoto, CropActivityModal },
    props: ["activityId"],

    data() {
      return {
        toggle: false,
        isModalOpen: false,
        photos: {
          images: [],
          primary: null
        },
        loading: false
      };
    },

    mounted() {
      this.loadPhotos();
    },

    methods: {
      base64toBlob: blobHelper.base64toBlob,

      submit() {
        if (this.photos.images.length) {
          this.toggle = true;
          this.isModalOpen = true;
        }
      },

      async loadPhotos() {
        const result = await api.fetchActivityPhotos(this.activityId);
        let i = 0;
        for (const p of result.data.photos) {
          let photoId = p.id;
          if (p.primary) {
            this.photos.primary = i;
          }
          let photo = await api.fetchActivityPhoto(this.activityId, photoId);
          let imageType = photo.headers["content-type"];
          let blob = this.base64toBlob(photo.data, imageType);

          this.photos.images.push({
            data: URL.createObjectURL(blob),
            type: imageType,
            id: photoId
          });
          i++;
        }
      },

      async uploadPhotos() {
        let primaryImgId = null;
        for (let i = 0; i < this.photos.images.length; i++) {
          let p = this.photos.images[i];
          // Found our primary image
          if (this.photos.primary != null && i === this.photos.primary) {
            // If we have uploaded the primary photo before, delete it
            if (p.id) {
              await this.deleteSinglePhoto(p.id);
            }

            // Upload the photo as a new photo since it might be cropped
            primaryImgId = await this.uploadSinglePhoto(p.data, p.type);
            await this.setPrimaryImage(primaryImgId);
          } else if (!p.id) {
            await this.uploadSinglePhoto(p.data, p.type);
          }
        }
      },

      async uploadSinglePhoto(photoObjUrl, imageType) {
        let photoBlob = await fetch(photoObjUrl).then(r => r.blob());

        let res = await api.uploadActivityPhoto(
          this.activityId,
          photoBlob,
          imageType
        );
        return res.data;
      },

      async deleteSinglePhoto(photoId) {
        await api.deleteActivityImage(this.activityId, photoId);
      },

      async setPrimaryImage(primaryImgId) {
        await api.setActivityImageAsPrimary(this.activityId, primaryImgId);
      },

      async handleCloseModal() {
        this.isModalOpen = false;
        this.loading = true;
        await this.uploadPhotos();
        this.$router.push({
          name: "ViewActivity",
          params: { activityId: this.activityId }
        });
        this.loading = false;
      }
    }
  };
</script>

<style scoped>
  .title {
    font-size: 1.8rem;
  }

  #upload-btn {
    margin-top: 3rem;
  }
</style>
