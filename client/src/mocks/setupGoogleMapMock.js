window.google = {
  maps: {
    Geocoder: jest.fn(),
    places: {
      AutocompleteService: jest.fn(),
      PlacesService: jest.fn(),
    },
  },
};

window.google.maps.places.PlacesService.mockImplementation(() => {
  return {
    getDetails: jest.fn()
  }
})