/* eslint-env jest */ // tell ESLint to check this file with jest syntax
import { mount, createLocalVue } from "@vue/test-utils";
import Profile from "../Profile";
import Buefy from "buefy";
import api from "@/Api";
import activities from "@/store/modules/activities";
import Vuex from "vuex";

const localVue = createLocalVue();
localVue.use(Buefy);
localVue.use(Vuex);

// Create a mock for API call to api.getUserProfile and api.getUserActivitiesById
jest.mock("@/Api");

describe("Profile Test", () => {
  // Give fake mock data for API calls
  api.getProfileById.mockResolvedValue({
    data: {
      id: 3,
      firstname: "Fabian",
      middlename: "",
      lastname: "Gilson",
      nickname: "Fab",
      bio: "Loves skateboarding when there's an eclipse",
      gender: "Male",
      primary_email: {
        address: "fabian@gmail.com",
        isPrimary: true,
        userEmailID: 4,
      },
      additional_email: [],
      date_of_birth: "1985-01-01",
      passports: [
        {
          code: "DZA",
          flag: "https://restcountries.eu/data/dza.svg",
          name: "Algeria",
        },
      ],
      activities: null,
      fitness: 2,
      location: {
        placeId: "ChIJ90l1l2CpQjQRJ7UUESRMX7M",
        description: null,
        latitude: null,
        longitude: null,
      },
    },
  });

  // Give fake mock data for API calls
  api.getUserActivitiesById.mockResolvedValue({
    data: {
      activities: ["Frying frites", "Fighting pandas"],
    },
  });

  let store;
  let wrapper;

  beforeEach(() => {
    store = new Vuex.Store({
      getters: {
        hasAdminPrivileges: (state) => true,
      },
      modules: {
        activities: {
          namespaced: true,
          actions: {
            fetchUserActivities: jest.fn(),
          },
          getters: {
            // eslint-disable-next-line no-unused-vars
            getActivityByRoleAndTimeRange: (state) => (role, continuous) => {
              return [];
            },
          },
          state: activities.state,
        },
      },
    });
    wrapper = mount(Profile, {
      localVue,
      store,
      stubs: ["router-link"],
      mocks: {
        $route: {
          params: {
            userId: 1,
          },
        },
      },
    });
  });

  // Checks if the element exists (by their id)
  it("renders properly", () => {
    expect(wrapper.find("#fullname").isVisible()).toBe(true);
  });

  it("Should display name correctly", (done) => {
    expect(wrapper.vm.userData.firstname).toBe("Fabian");

    // This is needed to make sure Promise is resolved
    wrapper.vm.$nextTick(() => {
      expect(wrapper.find("#fullname").exists()).toBe(true);
      expect(wrapper.find("#fullname").text()).toBe("Fabian Gilson");
      done();
    });
  });

  it("Should display nickname correctly", (done) => {
    expect(wrapper.vm.userData.nickname).toBe("Fab");

    // This is needed to make sure Promise is resolved
    wrapper.vm.$nextTick(() => {
      expect(wrapper.find("#nickname").exists()).toBe(true);
      expect(wrapper.find("#nickname").text()).toBe('"Fab"');
      done();
    });
  });

  it("Should display birthdate in pretty format correctly", (done) => {
    // This is needed to make sure Promise is resolved
    wrapper.vm.$nextTick(() => {
      expect(wrapper.find("#birthdate").exists()).toBe(true);
      expect(wrapper.find("#birthdate").text()).toBe("January 1, 1985");
      done();
    });
  });

  it("Should display gender correctly", (done) => {
    expect(wrapper.vm.userData.gender).toBe("Male");

    // This is needed to make sure Promise is resolved
    wrapper.vm.$nextTick(() => {
      expect(wrapper.find("#gender").exists()).toBe(true);
      expect(wrapper.find("#gender").text()).toBe("Male");
      done();
    });
  });

  it("Should display bio correctly", (done) => {
    expect(wrapper.vm.userData.bio).toBe(
      "Loves skateboarding when there's an eclipse"
    );

    // This is needed to make sure Promise is resolved
    wrapper.vm.$nextTick(() => {
      expect(wrapper.find("#bio").exists()).toBe(true);
      expect(wrapper.find("#bio").text()).toBe(
        "Loves skateboarding when there's an eclipse"
      );
      done();
    });
  });

  it("Should display primary email correctly", (done) => {
    // This is needed to make sure Promise is resolved
    wrapper.vm.$nextTick(() => {
      expect(wrapper.find("#primary-email").exists()).toBe(true);
      expect(wrapper.find("#primary-email").text()).toBe("fabian@gmail.com");
      done();
    });
  });
});
