<template>
  <div class="modal-card">
    <div class="box">
      <div class="is-vcentered">
        <div class="columns">
          <div class="column is-half">
            <figure class="image is-256x256">
              <img :src="image" alt="Profile photo preview" />
            </figure>
          </div>
          <div class="column is-half">
            <cropper
              class="cropper"
              :src="getImage"
              :stencil-props="{
                  aspectRatio: 16/9
                }"
              @change="change"
            ></cropper>
            <p class="heading">Maximum file size is 5MB</p>
            <b-button icon-right="check" @click="saveImage()" type="is-success" expanded>
              <strong>Save</strong>
            </b-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import { Cropper } from "vue-advanced-cropper";
  import blob from "@/utils/converter/blob";

  export default {
    name: "CropActivityPhoto",
    props: ["photos"],

    computed: {
      image() {
        return this.croppedImg ? this.croppedImg : this.uploadedImageUrl;
      },

      getImage() {
        return this.uploadedImageUrl;
      }
    },

    data() {
      return {
        isImageSaving: false,
        croppedImg: null,
        yes: true,
        uploadedImageUrl: null,
        photosToSend: []
      };
    },

    mounted() {
      this.uploadedImageUrl = this.photos.images[this.photos.primary].data;
    },

    components: {
      Cropper
    },

    watch: {
      photos: function() {
        this.uploadedImageUrl = this.photos.images[this.photos.primary].data;
      }
    },

    methods: {
      dataURItoBlob: blob.dataURItoBlob,

      saveImage() {
        this.isImageSaving = true;
        this.photos.images[this.photos.primary].data = URL.createObjectURL(
          this.dataURItoBlob(this.croppedImg)
        );
        this.photosToSend = this.photos;
        this.$emit("photos", this.photosToSend);
        this.$emit("close");
      },

      change(event) {
        this.croppedImg = event.canvas.toDataURL();
      }
    }
  };
</script>

<style scoped>
</style>
