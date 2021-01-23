/* eslint-env jest */ // tell ESLint to check this file with jest syntax
import { createLocalVue, mount } from "@vue/test-utils";
import Buefy from "buefy";
import Vuex from "vuex";
import SingleActivityCard from "../SingleActivityCard.vue";

const localVue = createLocalVue();
localVue.use(Buefy);
localVue.use(Vuex);

jest.mock("@/Api");

const mocks = {
  $router: {
    push: jest.fn(),
  },
  $route: {
    params: { userId: 3 },
  },
};

describe("API calls and Page Display", () => {
  let wrapper;

  beforeEach(() => {
    const activities = {
      namespaced: true,
      getters: {
        getActivityById: (state) => (activityId) => ({
          activityId: 1105,
          creatorId: 3,
          creatorName: "Fabian Gilson",
          description: "",
          activityName: "Hang out with Isabella Lee",
          activityType: ["Hang Gliding experience"],
          continuous: true,
          startTime: null,
          endTime: null,
          location: "Christchurch",
          hashtags: [],
          role: "creator",
          visibility: "public",
        }),
      },
    };
    const store = new Vuex.Store({
      modules: {
        activities,
      },
      state: {
        userId: 3,
      },
    });
    wrapper = mount(SingleActivityCard, {
      localVue,
      store,
      mocks,
      propsData: {
        activityId: 1105,
      },
    });
  });

  it("retrieves activity from Activity Store", () => {
    expect(wrapper.vm.activity.creatorName).toBe("Fabian Gilson");
  });

  it("reroutes to view activity page when view btn is clicked", async () => {
    await wrapper.find("img.activity-img").trigger("dblclick");
    await wrapper.vm.$nextTick();
    expect(mocks.$router.push).toHaveBeenCalledWith({
      name: "ViewActivity",
      params: { activityId: 1105, showBackButton: false },
    });
  });
});
