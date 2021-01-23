/* eslint-env jest */ // tell ESLint to check this file with jest syntax
import api from "@/Api";
import {createLocalVue, mount} from "@vue/test-utils";
import Buefy from "buefy";
import Vuelidate from "vuelidate";
import Vuex from "vuex";
import ActivityForm from "../ActivityForm.vue";

const localVue = createLocalVue();
localVue.use(Vuelidate);
localVue.use(Buefy);

jest.mock("@/Api");

const mocks = {
  $router: {
    push: jest.fn()
  }
};

api.getAllHashtags.mockResolvedValue({
  data: ["aaa", "bbb"],
});

describe("Page Display", () => {
  let wrapper;

  // Give fake mock data for API calls
  api.getActivityTypes.mockResolvedValue(
    Promise.resolve({
      data: ["Airsoft", "Air Sphering"],
    })
  );

  beforeEach(() => {
    const activitiesStore = {
      namespaced: true,
      state: {
        activities: [],
      },
    };
    const store = new Vuex.Store({
      modules: {
        activities: activitiesStore,
      },
      state: {
        userId: 10
      }
    });
    wrapper = mount(ActivityForm, {
      localVue,
      store,
      mocks,
    });
  });

  // Checks if the element exists (by their id)
  it("renders correct form fields", () => {
    expect(wrapper.find("#activity-types").isVisible()).toBe(true);
    expect(wrapper.find("#activity-name").isVisible()).toBe(true);
    expect(wrapper.find("#time-range-picker").isVisible()).toBe(true);
    expect(wrapper.find("#date-range").exists()).toBe(false); // does not exist when time range is continuous (default)
    expect(wrapper.find("#description").isVisible()).toBe(true);
  });

  it("shows date picker when time range is set to duration", async () => {
    wrapper.find("#time-range-switch").trigger("click");
    await wrapper.vm.$nextTick();
    expect(wrapper.find("#date-range").isVisible()).toBe(true);
    expect(wrapper.find("#date-start").isVisible()).toBe(true);
    expect(wrapper.find("#date-end").isVisible()).toBe(true);
    expect(wrapper.find("#time-range").isVisible()).toBe(true);
  });

  it("shows date picker when time range is set to duration and set time checkbox is ticked", async () => {
    wrapper.find("#time-range-switch").trigger("click");
    await wrapper.vm.$nextTick();

    wrapper.setData({enableStartEndTime: true});
    await wrapper.vm.$nextTick();

    expect(wrapper.find("#time-start").isVisible()).toBe(true);
    expect(wrapper.find("#time-end").isVisible()).toBe(true);
  });

  it("should reset the time back when switching back from duration to continunous", async () => {
    wrapper.find("#time-range-switch").trigger("click"); // now in duration
    await wrapper.vm.$nextTick();
    expect(wrapper.find("#date-end").isVisible()).toBe(true);

    // Set the end date to 3 days in future
    let threeDaysInFuture = new Date();
    threeDaysInFuture.setDate(threeDaysInFuture.getDate() + 3);

    // Set the start date to 5 days in future
    let fiveDaysInFuture = new Date();
    fiveDaysInFuture.setDate(fiveDaysInFuture.getDate() + 5);

    wrapper.setData({
      startDate: fiveDaysInFuture,
      endDate: threeDaysInFuture,
    });
    expect(wrapper.vm.startDate).toBe(fiveDaysInFuture);
    expect(wrapper.vm.endDate).toBe(threeDaysInFuture);
    await wrapper.vm.$nextTick();

    wrapper.find("#time-range-switch").trigger("click"); // now in continuous
    await wrapper.vm.$nextTick();

    const now = new Date();
    now.setHours(now.getHours() + 1);

    // in minutes
    let startTimeDiff =
      (wrapper.vm.startDate.getTime() - now.getTime()) / (60 * 1000);
    let endTimeDiff =
      (wrapper.vm.endDate.getTime() - now.getTime()) / (60 * 1000);

    expect(startTimeDiff).toBeCloseTo(0);
    expect(endTimeDiff).toBeCloseTo(60);
  });
});

localVue.use(Vuex);

describe("Editing continuous activitiy", () => {
  let wrapper;

  // Give fake mock data for API calls
  api.getActivityTypes.mockResolvedValue({
    data: ["Ice Hockey"],
  });

  beforeEach(() => {
    const activitiesStore = {
      namespaced: true,
      state: {
        activities: [
          {
            continuous: true,
            activityId: 1,
            creatorId: 10,
            creatorName: "Fabian Gilson",
            description: "Testing description",
            activityName: "Testing",
            activityType: ["Ice Hockey"],
            continous: true,
            startTime: null,
            endTime: null,
            location: "Auckland",
            visibility: "public"
          },
        ],
      },
    };
    const store = new Vuex.Store({
      modules: {
        activities: activitiesStore,
      },
      state: {
        userId: 10
      }
    });
    wrapper = mount(ActivityForm, {
      localVue,
      store,
      mocks,
      propsData: {activityId: 1},
    });
  });

  it("fills with existing activity name in edit mode", async () => {
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.activityName).toBe("Testing");
  });

  it("fills with existing activity type in edit mode", async () => {
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.activityTypes).toStrictEqual(["Ice Hockey"]);
  });

  it("fills with existing activity time range in edit mode", async () => {
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.timeRange).toBe("Continuous");
  });

  it("fills with existing activity location in edit mode", async () => {
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.location).toBe("Auckland");
  });

  it("fills with existing activity description in edit mode", async () => {
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.description).toBe("Testing description");
  });
});

describe("Editing time ranged activitiy", () => {
  let wrapper;

  let startTime = "2020-07-16T23:14:42.000+1200";
  let endTime = "2020-07-17T00:15:00.000+1200";

  // Give fake mock data for API calls
  api.getActivityTypes.mockResolvedValue({
    data: ["Ice Hockey"],
  });

  beforeEach(() => {
    const activitiesStore = {
      namespaced: true,
      state: {
        activities: [
          {
            activityId: 1,
            creatorId: 10,
            creatorName: "Fabian Gilson",
            description: "Testing description",
            activityName: "Testing",
            activityType: ["Ice Hockey"],
            continous: false,
            startTime,
            endTime,
            location: "Auckland",
          },
        ],
      },
    };
    const store = new Vuex.Store({
      modules: {
        activities: activitiesStore,
      },
      state: {
        userId: 10
      }
    });
    wrapper = mount(ActivityForm, {
      localVue,
      store,
      mocks,
      propsData: {activityId: 1},
    });
  });

  it("fills with existing activity start time", async () => {
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.startDate).toStrictEqual(new Date(startTime));
  });

  it("fills with existing activity end time", async () => {
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.endDate).toStrictEqual(new Date(endTime));
  });
});
