/* eslint-env jest */ // tell ESLint to check this file with jest syntax
import { mount, createLocalVue } from "@vue/test-utils";
import Vuelidate from "vuelidate";
import General from "../General";
import Buefy from "buefy";

const localVue = createLocalVue();
localVue.use(Vuelidate);
localVue.use(Buefy);

const userData = {
  firstname: "Kaguya",
  lastname: "Shinomiya",
  middlename: "Muscle",
  nickname: "Ice queen",
  date_of_birth: new Date("2003-01-01"),
  bio:
    "Very cold and rational individual and only started to warm up to others after joining the student council.",
  gender: "Female",
  fitness_level: 2,
  location: {
    city: "Fujinomiya",
    country: "Japan",
    locationId: 1,
    state: "Shizuoka",
  },
};

const $route = {
  params: {
    userId: 1,
  },
};

describe("Page Display", () => {
  let wrapper;

  beforeEach(() => {
    wrapper = mount(General, {
      localVue,
      stubs: ["router-link"],
      mocks: {
        $route: $route,
      },
      propsData: {
        userData: userData,
      },
    });
  });

  it("shows error when first name is removed", async () => {
    expect(wrapper.find("#firstname .help").exists()).toBe(false);
    expect(wrapper.vm.userData.firstname).toBe("Kaguya");
    wrapper.setData({ userData: { firstname: "" } });
    expect(wrapper.vm.userData.firstname).toBe("");
    wrapper.vm.$v.$touch();
    await wrapper.vm.$nextTick();
    expect(wrapper.find("#firstname .help").isVisible()).toBe(true);
  });

  it("shows error when last name is removed", async () => {
    expect(wrapper.find("#lastname .help").exists()).toBe(false);
    expect(wrapper.vm.userData.lastname).toBe("Shinomiya");
    wrapper.setData({ userData: { lastname: "" } });
    expect(wrapper.vm.userData.lastname).toBe("");
    wrapper.vm.$v.$touch();
    await wrapper.vm.$nextTick();
    expect(wrapper.find("#lastname .help").isVisible()).toBe(true);
  });

  it("shows error when first name is over char limit", async () => {
    const longFirstName = "Kaguya".repeat(50);
    expect(wrapper.find("#firstname .help").exists()).toBe(false);
    wrapper.setData({ userData: { firstname: longFirstName } });
    expect(wrapper.vm.userData.firstname).toBe(longFirstName);
    wrapper.vm.$v.$touch();
    await wrapper.vm.$nextTick();
    expect(wrapper.find("#firstname .help").isVisible()).toBe(true);
  });

  it("shows error when last name is over char limit", async () => {
    const longLastName = "Shinomiya".repeat(50);
    expect(wrapper.find("#lastname .help").exists()).toBe(false);
    wrapper.setData({ userData: { lastname: longLastName } });
    expect(wrapper.vm.userData.lastname).toBe(longLastName);
    wrapper.vm.$v.$touch();
    await wrapper.vm.$nextTick();
    expect(wrapper.find("#lastname .help").isVisible()).toBe(true);
  });

  it("shows error when middle name is over char limit", async () => {
    const longMiddleName = "Middle".repeat(50);
    expect(wrapper.find("#middlename .help").exists()).toBe(false);
    wrapper.setData({ userData: { middlename: longMiddleName } });
    expect(wrapper.vm.userData.middlename).toBe(longMiddleName);
    wrapper.vm.$v.$touch();
    await wrapper.vm.$nextTick();
    expect(wrapper.find("#middlename .help").isVisible()).toBe(true);
  });

  it("shows error when nickname is over char limit", async () => {
    const longNickname = "Ice Queen".repeat(50);
    expect(wrapper.find("#nickname .help").exists()).toBe(false);
    wrapper.setData({ userData: { nickname: longNickname } });
    expect(wrapper.vm.userData.nickname).toBe(longNickname);
    wrapper.vm.$v.$touch();
    await wrapper.vm.$nextTick();
    expect(wrapper.find("#nickname .help").isVisible()).toBe(true);
  });

  it("shows error when bio is over char limit", async () => {
    const longBio = "Loves saying how cute".repeat(200);
    expect(wrapper.find("#bio .help").exists()).toBe(false);
    wrapper.setData({ userData: { bio: longBio } });
    expect(wrapper.vm.userData.bio).toBe(longBio);
    wrapper.vm.$v.$touch();
    await wrapper.vm.$nextTick();
    expect(wrapper.find("#bio .help").isVisible()).toBe(true);
  });

  it("renders correct form fields", () => {
    expect(wrapper.find("#firstname").isVisible()).toBe(true);
    expect(wrapper.find("#lastname").isVisible()).toBe(true);
    expect(wrapper.find("#nickname").isVisible()).toBe(true);
    expect(wrapper.find("#middlename").isVisible()).toBe(true);
    expect(wrapper.find("#date-of-birth").isVisible()).toBe(true);
    expect(wrapper.find("#bio").isVisible()).toBe(true);
    expect(wrapper.find("#gender").isVisible()).toBe(true);
    expect(wrapper.find("#fitness-dropdown").isVisible()).toBe(true);
  });
});
