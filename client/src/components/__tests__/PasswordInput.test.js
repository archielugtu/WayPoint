/* eslint-env jest */ // tell ESLint to check this file with jest syntax
import {mount, createLocalVue} from '@vue/test-utils'
import Vuelidate from 'vuelidate'
import VueRouter from 'vue-router'
import PasswordInput from "../PasswordInput";

const localVue = createLocalVue()

localVue.use(VueRouter)
const router = new VueRouter()

localVue.use(Vuelidate)

const password = "password"

describe('Show error when password empty', () => {

  const wrapper = mount(PasswordInput, {
    localVue,
    router,
    propsData: {
      default: password,
      required: true
    }
  })

  const vm = wrapper.vm

  it('Field showing error when empty', async () => {
    expect(wrapper.find('#password-error').exists()).toBe(false)
    expect(wrapper.vm.password).toBe(password)
    vm.password = ""
    vm.$v.$touch()
    await vm.$nextTick()
    expect(wrapper.find('#password-error').isVisible()).toBe(true)
  })
})

describe('Show error when password too short', () => {

  const wrapper = mount(PasswordInput, {
    localVue,
    router,
    propsData: {
      default: password,
      required: true
    }
  })

  const vm = wrapper.vm

  it('Field showing error when empty', async () => {
    expect(wrapper.find('#password-error').exists()).toBe(false)
    expect(wrapper.vm.password).toBe(password)
    vm.password = "test"
    vm.$v.$touch()
    await vm.$nextTick()
    expect(wrapper.find('#password-error').isVisible()).toBe(true)
  })
})
