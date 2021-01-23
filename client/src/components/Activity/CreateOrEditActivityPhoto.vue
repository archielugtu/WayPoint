<template>
  <div>
    <div class="is-vcentered">
      <div class="columns is-multiline">
        <div class="column is-3" v-for="(image, index) in images" :key="image.data">
          <b-upload
            v-if="image.data == null"
            multiple
            v-model="uploadedImage"
            @input="loadImage"
            drag-drop
          >
            <div class="upload-box content has-text-centered">
              <p>
                <b-icon icon="plus" size="is-large"></b-icon>
              </p>
              <p>Click to start uploading</p>
            </div>
          </b-upload>

          <div
            v-else
            class="image-container is-center"
            :class="{'is-primary-img': index === primaryImgIdx}"
          >
            <img
              @click="makeImgPrimary(index)"
              :src="images[index].data"
              alt="Activity photo preview"
              class="images"
            />
            <div class="remove-btn">
              <button @click="removeImage(index)" class="button is-small is-danger">
                <span class="icon is-small">
                  <em class="fas fa-times"></em>
                </span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import { ToastProgrammatic as Toast } from "buefy";

  export default {
    name: "createOrEditActivityPhoto",
    props: [
      "value", // required for v-model binding
      "refresh"
    ],

    data() {
      return {
        numImagesPerRow: 5,
        uploadedImage: null,
        primaryImgIdx: null,
        yes: true,
        images: [{ data: null }] // null is used as a placeholder to place the b-upload element, dont remove
      };
    },

    watch: {
      refresh: {
        handler: "refreshCroppedImg"
      },
      value: {
        deep: true,
        handler: "initialiseImages"
      }
    },

    computed: {
      numOfRows() {
        return Math.ceil(this.images.length / this.numImagesPerRow);
      }
    },

    methods: {
      isValidRowAndCol(row, column) {
        return row * this.numImagesPerRow + column < this.images.length;
      },
      imageExists(row, column) {
        return this.images[row * this.numImagesPerRow + column].data;
      },

      refreshCroppedImg() {
        this.images.splice(
          this.primaryImgIdx,
          1,
          this.value.images[this.primaryImgIdx - 1]
        );
      },

      initialiseImages() {
        this.images = [];
        this.value.images.forEach(i => {
          this.images.push(i);
        });
        // insert null at position 0
        // this is used to render the upload button
        if (!this.images.length || (this.images.length && this.images[0].data != null)) {
          this.images.splice(0, 0, { data: null });
        }
        if (this.value.primary != null) {
          this.primaryImgIdx = this.value.primary + 1;
        }
      },

      emitData() {
        this.$emit("input", {
          // remove null placeholder before emitting image,
          // but keep it in the original array
          images: this.images.filter(i => i.data),
          primary: this.primaryImgIdx ? this.primaryImgIdx - 1 : null
        });
      },

      loadImage() {
        this.uploadedImage.forEach(img => this.loadSingleImage(img));
        this.uploadedImage = [];
        if (this.primaryImgIdx == null) {
          this.makeImgPrimary(1);
        }
        this.emitData();
      },

      loadSingleImage(img) {
        const imgUrl = URL.createObjectURL(img);
        let acceptedFormats = ["jpg", "jpeg", "gif", "png"];
        if (
          acceptedFormats.includes(
            img.name.split(".")[img.name.split(".").length - 1].toLowerCase()
          )
        ) {
          this.images.push({ data: imgUrl, type: img.type });
        } else {
          Toast.open({
            message: "This file type is not supported",
            type: "is-danger"
          });
        }
      },

      makeImgPrimary(imgIdx) {
        if (this.images.length > 1) {
          this.primaryImgIdx = imgIdx;
          this.$buefy.toast.open({
            message: "Primary image set!",
            duration: 500,
            type: "is-success"
          });
        }
        this.emitData();
      },

      removeImage(index) {
        if (this.images.length === 1) {
          this.primaryImgIdx = null;
        } else if (
          index === this.primaryImgIdx &&
          index === this.images.length - 1
        ) {
          this.primaryImgIdx--;
        }
        if (this.primaryImgIdx === 0) {
          this.primaryImgIdx = null;
        }
        this.images.splice(index, 1);

        this.emitData();
      }
    }
  };
</script>

<style scoped>
  .image-container {
    width: 225px;
    height: 127px;
    border: 1px solid black;
  }

  .upload-box {
    width: 215px;
    height: 117px;
    margin: auto;
    padding: 10% 0 0 0;
  }

  .is-primary-img {
    border: 5px solid #228b22;
  }

  .image-container:hover .images {
    opacity: 0.7;
  }

  .image-container:hover .remove-btn {
    opacity: 1;
  }

  img {
    float: left;
    width: 100%;
    height: 100%;
    object-fit: cover;
    box-sizing: border-box;
  }

  .remove-btn {
    transition: 0.5s ease;
    opacity: 0;
    position: absolute;
    transform: translate(5%, 5%);
    -ms-transform: translate(-50%, -50%);
    text-align: center;
  }
</style>
