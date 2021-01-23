/* eslint-env jest */ // tell ESLint to check this file with jest syntax
import { mount, createLocalVue } from "@vue/test-utils";
import Vuelidate from "vuelidate";
import Buefy from "buefy";
import Vuex from "vuex";
import Login from "../Login";
import api from "@/Api";
import flushPromises from "@/utils/flush-promises/index";

const localVue = createLocalVue();
localVue.use(Vuelidate);
localVue.use(Vuex);
localVue.use(Buefy);

const email = "curly@braces.com";
const password = "codescoping";

jest.mock("@/Api");

const mocks = {
  $router: {
    push: jest.fn(),
  },
};

const stubs = ["router-link"];

describe("Page Display", () => {
  const wrapper = mount(Login, {
    localVue,
    mocks,
    stubs,
  });

  // Checks if the element exists (by their id)
  it("renders correct form fields", () => {
    expect(wrapper.find("#email").isVisible()).toBe(true);
    expect(wrapper.find("#password").isVisible()).toBe(true);
  });

  it("renders login button", () => {
    expect(wrapper.find("#submit").is("button")).toBe(true);
  });
});

describe("API calls", () => {
  let wrapper;
  let store;

  beforeEach(() => {
    store = new Vuex.Store({
      actions: {
        setLoggedIn: jest.fn(),
        setUserId: jest.fn(),
        setIsAdmin: jest.fn(),
        setIsGlobalAdmin: jest.fn(),
      },
    });
  });

  // Checks if the element exists (by their id)
  it("sends login api call when email and password are filled", async () => {
    wrapper = mount(Login, { localVue, mocks, stubs, store });
    api.login.mockResolvedValue({
      data: { status: "SUCCESS", urlRedirect: "profile", userId: 1 },
    });
    wrapper.setData({ email: "test@test.com", password: "password123" });
    await wrapper.find("#submit").trigger("click");
    await flushPromises();
    expect(api.login).toHaveBeenCalledTimes(1);
    expect(mocks.$router.push).toHaveBeenCalledWith({
      name: "profile",
      params: { userId: 1 },
    });
  });

  it("does not call login api when email is empty", async () => {
    wrapper = mount(Login, { localVue, mocks, stubs, store });
    api.login = jest.fn();
    wrapper.setData({ email: "", password: "password123" });
    await wrapper.find("#submit").trigger("click");
    await flushPromises();
    expect(api.login).toHaveBeenCalledTimes(0);
  });

  it("does not call login api when password is empty", async () => {
    wrapper = mount(Login, { localVue, mocks, stubs, store });
    api.login = jest.fn();
    wrapper.setData({ email: "test@test.com", password: "" });
    await wrapper.find("#submit").trigger("click");
    await flushPromises();
    expect(api.login).toHaveBeenCalledTimes(0);
  });
});
