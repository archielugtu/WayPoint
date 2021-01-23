/* eslint-env jest */ // tell ESLint to check this file with jest syntax
import { mount, createLocalVue } from "@vue/test-utils";
import SearchUsers from "../SearchUsers";
import Vuex from "vuex";
import Buefy from "buefy";
import flushPromises from "@/utils/flush-promises/index";

import api from "@/Api";

const localVue = createLocalVue();
localVue.use(Vuex);
localVue.use(Buefy);

jest.mock("@/Api");
api.searchProfiles = jest.fn();
api.getUserAdminStatusFromEmail = jest.fn();

const activities = {
  namespaced: true,
  state: {
    activityTypes: ["Badminton", "Dodgeball"],
  },
  actions: {
    fetchActivityTypes: jest.fn(),
  },
};

describe("Display and API calls for normal user", () => {
  let wrapper;
  let $router;

  const store = new Vuex.Store({
    modules: {
      activities,
    },
    getters: {
      hasAdminPrivileges: (state) => false,
    },
  });

  beforeEach(() => {
    api.searchProfiles.mockResolvedValue({
      data: {
        results: [
          {
            lastname: "Van Houten",
            firstname: "Millhouse",
            middlename: "Mussolini",
            nickname: "Thrillhouse",
            primary_email: "millhouse@dev.com",
            activities: [],
          },
          {
            lastname: "Skinner",
            firstname: "Seymour",
            middlename: "W.",
            nickname: "Skinner",
            primary_email: "seymour@dev.com",
            activities: [],
          },
        ],
        totalPages: 2,
        totalElements: 2,
      },
    });

    api.getUserAdminStatusFromEmail.mockResolvedValue({
      data: [
        {
          admin: false,
          primary_email: "millhouse@dev.com",
          user_id: 149,
          is_admin: false,
        },
        {
          admin: false,
          primary_email: "seymour@dev.com",
          user_id: 156,
          is_admin: false,
        },
      ],
    });

    api.getActivityTypes.mockResolvedValue({
      data: [
        "Quad Biking"
      ],
    });

    $router = {
      push: jest.fn(),
    };

    wrapper = mount(SearchUsers, {
      localVue,
      store,
      mocks: {
        $router,
      },
    });
  });

  it("has first name input", () => {
    expect(wrapper.find("#firstname").isVisible()).toBe(true);
  });

  it("has last name input", () => {
    expect(wrapper.find("#lastname").isVisible()).toBe(true);
  });

  it("has email input", () => {
    expect(wrapper.find("#email").isVisible()).toBe(true);
  });

  it("has activity types input", () => {
    expect(wrapper.find("#activity-types").isVisible()).toBe(true);
  });

  it("has activity type search method input", () => {
    expect(wrapper.find("#search-method").isVisible()).toBe(true);
  });

  it("has search button", () => {
    expect(wrapper.find("#search-btn").isVisible()).toBe(true);
  });


  it("queries for first name when the first name input is filled", async () => {
    wrapper.setData({ searchFirstName: "Jim" });

    api.searchProfiles.mockResolvedValueOnce({
      data: {
        results: [],
        totalPages: 0,
        totalElements: 0,
      },
    });
    await wrapper.find("#search-btn").trigger("click");
    await wrapper.vm.$nextTick();

    expect(api.searchProfiles).toBeCalledWith({
      activities: "",
      email: "",
      firstname: "Jim",
      lastname: "",
      method: "and",
      page: 0,
    });
  });

  it("queries for last name when the last name input is filled", async () => {
    wrapper.setData({ searchLastName: "Bob" });

    api.searchProfiles.mockResolvedValueOnce({
      data: {
        results: [],
        totalPages: 0,
        totalElements: 0,
      },
    });
    await wrapper.find("#search-btn").trigger("click");
    await wrapper.vm.$nextTick();

    expect(api.searchProfiles).toBeCalledWith({
      activities: "",
      email: "",
      firstname: "",
      lastname: "Bob",
      method: "and",
      page: 0,
    });
  });

  it("queries for email when the email input is filled", async () => {
    wrapper.setData({ searchEmail: "jim@bob.com" });

    api.searchProfiles.mockResolvedValueOnce({
      data: {
        results: [],
        totalPages: 0,
        totalElements: 0,
      },
    });
    await wrapper.find("#search-btn").trigger("click");
    await wrapper.vm.$nextTick();

    expect(api.searchProfiles).toBeCalledWith({
      activities: "",
      email: "jim@bob.com",
      firstname: "",
      lastname: "",
      method: "and",
      page: 0,
    });
  });

  it("queries for activity types when the activity types input is filled", async () => {
    wrapper.setData({ selectedOptions: ["Badminton", "Dodgeball"] });

    api.searchProfiles.mockResolvedValueOnce({
      data: {
        results: [],
        totalPages: 0,
        totalElements: 0,
      },
    });
    await wrapper.find("#search-btn").trigger("click");
    await wrapper.vm.$nextTick();

    expect(api.searchProfiles).toBeCalledWith({
      activities: "Badminton%20Dodgeball",
      email: "",
      firstname: "",
      lastname: "",
      method: "and",
      page: 0,
    });
  });

  it("queries for OR search method when the search method input is changed", async () => {
    wrapper.setData({ searchMethod: "OR" });

    api.searchProfiles.mockResolvedValueOnce({
      data: {
        results: [],
        totalPages: 0,
        totalElements: 0,
      },
    });
    await wrapper.find("#search-btn").trigger("click");
    await wrapper.vm.$nextTick();

    expect(api.searchProfiles).toBeCalledWith({
      activities: "",
      email: "",
      firstname: "",
      lastname: "",
      method: "or",
      page: 0,
    });
  });

  it("resets the results table if backend returns non success status codes", async () => {
    await wrapper.find("#search-btn").trigger("click")
    await flushPromises();
    expect(wrapper.vm.users.length).toBe(2);
    api.searchProfiles.mockRejectedValueOnce({
      data: {},
    });
    await wrapper.find("#search-btn").trigger("click");
    await wrapper.vm.$nextTick();

    await flushPromises();
    expect(wrapper.vm.users.length).toBe(0);
    expect(wrapper.find("div.column.is-three-quarters .box").text()).toBe(
      "No users found"
    );
  });

  it("redirects to user's profile page when user's result entry is clicked", async () => {
    await wrapper.find("#search-btn").trigger("click")

    await flushPromises();
    //const searchResultLinks = wrapper.findAll("button.button.is-rounded");
    const searchResultLinks = wrapper.findAll("td");

    expect(searchResultLinks.length).toBe(10);

    const firstSearchResult = searchResultLinks.at(0);
    await firstSearchResult.trigger("click");
    await firstSearchResult.trigger("dblclick");
    expect($router.push).toHaveBeenLastCalledWith({
      name: "profile",
      params: { userId: 149 },
    });
  });
});

