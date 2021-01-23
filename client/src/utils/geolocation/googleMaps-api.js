/**
 * Defines functions related to making API calls to google maps api
 */

let autocompleteService = new window.google.maps.places.AutocompleteService();
let placeService = new window.google.maps.places.PlacesService(document.createElement('div'));
let geocoder = new window.google.maps.Geocoder();

export default {
  /**
   * Sends a GET request to google API with specified city name
   * to retrieve geolocation data
   *
   * @param cityName: Name of city to be queried
   * @returns [Promise] array of objects containing geolocation data
   * [{
   *    city: Denpasar
   *    state: Bali
   *    country: Indonesia
   *    formattedName: Denpasar, Bali, Indonesia
   * }]
   */
  searchByCity: async function (cityName, callback) {
    if (cityName.length < 2) {
      return [];
    }

    await autocompleteService.getPlacePredictions({
      input:cityName,
      types:["(cities)"]

    }, callback)
  },

  getInfoFromPlaceId: function(placeId, callback) {
    let request = {
      placeId: placeId,
      fields: ['address_components','formatted_address', 'geometry', 'place_id'],
    };
    placeService.getDetails(request, callback);
  },

  /**
   * Reads the results from getInfoFromPlaceId and returns object containing address components
   * @param out
   * @returns {{country: (*|string), address: string, city: (*|string), latitude: *, placeId: *,
   *            suburb: (*|string), state: (*|string), longitude: *}}
   */
  readAddressComponent: function(out) {
    const addressComponents = out.address_components
    const number = addressComponents.find(c => c.types.includes("street_number"))
    const street = addressComponents.find(c => c.types.includes("route"))
    const suburb = addressComponents.find(c => c.types.includes("sublocality"))
    const city = addressComponents.find(c => c.types.includes("locality"))
    const state = addressComponents.find(c => c.types.includes("administrative_area_level_1"))
    const country = addressComponents.find(c => c.types.includes("country"))
    const formatted_address = out.formatted_address;

    let address;
    if (number || street) {
      address = number ? number?.long_name + ' ' + street?.long_name : street?.long_name
    } else {
      address = ""
    }

    return {
      address,
      latitude : out.geometry.location.lat(),
      longitude : out.geometry.location.lng(),
      suburb : suburb?.long_name || "",
      city : city?.long_name || "",
      state : state?.long_name || "",
      country : country?.long_name || "",
      placeId : out.place_id,
      formatted_address: formatted_address
    };
  },

  searchByLatLng: async function (lat, lng, callback) {
    geocoder.geocode({location: {lat:lat, lng:lng}}, callback)
  },

  searchByLocationName: async function (addressName, callback) {
    if (!addressName || addressName.length < 2) {
      return [];
    }

    autocompleteService.getPlacePredictions({
      input:addressName
    }, callback)
  },

  searchByAddress: async function (addressName, callback) {
    if (!addressName || addressName.length < 2) {
      return [];
    }
    autocompleteService.getPlacePredictions({
      input:addressName,
      types:["address"]
    }, callback)
  }
};
