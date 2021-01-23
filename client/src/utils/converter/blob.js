export default {
  /**
   * Converts a data:image object to a blob
   *
   * Useful for creating objectURL objects after cropping image
   */
  dataURItoBlob: function(dataURI) {
    let mime = dataURI
      .split(",")[0]
      .split(":")[1]
      .split(";")[0];
    let binary = atob(dataURI.split(",")[1]);
    let array = [];
    for (let i = 0; i < binary.length; i++) {
      array.push(binary.charCodeAt(i));
    }
    return new Blob([new Uint8Array(array)], { type: mime });
  },

  /**
   * Converts a base64 representation of an image to a blob
   */
  base64toBlob: function(base64Str, imageType) {
    let byteString = atob(base64Str);
    let ab = new ArrayBuffer(byteString.length);
    let ia = new Uint8Array(ab);

    for (let i = 0; i < byteString.length; i++) {
      ia[i] = byteString.charCodeAt(i);
    }
    return new Blob([ab], { type: imageType });
  },
};
