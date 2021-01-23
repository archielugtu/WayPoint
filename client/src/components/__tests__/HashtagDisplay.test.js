/* eslint-env jest */ // tell ESLint to check this file with jest syntax
import { shallowMount, createLocalVue } from "@vue/test-utils";
import HashtagDisplay from "../HashtagDisplay";
import Buefy from "buefy";
import Vuex from "vuex";

const localVue = createLocalVue();
localVue.use(Buefy);
localVue.use(Vuex)

const mocks = {
  $router: {
    push: jest.fn(),
  },
};

describe("Display", () => {
  let wrapper;
  let store;

  beforeEach(() => {
    store = new Vuex.Store({
      actions: {
        setPrevActivitySearch: jest.fn()
      }
    })

    wrapper = shallowMount(HashtagDisplay, {
      store,
      localVue,
      mocks,
    });
  });

  it("shows no hashtags when none are passed as prop", async () => {
    await wrapper.setProps({ hashtags: [] });
    expect(wrapper.find(".hashtags").exists()).toBe(false);
  });

  it("shows hashtags when some are passed as prop", async () => {
    await wrapper.setProps({ hashtags: ["cool", "hashtag"] });
    expect(wrapper.find(".hashtags").exists()).toBe(true);
  });

  it("redirects to search page when hashtag is clicked", async () => {
    await wrapper.setProps({ hashtags: ["cool", "hashtag"] });
    await wrapper.find(".hashtags").trigger("click");
    expect(mocks.$router.push).toHaveBeenCalledWith({
      name: "SearchActivities", query: { load : true }
    });
  });
});
