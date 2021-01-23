/* eslint-env jest */ // tell ESLint to check this file with jest syntax
import { mount, createLocalVue } from "@vue/test-utils";
import Vuelidate from "vuelidate";
import Signup from "../Signup";
import Buefy from "buefy";
import api from "@/Api";
import Vuex from "vuex";
import flushPromises from "@/utils/flush-promises/index";

const localVue = createLocalVue();
localVue.use(Buefy);
localVue.use(Vuelidate);

const firstName = "Curly";
const lastName = "Braces";
const middleName = "Frickin";
const nickname = "{}";
const birthDate = "1945-08-17";
const email = "curly@braces.com";
const bio = "Be fast, be #agile";
const password = "codescoping";

// Create a mock for API call to api.getUserProfile and api.getUserActivitiesById
jest.mock("@/Api");

// Give fake mock data for API calls
api.getActivityTypes.mockResolvedValue(
  Promise.resolve({
    data: {
      activities: ["Quad Biking", "Water WalkerZ"],
    },
  })
);

api.getEmailIsInUse.mockResolvedValue({ data: false });

const userData = {
  devMode: true,
  user: {
    firstName: firstName,
    middleName: middleName,
    lastName: lastName,
    nickname: nickname,
    birthDate: birthDate,
    bio: bio,
    email: email,
    password: password,
  },
};

const mocks = {
  $params: userData,
  $router: {
    push: jest.fn(),
  },
};

const stubs = ["router-link"];

describe("Page Display", () => {
  let wrapper;

  beforeEach(() => {
    wrapper = mount(Signup, {
      localVue,
      mocks,
      stubs,
    });
  });

  // Checks if the element exists (by their id)
  it("renders correct form fields", () => {
    expect(wrapper.find("#firstname").isVisible()).toBe(true);
    expect(wrapper.find("#lastname").isVisible()).toBe(true);
    expect(wrapper.find("#nickname").isVisible()).toBe(true);
    expect(wrapper.find("#middlename").isVisible()).toBe(true);
    expect(wrapper.find("#date-of-birth").isVisible()).toBe(true);
    expect(wrapper.find("#gender").isVisible()).toBe(true);
    expect(wrapper.find("#email").isVisible()).toBe(true);
    expect(wrapper.find("#password").isVisible()).toBe(true);
    expect(wrapper.find("#email-setup").isVisible()).toBe(true);
  });

  // Checks if the element exists (by their id)
  it("renders correct form fields in the second page", async () => {
    await wrapper.find("#submit-button").trigger("click");
    await flushPromises();
    expect(wrapper.find("#bio").isVisible()).toBe(true);
    expect(wrapper.find("#fitness-slider").isVisible()).toBe(true);
  });

  it("renders correct form fields in the third page", async () => {
    await wrapper.find("#submit-button").trigger("click");
    await flushPromises();
    await wrapper.find("#submit-button").trigger("click");
    await flushPromises();
    expect(wrapper.find("#passport-countries").isVisible()).toBe(true);
  });

  it("renders signup button", () => {
    expect(wrapper.find("#submit-button").is("button")).toBe(true);
  });
});

describe("Input validation error message when empty", () => {
  let wrapper;
  let vm;

  beforeEach(() => {
    wrapper = mount(Signup, {
      localVue,
      mocks,
      stubs,
    });
    vm = wrapper.vm;
  });

  it("First name field showing error when empty", async () => {
    expect(wrapper.find("#firstname .help").exists()).toBe(false);
    expect(wrapper.vm.firstName).toBe(firstName);
    wrapper.setData({ firstName: "" });
    expect(wrapper.vm.firstName).toBe("");
    vm.$v.$touch();
    await vm.$nextTick();
    expect(wrapper.find("#firstname .is-danger").isVisible()).toBe(true);
  });

  it("Last name field showing error when empty", async () => {
    expect(wrapper.find("#lastname .help").exists()).toBe(false);
    expect(wrapper.vm.lastName).toBe(lastName);
    wrapper.setData({ lastName: "" });
    expect(wrapper.vm.lastName).toBe("");
    vm.$v.$touch();
    await vm.$nextTick();
    expect(wrapper.find("#lastname .help").isVisible()).toBe(true);
  });

  it("Email field showing error when empty", async () => {
    expect(wrapper.find("#email .help").exists()).toBe(false);
    expect(wrapper.vm.email).toBe(email);
    wrapper.setData({ email: "" });
    expect(wrapper.vm.email).toBe("");
    vm.$v.$touch();
    await vm.$nextTick();
    expect(wrapper.find("#email .help").isVisible()).toBe(true);
  });

  it("Password field showing error when empty", async () => {
    expect(wrapper.find("#password .help").exists()).toBe(false);
    expect(wrapper.vm.password).toBe(password);
    wrapper.setData({ password: "" });
    expect(wrapper.vm.password).toBe("");
    vm.$v.$touch();
    await vm.$nextTick();
    expect(wrapper.find("#password .help").isVisible()).toBe(true);
  });
});

