/* eslint-env jest */ // tell ESLint to check this file with jest syntax
import {createLocalVue, mount} from "@vue/test-utils";
import Buefy from "buefy";
import Vuelidate from "vuelidate";
import DynamicInput from "../DynamicInput.vue";

const localVue = createLocalVue();
localVue.use(Buefy);
localVue.use(Vuelidate);

describe("Behavior test for things requiring at least 2 answers", () => {

  let wrapper;

  beforeEach(() => {});

  it("should not add new answers unless the last one has been filled", async () => {
    wrapper = mount(DynamicInput, {localVue, propsData: {type: "multichoice_combination", viewType:'edit'}});

    wrapper.find("#add-answer-btn").trigger("click");
    await wrapper.vm.$nextTick();

    expect(wrapper.vm.answers.length).toBe(2);
  });

  it("should not remove answers when there's only two of them", async () => {
    wrapper = mount(DynamicInput, {localVue, propsData: {type: "multichoice_combination", viewType:'edit'}});

    wrapper.find("#remove-answer-btn").trigger("click");
    await wrapper.vm.$nextTick();

    expect(wrapper.vm.answers.length).toBe(2);
  });

  it("should add new answers when the latest one is filled", async () => {
    wrapper = mount(DynamicInput, {localVue, propsData: {type: "multichoice_combination", viewType:'edit'}});

    const answers = [];
    answers.push({answer: "test"});
    answers.push({answer: "test"});

    wrapper.setData({answers});
    wrapper.find("#add-answer-btn").trigger("click");
    await wrapper.vm.$nextTick();

    expect(wrapper.vm.answers.length).toBe(3);
  });

  it("should remove answers when there are more than 2", async () => {
    wrapper = mount(DynamicInput, {localVue, propsData: {type: "multichoice_combination", viewType:'edit'}});

    const answers = [];
    answers.push({answer: "test"});
    answers.push({answer: "test"});
    answers.push({answer: "test"});

    wrapper.setData({answers});
    wrapper.find("#remove-answer-btn").trigger("click");
    await wrapper.vm.$nextTick();

    expect(wrapper.vm.answers.length).toBe(2);
  });

});
