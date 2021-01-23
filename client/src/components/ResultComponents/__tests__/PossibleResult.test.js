/* eslint-env jest */ // tell ESLint to check this file with jest syntax
import {createLocalVue, mount} from "@vue/test-utils";
import Buefy from "buefy";
import PossibleResult from "../PossibleResult.vue";
import Vuelidate from "vuelidate";
import api from "@/Api";

const localVue = createLocalVue();
localVue.use(Buefy);
localVue.use(Vuelidate);

jest.mock("@/Api");

api.getActivitySpecificationById.mockResolvedValue({
  data: []
});

describe("Page Display", () => {
  let wrapper;

  beforeEach(() => {
    wrapper = mount(PossibleResult, {localVue});
    wrapper.setProps({viewType:'edit'})

  });

  it("has an input box", () => {
    expect(wrapper.find("#input-box").isVisible()).toBe(true);
  });

  it("has an add button", () => {
    expect(wrapper.find("#add-btn").isVisible()).toBe(true);
  });
});

describe("Behavior test", () => {

  let wrapper;

  beforeEach(() => {
    wrapper = mount(PossibleResult, {localVue});
    wrapper.setProps({viewType:'edit'})
  });

  it("Adds a SingleInput component when textbox is added", async () => {
    wrapper.setData({question: "a good question"});

    const addBtn = wrapper.find("#add-btn");
    await addBtn.trigger("click");

    const singleInputComponent = wrapper.find({name: "SingleInput"});
    expect(singleInputComponent.exists()).toBe(true);
  });

  it("Adds a SingleInput component when date input is added", async () => {
    wrapper.setData({question: "a good question", option: "Date"});

    const addBtn = wrapper.find("#add-btn");
    await addBtn.trigger("click");

    const singleInputComponent = wrapper.find({name: "SingleInput"});
    expect(singleInputComponent.exists()).toBe(true);
  });

  it("Adds a SingleInput component when checkbox input is added", async () => {
    wrapper.setData({question: "a good question", option: "Checkbox"});

    const addBtn = wrapper.find("#add-btn");
    await addBtn.trigger("click");
    const singleInputComponent = wrapper.find({name: "SingleInput"});
    expect(singleInputComponent.exists()).toBe(true);
  });

  it("Adds a SingleInput component when time input is added", async () => {
    wrapper.setData({question: "a good question", option: "Time"});

    const addBtn = wrapper.find("#add-btn");
    await addBtn.trigger("click");
    const singleInputComponent = wrapper.find({name: "SingleInput"});
    expect(singleInputComponent.exists()).toBe(true);
  });

  it("Adds a DynamicInput component when Multichoice single is added", async () => {
    wrapper.setData({question: "a good question", option: "Multi-Choice", isMultiChoiceCombo: false});

    const addBtn = wrapper.find("#add-btn");
    await addBtn.trigger("click");

    const singleInputComponent = wrapper.find({name: "DynamicInput"});
    expect(singleInputComponent.exists()).toBe(true);
  });

  it("Adds a DynamicInput component when Multichoice combo input is added", async () => {
    wrapper.setData({question: "a good question", option: "Multi-Choice", isMultiChoiceCombo: true});

    const addBtn = wrapper.find("#add-btn");
    await addBtn.trigger("click");

    const singleInputComponent = wrapper.find({name: "DynamicInput"});
    expect(singleInputComponent.exists()).toBe(true);
  });

});
