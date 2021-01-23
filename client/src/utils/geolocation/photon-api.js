/**
 * Defines functions related to making API calls to photon-api
 */
import axios from "axios";

function formatLocations(locations) {
  return locations.map((location) => {
    let p = location.properties;
    let formattedName;
    // Removes redundant state names. e.g "Auckland, Auckland, New Zealand"
    if (
      location.properties?.state &&
      location.properties?.state != location.properties?.name
    ) {
      formattedName = [p?.name, p?.state, p?.country].join(", ");
    } else {
      formattedName = [p?.name, p?.country].join(", ");
    }
    return {
      city: p?.name,
      state: p?.state,
      country: p?.country,
      formattedName: formattedName,
    };
  });
}

export default {
  /**
   * Sends a GET request to photon API with specified city name
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
  searchByCity: async function (cityName) {
    if (cityName.length < 2) {
      return [];
    }

    let results = [];
    let response = await axios.get("https://photon.komoot.de/api/", {
      params: {
        q: cityName,
        osm_tag: "place:city",
      },
    });

    const locations = response.data.features;
    const formattedLocations = formatLocations(locations);

    // Remove duplicates - can't use set conversion since it doesnt see to work on objects
    results = formattedLocations.filter((location, index, self) => {
      return (
        index ===
        self.findIndex((i) => i.formattedName == location.formattedName) &&
        location.city.toLowerCase().includes(cityName.toLowerCase())
      );
    });

    return results;
  },

  /**
   * Validates whether the given location is valid using an api query
   * 
   * @param city: Name of city to be queried
   * @param state: (Optional) Name of city to be queried
   * @param country: Name of country to be queried
   * @returns Promise Boolean of whether the given location is valid
   */
  validate: async function (city, state, country) {
    let queryString;
    if (state) {
      queryString = [
        city,
        state,
        country
      ].join(", ");
    } else {
      queryString = [
        city,
        country
      ].join(", ");
    }
    let response = await axios
      .get("https://photon.komoot.de/api/", {
        params: {
          q: queryString
        }
      })

    const cityLocations = response.data.features.filter(location => {
      return location.properties?.osm_value == "city";
    });
    let hasFoundValid = false
    for (let location of cityLocations) {
      const p = location?.properties
      if (state) {
        if (p?.name.toLowerCase() == city.toLowerCase() && p?.state.toLowerCase() == state.toLowerCase() && p?.country.toLowerCase() == country.toLowerCase()) {
          hasFoundValid = true
        }
      } else {
        if (p?.name.toLowerCase() == city.toLowerCase() && p?.country.toLowerCase() == country.toLowerCase()) {
          hasFoundValid = true
        }
      }
    }
    return hasFoundValid
  }
};