describe("Display and API calls for admin user", () => {
  let wrapper;
  let $router;

  const store = new Vuex.Store({
    modules: {
      activities,
    },
  });

  beforeEach(() => {
    api.searchProfiles.mockResolvedValueOnce({
      data: {
        results: [
          {
            lastname: "krabappel",
            firstname: "Edna",
            middlename: "",
            nickname: "Krab Apples",
            primary_email: "edna@dev.com",
            activities: [],
          },
        ],
        totalPages: 1,
        totalElements: 1,
      },
    });

    api.getUserAdminStatusFromEmail.mockResolvedValueOnce({
      data: [
        {
          admin: false,
          primary_email: "edna@dev.com",
          user_id: 1,
          is_admin: false,
        },
      ],
    });

    $router = {
      push: jest.fn(),
    };

    wrapper = mount(SearchUsers, {
      localVue,
      propsData: {
        fromAdminDashboard: true,
      },
      store,
      mocks: {
        $router,
      },
    });
  });

  it("has an edit button for search results", async () => {
    await wrapper.find("#search-btn").trigger("click")

    await flushPromises()
    // set to false because it's not visible until user clicks the
    // expand option button
    expect(wrapper.find(".edit-btn").isVisible()).toBe(false);
  });

  it("has an delete button for search results", async () => {
    await wrapper.find("#search-btn").trigger("click")

    await flushPromises()
    // set to false because it's not visible until user clicks the
    // expand option button
    expect(wrapper.find(".delete-btn").isVisible()).toBe(false);
  });
});
