<template>
  <div :class="{box: userId}">
    <div class="field">
      <div class="block">
        <b-radio name="name" native-value="Profile photo" v-model="radio">Profile photo</b-radio>
        <b-radio name="name" native-value="Cover photo" v-model="radio">Cover photo</b-radio>
      </div>
    </div>

    <div class="">
      <div class="columns is-desktop">
        <div class="column is-half">
          <div class="img-container">
            <figure class="is-16by9">
              <img :src="coverImage" class="banner-img"/>
            </figure>

            <figure class="profile-img image">
              <img :src="primaryImage" alt="banner image" class="is-rounded"/>
            </figure>

          </div>
        </div>

        <div class="column is-half">
          <b-field v-if="!uploadedImageUrl">
            <b-upload @input="loadImage" drag-drop v-model="uploadedImage">
              <section class="section">
                <div class="content has-text-centered">
                  <p>
                    <b-icon icon="upload" size="is-large"></b-icon>
                  </p>
                  <p>Click to upload, or drag your new {{ radio.toLowerCase() }} here</p>
                </div>
              </section>
            </b-upload>
          </b-field>

          <cropper
            :src="uploadedImageUrl"
            :stencil-props="{
                aspectRatio: photoAspectRatio
              }"
            @change="change"
            class="cropper"
            stencil-component="rectangle-stencil"
          ></cropper>

          <div class="columns" id="action-btns">
            <div class="column" v-if="uploadedImageUrl">
              <b-button
                :loading="isDeletingProfileImg"
                @click="reset()"
                class="spacer"
                expanded
                icon-left="times"
                type="is-light"
              >
                <strong>Cancel</strong>
              </b-button>

            </div>
            <div class="column" v-if="uploadedImageUrl">
              <b-button
                :loading="isImageSaving"
                @click="saveImage()"
                expanded
                icon-right="check"
                type="is-success"
                v-if="uploadedImageUrl"
              >
                <strong>Save</strong>
              </b-button>
            </div>
          </div>
        </div>
      </div>

      <div class="field is-grouped is-grouped-right">

        <div class="control" v-if="userId">
          <b-button
            :loading="isDeletingProfileImg"
            @click="confirmDelete('Profile photo')"
            class="spacer"
            expanded
            icon-left="trash"
            type="is-danger"
          >
            <strong>Delete Profile Image</strong>
          </b-button>
        </div>
        <div class="control" v-if="userId">

          <b-button
            :loading="isDeletingCoverImg"
            @click="confirmDelete('Cover photo')"
            expanded
            icon-left="trash"
            type="is-danger"
          >
            <strong>Delete Cover Image</strong>
          </b-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import {Cropper} from "vue-advanced-cropper";
  import {ToastProgrammatic as Toast} from "buefy";
  import api from "@/Api";
  import defaultPrimaryImage from "@/assets/backpack_hiker.png"
  import defaultCoverImage from "@/assets/activity-default.jpg"

  export default {
    name: "ImageInput",
    props: ["userId", "primaryImg", "coverImg"],

    components: {
      Cropper
    },

    computed: {
      primaryImage() {
        if (this.croppedPrimaryImage) return this.croppedPrimaryImage;
        if (this.primaryImg) return this.primaryImg;

        let defaultImage = defaultPrimaryImage;
        if (this.userId) {
          defaultImage = `${process.env.VUE_APP_SERVER_ADD}/profiles/${this.userId}/photos/primary?rnd=${this.cacheKey}`
        }

        return this.croppedPrimaryImage
          ? this.croppedPrimaryImage
          : defaultImage
      },

      coverImage() {
        if (this.croppedCoverImage) return this.croppedCoverImage;
        if (this.coverImg) return this.coverImg;

        let defaultImage = defaultCoverImage;
        if (this.userId) {
          defaultImage = `${process.env.VUE_APP_SERVER_ADD}/profiles/${this.userId}/photos/cover?rnd=${this.cacheKey}`
        }

        return this.croppedCoverImage
          ? this.croppedCoverImage
          : defaultImage
      },

      photoAspectRatio() {
        return this.radio === "Profile photo" ? 1 : 796 / 395 // banner image ratio;
      }
    },

    watch: {
      radio: function () {
        if (this.radio === "Profile photo") {
          this.croppedCoverImage = null;
        } else {
          this.croppedPrimaryImage = null;
        }
      }
    },

    data() {
      return {
        isImageSaving: false,
        uploadedImage: null,
        uploadedImageUrl: null,
        radio: "Profile photo",
        isDeletingProfileImg: false,
        isDeletingCoverImg: false,

        croppedPrimaryImage: null,
        croppedCoverImage: null,
        cacheKey: new Date().getTime()
      };
    },

    methods: {
      async deleteProfileImage() {
        this.isDeletingProfileImg = true;
        await api.deletePrimaryImage(this.userId);
        this.reset();
        this.isDeletingProfileImg = false;
      },

      async deleteCoverImage() {
        this.isDeletingCoverImg = true;
        await api.deleteCoverImage(this.userId);
        this.reset();
        this.isDeletingCoverImg = false;
      },

      confirmDelete(imageType) {
        this.$buefy.dialog.confirm({
          title: 'Deleting photo',
          message: `Are you sure you want to <b>delete</b> your ${imageType.toLowerCase()}? This action cannot be undone.`,
          confirmText: 'Delete photo',
          type: 'is-danger',
          hasIcon: true,
          onConfirm: () => imageType === "Profile photo" ? this.deleteProfileImage() : this.deleteCoverImage()
        })
      },

      async saveImage() {
        if (!this.userId) {
          if (this.croppedPrimaryImage) this.$emit("primaryImg", this.croppedPrimaryImage);
          if (this.croppedCoverImage) this.$emit("coverImg", this.croppedCoverImage);
          this.reset();
          return;
        }
        this.isImageSaving = true;
        try {
          let photo;
          if (this.radio === "Profile photo") {
            photo = await fetch(this.croppedPrimaryImage).then(r => r.blob());
            await api.uploadUserHeroImage(
              this.userId,
              photo,
              this.uploadedImage.type
            );
          } else {
            photo = await fetch(this.croppedCoverImage).then(r => r.blob());
            await api.uploadUserCoverImage(
              this.userId,
              photo,
              this.uploadedImage.type
            );
          }
          Toast.open({
            message: "Image saved",
            type: "is-success"
          });
        } catch (err) {
          if (err.response.status === 400) {
            Toast.open({
              message: err.response.data.errors[0],
              type: "is-danger"
            });
          }

          Toast.open({
            message: "An error occurred when trying to save the image",
            type: "is-danger"
          });
        } finally {
          this.reset();
        }
      },

      reset() {
        this.isImageSaving = false;
        this.uploadedImage = null;
        this.uploadedImageUrl = null;
        this.radio = "Profile photo";
        this.isDeletingProfileImg = false;
        this.isDeletingCoverImg = false;
        this.croppedPrimaryImage = null;
        this.croppedCoverImage = null;
        this.cacheKey = new Date().getTime()
      },

      loadImage() {
        let acceptedFormats = ["jpg", "jpeg", "gif", "png"];
        if (
          acceptedFormats.includes(
            this.uploadedImage.name.split(".")[
            this.uploadedImage.name.split(".").length - 1
              ].toLowerCase()
          )
        ) {
          this.uploadedImageUrl = URL.createObjectURL(this.uploadedImage);
        } else {
          Toast.open({
            message: "This file type is not supported",
            type: "is-danger"
          });
        }
      },

      change(event) {
        if (!("canvas" in event)) return;
        if (this.radio === "Profile photo") {
          this.croppedPrimaryImage = event.canvas.toDataURL();
        } else {
          this.croppedCoverImage = event.canvas.toDataURL();
        }

      }
    }
  };
</script>

<style scoped>
  .spacer {
    margin-bottom: 0.5rem;
  }

  .img-container {
    position: relative;
    text-align: center;
    height: auto;
    width: 100%;
  }

  .img-container .profile-img {
    width: 25%;
    position: absolute;
    bottom: 10%;
    left: 50%;
    transform: translate(-50%, -50%);
  }

  #action-btns {
    margin-top: -1rem;
  }

  .banner-img {
    margin-top: 0rem;
    max-width: 100%;
    height: 220px;
  }
</style>