describe("Input validation error message when surpasses max length", () => {
  let wrapper;
  let vm;
  beforeEach(() => {
    wrapper = mount(Signup, {
      localVue,
      mocks,
      stubs,
    });
    vm = wrapper.vm;
  });

  const reallyLongName = "LongName".repeat(25);

  it("First name field showing error when gone over max length", async () => {
    expect(wrapper.find("#firstname .help").exists()).toBe(false);
    expect(wrapper.vm.firstName).toBe(firstName);
    wrapper.setData({ firstName: reallyLongName });
    expect(wrapper.vm.firstName).toBe(reallyLongName);
    vm.$v.$touch();
    await vm.$nextTick();
    expect(wrapper.find("#firstname .is-danger").isVisible()).toBe(true);
  });

  it("Last name field showing error when gone over max length", async () => {
    expect(wrapper.find("#lastname .help").exists()).toBe(false);
    expect(wrapper.vm.lastName).toBe(lastName);
    wrapper.setData({ lastName: reallyLongName });
    expect(wrapper.vm.lastName).toBe(reallyLongName);
    vm.$v.$touch();
    await vm.$nextTick();
    expect(wrapper.find("#lastname .help").isVisible()).toBe(true);
  });

  it("Middle name field showing error when gone over max length", async () => {
    expect(wrapper.find("#middlename .help").exists()).toBe(false);
    expect(wrapper.vm.middleName).toBe(middleName);
    wrapper.setData({ middleName: reallyLongName });
    expect(wrapper.vm.middleName).toBe(reallyLongName);
    vm.$v.$touch();
    await vm.$nextTick();
    expect(wrapper.find("#middlename .help").isVisible()).toBe(true);
  });

  it("Nickname field showing error when gone over max length", async () => {
    expect(wrapper.find("#nickname .help").exists()).toBe(false);
    expect(wrapper.vm.nickname).toBe(nickname);
    wrapper.setData({ nickname: reallyLongName });
    expect(wrapper.vm.nickname).toBe(reallyLongName);
    vm.$v.$touch();
    await vm.$nextTick();
    expect(wrapper.find("#nickname .help").isVisible()).toBe(true);
  });

  it("Bio field showing error when gone over max length", async () => {
    await wrapper.find("#submit-button").trigger("click");
    const reallyLongBio = "Bio".repeat(1000);
    expect(wrapper.find("#bio .help").exists()).toBe(false);
    expect(wrapper.vm.bio).toBe(bio);
    wrapper.setData({ bio: reallyLongBio });
    expect(wrapper.vm.bio).toBe(reallyLongBio);
    vm.$v.$touch();
    await vm.$nextTick();
    expect(wrapper.find("#bio .help").isVisible()).toBe(true);
  });
});

localVue.use(Vuex);

describe("API call", () => {
  let wrapper;
  let store;

  beforeEach(() => {
    api.signup = jest.fn();
    store = new Vuex.Store({
      actions: {
        setLoggedIn: jest.fn(),
        setUserId: jest.fn(),
      },
    });
    userData.user = {
      firstName: firstName,
      middleName: middleName,
      lastName: lastName,
      nickname: nickname,
      birthDate: birthDate,
      bio: bio,
      email: email,
      password: password,
    };
  });

  it("redirects to user profile when signup succeeded", async () => {
    wrapper = mount(Signup, { localVue, mocks, stubs, store });
    api.signup.mockResolvedValue({ data: {} });
    api.login.mockResolvedValue({ data: { userId: 1 } });
    await wrapper.find("#submit-button").trigger("click");
    await wrapper.find("#submit-button").trigger("click");
    await flushPromises();
    expect(mocks.$router.push).toHaveBeenCalledWith({
      name: "profile",
      params: { userId: 1 },
    });
  });

  it("does not calls signup api call when first name is empty", async () => {
    userData.user.firstName = "";
    wrapper = mount(Signup, { localVue, mocks, stubs, store });
    await wrapper.find("#submit-button").trigger("click");
    await wrapper.find("#submit-button").trigger("click");

    await flushPromises();
    expect(api.signup).toHaveBeenCalledTimes(0);
  });

  it("does not calls signup api call when last name is empty", async () => {
    userData.user.lastName = "";
    wrapper = mount(Signup, { localVue, mocks, stubs, store });
    await wrapper.find("#submit-button").trigger("click");
    await wrapper.find("#submit-button").trigger("click");

    await flushPromises();
    expect(api.signup).toHaveBeenCalledTimes(0);
  });

  it("does not calls signup api call when email is empty", async () => {
    userData.user.email = "";
    wrapper = mount(Signup, { localVue, mocks, stubs, store });
    await wrapper.find("#submit-button").trigger("click");
    await wrapper.find("#submit-button").trigger("click");

    await flushPromises();
    expect(api.signup).toHaveBeenCalledTimes(0);
  });

  it("does not calls signup api call when password is empty", async () => {
    userData.user.password = "";
    wrapper = mount(Signup, { localVue, mocks, stubs, store });
    await wrapper.find("#submit-button").trigger("click");
    await wrapper.find("#submit-button").trigger("click");

    await flushPromises();
    expect(api.signup).toHaveBeenCalledTimes(0);
  });
});
