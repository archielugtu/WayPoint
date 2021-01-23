/* eslint-env jest */ // tell ESLint to check this file with jest syntax
import { shallowMount, createLocalVue } from "@vue/test-utils";

import Vuex from "vuex";

import Password from "../Password";
import Buefy from "buefy";
import Vuelidate from "vuelidate";
import api from "@/Api";

const localVue = createLocalVue();
localVue.use(Buefy);
localVue.use(Vuelidate);
localVue.use(Vuex);

jest.mock("@/Api");

const userId = 1;
const mocks = {
  $route: {
    params: {
      userId,
    },
  },
};

describe("Page Display", () => {
  let wrapper;
  let getters;

  beforeEach(() => {
    getters = {
      hasAdminPrivileges: () => true,
    };

    const store = new Vuex.Store({
      getters,
    });
    wrapper = shallowMount(Password, {
      localVue,
      store,
    });
  });

  it("Shows current password input box", () => {
    expect(wrapper.find("#old-password").exists()).toBe(true);
    expect(wrapper.find("#new-password-confirm").isVisible()).toBe(true);
  });

  it("Shows new password input box", () => {
    expect(wrapper.find("#new-password").exists()).toBe(true);
    expect(wrapper.find("#new-password-confirm").isVisible()).toBe(true);
  });

  it("Shows confirm new password input box", () => {
    expect(wrapper.find("#new-password-confirm").exists()).toBe(true);
    expect(wrapper.find("#new-password-confirm").isVisible()).toBe(true);
  });

  it("Shows reset password btn", () => {
    expect(wrapper.find("#reset-btn").exists()).toBe(true);
  });
});

describe("API calls", () => {
  let wrapper;
  let getters;

  beforeEach(() => {
    api.sendChangePassword = jest.fn();
    api.sendChangePassword.mockResolvedValue({ status: 200 });

    getters = {
      hasAdminPrivileges: () => true,
    };

    const store = new Vuex.Store({
      getters,
    });
    wrapper = shallowMount(Password, {
      localVue,
      store,
      mocks,
    });
  });

  it("does not send API call when new password is not specified", async () => {
    wrapper.setData({
      userData: {
        password: "OldPass234", // old password
        newPassword: "",
        confirmNewPassword: "NewPass123",
      },
    });

    await wrapper.find("#change-pw-btn").trigger("click");
    expect(api.sendChangePassword).toHaveBeenCalledTimes(0);
  });

  it("does not send API call when new password is not specified", async () => {
    wrapper.setData({
      userData: {
        password: "OldPass234", // old password
        newPassword: "NewPass123",
        confirmNewPassword: "NewDiffPass345",
      },
    });

    await wrapper.find("#change-pw-btn").trigger("click");
    expect(api.sendChangePassword).toHaveBeenCalledTimes(0);
  });

  it("sends API call when old password exists, also new and repeat password matches", async () => {
    wrapper.setData({
      userData: {
        password: "OldPass123", // old password
        newPassword: "NewPass123",
        confirmNewPassword: "NewPass123",
      },
    });

    await wrapper.find("#change-pw-btn").trigger("click");
    expect(api.sendChangePassword).toHaveBeenCalledWith(userId, {
      new_password: "72C8F5A59FD10E92",
      old_password: "9C4B04549BFBFF3E",
      repeat_password: "72C8F5A59FD10E92",
    });
  });
});
